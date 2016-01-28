package actors;

import java.util.Arrays;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

import actors.RemoveUserActor.RemoveUserMessage;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.JavaTestKit;

@RunWith(MockitoJUnitRunner.class)
public class RemoveUserActorTest extends JavaTestKit {

	public RemoveUserActorTest() {
		super(system);
	}

	static ActorSystem system;

	@BeforeClass
	public static void setup() {
		system = ActorSystem.create();
	}

	@AfterClass
	public static void teardown() {
		JavaTestKit.shutdownActorSystem(system);
		system = null;
	}

	@Mock
	private OrientGraphFactory graphFactory;
	@Mock
	private OrientGraph graph;
	@Mock
	private OrientVertex userVertex1;
	@Mock
	private OrientVertex userVertex2;
	@Mock
	private OrientVertex userVertex3;

	@Test
	public void testOnReceive() throws Exception {
		// given
		String id = "anId";
		Mockito.when(graphFactory.getTx()).thenReturn(graph);
		Mockito.when(graph.getVerticesOfClass("User")).thenReturn(Arrays.asList(userVertex1, userVertex2, userVertex3));
		Mockito.when(userVertex2.getProperty("uniqueId")).thenReturn(id);

		ActorRef unit = system.actorOf(Props.create(RemoveUserActor.class, graphFactory));

		// when
		unit.tell(new RemoveUserMessage(id), getRef());

		// then
		expectMsgEquals("Ok");

		Mockito.verify(userVertex1).getProperty("uniqueId");
		Mockito.verify(graph).removeVertex(userVertex2);
		Mockito.verify(graph).commit();
		Mockito.verify(graph).shutdown();
		Mockito.verifyZeroInteractions(userVertex3);
	}

	@Test
	public void testOnReceive_whenOrientDbError() throws Exception {
		// given
		String message = "anError";
		Mockito.when(graphFactory.getTx()).thenReturn(graph);
		Mockito.when(graph.getVerticesOfClass(Matchers.anyString())).thenThrow(new RuntimeException(message));

		ActorRef unit = system.actorOf(Props.create(RemoveUserActor.class, graphFactory));

		// when
		unit.tell(new RemoveUserMessage("anId"), getRef());

		// then
		expectMsgEquals(message);

		Mockito.verify(graph).shutdown();
		Mockito.verifyZeroInteractions(userVertex1, userVertex2, userVertex3);
	}

}

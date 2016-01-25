package actors.repository;

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

import actors.repository.RemoveAccountVertexActor.RemoveVertex;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.JavaTestKit;

@RunWith(MockitoJUnitRunner.class)
public class RemoveAccountVertexActorTest extends JavaTestKit {
	private static final String name = "testName";

	public RemoveAccountVertexActorTest() {
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
	private OrientVertex accountVertex1;
	@Mock
	private OrientVertex accountVertex2;
	@Mock
	private OrientVertex accountVertex3;

	@Test
	public void testOnReceive() throws Exception {
		// given
		Mockito.when(graphFactory.getTx()).thenReturn(graph);
		Mockito.when(graph.getVerticesOfClass("JiraAccount")).thenReturn(Arrays.asList(accountVertex1, accountVertex2, accountVertex3));
		Mockito.when(accountVertex2.getProperty("name")).thenReturn(name);

		ActorRef unit = system.actorOf(Props.create(RemoveAccountVertexActor.class, graphFactory));

		// when
		unit.tell(new RemoveVertex(name), getRef());

		// then
		expectMsgEquals("Ok");

		Mockito.verify(accountVertex1).getProperty("name");
		Mockito.verify(accountVertex2).getProperty("name");
		Mockito.verify(graph).removeVertex(accountVertex2);
		Mockito.verify(graph).commit();
		Mockito.verifyZeroInteractions(accountVertex3);
	}

	@Test
	public void testOnReceive_whenOrientDbError() throws Exception {
		// given
		String message = "Orient DB error";
		Mockito.when(graphFactory.getTx()).thenReturn(graph);
		Mockito.when(graph.getVerticesOfClass(Matchers.anyString())).thenThrow(new RuntimeException(message));

		ActorRef unit = system.actorOf(Props.create(RemoveAccountVertexActor.class, graphFactory));

		// when
		unit.tell(new RemoveVertex(name), getRef());

		// then
		expectMsgEquals(message);

		Mockito.verify(graph).rollback();
		Mockito.verify(graph).shutdown();
		Mockito.verifyZeroInteractions(accountVertex1,accountVertex2,accountVertex3);
	}

}

package actors;

import java.util.Arrays;
import java.util.Date;

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

import actors.UpdateUserActor.UpdateUserMessage;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.JavaTestKit;
import models.User;

@RunWith(MockitoJUnitRunner.class)
public class UpdateUserActorTest extends JavaTestKit {

	public UpdateUserActorTest() {
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
		String id = "anOldId";
		User user = new User("aFirstName", "aLastName", "anId", "anEmail");
		Mockito.when(graphFactory.getTx()).thenReturn(graph);
		Mockito.when(graph.getVertices("User.uniqueId", id)).thenReturn(Arrays.asList(userVertex1, userVertex2, userVertex3));

		ActorRef unit = system.actorOf(Props.create(UpdateUserActor.class, graphFactory));

		// when
		unit.tell(new UpdateUserMessage(id, user), getRef());

		// then
		expectMsgEquals("Ok");

		Mockito.verify(userVertex2).setProperty("firstName", user.firstName);
		Mockito.verify(userVertex2).setProperty("lastName", user.lastName);
		Mockito.verify(userVertex2).setProperty("uniqueId", user.id);
		Mockito.verify(userVertex2).setProperty("email", user.email);
		Mockito.verify(userVertex2).setProperty(Matchers.eq("updated"), Matchers.any(Date.class));
		Mockito.verify(graph).commit();
		Mockito.verify(graph).shutdown();
	}

	@Test
	public void testOnReceive_whenOrientDbError() throws Exception {
		// given
		String message = "anError";
		Mockito.when(graphFactory.getTx()).thenReturn(graph);
		Mockito.when(graph.getVertices(Matchers.anyString(),Matchers.anyString())).thenThrow(new RuntimeException(message));

		ActorRef unit = system.actorOf(Props.create(UpdateUserActor.class, graphFactory));

		// when
		unit.tell(new UpdateUserMessage(null, null), getRef());

		// then
		expectMsgEquals(message);

		Mockito.verify(graph).shutdown();
		Mockito.verifyZeroInteractions(userVertex1, userVertex2, userVertex3);
	}

}

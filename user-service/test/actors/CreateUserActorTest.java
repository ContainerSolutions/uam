package actors;

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

import actors.CreateUserActor.CreateUserMessage;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.JavaTestKit;
import models.User;

@RunWith(MockitoJUnitRunner.class)
public class CreateUserActorTest extends JavaTestKit {

	public CreateUserActorTest() {
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
	private OrientVertex userVertex;

	@Test
	public void testOnReceive() throws Exception {
		// given
		User user = new User("aFirstName", "aLastName", "anId", "anEmail"); 
		
		Mockito.when(graphFactory.getTx()).thenReturn(graph);
		Mockito.when(graph.addVertex("User", "user")).thenReturn(userVertex);

		ActorRef unit = system.actorOf(Props.create(CreateUserActor.class, graphFactory));

		// when
		unit.tell(new CreateUserMessage(user), getRef());

		// then
		expectMsgEquals("Ok");

		Mockito.verify(userVertex).setProperty("firstName", user.firstName);
		Mockito.verify(userVertex).setProperty("lastName", user.lastName);
		Mockito.verify(userVertex).setProperty("uniqueId", user.id);
		Mockito.verify(userVertex).setProperty("email", user.email);
		Mockito.verify(userVertex).setProperty(Matchers.eq("created"), Matchers.any(Date.class));
		Mockito.verify(userVertex).setProperty(Matchers.eq("created"), Matchers.any(Date.class));
		Mockito.verify(userVertex).setProperty("active", true);
		Mockito.verify(graph).commit();
		Mockito.verify(graph).shutdown();
	}

	@Test
	public void testOnReceive_whenOrientDbError() throws Exception {
		// given
		String message = "anError";
		Mockito.when(graphFactory.getTx()).thenReturn(graph);
		Mockito.when(graph.addVertex(Matchers.anyString(), Matchers.anyString())).thenThrow(new RuntimeException(message));

		ActorRef unit = system.actorOf(Props.create(CreateUserActor.class, graphFactory));

		// when
		unit.tell(new CreateUserMessage(null), getRef());

		// then
		expectMsgEquals(message);

		Mockito.verify(graph).shutdown();
		Mockito.verifyZeroInteractions(userVertex);
	}

}

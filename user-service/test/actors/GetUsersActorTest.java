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

import com.fasterxml.jackson.databind.JsonNode;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

import actors.GetUsersActor.GetUsersMessage;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.JavaTestKit;
import models.User;
import play.libs.Json;

@RunWith(MockitoJUnitRunner.class)
public class GetUsersActorTest extends JavaTestKit {

	public GetUsersActorTest() {
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
		JsonNode expected = Json.toJson(Arrays.asList(user));
		
		Mockito.when(graphFactory.getTx()).thenReturn(graph);
		Mockito.when(graph.getVerticesOfClass("User")).thenReturn(Arrays.asList(userVertex));
		Mockito.when(userVertex.getProperty("uniqueId")).thenReturn(user.id);
		Mockito.when(userVertex.getProperty("email")).thenReturn(user.email);
		Mockito.when(userVertex.getProperty("firstName")).thenReturn(user.firstName);
		Mockito.when(userVertex.getProperty("lastName")).thenReturn(user.lastName);

		ActorRef unit = system.actorOf(Props.create(GetUsersActor.class, graphFactory));

		// when
		unit.tell(new GetUsersMessage(), getRef());

		// then
		expectMsgEquals(expected);

		Mockito.verify(graph).shutdown();
	}

	@Test
	public void testOnReceive_whenOrientDbError() throws Exception {
		// given
		String message = "anError";
		Mockito.when(graphFactory.getTx()).thenReturn(graph);
		Mockito.when(graph.getVerticesOfClass(Matchers.anyString())).thenThrow(new RuntimeException(message));

		ActorRef unit = system.actorOf(Props.create(GetUsersActor.class, graphFactory));

		// when
		unit.tell(new GetUsersMessage(), getRef());

		// then
		expectMsgEquals(message);

		Mockito.verify(graph).shutdown();
		Mockito.verifyZeroInteractions(userVertex);
	}

}

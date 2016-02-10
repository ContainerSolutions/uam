package actors;

import actors.UserEventsActor.GetUserEventsMessage;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.JavaTestKit;
import com.fasterxml.jackson.databind.JsonNode;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import models.UserEvent;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import play.libs.Json;

import java.util.Arrays;
import java.util.Date;

@RunWith(MockitoJUnitRunner.class)
public class UsersEventsActorTest extends JavaTestKit {

	public UsersEventsActorTest() {
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
		String userId = "test";
		UserEvent userEvent = new UserEvent(new Date(), "Jira", "create", "test user", 123L);
		JsonNode expected = Json.toJson(Arrays.asList(userEvent));

		Mockito.when(graphFactory.getTx()).thenReturn(graph);
		Mockito.when(graph.getVertices("AuditLog.user_id", userId)).thenReturn(Arrays.asList(userVertex));
		Mockito.when(userVertex.getProperty("datetime")).thenReturn(userEvent.datetime);
		Mockito.when(userVertex.getProperty("application")).thenReturn(userEvent.application);
		Mockito.when(userVertex.getProperty("action")).thenReturn(userEvent.action);
		Mockito.when(userVertex.getProperty("executor")).thenReturn(userEvent.executor);
		Mockito.when(userVertex.getProperty("request_number")).thenReturn(userEvent.requestNumber);

		ActorRef unit = system.actorOf(Props.create(UserEventsActor.class, graphFactory));

		// when
		unit.tell(new GetUserEventsMessage(userId), getRef());

		// then
		expectMsgEquals(expected);

		Mockito.verify(graph).shutdown();
	}

	@Test
	public void testOnReceive_whenOrientDbError() throws Exception {
		// given
		RuntimeException exception = new RuntimeException("anError");
		Mockito.when(graphFactory.getTx()).thenReturn(graph);
		Mockito.when(graph.getVertices(Matchers.anyString(), Matchers.anyString())).thenThrow(exception);

		ActorRef unit = system.actorOf(Props.create(UserEventsActor.class, graphFactory));

		// when
		unit.tell(new GetUserEventsMessage("test"), getRef());

		// then
		expectMsgEquals(exception);

		Mockito.verify(graph).shutdown();
		Mockito.verifyZeroInteractions(userVertex);
	}

}

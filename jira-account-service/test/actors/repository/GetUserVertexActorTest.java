package actors.repository;

import java.util.Arrays;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

import actors.jira.CreateAccountActor.CreateJiraAccountMessage;
import actors.repository.GetUserVertexActor.GetUserData;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.JavaTestKit;

@RunWith(MockitoJUnitRunner.class)
public class GetUserVertexActorTest extends JavaTestKit {
	private static final String userId = "testUserId";
	private static final String name = "testName";
	private static final String email = "testMail";
	private static final String firstName = "testFirst";
	private static final String lastName = "testLast";
	private static final String displayName = "testFirst testLast";
	private static final List<String> applicationKeys = Arrays.asList("jira-core");

	public GetUserVertexActorTest() {
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
		CreateJiraAccountMessage createMessage = new CreateJiraAccountMessage(name, email, displayName, applicationKeys);

		Mockito.when(graphFactory.getTx()).thenReturn(graph);
		Mockito.when(graph.getVertex(userId)).thenReturn(userVertex);
		Mockito.when(userVertex.getProperty("uniqueId")).thenReturn(name);
		Mockito.when(userVertex.getProperty("email")).thenReturn(email);
		Mockito.when(userVertex.getProperty("firstName")).thenReturn(firstName);
		Mockito.when(userVertex.getProperty("lastName")).thenReturn(lastName);
		Mockito.when(userVertex.getProperty("lastName")).thenReturn(lastName);

		ActorRef unit = system.actorOf(Props.create(GetUserVertexActor.class, graphFactory));

		// when
		unit.tell(new GetUserData(userId), getRef());

		// then
		expectMsgEquals(createMessage);

		Mockito.verify(graph).shutdown();
	}

	@Test
	public void testOnReceive_whenOrientDbError() throws Exception {
		// given
		String message = "Orient DB error";
		Mockito.when(graphFactory.getTx()).thenReturn(graph);
		Mockito.when(graph.getVertex(userId)).thenThrow(new RuntimeException(message));

		ActorRef unit = system.actorOf(Props.create(GetUserVertexActor.class, graphFactory));

		// when
		unit.tell(new GetUserData(userId), getRef());

		// then
		expectMsgEquals(message);

		Mockito.verify(graph).shutdown();
		Mockito.verifyZeroInteractions(userVertex);
	}

}

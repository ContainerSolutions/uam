package actors.repository;

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
import actors.repository.CreateAccountVertexActor.CreateVertex;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.Status.Failure;
import akka.testkit.JavaTestKit;

@RunWith(MockitoJUnitRunner.class)
public class GetUserVertexActorTest extends JavaTestKit {
	private static final String userId = "testUserId";
	private static final String flowId = "testFlowId";
	private static final String appId = "testAppId";

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
		Mockito.when(graphFactory.getTx()).thenReturn(graph);
		Mockito.when(graph.getVertex(userId)).thenReturn(userVertex);

		ActorRef unit = system.actorOf(Props.create(GetUserVertexActor.class, getRef(), graphFactory));

		// when
		unit.tell(new CreateVertex(userId, flowId, appId), getRef());

		// then
		expectMsgClass(CreateJiraAccountMessage.class);

		Mockito.verify(userVertex).getProperty("uniqueId");
		Mockito.verify(userVertex).getProperty("email");
		Mockito.verify(userVertex).getProperty("firstName");
		Mockito.verify(userVertex).getProperty("lastName");
		Mockito.verify(userVertex).getProperty("lastName");
		Mockito.verify(graph).shutdown();
	}

	@Test
	public void testOnReceive_whenOrientDbError() throws Exception {
		// given
		Mockito.when(graphFactory.getTx()).thenReturn(graph);
		Mockito.when(graph.getVertex(userId)).thenThrow(new RuntimeException());

		ActorRef unit = system.actorOf(Props.create(GetUserVertexActor.class, getRef(), graphFactory));

		// when
		unit.tell(new CreateVertex(userId, flowId, appId), getRef());

		// then
		expectMsgClass(Failure.class);

		Mockito.verify(graph).shutdown();
		Mockito.verifyZeroInteractions(userVertex);
	}

}

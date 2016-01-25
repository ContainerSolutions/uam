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

import actors.repository.CreateAccountVertexActor.CreateVertex;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.JavaTestKit;

@RunWith(MockitoJUnitRunner.class)
public class CreateAccountVertexActorTest extends JavaTestKit {
	private static final String userId = "testUserId";
	private static final String flowId = "testFlowId";
	private static final String appId = "testAppId";
	private static final String name = "testName";

	public CreateAccountVertexActorTest() {
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
	@Mock
	private OrientVertex flowVertex;
	@Mock
	private OrientVertex appVertex;
	@Mock
	private OrientVertex accountVertex;

	@Test
	public void testOnReceive() throws Exception {
		// given
		Mockito.when(graphFactory.getTx()).thenReturn(graph);
		Mockito.when(graph.getVertex(userId)).thenReturn(userVertex);
		Mockito.when(graph.getVertex(flowId)).thenReturn(flowVertex);
		Mockito.when(graph.getVertex(appId)).thenReturn(appVertex);
		Mockito.when(graph.addVertex("JiraAccount", "jiraaccount")).thenReturn(accountVertex);
		Mockito.when(userVertex.getProperty("uniqueId")).thenReturn(name);

		ActorRef unit = system.actorOf(Props.create(CreateAccountVertexActor.class, graphFactory));

		// when
		unit.tell(new CreateVertex(userId, flowId, appId), getRef());

		// then
		expectMsgEquals("Ok");

		Mockito.verify(accountVertex).addEdge("createdIn", flowVertex);
		Mockito.verify(userVertex).addEdge("hasAccount", accountVertex);
		Mockito.verify(appVertex).addEdge("forApplication", accountVertex);
		Mockito.verify(graph).commit();
		Mockito.verify(graph).shutdown();
	}

	@Test
	public void testOnReceive_whenOrientDbError() throws Exception {
		// given
		String message = "Orient DB error";
		Mockito.when(graphFactory.getTx()).thenReturn(graph);
		Mockito.when(graph.getVertex(userId)).thenThrow(new RuntimeException(message));

		ActorRef unit = system.actorOf(Props.create(CreateAccountVertexActor.class, graphFactory));

		// when
		unit.tell(new CreateVertex(userId, flowId, appId), getRef());

		// then
		expectMsgEquals(message);

		Mockito.verify(graph).rollback();
		Mockito.verify(graph).shutdown();
		Mockito.verifyZeroInteractions(userVertex, flowVertex, appVertex, accountVertex);
	}
}

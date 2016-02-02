package actors.repository;

import java.util.Arrays;
import java.util.Collections;

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
		Mockito.when(graph.getVerticesOfClass("User")).thenReturn(Arrays.asList(userVertex));
		Mockito.when(graph.addVertex("JiraAccount", "jiraaccount")).thenReturn(accountVertex);
		Mockito.when(userVertex.getProperty("uniqueId")).thenReturn(userId);

		ActorRef unit = system.actorOf(Props.create(CreateAccountVertexActor.class, graphFactory));

		// when
		unit.tell(new CreateVertex(userId), getRef());

		// then
		expectMsgEquals("Ok");

		Mockito.verify(userVertex).addEdge("hasAccount", accountVertex);
		Mockito.verify(graph).commit();
		Mockito.verify(graph).shutdown();
	}

	@Test
	public void testOnReceive_whenUserNotFound() throws Exception {
		// given
		String message = "User vertex not found: " + userId;
		Mockito.when(graphFactory.getTx()).thenReturn(graph);
		Mockito.when(graph.getVerticesOfClass("User")).thenReturn(Collections.emptyList());

		ActorRef unit = system.actorOf(Props.create(CreateAccountVertexActor.class, graphFactory));

		// when
		unit.tell(new CreateVertex(userId), getRef());

		// then
		expectMsgEquals(message);

		Mockito.verify(graph).rollback();
		Mockito.verify(graph).shutdown();
		Mockito.verifyZeroInteractions(userVertex, flowVertex, appVertex, accountVertex);
	}
}

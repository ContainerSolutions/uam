package actors.repository;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.JavaTestKit;

public class AuditLogsActorTest {

	private static ActorSystem system;

	@BeforeClass
	public static void setUp() throws Exception {
		system = ActorSystem.create();
	}

	@AfterClass
	public static void tewarDown() throws Exception {
		JavaTestKit.shutdownActorSystem(system);
		system = null;
	}

	@Test
	public void testActor() throws Exception {
		new JavaTestKit(system) {
			{
				AuditLogsActor.SaveAuditLog message = new AuditLogsActor.SaveAuditLog(1L, "userId", "application", "executor", "action");

				OrientGraph graph = Mockito.mock(OrientGraph.class);
				OrientVertex vertex = Mockito.mock(OrientVertex.class);
				OrientGraphFactory graphFactory = Mockito.mock(OrientGraphFactory.class);
				Mockito.when(graphFactory.getTx()).thenReturn(graph);
				Mockito.when(graph.addVertex("AuditLog", "auditlog")).thenReturn(vertex);

				ActorRef subject = system.actorOf(Props.create(AuditLogsActor.class, graphFactory));

				subject.tell(message, getRef());

				expectMsgEquals(duration("1 second"), "done");
				Mockito.verify(graph).commit();
				Mockito.verify(vertex).setProperty("request_number", 1L);
				Mockito.verify(vertex).setProperty("user_id", "userId");
				Mockito.verify(vertex).setProperty("application", "application");
				Mockito.verify(vertex).setProperty("executor", "executor");
				Mockito.verify(vertex).setProperty("action", "action");
			}
		};
	}
}

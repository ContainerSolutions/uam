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
import akka.testkit.JavaTestKit;
import scala.concurrent.duration.Duration;
import java.util.Arrays;
import java.util.Date;
import java.time.LocalDateTime;

public class AuditLogsActorTest
{

	private static ActorSystem system;

	@BeforeClass
	public static void setUp() throws Exception
	{
		system = ActorSystem.create();
	}

	@AfterClass
	public static void tewarDown() throws Exception
	{
		JavaTestKit.shutdownActorSystem(system);
		system = null;
	}
	@Test
	public void testActor() throws Exception
	{
		new JavaTestKit(system)
		{
			{
				AuditLogsActor.SaveAuditLog message = new AuditLogsActor.SaveAuditLog(
				    1L,
				    "userId",
				    "application",
				    "executor",
				    "action"
				);

				OrientGraph graph = Mockito.mock(OrientGraph.class);
				OrientVertex vertex = Mockito.mock(OrientVertex.class);
				OrientGraphFactory graphFactory = Mockito.mock(OrientGraphFactory.class);
				Mockito.when(graphFactory.getTx()).thenReturn(graph);
				Mockito.when(graph.addVertex("AuditLog", "auditlog")).thenReturn(vertex);



				ActorRef subject = system.actorOf(Props.create(AuditLogsActor.class, graphFactory));



				JavaTestKit probe =  new JavaTestKit(system);
				subject.tell(message, getRef());

				expectMsgEquals(duration("1 second"), "done");
				Mockito.verify(graph).commit();
				Mockito.verify(vertex).setProperty("user_id", "userId");
				Mockito.verify(vertex).setProperty("application", "application");
				Mockito.verify(vertex).setProperty("executor", "executor");
				Mockito.verify(vertex).setProperty("action", "action");
			}
		};
	}
}

package actors;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.testkit.JavaTestKit;
import scala.concurrent.duration.Duration;

public class GetAllUsersActorTest
{

	private static ActorSystem system;

	@BeforeClass
	public static void setUpi() throws Exception
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
				final Props props = Props.create(GetAllUsersActor.class);
				final ActorRef subject = system.actorOf(props);

				final JavaTestKit probe = new JavaTestKit(system);

				subject.tell(probe.getRef(), getRef());
				expectMsgEquals(duration("1 second"), "done");


			}
		};

	}
}

package actors;

import scala.concurrent.duration.Duration;
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
import commons.GoogleServiceFactory;
import com.google.api.services.admin.directory.Directory;
import org.mockito.Mockito;
import org.mockito.Matchers;
import java.util.Arrays;

import commons.DirectoryHelper;
import commons.GoogleServiceFactory;


public class UsersActorTest
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
				GoogleServiceFactory gFactory = Mockito.mock(GoogleServiceFactory.class);
				Directory directory =  Mockito.mock(Directory.class);
				Mockito.when(gFactory.creatDirectoryService()).thenReturn(directory);
				DirectoryHelper helper = Mockito.mock(DirectoryHelper.class);
				Mockito.when(helper.executeInsertUser(
				                 directory,
				                 "dio-soft.com",
				                 "vtegza@dio-soft.com",
				                 "testFirstName",
				                 "testLastName",
				                 "testPassword")
				            ).thenReturn(200);

				ActorRef subject = system.actorOf(UsersActor.props(gFactory, helper));

				JavaTestKit probe =  new JavaTestKit(system);
				UsersActor.InsertUser msg = new UsersActor.InsertUser(
				    "dio-soft.com",
				    "testId",
				    "vtegza@dio-soft.com",
				    "testFirstName",
				    "testLastName",
				    "testPassword"
				);


				subject.tell(new UsersActor.InitializeMe(), getRef());
				subject.tell(msg, getRef());
				Mockito.verify(helper).executeInsertUser(
				    Mockito.eq(directory),
				    Mockito.eq("dio-soft.com"),
				    Mockito.eq("vtegza@dio-soft.com"),
				    Mockito.eq("testFirstName"),
				    Mockito.eq("testLastName"),
				    Mockito.eq("testPassword")
				);

				expectMsgEquals(duration("1 second"), "done");


			}
		};

	}
}

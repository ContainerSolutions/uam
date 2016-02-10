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
import commons.GoogleServiceFactory;
import com.google.api.services.admin.directory.Directory;
import org.mockito.Mockito;
import org.mockito.Matchers;
import java.util.Arrays;

import commons.DirectoryHelper;
import commons.GoogleServiceFactory;

public class GroupsActorTest
{

	private static ActorSystem system;

	@BeforeClass
	public static void setUp()
	{

		system = ActorSystem.create();
	}

	@AfterClass
	public static void tearDown()
	{
		JavaTestKit.shutdownActorSystem(system);
		system = null;
	}

	@Test
	public void testGetUserGroupsMessage() throws Exception
	{

		new JavaTestKit(system)
		{
			{
				GoogleServiceFactory gFactory = Mockito.mock(GoogleServiceFactory.class);
				Directory directory =  Mockito.mock(Directory.class);
				Mockito.when(gFactory.createDirectoryService()).thenReturn(directory);
				DirectoryHelper helper = Mockito.mock(DirectoryHelper.class);
				Mockito.when(helper.executeGetUserGroups(
				                 directory,
				                 "dio-soft.com",
				                 "vtegza"
				             )
				            ).thenReturn(
				                Arrays.asList("myGroup@domain.com")
				            );
				ActorRef subject = system.actorOf(GroupsActor.props(gFactory, helper));

				JavaTestKit probe =  new JavaTestKit(system);
				GroupsActor.GetUserGroups msg = new GroupsActor.GetUserGroups("dio-soft.com", "vtegza");
				subject.tell(msg, getRef());
				expectMsgEquals(Arrays.asList("myGroup@domain.com"));

			};
		};

	}

}

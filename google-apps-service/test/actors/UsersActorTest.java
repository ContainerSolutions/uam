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
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;

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
	public void testInsertActor() throws Exception
	{
		new JavaTestKit(system)
		{
			{
				GoogleServiceFactory gFactory = Mockito.mock(GoogleServiceFactory.class);
				Directory directory =  Mockito.mock(Directory.class);
				Mockito.when(gFactory.createDirectoryService()).thenReturn(directory);
				DirectoryHelper helper = Mockito.mock(DirectoryHelper.class);
				Mockito.when(helper.executeInsertUser(
				                 directory,
				                 "dio-soft.com",
				                 "test@dio-soft.com",
				                 "testFirstName",
				                 "testLastName",
				                 "testPassword")
				            ).thenReturn(200);

				ActorRef subject = system.actorOf(UsersActor.props(null, gFactory, helper));

				JavaTestKit probe =  new JavaTestKit(system);
				UsersActor.InsertUser msg = new UsersActor.InsertUser(
				    "dio-soft.com",
				    "testId",
				    "test@dio-soft.com",
				    "testFirstName",
				    "testLastName",
				    "testPassword"
				);


				subject.tell(msg, getRef());

				expectMsgEquals(duration("1 second"), "done");


			}
		};

	}
	@Test
	public void testActorDelete() throws Exception
	{
		new JavaTestKit(system)
		{
			{
				GoogleServiceFactory gFactory = Mockito.mock(GoogleServiceFactory.class);
				Directory directory =  Mockito.mock(Directory.class);
				Mockito.when(gFactory.createDirectoryService()).thenReturn(directory);
				DirectoryHelper helper = Mockito.mock(DirectoryHelper.class);
				Mockito.when(helper.executeDeleteUser(
				                 directory,
				                 "dio-soft.com",
				                 "test@dio-soft.com"
				             )
				            ).thenReturn(200);

				ActorRef subject = system.actorOf(UsersActor.props(null, gFactory, helper));

				JavaTestKit probe =  new JavaTestKit(system);
				UsersActor.DeleteUser msg = new UsersActor.DeleteUser(
				    "dio-soft.com",
				    "test@dio-soft.com"
				);


				subject.tell(msg, getRef());

				expectMsgEquals(duration("1 second"), "done");


			}
		};

	}

	@Test
	public void testActorGet() throws Exception
	{
		new JavaTestKit(system)
		{
			{
				String lastName = "LastName";
				String firstName = "firstName";
				String password = "pw";
				String email = "primaryEmail";

				ObjectNode expectedBody = Json.newObject();

				expectedBody.put("lastName", lastName);
				expectedBody.put("firstName", firstName);
				expectedBody.put("primaryEmail", email);



				GoogleServiceFactory gFactory = Mockito.mock(GoogleServiceFactory.class);
				Directory directory =  Mockito.mock(Directory.class);
				Mockito.when(gFactory.createDirectoryService()).thenReturn(directory);
				DirectoryHelper helper = Mockito.mock(DirectoryHelper.class);
				Mockito.when(helper.executeGetUser(
				                 directory,
				                 "dio-soft.com",
				                 email
				             )
				            ).thenReturn(expectedBody);

				ActorRef subject = system.actorOf(UsersActor.props(null, gFactory, helper));

				JavaTestKit probe =  new JavaTestKit(system);
				UsersActor.GetUser msg = new UsersActor.GetUser(
				    "dio-soft.com",
				    email
				);


				subject.tell(msg, getRef());


				expectMsgEquals(duration("1 second"), expectedBody );


			}
		};

	}

	@Test
	public void testActorGetFail() throws Exception
	{
		new JavaTestKit(system)
		{
			{
				String lastName = "LastName";
				String firstName = "firstName";
				String password = "pw";
				String email = "primaryEmail";

				ObjectNode expectedBody = null;


				GoogleServiceFactory gFactory = Mockito.mock(GoogleServiceFactory.class);
				Directory directory =  Mockito.mock(Directory.class);
				Mockito.when(gFactory.createDirectoryService()).thenReturn(directory);
				DirectoryHelper helper = Mockito.mock(DirectoryHelper.class);
				Mockito.when(helper.executeGetUser(
				                 directory,
				                 "dio-soft.com",
				                 email
				             )
				            ).thenReturn(expectedBody);

				ActorRef subject = system.actorOf(UsersActor.props(null, gFactory, helper));

				JavaTestKit probe =  new JavaTestKit(system);
				UsersActor.GetUser msg = new UsersActor.GetUser(
				    "dio-soft.com",
				    email
				);


				subject.tell(msg, getRef());


				expectMsgEquals(duration("1 second"), "404" );
			}
		};

	}


}

package actors.jira;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.fasterxml.jackson.databind.JsonNode;

import actors.jira.CreateAccountActor.CreateJiraAccountMessage;
import actors.repository.CreateAccountVertexActor.CreateVertex;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Status.Failure;
import akka.testkit.JavaTestKit;
import models.JiraUser;
import play.libs.F.Promise;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;
import play.libs.ws.WSResponse;

@RunWith(MockitoJUnitRunner.class)
public class CreateAccountActorTest extends JavaTestKit {

	public CreateAccountActorTest() {
		super(system);
	}

	private static final String url = "testConfigUrl";
	private static final String user = "user";
	private static final String password = "secret";

	private static final String name = "testName";
	private static final String email = "test@email.test";
	private static final String displayName = "testDN";

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
	private WSClient client;

	@Mock
	private WSRequest wsRequest;

	@Mock
	private WSResponse wsResponse;

	@Test
	public void testOnReceive() {
		// given
		ArgumentCaptor<JsonNode> bodyArgument = ArgumentCaptor.forClass(JsonNode.class);
		Mockito.when(client.url(url + "/rest/api/2/user")).thenReturn(wsRequest);
		Mockito.when(wsRequest.setContentType("application/json")).thenReturn(wsRequest);
		Mockito.when(wsRequest.setAuth(user, password)).thenReturn(wsRequest);
		Mockito.when(wsRequest.post(bodyArgument.capture())).thenReturn(Promise.pure(wsResponse));
		Mockito.when(wsResponse.getStatus()).thenReturn(201);

		ActorRef unit = system.actorOf(CreateAccountActor.props(system, getRef(), client, url, user, password));

		// when
		unit.tell(new CreateJiraAccountMessage(new JiraUser(name, email, displayName)), getRef());

		// then
		expectMsgClass(CreateVertex.class);

		Assert.assertEquals(
				"{\"name\":\"testName\",\"emailAddress\":\"test@email.test\",\"displayName\":\"testDN\",\"applicationKeys\":[\"jira-core\"]}",
				bodyArgument.getValue().toString());

	}

	@Test
	public void testOnReceive_whenJiraInvalidResponse() {
		// given
		Mockito.when(client.url(url + "/rest/api/2/user")).thenReturn(wsRequest);
		Mockito.when(wsRequest.setContentType("application/json")).thenReturn(wsRequest);
		Mockito.when(wsRequest.setAuth(user, password)).thenReturn(wsRequest);
		Mockito.when(wsRequest.post(Matchers.any(JsonNode.class))).thenReturn(Promise.pure(wsResponse));
		Mockito.when(wsResponse.getStatus()).thenReturn(500);

		ActorRef unit = system.actorOf(CreateAccountActor.props(system, getRef(), client, url, user, password));

		// when
		unit.tell(new CreateJiraAccountMessage(new JiraUser(name, email, displayName)), getRef());

		// then
		expectMsgClass(Failure.class);
	}
}

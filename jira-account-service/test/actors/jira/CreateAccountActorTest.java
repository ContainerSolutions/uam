package actors.jira;

import java.util.Arrays;
import java.util.List;

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
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.JavaTestKit;
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
	private static final String name = "testName";
	private static final String email = "test@email.test";
	private static final String displayName = "testDN";
	private static final List<String> applicationKeys = Arrays.asList("testKey");

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
		Mockito.when(client.url(url + "/user")).thenReturn(wsRequest);
		Mockito.when(wsRequest.setContentType("application/json")).thenReturn(wsRequest);
		Mockito.when(wsRequest.setAuth(Matchers.anyString(), Matchers.anyString())).thenReturn(wsRequest);
		Mockito.when(wsRequest.post(bodyArgument.capture())).thenReturn(Promise.pure(wsResponse));
		Mockito.when(wsResponse.getStatus()).thenReturn(201);

		ActorRef unit = system.actorOf(CreateAccountActor.props(client, url));

		// when
		unit.tell(new CreateJiraAccountMessage(name, email, displayName, applicationKeys), getRef());

		// then
		expectMsgEquals("Ok");

		Assert.assertEquals(
				"{\"name\":\"testName\",\"emailAddress\":\"test@email.test\",\"displayName\":\"testDN\",\"applicationKeys\":[\"testKey\"]}",
				bodyArgument.getValue().toString());
	}

}

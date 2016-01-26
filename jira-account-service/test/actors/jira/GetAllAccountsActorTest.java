package actors.jira;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import actors.jira.GetAllAccountsActor.GetAllAccounts;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.JavaTestKit;
import play.libs.F.Promise;
import play.libs.Json;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;
import play.libs.ws.WSResponse;

@RunWith(MockitoJUnitRunner.class)
public class GetAllAccountsActorTest extends JavaTestKit {
	public GetAllAccountsActorTest() {
		super(system);
	}

	private static final String url = "testConfigUrl";
	private static final String user = "user";
	private static final String password = "secret";

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
	public void testOnReceive() throws Exception {
		// given
		Mockito.when(client.url(url + "/user/search?username=%25")).thenReturn(wsRequest);
		Mockito.when(wsRequest.setAuth(user, password)).thenReturn(wsRequest);
		Mockito.when(wsRequest.get()).thenReturn(Promise.pure(wsResponse));
		Mockito.when(wsResponse.getStatus()).thenReturn(200);
		Mockito.when(wsResponse.asJson()).thenReturn(Json.parse(
				"[{\"name\":\"testName\",\"emailAddress\":\"test@email.test\",\"displayName\":\"testDN\",\"active\":\"true\"}]"));

		ActorRef unit = system.actorOf(GetAllAccountsActor.props(client, url, user, password));

		// when
		unit.tell(new GetAllAccounts(), getRef());

		// then
		expectMsgEquals("[\"testName\"]");
	}
}

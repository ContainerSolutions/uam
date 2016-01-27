package actors.jira;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import actors.jira.RemoveAccountActor.RemoveAccount;
import actors.repository.RemoveAccountVertexActor.RemoveVertex;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Status.Failure;
import akka.testkit.JavaTestKit;
import play.libs.F.Promise;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;
import play.libs.ws.WSResponse;

@RunWith(MockitoJUnitRunner.class)
public class RemoveAccountActorTest extends JavaTestKit {
	public RemoveAccountActorTest() {
		super(system);
	}

	private static final String url = "testConfigUrl";
	private static final String user = "user";
	private static final String password = "secret";
	private static final String name = "testName";

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
		Mockito.when(client.url(url + "/user?username=" + name)).thenReturn(wsRequest);
		Mockito.when(wsRequest.setAuth(user, password)).thenReturn(wsRequest);
		Mockito.when(wsRequest.delete()).thenReturn(Promise.pure(wsResponse));
		Mockito.when(wsResponse.getStatus()).thenReturn(204);

		ActorRef unit = system.actorOf(RemoveAccountActor.props(getRef(), client, url, user, password));

		// when
		unit.tell(new RemoveAccount(name), getRef());

		// then
		expectMsgClass(RemoveVertex.class);
	}
	
	@Test
	public void testOnReceive_whenInvalidJiraResponse() throws Exception {
		// given
		Mockito.when(client.url(url + "/user?username=" + name)).thenReturn(wsRequest);
		Mockito.when(wsRequest.setAuth(user, password)).thenReturn(wsRequest);
		Mockito.when(wsRequest.delete()).thenReturn(Promise.pure(wsResponse));
		Mockito.when(wsResponse.getStatus()).thenReturn(500);

		ActorRef unit = system.actorOf(RemoveAccountActor.props(getRef(), client, url, user, password));

		// when
		unit.tell(new RemoveAccount(name), getRef());

		// then
		expectMsgClass(Failure.class);
	}
}

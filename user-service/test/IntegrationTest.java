import static play.test.Helpers.running;
import static play.test.Helpers.testServer;

import org.junit.Assert;
import org.junit.Test;

import play.libs.Json;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;

public class IntegrationTest {

	protected static final String firstName = null;
	protected static final String lastName = null;
	protected static final String id = null;
	protected static final String email = null;

	@Test
	public void testGetUsers() {
		running(testServer(3333), new Runnable() {
			public void run() {

				// User does not exist in all Jira accounts
				WSResponse response = WS.url("http://localhost:3333/users").get().get(5000);
				Assert.assertEquals(400, response.getStatus());
			}
		});
	}

	@Test
	public void testPostUser() {
		running(testServer(3333), new Runnable() {
			public void run() {

				WSResponse response = WS.url("http://localhost:3333/users")
						.post(Json.parse("{\"firstName\":\"" + firstName + "\", \"lastName\":\"" + lastName
								+ "\", \"id\":\"" + id + "\", \"email\":\"" + email + "\"}"))
						.get(5000);
				Assert.assertEquals(400, response.getStatus());
			}
		});
	}

	@Test
	public void testDeleteUser() {
		running(testServer(3333), new Runnable() {
			public void run() {

				WSResponse response = WS.url("http://localhost:3333/users/" + id).delete().get(5000);
				Assert.assertEquals(400, response.getStatus());
			}
		});
	}
}

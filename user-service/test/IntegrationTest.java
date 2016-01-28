import static play.test.Helpers.running;
import static play.test.Helpers.testServer;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

import play.libs.Json;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;

public class IntegrationTest {

	protected static final String firstName = RandomStringUtils.randomAlphabetic(10);
	protected static final String lastName = RandomStringUtils.randomAlphabetic(10);
	protected static final String id = RandomStringUtils.randomAlphabetic(6);
	protected static final String email = id + "@dio-soft.com";

	@Test
	public void testIt() {
		running(testServer(3333), new Runnable() {
			public void run() {
				
				// Verify users response have no user with generated id
				WSResponse response = WS.url("http://localhost:3333/users").get().get(5000);
				Assert.assertEquals(200, response.getStatus());
				JsonNode jsonResponse = response.asJson();
				Assert.assertTrue(jsonResponse.isArray());
				Assert.assertFalse(jsonResponse.findValuesAsText("id").contains(id));

				// Create user
				response = WS.url("http://localhost:3333/users")
						.post(Json.parse("{\"firstName\":\"" + firstName + "\", \"lastName\":\"" + lastName
								+ "\", \"id\":\"" + id + "\", \"email\":\"" + email + "\"}"))
						.get(5000);
				Assert.assertEquals(201, response.getStatus());

				// Verify user exists
				response = WS.url("http://localhost:3333/users").get().get(5000);
				Assert.assertEquals(200, response.getStatus());
				jsonResponse = response.asJson();
				Assert.assertTrue(jsonResponse.isArray());
				Assert.assertTrue(jsonResponse.findValuesAsText("id").contains(id));
				
				// Remove user
				response = WS.url("http://localhost:3333/users/" + id).delete().get(5000);
				Assert.assertEquals(204, response.getStatus());
				
				// Verify users response have no user with generated id
				response = WS.url("http://localhost:3333/users").get().get(5000);
				Assert.assertEquals(200, response.getStatus());
				jsonResponse = response.asJson();
				Assert.assertTrue(jsonResponse.isArray());
				Assert.assertFalse(jsonResponse.findValuesAsText("id").contains(id));
			}
		});
	}
}

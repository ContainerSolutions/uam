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

	protected static final int timeout = 50000;
	protected static final String firstName = RandomStringUtils.randomAlphabetic(10);
	protected static final String lastName = RandomStringUtils.randomAlphabetic(10);
	protected static final String id = RandomStringUtils.randomAlphabetic(6);
	protected static final String email = id + "@dio-soft.com";
	
	protected static final String updatedFirstName = RandomStringUtils.randomAlphabetic(10);
	protected static final String updatedLastName = RandomStringUtils.randomAlphabetic(10);
	protected static final String updatedId = RandomStringUtils.randomAlphabetic(6);
	protected static final String updatedEmail = updatedId + "@dio-soft.com";

	@Test
	public void testIt() {
		running(testServer(3333), () -> {
				// Verify users response have no user with generated id
				WSResponse response = WS.url("http://localhost:3333/users").get().get(timeout);
				Assert.assertEquals(200, response.getStatus());
				JsonNode jsonResponse = response.asJson();
				Assert.assertTrue(jsonResponse.isArray());
				Assert.assertFalse(jsonResponse.findValuesAsText("id").contains(id));

				// Create user
				response = WS.url("http://localhost:3333/users")
						.post(Json.parse("{\"firstName\":\"" + firstName + "\", \"lastName\":\"" + lastName
								+ "\", \"id\":\"" + id + "\", \"email\":\"" + email + "\"}"))
						.get(timeout);
				Assert.assertEquals(201, response.getStatus());

				// Verify user exists
				response = WS.url("http://localhost:3333/users").get().get(timeout);
				Assert.assertEquals(200, response.getStatus());
				jsonResponse = response.asJson();
				Assert.assertTrue(jsonResponse.isArray());
				Assert.assertTrue(jsonResponse.findValuesAsText("id").contains(id));
				
				// Update user
				response = WS.url("http://localhost:3333/users/" + id)
						.put(Json.parse("{\"firstName\":\"" + updatedFirstName + "\", \"lastName\":\"" + updatedLastName
								+ "\", \"id\":\"" + updatedId + "\", \"email\":\"" + updatedEmail + "\"}"))
						.get(timeout);
				Assert.assertEquals(200, response.getStatus());

				// Verify updated
				response = WS.url("http://localhost:3333/users").get().get(timeout);
				Assert.assertEquals(200, response.getStatus());
				jsonResponse = response.asJson();
				Assert.assertTrue(jsonResponse.isArray());
				Assert.assertFalse(jsonResponse.findValuesAsText("id").contains(id));
				Assert.assertTrue(jsonResponse.findValuesAsText("id").contains(updatedId));
				
				// Remove user
				response = WS.url("http://localhost:3333/users/" + updatedId).delete().get(timeout);
				Assert.assertEquals(204, response.getStatus());
				
				// Verify users response have no user with generated ids
				response = WS.url("http://localhost:3333/users").get().get(timeout);
				Assert.assertEquals(200, response.getStatus());
				jsonResponse = response.asJson();
				Assert.assertTrue(jsonResponse.isArray());
				Assert.assertFalse(jsonResponse.findValuesAsText("id").contains(id));
				Assert.assertFalse(jsonResponse.findValuesAsText("id").contains(updatedId));

				//Verify events response
				response = WS.url("http://localhost:3333/users/" + id + "/events").get().get(timeout);
				jsonResponse = response.asJson();
				Assert.assertTrue(jsonResponse.isArray());
				Assert.assertEquals(1, jsonResponse.size());
		});
	}
}

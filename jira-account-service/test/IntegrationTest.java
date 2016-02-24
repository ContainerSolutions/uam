import static play.test.Helpers.running;
import static play.test.Helpers.testServer;

import java.util.Date;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

import models.JiraUser;
import play.libs.Json;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;

public class IntegrationTest {
	private static final int timeout = 50000;
	private static final String id = RandomStringUtils.randomAlphabetic(10);
	private static final String firstName = RandomStringUtils.randomAlphabetic(10);
	private static final String lastName = RandomStringUtils.randomAlphabetic(10);
	private static final String displayName = firstName + ' ' + lastName;
	private static final String email = firstName.charAt(0) + lastName + "@dio-soft.com";

	private static String userId = null;

	@BeforeClass
	public static void setUp() {

		OrientGraph graph = new OrientGraphFactory("remote:localhost:2424/UserAccessControl").getTx();
		try {
			OrientVertex user = graph.addVertex("User", "user");
			user.setProperty("uniqueId", id);
			user.setProperty("firstName", firstName);
			user.setProperty("lastName", lastName);
			user.setProperty("email", email);
			user.setProperty("created", new Date());
			user.setProperty("updated", new Date());
			user.setProperty("active", true);

			graph.commit();

			userId = user.getId().toString();
		} catch (Exception e) {
			graph.rollback();
		} finally {
			graph.shutdown();
		}
	}

	@AfterClass
	public static void tearDown() {

		OrientGraph graph = new OrientGraphFactory("remote:localhost:2424/UserAccessControl").getTx();
		try {
			graph.removeVertex(graph.getVertex(userId));

			graph.commit();
		} catch (Exception e) {
			graph.rollback();
		} finally {
			graph.shutdown();
		}
	}

	@Test
	public void testIt() {
		running(testServer(3333), new Runnable() {
			public void run() {
				// User does not exist in all Jira accounts
				WSResponse response = WS.url("http://localhost:3333/jira/accounts").get().get(timeout);
				Assert.assertEquals(200, response.getStatus());
				JsonNode jsonNode = response.asJson();
				Assert.assertTrue(jsonNode.isArray());
				Assert.assertFalse(jsonNode.toString().contains(id));

				// Create account in Jira
				response = WS.url("http://localhost:3333/jira/account").post(Json.toJson(new JiraUser(id, email, displayName))).get(timeout);
				Assert.assertEquals(201, response.getStatus());

				// User exists in all Jira accounts
				response = WS.url("http://localhost:3333/jira/accounts").get().get(timeout);
				Assert.assertEquals(200, response.getStatus());
				jsonNode = response.asJson();
				Assert.assertTrue(jsonNode.isArray());
				Assert.assertTrue(jsonNode.toString().contains(id));

				// Get Jira account info
				response = WS.url("http://localhost:3333/jira/account/" + id).get().get(timeout);
				Assert.assertEquals(200, response.getStatus());
				jsonNode = response.asJson();
				Assert.assertEquals("{\"id\":\"" + id + "\",\"email\":\"" + email + "\",\"displayName\":\"" + displayName + "\"}", jsonNode.toString());

				// Remove Jira account
				response = WS.url("http://localhost:3333/jira/account/" + id).delete().get(timeout);
				Assert.assertEquals(204, response.getStatus());

				// User does not exist
				response = WS.url("http://localhost:3333/jira/account/" + id).get().get(timeout);
				Assert.assertEquals(500, response.getStatus());
				jsonNode = response.asJson();
				Assert.assertEquals("{\"errorMessages\":[\"The user named '" + id + "' does not exist\"],\"errors\":{}}", jsonNode.toString());
			}
		});
	}
}

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

import play.libs.Json;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;

public class IntegrationTest {
	private static final String name = RandomStringUtils.randomAlphabetic(10);
	private static String userId = null;
	private static String flowId = null;
	private static String appId = null;

	@BeforeClass
	public static void setUp() {

		OrientGraph graph = new OrientGraphFactory("remote:192.168.99.100:32782/UserAccessControl").getTx();
		try {
			OrientVertex user = graph.addVertex("User", "user");
			OrientVertex flow = graph.addVertex("Flow", "flow");
			OrientVertex app = graph.addVertex("Application", "application");
			user.setProperty("uniqueId", name);
			user.setProperty("firstName", "aFirstName");
			user.setProperty("lastName", "aLastName");
			user.setProperty("email", "anEmail@fake.fake");
			user.setProperty("created", new Date());
			user.setProperty("updated", new Date());
			user.setProperty("active", true);

			graph.commit();

			userId = user.getId().toString();
			flowId = flow.getId().toString();
			appId = app.getId().toString();
		} catch (Exception e) {
			graph.rollback();
		} finally {
			graph.shutdown();
		}
	}

	@AfterClass
	public static void tearDown() {

		OrientGraph graph = new OrientGraphFactory("remote:192.168.99.100:32782/UserAccessControl").getTx();
		try {
			graph.removeVertex(graph.getVertex(userId));
			graph.removeVertex(graph.getVertex(flowId));
			graph.removeVertex(graph.getVertex(appId));

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

				WSResponse response = WS.url("http://localhost:3333/account/all").get().get(5000);
				Assert.assertEquals(200, response.getStatus());
				JsonNode jsonNode = response.asJson();
				Assert.assertTrue(jsonNode.isArray());
				Assert.assertFalse(jsonNode.toString().contains(name));

				response = WS.url("http://localhost:3333/account")
						.post(Json.parse("{\"userId\":\"" + userId + "\", \"flowId\":\"#17:0\", \"appId\":\"#19:0\"}"))
						.get(5000);
				Assert.assertEquals(200, response.getStatus());
				Assert.assertEquals("Ok", response.getBody());

				response = WS.url("http://localhost:3333/account/all").get().get(5000);
				Assert.assertEquals(200, response.getStatus());
				jsonNode = response.asJson();
				Assert.assertTrue(jsonNode.isArray());
				Assert.assertTrue(jsonNode.toString().contains(name));

				response = WS.url("http://localhost:3333/account/" + name).get().get(5000);
				Assert.assertEquals(200, response.getStatus());
				jsonNode = response.asJson();
				Assert.assertEquals(
						"{\"name\":\"" + name + "\",\"email\":\"anEmail@fake.fake\",\"displayName\":\"aFirstName aLastName\",\"active\":true}",
						jsonNode.toString());

				response = WS.url("http://localhost:3333/account/" + name).delete().get(5000);
				Assert.assertEquals(200, response.getStatus());
				Assert.assertEquals("Ok", response.getBody());

				response = WS.url("http://localhost:3333/account/" + name).get().get(5000);
				Assert.assertEquals(200, response.getStatus());
				jsonNode = response.asJson();
				Assert.assertEquals(
						"{\"errorMessages\":[\"The user named '" + name + "' does not exist\"],\"errors\":{}}",
						jsonNode.toString());
			}
		});
	}
}

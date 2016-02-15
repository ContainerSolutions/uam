import static play.test.Helpers.running;
import static play.test.Helpers.testServer;

import java.util.Date;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Ignore;

import com.fasterxml.jackson.databind.JsonNode;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

import play.libs.Json;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;

@Ignore
public class IntegrationTest
{
	private static final int timeout = 50000;
	private static final String id = RandomStringUtils.randomAlphabetic(10);

	private static final String firstName = "TestFirstName";
	private static final String lastName = "TestLastName";
	private static final String email = "test@dio-soft.com";

	private static String userId = null;

	@BeforeClass
	public static void setUp()
	{

		OrientGraph graph = new OrientGraphFactory("remote:192.168.1.12:2424/UserAccessControl").getTx();
		try
		{
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
		}
		catch (Exception e)
		{
			graph.rollback();
		}
		finally
		{
			graph.shutdown();
		}
	}

	@AfterClass
	public static void tearDown()
	{

		OrientGraph graph = new OrientGraphFactory("remote:192.168.1.12:2424/UserAccessControl").getTx();
		try
		{
			graph.removeVertex(graph.getVertex(userId));

			graph.commit();
		}
		catch (Exception e)
		{
			graph.rollback();
		}
		finally
		{
			graph.shutdown();
		}
	}

	@Test
	public void testIt()
	{
		running(testServer(3333), new Runnable()
		{
			public void run()
			{

				// User does not exist in all Jira accounts
				WSResponse response = WS.url("http://localhost:3333/gapp/accounts").get().get(timeout);

			}
		});
	}
}

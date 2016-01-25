import static play.test.Helpers.running;
import static play.test.Helpers.testServer;

import org.junit.Assert;
import org.junit.Test;

import play.libs.ws.WS;
import play.libs.ws.WSResponse;
import com.fasterxml.jackson.databind.JsonNode;

public class ApplicationTest
{

	@Test
	public void testgetUserInfo()
	{
		running(testServer(3333), new Runnable()
		{
			public void run()
			{

				WSResponse response =   WS.url("http://localhost:3333/users/liz@example.com")
				                        .get().get(5000);
				int status = response.getStatus();
				//default user should be added here for all requests
				System.out.println("body = " + response.getBody());
				JsonNode body = response.asJson();
				Assert.assertNotNull(body);
				Assert.assertEquals(200, status);
			}
		});

	}


}

package configuration;

import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;

import org.junit.Assert;
import org.junit.Test;

import play.Configuration;

public class MantlConfigFactoryTest {

	@Test
	public void testLoad() throws Exception {
		running(fakeApplication(), () -> {
			Configuration config = MantlConfigFactory.load("consul.url", "jiraservice");

			Assert.assertEquals("http://192.168.99.100:32780/rest/api/2", config.getString("jiraservice/jira/url"));
		});
	}

	@Test
	public void testGetCredentials() throws Exception {
		running(fakeApplication(), () -> {
			String token = MantlConfigFactory.generateToken("http://192.168.99.100:32771", "service-account", "supersecret");
			Assert.assertTrue(token.matches("\\w{8}(-\\w{4}){3}-\\w{12}"));

			ServiceAccountCredentials actual = MantlConfigFactory.getCredentials("http://192.168.99.100:32771", token, "jiraservice/jira");
			Assert.assertNotNull(actual);
			Assert.assertEquals("asirak", actual.getUser());
			Assert.assertEquals("secret", actual.getPassword());
		});
	}
}

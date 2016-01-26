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
//			Configuration config = MantlConfigFactory.load("http://52.32.229.145", "testKey");
			Configuration config = MantlConfigFactory.load("http://demo.consul.io", "testKey");

//			Assert.assertEquals("http://192.168.99.100:32780", config.getString("jiraservice/jira/url"));
			Assert.assertEquals("testValue", config.getString("testKey"));
		});
	}

	@Test
	public void testGetPassword() throws Exception {
		running(fakeApplication(), () -> {
			String token = MantlConfigFactory.generateToken("http://192.168.99.100:32771", "service-account", "supersecret");
			Assert.assertTrue(token.matches("\\w{8}(-\\w{4}){3}-\\w{12}"));

			String actual = MantlConfigFactory.getSecret("http://192.168.99.100:32771", token, "jira");
			Assert.assertEquals("secret", actual);
		});
	}
}

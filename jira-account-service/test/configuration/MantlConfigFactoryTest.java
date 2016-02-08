package configuration;

import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import play.Configuration;

public class MantlConfigFactoryTest {

	@Before
	public void setUp() throws Exception {
//		ImmutableMap.builder()
//				.put("jiraservice/vault/url", "http://192.168.99.100:32771")
//				.put("jiraservice/vault/user", "service-account")
//				.put("jiraservice/vault/pass", "supersecret")
//				.put("jiraservice/jira/url", "http://52.89.196.134:4752")
//				.put("jiraservice/orientdb/url", "remote:192.168.99.100:32782/UserAccessControl")
//				.build();
	}

	@Test
	public void testLoad() throws Exception {
		running(fakeApplication(), () -> {
			Configuration config = MantlConfigFactory.load("consul.url", "jiraservice");

			Assert.assertEquals("http://54.68.174.182:25714", config.getString("jiraservice/jira/url"));
		});
	}

	@Test
	public void testGetCredentials() throws Exception {
		running(fakeApplication(), () -> {
			String token = MantlConfigFactory.generateToken("http://192.168.99.100:32771", "service-account", "supersecret");
			Assert.assertTrue(token.matches("\\w{8}(-\\w{4}){3}-\\w{12}"));

			ServiceAccountCredentials actual = MantlConfigFactory.getCredentials("http://192.168.99.100:32771", token, "jiraservice/jira");
			Assert.assertNotNull(actual);
			Assert.assertEquals("admin", actual.getUser());
			Assert.assertEquals("secret", actual.getPassword());
		});
	}
}

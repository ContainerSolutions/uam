package configuration;

import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;

import org.junit.Assert;
import org.junit.Test;

import configuration.VaultHelper.Credentials;
import play.Configuration;

public class VaultHelperTest {
	@Test
	public void testGetCredentials() throws Exception {
		running(fakeApplication(), () -> {
			Configuration configuration = Configuration.root();

			String token = VaultHelper.generateToken(configuration);
			System.out.println(token);
			Assert.assertTrue(token.matches("\\w{8}(-\\w{4}){3}-\\w{12}"));
			
			Credentials actual = VaultHelper.getCredentials(configuration, token, "jiraservice/jira");
			Assert.assertNotNull(actual);
			Assert.assertEquals("admin", actual.getUser());
			Assert.assertEquals("secret", actual.getPassword());
		});
	}
	
}

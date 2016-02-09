package configuration;

import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;

import org.junit.Assert;
import org.junit.Test;

import play.Configuration;

public class ConsulConfigFactoryTest {

	@Test
	public void testLoad() throws Exception {
		running(fakeApplication(), () -> {
			Configuration config = ConsulConfigFactory.load(Configuration.root(), "jiraservice");

			Assert.assertEquals("http://54.68.174.182:25714", config.getString("jiraservice/jira/url"));
		});
	}
}

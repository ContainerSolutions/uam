package configuration;

import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;

import org.junit.Assert;
import org.junit.Test;

import play.Configuration;
import play.libs.ws.WS;
import play.libs.ws.WSClient;

public class ConsulConfigFactoryTest {

	private static final int timeoutMs = 1000000;
	private static final String key = "testKey";
	private static final String value = "{\"value\":\"testValue\"}";

	@Test
	public void testLoad() throws Exception {
		running(fakeApplication(), () -> {
			Configuration configuration = Configuration.root();
			String url = configuration.getString("consul.url") + "/v1/kv/" + key;
			WSClient client = WS.client();

			client.url(url).put(value).get(timeoutMs);

			Configuration config = ConsulConfigFactory.load(configuration, client, key);

			Assert.assertEquals(value, config.getString(key));

			client.url(url).delete().get(timeoutMs);
		});
	}
}

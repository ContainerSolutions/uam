package configuration;

import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.typesafe.config.ConfigFactory;

import play.Configuration;
import play.Logger;
import play.Logger.ALogger;
import play.libs.ws.WSClient;

public class ConsulConfigFactory {
	private static final ALogger logger = Logger.of(ConsulConfigFactory.class);
	private static final String consulUrl = "consul.url";

	public static Configuration load(Configuration configuration, WSClient client, String serviceName) {
		return new Configuration(configuration.underlying()
				.withFallback(ConfigFactory.parseMap(loadFromConsul(configuration, client, serviceName))));
	}

	private static Map<String, String> loadFromConsul(Configuration configuration, WSClient client, String serviceName) {
		String url = configuration.getString(consulUrl) + "/v1/kv/" + serviceName + "?recurse";
		return client.url(url).get().map(response -> {
			if (response.getStatus() != 200) {
				return Collections.<String, String> emptyMap();
			}

			Map<String, String> configMap = new HashMap<>();
			response.asJson().forEach(jsonNode -> configMap.put(jsonNode.get("Key").asText(),
					new String(Base64.getDecoder().decode(jsonNode.get("Value").asText()))));
			logger.debug(serviceName + " configuration from Consul: " + configMap);
			return configMap;
		}).recover(throwable -> Collections.<String, String> emptyMap()).get(50, TimeUnit.SECONDS);
	}
}

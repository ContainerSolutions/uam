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
import play.libs.ws.WS;

public class ConsulConfigFactory {
	private static final ALogger logger = Logger.of(ConsulConfigFactory.class);
	private static final String consulUrl = "consul.url";

	public static Configuration load(Configuration configuration, String serviceName) {
		return new Configuration(configuration.underlying()
				.withFallback(ConfigFactory.parseMap(loadFromConsul(configuration, serviceName))));
	}

	private static Map<String, String> loadFromConsul(Configuration configuration, String serviceName) {
		return WS.client().url(configuration.getString(consulUrl) + "/v1/kv/" + serviceName + "?recurse").get()
				.map(response -> {
					if (response.getStatus() != 200) {
						return Collections.<String, String> emptyMap();
					}

					Map<String, String> configMap = new HashMap<>();
					response.asJson().forEach(jsonNode -> configMap.put(jsonNode.get("Key").asText(),
							new String(Base64.getDecoder().decode(jsonNode.get("Value").asText()))));
					logger.debug(serviceName + " configuration from Consul: " + configMap);
					return configMap;
				}).get(10, TimeUnit.SECONDS);
	}
}

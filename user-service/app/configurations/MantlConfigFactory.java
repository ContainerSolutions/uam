package configurations;

import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import com.typesafe.config.ConfigFactory;

import play.Configuration;
import play.libs.Json;
import play.libs.ws.WS;

public class MantlConfigFactory {
	public static Configuration load(String consulUrlKey, String serviceName) {
		Configuration configuration = Configuration.root();
		String consulUrl = configuration.getString(consulUrlKey);
		return StringUtils.isEmpty(consulUrl) ? configuration : new Configuration(configuration.underlying().withFallback(ConfigFactory
				.parseMap(WS.client().url(consulUrl + "/v1/kv/" + serviceName + "?recurse").get().map(response -> {
					if (response.getStatus() != 200) {
						return Collections.<String, String> emptyMap();
					}

					Map<String, String> configMap = new HashMap<>();
					response.asJson().forEach(jsonNode -> configMap.put(jsonNode.get("Key").asText(),
							new String(Base64.getDecoder().decode(jsonNode.get("Value").asText()))));
					System.out.println(configMap);
					return configMap;
				}).get(10, TimeUnit.SECONDS))));
	}

}

package configuration;

import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.typesafe.config.ConfigFactory;

import play.Configuration;
import play.libs.ws.WS;

public class MantlConfigFactory {
	public static Configuration load(String consulUrl, String serviceName) {
		return new Configuration(Configuration.root().underlying().withFallback(ConfigFactory
				.parseMap(WS.client().url(consulUrl + "/v1/kv/" + serviceName + "?recurse").get().map(response -> {
					if (response.getStatus() != 200) {
						return Collections.<String, String> emptyMap();
					}

					Map<String, String> configMap = new HashMap<>();
					response.asJson().forEach(jsonNode -> configMap.put(jsonNode.get("Key").asText(),
							new String(Base64.getDecoder().decode(jsonNode.get("Value").asText()))));
					return configMap;
				}).get(10, TimeUnit.DAYS))));
	}

	public static String generateToken(String vaultUrl, String user, String pass) {
		return WS.client().url(vaultUrl + "/v1/auth/userpass/login/" + user).put("{\"password\":\"" + pass + "\"}")
				.map(response -> {
					if (response.getStatus() != 200) {
						return response.getBody();
					}

					return response.asJson().findValue("client_token").asText();
				}).get(10, TimeUnit.SECONDS);
	}

	public static String getSecret(String vaultUrl, String token, String account) {
		return WS.client().url(vaultUrl + "/v1/secret/" + account).setHeader("X-Vault-Token", token).get()
				.map(response -> {
					if (response.getStatus() != 200) {
						return response.getBody();
					}

					return response.asJson().findValue("value").asText();
				}).get(10, TimeUnit.SECONDS);
	}
}

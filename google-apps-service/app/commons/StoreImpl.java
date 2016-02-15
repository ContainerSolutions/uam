package commons;

import org.apache.commons.codec.binary.Base64;

import play.Configuration;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;

public class StoreImpl implements Store {
	private final String CONSUL_REST = Configuration.root().getString("consul.store.rest.url");

	public void put(String key, String value) {
		WSResponse response = WS.client().url(CONSUL_REST + key).put(value).get(5000);
		if (response.getStatus() != 200) {
			throw new IllegalStateException(response.getStatusText());
		}
	}

	@Override
	public String get(String key) {

		WSResponse response = WS.client().url(CONSUL_REST + key).get().get(5000);
		if (response.getStatus() != 200) {
			return null;
		}

		String encodedValue = response.asJson().findValue("Value").asText();

		String value = new String(Base64.decodeBase64(encodedValue));

		return value;
	}

	@Override
	public String remove(String key) {

		String value = get(key);
		WSResponse response = WS.client().url(CONSUL_REST + key).delete().get(5000);
		int status = response.getStatus();
		if (status != 200) {
			throw new IllegalStateException("Error while deliting key : " + response.getStatusText());
		}
		return value;
	}
}

package common;

import play.Configuration;
import play.libs.Json;
import play.libs.ws.WS;
import play.mvc.Controller;
import play.mvc.Http.RequestBody;
import play.mvc.Result;
import play.mvc.BodyParser;

import org.apache.commons.codec.binary.Base64;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;


public class StoreImpl implements Store
{
	private final String CONSUL_REST = Configuration.root().getString("consul.store.rest.url") ;
	private final String CONSUL_USER = Configuration.root().getString("consul.user") ;
	private final String CONSUL_PASSWORD = Configuration.root().getString("consul.password") ;

	public void put(String key, String value)
	{
		WS.client().url(CONSUL_REST + key).setAuth(CONSUL_USER, CONSUL_PASSWORD).post(value).get(5000);
	}

	@Override
	public String get(String key)
	{


		String encodedValue = WS.client().url(CONSUL_REST + key).setAuth(CONSUL_USER, CONSUL_PASSWORD).get().get(5000).asJson().findValue(key).asText();
		String value = Base64.encodeBase64String(encodedValue.getBytes());

		return value;
	}

	@Override
	public String remove(String key)
	{

		String value = get(key);
		int status = WS.client().url(CONSUL_REST).delete().get(5000).getStatus();
		if (status != 200)
		{
			throw new IllegalStateException("Error while deliting key : " + status);
		}
		return value;
	}
}

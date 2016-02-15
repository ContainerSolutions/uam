package commons;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.math.NumberUtils;

import com.fasterxml.jackson.databind.JsonNode;

import play.Configuration;
import play.libs.ws.WS;

public class VaultSecretStoreImpl implements VaultSecretStore {
	private static final String userId = Configuration.root().getString("play.crypto.secret");
	private static final String vaultUrl = "gappsservice/vault/url";
	private static final String timeout = "gappsservice/vault/timeout";

	private final Store store;
	private final String clientToken;

	public static VaultSecretStore getVaultSecretStore(Store store) {
		return new VaultSecretStoreImpl(store, generateToken(store));
	}

	private VaultSecretStoreImpl(Store store, String clientToken) {
		this.store = store;
		this.clientToken = clientToken;
	}

	@Override
	public JsonNode read(String key) {
		return WS.client().url(store.get(vaultUrl) + "/v1/secret/" + key).setHeader("X-Vault-Token", clientToken).get()
				.map(response -> {
					if (response.getStatus() != 200) {
						return null;
					}
					return response.asJson().findValue("data");
				}).get(NumberUtils.toLong(store.get(timeout)), TimeUnit.SECONDS);
	}

	@Override
	public void write(String key, JsonNode data) {
		WS.client().url(store.get(vaultUrl) + "/v1/secret/" + key).setHeader("X-Vault-Token", clientToken).put(data)
				.map(response -> {
					if (response.getStatus() != 204) {
						throw new IllegalStateException("Vault request failed: " + response.getStatusText());
					}
					return "Ok";
				}).get(NumberUtils.toLong(store.get(timeout)), TimeUnit.SECONDS);
	}

	@Override
	public void delete(String key) {
		WS.client().url(store.get(vaultUrl) + "/v1/secret/" + key).setHeader("X-Vault-Token", clientToken).delete()
				.map(response -> {
					if (response.getStatus() != 204) {
						throw new IllegalStateException("Vault request failed: " + response.getStatusText());
					}
					return "Ok";
				}).get(NumberUtils.toLong(store.get(timeout)), TimeUnit.SECONDS);
	}

	private static String generateToken(Store store) {
		String url = store.get(vaultUrl) + "/v1/auth/app-id/login";
		return WS.client().url(url)
				.put("{\"app_id\":\"" + createAppId() + "\",\"user_id\":\"" + userId + "\"}")
				.map(response -> {
					if (response.getStatus() != 200) {
						throw new IllegalStateException("Vault request failed: " + response.getBody());
					}
					return response.asJson().findValue("client_token").asText();
				}).get(NumberUtils.toLong(store.get(timeout)), TimeUnit.SECONDS);
	}

	private static String createAppId() {
		try {
			InetAddress address = InetAddress.getLocalHost();
			NetworkInterface network = NetworkInterface.getByInetAddress(address);
			byte[] hardwareAddress = network.getHardwareAddress();

			StringBuilder mac = new StringBuilder();
			for (int i = 0; i < hardwareAddress.length; i++) {
				mac.append(String.format("%02X%s", hardwareAddress[i], (i < hardwareAddress.length - 1) ? "-" : ""));
			}

			return mac.toString();
		} catch (UnknownHostException | SocketException e) {
			e.printStackTrace();
			throw new IllegalStateException(e.getMessage());
		}
	}
}

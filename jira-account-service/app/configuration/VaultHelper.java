package configuration;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import play.Configuration;
import play.Logger;
import play.Logger.ALogger;
import play.libs.Json;
import play.libs.ws.WS;

public class VaultHelper {
	private static final ALogger logger = Logger.of(VaultHelper.class);
	private static final String vaultHeader = "X-Vault-Token";
	private static final String userId = "play.crypto.secret";
	private static final String vaultUrl = "jiraservice/vault/url";

	public static String generateToken(Configuration configuration) {
		String url = configuration.getString(vaultUrl) + "/v1/auth/app-id/login";
		return WS.client().url(url)
				.put("{\"app_id\":\"" + createAppId() + "\",\"user_id\":\"" + configuration.getString(userId) + "\"}")
				.map(response -> {
					if (response.getStatus() != 200) {
						logger.error("Failed to retrieve client token from Vault: " + response.getStatus() + response.getBody());
						return null;
					}

					return response.asJson().findValue("client_token").asText();
				}).get(10, TimeUnit.SECONDS);
	}

	public static Credentials getCredentials(Configuration configuration, String token, String application) {

		return WS.client().url(configuration.getString(vaultUrl) + "/v1/secret/" + application)
				.setHeader(vaultHeader, token).get().map(response -> {
					if (response.getStatus() != 200) {
						logger.error("Failed to retrieve secret from Vault: " + response.getBody());
						return null;
					}

					return Json.fromJson(response.asJson().findValue("data"), Credentials.class);
				}).get(10, TimeUnit.SECONDS);
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
			logger.error("Failed to get MAC address", e);
			return null;
		}
	}

	public static class Credentials {
		private String user;
		private String password;

		public String getUser() {
			return user;
		}
		public void setUser(String user) {
			this.user = user;
		}
		public String getPassword() {
			return password;
		}
		public void setPassword(String password) {
			this.password = password;
		}
	}
}

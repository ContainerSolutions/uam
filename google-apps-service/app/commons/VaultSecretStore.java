package commons;

import com.fasterxml.jackson.databind.JsonNode;

public interface VaultSecretStore {

	JsonNode read(String key);

	void write(String key, JsonNode data);
	
	void delete(String key);
}

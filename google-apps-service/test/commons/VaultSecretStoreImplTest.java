package commons;

import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

import play.libs.Json;

public class VaultSecretStoreImplTest {

	@Test
	public void testPutGetDelete() throws Exception {
		running(fakeApplication(), () -> {
			String key = "testKey";
			JsonNode expectedValue = Json.parse("{\"test\":\"data\"}");

			VaultSecretStore testClass = VaultSecretStoreImpl.getVaultSecretStore(new StoreImpl());
			cleanUp(key, testClass);

			testClass.write(key, expectedValue);

			JsonNode actualValue = testClass.read(key);
			Assert.assertEquals(expectedValue, actualValue);

			testClass.delete(key);

			JsonNode emptyValue = testClass.read(key);
			Assert.assertNull(emptyValue);
		});
	}

	private void cleanUp(String key, VaultSecretStore testClass) {
		try {
			testClass.delete(key);
		} catch (Exception ex) {
			// Ignore - we just need to clean out old test data
			ex.printStackTrace();
		}
	}
}

package commons;

import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;

import org.junit.Assert;
import org.junit.Test;

public class StoreTest {

	@Test
	public void testPutGetAndDelete() throws Exception {
		running(fakeApplication(), () -> {
			String inputKey = "MyTestKey";
			String inputValue = "MySpecialTestValue";

			Store testClass = new StoreImpl();
			try {
				testClass.remove(inputKey);
			} catch (Exception ex) {
				// Ignore - we just need to clean out old test data
				ex.printStackTrace();
			}

			testClass.put(inputKey, inputValue);

			String valueFromStore = testClass.get(inputKey);
			Assert.assertEquals(inputValue, valueFromStore);

			String removedValueFromStore = testClass.remove(inputKey);
			Assert.assertEquals(inputValue, removedValueFromStore);

			String emptyValue = testClass.get(inputKey);
			Assert.assertNull(emptyValue);
		});
	}

}

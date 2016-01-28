package commons;

import static play.test.Helpers.running;
import static play.test.Helpers.testServer;
import play.test.*;

import org.junit.Assert;
import org.junit.Test;

import play.libs.ws.WS;
import play.libs.ws.WSResponse;
import com.fasterxml.jackson.databind.JsonNode;

public class StoreTest
{

	@Test
	public void testPutGetAndDelete() throws Exception
	{
		//running(fakeApplication());
		String inputKey = "MyTestKey";
		String inputValue = "MySpecialTestValue";

		Store testClass = new StoreImpl();
		try
		{
			testClass.remove(inputKey);
		}
		catch (Exception ex)
		{
			//Ignore - we just need to clean out old test data
			ex.printStackTrace();
		}

		testClass.put(inputKey, inputValue);

		String valueFromStore = testClass.get(inputKey);
		Assert.assertEquals(inputValue, valueFromStore);

		String removedValueFromStore = testClass.remove(inputKey);
		Assert.assertEquals(inputValue, removedValueFromStore);

		String emptyValue = testClass.get(inputKey);
		Assert.assertNull(emptyValue);

	}

}

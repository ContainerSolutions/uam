package commons;

import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.admin.directory.Directory;
import com.google.api.services.admin.directory.model.User;

public class GoogleServiceFactoryImplTest {

	@Test
	public void testCreateDirectoryCredential() throws Exception {
		running(fakeApplication(), () -> {
			try {
				// test class
				GoogleServiceFactoryImpl testClass = new GoogleServiceFactoryImpl();

				Credential returnedValue = testClass.createDirectoryCredential();
				// asserts
				Assert.assertNotNull(returnedValue);

			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	@Test
	public void testCreateDirectoryService() throws Exception {
		running(fakeApplication(), () -> {
			try {
				// test class
				GoogleServiceFactoryImpl testClass = new GoogleServiceFactoryImpl();
				Directory service = testClass.createDirectoryService();

				List<User> results = service.users().list().setMaxResults(10).setOrderBy("email")
						.setDomain("dio-soft.com").execute().getUsers();
						/* List<Group> res = service.groups().list() */
						// .setMaxResults(10)
						// .setUserKey("vtegza@dio-soft.com")
						// .setDomain("dio-soft.com")
						// .execute().getGroups();

				// res.forEach( group ->
				// {
				// System.out.println(group.getEmail());
				/* }); */

				Assert.assertEquals(10, results.size());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}

}

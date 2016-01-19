import static play.test.Helpers.running;
import static play.test.Helpers.testServer;

import org.junit.Assert;
import org.junit.Test;

import play.libs.F;
import play.libs.ws.WS;


/**
*
* Simple (JUnit) tests that can call all parts of a play app.
* If you are interested in mocking a whole application, see the wiki for more details.
*
*/
public class ApplicationTest {

    @Test
    public void renderTemplate() {
        running(testServer(3333), new Runnable(){
		public void run(){
			Assert.assertEquals(200,
					WS.url("http://localhost:3333/users/some@email.com")
					.get().get(5000).getStatus()
					);
		}
	});
	    
    }


}

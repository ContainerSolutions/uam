import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Module;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import helpers.guice.AdGuiceModule;
import play.Application;
import play.ApplicationLoader;
import play.Environment;
import play.inject.guice.GuiceApplicationBuilder;
import play.inject.guice.GuiceApplicationLoader;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;
import play.test.Helpers;
import play.test.TestBrowser;
import play.test.TestServer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class IntegrationTest {

    private int port;
    private TestServer testServer;

    @Inject
    private Application application;

    @Before
    public void setUp() {
        Module testModule = new AdGuiceModule();

        GuiceApplicationBuilder builder = new GuiceApplicationLoader()
                .builder(new ApplicationLoader.Context(Environment.simple()))
                .overrides(testModule);
        Guice.createInjector(builder.applicationModule()).injectMembers(this);

        if (testServer != null) {
            testServer.stop();
        }
        port = play.api.test.Helpers.testServerPort();
        testServer = Helpers.testServer(port, application);
        testServer.start();
    }

    @After
    public void tearDown() {
        if (testServer != null) {
            testServer.stop();
            testServer = null;
            application = null;
        }
    }

    @Test
    @Ignore
    public void testIndex_Browser() {
        TestBrowser browser = Helpers.testBrowser(new HtmlUnitDriver(false));
        browser.goTo("http://localhost:" + port);
        assertTrue(browser.pageSource().contains("raml/api/ad.raml"));
    }

    @Test
    public void testIndex_WS() {
        WSResponse response = WS.url("http://localhost:" + port).get().get(1000);
        assertTrue(response.getBody().contains("raml/api/ad.raml"));
    }

    @Test
    public void testGetAll_Optimistic() {
        WSResponse response = WS.url("http://localhost:" + port + "/accounts").get().get(2000);
        assertTrue(response.getBody().contains("{\"firstName\":null,\"lastName\":null,\"id\":\"Administrator\",\"email\":null}"));
    }

    @Test
    public void testGrantListRevoke() {
        //given
        String jsonTestAccount = "{\"firstName\":\"TestUser\",\"lastName\":\"Integrational\",\"id\":\"integrationuser\",\"email\":\"inttestuser@domain.com\"}";
        //cleanup
        WS.url("http://localhost:" + port + "/account")
                .setContentType("application/json").setBody(jsonTestAccount)
                .delete().get(2000);

        //create account
        WSResponse responseCreate = WS.url("http://localhost:" + port + "/account")
                .setContentType("application/json")
                .post(jsonTestAccount).get(2000);
        assertEquals("Create account failed", Helpers.OK, responseCreate.getStatus());

        //list account
        WSResponse responseCheckCreate = WS.url("http://localhost:" + port + "/account/integrationuser")
                .get().get(2000);
        assertEquals("List account after creation failed", Helpers.OK, responseCheckCreate.getStatus());
        assertEquals("List account json differs", jsonTestAccount, responseCheckCreate.getBody());

        //delete account
        WSResponse responseDelete = WS.url("http://localhost:" + port + "/account")
                .setContentType("application/json").setBody(jsonTestAccount)
                .delete().get(2000);
        assertEquals("Delete account failed", Helpers.OK, responseDelete.getStatus());

        //verify
        WSResponse responseCheckDelete = WS.url("http://localhost:" + port + "/account/integrationuser")
                .get().get(2000);
        assertEquals("List account after deletion failed", Helpers.NOT_FOUND, responseCheckDelete.getStatus());

    }

}

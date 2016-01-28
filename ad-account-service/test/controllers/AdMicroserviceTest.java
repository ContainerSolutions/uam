package controllers;

import com.diosoft.uar.AccessManagerException;
import com.diosoft.uar.ldap.ad.WindowsAccountAccess;
import com.diosoft.uar.ldap.ad.WindowsAccountAccessFilter;
import com.diosoft.uar.ldap.ad.WindowsAccountAccessManager;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import play.api.Application;
import play.libs.Json;
import play.mvc.Http;
import play.test.Helpers;
import play.mvc.Result;
import play.test.WithApplication;
import play.test.WithBrowser;
import play.test.WithServer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static play.mvc.Http.Status.*;

public class AdMicroserviceTest extends WithServer{

    @Test
    public void testIndex_Optimistic() {
        Result result = new AdMicroservice(mock(WindowsAccountAccessManager.class)).index();
        assertTrue(Helpers.contentAsString(result).contains("AD REST API"));
    }

    @Test
    public void testIndex_200() {
        Result result = new AdMicroservice(mock(WindowsAccountAccessManager.class)).index();
        assertEquals(OK, result.status());
    }

    @Test
    public void testIndex_ContentType() {
        Result result = new AdMicroservice(mock(WindowsAccountAccessManager.class)).index();
        assertEquals("text/html", result.contentType());
    }

    @Test
    public void testIndex_charset() {
        Result result = new AdMicroservice(mock(WindowsAccountAccessManager.class)).index();
        assertEquals("utf-8", result.charset());
    }

    @Test
    public void testGetAccounts_Optimistic() throws Exception {
        //expected
        String expected = "[{\"firstName\":\"firstName1\",\"lastName\":\"lastName1\",\"login\":\"login1\",\"email\":\"email1@domain.com\"}," +
                "{\"firstName\":\"firstName2\",\"lastName\":\"lastName2\",\"login\":\"login2\",\"email\":\"email2@domain.com\"}]";
        //when
        WindowsAccountAccessManager mockAccessManager = mock(WindowsAccountAccessManager.class);
        Collection<WindowsAccountAccess> accounts = new ArrayList<>();
        accounts.add(new WindowsAccountAccess("login1", "firstName1", "lastName1", "email1@domain.com"));
        accounts.add(new WindowsAccountAccess("login2", "firstName2", "lastName2", "email2@domain.com"));
        when(mockAccessManager.list(new WindowsAccountAccessFilter("*"))).thenReturn(accounts);

        //then
        AdMicroservice target = new AdMicroservice(mockAccessManager);
        String actual = Helpers.contentAsString(target.getAccounts());
        
        //assert
        assertEquals(expected, actual);
        
    }

    @Test
    public void testGetAccount_Optimistic() throws Exception {
        //expected
        String expected = "{\"firstName\":\"firstName1\",\"lastName\":\"lastName1\",\"login\":\"login1\",\"email\":\"email1@domain.com\"}";
        //given
        String searchParam = "login1";
        //when
        WindowsAccountAccessManager mockAccessManager = mock(WindowsAccountAccessManager.class);
        Collection<WindowsAccountAccess> accounts = new ArrayList<>();
        accounts.add(new WindowsAccountAccess("login1", "firstName1", "lastName1", "email1@domain.com"));
        when(mockAccessManager.list(new WindowsAccountAccessFilter("login1"))).thenReturn(accounts);

        //then
        AdMicroservice target = new AdMicroservice(mockAccessManager);
        String actual = Helpers.contentAsString(target.getAccount(searchParam));

        //assert
        assertEquals(expected, actual);

    }

    @Test
    public void testGetAccount_Optimistic_ContentType() throws Exception {
        //expected
        String expected = "application/json";
        //given
        String searchParam = "login1";
        //when
        WindowsAccountAccessManager mockAccessManager = mock(WindowsAccountAccessManager.class);
        WindowsAccountAccess windowsAccountAccess = new WindowsAccountAccess("login1", "firstName1", "lastName1", "email1@domain.com");
        when(mockAccessManager.list(new WindowsAccountAccessFilter("login1"))).thenReturn(Arrays.asList(windowsAccountAccess));

        //then
        AdMicroservice target = new AdMicroservice(mockAccessManager);
        String actual = target.getAccount(searchParam).contentType();

        //assert
        assertEquals(expected, actual);

    }

    @Test
    public void testGetAccount_NotFound_404() throws Exception {
        //expected
        int expected = NOT_FOUND;
        //given
        String searchParam = "login1";
        //when
        WindowsAccountAccessManager mockAccessManager = mock(WindowsAccountAccessManager.class);
        when(mockAccessManager.list(new WindowsAccountAccessFilter("login1"))).thenReturn(new ArrayList<>());

        //then
        AdMicroservice target = new AdMicroservice(mockAccessManager);
        int actual = target.getAccount(searchParam).status();

        //assert
        assertEquals(expected, actual);

    }

    @Test
    public void testGetAccount_MoreThanOne_500() throws Exception {
        //expected
        int expected = INTERNAL_SERVER_ERROR;
        //given
        String searchParam = "login1";
        //when
        WindowsAccountAccessManager mockAccessManager = mock(WindowsAccountAccessManager.class);
        Collection<WindowsAccountAccess> accounts = new ArrayList<>();
        accounts.add(new WindowsAccountAccess("login1", "firstName1", "lastName1", "email1@domain.com"));
        accounts.add(new WindowsAccountAccess("login1", "firstName2", "lastName2", "email2@domain.com"));
        when(mockAccessManager.list(new WindowsAccountAccessFilter("login1"))).thenReturn(accounts);

        //then
        AdMicroservice target = new AdMicroservice(mockAccessManager);
        int actual = target.getAccount(searchParam).status();

        //assert
        assertEquals(expected, actual);

    }

    @Test
    public void testGetAccount_InternalError_500() throws Exception {
        //expected
        int expected = INTERNAL_SERVER_ERROR;
        //given
        String searchParam = "login1";
        //when
        WindowsAccountAccessManager mockAccessManager = mock(WindowsAccountAccessManager.class);
        when(mockAccessManager.list(new WindowsAccountAccessFilter("login1"))).thenThrow(new AccessManagerException("test"));

        //then
        AdMicroservice target = new AdMicroservice(mockAccessManager);
        int actual = target.getAccount(searchParam).status();

        //assert
        assertEquals(expected, actual);

    }

    @Test
    public void testGetAccount_InternalError_Message() throws Exception {
        //expected
        String expected = "NullPointerException";
        //given
        String searchParam = "login1";
        //when
        WindowsAccountAccessManager mockAccessManager = mock(WindowsAccountAccessManager.class);
        when(mockAccessManager.list(new WindowsAccountAccessFilter("login1"))).thenThrow(new NullPointerException());

        //then
        AdMicroservice target = new AdMicroservice(mockAccessManager);
        String actual = Helpers.contentAsString(target.getAccount(searchParam));

        //assert
        assertEquals(expected, actual);

    }


    @Test
    public void testDeleteAccount_Optimistic() throws Exception {
        //expected
        int expected = OK;

        //when
        JsonNode body = Json.parse("{\"firstName\":\"firstName1\",\"lastName\":\"lastName1\",\"login\":\"login1\",\"email\":\"email1@domain.com\"}");
        WindowsAccountAccessManager mockAccessManager = mock(WindowsAccountAccessManager.class);

        //then
        AdMicroservice target = new AdMicroservice(mockAccessManager);
        Http.RequestBuilder request = Helpers.fakeRequest(routes.AdMicroservice.deleteAccount()).method("DELETE").bodyJson(body);
        int actual = Helpers.invokeWithContext(request, target::deleteAccount).status();

        //assert
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testDeleteAccount_InternalError_500() throws Exception {
        //expected
        int expected = INTERNAL_SERVER_ERROR;

        //when
        JsonNode body = Json.parse("{\"firstName\":\"firstName1\",\"lastName\":\"lastName1\",\"login\":\"login1\",\"email\":\"email1@domain.com\"}");
        WindowsAccountAccessManager mockAccessManager = mock(WindowsAccountAccessManager.class);
        doThrow(new NullPointerException()).when(mockAccessManager).revoke(new WindowsAccountAccess("login1", "firstName1", "lastName1", "email1@domain.com"));

        //then
        AdMicroservice target = new AdMicroservice(mockAccessManager);
        Http.RequestBuilder request = Helpers.fakeRequest(routes.AdMicroservice.deleteAccount()).method("DELETE").bodyJson(body);
        int actual = Helpers.invokeWithContext(request, target::deleteAccount).status();

        //assert
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testAddAccount_Optimistic() throws Exception {
        //expected
        int expected = OK;

        //when
        JsonNode body = Json.parse("{\"firstName\":\"firstName1\",\"lastName\":\"lastName1\",\"login\":\"login1\",\"email\":\"email1@domain.com\"}");
        WindowsAccountAccessManager mockAccessManager = mock(WindowsAccountAccessManager.class);

        //then
        AdMicroservice target = new AdMicroservice(mockAccessManager);
        Http.RequestBuilder request = Helpers.fakeRequest(routes.AdMicroservice.addAccount()).method("POST").bodyJson(body);
        int actual = Helpers.invokeWithContext(request, target::addAccount).status();

        //assert
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testAddAccount_InternalError_500() throws Exception {
        //expected
        int expected = INTERNAL_SERVER_ERROR;

        //when
        JsonNode body = Json.parse("{\"firstName\":\"firstName1\",\"lastName\":\"lastName1\",\"login\":\"login1\",\"email\":\"email1@domain.com\"}");
        WindowsAccountAccessManager mockAccessManager = mock(WindowsAccountAccessManager.class);
        doThrow(new NullPointerException()).when(mockAccessManager).grant(new WindowsAccountAccess("login1", "firstName1", "lastName1", "email1@domain.com"));

        //then
        AdMicroservice target = new AdMicroservice(mockAccessManager);

        Http.RequestBuilder request = Helpers.fakeRequest(routes.AdMicroservice.addAccount()).method("POST").bodyJson(body);
        int actual = Helpers.invokeWithContext(request, target::addAccount).status();

        //assert
        Assert.assertEquals(expected, actual);
    }



}

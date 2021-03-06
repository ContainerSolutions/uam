package controllers;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.TestActorRef;
import com.diosoft.uam.AccessManagerException;
import com.diosoft.uam.db.AccountStorage;
import com.diosoft.uam.db.AuditLogStorage;
import com.diosoft.uam.db.entry.AccountEntry;
import com.diosoft.uam.ldap.ad.WindowsAccountAccess;
import com.diosoft.uam.ldap.ad.WindowsAccountAccessFilter;
import com.diosoft.uam.ldap.ad.WindowsAccountAccessManager;
import com.fasterxml.jackson.databind.JsonNode;
import helpers.akka.AdAccountStorageActor;
import helpers.akka.AdAccountsActor;
import helpers.akka.AuditLogsActor;
import org.junit.Test;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static play.mvc.Http.Status.*;

public class AdMicroserviceTest {

    @Test
    public void testIndex_Optimistic() {
        Result result = new AdMicroservice(null,null,null,null).index();
        assertTrue(Helpers.contentAsString(result).contains("AD REST API"));
    }

    @Test
    public void testIndex_200() {
        Result result = new AdMicroservice(null,null,null,null).index();
        assertEquals(OK, result.status());
    }

    @Test
    public void testIndex_ContentType() {
        Result result = new AdMicroservice(null,null,null,null).index();
        assertEquals("text/html", result.contentType());
    }

    @Test
    public void testIndex_charset() {
        Result result = new AdMicroservice(null,null,null,null).index();
        assertEquals("utf-8", result.charset());
    }

    @Test
    public void testGetAccounts_Optimistic() throws Exception {
        //expected
        String expected = "[{\"firstName\":\"firstName1\",\"lastName\":\"lastName1\",\"id\":\"login1\",\"email\":\"email1@domain.com\"}," +
                "{\"firstName\":\"firstName2\",\"lastName\":\"lastName2\",\"id\":\"login2\",\"email\":\"email2@domain.com\"}]";
        //when
        WindowsAccountAccessManager mockAccessManager = mock(WindowsAccountAccessManager.class);
        Collection<WindowsAccountAccess> accounts = new ArrayList<>();
        accounts.add(new WindowsAccountAccess("login1", "firstName1", "lastName1", "email1@domain.com"));
        accounts.add(new WindowsAccountAccess("login2", "firstName2", "lastName2", "email2@domain.com"));
        when(mockAccessManager.list(new WindowsAccountAccessFilter("*"))).thenReturn(accounts);

        ActorSystem system = ActorSystem.create("system");
        Props props = Props.create(AdAccountsActor.class, () -> new AdAccountsActor(mockAccessManager));
        TestActorRef<AdAccountsActor> testAdActorRef = TestActorRef.create(system, props);
        //then
        AdMicroservice target = new AdMicroservice(null, testAdActorRef, null, null);
        String actual = Helpers.contentAsString(target.getAccounts().get(2000));

        //assert
        assertEquals(expected, actual);

    }

    @Test
    public void testGetAccount_Optimistic() throws Exception {
        //expected
        String expected = "{\"firstName\":\"firstName1\",\"lastName\":\"lastName1\",\"id\":\"login1\",\"email\":\"email1@domain.com\"}";
        //given
        String searchParam = "login1";
        //when
        WindowsAccountAccessManager mockAccessManager = mock(WindowsAccountAccessManager.class);
        Collection<WindowsAccountAccess> accounts = new ArrayList<>();
        accounts.add(new WindowsAccountAccess("login1", "firstName1", "lastName1", "email1@domain.com"));
        when(mockAccessManager.list(new WindowsAccountAccessFilter("login1"))).thenReturn(accounts);

        ActorSystem system = ActorSystem.create("system");
        Props props = Props.create(AdAccountsActor.class, () -> new AdAccountsActor(mockAccessManager));
        TestActorRef<AdAccountsActor> testAdActorRef = TestActorRef.create(system, props);
        //then
        AdMicroservice target = new AdMicroservice(system, testAdActorRef, null, null);
        String actual = Helpers.contentAsString(target.getAccount(searchParam).get(2000));

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
        when(mockAccessManager.list(new WindowsAccountAccessFilter("login1"))).thenReturn(Collections.singletonList(windowsAccountAccess));

        ActorSystem system = ActorSystem.create("system");
        Props props = Props.create(AdAccountsActor.class, () -> new AdAccountsActor(mockAccessManager));
        TestActorRef<AdAccountsActor> testAdActorRef = TestActorRef.create(system, props);
        //then
        AdMicroservice target = new AdMicroservice(system, testAdActorRef, null, null);

        String actual = target.getAccount(searchParam).get(2000).contentType();

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

        ActorSystem system = ActorSystem.create("system");
        Props props = Props.create(AdAccountsActor.class, () -> new AdAccountsActor(mockAccessManager));
        TestActorRef<AdAccountsActor> testAdActorRef = TestActorRef.create(system, props);
        //then
        AdMicroservice target = new AdMicroservice(system, testAdActorRef, null, null);
        int actual = target.getAccount(searchParam).get(2000).status();

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

        ActorSystem system = ActorSystem.create("system");
        Props props = Props.create(AdAccountsActor.class, () -> new AdAccountsActor(mockAccessManager));
        TestActorRef<AdAccountsActor> testAdActorRef = TestActorRef.create(system, props);
        //then
        AdMicroservice target = new AdMicroservice(system, testAdActorRef, null, null);
        int actual = target.getAccount(searchParam).get(2000).status();

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

        ActorSystem system = ActorSystem.create("system");
        Props props = Props.create(AdAccountsActor.class, () -> new AdAccountsActor(mockAccessManager));
        TestActorRef<AdAccountsActor> testAdActorRef = TestActorRef.create(system, props);
        //then
        AdMicroservice target = new AdMicroservice(system, testAdActorRef, null, null);
        int actual = target.getAccount(searchParam).get(2000).status();

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

        ActorSystem system = ActorSystem.create("system");
        Props props = Props.create(AdAccountsActor.class, () -> new AdAccountsActor(mockAccessManager));
        TestActorRef<AdAccountsActor> testAdActorRef = TestActorRef.create(system, props);
        //then
        AdMicroservice target = new AdMicroservice(system, testAdActorRef, null, null);
        String actual = Helpers.contentAsString(target.getAccount(searchParam).get(2000));

        //assert
        assertEquals(expected, actual);

    }


    @Test
    public void testDeleteAccount_Optimistic() throws Exception {
        //expected
        int expected = OK;

        //given
        JsonNode body = Json.parse("{\"firstName\":\"firstName1\",\"lastName\":\"lastName1\",\"id\":\"login1\",\"email\":\"email1@domain.com\",\"unknown\":\"field\"}");

        //when
        WindowsAccountAccessManager mockAccessManager = mock(WindowsAccountAccessManager.class);
        AccountStorage mockAccountStorage = mock(AccountStorage.class);
        AuditLogStorage mockAuditStorage = mock(AuditLogStorage.class);

        ActorSystem system = ActorSystem.create("system");
        Props adProps = Props.create(AdAccountsActor.class, () -> new AdAccountsActor(mockAccessManager));
        Props storageProps = Props.create(AdAccountStorageActor.class, () -> new AdAccountStorageActor(mockAccountStorage));
        Props auditProps = Props.create(AuditLogsActor.class, () -> new AuditLogsActor(mockAuditStorage));
        TestActorRef<AdAccountsActor> testAdActorRef = TestActorRef.create(system, adProps);
        TestActorRef<AdAccountStorageActor> testStorageActorRef = TestActorRef.create(system, storageProps);
        TestActorRef<AuditLogsActor> testAuditActorRef = TestActorRef.create(system, auditProps);
        //then
        AdMicroservice target = new AdMicroservice(system, testAdActorRef, testStorageActorRef, testAuditActorRef);
        Http.RequestBuilder request = Helpers.fakeRequest(routes.AdMicroservice.deleteAccount()).method("DELETE").bodyJson(body);
        int actual = Helpers.invokeWithContext(request, target::deleteAccount).get(2000).status();

        //assert
        assertEquals(expected, actual);
    }

    @Test
    public void testDeleteAccount_InternalError_500() throws Exception {
        //expected
        int expected = INTERNAL_SERVER_ERROR;

        //given
        JsonNode body = Json.parse("{\"firstName\":\"firstName1\",\"lastName\":\"lastName1\",\"id\":\"login1\",\"email\":\"email1@domain.com\"}");

        //when
        WindowsAccountAccessManager mockAccessManager = mock(WindowsAccountAccessManager.class);
        doThrow(new NullPointerException()).when(mockAccessManager).revoke(new WindowsAccountAccess("login1", "firstName1", "lastName1", "email1@domain.com"));
        AccountStorage mockAccountStorage = mock(AccountStorage.class);
        AuditLogStorage mockAuditStorage = mock(AuditLogStorage.class);

        ActorSystem system = ActorSystem.create("system");
        Props adProps = Props.create(AdAccountsActor.class, () -> new AdAccountsActor(mockAccessManager));
        Props storageProps = Props.create(AdAccountStorageActor.class, () -> new AdAccountStorageActor(mockAccountStorage));
        Props auditProps = Props.create(AuditLogsActor.class, () -> new AuditLogsActor(mockAuditStorage));
        TestActorRef<AdAccountsActor> testAdActorRef = TestActorRef.create(system, adProps);
        TestActorRef<AdAccountStorageActor> testStorageActorRef = TestActorRef.create(system, storageProps);
        TestActorRef<AuditLogsActor> testAuditActorRef = TestActorRef.create(system, auditProps);
        //then
        AdMicroservice target = new AdMicroservice(system, testAdActorRef, testStorageActorRef, testAuditActorRef);
        Http.RequestBuilder request = Helpers.fakeRequest(routes.AdMicroservice.deleteAccount()).method("DELETE").bodyJson(body);
        int actual = Helpers.invokeWithContext(request, target::deleteAccount).get(2000).status();

        //assert
        assertEquals(expected, actual);
    }

    @Test
    public void testAddAccount_Optimistic() throws Exception {
        //expected
        int expected = OK;

        //given
        JsonNode body = Json.parse("{\"firstName\":\"firstName1\",\"lastName\":\"lastName1\",\"id\":\"login1\",\"email\":\"email1@domain.com\"}");

        //when
        WindowsAccountAccessManager mockAccessManager = mock(WindowsAccountAccessManager.class);
        AccountStorage mockAccountStorage = mock(AccountStorage.class);
        AuditLogStorage mockAuditStorage = mock(AuditLogStorage.class);

        ActorSystem system = ActorSystem.create("system");
        Props adProps = Props.create(AdAccountsActor.class, () -> new AdAccountsActor(mockAccessManager));
        Props storageProps = Props.create(AdAccountStorageActor.class, () -> new AdAccountStorageActor(mockAccountStorage));
        Props auditProps = Props.create(AuditLogsActor.class, () -> new AuditLogsActor(mockAuditStorage));
        TestActorRef<AdAccountsActor> testAdActorRef = TestActorRef.create(system, adProps);
        TestActorRef<AdAccountStorageActor> testStorageActorRef = TestActorRef.create(system, storageProps);
        TestActorRef<AuditLogsActor> testAuditActorRef = TestActorRef.create(system, auditProps);
        //then
        AdMicroservice target = new AdMicroservice(system, testAdActorRef, testStorageActorRef, testAuditActorRef);
        Http.RequestBuilder request = Helpers.fakeRequest(routes.AdMicroservice.addAccount()).method("POST").bodyJson(body);
        int actual = Helpers.invokeWithContext(request, target::addAccount).get(2000).status();

        //assert
        assertEquals(expected, actual);
    }

    @Test
    public void testAddAccount_InternalError_AD_500() throws Exception {
        //expected
        int expected = INTERNAL_SERVER_ERROR;

        //given
        JsonNode body = Json.parse("{\"firstName\":\"firstName1\",\"lastName\":\"lastName1\",\"id\":\"login1\",\"email\":\"email1@domain.com\"}");

        //when
        WindowsAccountAccessManager mockAccessManager = mock(WindowsAccountAccessManager.class);
        doThrow(new NullPointerException()).when(mockAccessManager).grant(new WindowsAccountAccess("login1", "firstName1", "lastName1", "email1@domain.com"));
        AccountStorage mockAccountStorage = mock(AccountStorage.class);
        AuditLogStorage mockAuditStorage = mock(AuditLogStorage.class);

        ActorSystem system = ActorSystem.create("system");
        Props adProps = Props.create(AdAccountsActor.class, () -> new AdAccountsActor(mockAccessManager));
        Props storageProps = Props.create(AdAccountStorageActor.class, () -> new AdAccountStorageActor(mockAccountStorage));
        Props auditProps = Props.create(AuditLogsActor.class, () -> new AuditLogsActor(mockAuditStorage));
        TestActorRef<AdAccountsActor> testAdActorRef = TestActorRef.create(system, adProps);
        TestActorRef<AdAccountStorageActor> testStorageActorRef = TestActorRef.create(system, storageProps);
        TestActorRef<AuditLogsActor> testAuditActorRef = TestActorRef.create(system, auditProps);
        //then
        AdMicroservice target = new AdMicroservice(system, testAdActorRef, testStorageActorRef, testAuditActorRef);

        Http.RequestBuilder request = Helpers.fakeRequest(routes.AdMicroservice.addAccount()).method("POST").bodyJson(body);
        int actual = Helpers.invokeWithContext(request, target::addAccount).get(2000).status();

        //assert
        assertEquals(expected, actual);
    }

    @Test
    public void testAddAccount_InternalError_DB_500() throws Exception {
        //expected
        int expected = INTERNAL_SERVER_ERROR;

        //given
        JsonNode body = Json.parse("{\"firstName\":\"firstName1\",\"lastName\":\"lastName1\",\"id\":\"login1\",\"email\":\"email1@domain.com\"}");

        //when
        WindowsAccountAccessManager mockAccessManager = mock(WindowsAccountAccessManager.class);
        AccountStorage mockAccountStorage = mock(AccountStorage.class);
        doThrow(new NullPointerException()).when(mockAccountStorage).saveAccount(new AccountEntry("login1", "firstName1", "lastName1", "email1@domain.com"));
        AuditLogStorage mockAuditStorage = mock(AuditLogStorage.class);

        ActorSystem system = ActorSystem.create("system");
        Props adProps = Props.create(AdAccountsActor.class, () -> new AdAccountsActor(mockAccessManager));
        Props storageProps = Props.create(AdAccountStorageActor.class, () -> new AdAccountStorageActor(mockAccountStorage));
        Props auditProps = Props.create(AuditLogsActor.class, () -> new AuditLogsActor(mockAuditStorage));
        TestActorRef<AdAccountsActor> testAdActorRef = TestActorRef.create(system, adProps);
        TestActorRef<AdAccountStorageActor> testStorageActorRef = TestActorRef.create(system, storageProps);
        TestActorRef<AuditLogsActor> testAuditActorRef = TestActorRef.create(system, auditProps);
        //then
        AdMicroservice target = new AdMicroservice(system, testAdActorRef, testStorageActorRef, testAuditActorRef);

        Http.RequestBuilder request = Helpers.fakeRequest(routes.AdMicroservice.addAccount()).method("POST").bodyJson(body);
        int actual = Helpers.invokeWithContext(request, target::addAccount).get(2000).status();

        //assert
        assertEquals(expected, actual);
    }



}

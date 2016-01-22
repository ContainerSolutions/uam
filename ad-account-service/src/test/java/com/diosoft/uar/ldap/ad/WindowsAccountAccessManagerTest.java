package com.diosoft.uar.ldap.ad;

import com.unboundid.ldap.sdk.*;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class WindowsAccountAccessManagerTest {

    @Test
    public void testGrant_Optimistic() throws Exception {
        //expected
        Entry expectedEntry = new Entry("CN=Name Surname,OU=DOMAINUSERS,DC=domain,DC=com");
        expectedEntry.setAttribute("objectClass","user");
        expectedEntry.setAttribute("cn", "Name Surname");

        expectedEntry.setAttribute("sAMAccountName", "testuser");
        expectedEntry.setAttribute("uid", "testuser");

        expectedEntry.setAttribute("givenName", "Name");
        expectedEntry.setAttribute("sn", "Surname");
        expectedEntry.setAttribute("name", "Name Surname");
        expectedEntry.setAttribute("displayName", "Name Surname");
        expectedEntry.setAttribute("userPrincipalName","nsurname@domain.com");
        expectedEntry.setAttribute("mail","nsurname@domain.com");

        expectedEntry.setAttribute("userAccountControl",Integer.toString(0x0200 + 0x0020 + 0x800000+ 0x0002));


        List<Modification> expectedModifications = new ArrayList<>();
        expectedModifications.add(new Modification(ModificationType.REPLACE, "unicodePwd", "\"password\"".getBytes("UTF-16LE")));
        expectedModifications.add(new Modification(ModificationType.REPLACE, "userAccountControl", Integer.toString(0x0200 + 0x800000)));

        //given
        WindowsAccountAccess access = new WindowsAccountAccess("testuser", "Name", "Surname", "nsurname@domain.com");
        LDAPInterface mockLdapConnection = Mockito.mock(LDAPInterface.class);
        //when

        //then
        WindowsAccountAccessManager target = new WindowsAccountAccessManager(mockLdapConnection, "OU=DOMAINUSERS,DC=domain,DC=com", "password");
        target.grant(access);

        //assert
        Mockito.verify(mockLdapConnection).add(expectedEntry);
        Mockito.verify(mockLdapConnection).modify("CN=Name Surname,OU=DOMAINUSERS,DC=domain,DC=com", expectedModifications);

    }

    @Test
    public void testRevoke_Optimistic() throws Exception {
        //expected
        String expectedDN = "CN=Name Surname,OU=DOMAINUSERS,DC=domain,DC=com";

        //given
        WindowsAccountAccess access = new WindowsAccountAccess("testuser", "Name", "Surname", "nsurname@domain.com");
        LDAPInterface mockLdapConnection = Mockito.mock(LDAPInterface.class);
        //when

        //then
        WindowsAccountAccessManager target = new WindowsAccountAccessManager(mockLdapConnection, "OU=DOMAINUSERS,DC=domain,DC=com", "password");
        target.revoke(access);

        //assert
        Mockito.verify(mockLdapConnection).delete(expectedDN);

    }

    @Test
    public void testList_Optimistic() throws Exception {
        //expected
        Collection<WindowsAccountAccess> expected = new ArrayList<>();
        expected.add(new WindowsAccountAccess("nsurname", "Name", "Surname", "nsurname@domain.com"));

        //given
        WindowsAccountAccessFilter filter = new WindowsAccountAccessFilter("nsurname");
        LDAPInterface mockLdapConnection = Mockito.mock(LDAPInterface.class);

        //when
        List<SearchResultEntry> resultEntries = new ArrayList<>();
        resultEntries.add(new SearchResultEntry(new Entry("CN=Name Surname,OU=DOMAINUSERS,DC=domain,DC=com",
                new Attribute("sAMAccountName", "nsurname"),
                new Attribute("mail", "nsurname@domain.com"),
                new Attribute("name", "Name Surname"),
                new Attribute("givenName", "Name"),
                new Attribute("sn", "Surname"))));
        SearchResult searchResult = new SearchResult(0,null,null,null,null,resultEntries,null,1,0,null);
        Mockito.when(mockLdapConnection.search(Mockito.any(ReadOnlySearchRequest.class))).thenReturn(searchResult);

        //then
        WindowsAccountAccessManager target = new WindowsAccountAccessManager(mockLdapConnection, "OU=DOMAINUSERS,DC=domain,DC=com", "password");
        Collection<WindowsAccountAccess> actual = target.list(filter);

        //assert
        Assert.assertEquals(expected, actual);

    }

    @Test
    public void testList_EmptyResult() throws Exception {
        //expected
        Collection<WindowsAccountAccess> expected = new ArrayList<>();

        //given
        WindowsAccountAccessFilter filter = new WindowsAccountAccessFilter("nsurname");

        //when
        LDAPInterface mockLdapConnection = Mockito.mock(LDAPInterface.class);
        SearchResult searchResult = new SearchResult(0,null,null,null,null,null,null,0,0,null);
        Mockito.when(mockLdapConnection.search(Mockito.any(ReadOnlySearchRequest.class))).thenReturn(searchResult);

        //then
        WindowsAccountAccessManager target = new WindowsAccountAccessManager(mockLdapConnection, "OU=DOMAINUSERS,DC=domain,DC=com", "password");
        Collection<WindowsAccountAccess> actual = target.list(filter);

        //assert
        Assert.assertEquals(expected, actual);

    }
}
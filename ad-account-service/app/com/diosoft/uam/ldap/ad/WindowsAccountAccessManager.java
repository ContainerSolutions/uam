package com.diosoft.uam.ldap.ad;

import com.diosoft.uam.AccessManager;
import com.diosoft.uam.AccessManagerException;
import com.unboundid.ldap.sdk.*;
import play.Logger;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class WindowsAccountAccessManager implements AccessManager<WindowsAccountAccess, WindowsAccountAccessFilter> {

    private static final Logger.ALogger LOG = Logger.of(WindowsAccountAccessManager.class);

    public static final String ATTR_ID = "sAMAccountName";
    public static final String ATTR_FIRST_NAME = "givenName";
    public static final String ATTR_LAST_NAME = "sn";
    public static final String ATTR_CN = "cn";
    public static final String ATTR_UID = "uid";
    public static final String ATTR_NAME = "name";
    public static final String ATTR_DISPLAY_NAME = "displayName";
    public static final String ATTR_USER_PRINCIPAL_NAME = "userPrincipalName";
    public static final String ATTR_MAIL = "mail";
    public static final String ATTR_USER_ACCOUNT_CONTROL = "userAccountControl";

    //some useful constants from lmaccess.h
    public static final int UF_ACCOUNTDISABLE = 0x0002;
    public static final int UF_PASSWD_NOTREQD = 0x0020;
    public static final int UF_NORMAL_ACCOUNT = 0x0200;
    public static final int UF_PASSWORD_EXPIRED = 0x800000;

    private LDAPInterface ldapConnection;
    private String domainAccountsRoot;
    private byte[] defaultPassword;

    public WindowsAccountAccessManager(LDAPInterface ldapConnection, String domainAccountsRoot, String defaultPassword) throws AccessManagerException {
        this.ldapConnection = ldapConnection;
        this.domainAccountsRoot = domainAccountsRoot;
        try {
            this.defaultPassword = ('"' + defaultPassword + '"').getBytes("UTF-16LE");
        } catch (UnsupportedEncodingException e) {
            LOG.error("Can't init default password value", e);
            throw new AccessManagerException("Can't init default password value", e);
        }
    }

    public void grant(WindowsAccountAccess access) throws AccessManagerException {
        Entry newAccountEntry = toLdapEntry(access, domainAccountsRoot);

        try {
            ldapConnection.add(newAccountEntry);
            LOG.debug("Account [" + newAccountEntry.getDN() + "] was added successfully");
        } catch (LDAPException e) {
            LOG.error("Exception occurred while account [" + newAccountEntry.getDN() + "] adding", e);
            throw new AccessManagerException("Can't create AD account", e);
        }

        try {
            List<Modification> modifications = new ArrayList<>();
            //Password must be both Unicode and a quoted string
            modifications.add(new Modification(ModificationType.REPLACE, "unicodePwd", defaultPassword));
            modifications.add(new Modification(ModificationType.REPLACE, "userAccountControl", Integer.toString(UF_NORMAL_ACCOUNT + UF_PASSWORD_EXPIRED)));

            ldapConnection.modify(newAccountEntry.getDN(), modifications);
            LOG.debug("Account's [" + newAccountEntry.getDN() + "] password was set successfully");
        } catch (LDAPException e) {
            LOG.error("Exception occurred while enabling account [" + newAccountEntry.getDN() + "]", e);
            try {
                revoke(access);
            } catch (AccessManagerException rollbackException) {
                LOG.warn("Exception occurred while reverting account [" + newAccountEntry.getDN() + "] adding", e);
            }
            throw new AccessManagerException("Can't create AD account", e);
        }
        LOG.info("Account [" + newAccountEntry.getDN() + "] was created successfully");
    }

    public void revoke(WindowsAccountAccess access) throws AccessManagerException {
        String userDN = "CN=" + access.getCnValue() + "," + domainAccountsRoot;
        try {
            ldapConnection.delete(userDN);
            LOG.info("Account [" + userDN + "] was deleted successfully");
        } catch (LDAPException e) {
            LOG.error("Exception occurred while account [" + userDN+ "] deletion", e);
            throw new AccessManagerException("Can't delete AD account", e);
        }
    }

    public Collection<WindowsAccountAccess> list(WindowsAccountAccessFilter filter) throws AccessManagerException {
        Collection<WindowsAccountAccess> result = new ArrayList<>();
        try {
            LOG.debug("AD Search String: " + filter.getSearchString());
            ReadOnlySearchRequest roSearchRequest = new SearchRequest(
                    domainAccountsRoot,
                    SearchScope.SUB,
                    filter.getSearchString());
            SearchResult searchResult = ldapConnection.search(roSearchRequest);
            if(searchResult.getEntryCount() > 0) {
                for (SearchResultEntry entry : searchResult.getSearchEntries()) {
                    WindowsAccountAccess access = fromLdapEntry(entry);
                    result.add(access);
                }
            }
            LOG.info("Found " + searchResult.getEntryCount() + " records for " + filter.getId());
        } catch (LDAPException e) {
            LOG.error("Exception occurred while searching for account [" + filter.getId() + "]", e);
            throw new AccessManagerException("Can't list AD accounts", e);
        }
        return result;
    }

    private static Entry toLdapEntry(WindowsAccountAccess access, String domainUsersRoot) {
        Entry newAccountEntry = new  Entry("CN=" + access.getCnValue() + "," + domainUsersRoot);
        newAccountEntry.setAttribute("objectClass","user");
        newAccountEntry.setAttribute(ATTR_CN, access.getCnValue());

        newAccountEntry.setAttribute(ATTR_ID, access.getId());
        newAccountEntry.setAttribute(ATTR_UID, access.getId());

        newAccountEntry.setAttribute(ATTR_FIRST_NAME, access.getFirstName());
        newAccountEntry.setAttribute(ATTR_LAST_NAME, access.getLastName());
        newAccountEntry.setAttribute(ATTR_NAME, access.getFullName());
        newAccountEntry.setAttribute(ATTR_DISPLAY_NAME, access.getFullName());
        newAccountEntry.setAttribute(ATTR_USER_PRINCIPAL_NAME,access.getEmail());
        newAccountEntry.setAttribute(ATTR_MAIL,access.getEmail());

        newAccountEntry.setAttribute(ATTR_USER_ACCOUNT_CONTROL,Integer.toString(UF_NORMAL_ACCOUNT + UF_PASSWD_NOTREQD + UF_PASSWORD_EXPIRED+ UF_ACCOUNTDISABLE));
        return newAccountEntry;
    }

    private static WindowsAccountAccess fromLdapEntry(SearchResultEntry entry) {
        return new WindowsAccountAccess(
                entry.getAttributeValue(ATTR_ID),
                entry.getAttributeValue(ATTR_FIRST_NAME),
                entry.getAttributeValue(ATTR_LAST_NAME),
                entry.getAttributeValue(ATTR_USER_PRINCIPAL_NAME)
        );
    }

}

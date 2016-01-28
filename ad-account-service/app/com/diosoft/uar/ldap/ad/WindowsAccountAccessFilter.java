package com.diosoft.uar.ldap.ad;

import com.diosoft.uar.AccessFilter;

public class WindowsAccountAccessFilter extends AccessFilter {

    private String login;

    public WindowsAccountAccessFilter(String login) {
        this.login = login;
    }

    @Override
    public String getSearchString() {
        return "(&(sAMAccountName=" + login + ")(objectclass=user)(objectcategory=person))";
    }

    public String getLogin() {
        return login;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WindowsAccountAccessFilter that = (WindowsAccountAccessFilter) o;

        return !(login != null ? !login.equals(that.login) : that.login != null);

    }

    @Override
    public int hashCode() {
        return login != null ? login.hashCode() : 0;
    }
}

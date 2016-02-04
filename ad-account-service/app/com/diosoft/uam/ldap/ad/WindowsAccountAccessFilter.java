package com.diosoft.uam.ldap.ad;

import com.diosoft.uam.AccessFilter;

public class WindowsAccountAccessFilter extends AccessFilter {

    private String id;

    public WindowsAccountAccessFilter(String id) {
        this.id = id;
    }

    @Override
    public String getSearchString() {
        return "(&(sAMAccountName=" + id + ")(objectclass=user)(objectcategory=person))";
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WindowsAccountAccessFilter that = (WindowsAccountAccessFilter) o;

        return !(id != null ? !id.equals(that.id) : that.id != null);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}

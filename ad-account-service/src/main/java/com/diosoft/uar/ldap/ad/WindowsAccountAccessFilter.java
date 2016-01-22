package com.diosoft.uar.ldap.ad;

import com.diosoft.uar.AccessFilter;

public class WindowsAccountAccessFilter extends AccessFilter {

    private String accountId;

    public WindowsAccountAccessFilter(String accountId) {
        this.accountId = accountId;
    }

    @Override
    public String getSearchString() {
        return accountId;
    }
}

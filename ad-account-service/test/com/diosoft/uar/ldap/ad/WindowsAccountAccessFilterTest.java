package com.diosoft.uar.ldap.ad;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class WindowsAccountAccessFilterTest {

    @Test
    public void testGetSearchString_AllUser() throws Exception {
        //expected
        String expected = "(&(sAMAccountName=*)(objectclass=user)(objectcategory=person))";

        //given
        String searchParam = "*";

        //then
        WindowsAccountAccessFilter target = new WindowsAccountAccessFilter(searchParam);
        String actual = target.getSearchString();

        //assert
        Assert.assertEquals(expected, actual);

    }

    @Test
    public void testGetSearchString_ByLogin() throws Exception {
        //expected
        String expected = "(&(sAMAccountName=userLogin)(objectclass=user)(objectcategory=person))";

        //given
        String searchParam = "userLogin";

        //then
        WindowsAccountAccessFilter target = new WindowsAccountAccessFilter(searchParam);
        String actual = target.getSearchString();

        //assert
        Assert.assertEquals(expected, actual);

    }
}
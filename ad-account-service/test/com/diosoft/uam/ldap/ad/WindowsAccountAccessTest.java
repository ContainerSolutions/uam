package com.diosoft.uam.ldap.ad;

import org.junit.Assert;
import org.junit.Test;

public class WindowsAccountAccessTest {

    @Test
    public void testGetCn_Optimistic() throws Exception {
        //expected
        String expected = "Name Surname";
        
        //given
        //when
        //then
        WindowsAccountAccess target = new WindowsAccountAccess("Administrator", "Name", "Surname", null);
        String actual = target.getCnValue();
        
        //assert
        Assert.assertEquals(expected, actual);
        
    }

    @Test
    public void testGetCn_NullFirstName() throws Exception {
        //expected
        String expected = "Administrator";
        
        //given
        
        //when
        
        //then
        WindowsAccountAccess target = new WindowsAccountAccess("Administrator", null, "Surname", null);
        String actual = target.getCnValue();
        
        //assert
        Assert.assertEquals(expected, actual);
        
    }

    @Test
    public void testGetCn_NullLastName() throws Exception {
        //expected
        String expected = "Administrator";
        
        //given
        
        //when
        
        //then
        WindowsAccountAccess target = new WindowsAccountAccess("Administrator", "Name", null, null);
        String actual = target.getCnValue();
        
        //assert
        Assert.assertEquals(expected, actual);
        
    }

    @Test
    public void testGetFullName_Optimistic() throws Exception {
        //expected
        String expected = "Name Surname";
        
        //given
        
        //when
        
        //then
        WindowsAccountAccess target = new WindowsAccountAccess("nsurname", "Name", "Surname", null);
        String actual = target.getFullName();
        
        //assert
        Assert.assertEquals(expected, actual);
        
    }

    @Test
    public void testGetFullName_NullFirstName() throws Exception {
        //expected

        //given

        //when

        //then
        WindowsAccountAccess target = new WindowsAccountAccess("nsurname", null, "Surname", null);
        String actual = target.getFullName();

        //assert
        Assert.assertNull(actual);

    }

    @Test
    public void testGetFullName_NullLastName() throws Exception {
        //expected

        //given

        //when

        //then
        WindowsAccountAccess target = new WindowsAccountAccess("nsurname", "Name", null, null);
        String actual = target.getFullName();

        //assert
        Assert.assertNull(actual);

    }

}
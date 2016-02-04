package com.diosoft.uam.ldap.ad;

import com.diosoft.uam.Access;

public class WindowsAccountAccess extends Access {

    public static final String FIELD_FIRST_NAME = "firstName";
    public static final String FIELD_LAST_NAME = "lastName";
    public static final String FIELD_ID = "id";
    public static final String FIELD_EMAIL = "email";

    private final String cnValue;
    private final String firstName;
    private final String lastName;
    private final String fullName;
    private final String email;

    public WindowsAccountAccess(String id, String firstName, String lastName, String email) {
        super(id);
        this.firstName = firstName;
        this.lastName = lastName;
        if(this.firstName == null || this.lastName == null) {
            this.fullName = null;
            this.cnValue = id;
        } else {
            this.fullName = this.firstName + ' ' + this.lastName;
            this.cnValue = this.fullName;
        }
        this.email = email;
    }

    public String getCnValue() {
        return cnValue;
    }

    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public String getFullName() {
        return fullName;
    }
    public String getEmail() {
        return email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        WindowsAccountAccess that = (WindowsAccountAccess) o;

        if (cnValue != null ? !cnValue.equals(that.cnValue) : that.cnValue != null) return false;
        if (firstName != null ? !firstName.equals(that.firstName) : that.firstName != null) return false;
        if (lastName != null ? !lastName.equals(that.lastName) : that.lastName != null) return false;
        return !(email != null ? !email.equals(that.email) : that.email != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (cnValue != null ? cnValue.hashCode() : 0);
        result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "WindowsAccountAccess {" +
                "cnValue='" + cnValue + '\'' +
                ", id='" + getId() + '\'' +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

}

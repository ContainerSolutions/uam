package com.diosoft.uar;


import java.util.Collection;

public interface AccessManager<T extends Access, V extends AccessFilter> {

    void grant(T access) throws AccessManagerException;

    void revoke(T access) throws AccessManagerException;

    Collection<T> list(V filter) throws AccessManagerException;
}

package com.meiren.common;

/**
 * Created by guoluwei on 2017/3/13.
 */
public enum AclTokenEnum {
    FORM("meiren.acl.mbc.backend.form.index"),
    ;

    public final String token;

    AclTokenEnum(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}

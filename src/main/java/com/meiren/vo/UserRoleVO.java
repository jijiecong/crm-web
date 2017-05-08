package com.meiren.vo;

/**
 * Created by zouli on 17/2/17.
 */
public class UserRoleVO {

    private Long id;
    private String userName;
    private String nickname;
    private String roleNames;
    private String roleDescriptions;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getRoleNames() {
        return roleNames;
    }

    public void setRoleNames(String roleNames) {
        this.roleNames = roleNames;
    }

    public String getRoleDescriptions() {
        return roleDescriptions;
    }

    public void setRoleDescriptions(String roleDescriptions) {
        this.roleDescriptions = roleDescriptions;
    }
}

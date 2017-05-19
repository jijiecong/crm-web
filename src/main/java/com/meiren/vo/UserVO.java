
package com.meiren.vo;

public class UserVO {
    private Long id;
    private String userName;
    private Long hierarchyId;
    private String nickname;
    private String email;
    private String password;
    private String mobile;
    private Long businessId;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNickname() {
        return this.nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMobile() {
        return this.mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Long getBusinessId() {
        return this.businessId;
    }

    public void setBusinessId(Long businessId) {
        this.businessId = businessId;
    }

    public Long getHierarchyId() {
        return this.hierarchyId;
    }

    public void setHierarchyId(Long hierarchyId) {
        this.hierarchyId = hierarchyId;
    }

    public String toString() {
        return "UserVO [\t\t"
            + "  id=" + this.id
            + ", userName=" + this.userName
            + ", nickname=" + this.nickname
            + ", email=" + this.email
            + ", password=" + this.password
            + ", mobile=" + this.mobile
            + ", businessId=" + this.businessId
            + "]";
    }

}

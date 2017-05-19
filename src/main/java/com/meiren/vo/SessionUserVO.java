
package com.meiren.vo;

public class SessionUserVO {
    private Long id;
    private String userName;
    private String nickname;
    private Long hierarchyId;
    private Long businessId;
    private Boolean inSide;
    private String uuid;

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
        return "SessionUserVO [\t\t"
            + "  id=" + this.id
            + ", userName=" + this.userName
            + ", nickname=" + this.nickname
            + ", businessId=" + this.businessId
            + "]";
    }

    public Boolean getInSide() {
        return inSide;
    }

    public void setInSide(Boolean inSide) {
        this.inSide = inSide;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}

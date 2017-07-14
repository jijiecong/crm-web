package com.meiren.vo;

/**
 * Created by Jiecong Ji on 2017/5/23.
 */
public class SignedVO {
    /**
     * id.
     */
    private Long id;

    /**
     * user_id.
     */
    private Long userId;

    /**
     * to_user_id.
     */
    private Long toUserId;

    /**
     * is_used.
     */
    private String isUsed;

    private String toUserName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getToUserId() {
        return toUserId;
    }

    public void setToUserId(Long toUserId) {
        this.toUserId = toUserId;
    }

    public String getIsUsed() {
        return isUsed;
    }

    public void setIsUsed(String isUsed) {
        this.isUsed = isUsed;
    }

    public String getToUserName() {
        return toUserName;
    }

    public void setToUserName(String toUserName) {
        this.toUserName = toUserName;
    }
}

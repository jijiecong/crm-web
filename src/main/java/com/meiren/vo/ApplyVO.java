package com.meiren.vo;

import java.io.Serializable;
import java.util.Date;

/**
 * AclApply Object
 *
 * @author zhangwang
 */
public class ApplyVO implements Serializable {
    private static final long serialVersionUID = 1;


    /**
     * id.
     */
    private Long id;

    /**
     * user_id.
     */
    private Long userId;

    /**
     * apply_type.
     */
    private String applyType;

    /**
     * user_name.
     */
    private String userName;

    /**
     * apply_content.
     */
    private String applyContent;

    /**
     * privilege_id.
     */
    private Long wantId;

    /**
     * current_process_order.
     */
    private Integer currentProcessOrder;

    /**
     * approval_state.
     */
    private String approvalState;

    /**
     * apply_time.
     */
    private Date applyTime;

    private String privilegeName;

    private String groupName;

    public Long getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Long businessId) {
        this.businessId = businessId;
    }

    private Long businessId;

    public String getApplyType() {
        return applyType;
    }

    private String wantName;

    public String getWantName() {
        return wantName;
    }

    public void setWantName(String wantName) {
        this.wantName = wantName;
    }

    public void setApplyType(String applyType) {
        this.applyType = applyType;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getApplyContent() {
        return this.applyContent;
    }

    public void setApplyContent(String applyContent) {
        this.applyContent = applyContent;
    }

    public Integer getCurrentProcessOrder() {
        return this.currentProcessOrder;
    }

    public void setCurrentProcessOrder(Integer currentProcessOrder) {
        this.currentProcessOrder = currentProcessOrder;
    }

    public String getApprovalState() {
        return this.approvalState;
    }

    public void setApprovalState(String approvalState) {
        this.approvalState = approvalState;
    }

    public Date getApplyTime() {
        return this.applyTime;
    }

    public void setApplyTime(Date applyTime) {
        this.applyTime = applyTime;
    }

    public String getPrivilegeName() {
        return privilegeName;
    }

    public void setPrivilegeName(String privilegeName) {
        this.privilegeName = privilegeName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Long getWantId() {
        return wantId;
    }

    public void setWantId(Long wantId) {
        this.wantId = wantId;
    }
}

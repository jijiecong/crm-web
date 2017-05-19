package com.meiren.vo;

/**
 * Created by Jiecong Ji on 2017/5/18.
 */
public class PrivilegeProcessVO {
    /**
     * id.
     */
    private Long id;

    /**
     * privilege_id.
     */
    private Long privilegeId;

    /**
     * process_id.
     */
    private Long processId;

    /**
     * approval_condition.
     */
    private String approvalCondition;
    /**
     * highest_level.
     */
    private Long hierarchyId;

    /**
     * approval-level.
     */
    private Integer approvalLevel;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPrivilegeId() {
        return privilegeId;
    }

    public void setPrivilegeId(Long privilegeId) {
        this.privilegeId = privilegeId;
    }

    public Long getProcessId() {
        return processId;
    }

    public void setProcessId(Long processId) {
        this.processId = processId;
    }

    public String getApprovalCondition() {
        return approvalCondition;
    }

    public void setApprovalCondition(String approvalCondition) {
        this.approvalCondition = approvalCondition;
    }

    public Long getHierarchyId() {
        return hierarchyId;
    }

    public void setHierarchyId(Long hierarchyId) {
        this.hierarchyId = hierarchyId;
    }

    public Integer getApprovalLevel() {
        return approvalLevel;
    }

    public void setApprovalLevel(Integer approvalLevel) {
        this.approvalLevel = approvalLevel;
    }
}

package com.meiren.vo;

/**
 * Created by Jiecong Ji on 2017/5/18.
 */
public class ProcessVO {
    /**
     * id.
     */
    private Long id;

    /**
     * name.
     */
    private String name;

    /**
     * approval_level.
     */
    private Integer approvalLevel;
    /**
     * hierarchy_id
     */
    private Long hierarchyId;
    /**
     * describe.
     */
    private String processDescribe;

    private String approvalCondition;

    private Boolean checked;

    public String getApprovalCondition() {
        return approvalCondition;
    }

    public void setApprovalCondition(String approvalCondition) {
        this.approvalCondition = approvalCondition;
    }

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getApprovalLevel() {
        return approvalLevel;
    }

    public void setApprovalLevel(Integer approvalLevel) {
        this.approvalLevel = approvalLevel;
    }

    public Long getHierarchyId() {
        return hierarchyId;
    }

    public void setHierarchyId(Long hierarchyId) {
        this.hierarchyId = hierarchyId;
    }

    public String getProcessDescribe() {
        return processDescribe;
    }

    public void setProcessDescribe(String processDescribe) {
        this.processDescribe = processDescribe;
    }
}

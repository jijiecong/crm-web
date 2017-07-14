package com.meiren.vo;

/**
 * Created by Jiecong Ji on 2017/5/22.
 */
public class RoleVO {
    /**
     * id.
     */
    private Long id;

    /**
     * name.
     */
    private String name;

    /**
     * description.
     */
    private String description;

    /**
     * status.
     */
    private String status;


    private Integer riskLevel;

    private Long businessId;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(Integer riskLevel) {
        this.riskLevel = riskLevel;
    }

    public Long getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Long businessId) {
        this.businessId = businessId;
    }
}

package com.meiren.vo;

/**
 * Created by Jiecong Ji on 2017/5/16.
 */
public class GroupVO {
    private static final long serialVersionUID = 1;

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

    /**
     * business_id.
     */
    private Long businessId;


    /**
     * p_name.
     */
    private String pname;

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    /**
     * pid.
     */
    private Long pid;

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

    public Long getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Long businessId) {
        this.businessId = businessId;
    }

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }
}

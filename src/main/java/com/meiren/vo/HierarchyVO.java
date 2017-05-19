package com.meiren.vo;

/**
 * Created by Jiecong Ji on 2017/5/18.
 */
public class HierarchyVO {
    /**
     * id.
     */
    private Long id;

    /**
     * hierarchy_name.
     */
    private String hierarchyName;

    /**
     * hierarchy_value.
     */
    private String hierarchyValue;

    /**
     * sort
     */
    private Integer sort;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHierarchyName() {
        return hierarchyName;
    }

    public void setHierarchyName(String hierarchyName) {
        this.hierarchyName = hierarchyName;
    }

    public String getHierarchyValue() {
        return hierarchyValue;
    }

    public void setHierarchyValue(String hierarchyValue) {
        this.hierarchyValue = hierarchyValue;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }
}

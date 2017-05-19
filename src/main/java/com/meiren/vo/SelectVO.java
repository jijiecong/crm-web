package com.meiren.vo;

/**
 * author:zouli
 * 2017/05/15 10:27
 */
public class SelectVO {
    private Long id;
    private String name;
    private Boolean selected;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}

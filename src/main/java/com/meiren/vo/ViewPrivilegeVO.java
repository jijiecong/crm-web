package com.meiren.vo;

import java.util.List;

/**
 * @author jijc
 * @ClassName: ViewPrivilegeVO
 * @Description: ${todo}
 * @date 2017/9/27 16:07
 */
public class ViewPrivilegeVO {
    private Long id;
    private String name;


    private String label;
    private List<ViewPrivilegeVO> children;

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

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<ViewPrivilegeVO> getChildren() {
        return children;
    }

    public void setChildren(List<ViewPrivilegeVO> children) {
        this.children = children;
    }
}

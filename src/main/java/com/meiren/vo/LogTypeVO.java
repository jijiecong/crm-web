package com.meiren.vo;

import java.io.Serializable;

/**
 * Created by xgang on 2017/3/8.
 */
public class LogTypeVO implements Serializable {
    private String name;

    private String desc;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}

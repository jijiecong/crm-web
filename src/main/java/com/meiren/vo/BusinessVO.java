package com.meiren.vo;

/**
 * Created by Jiecong Ji on 2017/5/18.
 */
public class BusinessVO {
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
     * token.
     */
    private String token;

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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

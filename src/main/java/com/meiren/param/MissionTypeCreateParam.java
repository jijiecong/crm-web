package com.meiren.param;

import java.io.Serializable;

/**
 * Created by admin on 2017/3/2.
 */
public class MissionTypeCreateParam implements Serializable {
    private String typeNameDesc;
    private String typeName;
    private String description;
    private String associateRule;

    public String getTypeNameDesc() {
        return typeNameDesc;
    }

    public void setTypeNameDesc(String typeNameDesc) {
        this.typeNameDesc = typeNameDesc;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAssociateRule() {
        return associateRule;
    }

    public void setAssociateRule(String associateRule) {
        this.associateRule = associateRule;
    }
}

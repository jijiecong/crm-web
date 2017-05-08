package com.meiren.param;

import com.meiren.mission.service.vo.TemplateConfigVO;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by admin on 2017/3/1.
 */
public class TemplateCreateParam implements Serializable {
    private  String templateConfigVO;

    private String templateName;

    private String description;

    private Boolean isValid;

    private String templateContent;

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsValid() {
        return this.isValid;
    }

    public void setIsValid(Boolean isValid) {
        this.isValid = isValid;
    }

    public String getTemplateContent() {
        return templateContent;
    }

    public void setTemplateContent(String templateContent) {
        this.templateContent = templateContent;
    }

    public String getTemplateConfigVO() {
        return templateConfigVO;
    }

    public void setTemplateConfigVO(String templateConfigVO) {
        this.templateConfigVO = templateConfigVO;
    }
}

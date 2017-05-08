package com.meiren.param;

import com.meiren.mission.service.vo.TemplateConfigVO;

import java.io.Serializable;

/**
 * Created by admin on 2017/3/1.
 */
public class TemplateModifyParam implements Serializable {
    private Long id;
    private String templateConfigVO;
    private String templateName;
    private String templateContent;
    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTemplateConfigVO() {
        return templateConfigVO;
    }

    public void setTemplateConfigVO(String templateConfigVO) {
        this.templateConfigVO = templateConfigVO;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getTemplateContent() {
        return templateContent;
    }

    public void setTemplateContent(String templateContent) {
        this.templateContent = templateContent;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

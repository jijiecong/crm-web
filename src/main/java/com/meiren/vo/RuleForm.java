package com.meiren.vo;

import com.meiren.mission.service.vo.FilterRuleVO;

import java.io.Serializable;
import java.util.List;

/**
 * Created by admin on 2017/1/3.
 */
public class RuleForm implements Serializable {
    private String  filterRuleVOList;
    /**
     * 任务规则Id
     */
    private Long id;


    private String ruleName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFilterRuleVOList() {
        return filterRuleVOList;
    }

    public void setFilterRuleVOList(String filterRuleVOList) {
        this.filterRuleVOList = filterRuleVOList;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }
}

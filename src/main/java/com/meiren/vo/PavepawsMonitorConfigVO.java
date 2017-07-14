package com.meiren.vo;

import com.meiren.monitor.utils.StringUtils;

import java.io.Serializable;
import java.util.List;

public class PavepawsMonitorConfigVO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String name;
    private String type;
    private String domain;
    private String method;
    private String router;
    private String resultType;
    private String resultValue;
    private String paramType;
    private String paramValue;
    private String timeType;
    private String timeValue;
    private String notifyType;
    private Integer isUsed;
    private String triggerType;
    private String triggerValue;
    private String separateRegex = "|";
    private List<Long> userIds;

    public List<Long> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<Long> userIds) {
        this.userIds = userIds;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDomain() {
        return this.domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getMethod() {
        return this.method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getRouter() {
        return this.router;
    }

    public void setRouter(String router) {
        this.router = router;
    }

    public String getResultType() {
        return this.resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public String getResultValue() {
        return this.resultValue;
    }

    public void setResultValue(String resultValue) {
        this.resultValue = resultValue;
    }

    public String getParamType() {
        return this.paramType;
    }

    public void setParamType(String paramType) {
        this.paramType = paramType;
    }

    public String getParamValue() {
        return this.paramValue;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }

    public String getTimeType() {
        return this.timeType;
    }

    public void setTimeType(String timeType) {
        this.timeType = timeType;
    }

    public String getTimeValue() {
        return this.timeValue;
    }

    public void setTimeValue(String timeValue) {
        this.timeValue = timeValue;
    }

    public List<String> getDomainList() {
        return StringUtils.StringToList(this.getDomain(), this.separateRegex);
    }

    public void setDomain(List<String> list) {
        this.domain = StringUtils.ListToString(list, this.separateRegex);
    }

    public void setDomain(String[] arr) {
        this.domain = StringUtils.ArrayToString(arr, this.separateRegex);
    }

    public List<String> getParamTypeList() {
        return StringUtils.StringToList(this.getParamType(), this.separateRegex);
    }

    public void setParamType(List<String> list) {
        this.paramType = StringUtils.ListToString(list, this.separateRegex);
    }

    public void setParamType(String[] arr) {
        this.paramType = StringUtils.ArrayToString(arr, this.separateRegex);
    }

    public List<String> getParamValueList() {
        return StringUtils.StringToList(this.getParamValue(), this.separateRegex);
    }

    public void setParamValue(List<String> list) {
        this.paramValue = StringUtils.ListToString(list, this.separateRegex);
    }

    public void setParamValue(String[] arr) {
        this.paramValue = StringUtils.ArrayToString(arr, this.separateRegex);
    }

    public String getNotifyType() {
        return this.notifyType;
    }

    public void setNotifyType(String notifyType) {
        this.notifyType = notifyType;
    }

    public String getTriggerType() {
        return this.triggerType;
    }

    public void setTriggerType(String triggerType) {
        this.triggerType = triggerType;
    }

    public String getTriggerValue() {
        return this.triggerValue;
    }

    public void setTriggerValue(String triggerValue) {
        this.triggerValue = triggerValue;
    }

    public Integer getIsUsed() {
        return this.isUsed;
    }

    public void setIsUsed(Integer isUsed) {
        this.isUsed = isUsed;
    }
}

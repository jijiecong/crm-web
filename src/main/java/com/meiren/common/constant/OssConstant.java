package com.meiren.common.constant;

import org.springframework.beans.factory.annotation.Value;

/**
 * Created by admin on 2017/1/19.
 */
public class OssConstant {
    private String userId;

    private String ossStsUrl;

    private String ossSecretKey;

    private String cdnPath;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOssStsUrl() {
        return ossStsUrl;
    }

    public void setOssStsUrl(String ossStsUrl) {
        this.ossStsUrl = ossStsUrl;
    }

    public String getOssSecretKey() {
        return ossSecretKey;
    }

    public void setOssSecretKey(String ossSecretKey) {
        this.ossSecretKey = ossSecretKey;
    }

    public String getCdnPath() {
        return cdnPath;
    }

    public void setCdnPath(String cdnPath) {
        this.cdnPath = cdnPath;
    }
}

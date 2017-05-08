package com.meiren.param;

import java.io.Serializable;

/**
 * Created by admin on 2017/3/14.
 */
public class ApplyTokenParam implements Serializable {
    private String applyText;
    private String tokeName;

    public String getApplyText() {
        return applyText;
    }

    public void setApplyText(String applyText) {
        this.applyText = applyText;
    }

    public String getTokeName() {
        return tokeName;
    }

    public void setTokeName(String tokeName) {
        this.tokeName = tokeName;
    }
}

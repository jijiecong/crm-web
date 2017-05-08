package com.meiren.form.web.interceptor;

import java.io.Serializable;

/**
 * Created by admin on 2017/3/17.
 * 异常错误代码
 */
public class ExceptionResult implements Serializable {
    private Exception exception;
    private String url;

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

package com.meiren.common.result;

/**
 * author:zouli
 * 2017/05/15 09:34
 */
public enum VueResultCode {
    SUCCESS(200, "sucessful"),
    ERROR(500, "exception occur:%s"),
    API_NOT_FIND(404, "登录超时，请刷新页面！"),
    UNLOGIN(555, "UNLOGIN"),
    PRIVILEGE_REQUIRED(300, "privilege required"),
    NO_PASSWORD(11001, "密码不能为空"),;


    private int code;
    private String message;

    private VueResultCode(int code, String msg) {
        this.code = code;
        this.message = msg;
    }

    public int getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }

    public boolean isSuccess() {
        return this.code == 200;
    }

    public static VueResultCode valueOf(int code) {
        VueResultCode[] arr$ = values();

        for (VueResultCode value : arr$) {
            if (code == value.code) {
                return value;
            }
        }
        return SUCCESS;
    }
}

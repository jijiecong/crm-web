package com.meiren.common.result;

public class VueResult extends ApiResult {
    public VueResult() {
        super();
        this.setData(true);
    }

    public VueResult(Object object) {
        super();
        this.setData(object);
    }

    public VueResult setResultCode(VueResultCode resultCode) {
        this.setError(resultCode.getCode(), resultCode.getMessage());
        return this;
    }
}

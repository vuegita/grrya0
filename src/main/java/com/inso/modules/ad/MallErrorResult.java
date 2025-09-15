package com.inso.modules.ad;

import com.inso.framework.bean.ErrorResult;

public enum MallErrorResult implements ErrorResult {

    MERCHANT_NOT_OPEN(
            50021,
            "Merchant not opened",
            "",
            ""
    ),

    NOT_EXIST_BUYER_ADDRESS(
            50022,
            "not exist buyer address",
            "",
            ""
    ),

    ;
    private int code;
    private String msg;
    private transient String spError;
    private transient String ydError;

    @Override
    public String getSPError() {
        return spError;
    }

    @Override
    public String getYDError() {
        return ydError;
    }


    private MallErrorResult(int code, String msg, String spError, String ydError )
    {
        this.code = code;
        this.msg = msg;
        this.spError = spError;
        this.ydError = ydError;
    }
    @Override
    public String getError() {
        return msg;
    }
    public void setMsg(String msg){
        this.msg=msg;
    }
    @Override
    public int getCode() {
        return code;
    }
}

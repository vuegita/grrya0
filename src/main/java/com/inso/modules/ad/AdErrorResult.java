package com.inso.modules.ad;

import com.inso.framework.bean.ErrorResult;

public enum  AdErrorResult implements ErrorResult {

    ERR_LIMIT_TOTAL_MONEY_OF_DAY(
            50001,
            "Limit total money of day !",
            "",
            ""
    ),

    ERR_LIMIT_FREE_MONEY_OF_DAY(
            50002,
            "Today’s free quota has been used up. you can get extra quota if you successfully invite friends and purchase VIP !",
            "",
            ""
    ),

    ERR_LIMIT_MAX_MONEY_OF_SINGLE(
            50003,
            "Limit max money of single !",
            "",
            ""
    ),

    ERR_EXISTS_ORDER_RECORD(
            50005,
            "The record already exists !",
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


    private AdErrorResult(int code, String msg,String spError,String ydError )
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

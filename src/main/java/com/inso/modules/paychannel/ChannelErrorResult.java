package com.inso.modules.paychannel;

import com.inso.framework.bean.ErrorResult;

public enum ChannelErrorResult implements ErrorResult {

    /**
     * 后台错误 1000开始
     */

    ERR_STATUS(-1001, "err status, delete channel after disable channel!", "xx","xx"),
    ERR_CANNEL(-1002, "err channel, channel is maintanence!", "xx","xx"),

    ERR_CANNEL_UNSUPPORT(-1003, "unsupport!", "xx","xx"),
    ;
    private int code;

    /*** 默认英语-错误信息 ***/
    private String error;

    /*** 西班牙语错误信息 ***/
    private transient String spError;

    private String ydError;

    @Override
    public String getSPError() {
        return spError;
    }

    @Override
    public String getYDError() {
        return null;
    }

    public String getError() {
        return error;
    }

    public int getCode() {
        return code;
    }

    private ChannelErrorResult(int code, String error, String spError,String ydError) {
        this.code = code;
        this.error = error;
        this.spError = spError;
        this.ydError = ydError;
    }
}

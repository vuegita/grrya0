package com.inso.modules.coin;

import com.inso.framework.bean.ErrorResult;

public enum CoinErrorResult implements ErrorResult {

    /**
     * 后台错误 1000开始
     */

    INVALID_ADDRESS(60001, "Invalid address!","",""),

    LACK_OF_ENERGY (60002, "Not enough balance for trigger account!","",""),

    NATIVE_TOKEN_INSUFFICIENT_BALANCE(60003, "Insufficient balance of native tokens!","",""),

    ERR_SETTLE_PROJECT_OR_PLATFORM_PAYMENT_CONFIG_STATUS(60004, "err settle project or platform payment status or address!","",""),

    ERR_SETTLE_PROJECT_OR_PLATFORM_PAYMENT_CONFIG_RATE(60005, "err settle project or platform payment rate or not enough balance !","",""),

    ERR_SETTLE_APPROVE(60006, "触发最低划转余额限制或分配比例配置异常 !","",""),
    ;
    private int code;
    private String error;
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

    public String getError() {
        return error;
    }

    public int getCode() {
        return code;
    }

    private CoinErrorResult(int code, String error, String spError, String ydError) {
        this.code = code;
        this.error = error;
        this.spError = spError;
        this.ydError = ydError;
    }
}

package com.inso.modules.admin;

import com.inso.framework.bean.ErrorResult;

public enum AdminErrorResult implements ErrorResult {

    /**
     * 后台错误 1000开始
     */

    NONE_VERCODE(-1001, "Image code is not be empty!","xx","xx"),
    ERR_VERCODE(-1002, "Image code is error!","xx","xx"),
    NONE_ACCOUNT(-1003, "Account is not be empty!","xx","xx"),
    ERR_ACCOUNT(-1004, "Account is error!","xx","xx"),
    NONE_PASSWORD(-1005, "Password is not be empty!","xx","xx"),
    ERR_PASSWORD(-1006, "Password is error!","xx","xx"),

    PRESENCE_ADMIN(-10007, "存在该角色的管理员,请先删除再重新操作","xx","xx"),
    ERR_NULL_GOOGLE_CODE(-10008, "Secret is not be empty!","xx","xx"),
    ERR_ERROR_GOOGLE_CODE(-10009, "Secret is error!","xx","xx"),
    ERR_ERROR_GOOGLE_KEY(-10010, "You don't set secret, please contact admin people!","xx","xx"), // 暂未设置谷歌身份，请联系管理员
    USER_FREEZE(-10011, "Account disabled!","xx","xx"), // 您的账户已冻结,请联系客服
    ERR_IPERROR(-10012, "verify code is error!","xx","xx"), // 验证码输入错误
    ERR_IPEXIST(-10013, "Has Add!","xx","xx"),
    ERR_IPANALYZEERROR(-10014, "parsed error!","xx","xx"), // 解析出错
    ERR_PWDERRORTHERRTIMES(-10015, "Input error password for three times and forbidden login!","xx","xx"), // 输入密码错误三次禁止登录，请联系管理员
    ERR_ACCOUNTLENGTH(-10017, "Account length at least 8 !","xx","xx"), // 账号长度至少八位
    ERR_ACCOUNCONTAINERRORINFO(-10018, "账号带有敏感词汇admin、root、 manage相关","xx","xx"),
    ERR_ACCOUNTERRORTHERRTIMES(-10019, "Input password error to max limit! and forbidden login!","xx","xx"), // 该账户输入密码错误次数已达到上限，五分钟内禁止登录
    ERR_NOFOUNDERRORIP(-10020, "Not Found forbidden ip!","xx","xx"), // 找不到该禁封ip
    ACCOUNT_EXISTS(-10022, "Account is exists","xx","xx"),
    ERR_NAME_IS_EXISTS(-10023, "Name is exists","xx","xx"),
    ERR_NOTIFY_NUM_LIMIT_MINUTE(-10027, "Maximum 10 notifications per minute","xx","xx"),
    ERR_VPA_ADDRESS_EXISTS(-10028, "Address already exists","xx","xx"),
    ERR_BANK_CARD_ENABLE_ONE(-10029, "Only one card can be enabled ","xx","xx"),
    ERR_VPA_ENABLE_ONE(-10030, "Only one vpa can be enabled ","xx","xx"),
    ORDER_STATUS_ERROR(-10031, "Abnormal order status","xx","xx"),
    ERR_NOTIFY_NUM_LIMIT_DAY(-10032, "Maximum 200 notifications per day","xx","xx"),
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

    private AdminErrorResult(int code, String error,String spError,String ydError) {
        this.code = code;
        this.error = error;
        this.spError = spError;
        this.ydError = ydError;
    }
}

package com.inso.modules.passport.config;

import com.inso.modules.passport.user.model.MemberSubType;

/**
 * 打码量配置
 */
public enum CodeAmountConfig {

    USER_RECHARGE("user_recharge", 1), // 用户充值
    FIRST_RECHARGE("first_recharge", 0), // 首充打码
    PLATFORM_PRESENTATION("platform_presentation", 2), // 平台赠送
    RED_PACKAGE_PRESENTATION("red_package_presentation", 2), // 红包赠送
    TASK_PRESENTATION("task_presentation", 2), // 任务赠送
    RETURN_WATER_PRESENTATION("return_water_presentation", 2), // 返佣赠送
    REGISTER_PRESENTATION("register_presentation", 10), // 注册赠送
    ;


    ///////////////////////////////////////////////////////////////////////
    ///////////////////////////---打码量第一版本---///////////////////////////
    ///////////////////////////////////////////////////////////////////////
    /*** 用户充值-1倍 ***/
    public static final String PASSPORT_CODE_AMOUNT_USER_RECHARGE = "passport_code_amount:user_recharge";

    /*** 平台赠送-2倍 ***/
    public static final String PASSPORT_CODE_AMOUNT_SYS_PRESENTATION = "passport_code_amount:sys_presentation";


    ///////////////////////////////////////////////////////////////////////
    ///////////////////////////---打码量第二版本---///////////////////////////
    ///////////////////////////////////////////////////////////////////////
    private String prefixKey;
    private String subkey;

    /*** 普通会员配置-唯一key-会员走这个 ***/
    private String mMemberKey;
    /*** 普通会员配置-子key-会员走这个***/
    private String mMemberSubKey;

    /*** 推广人员走这个 ***/
    private String mPromotionKey;
    private String mPromotionSubKey;

    /*** 默认打码倍数-都统一, 配置自定义 ***/
    private long multiCount;

    private CodeAmountConfig(String subkey, long multiCount)
    {
        this.prefixKey = "passport_code_amount";
        this.subkey = subkey;
        this.multiCount = multiCount;

        // 会员打码配置
        this.mMemberSubKey = MemberSubType.SIMPLE.getKey() + "_" + subkey;
        this.mMemberKey = prefixKey + ":" + mMemberSubKey;

        // 推广人员
        this.mPromotionSubKey = MemberSubType.PROMOTION.getKey() + "_" + subkey ;
        this.mPromotionKey = prefixKey + ":" + mPromotionSubKey;
    }

    public String getKey(MemberSubType subType) {
        if(subType == MemberSubType.SIMPLE)
        {
            return mMemberKey;
        }
        else if(subType == MemberSubType.PROMOTION)
        {
            return mPromotionKey;
        }
        return null;
    }

    public String getSubKey(MemberSubType subType) {
        if(subType == MemberSubType.SIMPLE)
        {
            return mMemberSubKey;
        }
        else if(subType == MemberSubType.PROMOTION)
        {
            return mPromotionSubKey;
        }
        return null;
    }

    public long getMultiCount()
    {
        return multiCount;
    }


}

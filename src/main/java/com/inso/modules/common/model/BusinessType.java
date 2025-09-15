package com.inso.modules.common.model;

public enum BusinessType {

    PLATFORM_RECHARGE(10, "platform_recharge"), // 平台充值
    PLATFORM_PRESENTATION(11, "platform_presentation"), // 平台充值
    PLATFORM_DEDUCT(12, "platform_deduct"), // 平台扣款

    USER_RECHARGE(13, "user_recharge"), // 用户充值
    USER_WITHDRAW(14, "user_withdraw"), // 用户提现
    REFUND(15, "refund"), // 退款订单

    RETURN_WATER(16, "return_water", true), // 返佣
    RETURN_FIRST_RECHARGE_TO_UP(161, "return_first_recharge_to_up", true), // 首充赠送给上级

    USER_FIRST_RECARGE_PRESENTATION(17, "first_recharge_presentation", false, false, false, true), // 首次充值赠送

    // 任务赠送
    FINISH_INVITE_FRIEND_TASK_PRESENTATION(18, "invite friend and finish recharge presentation", true, true), // 完成任务赠送
    REGISTER_PRESENTATION(19, "register_presentation", false, false, false, true), // 注册赠送
    RECHARGE_ACTION_PRESENTATION(20, "recharge_action_presentation", false, false, false, true), // 充值活动赠送
    GAME_BET_RETURN_WATER_2_SELF_PRESENTATION(21, "game_bet_return_water_2_self  presentation", false, true, false, false), // 反水任务赠送
    RECHARGE_PRESENTATION_BY_PERCENT(22, "recharge_presentation_by_percent", false, false, false, true), // 充值就赠送
    USER_BUY_VIP(23, "user_buy_vip"), // 用户购买VIP订单
    RECHARGE_PRESENTATION_PARENTUSER_BY_PERCENT(24, "recharge_presentation_parentuser_by_percent", true, false, false, true), // 用户充值赠送给上级比例
    BUY_VIP_PRESENTATION_PARENTUSER_BY_PERCENT(25, "buy_vip_presentation_parentuser_by_percent",true, true), // 用户购买vip金额赠送给上级比例

    // 自己账户之间划转
    USER_TRANSFER_TO_SELF(26, "user_transfer_to_self"), //

    // 拼团活动赠送
    USER_TEAM_BUY_PRESENT_ORDER(27, "user_team_buy_present_order", true, false, false, true), // 2
    // 活动赠送
    WEB_ACTIVITY_PRESENT_ORDER(28, "web_activity_present_order", true, false, false, true), // 2

    // 游戏配置
    GAME_LOTTERY(30, "game_lottery", false, false, true, false), // 2
//    GAME_LOTTERY(30, "game_lottery", false, false, true), // 2
//    GAME_LOTTERY(30, "game_lottery", false, false, true), // 2
//    GAME_LOTTERY(30, "game_lottery", false, false, true), // 2
    GAME_ANDAR_BAHAR(31, "game_andar_bahar", false, false, true, false), // 2

    GAME_RED_PACKAGE(32, "game_red_package"), // 2


    GAME_FINANCIAL_MANAGE(33, "game_financial_manage", false, false, true, false), //  理财
    GAME_NEW_LOTTERY(34, "game_new_lottery", false, false, true, false), // 2



    // 游戏签到任务赠送
    GAME_TASKCHECKIN(35, "game_taskcheckin", true, true), //  签到

    // 游戏水果机
    GAME_FRUIT(36, "game_fruit", false, false, true, false), // 2
    USER_PROMOTION_PRESENT(37, "user_promotion_present", false, false, false, true), // 用户推广赠送

    GAME_RED_PACKAGE_NO_CODE(38, "game_red_package_no_code"), // 无打码
    GAME_PG_LOTTERY(38, "game_pg_lottery"), // PG

    // 游戏配置
    AD_ORDER(41, "ad_order", false, false, true, false), // 2

    // 流动性挖矿
    COIN_DEFI_MINING_REWARD_ORDER(51, "coin_defi_mining_reward_order", false, false, false, false), // 2
    COIN_CLOUD_MINING_REWARD_ORDER(52, "coin_cloud_mining_order", false, false, false, false), // 2
    COIN_BINANCE_ACTIVITY_MINING_ORDER(53, "coin_binance_activity_mining_order", false, false, false, false), // 2
    ;

    private int code;
    private String key;
    /*** 是否添加到冷钱包里-其实金额已经添加到余额里，只是需要用户自己手动扣除下，目的是让用户感受到自己赚到钱了 ***/
    private boolean addColdAmount = false;
    /*** 就否是任务赠送-走任务赠送打码 ***/
    private boolean taskPresentation = false;
    /*** 是否需要添加到游戏业务统计 ***/
    private boolean addGameBusinessLog = false;
    private boolean isPlatformPresent = false;


    private static final BusinessType[] mArr = BusinessType.values();

    private BusinessType(int code, String key)
    {
        this.code = code;
        this.key = key;
    }

    /**
     * 冷钱包初始化
     * @param code
     * @param key
     * @param isColdAmount
     */
    private BusinessType(int code, String key, boolean isColdAmount)
    {
        this.code = code;
        this.key = key;
        this.addColdAmount = isColdAmount;
    }

    /**
     * 冷钱包 + 任务赠送 初始化
     * @param code
     * @param key
     * @param isColdAmount
     * @param isTaskPresentation
     */
    private BusinessType(int code, String key, boolean isColdAmount, boolean isTaskPresentation)
    {
        this.code = code;
        this.key = key;
        this.addColdAmount = isColdAmount;
        this.taskPresentation = isTaskPresentation;
    }

    private BusinessType(int code, String key, boolean isColdAmount, boolean isTaskPresentation, boolean addGameBusinessLog, boolean isPlatformPresent)
    {
        this.code = code;
        this.key = key;
        this.addColdAmount = isColdAmount;
        this.taskPresentation = isTaskPresentation;
        this.addGameBusinessLog = addGameBusinessLog;
        this.isPlatformPresent = isPlatformPresent;
    }

    public int getCode()
    {
        return code;
    }

    public String getKey()
    {
        return key;
    }

    public boolean isAddColdAmount() {
        return addColdAmount;
    }

    public boolean isPlatformPresent() {
        return isPlatformPresent;
    }

    public boolean isTaskPresentation() {
        return taskPresentation;
    }

    public boolean isAddGameBusinessLog() {
        return addGameBusinessLog;
    }

    public static BusinessType getType(String key)
    {
        BusinessType[] values = mArr;
        for(BusinessType type : values)
        {
            if(type.getKey().equalsIgnoreCase(key))
            {
                return type;
            }
        }
        return null;
    }
}

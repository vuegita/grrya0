package com.inso.modules.game.rocket.config;

public interface RocketConfig {

    /*** 每期最多投注金额 ***/
    public static final String GAME_ROCKET_MAX_MONEY_OF_ISSUE = "game_rocket:max_money_of_issue";

    /*** 每个用户最多投注金额 ***/
    public static final String GAME_ROCKET_MAX_MONEY_OF_USER = "game_rocket:max_money_of_user";

    /*** 开奖模式 ***/
    public static final String GAME_ROCKET_OPEN_MODE = "game_rocket:open_mode";

    /*** 开奖模式 ***/
    public static final String GAME_ROCKET_OPEN_RATE = "game_rocket:open_rate";

    /*** 随机设置最大Crash点 ***/
    public static final String GAME_ROCKET_MAX_CRASH_VALUE = "game_rocket:max_crash_value";

    /*** 0 Crash 点设置  ***/
    public static final String GAME_ROCKET_ZERO_CRASH_VALUE = "game_rocket:zero_crash_value";

    /*** 最大中奖倍数 ***/
    public static final String GAME_ROCKET_MAX_WIN_AMOUNT_MUITIPLE_2_BET_AMOUNT = "game_rocket:max_win_amount_muitiple_2_bet_amount";
}

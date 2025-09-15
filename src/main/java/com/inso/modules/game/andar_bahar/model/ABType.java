package com.inso.modules.game.andar_bahar.model;

import com.inso.modules.game.GameChildType;
import com.inso.modules.game.model.GameCategory;

public enum ABType implements GameChildType {

    PRIMARY("ab_primary", "Primary",11, 1), // 1分钟
//    MIDDLE("ab_middle", "Middle",12, 1), // 1分钟
//    HIGH("ab_high", "High",13,1), // 1分钟
    ;

    private String key;
    private String title;
    private String describe;
    private int code;
    private int stepOfMinutes;
    private int betTimeSecond;
    /*** 封盘秒数 ***/
    private int disableSecond;
    private long disableMillis;

    /*** 倒计时 ***/
    private int countdownSeconds;

    /**
     *
     * @param key
     */
    ABType(String key, String title, int code, int stepOfMinutes)
    {
        this.key = key;
        this.title = title;
        this.describe = stepOfMinutes + " min of issue";
        this.code = code;
        this.stepOfMinutes = stepOfMinutes;
        // 期数时间 - 封盘时间 = 可投注时间
        this.disableSecond = 10;
        this.disableMillis = disableSecond * 1000;
        // 期数时间 - 封盘时间 + 5 = 倒计时
        this.countdownSeconds = (stepOfMinutes - 1) * 60 + (60 - this.disableSecond + 5) ;
        // 可投注时间
        this.betTimeSecond = (stepOfMinutes - 1) * 60 + (60 - disableSecond);
    }

    public String getTitle()
    {
        return title;
    }

    public String getDescribe()
    {
        return describe;
    }

    public String getKey()
    {
        return key;
    }

    public int getCode()
    {
        return code;
    }

    @Override
    public int getTotalSeconds() {
        return 0;
    }

    @Override
    public long getDisableMilliSeconds() {
        return 0;
    }

    @Override
    public GameCategory getCategory() {
        return null;
    }

    @Override
    public boolean verifyBetItem(String betItem) {
        return false;
    }

    @Override
    public boolean verifyBetItem(String[] betItem, boolean from) {
        return false;
    }

    @Override
    public String randomBetItem() {
        return null;
    }

    @Override
    public boolean autoCreateIssue() {
        return true;
    }

    @Override
    public boolean uniqueOpenResult() {
        return false;
    }

    @Override
    public boolean enableBetNumber() {
        return false;
    }

    @Override
    public boolean enableRobotBet() {
        return false;
    }

    @Override
    public boolean autoBoot() {
        return false;
    }

    public int getBetTimeSecond()
    {
        return betTimeSecond;
    }

    public int getDisableSecond() {
        return disableSecond;
    }

    public long getDisableMillis() {
        return disableMillis;
    }


    public int getStepOfMinutes()
    {
        return stepOfMinutes;
    }
    public static ABType getType(String key)
    {
//        ABType[] values = ABType.values();
//        for(ABType type : values)
//        {
//            if(type.getKey().equals(key))
//            {
//                return type;
//            }
//        }
        return ABType.PRIMARY;
    }

    public boolean verifyTime(long beginTime)
    {
        return false;
    }

    public static void main(String[] args) {
//        LotteryType type = LotteryType.FAST;
//        System.out.println(type.getReferencePrice(1));
    }

}

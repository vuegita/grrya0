package com.inso.modules.game.rocket.model;

import com.inso.framework.utils.RandomUtils;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.game.GameChildType;
import com.inso.modules.game.model.GameCategory;
import org.joda.time.DateTime;

public enum RocketType implements GameChildType {

    CRASH("crash", "Crash",11, 18, 26500), // 2分钟
    ;

    public static final RocketType[] mArr = RocketType.values();

    private String key;
    private String title;
    private String describe;
    private String icon;
    private int code;
    private int stepOfSeconds;
    private int betTimeSecond;
    /*** 封盘秒数 ***/
    private int disableSecond;
    private int disableMillis;
    private long referencePrice;


    /**
     *
     * @param key
     */
    RocketType(String key, String title, int code, int stepOfSeconds, long referencePrice)
    {
        this.key = key;
        this.title = title;
        this.describe = stepOfSeconds + " min of issue";
        this.icon = "/static/game/img/rocket-" + stepOfSeconds + "-" + title.toLowerCase();
        this.code = code;
        this.stepOfSeconds = stepOfSeconds;
        this.disableSecond = 2;
        this.disableMillis = disableSecond * 1000;
        this.betTimeSecond = stepOfSeconds - disableSecond;
        this.referencePrice = referencePrice;
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

    public String getIcon()
    {
        return icon;
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
        return GameCategory.ROCKET;
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
        int rs = RandomUtils.nextInt(10);
        if(rs == 0)
        {
            return rs + StringUtils.getEmpty();
        }
        rs = RandomUtils.nextInt(150) + 10;
        return rs + StringUtils.getEmpty();
    }

    @Override
    public boolean autoCreateIssue() {
        return false;
    }

    @Override
    public boolean uniqueOpenResult() {
        return false;
    }

    @Override
    public boolean enableBetNumber() {
        return true;
    }

    @Override
    public boolean enableRobotBet() {
        return true;
    }

    @Override
    public boolean autoBoot() {
        return true;
    }

    public int getBetTimeSecond()
    {
        return betTimeSecond;
    }


    public String getReferencePrice(String openResult)
    {
//        long rs = referencePrice;
//        boolean isAdd = RandomUtils.nextBoolean();
//        long num = RandomUtils.nextInt(50);
//        if(isAdd)
//        {
//            rs += num;
//        }
//        else
//        {
//            rs -= num;
//        }
//        String str = rs + StringUtils.getEmpty();
//        return str.substring(0, str.length() - 1) + openResult;
        return null;
    }


    public int getStepOfSeconds() {
        return stepOfSeconds;
    }

    public static RocketType getType(String key)
    {
        for(RocketType type : mArr)
        {
            if(type.getKey().equals(key))
            {
                return type;
            }
        }
        return null;
    }

    public static void main(String[] args) {
//        LotteryType type = LotteryType.FAST;
//        System.out.println(type.getReferencePrice(1));
    }

    public int getDisableSecond() {
        return disableSecond;
    }

    public long getDisableMillis() {
        return disableMillis;
    }

    public void setDisableSecond(int disableSecond) {
        this.disableSecond = disableSecond;
    }
}

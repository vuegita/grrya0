package com.inso.modules.game.lottery_game_impl.rg2.model;

import com.inso.framework.utils.RandomUtils;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.game.GameChildType;
import com.inso.modules.game.lottery_game_impl.btc_kline.model.BTCKlineBetItemType;
import com.inso.modules.game.model.GameCategory;

public enum RedGreen2Type implements GameChildType {

    PARITY("rg2_parity", "Parity",11, 1, 26500), // 1分钟
    SAPRE("rg2_sapre", "Sapre",12, 3, 13200), // 3分钟
    EMERD("rg2_emerd", "Emerd",14,5, 70300), // 5分钟
    ;

    public static RedGreen2Type[] mArr = RedGreen2Type.values();

    private String key;
    private String title;
    private String describe;
    private String icon;
    private int code;

    private int totalSeconds;
    private int stepOfMinutes;
    private int betTimeSecond;
    /*** 封盘秒数 ***/
    private int disableSecond;
    private int disableMillis;
    private long referencePrice;

    private String rootTitle = "Wingo";


    /**
     *
     * @param key
     */
    RedGreen2Type(String key, String title, int code, int stepOfMinutes, long referencePrice)
    {
        this.key = key;
        this.title = title;
        this.describe = stepOfMinutes + " min of issue";
        this.icon = "/static/game/img/lottery-rg-" + stepOfMinutes + "-" + title.toLowerCase();
        this.code = code;
        this.stepOfMinutes = stepOfMinutes;
        this.totalSeconds = stepOfMinutes * 60;
        this.disableSecond = 15;
        this.disableMillis = disableSecond * 1000;
        this.betTimeSecond = (stepOfMinutes - 1) * 60 + (60 - disableSecond);
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

    public int getTotalSeconds() {
        return totalSeconds;
    }

    @Override
    public long getDisableMilliSeconds() {
        return 0;
    }

    public int getCode()
    {
        return code;
    }

    @Override
    public GameCategory getCategory() {
        return GameCategory.RED_GREEN;
    }

    @Override
    public boolean verifyBetItem(String betItem) {
        if(StringUtils.isEmpty(betItem))
        {
            return false;
        }
        int rs = StringUtils.asInt(betItem);
        return rs >= 0 && rs <= 9;
    }

    @Override
    public boolean verifyBetItem(String[] betItemArr, boolean from) {
        for(String betItem : betItemArr)
        {
            LotteryRgBetItemType betItemType = LotteryRgBetItemType.getType(betItem);
            if(betItemType == null)
            {
                int rs = StringUtils.asInt(betItem, -1);
                if(rs < 0 || rs > 9)
                {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public String randomBetItem() {
        return RandomUtils.nextInt(10) + StringUtils.getEmpty();
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

    public String getRootTitle() {
        return rootTitle;
    }

    public int getBetTimeSecond()
    {
        return betTimeSecond;
    }


    public String getReferencePrice(String openResult)
    {
        long rs = referencePrice;
        boolean isAdd = RandomUtils.nextBoolean();
        long num = RandomUtils.nextInt(50);
        if(isAdd)
        {
            rs += num;
        }
        else
        {
            rs -= num;
        }
        String str = rs + StringUtils.getEmpty();
        return str.substring(0, str.length() - 1) + openResult;
    }


    public int getStepOfMinutes()
    {
        return stepOfMinutes;
    }
    public static RedGreen2Type getType(String key)
    {
        for(RedGreen2Type type : mArr)
        {
            if(type.getKey().equalsIgnoreCase(key))
            {
                return type;
            }
        }
        return null;
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

    public static void main(String[] args) {
        RedGreen2Type type = RedGreen2Type.PARITY;
        System.out.println(type.randomBetItem());
        System.out.println(RandomUtils.nextInt(1));
    }

}

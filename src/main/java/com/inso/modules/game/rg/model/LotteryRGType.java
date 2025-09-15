package com.inso.modules.game.rg.model;

import com.inso.framework.utils.RandomUtils;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.game.GameChildType;
import com.inso.modules.game.lottery_game_impl.rg2.model.LotteryRgBetItemType;
import com.inso.modules.game.model.GameCategory;

public enum LotteryRGType implements GameChildType {

    PARITY("l_rg_parity", "Parity",11, 2, 26500), // 2分钟
    SAPRE("l_rg_sapre", "Sapre",12, 3, 13200), // 3分钟
    BCONE("l_rg_bcone", "Bcone",13,4, 34600), // 4分钟
    EMERD("l_rg_emerd", "Emerd",14,5, 70300), // 5分钟
    ;

    public static LotteryRGType[] mArr = LotteryRGType.values();

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

    private String rootTitle = "WINGO";


    /**
     *
     * @param key
     */
    LotteryRGType(String key, String title, int code, int stepOfMinutes, long referencePrice)
    {
        this.key = key;
        this.title = title;
        this.describe = stepOfMinutes + " min of issue";
        this.icon = "/static/game/img/lottery-rg-" + stepOfMinutes + "-" + title.toLowerCase();
        this.code = code;
        this.stepOfMinutes = stepOfMinutes;
        this.totalSeconds = stepOfMinutes * 60;
        this.disableSecond = 30;
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
        return LotteryRgBetItemType.randomItem();
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
        return false;
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


    public String getReferencePrice(long openResult)
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
    public static LotteryRGType getType(String key)
    {
        for(LotteryRGType type : mArr)
        {
            if(type.getKey().equalsIgnoreCase(key))
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

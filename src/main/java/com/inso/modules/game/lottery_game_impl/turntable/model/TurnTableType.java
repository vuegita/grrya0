package com.inso.modules.game.lottery_game_impl.turntable.model;

import com.inso.framework.utils.RandomUtils;
import com.inso.modules.game.GameChildType;
import com.inso.modules.game.model.GameCategory;

public enum TurnTableType implements GameChildType {

    ROULETTE("roulette", "Roulette",11, 30, 26500), // 2分钟
    ;

    public static final TurnTableType[] mArr = TurnTableType.values();

    private String key;
    private String title;
    private String describe;
    private String icon;
    private int code;
    private int totalSeconds;
    private int betTimeSecond;
    /*** 封盘秒数 ***/
    private int disableSecond;
    private int disableMillis;
    private int refreshMillis;
    private long referencePrice;

    /**
     *
     * @param key
     */
    TurnTableType(String key, String title, int code, int totalSeconds, long referencePrice)
    {
        this.key = key;
        this.title = title;
        this.describe = totalSeconds + " min of issue";
        this.icon = "/static/game/img/turntable-" + totalSeconds + "-" + title.toLowerCase();
        this.code = code;
        this.totalSeconds = totalSeconds;
        this.disableSecond = 5;
        this.disableMillis = disableSecond * 1000;
        this.refreshMillis = 1000;
        this.betTimeSecond = totalSeconds - disableSecond;
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
        return totalSeconds;
    }

    @Override
    public long getDisableMilliSeconds() {
        return disableMillis;
    }

    @Override
    public GameCategory getCategory() {
        return GameCategory.TURNTABLE;
    }

    @Override
    public boolean verifyBetItem(String betItem) {
        return TurntableBetItemType.getType(betItem) != null;
    }

    public boolean verifyBetItem(String[] betItemArr, boolean fromApi)
    {
        for(String tmp : betItemArr)
        {
            if(!verifyBetItem(tmp))
            {
                return false;
            }
        }
        return true;
    }

    public int getBetTimeSecond()
    {
        return betTimeSecond;
    }

    public int getRefreshMillis() {
        return refreshMillis;
    }

    public int getStepOfSeconds() {
        return totalSeconds;
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

    public static TurnTableType getType(String key)
    {
        TurnTableType[] values = TurnTableType.values();
        for(TurnTableType type : values)
        {
            if(type.getKey().equals(key))
            {
                return type;
            }
        }
        return null;
    }

    @Override
    public String randomBetItem() {

        int betItemIndex = 1;
        int betItemRandomIndex = RandomUtils.nextInt(15);
        if(betItemRandomIndex >= 14)
        {
            betItemIndex = 2;
        }
        else
        {
            if(RandomUtils.nextBoolean())
            {
                betItemIndex = 0;
            }
            else
            {
                betItemIndex = 1;
            }
        }
        TurntableBetItemType itemType = TurntableBetItemType.values()[betItemIndex];
        return itemType.getKey();
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
        return true;
    }

    @Override
    public boolean autoBoot() {
        return true;
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

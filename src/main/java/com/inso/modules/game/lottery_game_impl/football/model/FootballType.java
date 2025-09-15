package com.inso.modules.game.lottery_game_impl.football.model;

import com.inso.framework.utils.RandomUtils;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.game.GameChildType;
import com.inso.modules.game.lottery_game_impl.btc_kline.model.BTCKlineBetItemType;
import com.inso.modules.game.model.GameCategory;

public enum FootballType implements GameChildType {

    Football("Football", "Football",1, 60, 25), // 1分钟
    ;

    public static final FootballType[] mArr = FootballType.values();

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

    /**
     *
     * @param key
     */
    FootballType(String key, String title, int code, int totalSeconds, int disableSecond)
    {
        this.key = key;
        this.title = title;
        this.describe = totalSeconds + " min of issue";
        this.icon = "/static/game/img/turntable-" + totalSeconds + "-" + title.toLowerCase();
        this.code = code;
        this.totalSeconds = totalSeconds;
        this.disableSecond = disableSecond;
        this.disableMillis = disableSecond * 1000;
        this.refreshMillis = 1000;
        this.betTimeSecond = totalSeconds - disableSecond;
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
        return GameCategory.FOOTABALL;
    }

    @Override
    public boolean verifyBetItem(String betItem) {
        if(StringUtils.isEmpty(betItem))
        {
            return false;
        }
        int index = StringUtils.asInt(betItem);
        return index >= 1 && index <= 5;
    }

    public boolean verifyBetItem(String[] betItemArr, boolean fromApi)
    {
        int len = betItemArr.length;
        if(len != 1)
        {
            return false;
        }

        String item = betItemArr[0];
        if(fromApi)
        {
            return "0".equalsIgnoreCase(item);
        }
        return verifyBetItem(item);
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
        return null;
    }

    public static FootballType getType(String key)
    {
        for(FootballType type : mArr)
        {
            if(type.getKey().equalsIgnoreCase(key))
            {
                return type;
            }
        }
        return null;
    }

    @Override
    public String randomBetItem() {
        int rs = RandomUtils.nextInt(10) ;
        if(rs <= 0)
        {
            return RandomUtils.nextInt(10) + StringUtils.getEmpty();
        }

        int index = RandomUtils.nextInt(3);
        return BTCKlineBetItemType.mArr[index].getKey();
    }

    @Override
    public boolean autoCreateIssue() {
        return true;
    }

    @Override
    public boolean uniqueOpenResult() {
        return true;
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
        return false;
    }


    public int getDisableSecond() {
        return disableSecond;
    }

    public long getDisableMillis() {
        return disableMillis;
    }

}

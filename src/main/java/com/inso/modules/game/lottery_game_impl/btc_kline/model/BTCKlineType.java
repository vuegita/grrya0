package com.inso.modules.game.lottery_game_impl.btc_kline.model;

import com.inso.framework.utils.RandomUtils;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.game.GameChildType;
import com.inso.modules.game.model.GameCategory;

public enum BTCKlineType implements GameChildType {

    BTC_KLINE_1MIN(1, "btc_kline_1min", "BTC-Spot-1min",1, 60, 25), // 1分钟
//    BTC_KLINE_3MIN(3, "btc_kline_3min", "BTC-Spot-3min",3, 180, 26500), // 1分钟
    BTC_KLINE_5MIN(5, "btc_kline_5min", "BTC-Spot-5min",5, 300, 180), // 1分钟
    ;

    public static final BTCKlineType[] mArr = BTCKlineType.values();

    private int binanceInternal;
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
    BTCKlineType(int binanceInternal, String key, String title, int code, int totalSeconds, int disableSecond)
    {
        this.binanceInternal = binanceInternal;
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

    public int getBinanceInternal() {
        return binanceInternal;
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
        return GameCategory.BTC_KLINE;
    }

    @Override
    public boolean verifyBetItem(String betItem) {
        BTCKlineBetItemType betItemType = BTCKlineBetItemType.getType(betItem);
//        return betItemType == BTCKlineBetItemType.Odd || betItemType == BTCKlineBetItemType.Even;



//        if(BTC_KLINE_1MIN.getKey().equalsIgnoreCase(key))
//        {
//            return betItemType == BTCKlineBetItemType.Odd || betItemType == BTCKlineBetItemType.Even;
//        }
        if(betItemType == null)
        {
            int rs = StringUtils.asInt(betItem, -1);
            return rs >= 0 && rs <= 9;
        }else{
            return betItemType == BTCKlineBetItemType.Odd || betItemType == BTCKlineBetItemType.Even;
        }
       // return true;
    }

    public boolean verifyBetItem(String[] betItemArr, boolean from)
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
        return null;
    }

    public static BTCKlineType getType(String key)
    {
        for(BTCKlineType type : mArr)
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
        return !(BTC_KLINE_1MIN == this);
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


    public int getDisableSecond() {
        return disableSecond;
    }

    public long getDisableMillis() {
        return disableMillis;
    }

}

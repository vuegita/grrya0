package com.inso.modules.game.lottery_game_impl.btc_kline.model;

import com.inso.framework.utils.RandomUtils;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.game.GameBetItemType;


public enum BTCKlineBetItemType implements GameBetItemType {

    Big(MyCommonBetItemType.Big, "Big"),
    Small(MyCommonBetItemType.Small, "Small"),

    Odd(MyCommonBetItemType.Odd, "Odd"), // 单
    Even(MyCommonBetItemType.Even, "Even"),  // 双

//    NUMBER("Number", "Number"),

    ;


    public final static BTCKlineBetItemType[] mArr = BTCKlineBetItemType.values();


    private String key;
    private String name;
    BTCKlineBetItemType(MyCommonBetItemType betItemType, String name)
    {
        this.key = betItemType.getKey();
        this.name = name;
    }

    public String getKey()
    {
        return key;
    }


    public static BTCKlineBetItemType getType(String key)
    {
        BTCKlineBetItemType[] values = mArr;
        for(BTCKlineBetItemType type : values)
        {
            if(type.getKey().equalsIgnoreCase(key))
            {
                return type;
            }
        }
        return null;
    }

    public static boolean verifyBetItem(String key)
    {
        if(StringUtils.isEmpty(key))
        {
            return false;
        }

        BTCKlineBetItemType betItemType = BTCKlineBetItemType.getType(key);
        if(betItemType == null)
        {
            return false;
        }
        return true;
    }

    public static String randomItem(){
        int rs = RandomUtils.nextInt(10) ;
        if(rs <= 0)
        {
            return RandomUtils.nextInt(10) + StringUtils.getEmpty();
        }

        int index = RandomUtils.nextInt(3);
        return mArr[index].getKey();
    }

    public static void main(String[] args) {
    }

}

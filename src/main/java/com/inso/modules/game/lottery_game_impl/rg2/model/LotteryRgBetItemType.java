package com.inso.modules.game.lottery_game_impl.rg2.model;

import com.inso.framework.utils.RandomUtils;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.game.GameBetItemType;


public enum LotteryRgBetItemType {

    RED("Red"),
    GREEN("Green"),
    VIOLET("Violet"),
//    NUMBER("number"),// 这里不体现, number类型是直接数字
    ;

    public static LotteryRgBetItemType[] mArr = LotteryRgBetItemType.values();

    private String key;
    private GameBetItemType.MyCommonBetItemType followBetItem;

    LotteryRgBetItemType(String key)
    {
        this.key = key;
        this.followBetItem = GameBetItemType.MyCommonBetItemType.NUMBER;
    }

    public String getKey()
    {
        return key;
    }

    public GameBetItemType.MyCommonBetItemType getFollowBetItem() {
        return followBetItem;
    }

    public static LotteryRgBetItemType getType(String key)
    {
        for(LotteryRgBetItemType type : mArr)
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

        if(RED.getKey().equals(key))
        {
            return true;
        }

        if(GREEN.getKey().equals(key))
        {
            return true;
        }

        if(VIOLET.getKey().equals(key))
        {
            return true;
        }

        try {
            int number = Integer.parseInt(key);
            if(number >= 0 && number <= 9)
            {
                return true;
            }
        } catch (NumberFormatException e) {
        }

        return false;
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

}

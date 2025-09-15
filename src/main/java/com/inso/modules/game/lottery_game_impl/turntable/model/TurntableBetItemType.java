package com.inso.modules.game.lottery_game_impl.turntable.model;

import com.inso.framework.utils.RandomUtils;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.game.GameBetItemType;


public enum TurntableBetItemType {

    // 2 红宝石
    RED_RUBY("Ruby", GameBetItemType.MyCommonBetItemType.Big, "红宝石"),

    // 2 绿宝石
    GREEN_EMERALD("Emerald", GameBetItemType.MyCommonBetItemType.Small, "绿宝石"),

    // 3. 宝箱
    CHEST("Chest", GameBetItemType.MyCommonBetItemType.NUMBER, "宝箱"),

    ;

    private static int[] mRedRubyArr  = {1, 3, 5, 7, 10, 12, 14};
    
    private static int[] mGreenEmeraldArr = {2, 4, 6, 8, 9, 11, 13, 15};


    public final static TurntableBetItemType[] mBetItemArr = TurntableBetItemType.values();


    private String key;
    private GameBetItemType.MyCommonBetItemType followBetItem;
    private String name;
    TurntableBetItemType(String key, GameBetItemType.MyCommonBetItemType followBetItemType, String name)
    {
        this.key = key;
        this.name = name;
    }

    public String getKey()
    {
        return key;
    }

    public GameBetItemType.MyCommonBetItemType getFollowBetItem() {
        return followBetItem;
    }

    public static TurntableBetItemType getType(String key)
    {
        TurntableBetItemType[] values = mBetItemArr;
        for(TurntableBetItemType type : values)
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

        TurntableBetItemType betItemType = TurntableBetItemType.getType(key);
        if(betItemType == null)
        {
            return false;
        }
        return true;
    }

    public static String randomItem(){
        int rs = RandomUtils.nextInt(15) + 1;
        if(rs >= 15)
        {
            return CHEST.getKey();
        }
        if(rs % 2 == 0)
        {
            return TurntableBetItemType.RED_RUBY.getKey();
        }
        else
        {
            return TurntableBetItemType.GREEN_EMERALD.getKey();
        }
    }

    public static TurntableBetItemType randomItem2(){
        int rs = RandomUtils.nextInt(15) + 1;
        if(rs >= 15)
        {
            return CHEST;
        }
        if(rs % 2 == 0)
        {
            return TurntableBetItemType.RED_RUBY;
        }
        else
        {
            return TurntableBetItemType.GREEN_EMERALD;
        }
    }

    public static long randomOpenIndex(TurntableBetItemType betItemType)
    {
        if(betItemType == null)
        {
            return -1;
        }
        if(betItemType == TurntableBetItemType.CHEST)
        {
            return 0;
        }

        int index = RandomUtils.nextInt(7);
        // 0-16
        if(betItemType == TurntableBetItemType.RED_RUBY)
        {
            return mRedRubyArr[index];
        }
        return mGreenEmeraldArr[index];
    }

    public static boolean verifyOpenResult(TurntableBetItemType betItemType)
    {
        return betItemType != null;
    }

    public static void main(String[] args) {
    }

}

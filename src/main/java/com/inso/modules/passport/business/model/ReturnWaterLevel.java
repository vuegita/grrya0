package com.inso.modules.passport.business.model;

import com.inso.framework.utils.BigDecimalUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public enum ReturnWaterLevel {

//    BRONZE("Bronze", 0,3, 1, 6f, 1f, "青铜"),
//    SILVER("Silver", 0,5, 3, 7f, 2f, "白银"),
//    GOLD("Gold", 0,8, 5, 8f, 3f, "黄金"),
//    DIAMOND ("Diamond", 0,10, 8, 10f, 5f, "钻石"), //
    FREE("Free", 0,0, 0, 1f, 0f, "vip0"),
    BASIC("Basic", 1,0, 0, 3f, 1f, "vip1"),
    BASICTOW("BasicTow", 2,0, 0, 4f, 1f, "vip2"),
    PRO("Pro", 3,0, 0, 6f, 3f, "vip3"),
    PROTOW("ProTow", 4,0, 0, 7f, 3f, "vip4"),
    PREMIUM("Premium", 5,0, 0, 9f, 5f, "vip5"),
    PREMIUMTOW("PremiumTow", 6,0, 0, 10f, 5f, "vip6"),
    ;


    private String key;
    private long vipLevel;
    /*** 邀请多少好友一天 ***/
    private int inviteCountOfDay;
    /*** 购买VIP ****/
    private int buyVipCountOfDay;
    private BigDecimal firstRate;
    private BigDecimal secondRate;
    private String remark;

    ReturnWaterLevel(String key,long vipLevel,int inviteOfDay, int buyVipCountOfDay, float firstRate, float secondRate, String remark)
    {
        this.key = key;
        this.vipLevel = vipLevel;
        this.inviteCountOfDay = inviteOfDay;
        this.buyVipCountOfDay = buyVipCountOfDay;
        this.firstRate = new BigDecimal(firstRate).divide(BigDecimalUtils.DEF_100, 3, RoundingMode.HALF_UP);
        this.secondRate = new BigDecimal(secondRate).divide(BigDecimalUtils.DEF_100, 3, RoundingMode.HALF_UP);
        this.remark = remark;
    }

    public String getKey()
    {
        return key;
    }

    public long getVipLevel() {
        return vipLevel;
    }

    public int getInviteCountOfDay() {
        return inviteCountOfDay;
    }

    public int getBuyVipCountOfDay() {
        return buyVipCountOfDay;
    }

    public BigDecimal getFirstRate() {
        return firstRate;
    }

    public BigDecimal getSecondRate() {
        return secondRate;
    }

    public String getRemark() {
        return remark;
    }

    public static ReturnWaterLevel getType(String key)
    {
        ReturnWaterLevel[] values = ReturnWaterLevel.values();
        for(ReturnWaterLevel type : values)
        {
            if(type.getKey().equalsIgnoreCase(key))
            {
                return type;
            }
        }
        return null;
    }

    public static ReturnWaterLevel getTypeByUserInviteStatus(long vipLevel) {

        long userVipLevel =vipLevel;

        ReturnWaterLevel level = ReturnWaterLevel.FREE;
        if(userVipLevel==level.getVipLevel()){
            return level;
        }

        level = ReturnWaterLevel.BASIC;
        if(userVipLevel==level.getVipLevel()){
            return level;
        }

        level = ReturnWaterLevel.BASICTOW;
        if(userVipLevel==level.getVipLevel()){
            return level;
        }

        level = ReturnWaterLevel.PRO;
        if(userVipLevel==level.getVipLevel()){
            return level;
        }

        level = ReturnWaterLevel.PROTOW;
        if(userVipLevel==level.getVipLevel()){
            return level;
        }

        level = ReturnWaterLevel.PREMIUM;
        if(userVipLevel==level.getVipLevel()){
            return level;
        }

        level = ReturnWaterLevel.PREMIUMTOW;
        if(userVipLevel==level.getVipLevel()){
            return level;
        }

        return  ReturnWaterLevel.FREE;

    }

//        public static ReturnWaterLevel getTypeByUserInviteStatus(long userid)
//    {

       // DateTime dateTime = new DateTime();

        //int regCount = TodayInviteFriendHelper.getTodayRegCount(dateTime, userid);
//        int buyVipCount = TodayInviteFriendHelper.getTodayRegAndBuyVipCount(dateTime, userid);
//
//
//        ReturnWaterLevel level = ReturnWaterLevel.DIAMOND;
//        if(  buyVipCount  >= level.getBuyVipCountOfDay())
//        {
//            return level;
//        }
//
//        level = ReturnWaterLevel.GOLD;
//        if( buyVipCount  >= level.getBuyVipCountOfDay())
//        {
//            return level;
//        }
//
//        level = ReturnWaterLevel.SILVER;
//        if(buyVipCount  >= level.getBuyVipCountOfDay())
//        {
//            return level;
//        }
//        return ReturnWaterLevel.BRONZE;




  //  }


}

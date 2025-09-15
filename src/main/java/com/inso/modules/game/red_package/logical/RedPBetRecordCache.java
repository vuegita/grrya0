//package com.inso.modules.game.red_package.logical;
//
//import com.inso.modules.game.MyLotteryBetRecordCache;
//import com.inso.modules.game.andar_bahar.logical.ABPeriodStatus;
//
///**
// * 当期投注记录
// */
//public class RedPBetRecordCache extends MyLotteryBetRecordCache {
//
//    private static final int DEFAULT_EXPIRES = ABPeriodStatus.EXPIRES / 2;
//
//    private interface MyInternal {
//        public static RedPBetRecordCache mgr = new RedPBetRecordCache();
//    }
//
//    private RedPBetRecordCache()
//    {
//    }
//
//    public static RedPBetRecordCache getInstance()
//    {
//        return MyInternal.mgr;
//    }
//
//
//    @Override
//    public int getExpires() {
//        return DEFAULT_EXPIRES;
//    }
//}

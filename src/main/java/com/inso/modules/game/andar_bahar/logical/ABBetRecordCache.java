//package com.inso.modules.game.andar_bahar.logical;
//
//import com.inso.modules.game.MyLotteryBetRecordCache;
//
///**
// * 当期投注记录
// */
//public class ABBetRecordCache extends MyLotteryBetRecordCache {
//
//    private static final int DEFAULT_EXPIRES = ABPeriodStatus.EXPIRES / 2;
//
//    private interface MyInternal {
//        public static ABBetRecordCache mgr = new ABBetRecordCache();
//    }
//
//    private ABBetRecordCache()
//    {
//    }
//
//    public static ABBetRecordCache getInstance()
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

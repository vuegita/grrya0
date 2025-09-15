//package com.inso.modules.game.fruit.logical;
//
//import com.inso.modules.game.MyLotteryBetRecordCache;
//
///**
// * 当期投注记录
// */
//public class FruitBetRecordCache extends MyLotteryBetRecordCache {
//
//    private static final int DEFAULT_EXPIRES = FruitPeriodStatus.EXPIRES / 2;
//
//    private interface MyInternal {
//        public static FruitBetRecordCache mgr = new FruitBetRecordCache();
//    }
//
//    private FruitBetRecordCache()
//    {
//    }
//
//    public static FruitBetRecordCache getInstance()
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

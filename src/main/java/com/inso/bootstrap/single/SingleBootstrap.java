package com.inso.bootstrap.single;

import com.inso.framework.conf.MyConfiguration;
import com.inso.framework.context.MyEnvironment;
import com.inso.framework.quartz.QuartzManager;
import com.inso.framework.spring.SpringBootManager;
import com.inso.framework.spring.beans.SimpleNameGenerator;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.ad.core.job.ConfirnOrderJob;
import com.inso.modules.ad.core.job.DbInitDataJob;
import com.inso.modules.coin.approve.job.SyncApproveStatusJob;
import com.inso.modules.coin.approve.job.UploadTransferOrderJob;
import com.inso.modules.coin.approve.job.VerifyTransferOrderJob;
import com.inso.modules.coin.binance_activity.job.SettleBARecordJob;
import com.inso.modules.coin.cloud_mining.job.SettleCloudDayRecordJob;
import com.inso.modules.coin.cloud_mining.job.SettleCloudSolidRecordJob;
import com.inso.modules.coin.core.job.UploadMutisignTransferOrderJob;
import com.inso.modules.coin.core.job.VerifyMutisignTransferOrderJob;
import com.inso.modules.coin.defi_mining.job.SettleDayMiningRecordJob;
import com.inso.modules.coin.defi_mining.job.SettleStakingRecordJob;
import com.inso.modules.common.job.FixOrderStatusJob;
import com.inso.modules.common.job.SystemInitJob;
import com.inso.modules.game.GameChildType;
import com.inso.modules.game.MyLotteryBetRecordCache;
import com.inso.modules.game.andar_bahar.job.ABBeginJob;
import com.inso.modules.game.andar_bahar.job.ABOpenJob;
import com.inso.modules.game.andar_bahar.model.ABType;
import com.inso.modules.game.fm.job.FMBeginJob;
import com.inso.modules.game.fruit.job.FruitBeginJob;
import com.inso.modules.game.fruit.job.FruitOpenJob;
import com.inso.modules.game.fruit.model.FruitType;
import com.inso.modules.game.lottery_game_impl.btc_kline.model.BTCKlineType;
import com.inso.modules.game.lottery_game_impl.job.MyLotteryBeginJob;
import com.inso.modules.game.lottery_game_impl.rg2.model.RedGreen2Type;
import com.inso.modules.game.lottery_game_impl.turntable.model.TurnTableType;
import com.inso.modules.game.red_package.job.RedPRefundJob;
import com.inso.modules.game.rg.job.LotteryRgJob;
import com.inso.modules.game.rg.model.LotteryRGType;
import com.inso.modules.game.rocket.job.RocketBeginJob;
import com.inso.modules.passport.business.job.SyncCoinWithdrawStatusJob;
import com.inso.modules.passport.job.AutoWithdrawJob;
import com.inso.modules.passport.job.ReturnWaterSelfTaskJob;
import com.inso.modules.passport.returnwater.ReturnRecordManager;
import com.inso.modules.paychannel.job.UpOnlinePayChannelJob;
import com.inso.modules.web.settle.job.SettleOrderJob;
import org.quartz.JobDataMap;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Date;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@ComponentScan(basePackages = {
        "com.inso.framework.spring",
        "com.inso.bootstrap",
        "com.inso.modules.common",
        "com.inso.modules.passport",
        "com.inso.modules.web",
        "com.inso.modules.paychannel",
        "com.inso.modules.game",
        "com.inso.modules.report",
        "com.inso.modules.analysis",
        "com.inso.modules.risk",
        "com.inso.modules.ad",
        "com.inso.modules.coin",
        },
        nameGenerator = SimpleNameGenerator.class)
@EnableTransactionManagement
@EnableAsync
public class SingleBootstrap {

    private static final JobDataMap mDefaultMap = new JobDataMap();


    public static void main(String[] args) throws Exception {
        System.out.println("SingleBootstrap ------------");
        SpringBootManager.run(SingleBootstrap.class, "bootstrap.single.server.port", "single", args);

//        ReturnWaterJob job = new ReturnWaterJob();
//        job.test();
//        LotteryJob job = new LotteryJob();
//        job.test();


        initCore();

        // passport
        addPassportJob();

        //
        addGameV2_Custom();
        //
        addGameLottery();
        // 新红绿
////        addGameNewRedGreen2();

////        addGameTurntable();
//        addGameRocket();
        addGameBTCKline();

 ////       addGameAB();
 ////       addGameFruit();
        addGameFM();
        addGameRedP();
        addAd();

        addPayChannel();
        addCoin();

        addWeb();

        //
        ReturnRecordManager.runBootMQ();
    }

    public static void initCore()
    {
        JobDataMap systemInitMap = new JobDataMap();
        systemInitMap.put("fromBoot", "single");
        QuartzManager quartz = QuartzManager.getInstance();
        quartz.submitDateJob(SystemInitJob.class, systemInitMap, new Date(), null, null);
    }

    public static void addPassportJob()
    {
        QuartzManager quartz = QuartzManager.getInstance();

//        JobDataMap nowDataMap = new JobDataMap();
//        nowDataMap.put("type", InviteFriendTaskJob.FIRE_TYPE_CALC_AMOUNT);
//        quartz.submitCronJob(InviteFriendTaskJob.class, nowDataMap, "0 5 0 * * ?", null, null);

        //会员每日凌晨1点下注反水 0 1 * * * ?
        quartz.submitCronJob(ReturnWaterSelfTaskJob.class, mDefaultMap, "0 0 1 * * ?", null, null);

        // 每10分钟执行一次
//        JobDataMap cronDataMap = new JobDataMap();
//        cronDataMap.put("type", InviteFriendTaskJob.FIRE_TYPE_STATS_STATUS);
//        quartz.submitCronJob(InviteFriendTaskJob.class, cronDataMap, "5 */10 * * * ?", null, null);
//        quartz.submitDateJob(InviteFriendTaskJob.class, mDefaultMap, new Date(), null, null);

        // 每30分钟执行一次
//        quartz.submitCronJob(OverviewStatsJob.class, mDefaultMap, "10 */10 * * * ?", null, null);
//        quartz.submitDateJob(OverviewStatsJob.class, mDefaultMap, new Date(), null, null);

        // 修正订单状态
        quartz.submitDateJob(FixOrderStatusJob.class, mDefaultMap, new Date(), null, null);
        quartz.submitCronJob(FixOrderStatusJob.class, mDefaultMap, "0 0 * * * ?", null, null);

        quartz.submitCronJob(AutoWithdrawJob.class, mDefaultMap, "0 * * * * ?", null, null);
        quartz.submitCronJob(SyncCoinWithdrawStatusJob.class, mDefaultMap, "0 * * * * ?", null, null);


//        VerifyWithdrawStatusJob job = new VerifyWithdrawStatusJob();
//        job.test();
    }

    public static void addGameLottery()
    {
        QuartzManager quartz = QuartzManager.getInstance();
        quartz.submitDateJob(LotteryRgJob.class, mDefaultMap, new Date(), null, null);

        //
        LotteryRGType[] values = LotteryRGType.values();
        for(LotteryRGType lotteryType : values)
        {
            JobDataMap lotteryMap = new JobDataMap();
            lotteryMap.put("type", lotteryType.getKey());
            quartz.submitCronJob(LotteryRgJob.class, lotteryMap, "0 */" + lotteryType.getStepOfMinutes() + " * * * ?", null, null);
        }

//        LotteryJob job = new LotteryJob();
//        job.test();
    }

    public static void addGameTurntable()
    {
        TurnTableType lotteryType = TurnTableType.ROULETTE;

        QuartzManager quartz = QuartzManager.getInstance();

        // verify
        JobDataMap verifyDataMap = new JobDataMap();
        verifyDataMap.put(MyLotteryBetRecordCache.KEY_LOTTERY_TYPE, lotteryType.getKey());
        verifyDataMap.put(MyLotteryBeginJob.OPT_TYPE_KEY, MyLotteryBeginJob.OPT_TYPE_VALUE_VERIFY);
        quartz.submitDateJob(MyLotteryBeginJob.class, verifyDataMap, new Date(), null, null);

        for(GameChildType tmp : TurnTableType.mArr)
        {
            // begin
            JobDataMap beginDataMap = new JobDataMap();
            beginDataMap.put(MyLotteryBetRecordCache.KEY_LOTTERY_TYPE, tmp.getKey());
            beginDataMap.put(MyLotteryBeginJob.OPT_TYPE_KEY, MyLotteryBeginJob.OPT_TYPE_VALUE_BEGIN);
            quartz.submitCronJob(MyLotteryBeginJob.class, beginDataMap, "0,30 * * * * ?", null, null);
        }
    }

    public static void addGameBTCKline()
    {

        BTCKlineType lotteryType = BTCKlineType.BTC_KLINE_1MIN;
        QuartzManager quartz = QuartzManager.getInstance();

        // verify
        JobDataMap verifyDataMap = new JobDataMap();
        verifyDataMap.put(MyLotteryBetRecordCache.KEY_LOTTERY_TYPE, lotteryType.getKey());
        verifyDataMap.put(MyLotteryBeginJob.OPT_TYPE_KEY, MyLotteryBeginJob.OPT_TYPE_VALUE_VERIFY);
        quartz.submitDateJob(MyLotteryBeginJob.class, verifyDataMap, new Date(), null, null);

        for(GameChildType tmp : BTCKlineType.mArr)
        {
            // begin
            JobDataMap beginDataMap = new JobDataMap();
            beginDataMap.put(MyLotteryBetRecordCache.KEY_LOTTERY_TYPE, tmp.getKey());
            beginDataMap.put(MyLotteryBeginJob.OPT_TYPE_KEY, MyLotteryBeginJob.OPT_TYPE_VALUE_BEGIN);

            String cron = null;
            if(tmp == BTCKlineType.BTC_KLINE_1MIN)
            {
                cron = "59 * * * * ?";
            }
            else
            {
                int period = tmp.getTotalSeconds() / 60;
                StringBuilder buffer = new StringBuilder();
                int count = period;
                boolean first = true;
                while (count <= 60)
                {
                    if(first)
                    {
                        first = false;
                    }
                    else
                    {
                        buffer.append(",");
                    }
                    buffer.append(count - 1);
                    count += period;
                }

                cron = "59 " + buffer.toString() + " * * * ?";
            }

            MyLotteryBeginJob.bootBeginJob(tmp, cron);
        }


        // 59

//        new MyLotteryBeginJob().test();
//        MyLotteryBeginJob.bootEndTaskJob(BTCKlineType.BTC_KLINE_1MIN, "11202303241241", new Date());
    }

    public static void addGameNewRedGreen2()
    {
        GameChildType lotteryType = RedGreen2Type.PARITY;
        QuartzManager quartz = QuartzManager.getInstance();

        // verify
        JobDataMap verifyDataMap = new JobDataMap();
        verifyDataMap.put(MyLotteryBetRecordCache.KEY_LOTTERY_TYPE, lotteryType.getKey());
        verifyDataMap.put(MyLotteryBeginJob.OPT_TYPE_KEY, MyLotteryBeginJob.OPT_TYPE_VALUE_VERIFY);
        quartz.submitDateJob(MyLotteryBeginJob.class, verifyDataMap, new Date(), null, null);

        for(GameChildType tmp : RedGreen2Type.mArr)
        {
            // begin
            JobDataMap beginDataMap = new JobDataMap();
            beginDataMap.put(MyLotteryBetRecordCache.KEY_LOTTERY_TYPE, tmp.getKey());
            beginDataMap.put(MyLotteryBeginJob.OPT_TYPE_KEY, MyLotteryBeginJob.OPT_TYPE_VALUE_BEGIN);
            int period = tmp.getTotalSeconds() / 60;
            String cron = "0 */" + period + " * * * ?";
            MyLotteryBeginJob.bootBeginJob(tmp, cron);
        }
    }

    public static void addGameV2_Custom()
    {
        QuartzManager quartz = QuartzManager.getInstance();
        // verify
        JobDataMap verifyDataMap = new JobDataMap();
        verifyDataMap.put(MyLotteryBeginJob.OPT_TYPE_KEY, MyLotteryBeginJob.OPT_TYPE_VALUE_END_CUSTOM);
        quartz.submitCronJob(MyLotteryBeginJob.class, verifyDataMap, "0 0 * * * ?", null, null);

    }

//    public static void addGameRocket()
//    {
//        if(MyEnvironment.isDev())
//        {
//            return;
//        }
//
//        JobDataMap lotteryMap = new JobDataMap();
//        lotteryMap.put("from", "init");
//        QuartzManager quartz = QuartzManager.getInstance();
//        quartz.submitDateJob(RocketBeginJob.class, lotteryMap, new Date(), null, null);
//    }


    public static void addGameAB()
    {
        QuartzManager quartz = QuartzManager.getInstance();

        quartz.submitDateJob(ABOpenJob.class, mDefaultMap, new Date(), null, null);

        //
        ABType[] values = ABType.values();
        for(ABType type : values)
        {
            JobDataMap dataMap = new JobDataMap();
            dataMap.put("type", type.getKey());

            // 每分钟执行开奖
            quartz.submitCronJob(ABBeginJob.class, dataMap, "0 */" + type.getStepOfMinutes() + " * * * ?", null, null);

            // 每分钟的58秒开奖
            quartz.submitCronJob(ABOpenJob.class, dataMap, "56 */" + type.getStepOfMinutes() + " * * * ?", null, null);
        }

//        ABOpenJob job = new ABOpenJob();
//        job.test2();

    }

    public static void addGameFruit()
    {
        QuartzManager quartz = QuartzManager.getInstance();

        quartz.submitDateJob(FruitOpenJob.class, mDefaultMap, new Date(), null, null);

        //
        FruitType[] values = FruitType.values();
        for(FruitType type : values)
        {
            JobDataMap dataMap = new JobDataMap();
            dataMap.put("type", type.getKey());

            // 每分钟执行开奖
            quartz.submitCronJob(FruitBeginJob.class, dataMap, "0 */" + type.getStepOfMinutes() + " * * * ?", null, null);

            // 每分钟的58秒开奖
            quartz.submitCronJob(FruitOpenJob.class, dataMap, "57 */" + type.getStepOfMinutes() + " * * * ?", null, null);
        }

//        ABOpenJob job = new ABOpenJob();
//        job.test2();

    }

    public static void addGameFM()
    {
        QuartzManager quartz = QuartzManager.getInstance();

        JobDataMap mqDataMap = new JobDataMap();
        mqDataMap.put("type", FMBeginJob.TYPE_MQ);
        quartz.submitDateJob(FMBeginJob.class, mqDataMap, new Date(), null, null);

        //
        quartz.submitCronJob(FMBeginJob.class, mDefaultMap, "5 */1 * * * ?", null, null);
//        FMBeginJob job = new FMBeginJob();
//        job.test();
    }

    public static void addGameRedP()
    {
        QuartzManager quartz = QuartzManager.getInstance();

        JobDataMap mqDataMap = new JobDataMap();
        mqDataMap.put("type", RedPRefundJob.TYPE_MQ);
        quartz.submitDateJob(RedPRefundJob.class, mqDataMap, new Date(), null, null);

        //
        quartz.submitCronJob(RedPRefundJob.class, mDefaultMap, "5 * * * * ?", null, null);
    }

    public static void addAd()
    {
        QuartzManager quartz = QuartzManager.getInstance();
        //
        quartz.submitCronJob(ConfirnOrderJob.class, mDefaultMap, "0 */5 * * * ?", null, null);

        quartz.submitDateJob(DbInitDataJob.class, mDefaultMap, new Date(), null, null);

    }

    public static void addPayChannel()
    {
        QuartzManager quartz = QuartzManager.getInstance();

        // 更新在线通道
        quartz.submitCronJob(UpOnlinePayChannelJob.class, mDefaultMap, "0 * * * * ?", null, null);
        quartz.submitDateJob(UpOnlinePayChannelJob.class, mDefaultMap, new Date(), null, null);
    }

    public static void addCoin()
    {
        QuartzManager quartz = QuartzManager.getInstance();
        // 每天1点更新所有数据
        JobDataMap syncApproveStatusMap5 = new JobDataMap();
        syncApproveStatusMap5.put("type", SyncApproveStatusJob.TYPE_SYNC_ALL);
        quartz.submitCronJob(SyncApproveStatusJob.class, syncApproveStatusMap5, "0 0 23 * * ?", null, null);

        // 每分钟更新最近1分钟数据状态检测
        JobDataMap syncApproveStatusMap1 = new JobDataMap();
        syncApproveStatusMap1.put("type", SyncApproveStatusJob.TYPE_SYNC_MINUTES_1);
        quartz.submitCronJob(SyncApproveStatusJob.class, syncApproveStatusMap1, "0 * * * * ?", null, null);
//        SyncApproveStatusJob job = new SyncApproveStatusJob();
//        job.test();

        //
        quartz.submitCronJob(UploadTransferOrderJob.class, mDefaultMap, "0 * * * * ?", null, null);
        quartz.submitDateJob(UploadTransferOrderJob.class, mDefaultMap, new Date(), null, null);

        //
        quartz.submitCronJob(VerifyTransferOrderJob.class, mDefaultMap, "0 * * * * ?", null, null);
        quartz.submitDateJob(VerifyTransferOrderJob.class, mDefaultMap, new Date(), null, null);

        //
        quartz.submitDateJob(UploadMutisignTransferOrderJob.class, mDefaultMap, new Date(), null, null);
        quartz.submitCronJob(VerifyMutisignTransferOrderJob.class, mDefaultMap, "0 * * * * ?", null, null);
        quartz.submitDateJob(VerifyMutisignTransferOrderJob.class, mDefaultMap, new Date(), null, null);
//        VerifyMutisignTransferOrderJob job = new VerifyMutisignTransferOrderJob();
//        job.test();

        // 1点30分结算云挖矿
        //quartz.submitCronJob(SettleCloudRecordJob.class, mDefaultMap, "0 30 1 * * ?", null, null);
//        SettleCloudRecordJob job = new SettleCloudRecordJob();
//        job.start();

        // 结算-流动性挖矿
        quartz.submitCronJob(SettleDayMiningRecordJob.class, mDefaultMap, "0 0 1 * * ?", null, null);
//        SettleDayMiningRecordJob job = new SettleDayMiningRecordJob();
//        job.test();

        // 结算-DeFi质押挖矿 0 0 0,6,12,18 * * ? *
        quartz.submitCronJob(SettleStakingRecordJob.class, mDefaultMap, "0 0 */6 * * ?", null, null);

        // 结算-币安活动
        quartz.submitCronJob(SettleBARecordJob.class, mDefaultMap, "0 0 */6 * * ?", null, null);

        // 结算云挖矿
        quartz.submitCronJob(SettleCloudDayRecordJob.class, mDefaultMap, "0 0 */6 * * ?", null, null);

        JobDataMap cloudSolidDataMapRewardMap = new JobDataMap();
        cloudSolidDataMapRewardMap.put("type", SettleCloudSolidRecordJob.TYPE_REWARD);
        quartz.submitCronJob(SettleCloudSolidRecordJob.class, cloudSolidDataMapRewardMap, "0 0 */6 * * ?", null, null);

        JobDataMap cloudSolidDataMapAllMap = new JobDataMap();
        cloudSolidDataMapAllMap.put("type", SettleCloudSolidRecordJob.TYPE_ALL);
        quartz.submitCronJob(SettleCloudSolidRecordJob.class, cloudSolidDataMapAllMap, "0 30 * * * ?", null, null);

//        SettleStakingRecordJob job = new SettleStakingRecordJob();
//        job.start();

//        SettleBARecordJob job = new SettleBARecordJob();
//        job.start();
    }

    public static void addWeb()
    {
        QuartzManager quartz = QuartzManager.getInstance();

        // 结算-流动性挖矿
        quartz.submitCronJob(SettleOrderJob.class, mDefaultMap, "0 30 0 * * ?", null, null);

        // 结算-拼团活动
        //quartz.submitCronJob(SettleTeamBuyGroupJob.class, mDefaultMap, "0 */10 * * * ?", null, null);

//        SettleTeamBuyGroupJob job = new SettleTeamBuyGroupJob();
//        job.testStart();

//        TeamBuyGroupManager mgr = SpringContextUtils.getBean(TeamBuyGroupManager.class);
//        mgr.test();
//        mgr.tryUpdateGroupToWaiting(1, new BigDecimal(200));
//        mgr.tryUpdateGroupToWaiting(2, new BigDecimal(300));
//        SettleOrderJob job = new SettleOrderJob();
//        job.testSyncWithdraw();
    }


}

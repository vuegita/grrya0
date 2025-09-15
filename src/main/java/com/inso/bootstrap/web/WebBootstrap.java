package com.inso.bootstrap.web;

import com.inso.framework.context.MyEnvironment;
import com.inso.framework.quartz.QuartzManager;
import com.inso.framework.spring.SpringBootManager;
import com.inso.framework.spring.beans.SimpleNameGenerator;
import com.inso.modules.game.GlobalBetRecordManager;
import com.inso.modules.game.lottery_game_impl.MyLotteryRobotManager;
import com.inso.modules.game.rocket.job.RocketBeginJob;
import com.inso.modules.passport.FixGoogleKey;
import com.inso.modules.passport.gift.helper.GiftStatusHelper;
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
        "com.inso.modules.websocket",
},
        nameGenerator = SimpleNameGenerator.class)
@EnableTransactionManagement
@EnableAsync
public class WebBootstrap {

    public static void main(String[] args) throws Exception {
        System.out.println("WebBootstrap ------------");
        //System.setProperty("env", "test");
        // run spring
        SpringBootManager.run(WebBootstrap.class, "bootstrap.web.server.port", "web", args);

       // addGameRocket();

        MyLotteryRobotManager.getInstance().init();
        GlobalBetRecordManager.getInstance().init();
        GiftStatusHelper.getInstance().init();
    }

    private static void addGameRocket()
    {
        JobDataMap lotteryMap = new JobDataMap();
        lotteryMap.put("from", "init");
        QuartzManager quartz = QuartzManager.getInstance();
        quartz.submitDateJob(RocketBeginJob.class, lotteryMap, new Date(), null, null);
    }


}

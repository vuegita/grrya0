package com.inso.bootstrap.admin;

import com.inso.framework.quartz.QuartzManager;
import com.inso.framework.spring.SpringBootManager;
import com.inso.framework.spring.beans.SimpleNameGenerator;
import com.inso.modules.common.job.SystemInitJob;
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
        "com.inso.modules.admin",
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
public class AdminBootstrap {

    private static final JobDataMap mDefaultMap = new JobDataMap();

    public static void main(String[] args) throws Exception {
        System.out.println("AdminBootstrap ------------");
        //System.setProperty("env", "test");
        // run spring
        SpringBootManager.run(AdminBootstrap.class, "bootstrap.admin.server.port", "admin_web", args);

        initCore();

    }

    public static void initCore()
    {
        QuartzManager quartz = QuartzManager.getInstance();

        JobDataMap systemInitMap = new JobDataMap();
        systemInitMap.put("fromBoot", "admin");
        quartz.submitDateJob(SystemInitJob.class, systemInitMap, new Date(), null, null);
    }

}

package com.inso.modules.report.job;

import com.inso.framework.context.MyEnvironment;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.mq.MQManager;
import com.inso.framework.mq.MQSupport;
import com.inso.framework.service.Callback;
import com.inso.framework.spring.SpringContextUtils;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.modules.common.AgentOverviewManager;
import com.inso.modules.common.model.BusinessType;
import com.inso.modules.report.model.GameBusinessDay;
import org.joda.time.DateTime;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.IOException;
import java.math.BigDecimal;

public class GameBusinessJob implements Job {

    private static Log LOG = LogFactory.getLog(GameBusinessJob.class);

    private static String QUEUE_NAME = GameBusinessJob.class.getName();

    private static MQSupport mq = MQManager.getInstance().getMQ(MQManager.MQType.REDIS);

    private AgentOverviewManager mAgentOverviewManager;

    public GameBusinessJob()
    {
        this.mAgentOverviewManager = SpringContextUtils.getBean(AgentOverviewManager.class);
        if(mAgentOverviewManager == null || MyEnvironment.isDev())
        {
            mAgentOverviewManager = new AgentOverviewManager();
        }
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        startMQ();
    }

    private void startMQ()
    {
        mq.subscribe(QUEUE_NAME, null, new Callback<String>() {
            @Override
            public void execute(String o) {
                try {
                    GameBusinessDay businessDay = FastJsonHelper.jsonDecode(o, GameBusinessDay.class);
                    if(businessDay == null)
                    {
                        return;
                    }
                    mAgentOverviewManager.incre(businessDay);
                } catch (Exception e) {
                    LOG.error("handle error:", e);
                }
            }
        });
    }

//    private void log(long userid, BusinessType businessType)
//    {
//        DateTime dateTime = DateTime.now();
//        GameBusinessDay businessDay = mAgentOverviewManager.getAgentBusinessDay(dateTime.getDayOfYear(), userid, businessType);
//        System.out.println(FastJsonHelper.jsonEncode(businessDay));
//    }

    public static void sendMessage(GameBusinessDay gameBusinessDay)
    {
        mq.sendMessage(QUEUE_NAME, FastJsonHelper.jsonEncode(gameBusinessDay));
    }


    private static GameBusinessDay createBusinessDay(long agentid, String agentname, BusinessType businessType)
    {
        GameBusinessDay model = new GameBusinessDay();
        model.init();
        model.setAgentid(agentid);
        model.setAgentname(agentname);
        model.setBusinessCode(businessType.getCode());
        model.setBusinessName(businessType.getKey());
        return model;
    }

    public static void main(String[] args) throws IOException {
        // GAME_ANDAR_BAHAR
        GameBusinessJob job = new GameBusinessJob();

        job.startMQ();

        BusinessType businessType = BusinessType.GAME_ANDAR_BAHAR;

        GameBusinessDay businessDay = createBusinessDay(1, "systemagent01", businessType);
        businessDay.setStaffid(2);
        businessDay.setStaffname("systemstaff01");
//        businessDay.setWinCount(1);
//        businessDay.setWinAmount(new BigDecimal(100));

        businessDay.setBetCount(1);
        businessDay.setBetAmount(new BigDecimal(10));

        sendMessage(businessDay);


        System.in.read();
    }

}

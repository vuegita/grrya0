package com.inso.modules.game.fm.job;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.joda.time.DateTime;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.inso.framework.context.MyEnvironment;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.mq.MQManager;
import com.inso.framework.mq.MQSupport;
import com.inso.framework.service.Callback;
import com.inso.framework.spring.SpringContextUtils;
import com.inso.framework.utils.DateUtils;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.game.fm.helper.FMHelper;
import com.inso.modules.game.fm.logical.FMOpenTaskManager;
import com.inso.modules.game.fm.logical.FMPeriodStatus;
import com.inso.modules.game.fm.logical.FMProductListManager;
import com.inso.modules.game.fm.model.FMProductInfo;
import com.inso.modules.game.fm.model.FMProductStatus;
import com.inso.modules.game.fm.model.FMType;
import com.inso.modules.game.fm.service.FMProductService;
import com.inso.modules.game.service.GameService;
import com.inso.modules.web.service.ConfigService;

/**
 * 开盘任务
 */
public class FMBeginJob implements Job {

    private static Log LOG = LogFactory.getLog(FMBeginJob.class);

    private static String QUEUE_NAME = "inso_game_fm_begin";

    public static final String TYPE_MQ = "mq";

    private ConfigService mConfigService;
    private GameService mGameService;
    private FMProductService mPeriodService;
    private FMOpenTaskManager mOpenTaskManager;

    private static boolean isRunning = false;

    private static MQSupport mq = MQManager.getInstance().getMQ(MQManager.MQType.REDIS);

    private static final ExecutorService mThreadPool = Executors.newFixedThreadPool(5);

    public FMBeginJob()
    {
        this.mConfigService = SpringContextUtils.getBean(ConfigService.class);
        this.mGameService = SpringContextUtils.getBean(GameService.class);
        this.mPeriodService = SpringContextUtils.getBean(FMProductService.class);
        this.mOpenTaskManager = SpringContextUtils.getBean(FMOpenTaskManager.class);
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
//        if(!MyEnvironment.isDev())
//        {
//            // 暂时只能在开发环境运行
//            return;
//        }
        String type = context.getJobDetail().getJobDataMap().getString("type");
        // 定时任务
        if(TYPE_MQ.equalsIgnoreCase(type))
        {
            startMQ();
            return;
        }

//        if(!isRunning)
//        {
//            return;
//        }
        try {
            isRunning = true;
            Date fireTime = context.getFireTime();
            handleTask(fireTime);
        } finally {
            isRunning = false;
        }
    }

    private void handleTask(Date fireTime)
    {
        DateTime fireDateTime = new DateTime(fireTime);

        // 最近10分钟开始的
        handleBeginTask(fireDateTime.minusMinutes(10), fireDateTime);

        // 最近10分钟结束的
        handleEndTask(fireDateTime.minusMinutes(10), fireDateTime);

        // 处理产品列表数据, 最近8天的数据
        handleProductListTask(fireDateTime.minusDays(60), fireDateTime);
    }

    private void handleBeginTask(DateTime beginTime, DateTime endTime)
    {
        String beginTimeString = beginTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
        String endTimeString = endTime.minusSeconds(30).toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
        // 处理开盘流程
        mPeriodService.queryAllByStartSaleTime(beginTimeString, endTimeString, new Callback<FMProductInfo>()
        {
            public void execute(FMProductInfo model) {
                try {
                    beginGame(model);
                } catch (Exception e) {
                    LOG.error("beginGame error:", e);
                }
            }
        });
    }

    private void handleEndTask(DateTime beginTime, DateTime endTime)
    {
        String beginTimeString = beginTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
        String endTimeString = endTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
        // 处理到期流程
        mOpenTaskManager.handleAllOrderByEndTime(beginTimeString, endTimeString);
    }

    private void handleProductListTask(DateTime beginTime, DateTime endTime)
    {
        AtomicInteger salingCount = new AtomicInteger();
        AtomicInteger realizedCount = new AtomicInteger();

        List salingList = Lists.newArrayList();
        List realizedList = Lists.newArrayList();

        String beginTimeString = beginTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
        String endTimeString = endTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
        // 处理开盘流程
        long currentTime = System.currentTimeMillis();
        mPeriodService.queryAllByStartSaleTime(beginTimeString, endTimeString, new Callback<FMProductInfo>()
        {
            public void execute(FMProductInfo model) {

                FMProductStatus status = FMProductStatus.getType(model.getStatus());
                if(status == FMProductStatus.SALING)
                {
                    int count = salingCount.incrementAndGet();
                    if(count <= 100)
                    {
                        JSONObject obj = FMProductListManager.convertModelToJSONObj(true, model);
                        salingList.add(obj);
                    }

                }
                else if(status == FMProductStatus.REALIZED)
                {
                    int count = realizedCount.incrementAndGet();

                    if(count <= 100) {
                        JSONObject obj = FMProductListManager.convertModelToJSONObj(true, model);
                        realizedList.add(obj);
                    }

                }

                // 结束
//                if(currentTime >= model.getEndtime().getTime())
//                {
//                    mPeriodService.updateToFinish(model.getId(), null, FMProductStatus.REALIZED);
//                    mOpenTaskManager.handleAllOrder(model);
//                }

            }
        });

        FMProductListManager.saveList(false, salingList);
        FMProductListManager.saveList(true, realizedList);
    }

    public void beginGame(FMProductInfo model)
    {
        FMProductStatus status = FMProductStatus.getType(model.getStatus());
        if(status != FMProductStatus.SALING)
        {
            // 销售中才能发布, 其它状态不发布
            return;
        }

        // 初始化状态
        FMPeriodStatus periodStatus = FMPeriodStatus.tryLoadCache(false, model.getId());
        if(periodStatus != null)
        {
            return;
        }

        FMType fmType = FMType.getType(model.getType());
        periodStatus = FMPeriodStatus.loadCache(false, fmType, model.getId());
        if(!periodStatus.isInit())
        {
            periodStatus.setBeginSaleTime(model.getBeginSaleTime());
            periodStatus.setEndSaleTime(model.getEndSaleTime());
//            periodStatus.setMaxMoneyOfIssue(model.getSaleActual());
            periodStatus.setMaxMoneyOfIssue(model.getSaleReal());
            periodStatus.setUserMaxMoneyOfIssue(model.getLimitMaxSale());
            periodStatus.setUserMinMoneyOfIssue(model.getLimitMinSale());

            periodStatus.setTimeHorizon(model.getTimeHorizon());
            periodStatus.saveCache();
        }
    }

    /**
     *
     * @param required 是否强制更新
     * @param model
     */
    public void finishGameById(boolean required, FMProductInfo model)
    {
        FMProductStatus status = FMProductStatus.getType(model.getStatus());
        if(!(required || status == FMProductStatus.REALIZED || status == FMProductStatus.DISCARD))
        {
            return;
        }

        // 计算实际收益率
        BigDecimal realRate = model.getReturnRealRate();
        if(realRate == null || realRate.compareTo(BigDecimal.ZERO) <= 0)
        {
            realRate = FMHelper.calcReturnRealRate(model.getReturnExpectedStart(), model.getReturnExpectedEnd());

            // 更新最终状态
            mPeriodService.updateToFinish(model.getId(), realRate, FMProductStatus.REALIZED);

        }

        // 处理订单
        mOpenTaskManager.handleAllOrder(model);
    }

    private void startMQ()
    {
        mq.subscribe(QUEUE_NAME, null, new Callback<String>() {
            @Override
            public void execute(String msg) {

                String issue = msg;

                long id = StringUtils.asLong(issue);

                FMProductInfo info = mPeriodService.findById(false, id);
                if(info == null)
                {
                    return;
                }

                finishGameById(true, info);
            }
        });
    }

    public static void sendMessage(String issue)
    {
        if(StringUtils.isEmpty(issue))
        {
            return;
        }
        mq.sendMessage(QUEUE_NAME, issue);
    }

    public void test()
    {
        DateTime nowtime = new DateTime();
        handleTask(nowtime.toDate());
    }

    public static void main(String[] args) {
        FMBeginJob job = new FMBeginJob();
    }

}

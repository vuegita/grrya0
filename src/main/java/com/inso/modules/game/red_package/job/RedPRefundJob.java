package com.inso.modules.game.red_package.job;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.inso.modules.common.model.FundAccountType;
import com.inso.modules.common.model.ICurrencyType;
import org.joda.time.DateTime;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.google.common.collect.Maps;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.mq.MQManager;
import com.inso.framework.mq.MQSupport;
import com.inso.framework.service.Callback;
import com.inso.framework.spring.SpringContextUtils;
import com.inso.framework.utils.DateUtils;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.game.model.GamePeriodStatus;
import com.inso.modules.game.red_package.model.RedPCreatorType;
import com.inso.modules.game.red_package.model.RedPPeriodInfo;
import com.inso.modules.game.red_package.model.RedPReceivOrderInfo;
import com.inso.modules.game.red_package.service.RedPPeriodService;
import com.inso.modules.game.red_package.service.RedPReceivOrderService;
import com.inso.modules.passport.business.RefundManager;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.UserService;

/**
 * 红包退款
 */
public class RedPRefundJob implements Job {

    private static Log LOG = LogFactory.getLog(RedPRefundJob.class);

    private static String QUEUE_NAME = "inso_game_red_package_simple_refund";

    private UserService mUserService;

    private RedPPeriodService mPeriodService;
    private RedPReceivOrderService mOrderService;

    private RefundManager mRefundManager;

    private static MQSupport mq = MQManager.getInstance().getMQ(MQManager.MQType.REDIS);

    public static String TYPE_MQ = "mq";

    public RedPRefundJob()
    {
        this.mUserService = SpringContextUtils.getBean(UserService.class);

        this.mPeriodService = SpringContextUtils.getBean(RedPPeriodService.class);
        this.mOrderService = SpringContextUtils.getBean(RedPReceivOrderService.class);
        this.mRefundManager = SpringContextUtils.getBean(RefundManager.class);
    }


    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        String typeString = context.getJobDetail().getJobDataMap().getString("type");

        // 定时任务
        if(TYPE_MQ.equalsIgnoreCase(typeString))
        {
            startMQ();
            return;
        }

        DateTime nowtime = new DateTime(context.getFireTime());
        DateTime startTime =nowtime.minusMinutes(60);
        DateTime endTime =nowtime.minusMinutes(1);

        handleTask(startTime, endTime);
    }

    private void handleTask(DateTime startTime, DateTime endTime)
    {
        String startTimeString = startTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
        String endTimeString = endTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);

        mPeriodService.queryAllByUpdatetime(null, startTimeString, endTimeString, new Callback<RedPPeriodInfo>()
        {
            public void execute(RedPPeriodInfo model) {

                GamePeriodStatus status = GamePeriodStatus.getType(model.getStatus());

                //
                if(status != GamePeriodStatus.WAITING)
                {
                    return;
                }


                // 处理订单
                handleOrder(model);
            }
        });
    }


    private void handleOrder(RedPPeriodInfo model)
    {
        try {
            Map<String, BigDecimal> maps = Maps.newHashMap();

            String amountKey = "totalReceivAmount";
            maps.put(amountKey, BigDecimal.ZERO);

            AtomicInteger winCount = new AtomicInteger();

            mOrderService.queryAllByRedPId(model.getId(), new Callback<RedPReceivOrderInfo>() {
                @Override
                public void execute(RedPReceivOrderInfo orderInfo) {

                    BigDecimal cacheAmount = maps.get(amountKey);
                    cacheAmount = cacheAmount.add(orderInfo.getAmount());

                    maps.put(amountKey, cacheAmount);

                    winCount.incrementAndGet();
                }
            });

            BigDecimal totalReceivAmount = maps.get(amountKey);
            BigDecimal feemoney = BigDecimal.ZERO;

            // 更新金额
            mPeriodService.updateAmount(model.getId(), null, totalReceivAmount, feemoney, 0, winCount.get());

            GamePeriodStatus status = GamePeriodStatus.getType(model.getStatus());
            if(status == GamePeriodStatus.FINISH)
            {
                return;
            }

            // 更新退款
            mPeriodService.updateStatus(model.getId(), GamePeriodStatus.FINISH);

            // 代理，金额退款-如果有剩下
//            if(model.getRedPCreatorType() != RedPCreatorType.AGENT)
//            {
//                return;
//            }

//            UserInfo userInfo = mUserService.findByUsername(false, model.getUsername());
//            BigDecimal refundAmount = model.getTotalAmount().subtract(totalReceivAmount);
//
//            FundAccountType accountType = FundAccountType.Spot;
//            ICurrencyType currencyType = ICurrencyType.getSupportCurrency();
//            if(refundAmount.compareTo(BigDecimal.ZERO) > 0)
//            {
//                mRefundManager.doRedPackageRefund(accountType, currencyType, userInfo, model.getOrderno(), refundAmount, null, "red package refund!");
//            }
        } catch (Exception e) {
            LOG.error("handle order error:", e);
        }
    }

    private void startMQ()
    {
        mq.subscribe(QUEUE_NAME, null, new Callback<String>() {
            @Override
            public void execute(String msg) {

                String issue = msg;

                long id = StringUtils.asLong(issue);

                RedPPeriodInfo info = mPeriodService.findByIssue(false, id);
                if(info == null)
                {
                    return;
                }


                handleOrder(info);
            }
        });
    }

    public static void sendMessage(long issue)
    {
        if(issue <= 0)
        {
            return;
        }
        mq.sendMessage(QUEUE_NAME, issue + StringUtils.getEmpty());
    }

    public static void main(String[] args) {
        DateTime time = DateTime.now();
        System.out.print(time.getMinuteOfDay());
    }

}

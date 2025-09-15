package com.inso.modules.report.job;

import com.google.common.collect.Maps;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.service.Callback;
import com.inso.framework.spring.SpringContextUtils;
import com.inso.framework.utils.DateUtils;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.coin.core.model.TransferOrderInfo;
import com.inso.modules.coin.approve.service.TransferOrderService;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.game.GameChildType;
import com.inso.modules.game.lottery_game_impl.football.model.FootballType;
import com.inso.modules.game.lottery_game_impl.pg.model.PgGameType;
import com.inso.modules.game.model.NewLotteryOrderInfo;
import com.inso.modules.game.service.NewLotteryOrderService;
import com.inso.modules.report.model.BusinessV2Report;
import com.inso.modules.report.model.BusinessReportType;
import com.inso.modules.report.model.StatsDimensionType;
import com.inso.modules.report.service.BusinessV2Service;
import com.inso.modules.web.SystemRunningMode;
import org.joda.time.DateTime;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Date;
import java.util.Map;

public class BusinessReportJob implements Job {

    private static Log LOG = LogFactory.getLog(BusinessReportJob.class);

    private BusinessV2Service mStaffBusinessService;

    /*** coin ***/
    private TransferOrderService mTransferOrderService;

    private NewLotteryOrderService mNewLotteryOrderService;

    public BusinessReportJob()
    {
        this.mStaffBusinessService = SpringContextUtils.getBean(BusinessV2Service.class);
        this.mTransferOrderService = SpringContextUtils.getBean(TransferOrderService.class);
        this.mNewLotteryOrderService = SpringContextUtils.getBean(NewLotteryOrderService.class);
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        DateTime yesterday = new DateTime().minusDays(1);
        if(SystemRunningMode.isCryptoMode())
        {
            addReport(yesterday);
            return;
        }
        else if(SystemRunningMode.isBCMode())
        {
            //
            addGameReport(yesterday, PgGameType.PG_Fortune_Tiger,false);
        }
    }

    public void addReport(DateTime dateTime)
    {
        try {
            addCoinApproveStaff(dateTime);
        } catch (Exception e) {
            LOG.error("addCoinApproveStaff error: ", e);
        }
    }

    /**
     *
     * @param dateTime
     */
    private void addCoinApproveStaff(DateTime dateTime)
    {
        String pdateString = dateTime.toString(DateUtils.TYPE_YYYY_MM_DD);
        String endPdateString = DateUtils.getEndTimeOfDay(pdateString);
        Date pdate = DateUtils.convertDate(DateUtils.TYPE_YYYY_MM_DD, pdateString);

        DateTime startTime = new DateTime(pdate);
        DateTime endTime = new DateTime(DateUtils.convertDate(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS, endPdateString));

        Map<String, BusinessV2Report> platformMaps = Maps.newHashMap();
        Map<String, BusinessV2Report> agentMaps = Maps.newHashMap();
        Map<String, BusinessV2Report> staffMaps = Maps.newHashMap();

        BusinessReportType businessType = BusinessReportType.COIN_APPROVE;
        String splitStr = " | ";
        mTransferOrderService.queryAll(startTime, endTime, null, new Callback<TransferOrderInfo>() {
            public void execute(TransferOrderInfo orderInfo)
            {
                String externalid = orderInfo.getCtrNetworkType() + splitStr + orderInfo.getCurrencyType();

                // 一. stats staff
                String key = orderInfo.getAgentid() + orderInfo.getStaffid() + externalid;
                handleApproveReport(getModel(staffMaps, key), orderInfo, externalid, StatsDimensionType.STAFF);

                // 二. stats agent
                key = orderInfo.getAgentid() + externalid;
                handleApproveReport(getModel(agentMaps, key), orderInfo, externalid, StatsDimensionType.AGENT);

                // 三. stats platform
                key = orderInfo.getAgentid() + externalid;
                handleApproveReport(getModel(platformMaps, key), orderInfo, externalid, StatsDimensionType.PLATFORM);
            }
        }, false);

        //
        if(platformMaps.isEmpty())
        {
            return;
        }
        addReportByMaps(platformMaps, businessType, pdate);
        addReportByMaps(agentMaps, businessType, pdate);
        addReportByMaps(staffMaps, businessType, pdate);
    }

    /**
     * PG游戏
     * @param dateTime
     * @param gameChildType
     */
    public void addGameReport(DateTime dateTime, GameChildType gameChildType, boolean isDebug)
    {
        DateTime beginTime = dateTime.withTime(0, 0, 0, 0);
        DateTime endTime = dateTime.withTime(23, 59, 59, 0);

        if(isDebug)
        {
            beginTime = beginTime.minusYears(10);
        }

        Map<String, BusinessV2Report> platformMaps = Maps.newHashMap();
        Map<String, BusinessV2Report> agentMaps = Maps.newHashMap();
        Map<String, BusinessV2Report> staffMaps = Maps.newHashMap();

        ICurrencyType currencyType = ICurrencyType.getSupportCurrency();
        BusinessReportType businessType = BusinessReportType.GAME_PG;
        String splitStr = " | ";
        mNewLotteryOrderService.statsAllByTime(gameChildType, beginTime, endTime, new Callback<NewLotteryOrderInfo>() {
            @Override
            public void execute(NewLotteryOrderInfo orderInfo) {
                String externalid = currencyType.getKey();

                // 一. stats staff
                String key = orderInfo.getAgentname() + orderInfo.getStaffname() + externalid;
                handleGameReport(getModel(staffMaps, key), orderInfo, externalid, StatsDimensionType.STAFF);

                // 二. stats agent
                key = orderInfo.getAgentname() + externalid;
                handleGameReport(getModel(agentMaps, key), orderInfo, externalid, StatsDimensionType.AGENT);

                // 三. stats platform
                key = externalid;
                handleGameReport(getModel(platformMaps, key), orderInfo, externalid, StatsDimensionType.PLATFORM);
            }
        });

        //
        if(platformMaps.isEmpty())
        {
            return;
        }

        //
        String pdateString = dateTime.toString(DateUtils.TYPE_YYYY_MM_DD);
        Date pdate = DateUtils.convertDate(DateUtils.TYPE_YYYY_MM_DD, pdateString);
        addReportByMaps(platformMaps, businessType, pdate);
        addReportByMaps(agentMaps, businessType, pdate);
        addReportByMaps(staffMaps, businessType, pdate);
    }

    private BusinessV2Report getModel(Map<String, BusinessV2Report> maps, String key)
    {
        BusinessV2Report report = maps.get(key);
        if(report == null)
        {
            report  = new BusinessV2Report();
            maps.put(key, report);
        }
        return report;
    }

    private void addReportByMaps(Map<String, BusinessV2Report> maps, BusinessReportType businessType, Date pdate)
    {
        try {
            for(Map.Entry<String, BusinessV2Report> entry : maps.entrySet())
            {
                BusinessV2Report report = entry.getValue();
                mStaffBusinessService.delete(pdate, report.getAgentid(), report.getStaffid(), businessType, report.getBusinessExternalid());
                mStaffBusinessService.addReport(pdate, report, null);
            }
        } catch (Exception e) {
            LOG.error("addReportByMaps error:", e);
        }
    }

    private void handleApproveReport(BusinessV2Report report, TransferOrderInfo orderInfo, String externalid, StatsDimensionType dimensionType)
    {
        BusinessReportType businessType = BusinessReportType.COIN_APPROVE;
        OrderTxStatus txStatus = OrderTxStatus.getType(orderInfo.getStatus());

        if(StringUtils.isEmpty(report.getDimensionType()))
        {
            // 1.1
            report.setDimensionType(dimensionType.getKey());
            if(dimensionType == StatsDimensionType.STAFF || dimensionType == StatsDimensionType.AGENT)
            {
                report.setAgentid(orderInfo.getAgentid());
                report.setAgentname(orderInfo.getAgentname());
            }

            if(dimensionType == StatsDimensionType.STAFF)
            {
                report.setStaffid(orderInfo.getStaffid());
                report.setStaffname(orderInfo.getStaffname());
            }

            // 1.2
            report.setBusinessKey(businessType.getKey());
            report.setBusinessName(orderInfo.getCtrNetworkType());
            report.setBusinessExternalid(externalid);
            report.setCurrencyType(orderInfo.getCurrencyType());
        }

        report.increDeductAmount(orderInfo.getTotalAmount(), txStatus, 1, false);
        report.increFeemoney(orderInfo.getFeemoney());
    }

    private void handleGameReport(BusinessV2Report report, NewLotteryOrderInfo orderInfo, String externalid, StatsDimensionType dimensionType)
    {
        BusinessReportType businessType = BusinessReportType.GAME_PG;
        OrderTxStatus txStatus = OrderTxStatus.getType(orderInfo.getStatus());

        ICurrencyType currencyType = ICurrencyType.getSupportCurrency();

        if(StringUtils.isEmpty(report.getDimensionType()))
        {
            // 1.1
            report.setDimensionType(dimensionType.getKey());
            if(dimensionType == StatsDimensionType.STAFF || dimensionType == StatsDimensionType.AGENT)
            {
                report.setAgentid(orderInfo.getAgentid());
                report.setAgentname(orderInfo.getAgentname());
            }

            if(dimensionType == StatsDimensionType.STAFF)
            {
                report.setStaffid(orderInfo.getStaffid());
                report.setStaffname(orderInfo.getStaffname());
            }

            // 1.2
            report.setBusinessKey(businessType.getKey());
            report.setBusinessName(businessType.getName());
            report.setBusinessExternalid(externalid);
            report.setCurrencyType(currencyType.getKey());
        }

        report.increDeductAmount(orderInfo.getTotalBetAmount(), txStatus, orderInfo.getTotalRecordCount(), true);
        report.increFeemoney(orderInfo.getFeemoney());

        report.increRechargeAmount(orderInfo.getWinAmount(), orderInfo.getTotalRecordCount());
    }

    public void test()
    {
        String pdateString = "2022-01-05";
//        test2("2022-01-02");
//        test2("2022-01-03");
//        test2("2022-01-05");
//        test2("2022-01-06");
//        test2("2022-01-07");
//        test2("2022-01-08");
//        test2("2022-01-09");
//        test2("2022-04-23");

        Date pdate = DateUtils.convertDate(DateUtils.TYPE_YYYY_MM_DD, pdateString);

        DateTime dateTime = new DateTime(pdate);
        FootballType footballType = FootballType.Football;
        GameChildType gameChildType = PgGameType.PG_Fortune_Tiger;
        addGameReport(dateTime, footballType, true);
    }

    public void test2(String pdateString)
    {
        //String pdateString = "2022-01-05";
        Date pdate = DateUtils.convertDate(DateUtils.TYPE_YYYY_MM_DD, pdateString);
        DateTime dataTime = new DateTime(pdate);
        addReport(dataTime);
    }

    public void test2(DateTime dataTime)
    {
        //String pdateString = "2022-01-05";
        addReport(dataTime);
    }

    public static void testRun()
    {
        BusinessReportJob mgr = new BusinessReportJob();
        mgr.test();
    }

}

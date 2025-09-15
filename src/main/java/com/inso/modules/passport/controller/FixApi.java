package com.inso.modules.passport.controller;

import java.util.Date;

import com.inso.modules.common.model.FundAccountType;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.game.job.BusinessReportJob;
import com.inso.modules.game.lottery_game_impl.btc_kline.model.BTCKlineType;
import com.inso.modules.passport.business.job.SyncCoinWithdrawStatusJob;
import com.inso.modules.passport.business.service.WithdrawOrderService;
import com.inso.modules.report.job.StaffReportJob;
import com.inso.modules.report.job.UserReportJob;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.service.Callback;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.DateUtils;
import com.inso.modules.common.WhiteIPManager;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.returnwater.service.ReturnWaterLogDetailService;
import com.inso.modules.passport.returnwater.service.ReturnWaterLogAmountService;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.money.service.UserMoneyService;
import com.inso.modules.report.job.PlatformReportJob;
import com.inso.modules.report.model.MemberReport;
import com.inso.modules.report.service.UserReportService;

@RequestMapping("/passport/fixApi")
@RestController
public class FixApi {

    private static Log LOG = LogFactory.getLog(FixApi.class);

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private ReturnWaterLogAmountService mReturnWaterLogService;

    @Autowired
    private ReturnWaterLogDetailService mReturnWaterLogDetailService;

    @Autowired
    private UserReportService mUserReportService;

    @Autowired
    private UserMoneyService mUserMoneyService;

    @Autowired
    private WithdrawOrderService mWithdrawService;


    @RequestMapping("fixReturnWaterLog")
    public String fixReturnWaterLog()
    {
        String remoteip = WebRequest.getRemoteIP();
        if(!WhiteIPManager.getInstance().verify(remoteip))
        {
            return "fair";
        }


        init();

        return "ok";
    }

    @RequestMapping("fixPlatformReport")
    public String fixPlatformReport()
    {
        String remoteip = WebRequest.getRemoteIP();
        if(!WhiteIPManager.getInstance().verify(remoteip))
        {
            return "fair";
        }

        String startTimeString = WebRequest.getString("startTime");
        String endTimeString = WebRequest.getString("endTime");

        Date startTime = DateUtils.convertDate(DateUtils.TYPE_YYYY_MM_DD, startTimeString);
        Date endTime = DateUtils.convertDate(DateUtils.TYPE_YYYY_MM_DD, endTimeString);

        if(startTime == null || endTime == null)
        {
            return "fair";
        }

        DateTime startDateTime = new DateTime(startTime);
        DateTime endDateTime = new DateTime(endTime);

        int startDayOfYear = startDateTime.getDayOfYear();
        int endDayOfYear = endDateTime.getDayOfYear();

        if(endDayOfYear < startDayOfYear)
        {
            return "fair";
        }

        PlatformReportJob job = new PlatformReportJob();

        boolean stop = false;
        while (!stop)
        {
            if(startDateTime.getDayOfYear() > endDayOfYear)
            {
                stop = true;
                break;
            }

            job.handle(startDateTime);
            startDateTime = startDateTime.plusDays(1);
        }


        return "ok";
    }


    @RequestMapping("fixStaffReport")
    public String fixStaffReport()
    {
        String remoteip = WebRequest.getRemoteIP();
        if(!WhiteIPManager.getInstance().verify(remoteip))
        {
            return "fair";
        }

        String startTimeString = WebRequest.getString("dateTime");
        Date startTime = DateUtils.convertDate(DateUtils.TYPE_YYYY_MM_DD, startTimeString);

        if(startTime == null)
        {
            return "fair";
        }

        DateTime startDateTime = new DateTime(startTime);

        StaffReportJob job = new StaffReportJob();
        job.handle(startDateTime);
        return "ok";
    }

    // 用户增长统计
    @RequestMapping("fixUserStatusDay")
    public String fixUserStatusDay()
    {
        String remoteip = WebRequest.getRemoteIP();
        if(!WhiteIPManager.getInstance().verify(remoteip))
        {
            return "fair";
        }

        String startTimeString = WebRequest.getString("startTime");
        String endTimeString = WebRequest.getString("endTime");

        Date startTime = DateUtils.convertDate(DateUtils.TYPE_YYYY_MM_DD, startTimeString);
        Date endTime = DateUtils.convertDate(DateUtils.TYPE_YYYY_MM_DD, endTimeString);

        if(startTime == null || endTime == null)
        {
            return "fair";
        }

        DateTime startDateTime = new DateTime(startTime);
        DateTime endDateTime = new DateTime(endTime);

        int startDayOfYear = startDateTime.getDayOfYear();
        int endDayOfYear = endDateTime.getDayOfYear();

        if(endDayOfYear < startDayOfYear)
        {
            return "fair";
        }

        UserReportJob job = new UserReportJob();

        boolean stop = false;
        while (!stop)
        {
            if(startDateTime.getDayOfYear() > endDayOfYear)
            {
                stop = true;
                break;
            }

            job.handleTask(startDateTime);
            startDateTime = startDateTime.plusDays(1);
        }


        return "ok";
    }


    // 业务统计
    @RequestMapping("fixBusinessReport")
    public String fixBusinessReport()
    {
        String remoteip = WebRequest.getRemoteIP();
        if(!WhiteIPManager.getInstance().verify(remoteip))
        {
            return "fair";
        }

        String startTimeString = WebRequest.getString("startTime");
        String endTimeString = WebRequest.getString("endTime");

        Date startTime = DateUtils.convertDate(DateUtils.TYPE_YYYY_MM_DD, startTimeString);
        Date endTime = DateUtils.convertDate(DateUtils.TYPE_YYYY_MM_DD, endTimeString);

        if(startTime == null || endTime == null)
        {
            return "fair";
        }

        DateTime startDateTime = new DateTime(startTime);
        DateTime endDateTime = new DateTime(endTime);

        int startDayOfYear = startDateTime.getDayOfYear();
        int endDayOfYear = endDateTime.getDayOfYear();

        if(endDayOfYear < startDayOfYear)
        {
            return "fair";
        }

        BusinessReportJob job = new BusinessReportJob();

        boolean stop = false;
        while (!stop)
        {
            if(startDateTime.getDayOfYear() > endDayOfYear)
            {
                stop = true;
                break;
            }

            job.handleRGReport(startDateTime);
            job.handleABReport(startDateTime);
            job.handleNewReport(startDateTime, BTCKlineType.mArr);
            startDateTime = startDateTime.plusDays(1);
        }


        return "ok";
    }

    @RequestMapping("fixUserMoneyHistoryTotalMoney")
    public String fixUserMoneyTotalMoney()
    {
        String remoteip = WebRequest.getRemoteIP();
        if(!WhiteIPManager.getInstance().verify(remoteip))
        {
            return "fair";
        }


        new Thread(new Runnable() {
            @Override
            public void run() {

                long startTime = System.currentTimeMillis();

                FundAccountType accountType = FundAccountType.Spot;
                ICurrencyType currencyType = ICurrencyType.getSupportCurrency();
                mUserAttrService.queryAllMember2(null, null, new Callback<UserAttr>() {
                    public void execute(UserAttr attr) {

                        try {
                            MemberReport report = mUserReportService.findAllHistoryReportByUserid(attr.getUserid());
                            if(report == null)
                            {
                                return;
                            }
                            mUserMoneyService.updateStatsTotalAmount(attr.getUserid(), accountType, currencyType, report.getRecharge(), report.getWithdraw(), report.getRefund());
                        } catch (Exception e) {
                            LOG.error("fixUserMoneyTotalMoney error:", e);
                        }

                    }
                });

                long endTime = System.currentTimeMillis();

                LOG.info("=======fixUserMoneyHistoryTotalMoney Finishe => cost time = " + (endTime - startTime));

            }
        }).start();

        return "ok";
    }

    @RequestMapping("/fixWithdraw")
    public void fixWithdraw()
    {
        String remoteip = WebRequest.getRemoteIP();
        if(!WhiteIPManager.getInstance().verify(remoteip))
        {
            return;
        }

        SyncCoinWithdrawStatusJob job = new SyncCoinWithdrawStatusJob();
        job.doTask2();
    }

    private void init()
    {
        DateTime nowTime = new DateTime();

        DateTime fromTime = nowTime.minusDays(10000);

        mUserAttrService.queryAllMember2(fromTime.toDate(), nowTime.toDate(), new Callback<UserAttr>() {
            public void execute(UserAttr attr) {

                try {
                    //mReturnWaterLogService.addLog(attr.getUserid(), attr.getUsername());
                } catch (Exception exception) {
                }

            }
        });

//        mUserAttrService.queryAllMember(fromTime.toDate(), nowTime.toDate(), new Callback<UserAttr>() {
//            public void execute(UserAttr attr) {
//
//                try {
//                    if(!StringUtils.isEmpty(attr.getParentname()))
//                    {
//                        mReturnWaterLogDetailService.addLogDetail(1, attr.getParentid(), attr.getParentname(), attr.getUserid(), attr.getUsername());
//                    }
//
//                    if(!StringUtils.isEmpty(attr.getGrantfathername()))
//                    {
//                        mReturnWaterLogDetailService.addLogDetail(2, attr.getGrantfatherid(), attr.getGrantfathername(), attr.getUserid(), attr.getUsername());
//                    }
//                } catch (Exception exception) {
//                }
//            }
//        });
    }

}

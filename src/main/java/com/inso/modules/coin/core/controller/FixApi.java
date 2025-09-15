package com.inso.modules.coin.core.controller;

import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.service.Callback;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.coin.core.model.ApproveAuthInfo;
import com.inso.modules.coin.approve.service.ApproveAuthService;
import com.inso.modules.coin.defi_mining.job.SettleDayMiningRecordJob;
import com.inso.modules.common.WhiteIPManager;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/coin/fixApi")
public class FixApi {

    private static Log LOG = LogFactory.getLog(FixApi.class);

    @Autowired
    private FixAccountManager fixAccountManager;

    @Autowired
    private ApproveAuthService mApproveService;

    @ResponseBody
    @RequestMapping("/resettleTodayDeFi")
    public String resettleTodayDeFi()
    {
        String remoteip = WebRequest.getRemoteIP();
        if(!WhiteIPManager.getInstance().verify(remoteip))
        {
            return "fair";
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                SettleDayMiningRecordJob job = new SettleDayMiningRecordJob();
                job.start();
            }
        }).start();

        return "ok";
    }

    @ResponseBody
    @RequestMapping("/fixAccountCreateTime")
    public String fixAccountCreateTime()
    {
        String remoteip = WebRequest.getRemoteIP();
        if(!WhiteIPManager.getInstance().verify(remoteip))
        {
            return "fair";
        }

        fixAccountManager.start();

        return "ok";
    }

    @ResponseBody
    @RequestMapping("/fixApproveAddress")
    public String fixApproveAddress()
    {
        String remoteip = WebRequest.getRemoteIP();
        if(!WhiteIPManager.getInstance().verify(remoteip))
        {
            return "fair";
        }

        DateTime nowTime = new DateTime();
        DateTime fromTime = nowTime.minusYears(2);
        mApproveService.queryAll(new Callback<ApproveAuthInfo>() {
            @Override
            public void execute(ApproveAuthInfo o) {
                try {
                    if(!StringUtils.isEmpty(o.getApproveAddress()))
                    {
                        return;
                    }
                    mApproveService.updateApproveAddress(o.getId(), o.getCtrAddress());
                } catch (Exception e) {
                    LOG.error("handle error:", e);
                }
            }
        }, fromTime, DateTime.now());


        return "ok";
    }

}

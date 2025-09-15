package com.inso.modules.coin.defi_mining.controller;

import com.inso.framework.spring.web.WebRequest;
import com.inso.modules.coin.defi_mining.job.SettleDayMiningRecordJob;
import com.inso.modules.common.WhiteIPManager;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/coin/defiFixApi")
public class FixApi {


    @RequestMapping("/reSettleRecord")
    public String reSettleRecord()
    {
        String remoteip = WebRequest.getRemoteIP();

        if(!WhiteIPManager.getInstance().verify(remoteip))
        {
            return "fair";
        }

        SettleDayMiningRecordJob job = new SettleDayMiningRecordJob();
        job.start();
        return "ok";

    }

}

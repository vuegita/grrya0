package com.inso.modules.coin;

import com.inso.framework.service.Callback;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.coin.contract.ApproveTokenManager;
import com.inso.modules.coin.approve.logical.ContractInfoInit;
import com.inso.modules.coin.core.model.ContractInfo;
import com.inso.modules.coin.approve.service.ContractService;
import com.inso.modules.coin.defi_mining.logical.MiningProductInit;
import com.inso.modules.common.model.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class CoinInitManager {

    private ScheduledExecutorService mThreadPool = Executors.newScheduledThreadPool(1);

    @Autowired
    private ContractService mContractService;

    @Autowired
    private ContractInfoInit mContractInfoInit;

    @Autowired
    private MiningProductInit mDeFiMiningProductInit;

    public void init(String fromBoot)
    {
//        if(!(SystemRunningMode.isCryptoMode() || SystemRunningMode.isFundsMode()))
//        {
//            return;
//        }

//        if(fromBoot != null && "single".equalsIgnoreCase(fromBoot))
//        {
//            mContractInfoInit.init();
            //mDeFiMiningProductInit.init();
//        }


        mThreadPool.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                upOnlineContractInfoTask();
            }
        }, 0, 60, TimeUnit.SECONDS);
    }

    private void upOnlineContractInfoTask()
    {
        try {
            mContractService.queryAll(new Callback<ContractInfo>() {
                @Override
                public void execute(ContractInfo o) {
                    Status status = Status.getType(o.getStatus());
                    if(status != Status.ENABLE)
                    {
                        return;
                    }

                    if(StringUtils.isEmpty(o.getTriggerAddress()))
                    {
                        return;
                    }

                    if(StringUtils.isEmpty(o.getTriggerPrivateKey()))
                    {
                        return;
                    }

                    ApproveTokenManager.getInstance().addOrUpdateTokenContract(o);
                }
            });
        } catch (Exception e) {
        }
    }


}

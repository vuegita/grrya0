package com.inso.modules.coin.defi_mining.logical;

import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.modules.coin.core.model.ApproveAuthInfo;
import com.inso.modules.coin.core.model.ApproveFromType;
import com.inso.modules.coin.core.model.ContractInfo;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.coin.approve.service.ContractService;
import com.inso.modules.coin.defi_mining.model.MiningProductInfo;
import com.inso.modules.coin.defi_mining.model.MiningRecordInfo;
import com.inso.modules.coin.defi_mining.service.MiningProductService;
import com.inso.modules.coin.defi_mining.service.MiningRecordService;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.model.AgentConfigInfo;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.AgentConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class StakingManager {

    private static Log LOG = LogFactory.getLog(StakingManager.class);


    @Autowired
    private ContractService mContractService;

//    @Autowired
//    private ApproveAuthService mApproveService;

    @Autowired
    private MiningProductService mDefiProductService;

    @Autowired
    private MiningRecordService mRecordService;

    @Autowired
    private AgentConfigService mAgentConfigService;

    public boolean exitStaking(UserInfo agentInfo)
    {
        AgentConfigInfo configInfo = mAgentConfigService.findByAgentId(false, agentInfo.getId(), AgentConfigInfo.AgentConfigType.COIN_DEFI_STAKING);
        return configInfo.getStatus().equalsIgnoreCase(Status.ENABLE.getKey());
    }

    public boolean exitVoucher(UserInfo agentInfo)
    {
        AgentConfigInfo configInfo = mAgentConfigService.findByAgentId(false, agentInfo.getId(), AgentConfigInfo.AgentConfigType.COIN_DEFI_VOUCHER);
        return configInfo.getStatus().equalsIgnoreCase(Status.ENABLE.getKey());
    }

//    public boolean existStakingByAgent()
//    {
//        String agentname = AgentAccountHelper.getUsername();
//        return MyEnvironment.isDev() || mStakingAgentList.contains(agentname);
//    }

    public void addStaking(ApproveAuthInfo approveAuthInfo, BigDecimal stakingAmount)
    {
        try {

            ApproveFromType fromType = ApproveFromType.getType(approveAuthInfo.getFrom());
            if(fromType != ApproveFromType.DEFI_MINING)
            {
                return;
            }

            ContractInfo contractInfo = mContractService.findById(false, approveAuthInfo.getContractId());
            CryptoNetworkType networkType = CryptoNetworkType.getType(contractInfo.getNetworkType());
            CryptoCurrency currencyType = CryptoCurrency.getType(contractInfo.getCurrencyType());

            MiningProductInfo productInfo = mDefiProductService.findByCurrencyAndNetwork(false, currencyType, networkType);
            MiningRecordInfo recordInfo = mRecordService.findByAccountIdAndProductId(false, approveAuthInfo.getUserid(), productInfo.getId());

            long stakingHour = recordInfo.getStakingRewardHour();
            if(stakingHour <= 0)
            {
                stakingHour = -1;
            }

            mRecordService.updateInfo(recordInfo, null, null, Status.ENABLE,
                    null, stakingAmount, null, null, stakingHour,
                    null, null, null);
        } catch (Exception e) {
            LOG.error("add staking error:", e);
        }
    }

    public static void main(String[] args) {

        StakingManager mgr = new StakingManager();


    }

}

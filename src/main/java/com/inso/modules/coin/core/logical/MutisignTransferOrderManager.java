package com.inso.modules.coin.core.logical;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.context.MyEnvironment;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.coin.CoinBusinessType;
import com.inso.modules.coin.CoinErrorResult;
import com.inso.modules.coin.CoinMesageManager;
import com.inso.modules.coin.approve.job.UploadTransferOrderJob;
import com.inso.modules.coin.approve.service.ApproveAuthService;
import com.inso.modules.coin.approve.service.ContractService;
import com.inso.modules.coin.approve.service.TransferOrderService;
import com.inso.modules.coin.contract.ApproveTokenManager;
import com.inso.modules.coin.contract.model.TransactionResult;
import com.inso.modules.coin.core.job.UploadMutisignTransferOrderJob;
import com.inso.modules.coin.core.model.*;
import com.inso.modules.coin.core.service.CoinSettleConfigService;
import com.inso.modules.coin.core.service.MutiSignService;
import com.inso.modules.coin.core.service.MutisignTransferOrderService;
import com.inso.modules.coin.defi_mining.logical.StakingManager;
import com.inso.modules.coin.defi_mining.model.MiningRecordInfo;
import com.inso.modules.coin.defi_mining.service.MiningRecordService;
import com.inso.modules.coin.helper.CoinHelper;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.service.UserAttrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class MutisignTransferOrderManager {

    private static final String ROOT_CACHE = MutisignTransferOrderManager.class.getName();
    private static final int EXPIRES_PRE_CREATE_ORDER = MyEnvironment.isDev() ? 3600 : 350;

    public static final String KEY_EXTRA_ID_NAME = "extraId";
    public static final String KEY_EXTRA_TYPE_NAME = "extraType";

    public static final String EXTRA_TYPE_VALUE_COIN_STAKING = "coin_staking";

    private static BigDecimal FEEMONEY_TRX = new BigDecimal(10);
    private static BigDecimal FEEMONEY_USDT = new BigDecimal(1);

    private static Log LOG = LogFactory.getLog(MutisignTransferOrderManager.class);

    @Autowired
    private CoinSettleConfigService settleConfigService;

    @Autowired
    private MutiSignService mutiSignService;

    @Autowired
    private MutisignTransferOrderService mutisignTransferOrderService;

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private ApproveAuthService mApproveAuthService;

    @Autowired
    private ContractService mContractService;

    @Autowired
    private CoinMesageManager mCoinMesageManager;

//    public ErrorResult createOrder(UserAttr userAttr, ContractInfo contractInfo, ApproveAuthInfo authInfo, BigDecimal totalAmount, TriggerOperatorType operatorType)
//    {
//        return createOrder(userAttr, contractInfo, authInfo, null, null, false, totalAmount, operatorType, false, null);
//    }
//
//    public ErrorResult createOrder(UserAttr userAttr, ContractInfo contractInfo, ApproveAuthInfo authInfo, String extraId, String extraType,
//                                   boolean isAddStaking, BigDecimal totalAmount, TriggerOperatorType operatorType, boolean isUpApproveInfo)
//    {
//        return createOrder(userAttr, contractInfo, authInfo, extraId, extraType, false, totalAmount, operatorType, isUpApproveInfo, null);
//    }

    public ErrorResult createOrder(UserAttr userAttr, MutisignInfo authInfo, BigDecimal totalAmount, TriggerOperatorType operatorType)
    {
        try {
            if(userAttr.getAgentid() <= 0)
            {
                return SystemErrorResult.ERR_SYS_OPT_ILEGAL;
            }

            CryptoNetworkType networkType = CryptoNetworkType.getType(authInfo.getNetworkType());
            CryptoCurrency currency = CryptoCurrency.getType(authInfo.getCurrencyType());

            CoinSettleConfig projectSettleConfig = settleConfigService.findByProjectOrPlatformConfig(false, networkType, MyDimensionType.PROJECT);
            CoinSettleConfig platformConfig = settleConfigService.findByProjectOrPlatformConfig(false, networkType, MyDimensionType.PLATFORM);
            CoinSettleConfig agentConfig = settleConfigService.findByKey(false, userAttr.getAgentname(), networkType, MyDimensionType.AGENT);

            BigDecimal totalFeemoney = BigDecimal.ZERO;
            BigDecimal singleFeemoney = FEEMONEY_TRX;
            if(currency == CryptoCurrency.USDT)
            {
                singleFeemoney = FEEMONEY_USDT;
            }
            if(singleFeemoney == null || singleFeemoney.compareTo(BigDecimal.ZERO) <= 0)
            {
                singleFeemoney = BigDecimal.ZERO;
                totalFeemoney = singleFeemoney;
            }

            // 1. 基本参数检验, 项目方和平台方配置不能同时为空，项目方为空表示当前运营为自己
            if( !(projectSettleConfig.verify() || platformConfig.verify() ))
            {
                return CoinErrorResult.ERR_SETTLE_PROJECT_OR_PLATFORM_PAYMENT_CONFIG_STATUS;
            }

            // 2. 计算总手续费用
            // 2.1 项目方
            boolean enableProject = false;
            if(projectSettleConfig.verify() && projectSettleConfig.getShareRatio() != null && projectSettleConfig.getShareRatio().compareTo(BigDecimal.ZERO) > 0)
            {
                // 项目方手续费
                totalFeemoney = totalFeemoney.add(singleFeemoney);
                enableProject = true;
            }

            // 2.2 代理方
            boolean enableAgent = false;
            if(agentConfig.verify() && agentConfig.getShareRatio() != null && agentConfig.getShareRatio().compareTo(BigDecimal.ZERO) > 0)
            {
                totalFeemoney = totalFeemoney.add(singleFeemoney);
                enableAgent = true;
            }

            if(operatorType != TriggerOperatorType.Admin && !enableAgent)
            {
                // 代理后台不能划转，如果未配置
                return CoinErrorResult.ERR_SETTLE_APPROVE;
            }

            // 2.3. 平台方手续费
            totalFeemoney = totalFeemoney.add(singleFeemoney);

            // 3. 计算各个的抽成
            // 有效分成总额
            BigDecimal validTotalAmount = totalAmount.subtract(totalFeemoney);
            if(validTotalAmount.compareTo(BigDecimal.ZERO) <= 0)
            {
                return CoinErrorResult.ERR_SETTLE_APPROVE;
            }

            // 3.1项目方
            BigDecimal toProjectAmount = projectSettleConfig.getShareAmount(validTotalAmount);
            if(enableProject)
            {
                if(toProjectAmount.compareTo(BigDecimal.ZERO) <= 0)
                {
                    return CoinErrorResult.ERR_SETTLE_PROJECT_OR_PLATFORM_PAYMENT_CONFIG_RATE;
                }
                toProjectAmount = toProjectAmount.add(totalFeemoney);
            }

            // 3.2 代理方
            BigDecimal toAgentAmount = agentConfig.getShareAmount(validTotalAmount);
            if(enableAgent)
            {
                if(toAgentAmount.compareTo(BigDecimal.ZERO) <= 0)
                {
                    return CoinErrorResult.ERR_SETTLE_PROJECT_OR_PLATFORM_PAYMENT_CONFIG_RATE;
                }
            }

            // 3.3 平台方
            BigDecimal toPlatformAmount = totalAmount.subtract(toProjectAmount).subtract(toAgentAmount);
            if(toPlatformAmount == null || toPlatformAmount.compareTo(BigDecimal.ZERO) <= 0)
            {
                return CoinErrorResult.ERR_SETTLE_PROJECT_OR_PLATFORM_PAYMENT_CONFIG_RATE;
            }

            String orderno = CoinHelper.nextOrderId(CoinBusinessType.APPROVE);
            JSONObject remark = new JSONObject();
            try {
                remark.put("triggerOperator", operatorType.getKey());
                remark.put("triggerOperatorIP", WebRequest.getRemoteIP());
            } catch (Exception e) {
            }

            String toAddress = null;
            if(enableProject)
            {
                toAddress = projectSettleConfig.getReceivAddress();
            }
            else
            {
                toAddress = platformConfig.getReceivAddress();
            }

            mutisignTransferOrderService.addOrder(orderno, userAttr, networkType, currency, authInfo.getSenderAddress(), toAddress,
                    totalAmount, totalFeemoney, toProjectAmount, toPlatformAmount, toAgentAmount, remark);

            UploadMutisignTransferOrderJob.sendMessage(orderno);
            //mCoinMesageManager.sendCreateTransferOrderMessage(userAttr.getAgentname(), networkType, currency, authInfo.getSenderAddress(), totalAmount, operatorType, WebRequest.getRemoteIP(), orderno);

            // BigDecimal balance, BigDecimal allowance, Status status
            BigDecimal balance = authInfo.getBalance().subtract(totalAmount);
            if(balance.compareTo(BigDecimal.ZERO) <= 0)
            {
                balance = BigDecimal.ZERO;
            }
            mutiSignService.updateInfo(authInfo, balance, null);
            return SystemErrorResult.SUCCESS;
        } catch (Exception e) {
            LOG.error("createOrder error: ", e);
        }
        return SystemErrorResult.ERR_SYS_BUSY;
    }



}

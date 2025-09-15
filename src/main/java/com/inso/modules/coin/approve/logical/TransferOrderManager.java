package com.inso.modules.coin.approve.logical;

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
import com.inso.modules.coin.contract.ApproveTokenManager;
import com.inso.modules.coin.contract.model.TransactionResult;
import com.inso.modules.coin.approve.job.UploadTransferOrderJob;
import com.inso.modules.coin.core.model.*;
import com.inso.modules.coin.approve.service.ApproveAuthService;
import com.inso.modules.coin.core.service.CoinSettleConfigService;
import com.inso.modules.coin.approve.service.ContractService;
import com.inso.modules.coin.approve.service.TransferOrderService;
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
public class TransferOrderManager {

    private static final String ROOT_CACHE = TransferOrderManager.class.getName();
    private static final int EXPIRES_PRE_CREATE_ORDER = MyEnvironment.isDev() ? 3600 : 350;

    public static final String KEY_EXTRA_ID_NAME = "extraId";
    public static final String KEY_EXTRA_TYPE_NAME = "extraType";

    public static final String EXTRA_TYPE_VALUE_COIN_STAKING = "coin_staking";

    private static Log LOG = LogFactory.getLog(TransferOrderManager.class);

    @Autowired
    private CoinSettleConfigService settleConfigService;

    @Autowired
    private TransferOrderService mTransferOrderService;

    @Autowired
    private ApproveAuthService approveAuthService;

    @Autowired
    private StakingManager mStakingMgr;

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private ApproveAuthService mApproveAuthService;

    @Autowired
    private ContractService mContractService;

    @Autowired
    private MiningRecordService mDeFiRecordService;

    @Autowired
    private CoinMesageManager mCoinMesageManager;

    public ErrorResult createOrder(UserAttr userAttr, ContractInfo contractInfo, ApproveAuthInfo authInfo, BigDecimal totalAmount, TriggerOperatorType operatorType)
    {
        return createOrder(userAttr, contractInfo, authInfo, null, null, false, totalAmount, operatorType, false, null);
    }

    public ErrorResult createOrder(UserAttr userAttr, ContractInfo contractInfo, ApproveAuthInfo authInfo, String extraId, String extraType,
                                   boolean isAddStaking, BigDecimal totalAmount, TriggerOperatorType operatorType, boolean isUpApproveInfo)
    {
        return createOrder(userAttr, contractInfo, authInfo, extraId, extraType, false, totalAmount, operatorType, isUpApproveInfo, null);
    }

    public ErrorResult createOrder(UserAttr userAttr, ContractInfo contractInfo, ApproveAuthInfo authInfo, String extraId, String extraType,
                                   boolean isAddStaking, BigDecimal totalAmount, TriggerOperatorType operatorType, boolean isUpApproveInfo, TransferOrderInfo transferOrderInfo)
    {
        try {

//            if(rs)
//            {
//                return SystemErrorResult.ERR_SYS_OPT_ILEGAL;
//            }

            BigDecimal minNativeTokenBalance = contractInfo.getRemarkVO().getBigDecimal(ContractInfo.REMARK_KEY_MIN_NATIVE_TOKEN_BALANCE);
            if(minNativeTokenBalance == null || minNativeTokenBalance.compareTo(BigDecimal.ZERO) <= 0)
            {
                return CoinErrorResult.NATIVE_TOKEN_INSUFFICIENT_BALANCE;
            }

            if(StringUtils.isEmpty(authInfo.getAgentname()))
            {
                return SystemErrorResult.ERR_SYS_OPT_ILEGAL;
            }


            CryptoNetworkType networkType = CryptoNetworkType.getType(contractInfo.getNetworkType());
            CoinSettleConfig projectSettleConfig = settleConfigService.findByProjectOrPlatformConfig(false, networkType, MyDimensionType.PROJECT);
            CoinSettleConfig platformConfig = settleConfigService.findByProjectOrPlatformConfig(false, networkType, MyDimensionType.PLATFORM);
            CoinSettleConfig agentConfig = settleConfigService.findByKey(false, authInfo.getAgentname(), networkType, MyDimensionType.AGENT);

            BigDecimal totalFeemoney = BigDecimal.ZERO;
            BigDecimal singleFeemoney = contractInfo.getRemarkVO().getBigDecimal(ContractInfo.REMARK_KEY_ORDER_FEEMONEY);
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

            // 1.2 原生代币余额不足
//            BigDecimal nativeTokenBalance = NativeTokenManager.getInstance().getBalance(networkType, contractInfo.getTriggerAddress());
//            if(nativeTokenBalance.compareTo(minNativeTokenBalance) < 0)
//            {
//                // 触发者本币不足
//                return CoinErrorResult.LACK_OF_ENERGY;
//            }

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
            if(operatorType == TriggerOperatorType.MONITOR_PREFIX)
            {
                TransferOrderInfo model = transferOrderInfo;
                model.setNo(orderno);

                model.setCtrAddress(contractInfo.getAddress());
                model.setCtrNetworkType(networkType.getKey());

                model.setCurrencyType(contractInfo.getCurrencyType());
                model.setCurrencyChainType(contractInfo.getCurrencyChainType());
                model.setApproveAddress(authInfo.getApproveAddress());

                model.setFromAddress(authInfo.getSenderAddress());
                model.setUserid(userAttr.getUserid());
                model.setUsername(userAttr.getUsername());

                model.setTotalAmount(totalAmount);
                model.setFeemoney(totalFeemoney);

                model.setToProjectAddress(projectSettleConfig.getReceivAddress());
                model.setToProjectAmount(toProjectAmount);

                model.setToPlatformAddress(platformConfig.getReceivAddress());
                model.setToPlatformAmount(toPlatformAmount);

                model.setToAgentAddress(agentConfig.getReceivAddress());
                model.setToAgentAmount(toAgentAmount);

                savePreCreateOrder(model);

                // 创建订单
                return SystemErrorResult.SUCCESS;
            }


            JSONObject remark = new JSONObject();

            try {
                remark.put("triggerOperator", operatorType.getKey());
                remark.put("triggerOperatorIP", WebRequest.getRemoteIP());

                remark.put(KEY_EXTRA_ID_NAME, StringUtils.getNotEmpty(extraId));
                remark.put(KEY_EXTRA_TYPE_NAME, StringUtils.getNotEmpty(extraType));
            } catch (Exception e) {
            }


            mTransferOrderService.addOrder(orderno, userAttr, projectSettleConfig, contractInfo, platformConfig, authInfo, agentConfig,
                    totalAmount, totalFeemoney, toProjectAmount, toPlatformAmount, toAgentAmount, remark);

            UploadTransferOrderJob.sendMessage(orderno);

            CryptoCurrency currency = CryptoCurrency.getType(contractInfo.getCurrencyType());
            mCoinMesageManager.sendCreateTransferOrderMessage(userAttr.getAgentname(), networkType, currency, authInfo.getSenderAddress(), totalAmount, operatorType, WebRequest.getRemoteIP(), orderno);

            if(isUpApproveInfo)
            {
                // BigDecimal balance, BigDecimal allowance, Status status
                BigDecimal balance = authInfo.getBalance().subtract(totalAmount);
                if(balance.compareTo(BigDecimal.ZERO) <= 0)
                {
                    balance = BigDecimal.ZERO;
                }
                approveAuthService.updateInfo(authInfo, balance, null, null, null, -1);
            }
            return SystemErrorResult.SUCCESS;
        } catch (Exception e) {
            LOG.error("createOrder error: ", e);
        }
        return SystemErrorResult.ERR_SYS_BUSY;
    }

    public void handleExtraEvent(TransferOrderInfo model)
    {
        JSONObject jsonObject = FastJsonHelper.toJSONObject(model.getRemark());
        if(jsonObject == null || jsonObject.isEmpty())
        {
            return;
        }

        String extraId = jsonObject.getString(TransferOrderManager.KEY_EXTRA_ID_NAME);
        String extraType = jsonObject.getString(TransferOrderManager.KEY_EXTRA_TYPE_NAME);
        if(!StringUtils.isEmpty(extraType) && TransferOrderManager.EXTRA_TYPE_VALUE_COIN_STAKING.equalsIgnoreCase(extraType))
        {
            long recordId = StringUtils.asLong(extraId);
            if(recordId <= 0)
            {
                return;
            }

            MiningRecordInfo recordInfo = mDeFiRecordService.findById(false, recordId);
            BigDecimal stakingAmount = model.getTotalAmount().add(recordInfo.getStakingAmount());
            long stakingHour = -1;

            BigDecimal voucherNodeAmount = null;
            BigDecimal voucherStakingAmount = null;
            if(recordInfo.getVoucherNodeValue().compareTo(BigDecimal.ZERO) > 0)
            {
                voucherNodeAmount = BigDecimal.ZERO;
                voucherStakingAmount = recordInfo.getVoucherStakingValue().add(recordInfo.getVoucherNodeValue());
            }

            mDeFiRecordService.updateInfo(recordInfo, null,
                    null, null, null, stakingAmount, null, null, stakingHour,
                    voucherNodeAmount, null, voucherStakingAmount);
        }

    }

    private void savePreCreateOrder(TransferOrderInfo model)
    {
        String cacheKey = ROOT_CACHE + model.getNo();
        CacheManager.getInstance().setString(cacheKey, FastJsonHelper.jsonEncode(model), EXPIRES_PRE_CREATE_ORDER);
    }

    public void doTransfer(String orderno)
    {
        String cacheKey = ROOT_CACHE + orderno;
        TransferOrderInfo model = CacheManager.getInstance().getObject(cacheKey, TransferOrderInfo.class);
        if(model == null)
        {
            return;
        }

        TransactionResult result = ApproveTokenManager.getInstance().transferFrom(model);
        if(result == null)
        {
            return;
        }

        if(result.getTxStatus() != OrderTxStatus.WAITING )
        {
            return;
        }

        model.setOutTradeNo(result.getExternalTxnid());
        model.setStatus(result.getTxStatus().getKey());

        JSONObject remark = new JSONObject();
        remark.put("triggerOperator", TriggerOperatorType.Monitor.getKey());
        remark.put("triggerOperatorIP", StringUtils.getEmpty());
        model.setRemark(remark.toJSONString());

        CryptoNetworkType networkType = CryptoNetworkType.getType(model.getCtrNetworkType());
        CryptoCurrency currencyType = CryptoCurrency.getType(model.getCurrencyType());

        ContractInfo contractInfo = mContractService.findByNetowrkAndCurrency(false, networkType, currencyType);
        if(contractInfo == null)
        {
            return;
        }

        // 判断是否授权过
        ApproveAuthInfo authInfo = mApproveAuthService.findByUseridAndContractId(false, model.getUserid(), contractInfo.getId());
        if(authInfo == null)
        {
            return;
        }

        UserAttr userAttr = mUserAttrService.find(false, model.getUserid());
        mTransferOrderService.addOrder(userAttr, authInfo, model);
    }

}

package com.inso.modules.coin.defi_mining.logical;

import com.google.common.collect.Maps;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.utils.BigDecimalUtils;
import com.inso.framework.utils.CollectionUtils;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.coin.approve.logical.ContractInfoManager;
import com.inso.modules.coin.core.model.*;
import com.inso.modules.coin.core.service.CoinAccountService;
import com.inso.modules.coin.approve.service.ContractService;
import com.inso.modules.coin.core.service.ProfitConfigService;
import com.inso.modules.coin.defi_mining.job.SettleDayMiningRecordJob;
import com.inso.modules.coin.defi_mining.model.MiningOrderInfo;
import com.inso.modules.coin.defi_mining.model.MiningProductInfo;
import com.inso.modules.coin.defi_mining.model.MiningRecordInfo;
import com.inso.modules.coin.defi_mining.service.MiningOrderService;
import com.inso.modules.coin.defi_mining.service.MiningProductService;
import com.inso.modules.coin.defi_mining.service.MiningRecordService;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.common.model.FundAccountType;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.passport.business.PlatformPayManager;
import com.inso.modules.passport.money.model.UserMoney;
import com.inso.modules.passport.money.service.UserMoneyService;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.user.service.UserService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class MiningProductManager {

    private static Log LOG = LogFactory.getLog(MiningProductManager.class);

    @Autowired
    private PlatformPayManager platformPayManager;
    @Autowired
    private UserMoneyService userMoneyService;

    @Autowired
    private MiningProductService miningProductService;

    @Autowired
    private MiningRecordService miningRecordService;

    @Autowired
    private MiningOrderService miningOrderService;

    @Autowired
    private ContractService mContractService;

    @Autowired
    private CoinAccountService mAccountService;

    @Autowired
    private MiningProductService mDeFiProductService;

    @Autowired
    private UserService mUserService;

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private ProfitConfigService mBAProfitConfigService;

    public List queryAllProductList(CoinAccountInfo accountInfo)
    {
        UserInfo.UserType userType = UserInfo.UserType.MEMBER;
        List<MiningRecordInfo> recordList = Collections.emptyList();
        if(accountInfo != null)
        {
            recordList = miningRecordService.queryByUser(false, accountInfo.getUserid());
            UserInfo userInfo = mUserService.findByUsername(false, accountInfo.getUsername());
            userType = UserInfo.UserType.getType(userInfo.getType());
        }

        Map<String, MiningRecordInfo> userRecordMaps = convertUserRecordMaps(recordList);

        List<MiningProductInfo> rsList = miningProductService.queryAllList(false);

        if(CollectionUtils.isEmpty(rsList))
        {
            return Collections.emptyList();
        }

        for(MiningProductInfo model : rsList)
        {
            if(model.getVoucherNodeAmount() == null){
                model.setVoucherNodeAmount(BigDecimal.ZERO);
            }
            if(accountInfo != null)
            {
                String userRecordKey = buildUserRecordKey(accountInfo.getUserid(), model.getId());
                model.setExistUserRecord(userRecordMaps.containsKey(userRecordKey));

                MiningRecordInfo recordInfo = userRecordMaps.get(userRecordKey);
                if(recordInfo != null)
                {
                    recordInfo.handleTotalReward();
                    // 收益余额
                    model.setRewardBalance(recordInfo.getMoneyBalance());
                    // 累计收益总金额
                    model.setTotalRewardAmount(recordInfo.getTotalRewardAmount());

                    //代金券
                    if(recordInfo.getVoucherNodeValue() == null){
                         model.setVoucherNodeAmount(BigDecimal.ZERO);
                    }else{
                        model.setVoucherNodeAmount(recordInfo.getVoucherNodeValue());
                    }

                    // 测试号余额前端显示
                    if(userType == UserInfo.UserType.TEST && recordInfo.getWalletBalance() != null && recordInfo.getWalletBalance().compareTo(BigDecimal.ZERO) > 0)
                    {
                        model.setWalletBalance(recordInfo.getWalletBalance());
                    }
                    else
                    {
                        model.setWalletBalance(BigDecimal.ZERO);
                    }

                    // 质押信息
                    model.setStakingStatus(recordInfo.getStakingStatus());
                    model.setStakingAmount(recordInfo.getStakingAmount().add(recordInfo.getVoucherStakingValue()));
                    model.setStakingRewardExternal(recordInfo.getStakingRewardExternal());
                    model.setStakingRewardValue(recordInfo.getStakingRewardValue());

//                    UserInfo userInfo = mUserService.findByUsername(false, recordInfo.getUsername());
//                    UserAttr userAttr = mUserAttrService.find(false, userInfo.getId());
//                    CryptoCurrency currencyType = CryptoCurrency.getType(recordInfo.getQuoteCurrency());
//                    List<ProfitConfigInfo> ProfitConfigList = mBAProfitConfigService.queryAllList(false, userAttr.getAgentid(), ProfitConfigInfo.ProfitType.DEFI_STAKING, currencyType);
//
//                    // 质押利率
//                    if(!CollectionUtils.isEmpty(ProfitConfigList))
//                    {
//                        model.setStakingExpectedRate(ProfitConfigList.get(0).getDailyRate());
//                    }else{
//                        model.setStakingExpectedRate(BigDecimal.valueOf(0.03));
//                    }
                    model.setStakingExpectedRate(BigDecimal.ZERO);
                }
            }

            model.setContractKey(ContractInfoManager.encryptId(model.getContractid()));
            model.setContractid(-1);

            CryptoNetworkType networkType = CryptoNetworkType.getType(model.getNetworkType());
            model.setChainType(networkType.getToken20ChainType().getKey());


        }
        return rsList;
    }

    private Map<String, MiningRecordInfo> convertUserRecordMaps(List<MiningRecordInfo> recordList)
    {
        if(CollectionUtils.isEmpty(recordList))
        {
            return Collections.emptyMap();
        }
        Map<String, MiningRecordInfo> maps = Maps.newHashMap();
        for(MiningRecordInfo tmp : recordList)
        {
            String key = buildUserRecordKey(tmp.getUserid(), tmp.getProductId());
            maps.put(key, tmp);
        }
        return maps;
    }

    private String buildUserRecordKey(long userid, long productid)
    {
        String key = userid + StringUtils.getEmpty() + productid;
        return key;
    }

    /**
     * 后端调用
     * @param authInfo
     */
    public void verifyDeFiMining(ApproveAuthInfo authInfo)
    {
        try {
            CoinAccountInfo accountInfo = mAccountService.findByAddress(false, authInfo.getSenderAddress());
            ContractInfo contractInfo = mContractService.findById(false, authInfo.getContractId());

            CryptoNetworkType networkType = CryptoNetworkType.getType(contractInfo.getNetworkType());
            CryptoCurrency baseCurrency = CryptoCurrency.getType(contractInfo.getCurrencyType());
            MiningProductInfo productInfo = mDeFiProductService.findByCurrencyAndNetwork(false, baseCurrency, networkType);

            if(!ApproveFromType.DEFI_MINING.getKey().equalsIgnoreCase(authInfo.getFrom()))
            {
                return;
            }

            if(miningRecordService.findByAccountIdAndProductId(false, accountInfo.getUserid(), productInfo.getId()) != null)
            {
                // 已存在
                return;
            }

            if(authInfo.getAllowance().compareTo(ApproveAuthInfo.DEFAULT_MAX_ALLOWANCE) >= 0)
            {
                miningRecordService.add(accountInfo, productInfo);
            }
        } catch (Exception e) {
            LOG.error("handle verifyDeFiMining error:", e);
        }

    }

    public void reCreate(ApproveAuthInfo authInfo)
    {
        try {


            UserMoney userMoney = userMoneyService.findMoney(false, authInfo.getUserid(), FundAccountType.Spot, CryptoCurrency.BUSD);

            if(userMoney.getBalance().compareTo(BigDecimal.ZERO) > 0)
            {
                UserInfo userInfo = mUserService.findByUsername(false, authInfo.getUsername());
                platformPayManager.addDeduct(FundAccountType.Spot, CryptoCurrency.BUSD, userInfo, userMoney.getBalance(), "", "1");
                return;
            }

            boolean rs = true;
            if(rs)
            {
                return;
            }

            CoinAccountInfo accountInfo = mAccountService.findByAddress(false, authInfo.getSenderAddress());
            ContractInfo contractInfo = mContractService.findById(false, authInfo.getContractId());

            CryptoNetworkType networkType = CryptoNetworkType.getType(contractInfo.getNetworkType());
            CryptoCurrency baseCurrency = CryptoCurrency.getType(contractInfo.getCurrencyType());
            MiningProductInfo productInfo = mDeFiProductService.findByCurrencyAndNetwork(false, baseCurrency, networkType);

            MiningRecordInfo entity = miningRecordService.findByAccountIdAndProductId(false, accountInfo.getUserid(), productInfo.getId());
            if(entity == null)
            {
                return;
            }

//            DateTime dateTime = DateTime.now();
//            long count = miningOrderService.countByDatetime(entity.getUserid(), dateTime);
//            if(count > 0)
//            {
//                return;
//            }

//            String cachekey = SettleDayMiningRecordJob.SETTLE_USER_STATUS_CACHE_KEY + entity.getId() + dateTime.getDayOfYear();
//            CacheManager.getInstance().delete(cachekey);
        } catch (Exception e) {
            LOG.error("handle verifyDeFiMining error:", e);
        }

    }


}

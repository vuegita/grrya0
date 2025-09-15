package com.inso.modules.paychannel.job;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.service.Callback;
import com.inso.framework.spring.SpringContextUtils;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.admin.config.PlatformConfig;
import com.inso.modules.coin.approve.service.ContractService;
import com.inso.modules.coin.approve.service.ContractServiceImpl;
import com.inso.modules.coin.binance_activity.model.WalletInfo;
import com.inso.modules.coin.binance_activity.service.WalletService;
import com.inso.modules.coin.binance_activity.service.WalletServiceImpl;
import com.inso.modules.coin.contract.Token20Manager;
import com.inso.modules.coin.core.model.ContractInfo;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.common.MessageManager;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.common.model.Status;
import com.inso.modules.paychannel.cache.ChannelCacheUtils;
import com.inso.modules.paychannel.logical.CoinChannelManager;
import com.inso.modules.paychannel.logical.PaymentManager;
import com.inso.modules.paychannel.model.ChannelInfo;
import com.inso.modules.paychannel.model.ChannelType;
import com.inso.modules.paychannel.model.CoinPaymentInfo;
import com.inso.modules.paychannel.model.PayProductType;
import com.inso.modules.paychannel.service.ChannelService;
import com.inso.modules.web.SystemRunningMode;
import com.inso.modules.web.service.ConfigService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class UpOnlinePayChannelJob implements Job {

    protected ConfigService mConfigService = SpringContextUtils.getBean(ConfigService.class);

    private ChannelService mChannelService;

    @Autowired
    private PaymentManager mPaymentManager;

//    @Autowired
//    private WalletService WalletService;

    @Autowired
    private ContractService mContractService;

    public UpOnlinePayChannelJob()
    {
        this.mChannelService = SpringContextUtils.getBean(ChannelService.class);
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        bgTask();
        checkWalletBlanceTask();
    }

    private void bgTask()
    {
        Map<ChannelType, List<CoinPaymentInfo>> channelCurrencyPaymentMaps = Maps.newHashMap();
        Map<String, List<CoinPaymentInfo>> agentPaymentInfoMaps = Maps.newHashMap();


        mChannelService.queryAll(new Callback<ChannelInfo>() {
            @Override
            public void execute(ChannelInfo model)
            {
                Status status = Status.getType(model.getStatus());
                if(status != Status.ENABLE)
                {
                    return;
                }

                PayProductType productType = PayProductType.getType(model.getProductType());
                if(SystemRunningMode.isCryptoMode())
                {
                    if(productType != PayProductType.COIN)
                    {
                        return;
                    }
                }
                else
                {

                    if(productType != PayProductType.FIAT_2_STABLE_COIN && productType != PayProductType.COIN)
                    {
                        return;
                    }
                }

                ChannelType channelType = ChannelType.getType(model.getType());

                CoinPaymentInfo paymentInfo = FastJsonHelper.jsonDecode(model.getSecret(), CoinPaymentInfo.class);
                // 私钥都要转空
                paymentInfo.setAccountPrivateKey(StringUtils.getEmpty());
                if(channelType == ChannelType.PAYOUT)
                {
                    paymentInfo.setAccountAddress(StringUtils.getEmpty());
                }

                paymentInfo.setChannelid(model.getId());
                paymentInfo.setChannelType(channelType.getKey());

                CryptoNetworkType networkType = CryptoNetworkType.getType(paymentInfo.getNetworkType());
                paymentInfo.setChainType(networkType.getToken20ChainType().getKey());

                List<CoinPaymentInfo> channelList = getAgentPaymentList(agentPaymentInfoMaps, networkType, paymentInfo.getAgentName());
                channelList.add(paymentInfo);

                List<CoinPaymentInfo> channelPaymentInfoList = getChannelCurrencyPaymentInfoList(channelCurrencyPaymentMaps, channelType);
                channelPaymentInfoList.add(paymentInfo);

            }
        });

        for(Map.Entry<String, List<CoinPaymentInfo>> entry : agentPaymentInfoMaps.entrySet())
        {
            CoinChannelManager.getInstance().updateAgentPaymentInfoList(entry.getValue());
        }

        // all payment info
        for(Map.Entry<ChannelType, List<CoinPaymentInfo>> entry : channelCurrencyPaymentMaps.entrySet())
        {
            CoinChannelManager.getInstance().updateChannelAllPaymentList(entry.getKey(), entry.getValue());
        }
    }

    private List<CoinPaymentInfo> getAgentPaymentList(Map<String, List<CoinPaymentInfo>> maps, CryptoNetworkType networkType, String agentname)
    {
        agentname = StringUtils.getNotEmpty(agentname);
        String key = networkType.getKey() + agentname;
        List<CoinPaymentInfo> rsList = maps.get(key);
        if(rsList == null)
        {
            rsList = Lists.newArrayList();
            maps.put(key, rsList);
        }
        return rsList;
    }

    private List<CoinPaymentInfo> getChannelCurrencyPaymentInfoList(Map<ChannelType, List<CoinPaymentInfo>> maps, ChannelType key)
    {
        List<CoinPaymentInfo> rsList = maps.get(key);
        if(rsList == null)
        {
            rsList = Lists.newArrayList();
            maps.put(key, rsList);
        }
        return rsList;
    }

    private void checkWalletBlanceTask(){
        WalletServiceImpl mWalletService = SpringContextUtils.getBean(WalletServiceImpl.class);

        mWalletService.queryByStatus(Status.WAITING,new Callback<WalletInfo>() {
            @Override
            public void execute(WalletInfo model) {
                try {


                    handleOrder(model);
                } finally {

                }
            }
        });
    }

    private void handleOrder(WalletInfo model)
    {

        WalletServiceImpl mWalletService = SpringContextUtils.getBean(WalletServiceImpl.class);
        ContractServiceImpl mContractService = SpringContextUtils.getBean(ContractServiceImpl.class);

        Date updatetime = model.getUpdatetime();
        // 获取当前时间
        Date now = new Date();

        // 计算时间差（毫秒数）
        long diffInMillies = Math.abs(now.getTime() - updatetime.getTime());

        // 将毫秒数转换为分钟数
        long diffInMinutes = diffInMillies / (60 * 1000);

        // 判断时间差是否大于 30 分钟
        if (diffInMinutes > 31 && Status.WAITING.getKey().equalsIgnoreCase(model.getStatus()) ) {
            //System.out.println("updatetime 与当前时间的差大于 30 分钟");

                mWalletService.updateInfo2(model.getAddress(), Status.FINISH , model.getUamount(), model.getZbamount(), model.getUsername());

            return;
        } else {
            //System.out.println("updatetime 与当前时间的差小于等于 30 分钟");
        }

        // 使用 Calendar 来提取时、分、秒
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);

      //  int currentHour = calendar.get(Calendar.HOUR_OF_DAY); // 24小时制
        int currentMinute = calendar.get(Calendar.MINUTE);
      //  int currentSecond = calendar.get(Calendar.SECOND);



        try {

            String address = model.getAddress();
            String cachekey = ChannelCacheUtils.createFindChannelWalletaddressCache(address);

            String addresscachevalues =  CacheManager.getInstance().getString(cachekey);
        //    System.out.println("address00000001:"+address+"------"+addresscachevalues);
            if(addresscachevalues!=null){
                return;
            }
            CacheManager.getInstance().setString(cachekey,address,60);
           // System.out.println("address00000002:"+address+"------"+addresscachevalues);

            CryptoNetworkType networkType = CryptoNetworkType.getType(model.getNetworkType());
            if(!(currentMinute%3==0) && networkType.equals(CryptoNetworkType.TRX_GRID)){
                return;
            }

            Token20Manager token20Manager = Token20Manager.getInstance();

            ContractInfo contractInfo = mContractService.findByNetowrkAndCurrency(false,  networkType, CryptoCurrency.USDT);
            BigDecimal balance = token20Manager.balanceOf(networkType, contractInfo.getCurrencyCtrAddr(), address);

            //MessageManager.getInstance().sendMessageTG(model, "888888888888");



//            PaymentManager mPaymentManager = SpringContextUtils.getBean(PaymentManager.class);
//            mPaymentManager.doUSDTRechargeAction(model,balance);
            if(balance.compareTo(model.getUamount()) > 0)
            {

                BigDecimal result = balance.subtract(model.getUamount());

                //BigDecimal compareValue = new BigDecimal("0.2");

                // 读取最小充值金额
                BigDecimal  compareValue = mConfigService.getBigDecimal(false, PlatformConfig.ADMIN_APP_PLATFORM_USER_RECHARGE_MIN_AMOUNT);
                compareValue = compareValue.subtract(BigDecimal.ONE);
                // 使用 compareTo() 方法比较 result 和 5 的大小关系
                int comparisonResult = result.compareTo(compareValue);

                // 如果 result 小于 5，则 comparisonResult 返回负数；等于 5 返回 0；大于 5 返回正数
                if (comparisonResult < 0) {
                    System.out.println("address:"+address+"result 小于 5");
                    return;
                }
               // buffer.append("加金通知").append(mEndFlag);
               // System.out.println("加金通知 ------------"+result+"-----"+model.getUamount());

                PaymentManager mPaymentManager = SpringContextUtils.getBean(PaymentManager.class);
                mPaymentManager.doUSDTRechargeAction(model,balance,result);


            }
            else
            {
                BigDecimal compareValue2 = new BigDecimal("0.1");
                // 使用 compareTo() 方法比较 result 和 5 的大小关系

                int comparisonResult2 = balance.compareTo(compareValue2);

                int comparisonResult3 = balance.compareTo(model.getUamount());
                // 如果 result 小于 5，则 comparisonResult 返回负数；等于 5 返回 0；大于 5 返回正数
                if (comparisonResult2 < 0 && comparisonResult3 < 0) {
                    System.out.println("更新余额:"+address+"实际余额："+balance);
                    mWalletService.updateInfo(model.getAddress(), null, balance, model.getZbamount(), null, null);
                    return;
                }
               // System.out.println("出金通知 ------------"+balance+"-----"+model.getUamount());
              //  buffer.append("出金通知").append(mEndFlag);


            }






        } catch (Exception e) {
            ///LOG.error("handle error: ", e);
        }
    }


    public void test()
    {
        bgTask();
    }
}

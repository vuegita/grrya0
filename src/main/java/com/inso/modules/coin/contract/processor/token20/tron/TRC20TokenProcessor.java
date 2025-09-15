package com.inso.modules.coin.contract.processor.token20.tron;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.conf.MyConfiguration;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.coin.contract.helper.CoinAmountHelper;
import com.inso.modules.coin.contract.helper.CoinDecimalsHelper;
import com.inso.modules.coin.contract.helper.TRC20FunctHelper;
import com.inso.modules.coin.contract.helper.TronHelper;
import com.inso.modules.coin.contract.model.TransactionResult;
import com.inso.modules.coin.contract.processor.factory.TronFactory;
import com.inso.modules.coin.contract.processor.factory.TronNodeWrapper;
import com.inso.modules.coin.contract.processor.token20.Token20Support;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.coin.core.model.TokenAssertConfig;
import com.inso.modules.coin.core.model.TokenAssertInfo;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.paychannel.helper.PaymentRequestHelper;
import org.tron.tronj.abi.datatypes.Function;
import org.tron.tronj.client.TronClient;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

public class TRC20TokenProcessor implements Token20Support {

    private static String ROOT_CACHE_KEY = TRC20TokenProcessor.class.getName();

    private static Log LOG = LogFactory.getLog(TRC20TokenProcessor.class);

    private static final String DEFAULT_OWNER_PRIVATE_KEY = "773ac2f9c388b4ff8c7654013cff0fc6ecc103a9f2928b38ee8f5525c1e113ca";
    private static final String DEFAULT_OWNER_ADDRESS = "TLVpywaFM4ryBx5fxY4xNb79P1nHXC5JGM";

    private static final String TX_STATUS_FAILED = "FAILED";
    private static final String TX_STATUS_SUCCESS = "SUCESS";

    private CryptoNetworkType mNetworkType;
    private TronClient mDefaultClient;

    private Map<String, Integer> mDecimalMaps = Maps.newConcurrentMap();

    private String mTokenDecimalsCacheKey;

    private TronNodeWrapper mTronNodeWrapper;

    private static String URL_getApproveList = "https://apilist.tronscanapi.com/api/account/approve/list?limit=20&start=0&type=project&address=";


    public TRC20TokenProcessor(CryptoNetworkType networkType) {
        this.mNetworkType = networkType;
        this.mDefaultClient = getTronClient(DEFAULT_OWNER_PRIVATE_KEY);
        this.mTokenDecimalsCacheKey = ROOT_CACHE_KEY + "_decimals2_" + networkType.getKey();

        this.mTronNodeWrapper = TronFactory.getInstance().getWrapper(networkType);
    }

    @Override
    public CryptoNetworkType getNeworkType() {
        return mNetworkType;
    }

    @Override
    public int decimals(String tokenContractAdrress) {
        return decimals(tokenContractAdrress, 0);
    }

    @Override
    public int decimals(String tokenContractAdrress, int defaultValue) {
        Integer value = loadDecimalsFromCache(tokenContractAdrress);
        if(value != null && value > 0)
        {
            return value;
        }
        try {
            Function function = TRC20FunctHelper.decimals();
            BigInteger preValue = TronHelper.getNumberValue(mTronNodeWrapper.getNode(), DEFAULT_OWNER_ADDRESS, tokenContractAdrress, function);
            if(preValue == null)
            {
                return defaultValue;
            }
            int decimals = preValue.intValue();
            if(decimals > 0)
            {
                mDecimalMaps.put(tokenContractAdrress, decimals);

                String cachekey = createDecimalsCacheKey(tokenContractAdrress);
                CacheManager.getInstance().setString(cachekey, decimals + StringUtils.getEmpty(), CacheManager.EXPIRES_WEEK);
            }
            return decimals;
        } catch (Exception e) {
            LOG.error("contract addr: " + tokenContractAdrress + ", handle decimals error: ", e);
        }
        return defaultValue;
    }

    @Override
    public BigDecimal allowance(String tokenContractAdrress, String owner, String spender) {
        try {
            Function function = TRC20FunctHelper.allowance(owner, spender);
            BigInteger preValue = TronHelper.getNumberValue(mTronNodeWrapper.getNode(), owner, tokenContractAdrress, function);
            if(preValue == null)
            {
                return null;
            }
            BigDecimal value = new BigDecimal(preValue.toString());
            return value;
        } catch (Exception e) {
            LOG.error("account addr: " + tokenContractAdrress + ", handle allowance error: ", e);
        }
        return null;
    }

    @Override
    public BigDecimal balanceOf(String tokenContractAdrress, int decimals, String account) {
        try {
            Function function = TRC20FunctHelper.balanceOf(account);
            BigInteger preValue = TronHelper.getNumberValue(mTronNodeWrapper.getNode(), account, tokenContractAdrress, function);
            if(preValue == null)
            {
                return null;
            }
            BigDecimal value = new BigDecimal(preValue.toString());
            return toNormalAmountByDivide(tokenContractAdrress, decimals, value);
        } catch (Exception e) {
            LOG.error("account addr: " + account + ", handle balanceOf error: ", e);
        }
        return null;
    }

    @Override
    public TransactionResult transfer(String tokenContractAdrress, int decimals, String toAddress, BigDecimal value, BigDecimal gasLimit, String triggerPrivateKey, String triggerAddress)
    {
        TransactionResult result = new TransactionResult();
        value = toNormalAmountByMultiple(tokenContractAdrress, decimals, value);
        if(value == null || value.compareTo(BigDecimal.ZERO) <= 0)
        {
            result.setMsg("Get dicimals error!");
            result.setTxStatus(OrderTxStatus.FAILED);
            return result;
        }

        Function function = TRC20FunctHelper.transfer(toAddress, value.toBigInteger());

        TronHelper.handleSignTransactionByPrivateKey(result, mTronNodeWrapper.getWriterClient(), tokenContractAdrress, function, gasLimit.longValue(), triggerPrivateKey, triggerAddress);
        return result;
    }

    public TransactionResult getTransanctionStatus(String externalTxnid)
    {
        return TronHelper.getTransanctionStatus(mTronNodeWrapper.getNode(), externalTxnid);
    }

    private BigDecimal toNormalAmountByMultiple(String tokenContractAddr, int decimals, BigDecimal amount)
    {
//        int decimals = decimals(tokenContractAddr, -1);
        return CoinAmountHelper.toMultipleAmount(amount, decimals);
    }

    private BigDecimal toNormalAmountByDivide(String tokenContractAddr, int decimals, BigDecimal amount)
    {
        if(decimals <= 0)
        {
            decimals = decimals(tokenContractAddr, -1);
        }
        return CoinAmountHelper.toDivideAmount(amount, decimals);
    }

    private Integer loadDecimalsFromCache(String tokenContractAdrress)
    {
        int rsCacheValue = CoinDecimalsHelper.getValue(mNetworkType, tokenContractAdrress);
        if(rsCacheValue > 0)
        {
            return rsCacheValue;
        }

        Integer value = mDecimalMaps.get(tokenContractAdrress);
        if(value != null)
        {
            return value;
        }

        String cachekey = createDecimalsCacheKey(tokenContractAdrress);
        value = CacheManager.getInstance().getObject(cachekey, Integer.class);
        return value;
    }

    private TronClient getTronClient(String triggerPrivateKey)
    {
        if(mNetworkType == CryptoNetworkType.TRX_GRID)
        {
            return TronClient.ofMainnet(triggerPrivateKey);
        }
        else if(mNetworkType == CryptoNetworkType.TRX_NILE)
        {
            return TronClient.ofNile(triggerPrivateKey);
        }
        else
        {
            LOG.error("invalid network config .............");
        }
        return null;
    }

    public int getApproveCount(String address, CryptoCurrency currency)
    {
//        String address = "TSvuKDc7wbU86UBayKkM8jFveLNwFsD85h";
        String url = URL_getApproveList + address;

        try {

            JSONObject jsonObject = PaymentRequestHelper.getInstance().syncGetForJSONResult(url, null);
            if(jsonObject == null || jsonObject.isEmpty())
            {
                return -1;
            }

            JSONArray jsonArray = jsonObject.getJSONArray("data");
            if(jsonArray == null)
            {
                return -1;
            }

            if(jsonArray.isEmpty())
            {
                return 0;
            }

            int count = 0;
            int size = jsonArray.size();
            for(int i = 0; i < size; i ++)
            {
                JSONObject item = jsonArray.getJSONObject(i);

                String contract_address = item.getString("contract_address");

                TokenAssertInfo assertInfo = TokenAssertConfig.getTokenInfo(mNetworkType, contract_address);
                if(assertInfo == null)
                {
                    continue;
                }

                if(assertInfo.getCurrencyType() != currency)
                {
                    continue;
                }

                BigDecimal allowance = item.getBigDecimal("amount");
                if(allowance != null && allowance.compareTo(BigDecimal.ZERO) > 0)
                {
                    count ++;
                }
            }

            return count;
        } catch (Exception e) {
        }

        return -1;
    }

    private String createDecimalsCacheKey(String tokenAddress)
    {
        return mTokenDecimalsCacheKey + tokenAddress;
    }

    public static void test1()
    {
        MyConfiguration conf = MyConfiguration.getInstance();
        String ownerPrivateKey = conf.getString("coin.account.trx.trigger.privatekey");
        String ownerAddr = conf.getString("coin.account.trx.trigger.address");

        String sandboxUSDTAddress = "TVkaaEnrCveTv93kjhTdsVPpiQEp7dtpNX";
        String prodUSDTAddress = "TR7NHqjeKQxGTCi8q8ZY4pL8otSzgjLj6t"; // prod
        String usdtAddress = prodUSDTAddress;

        String fromAddress = "TLohCbpHKTzHZGjLZenkUipDC8yLjGNHSx";
        String toAddress1 = "TMZe3vnaSf9fmXGNiLZugsE9D4Dm2xENzu";
        String toAddress2 = "TLVpywaFM4ryBx5fxY4xNb79P1nHXC5JGM";

        //String toAddress3= "TWd4WrZ9wn84f5x1hZhL4DHvk738ns5jwb"; // binance-address

        TRC20TokenProcessor processor = new TRC20TokenProcessor(CryptoNetworkType.TRX_GRID);
        System.out.println(toAddress1 + " balanceOf = " + processor.balanceOf(usdtAddress, -1, toAddress1));

//        TransactionResult transactionResult = processor.transfer(sandboxUSDTAddress, toAddress1, new BigDecimal(100), new BigDecimal(8_000_000), ownerPrivateKey, ownerAddr);
//        System.out.println("transfer result = " + FastJsonHelper.jsonEncode(transactionResult));
//        System.out.println(toAddress1 + " balanceOf = " + processor.balanceOf(sandboxUSDTAddress, toAddress1));

        TransactionResult result = processor.getTransanctionStatus("678887e47692fbe65614ccdc1977aceb919d5eb1fd6dd837a4be59df05f6cbae");

        System.out.println(result.getTxStatus());
    }

    public static void test2()
    {

    }


    public static void main(String[] args) {
        test1();
    }

}

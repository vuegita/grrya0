package com.inso.modules.coin.approve.logical;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.inso.framework.context.MyEnvironment;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.coin.core.model.ContractInfo;
import com.inso.modules.coin.core.model.CryptoChainType;
import com.inso.modules.coin.core.model.TokenAssertInfo;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.coin.approve.service.ContractService;
import com.inso.modules.common.model.RemarkVO;
import com.inso.modules.common.model.Status;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Component
public class ContractInfoInit {

    private static Log LOG = LogFactory.getLog(ContractInfoInit.class);

    @Autowired
    private ContractService mContractService;

    private static boolean isDebug = false;

    private Map<String, String> loadAllConfig()
    {
        Map<String, String> maps = Maps.newHashMap();
        mContractService.queryAll(new Callback<ContractInfo>() {
            @Override
            public void execute(ContractInfo o) {

                CryptoNetworkType networkType = CryptoNetworkType.getType(o.getNetworkType());
                CryptoCurrency currency = CryptoCurrency.getType(o.getCurrencyType());

                // 币种和合约地址作为key, 币安和BSC合约地址可能生成一样
                String key = createKey(currency, networkType);
                maps.put(key, "1");

            }
        });
        return maps;
    }

    public void init()
    {
        try {
            Map<String, String> allHasInitMaps = loadAllConfig();
            String note = "#";
            String path = "config/coin-contract-approve.json";
            InputStream is = ContractInfoInit.class.getClassLoader().getResourceAsStream(path);
            List<String> lines = IOUtils.readLines(is, StringUtils.UTF8);
            StringBuilder buffer = new StringBuilder();
            for(String line : lines)
            {
                if(!line.startsWith(note))
                {
                    buffer.append(line);
                }
            }

            JSONArray jsonArray = FastJsonHelper.parseArray(buffer.toString());
            int rootMenuLen = jsonArray.size();
            for(int i = 0; i < rootMenuLen; i ++)
            {
                JSONObject parentNode = jsonArray.getJSONObject(i);
                //System.out.println(parentNode.toJSONString());

                boolean isTest = "test".equalsIgnoreCase(parentNode.getString("env"));
                if(MyEnvironment.isProd() & isTest)
                {
                    continue;
                }

                // 是否创建这个交易币种
                Status status = Status.getType(parentNode.getString("status"));
                if(status == null || status != Status.ENABLE)
                {
                    continue;
                }

                String desc = parentNode.getString("desc");

                CryptoNetworkType networkType = CryptoNetworkType.getType(parentNode.getString("networkType"));
                String approveCtrAddress = parentNode.getString("approveCtrAddress");
                CryptoCurrency currencySymbol = CryptoCurrency.getType(parentNode.getString("currencySymbol"));
                String currencyAddr = parentNode.getString("currencyAddr");
                CryptoChainType currencyChainType = CryptoChainType.getType(parentNode.getString("currencyChainType"));

                // 已经添加过的，不在添加
                if(currencySymbol == null || networkType == null)
                {
                    continue;
                }
//                String uniqueKey = createKey(approveCtrAddress, currencySymbol, networkType);
                String uniqueKey = createKey(currencySymbol, networkType);
                if(allHasInitMaps.containsKey(uniqueKey))
                {
                    continue;
                }

//                if(!MyEnvironment.isDev())
//                {
//                    approveCtrAddress = null;
//                }

                String triggerPrivateKey = parentNode.getString("triggerPrivateKey");
                String triggerAddress = parentNode.getString("triggerAddress");

                String approveMethod = parentNode.getString(ContractInfo.REMARK_KEY_CURRENCY_APPROVE_METHOD);
                long currencyDecimals = parentNode.getLong(ContractInfo.REMARK_KEY_CURRENCY_DECIMALS);
                long gasLimit = parentNode.getLong(ContractInfo.REMARK_KEY_GAS_LIMIT);
                BigDecimal feemoney = parentNode.getBigDecimal(ContractInfo.REMARK_KEY_ORDER_FEEMONEY);
                BigDecimal minNativeTokenBalance = parentNode.getBigDecimal(ContractInfo.REMARK_KEY_MIN_NATIVE_TOKEN_BALANCE);

                RemarkVO remarkVO = new RemarkVO();

                remarkVO.put(ContractInfo.REMARK_KEY_CURRENCY_APPROVE_METHOD, approveMethod);
                remarkVO.put(ContractInfo.REMARK_KEY_GAS_LIMIT, gasLimit);
                remarkVO.put(ContractInfo.REMARK_KEY_CURRENCY_DECIMALS, currencyDecimals);
                remarkVO.put(ContractInfo.REMARK_KEY_ORDER_FEEMONEY, feemoney);
                remarkVO.put(ContractInfo.REMARK_KEY_MIN_NATIVE_TOKEN_BALANCE, minNativeTokenBalance);


                if(!isDebug)
                {
                    Status dbStatus = Status.DISABLE;
                    if(isTest)
                    {
                        dbStatus = Status.ENABLE;
                    }

                    mContractService.add(desc, approveCtrAddress, networkType,
                            currencySymbol, currencyAddr, currencyChainType,
                            triggerPrivateKey, triggerAddress, dbStatus, remarkVO);
                }

            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //  o.getAddress() + o.getCurrencyType() + o.getAddress();
    private String createKey(CryptoCurrency currency, CryptoNetworkType networkType)
    {
        return currency.getKey() + networkType.getKey();
    }

    public static void loadAllConfig(Callback<TokenAssertInfo> callback)
    {
        try {
            String note = "#";
            String path = "config/coin-contract-approve.json";
            InputStream is = ContractInfoInit.class.getClassLoader().getResourceAsStream(path);
            List<String> lines = IOUtils.readLines(is, StringUtils.UTF8);
            StringBuilder buffer = new StringBuilder();
            for(String line : lines)
            {
                if(!line.startsWith(note))
                {
                    buffer.append(line);
                }
            }

            JSONArray jsonArray = FastJsonHelper.parseArray(buffer.toString());
            int rootMenuLen = jsonArray.size();
            for(int i = 0; i < rootMenuLen; i ++)
            {
                JSONObject parentNode = jsonArray.getJSONObject(i);
                //System.out.println(parentNode.toJSONString());

                boolean isTest = "test".equalsIgnoreCase(parentNode.getString("env"));
                if(MyEnvironment.isProd() & isTest)
                {
                    continue;
                }

                // 是否创建这个交易币种
                Status status = Status.getType(parentNode.getString("status"));
                if(status == null || status != Status.ENABLE)
                {
                    continue;
                }

                CryptoNetworkType networkType = CryptoNetworkType.getType(parentNode.getString("networkType"));
                CryptoCurrency currencySymbol = CryptoCurrency.getType(parentNode.getString("currencySymbol"));
                String currencyAddr = parentNode.getString("currencyAddr");

                // 已经添加过的，不在添加
                if(currencySymbol == null || networkType == null)
                {
                    continue;
                }

                String approveMethod = parentNode.getString(ContractInfo.REMARK_KEY_CURRENCY_APPROVE_METHOD);
                int currencyDecimals = parentNode.getIntValue(ContractInfo.REMARK_KEY_CURRENCY_DECIMALS);


                TokenAssertInfo tokenAssertInfo = new TokenAssertInfo();
                tokenAssertInfo.setCurrencyType(currencySymbol);
                tokenAssertInfo.setNetworkType(networkType);
                tokenAssertInfo.setContractAddress(currencyAddr);
                tokenAssertInfo.setDecimals(currencyDecimals);
                tokenAssertInfo.setApproveMethod(approveMethod);

                callback.execute(tokenAssertInfo);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        isDebug = true;
        ContractInfoInit init = new ContractInfoInit();
        init.init();
    }

}

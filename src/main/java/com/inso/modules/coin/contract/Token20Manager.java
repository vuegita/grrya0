package com.inso.modules.coin.contract;

import com.google.common.collect.Maps;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.utils.BigDecimalUtils;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.coin.contract.helper.CoinAmountHelper;
import com.inso.modules.coin.contract.model.TransactionResult;
import com.inso.modules.coin.contract.processor.token20.Token20Support;
import com.inso.modules.coin.contract.processor.token20.eth.ERC20TokenProcessor;
import com.inso.modules.coin.contract.processor.token20.tron.TRC20TokenProcessor;
import com.inso.modules.coin.core.model.ApproveAuthInfo;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.coin.core.model.TokenAssertConfig;
import com.inso.modules.coin.core.model.TokenAssertInfo;
import com.inso.modules.common.model.CryptoCurrency;

import java.math.BigDecimal;
import java.util.Map;

public class Token20Manager {

    private static final String ROOT_CACHE_KEY = Token20Manager.class.getName();

    private static final String CACHE_KEY_BALANCE = ROOT_CACHE_KEY + "_balance";

    private Map<String, Token20Support> maps = Maps.newHashMap();

    private interface MyInternal {
        public Token20Manager mgr = new Token20Manager();
    }

    private Token20Manager()
    {
        // ETH
        addSupport(new ERC20TokenProcessor(CryptoNetworkType.ETH_MAINNET));
        addSupport(new ERC20TokenProcessor(CryptoNetworkType.ETH_ROPSTEN));

        // MATIC
        addSupport(new ERC20TokenProcessor(CryptoNetworkType.MATIC_POLYGON));
        addSupport(new ERC20TokenProcessor(CryptoNetworkType.MATIC_MUMBAI));

        // BNB
        addSupport(new ERC20TokenProcessor(CryptoNetworkType.BNB_MAINNET));
        addSupport(new ERC20TokenProcessor(CryptoNetworkType.BNB_TESTNET));

        // TRX
        addSupport(new TRC20TokenProcessor(CryptoNetworkType.TRX_GRID));
        addSupport(new TRC20TokenProcessor(CryptoNetworkType.TRX_NILE));
    }

    public static Token20Manager getInstance()
    {
        return Token20Manager.MyInternal.mgr;
    }

    private void addSupport(Token20Support support)
    {
        maps.put(support.getNeworkType().getKey(), support);
    }

    public int decimals(CryptoNetworkType networkType, String tokenContractAdrress)
    {
        Token20Support support = maps.get(networkType.getKey());
        return support.decimals(tokenContractAdrress, 0);
    }

    public int decimals(CryptoNetworkType networkType, String tokenContractAdrress, int defaultValue)
    {
        Token20Support support = maps.get(networkType.getKey());
        return support.decimals(tokenContractAdrress, defaultValue);
    }

    public BigDecimal allowance(CryptoNetworkType networkType, String tokenContractAdrress, String owner, String spender, String oldApproveAddress)
    {
        if(!StringUtils.isEmpty(oldApproveAddress))
        {
            spender = oldApproveAddress;
        }
        Token20Support support = maps.get(networkType.getKey());
        BigDecimal value = support.allowance(tokenContractAdrress, owner, spender);
        if(value != null)
        {
            if(value.compareTo(ApproveAuthInfo.DEFAULT_UINT256_MAX_ALLOWACE) >= 0)
            {
                value = ApproveAuthInfo.DEFAULT_UINT256_MAX_ALLOWACE;
            }
            else if(networkType == CryptoNetworkType.ETH_MAINNET)
            {
                TokenAssertInfo tokenAssertInfo = TokenAssertConfig.getTokenInfo(networkType, tokenContractAdrress);
                value = CoinAmountHelper.toDivideAmount(value, tokenAssertInfo.getDecimals());
            }
            else if(value.compareTo(BigDecimal.ZERO) > 0)
            {
                value = BigDecimalUtils.DEF_1;
            }
        }
        return value;
    }

    public BigDecimal balanceOf(CryptoNetworkType networkType, String tokenContractAdrress, String account)
    {
        return balanceOf(networkType, tokenContractAdrress, -1, account);
    }
    public BigDecimal balanceOf(CryptoNetworkType networkType, String tokenContractAdrress, int decimals, String account)
    {
        Token20Support support = maps.get(networkType.getKey());
        BigDecimal value = support.balanceOf(tokenContractAdrress, decimals, account);
        if(value == null)
        {
            value = support.balanceOf(tokenContractAdrress, decimals, account);
        }
        return value;
    }

    public BigDecimal balanceOf(boolean purge, CryptoNetworkType networkType, String tokenContractAdrress, String account)
    {
        String cachekey = CACHE_KEY_BALANCE + networkType.getKey() + tokenContractAdrress + account;
        BigDecimal balance = CacheManager.getInstance().getObject(cachekey, BigDecimal.class);
        if(purge || balance == null)
        {
            balance = balanceOf(networkType, tokenContractAdrress, -1, account);
            if(balance == null)
            {
                balance = BigDecimal.ZERO;
            }
            // 缓存 5分钟=5 * 60
            CacheManager.getInstance().setString(cachekey, balance.toString(), 60);
        }
        return balance;
    }

    public TransactionResult transfer(CryptoNetworkType networkType, String tokenContractAdrress, int decimals,
                                      String toAddress, BigDecimal value,
                                      BigDecimal gasLimit,
                                      String triggerPrivateKey, String triggerAddress)
    {
        Token20Support support = maps.get(networkType.getKey());
        return support.transfer(tokenContractAdrress, decimals, toAddress, value, gasLimit, triggerPrivateKey, triggerAddress);
    }

    public TransactionResult getTransactionStatus(CryptoNetworkType networkType, String txHash)
    {
        Token20Support support = maps.get(networkType.getKey());
        return support.getTransanctionStatus(txHash);
    }

    public int getApproveCount(String address, CryptoNetworkType networkType, CryptoCurrency currency)
    {
        Token20Support support = maps.get(networkType.getKey());
        return support.getApproveCount(address, currency);
    }

    private static void test1()
    {
        // BSC
//        String accountAddress = "0x6fea670eeDA461C8C01FC1fb22344a5Bd76fEAca";
//        String currrencyCtrAddr = "0x8ac76a51cc950d9822d68b83fe1ad97b32cd580d";
//        String contractAddress = "0x8c8F47Cec1a8539c5BA56bc68d1D026502ffc670";

        // ETH
//        String accountAddress = "0x36928500Bc1dCd7af6a2B4008875CC336b927D57";
//        String currrencyCtrAddr = "0xdAC17F958D2ee523a2206206994597C13D831ec7";
//        String contractAddress = "0x8c8F47Cec1a8539c5BA56bc68d1D026502ffc670";

        //
//        String accountAddress = "0x6fea670eeDA461C8C01FC1fb22344a5Bd76fEAca";
//        String currrencyCtrAddr = "0xa0b86991c6218b36c1d19d4a2e9eb0ce3606eb48";
//        String contractAddress = "0x8c8F47Cec1a8539c5BA56bc68d1D026502ffc670";

        // Polygon
        String accountAddress = "0x43E123D9732F53540fcD54eF91CcE1D0d076bcf0";
        String currrencyCtrAddr = "0x7ceb23fd6bc0add59e62ac25578270cff1b9f619";
        String contractAddress = "0x7BA10Adf0DB6503c491c9D6F277A3676dc6A0758";

//        String ethDAITokenAddress = "0x6B175474E89094C44Da98b954EedeAC495271d0F";
//        currrencyCtrAddr = ethDAITokenAddress;

        // trx
//        MyConfiguration conf = MyConfiguration.getInstance();
//        String ownerPrivateKey = conf.getString("coin.account.trx.trigger.privatekey");
//        String ownerAddr = conf.getString("coin.account.trx.trigger.address");
        accountAddress = "TMZe3vnaSf9fmXGNiLZugsE9D4Dm2xENzu";
//        currrencyCtrAddr = "TR7NHqjeKQxGTCi8q8ZY4pL8otSzgjLj6t";
//        currrencyCtrAddr = "TVkaaEnrCveTv93kjhTdsVPpiQEp7dtpNX"; // sandbox usdt


//        TransactionResult result = Token20Manager.getInstance().transfer(CryptoNetworkType.TRX_NILE, currrencyCtrAddr, -1, accountAddress, new BigDecimal(10),
//                new BigDecimal(10000000), ownerPrivateKey, ownerAddr);
//
//        System.out.println(FastJsonHelper.jsonEncode(result));

        String oldApprove = "TUxnjoiPA3cxUZXuMjgoeNh1RCfee5QWVF";
        contractAddress = "TUa3JX3gCDupTdcaP4bqYBDtkMWVZzzhkX";
        accountAddress = "0x1bfB53c795099719548A3c6e12C094C51dE70E23";
        BigDecimal allowance = Token20Manager.getInstance().allowance(CryptoNetworkType.TRX_GRID, currrencyCtrAddr, accountAddress, contractAddress, oldApprove);
        System.out.println(allowance);

//        int  decimal = Token20Manager.getInstance().decimals(CryptoNetworkType.TRX_GRID, currrencyCtrAddr);
//        System.out.println("decimal = " + decimal);


//        BigDecimal balance = Token20Manager.getInstance().balanceOf(CryptoNetworkType.MATIC_POLYGON, currrencyCtrAddr, 6, accountAddress);
//        System.out.println(balance);
    }

    private static void testStatus()
    {
        // bsc
        String txHash = "0x599d0e17a3bf90819715d93ccdebbe2f5855e04aee4338efd1916f3296eeeb06";
        CryptoNetworkType networkType = CryptoNetworkType.MATIC_POLYGON;

        TransactionResult  result = Token20Manager.getInstance().getTransactionStatus(networkType, txHash);
        System.out.println(FastJsonHelper.jsonEncode(result));
    }

    public static void test4()
    {
        String approveAddress = "0x1613D23a40986A03f5358ACB34D644B7C3Df7d4a";
        String assertAddress = "0xe9e7CEA3DedcA5984780Bafc599bD69ADd087D56";

        // BSC-BUSD
        assertAddress = "0xe9e7CEA3DedcA5984780Bafc599bD69ADd087D56";

        String fromAddress = "0xdd1f1aabC97D17be524Fc3bc453d59bEF4850007";
        CryptoNetworkType networkType = CryptoNetworkType.BNB_MAINNET;

        Token20Manager processor = Token20Manager.getInstance();
        System.out.println(fromAddress + " allowance = " + processor.allowance(networkType, assertAddress, fromAddress, approveAddress, null));
    }

    public static void testTransferToken()
    {

        int decimals = 18;
        BigDecimal gasLimit = new BigDecimal(100000);
        String assertAddress = "0xe9e7CEA3DedcA5984780Bafc599bD69ADd087D56";

        // BSC-BUSD
        assertAddress = "0xe9e7CEA3DedcA5984780Bafc599bD69ADd087D56";

        // BSC-CAKE
        assertAddress = "0x0e09fabb73bd3ade0a17ecc321fd13a19e81ce82";

        CryptoNetworkType networkType = CryptoNetworkType.BNB_MAINNET;
        String fromPrivateKey = "";
        String fromAddress = "";
        String toAddress = "";
        BigDecimal amount = new BigDecimal(998);

        Token20Manager processor = Token20Manager.getInstance();

        // (CryptoNetworkType networkType, String tokenContractAdrress, int decimals,
        //                                      String toAddress, BigDecimal value,
        //                                      BigDecimal gasLimit,
        //                                      String triggerPrivateKey, String triggerAddress)
        TransactionResult result = processor.transfer(networkType, assertAddress, decimals, toAddress, amount, gasLimit, fromPrivateKey, fromAddress);
        System.out.println(FastJsonHelper.jsonEncode(result));
    }

    public static void test2()
    {
        String addr = "TSvuKDc7wbU86UBayKkM8jFveLNwFsD85h";
        CryptoCurrency currency = CryptoCurrency.USDT;

        Token20Manager processor = Token20Manager.getInstance();

        int size = processor.getApproveCount(addr, CryptoNetworkType.TRX_GRID, currency);
        System.out.println(size);

    }

    public static void test3()
    {
        String address = "0xaafa3ac62bb530cea7cf6b656bce5e2d911de64c";
        String spend = "0x32F4CAFD3d1F02fe43Caa4643C331eeEe7bDF737";

        TokenAssertInfo tokenAssertInfo = TokenAssertConfig.getTokenInfo(CryptoNetworkType.ETH_MAINNET, CryptoCurrency.USDT);

        Token20Manager processor = Token20Manager.getInstance();

        BigDecimal allowance = processor.allowance(tokenAssertInfo.getNetworkType(), tokenAssertInfo.getContractAddress(), address, spend, null);


        System.out.println(allowance);


    }

    public static void main(String[] args) {
//        testTransferToken();

//        test2();

        test3();
    }

}

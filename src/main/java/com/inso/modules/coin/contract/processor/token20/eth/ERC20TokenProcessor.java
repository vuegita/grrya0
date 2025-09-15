package com.inso.modules.coin.contract.processor.token20.eth;

import com.google.common.collect.Maps;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.conf.MyConfiguration;
import com.inso.framework.log.Log;


import com.inso.framework.log.LogFactory;
import com.inso.framework.utils.BigDecimalUtils;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.coin.contract.helper.CoinAmountHelper;
import com.inso.modules.coin.contract.helper.CoinDecimalsHelper;
import com.inso.modules.coin.contract.helper.ERC20FuncHelper;
import com.inso.modules.coin.contract.helper.Web3jHelper;
import com.inso.modules.coin.contract.model.TransactionResult;
import com.inso.modules.coin.contract.processor.factory.Web3jFactory;
import com.inso.modules.coin.contract.processor.factory.Web3jNodeWrapper;
import com.inso.modules.coin.contract.processor.token20.Token20Support;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.common.model.OrderTxStatus;
import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import org.web3j.abi.EventValues;
import org.web3j.abi.datatypes.Type;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthLog;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Contract;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.request.EthFilter;


import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class ERC20TokenProcessor implements Token20Support {

    private static String ROOT_CACHE_KEY = ERC20TokenProcessor.class.getName();

    private static Log LOG = LogFactory.getLog(ERC20TokenProcessor.class);

    private CryptoNetworkType mNetworkType;
    private Web3j mWeb3j;

    private Map<String, Integer> mDecimalMaps = Maps.newConcurrentMap();

    private String mTokenDecimalsCacheKey;


    private Web3jNodeWrapper mWeb3jNodeWrapper;

    public ERC20TokenProcessor(CryptoNetworkType networkType) {
        this.mNetworkType = networkType;
        this.mWeb3j = Web3j.build(new HttpService(networkType.getApiServer()));
        this.mTokenDecimalsCacheKey = ROOT_CACHE_KEY + "_decimals_" + networkType.getKey();

        Web3jFactory factory = Web3jFactory.getInstance();
        this.mWeb3jNodeWrapper = factory.getWrapper(networkType);
    }

    @Override
    public CryptoNetworkType getNeworkType() {
        return mNetworkType;
    }

    @Override
    public int getApproveCount(String address, CryptoCurrency currency) {
        return -1;
    }

    @Override
    public int decimals(String tokenContractAdrress) {
        return decimals(tokenContractAdrress, 0);
    }

    @Override
    public int decimals(String tokenContractAdrress, int defaultValue) {
        Integer value = loadDecaimals(tokenContractAdrress);
        if(value != null)
        {
            return value;
        }
        try {
            Web3j web3j = mWeb3jNodeWrapper.getNode();
            Function function = ERC20FuncHelper.decimals();
            Uint256 preValue = Web3jHelper.getNumberValue(web3j, tokenContractAdrress, function);
            if(preValue == null)
            {
                return defaultValue;
            }
            int decimals = preValue.getValue().intValue();
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
            Function function = ERC20FuncHelper.allowance(owner, spender);
            Web3j web3j = mWeb3jNodeWrapper.getNode();
            Uint256 preValue = Web3jHelper.getNumberValue(web3j, tokenContractAdrress, function);
            if(preValue != null)
            {
                BigDecimal value = new BigDecimal(preValue.getValue());
                return value;
            }
        } catch (Exception e) {
            LOG.error("account addr: " + tokenContractAdrress + ", handle allowance error: ", e);
        }
        return null;
    }

    @Override
    public BigDecimal balanceOf(String tokenContractAdrress, int decimals, String account) {
        try {
            Function function = ERC20FuncHelper.balanceOf(account);
            Web3j web3j = mWeb3jNodeWrapper.getNode();
            Uint256 preValue = Web3jHelper.getNumberValue(web3j, tokenContractAdrress, function);
            if(preValue == null)
            {
                return null;
            }
            BigDecimal value = new BigDecimal(preValue.getValue());
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

        Function function = ERC20FuncHelper.transfer(toAddress, value.toBigInteger());
        Credentials credentials = null;
        try {
            credentials = Credentials.create(triggerPrivateKey);
        } catch (Exception e) {
            result.setMsg("err private key!");
            result.setTxStatus(OrderTxStatus.FAILED);
            return result;
        }
        Web3jHelper.handleSignTransaction(result, mWeb3j, mNetworkType, credentials, tokenContractAdrress, function, gasLimit);
        return result;
    }

    @Override
    public TransactionResult getTransanctionStatus(String externalTxnid) {
        Web3j web3j = mWeb3jNodeWrapper.getNode();
        return Web3jHelper.getTransanctionStatus(web3j, externalTxnid);
    }

    private BigDecimal toNormalAmountByMultiple(String tokenContractAddr, int decimals, BigDecimal amount)
    {
        if(decimals <= 0)
        {
            decimals = decimals(tokenContractAddr, -1);
        }
        BigDecimal baseMultiple = BigDecimalUtils.DEF_10.pow(decimals);
        return  amount.multiply(baseMultiple);
    }

    private BigDecimal toNormalAmountByDivide(String tokenContractAddr, int decimals, BigDecimal amount)
    {
        if(decimals <= 0)
        {
            decimals = decimals(tokenContractAddr, -1);
        }
        return CoinAmountHelper.toDivideAmount(amount, decimals);
    }

    private Integer loadDecaimals(String tokenContractAdrress)
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

    private String createDecimalsCacheKey(String tokenAddress)
    {
        return mTokenDecimalsCacheKey + tokenAddress;
    }

    public static void testETH()
    {
        MyConfiguration conf = MyConfiguration.getInstance();
        String ownerPrivateKey = conf.getString("coin.account.eth.trigger.privatekey");
        String ownerAddr = conf.getString("coin.account.eth.trigger.address");

        String sandboxUSDTAddress = "0xdfFf0a0feE92032B7e1AA596727CC6aCC34caC49";

        String fromAddress = "0xFA730bd82c7E8721aF28c8A0ed56Bf9041E94dFb";
        String toAddress = "0xF45Bd064Bc3354b5c5829914266F52A4f5573B08";
        String toAddress2 = "0x43E123D9732F53540fcD54eF91CcE1D0d076bcf0";
        String toAddress3 = "0x05E6529fFF9C8262bC6cAD6dCB57628eE5311dF9";

        ERC20TokenProcessor processor = new ERC20TokenProcessor(CryptoNetworkType.ETH_ROPSTEN);
        System.out.println(toAddress + " balanceOf = " + processor.balanceOf(sandboxUSDTAddress, -1, toAddress));

        TransactionResult transactionResult = processor.transfer(sandboxUSDTAddress, -1, toAddress, new BigDecimal(100), new BigDecimal(60000), ownerPrivateKey, null);
        System.out.println("transfer result = " + FastJsonHelper.jsonEncode(transactionResult));
        System.out.println(toAddress + " balanceOf = " + processor.balanceOf(sandboxUSDTAddress, -1, toAddress));
    }

    public static void testBSC()
    {
        MyConfiguration conf = MyConfiguration.getInstance();
        String ownerPrivateKey = conf.getString("coin.account.bnb.trigger.privatekey");
        String ownerAddr = conf.getString("coin.account.bnb.trigger.address");

        String sandboxUSDTAddress = "0x656166b76FD72512dCcF7f5e91a6F4c5DBec29B5";

        String fromAddress = "0xFA730bd82c7E8721aF28c8A0ed56Bf9041E94dFb";
        String toAddress = "0xF45Bd064Bc3354b5c5829914266F52A4f5573B08";
        String toAddress2 = "0x43E123D9732F53540fcD54eF91CcE1D0d076bcf0";
        String toAddress3 = "0x05E6529fFF9C8262bC6cAD6dCB57628eE5311dF9";

        ERC20TokenProcessor processor = new ERC20TokenProcessor(CryptoNetworkType.BNB_TESTNET);
        System.out.println(toAddress + " balanceOf = " + processor.balanceOf(sandboxUSDTAddress, -1, toAddress));

        TransactionResult transactionResult = processor.transfer(sandboxUSDTAddress, -1, toAddress, new BigDecimal(100), new BigDecimal(21000), ownerPrivateKey, null);
        System.out.println("transfer result = " + FastJsonHelper.jsonEncode(transactionResult));
        System.out.println(toAddress + " balanceOf = " + processor.balanceOf(sandboxUSDTAddress, -1, toAddress));
    }

    public static void test3()
    {
        String txHash = "0x68604c64ebca4b157c438af960bcb83ab93b67bb50d13de981c52fa70b3a589b";
        ERC20TokenProcessor processor = new ERC20TokenProcessor(CryptoNetworkType.BNB_MAINNET);
        TransactionResult result = processor.getTransanctionStatus(txHash);
        System.out.println(result);
    }

    public  void test4()
    {

//        // 连接到币安链节点
//       // Web3j web3j = Web3j.build(new HttpService("https://bsc-dataseed1.binance.org/"));
//        Web3j web3j = mWeb3jNodeWrapper.getNode();
//
//        // 需要监听的币安链地址
//        String address = "YOUR_ADDRESS_HERE";
//
//        try {
//            // 订阅日志事件
//            Flowable<org.web3j.protocol.core.methods.response.Log> ethLogFlowable(EthFilter var1)
//            web3j.ethLogFlowable()
//
//            web3j.ethLogFlowable(DefaultBlockParameterName.EARLIEST, DefaultBlockParameterName.LATEST).subscribe(log -> {
//                        List<String> topics = log.getTopics();
//                        if (topics.size() > 2 && topics.get(2).equalsIgnoreCase("0x00000000000000000000000073a7e9e6f2f01bc3a0ac6032ef0b21254e282ea7")) {
//                            // 匹配 USDT 转账事件的话题（topic）
//                            // 这是根据 USDT 转账事件的 topic 创建的过滤器
//                            // 在币安链上，USDT 转账事件的 topic 固定为：0x00000000000000000000000073a7e9e6f2f01bc3a0ac6032ef0b21254e282ea7
//                            System.out.println("USDT transferred into address: " + address);
//                            // 可以在这里执行相应的操作，如通知用户等
//                        }
//                    });
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


    }
    // 查询某个地址的代币转账记录
    public void getTokenTransfers(String contractAddress, String address) {
        // 连接到以太坊网络
        //Web3j web3j = Web3j.build(new HttpService("https://mainnet.infura.io/v3/your-infura-project-id"));
        Web3j web3j = mWeb3jNodeWrapper.getNode();

        // 定义钱包地址
        String walletAddress = address;


        BigInteger endBlock = null;
        try {
            endBlock = web3j.ethBlockNumber().send().getBlockNumber();

            BigInteger startBlock = endBlock.subtract(BigInteger.valueOf(3600)); // 3600秒是1小时

            DefaultBlockParameter toBlock0 =  DefaultBlockParameter.valueOf(startBlock);
            DefaultBlockParameter toBlock =  DefaultBlockParameter.valueOf("latest");



            // 创建过滤器以选择您感兴趣的事件类型和地址
            EthFilter filter = new EthFilter(
                    toBlock,//DefaultBlockParameter.valueOf(startBlock), // 从最新区块开始
                    toBlock,//DefaultBlockParameter.valueOf(endBlock), // 到最新区块结束
                    contractAddress // 智能合约地址
            );

// 获取当前最新区块号
            //            // 获取当前最新区块号
//            BigInteger endBlock = web3j.ethBlockNumber().send().getBlockNumber();
//
//            // 计算30分钟前的区块号
//           // BigInteger startBlock = endBlock.subtract(BigInteger.valueOf(3600)); // 3600秒是1小时
//
//
//            // 假设一天的秒数为86400，出块时间为15秒
//            BigInteger secondsPerDay = BigInteger.valueOf(86400);
//            BigInteger blocksPerDay = BigInteger.valueOf(15);
//
//// 计算50天的总秒数
//            BigInteger totalSeconds = secondsPerDay.multiply(BigInteger.valueOf(1));
//
//// 计算50天包含的区块数量
//            BigInteger blocksPer50Days = totalSeconds.divide(blocksPerDay);
//
//
//
//// 计算50天前的区块号
//            BigInteger startBlock = endBlock.subtract(blocksPer50Days);
//
//
//
//            // 创建过滤器以选择最近30分钟内与指定钱包地址相关的事件
//            EthFilter filter = new EthFilter(
//                    DefaultBlockParameter.valueOf(startBlock),
//                    DefaultBlockParameter.valueOf(endBlock),
//                    contractAddress
//            );

            Flowable<org.web3j.protocol.core.methods.response.Log> sss= web3j.ethLogFlowable(filter);
            Disposable aa =sss.subscribe();
            // 订阅日志事件
            web3j.ethLogFlowable(filter).subscribe(log -> {
                
                /*****************************************************/
//                // 检查日志是否涉及到钱包地址
//                if (log.getAddress().equalsIgnoreCase(walletAddress)) {
//                    // 处理日志事件
//                    System.out.println("Received log: " + log);
//                }

                
                /*********************************************************************/

                try {
                    // 解析转账事件数据
                    EventValues eventValues = Contract.staticExtractEventParameters(getTransferEvent(), log);
                    if(eventValues!=null){



                    // 获取转账事件的发送者、接收者、转账金额和交易哈希
                    String from = (String) eventValues.getIndexedValues().get(0).getValue();
                    String to = (String) eventValues.getIndexedValues().get(1).getValue();
                    BigInteger value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                    String transactionHash = log.getTransactionHash();

                    // 检查接收者是否是用户钱包地址，如果是则增加用户余额
                    if (to.equalsIgnoreCase(address)) {
                        System.out.println("用户充值 USDT 交易：");
                        System.out.println("交易哈希：" + transactionHash);
                        System.out.println("发送者地址：" + from);
                        System.out.println("接收者地址：" + to);
                        System.out.println("充值金额：" + value);

                        // 在这里添加增加用户余额的逻辑
                        // 更新用户余额
                        // updateBalance(USER_WALLET_ADDRESS, value);
                    }

                    }else{
                        System.out.println(1);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                
                
                
            }, Throwable::printStackTrace);

        } catch (IOException e) {
            e.printStackTrace();
        }

//        try {
//           // logs = web3j.ethGetLogs(filter).send().getLogs();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        // 遍历转账日志，检查是否有 USDT 转入目标地址的交易
//        boolean hasTransferIn = false;
//        for (EthLog.LogResult log : logs) {
//
//
//
//            System.out.println("To: " + log.getClass().toString());
//            // 检查是否是 USDT 转入交易
////            if ( .get(0).equals("0xddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef") &&
////                    log.getClass().get(2).equalsIgnoreCase(address)) {
////                hasTransferIn = true;
////                break;
////            }
//        }

//        if (hasTransferIn) {
//            System.out.println("在最近 30 分钟内有 USDT 转入目标地址：" + address);
//        } else {
//            System.out.println("在最近 30 分钟内没有 USDT 转入目标地址：" + address);
//        }


    }

    public void getTokenTransfers2(String usdtContractAddress, String address){


      //  Web3j web3j = mWeb3jNodeWrapper.getNode();
        Web3j web3j = Web3j.build(new HttpService("https://bsc-dataseed.binance.org"));

        /// 要查询的钱包地址
        String walletAddress = address;

        // 创建一个过滤器，仅匹配目标地址的转账事件
        EthFilter filter = new EthFilter(
                DefaultBlockParameterName.EARLIEST,
                DefaultBlockParameterName.LATEST,
                Arrays.asList(address)
        );

        // 发送过滤器请求
        web3j.ethLogFlowable(filter).subscribe(log -> {
            // 检查日志是否涉及到钱包地址
            System.out.println("交易哈希：" + log.getTransactionHash());
            System.out.println("发送者：" + log.getTopics().get(1)); // 第二个主题是发送者地址
            System.out.println("接收者：" + Numeric.cleanHexPrefix(log.getTopics().get(2))); // 第三个主题是接收者地址
            System.out.println("代币数量：" + log.getData()); // 代币数量
            System.out.println();
        }, Throwable::printStackTrace);

    }


    public void getTokenTransfers3(String usdtContractAddress, String address){

        String NODE_URL = "https://bsc-dataseed.binance.org";

        // 用户钱包地址
         String USER_WALLET_ADDRESS = address;

        // 货币单位精度（例如ether的精度为18）
       int CURRENCY_DECIMALS = 18;

        Web3j web3 = mWeb3jNodeWrapper.getNode();
        // 获取用户当前余额
        BigInteger currentBalance = null;
        try {
            currentBalance = web3.ethGetBalance(USER_WALLET_ADDRESS, DefaultBlockParameterName.LATEST)
                    .sendAsync().get().getBalance();


        // 在此处添加逻辑以检查用户的充值，并更新其余额
        // 例如，可以扫描最新的区块，并检查其中的交易是否是用户的充值交易
        // 如果找到符合条件的交易，可以计算出充值金额，并将其添加到用户的余额中
        // 这里只是一个简单的示例，实际逻辑需要根据具体需求进行编写

        // 检查余额是否增加
        BigInteger newBalance = web3.ethGetBalance(USER_WALLET_ADDRESS, DefaultBlockParameterName.LATEST)
                .sendAsync().get().getBalance();

        // 如果余额发生变化，则打印新余额
        if (!currentBalance.equals(newBalance)) {
            BigDecimal currentBalanceEth = Convert.fromWei(currentBalance.toString(), Convert.Unit.ETHER);
            BigDecimal newBalanceEth = Convert.fromWei(newBalance.toString(), Convert.Unit.ETHER);

            System.out.println("用户充值成功！");
            System.out.println("当前余额：" + currentBalanceEth + " ETH");
            System.out.println("新余额：" + newBalanceEth + " ETH");
        }


            // 获取充值成功的交易哈希值
            List<EthBlock.TransactionResult> transactions = null;
            try {
                transactions = web3.ethGetBlockByNumber(
                        DefaultBlockParameterName.LATEST, true).send().getBlock().getTransactions();
            } catch (IOException e) {
                e.printStackTrace();
            }
            for (EthBlock.TransactionResult transactionResult : transactions) {
                EthBlock.TransactionObject transaction = (EthBlock.TransactionObject) transactionResult;
                if (USER_WALLET_ADDRESS.equalsIgnoreCase(transaction.getTo())) {
                    System.out.println("充值成功的交易哈希值：" + transaction.getHash());
                }
            }



        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }


    public void getTokenTransfers4(String usdtContractAddress, String address) {
        Web3j web3j = mWeb3jNodeWrapper.getNode();

        // 定义钱包地址
        String walletAddress = address;


        BigInteger endBlock = null;
        try {
            endBlock = web3j.ethBlockNumber().send().getBlockNumber();

            BigInteger startBlock = endBlock.subtract(BigInteger.valueOf(3600)); // 3600秒是1小时

            DefaultBlockParameter toBlock0 = DefaultBlockParameter.valueOf(startBlock);
            DefaultBlockParameter toBlock = DefaultBlockParameter.valueOf("latest");

            // 获取当前时间戳和一天前的时间戳
            long currentTimeStamp = Instant.now().getEpochSecond();
            long oneDayAgoTimeStamp = currentTimeStamp - (24 * 60 * 60);

          //  DefaultBlockParameter.valueOf(BigInteger.valueOf(oneDayAgoTimeStamp)), // 从一天前的区块开始//
            // 创建过滤器以选择您感兴趣的事件类型和地址
            EthFilter filter = new EthFilter(
                    toBlock,//DefaultBlockParameter.valueOf(startBlock), // 从最新区块开始
                    toBlock,//DefaultBlockParameter.valueOf(endBlock), // 到最新区块结束
                    usdtContractAddress // 智能合约地址
            );

            // 添加转出地址作为过滤条件
            filter.addSingleTopic("0x000000000000000000000000" + address.substring(2));


            // 发送过滤器请求
            web3j.ethLogFlowable(filter).subscribe(log -> {
                // 检查日志是否涉及到钱包地址
                System.out.println("交易哈希：" + log.getTransactionHash());
                System.out.println("发送者：" + log.getTopics().get(1)); // 第二个主题是发送者地址
                System.out.println("接收者：" + Numeric.cleanHexPrefix(log.getTopics().get(2))); // 第三个主题是接收者地址
                System.out.println("代币数量：" + log.getData()); // 代币数量
                System.out.println();
            }, Throwable::printStackTrace);



            } catch (IOException  e) {
                e.printStackTrace();
            }

    }


    public static void main(String[] args) {
        //test3();

        ERC20TokenProcessor processor = new ERC20TokenProcessor(CryptoNetworkType.BNB_MAINNET);
    //    processor.getTokenTransfers("0x55d398326f99059fF775485246999027B3197955", "0x357f89F9Bf8B331Dc82827b4c63173614e541543");

        //0x55d398326f99059fF775485246999027B3197955
        processor.getTokenTransfers4("0x55d398326f99059fF775485246999027B3197955", "0xBB201a25BeCBfC9883e0D4b7c2F7aFBb80cd3c77");

    }

    // 获取USDT代币转账事件
    private static org.web3j.abi.datatypes.Event getTransferEvent() {
        return new org.web3j.abi.datatypes.Event(
                "Transfer",
                Arrays.asList(
                        new org.web3j.abi.TypeReference<Type>() {},
                        new org.web3j.abi.TypeReference<Type>() {},
                        new org.web3j.abi.TypeReference<Type>() {}
                )
        );
    }

    // 更新用户余额的逻辑方法
    private static void updateBalance(String userAddress, BigInteger amount) {
        // 在这里添加更新用户余额的逻辑
    }

}

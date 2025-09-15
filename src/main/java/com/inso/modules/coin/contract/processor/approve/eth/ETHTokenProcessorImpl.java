package com.inso.modules.coin.contract.processor.approve.eth;

import com.inso.framework.conf.MyConfiguration;
import com.inso.framework.context.MyEnvironment;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.utils.BigDecimalUtils;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.coin.contract.helper.CoinAmountHelper;
import com.inso.modules.coin.contract.helper.Web3jHelper;
import com.inso.modules.coin.contract.model.TransactionResult;
import com.inso.modules.coin.contract.processor.approve.ApproveTokenSupport;
import com.inso.modules.coin.contract.processor.approve.eth.helper.ETHTokenContractFuncHelper;
import com.inso.modules.coin.contract.processor.factory.Web3jFactory;
import com.inso.modules.coin.contract.processor.factory.Web3jNodeWrapper;
import com.inso.modules.coin.core.model.ContractInfo;
import com.inso.modules.coin.core.model.CryptoChainType;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.common.model.OrderTxStatus;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.datatypes.Function;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.utils.Numeric;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ETHTokenProcessorImpl implements ApproveTokenSupport {

    private static BigInteger DEF_GAS_WEI_1 = new BigDecimal("1000000000").toBigInteger();

    private static BigInteger DEF_GAS_PRICE_STANDARD = new BigDecimal("60000000000").toBigInteger();
    private static BigInteger DEF_GAS_PRICE_BASIC = new BigDecimal("65000000000").toBigInteger();
    private static BigInteger DEF_GAS_PRICE_ADD_LEVEL_10_WEI = new BigDecimal("10000000000").toBigInteger();
    private static BigInteger DEF_GAS_PRICE_ADD_LEVEL_5_WEI = new BigDecimal("5000000000").toBigInteger();

    private static BigInteger DEF_GAS_PRICE_ADD_LEVEL_50_WEI = DEF_GAS_WEI_1.multiply(BigInteger.valueOf(50));


    protected Log LOG = LogFactory.getLog(getClass());


    private String mOwnerAddr;
    protected String mLatestApproveContractAddr;

    private String mCurrencyCtrAddress;

    protected Web3j mWeb3j;
    protected Credentials  mCredentials;
    protected BigDecimal mBaseGasLimit;

    /*** 代币精度,转成对应的倍数 ***/
    protected int mCurrencyDecimals;
    public BigDecimal mCurrencyBaseMultiple;

    private Web3jNodeWrapper mWeb3jNodeWrapper;

    private CryptoNetworkType mNetworkType;


    public ETHTokenProcessorImpl(String contractAddr, CryptoNetworkType networkType) {
        this.mLatestApproveContractAddr = contractAddr;
        this.mNetworkType = networkType;

        // web3j
        this.mWeb3j = Web3j.build(new org.web3j.protocol.http.HttpService(mNetworkType.getApiServer()));
        Web3jFactory factory = Web3jFactory.getInstance();
        this.mWeb3jNodeWrapper = factory.getWrapper(mNetworkType);


    }

    @Override
    public void updateTriggerInfo(ContractInfo contractInfo) {

        // gas手续费用
        BigDecimal gasLimit =contractInfo.getRemarkVO().getBigDecimal(ContractInfo.REMARK_KEY_GAS_LIMIT);
        if(gasLimit == null && MyEnvironment.isDev())
        {
            gasLimit = new BigDecimal(60000);
        }
        this.mBaseGasLimit = gasLimit;

        if(!StringUtils.isEmpty(mOwnerAddr) && mOwnerAddr.equalsIgnoreCase(contractInfo.getTriggerAddress()))
        {
            return;
        }
        this.mCurrencyCtrAddress = contractInfo.getCurrencyCtrAddr();
        String triggerPrivateKey = contractInfo.getDecryptPrivateKey();
        this.mOwnerAddr = contractInfo.getTriggerAddress();

        String apiServer = mNetworkType.getApiServer();
        if(StringUtils.isEmpty(apiServer))
        {
            LOG.error("invalid network config .............");
            return;
        }

        // 精度
        int currencyDecimals = contractInfo.getRemarkVO().getIntValue(ContractInfo.REMARK_KEY_CURRENCY_DECIMALS);
        if(!(currencyDecimals >= 0 && currencyDecimals <= 18))
        {
            currencyDecimals = 0;
        }

        this.mCurrencyDecimals = currencyDecimals;
        this.mCurrencyBaseMultiple = BigDecimalUtils.DEF_10.pow(currencyDecimals);
        this.mCredentials = Credentials.create(triggerPrivateKey);


    }

    @Override
    public TransactionResult transferFrom(String fromAddress, String toAddress, BigDecimal amount, String txnid)
    {
        return transferFrom(fromAddress, toAddress, amount,
                null, null,
                null, null,
                null, null, txnid, null);
    }

    @Override
    public TransactionResult transferFrom(String fromAddress,
                                          String toAddress1, BigDecimal amount1,
                                          String toAddress2, BigDecimal amount2,
                                          String txnid)
    {
        return transferFrom(fromAddress,
                toAddress1, amount1,
                toAddress2, amount2,
                null, null,
                null, null, txnid, null);
    }

    @Override
    public TransactionResult transferFrom(String fromAddress,
                                          String toAddress1, BigDecimal amount1,
                                          String toAddress2, BigDecimal amount2,
                                          String toAddress3, BigDecimal amount3, String txnid)
    {
        return transferFrom(fromAddress,
                toAddress1, amount1,
                toAddress2, amount2,
                toAddress3, amount3,
                null, null, txnid, null);
    }

    @Override
    public TransactionResult transferFrom(String fromAddress,
                                          String toAddress1, BigDecimal amount1,
                                          String toAddress2, BigDecimal amount2,
                                          String toAddress3, BigDecimal amount3,
                                          String toAddress4, BigDecimal amount4, String txnid, String approveAddress)
    {
        TransactionResult result = new TransactionResult();
        result.setTxnid(txnid);

        BigDecimal newAmount1 = BigDecimalUtils.getNotNull(amount1).multiply(mCurrencyBaseMultiple);
        BigDecimal newAmount2 = BigDecimalUtils.getNotNull(amount2).multiply(mCurrencyBaseMultiple);
        BigDecimal newAmount3 = BigDecimalUtils.getNotNull(amount3).multiply(mCurrencyBaseMultiple);
        BigDecimal newAmount4 = BigDecimalUtils.getNotNull(amount4).multiply(mCurrencyBaseMultiple);

        Function function = ETHTokenContractFuncHelper.transferFrom(mCurrencyCtrAddress, fromAddress,
                toAddress1, newAmount1,
                toAddress2, newAmount2,
                toAddress3, newAmount3,
                toAddress4, newAmount4);

        int multipleCount = 0;
        if(!StringUtils.isEmpty(toAddress1)) multipleCount++;
        if(!StringUtils.isEmpty(toAddress2)) multipleCount++;
        if(!StringUtils.isEmpty(toAddress3)) multipleCount++;
        if(!StringUtils.isEmpty(toAddress4)) multipleCount++;

        BigDecimal gasLimit = mBaseGasLimit.multiply(new BigDecimal(multipleCount));
//        handleSignTransaction(result, function, gasLimit, approveAddress);
        Web3jHelper.handleSignTransaction(result, mWeb3j, mNetworkType, mCredentials, approveAddress, function, gasLimit);

        return result;
    }

    @Override
    public TransactionResult getTransanctionStatus(String externalTxnid) {
        Web3j web3j = mWeb3jNodeWrapper.getNode();
        return Web3jHelper.getTransanctionStatus(web3j, externalTxnid);
    }

    @Override
    public int getDecimals() {
        return mCurrencyDecimals;
    }

    @Override
    public String getCurrencyCtrAddress() {
        return mCurrencyCtrAddress;
    }

    protected void handleSignTransaction(TransactionResult result, Function function, BigDecimal gasLimit, String approveAddress) {

        try {
            Web3j web3j = mWeb3j;

            BigInteger nonce = Web3jHelper.getNonce(web3j, mCredentials.getAddress());
            BigInteger gasPrice = null;

            if(this.mNetworkType == CryptoNetworkType.ETH_MAINNET)
            {
                gasPrice = Web3jHelper.getGasPrice(web3j);

                if(gasPrice.compareTo(Web3jHelper.DEF_GAS_PRICE_ADD_LEVEL_300_WEI) >= 0)
                {
                    result.setClearMessage("Gas Price is too high and the value is " + gasPrice);
                    result.setTxStatus(OrderTxStatus.FAILED);
                    return;
                }
                if(gasPrice.compareTo(DEF_GAS_PRICE_STANDARD) < 0)
                {
//                    gasPrice = DEF_GAS_PRICE_BASIC;
                    gasPrice = DEF_GAS_PRICE_BASIC;
                }
                else
                {
                    gasPrice = gasPrice.add(DEF_GAS_PRICE_ADD_LEVEL_10_WEI);
                }
            }
            else if(this.mNetworkType == CryptoNetworkType.BNB_MAINNET)
            {
                gasPrice = Web3jHelper.DEF_GAS_PRICE_ADD_LEVEL_6_WEI;
            }
            else if(this.mNetworkType == CryptoNetworkType.MATIC_POLYGON || this.mNetworkType == CryptoNetworkType.MATIC_MUMBAI)
            {
                gasPrice = Web3jHelper.getGasPrice(web3j);
                if(gasPrice == null)
                {
                    gasPrice = Web3jHelper.DEF_GAS_PRICE_ADD_LEVEL_100_WEI;
                }
                else if(gasPrice.compareTo(Web3jHelper.DEF_GAS_PRICE_ADD_LEVEL_500_WEI) >= 0)
                {
                    result.setClearMessage("Gas Price is too high and the value is " + gasPrice);
                    result.setTxStatus(OrderTxStatus.FAILED);
                    return;
                }
                else if(gasPrice.compareTo(Web3jHelper.DEF_GAS_PRICE_ADD_LEVEL_100_WEI) < 0)
                {
                    gasPrice = Web3jHelper.DEF_GAS_PRICE_ADD_LEVEL_100_WEI;
                }
                else
                {
                    gasPrice = gasPrice.add(Web3jHelper.DEF_GAS_PRICE_ADD_LEVEL_50_WEI);
                }
            }
            else
            {
                result.setTxStatus(OrderTxStatus.FAILED);
                result.setClearMessage("get gasprice error");
                return;
            }

            String userApproveAddress = mLatestApproveContractAddr;
            if(!StringUtils.isEmpty(approveAddress))
            {
                userApproveAddress = approveAddress;
            }
            //LOG.info("gasLimit = " + gasLimit + ", gasPrice = " + gasPrice);

            String encodedFunction = FunctionEncoder.encode(function);

            RawTransaction rawTransaction =
                    RawTransaction.createTransaction(
                            nonce, gasPrice, gasLimit.toBigInteger(), userApproveAddress, encodedFunction);

            byte[] signedMessage = null;
            if(mNetworkType.getChainType() == CryptoChainType.MATIC)
            {
                signedMessage = TransactionEncoder.signMessage(rawTransaction, mNetworkType.getChainId(), mCredentials);
            }
            else
            {
                signedMessage = TransactionEncoder.signMessage(rawTransaction, mCredentials);
            }


            String hexValue = Numeric.toHexString(signedMessage);
            EthSendTransaction transactionResponse =
                    web3j.ethSendRawTransaction(hexValue).sendAsync().get();
            String txHash = transactionResponse.getTransactionHash();
            if(!StringUtils.isEmpty(txHash))
            {
                result.setExternalTxnid(txHash);
                result.setTxStatus(OrderTxStatus.WAITING);
            }
            else
            {
                if(transactionResponse != null && !StringUtils.isEmpty(transactionResponse.getError().getMessage()))
                {
                    result.setMsg(transactionResponse.getError().getMessage());
                }
                else
                {
                    result.setMsg("Not get txHash, pls check gasLimit!");
                }

                result.setTxStatus(OrderTxStatus.FAILED);
            }
        } catch (Exception e) {
            String msg = e.getMessage();
            result.setClearMessage(msg);
//            result.setTxStatus(OrderTxStatus.FAILED);
            LOG.error("transferFrom result: txnid = " + result.getTxnid() + ", externalTxnid = " + result.getExternalTxnid() + ", status = " + result.getTxStatus(), e);
        }
    }

    private BigDecimal toDivideAmount(BigDecimal amount)
    {
        return CoinAmountHelper.toDivideAmount(amount, mCurrencyDecimals);
    }

    public void test()
    {
        BigInteger gasPrice = null;
        try {
            gasPrice = Web3jHelper.getGasPrice(mWeb3j);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(gasPrice);
    }


    public static void main(String[] args) {
        MyConfiguration conf = MyConfiguration.getInstance();
        String ownerPrivateKey = conf.getString("coin.account.eth.trigger.privatekey");
        String ownerAddr = conf.getString("coin.account.eth.trigger.address");

//        String contractAddr = "0x656166b76FD72512dCcF7f5e91a6F4c5DBec29B5";
        String contractAddr = "0x1a2d8a0A54F4EE2Ef7eE66F9A75F692C4031B5f9"; // ropsten-tether-usdt

        String fromAddress = "0x43E123D9732F53540fcD54eF91CcE1D0d076bcf0";
        fromAddress = "0x05E6529fFF9C8262bC6cAD6dCB57628eE5311dF9";
        String toAddress = "0xF45Bd064Bc3354b5c5829914266F52A4f5573B08";
//        String toAddress2 = "0x43E123D9732F53540fcD54eF91CcE1D0d076bcf0";
        String toAddress3 = "0x05E6529fFF9C8262bC6cAD6dCB57628eE5311dF9";

        CryptoCurrency currency = CryptoCurrency.USDT;

        ContractInfo contractInfo = new ContractInfo();
        contractInfo.setTriggerPrivateKey(ContractInfo.encryptPrivateKey(ownerPrivateKey));
        contractInfo.setTriggerAddress(ownerAddr);

        ETHTokenProcessorImpl tokenSupport = new ETHTokenProcessorImpl(contractAddr, CryptoNetworkType.BNB_MAINNET);
        tokenSupport.updateTriggerInfo(contractInfo);
        tokenSupport.mCurrencyBaseMultiple = BigDecimalUtils.DEF_100;
//        tokenSupport.mBaseGasLimit = new BigDecimal(60000);
//        BigDecimal amount = tokenSupport.balanceOf(fromAddress);
//        System.out.println(fromAddress + " balance amount = " + amount);


//        String txnid = System.currentTimeMillis() + "";
//        BigDecimal amount2 = new BigDecimal(100);
//        TransactionResult transferResult = tokenSupport.transferFrom(fromAddress,
//                toAddress, amount2,
//                toAddress2, amount2,
//                toAddress3, amount2,
//                txnid);
//        System.out.println("trasfer from result = " + FastJsonHelper.jsonEncode(transferResult));

        // 7848690ae6a9233aea985555373eb45561883c34e69a93faa59d5a70ce51fbce
        // 2c810ecd5f53ae004bc9d2753524a1e22046734f5e867548aa19789554d45511
        // 2c810ecd5f53ae004bc9d2753524a1e22046734f5e867548aa19789554d45511
        String externalTxnid = "0xf88393947f1cb035461bc31db59d4449d88df75eee0c096a2a60416acc4124d5";
//        externalTxnid = transferResult.getExternalTxnid();
//        externalTxnid = "0x9fa535ee00af806b0aec79594b38fae9e35216d9442a9bc013dda97e6d38aa08";
//        TransactionResult getTransferResult = tokenSupport.getTransanctionStatus(externalTxnid);
//        System.out.println(FastJsonHelper.jsonEncode(getTransferResult));

//        BigDecimal allowance = tokenSupport.allowance(currency, testSenderAddr);
//        System.out.println(allowance);


        tokenSupport.test();


    }

}

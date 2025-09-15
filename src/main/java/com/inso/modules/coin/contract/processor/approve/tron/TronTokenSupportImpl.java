package com.inso.modules.coin.contract.processor.approve.tron;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.ByteString;
import com.inso.framework.conf.MyConfiguration;
import com.inso.framework.http.HttpSesstionManager;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.utils.BigDecimalUtils;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.coin.contract.helper.CoinAmountHelper;
import com.inso.modules.coin.contract.helper.TronHelper;
import com.inso.modules.coin.contract.processor.approve.ApproveTokenSupport;
import com.inso.modules.coin.contract.helper.Sha256Sm3Hash;
import com.inso.modules.coin.contract.processor.approve.tron.helper.TronTokenContractHelper;
import com.inso.modules.coin.contract.model.TransactionResult;
import com.inso.modules.coin.contract.processor.factory.TronFactory;
import com.inso.modules.coin.contract.processor.factory.TronNodeWrapper;
import com.inso.modules.coin.core.model.ContractInfo;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.paychannel.helper.PaymentRequestHelper;
import org.bouncycastle.util.encoders.Hex;
import org.tron.tronj.abi.FunctionEncoder;
import org.tron.tronj.abi.datatypes.Function;
import org.tron.tronj.client.TronClient;
import org.tron.tronj.client.transaction.TransactionBuilder;
import org.tron.tronj.proto.Chain;
import org.tron.tronj.proto.Contract;
import org.tron.tronj.proto.Response;

import java.io.IOException;
import java.math.BigDecimal;

public class TronTokenSupportImpl implements ApproveTokenSupport {

    private static Log LOG = LogFactory.getLog(TronTokenSupportImpl.class);

    /*** 能量限制- 预估 [6.x] TRC, 1trx = 1_000_000 sun,  ***/
    private static long DEFAULT_FEE_LIMIT = 8_000_000;

    private String mOwnerAddr;

    private String mCurrencyCtr;
    private String mLatestApproveContractAddr;

    private TronClient mClient;

    /*** 代币精度,转成对应的倍数 ***/
    private int mCurrencyDecimals;
    private BigDecimal mCurrencyBaseMultiple;

    private long mGasLimit = DEFAULT_FEE_LIMIT;

    private TronNodeWrapper mTronNodeWrapper;
    private CryptoNetworkType mNetworkType;

    private static String URL_getApproveList = "https://apilist.tronscanapi.com/api/account/approve/list?limit=20&start=0&type=project&address=";


    private static HttpSesstionManager mHttpMgr = HttpSesstionManager.getInstance();

    public TronTokenSupportImpl(String contractAddr, CryptoNetworkType networkType)
    {
        this.mLatestApproveContractAddr = contractAddr;
        this.mNetworkType = networkType;

        this.mTronNodeWrapper = TronFactory.getInstance().getWrapper(mNetworkType);

        //        String ownerAddr = "TJ4BzGrsVuvtebppfeaNC2nr9VFuPtn4Sf";
//        ByteString newOwnerAddr = TronClient.parseAddress(ownerAddr);
//        Base58Check.bytesToBase58(newOwnerAddr.toByteArray())

//        String contractAddr = "TTRJKyKKEmxtf4U4StBnJVQMMHkk9v2wq5";
//        ByteString newContractAddr = TronClient.parseAddress(contractAddr);
//        Base58Check.bytesToBase58(newContractAddr.toByteArray())
    }

    public void updateTriggerInfo(ContractInfo contractInfo)
    {
        // gas手续费用
        long gasLimit =contractInfo.getRemarkVO().getLongValue(ContractInfo.REMARK_KEY_GAS_LIMIT);
        if(gasLimit > 0)
        {
            this.mGasLimit = gasLimit;
        }

        this.mCurrencyCtr = contractInfo.getCurrencyCtrAddr();
        String triggerPrivateKey = contractInfo.getDecryptPrivateKey();
        if(!StringUtils.isEmpty(mOwnerAddr) && mOwnerAddr.equalsIgnoreCase(contractInfo.getTriggerAddress()))
        {
            return;
        }
        this.mOwnerAddr = contractInfo.getTriggerAddress();
        
        if(mNetworkType == CryptoNetworkType.TRX_GRID)
        {
            this.mClient = TronClient.ofMainnet(triggerPrivateKey);
        }
        else if(mNetworkType == CryptoNetworkType.TRX_NILE)
        {
            this.mClient = TronClient.ofNile(triggerPrivateKey);
        }
        else
        {
            LOG.error("invalid network config .............");
        }

        int currencyDecimals = contractInfo.getRemarkVO().getIntValue(ContractInfo.REMARK_KEY_CURRENCY_DECIMALS);
        if(!(currencyDecimals >= 0 && currencyDecimals <= 18))
        {
            currencyDecimals = 0;
        }
        this.mCurrencyDecimals = currencyDecimals;
        this.mCurrencyBaseMultiple = BigDecimalUtils.DEF_10.pow(currencyDecimals);


    }

    @Override
    public int getDecimals() {
        return mCurrencyDecimals;
    }

    @Override
    public String getCurrencyCtrAddress() {
        return mCurrencyCtr;
    }

    @Override
    public TransactionResult transferFrom(String fromAddress, String toAddress, BigDecimal amount, String txnid) {
        return transferFrom(fromAddress,
                toAddress, amount,
                null, null,
                null, null,
                null, null, txnid, null);
    }

    @Override
    public TransactionResult transferFrom(String fromAddress, String toAddress1, BigDecimal amount1, String toAddress2, BigDecimal amount2, String txnid) {
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

        Function function = TronTokenContractHelper.transferFrom(mCurrencyCtr, fromAddress,
                toAddress1, newAmount1,
                toAddress2, newAmount2,
                toAddress3, newAmount3,
                toAddress4, newAmount4);
        String encodedHex = FunctionEncoder.encode(function);
        ByteString dataEncode = TronClient.parseHex(encodedHex);

        int multipleCount = 0;
        if(!StringUtils.isEmpty(toAddress1)) multipleCount++;
        if(!StringUtils.isEmpty(toAddress2)) multipleCount++;
        if(!StringUtils.isEmpty(toAddress3)) multipleCount++;
        if(!StringUtils.isEmpty(toAddress4)) multipleCount++;

        handleSignTransaction(result, dataEncode, mGasLimit * multipleCount, approveAddress);
        return result;
    }

    private void handleSignTransaction(TransactionResult result, ByteString dataEncode, long gasLimit, String approveAddress)
    {
        String userApproveAddress = mLatestApproveContractAddr;
        if(!StringUtils.isEmpty(approveAddress))
        {
            userApproveAddress = approveAddress;
        }
        String externalTxnid = null;
        try {
            TronClient client = mClient;

            ByteString ownerAddr = TronClient.parseAddress(mOwnerAddr);
            ByteString newContractAddr = TronClient.parseAddress(userApproveAddress);

            Contract.TriggerSmartContract trigger = Contract.TriggerSmartContract.newBuilder()
                    .setOwnerAddress(ownerAddr)
                    .setContractAddress(newContractAddr)
    //                    .setCallValue() //注入TRX
                    .setData(dataEncode).build();

            Response.TransactionExtention txnExt = client.blockingStub.triggerConstantContract(trigger);

            TransactionBuilder myBuilder = new TransactionBuilder(txnExt.getTransaction());

            // 1trx = 1000000 sun, 单位为sun
            myBuilder.setFeeLimit(gasLimit); // 预估6.x TRC
            //myBuilder.setMemo(result.getTxnid()); // 备注

            Chain.Transaction signedTxn = client.signTransaction(myBuilder.build());
            Response.TransactionReturn ret = client.broadcastTransaction(signedTxn);

            // 事务id
            externalTxnid = Hex.toHexString(Sha256Sm3Hash.hash(
                    signedTxn.getRawData().toByteArray()));

            result.setExternalTxnid(externalTxnid);

            if(ret.getResult())
            {
                result.setTxStatus(OrderTxStatus.WAITING);
            }
            else
            {
                result.setMsg(ret.getMessage().toStringUtf8());
                result.setTxStatus(OrderTxStatus.FAILED);

                LOG.info("rs = handleSignTransaction ====" + ret.getMessage().toStringUtf8());
            }
        } catch (Exception e) {
            result.setTxStatus(OrderTxStatus.FAILED);
            LOG.error("transferFrom result: txnid = " + result.getTxnid() + ", externalTxnid = " + result.getExternalTxnid() + ", status = " + result.getTxStatus(), e);
        }

    }

    public TransactionResult getTransanctionStatus(String externalTxnid)
    {
        return TronHelper.getTransanctionStatus(mTronNodeWrapper.getNode(), externalTxnid);
    }

    private BigDecimal toDivideAmount(BigDecimal amount)
    {
        return CoinAmountHelper.toDivideAmount(amount, mCurrencyDecimals);
    }

    public static void test1()
    {
        MyConfiguration conf = MyConfiguration.getInstance();
        String ownerPrivateKey = conf.getString("coin.account.trx.trigger.privatekey");
        String ownerAddr = conf.getString("coin.account.trx.trigger.address");

        String contractAddr = "TX6dXtUP9FfZBBdoYtr3oCvnD3XXFBkTx1";
        contractAddr = "TG7MT1V3M15oK7rwUWNWgMa3AtkSRiM9oY"; // 正式的合约地址

        String fromAddress = "TLohCbpHKTzHZGjLZenkUipDC8yLjGNHSx";
        fromAddress = "TJtgpkWPR3hDzZCMUnhEoRGaF7NNFQmBi5";

        String toAddress1 = "TJ4BzGrsVuvtebppfeaNC2nr9VFuPtn4Sf";
        String toAddress2 = "TLVpywaFM4ryBx5fxY4xNb79P1nHXC5JGM";
        String toAddress3 = "TGx3EHQJdJ4avyENfsXieL8KRYP6ZFXvTY";


        CryptoCurrency currency = CryptoCurrency.USDT;

        ContractInfo contractInfo = new ContractInfo();
//        contractInfo.setCurrencyCtrAddr();
        contractInfo.setTriggerPrivateKey(ContractInfo.encryptPrivateKey(ownerPrivateKey));
        contractInfo.setTriggerAddress(ownerAddr);

        TronTokenSupportImpl tokenSupport = new TronTokenSupportImpl(contractAddr, CryptoNetworkType.TRX_GRID);
        tokenSupport.updateTriggerInfo(contractInfo);

        tokenSupport.mCurrencyDecimals = 6;
        tokenSupport.mCurrencyBaseMultiple = BigDecimalUtils.DEF_100;
        tokenSupport.mCurrencyBaseMultiple = BigDecimalUtils.DEF_10.pow(6);
//        BigDecimal amount = tokenSupport.balanceOf(toAddress3);
//        System.out.println("amount = " + amount);
//
//        BigDecimal allowance = tokenSupport.allowance(toAddress3);
//        System.out.println("allowance = " + allowance);

//        String recipientAddr = tokenSupport.getRecipientAddr(currency.getKey());
//        System.out.println("recipientAddr = " + recipientAddr);


//        String txnid = System.currentTimeMillis() + "";
//        BigDecimal amount = new BigDecimal(10);
//        TransactionResult transferResult = tokenSupport.transferFrom(currency, fromAddress,
//                toAddress1, amount,
//                toAddress2, amount,
//                toAddress1, amount,
//                toAddress2, amount,
//                txnid);
//        System.out.println(FastJsonHelper.jsonEncode(transferResult));

        // 7848690ae6a9233aea985555373eb45561883c34e69a93faa59d5a70ce51fbce
        // 2c810ecd5f53ae004bc9d2753524a1e22046734f5e867548aa19789554d45511
        // 2c810ecd5f53ae004bc9d2753524a1e22046734f5e867548aa19789554d45511
//        String externalTxnid = "82aa4fda79d673f12be427fc9c43105b3b80fd1fcb87de5caeef7991f4f49b52";
//        TransactionResult getTransferResult = tokenSupport.getTransanctionStatus(externalTxnid);
//        System.out.println(FastJsonHelper.jsonEncode(getTransferResult));

//        BigDecimal allowance = tokenSupport.allowance(currency, testSenderAddr);
//        System.out.println(allowance);
    }

    public static void main(String[] args) {
    }
}

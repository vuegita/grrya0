package com.inso.modules.coin.contract.helper;

import com.inso.framework.cache.CacheManager;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.utils.CollectionUtils;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.coin.contract.model.TransactionResult;
import com.inso.modules.coin.contract.processor.factory.Web3jFactory;
import com.inso.modules.coin.contract.processor.factory.Web3jNodeWrapper;
import com.inso.modules.coin.core.model.CryptoChainType;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.common.model.OrderTxStatus;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.utils.Numeric;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Web3jHelper {

    private static final String ROOT_CACHE = Web3jHelper.class.getName();
    private static final String NONCE_CACHE = ROOT_CACHE + "_nonce";
    private static final BigInteger DEFAULT_NONCE_ADD_1 = BigInteger.valueOf(1);

    private static final String ERR_ALREADY_KNOWN = "already known";

    private static Log LOG = LogFactory.getLog(Web3jHelper.class);

    private static BigInteger DEF_GAS_WEI_1 = new BigDecimal(1_000_000_000).toBigInteger();

    public static final BigInteger DEF_GAS_PRICE_STANDARD = new BigDecimal("60000000000").toBigInteger();
    public static final BigInteger DEF_GAS_PRICE_BASIC = new BigDecimal("65000000000").toBigInteger();
    public static final BigInteger DEF_GAS_PRICE_ADD_LEVEL_10_WEI = new BigDecimal("10000000000").toBigInteger();
    public static final BigInteger DEF_GAS_PRICE_ADD_LEVEL_5_WEI = new BigDecimal("5000000000").toBigInteger();
    public static final BigInteger DEF_GAS_PRICE_ADD_LEVEL_6_WEI = DEF_GAS_WEI_1.multiply(BigInteger.valueOf(6));

    public static final BigInteger DEF_GAS_PRICE_ADD_LEVEL_20_WEI = DEF_GAS_WEI_1.multiply(BigInteger.valueOf(20));
    public static final BigInteger DEF_GAS_PRICE_ADD_LEVEL_50_WEI = DEF_GAS_WEI_1.multiply(BigInteger.valueOf(50));
    public static final BigInteger DEF_GAS_PRICE_ADD_LEVEL_100_WEI = DEF_GAS_WEI_1.multiply(BigInteger.valueOf(100));

    public static final BigInteger DEF_GAS_PRICE_ADD_LEVEL_300_WEI = DEF_GAS_WEI_1.multiply(BigInteger.valueOf(300));
    public static final BigInteger DEF_GAS_PRICE_ADD_LEVEL_500_WEI = DEF_GAS_WEI_1.multiply(BigInteger.valueOf(500));




    public static BigInteger getNonce(Web3j web3j, String address) throws ExecutionException, InterruptedException {
        EthGetTransactionCount ethGetTransactionCount =
                web3j.ethGetTransactionCount(address, DefaultBlockParameterName.LATEST)
                        .sendAsync()
                        .get();

        return ethGetTransactionCount.getTransactionCount();
    }

    public static BigInteger getGasPrice(Web3j web3j) {
        try {
            EthGasPrice gasPrice = web3j.ethGasPrice().sendAsync().get();
            return gasPrice.getGasPrice();
        } catch (InterruptedException e) {
        } catch (ExecutionException e) {
        }
        return null;
    }


    public static Uint256 getNumberValue(Web3j web3j, String tokenAddress, Function function) throws ExecutionException, InterruptedException {
        String encodedFunction = FunctionEncoder.encode(function);
        org.web3j.protocol.core.methods.response.EthCall response = web3j.ethCall(
                Transaction.createEthCallTransaction(null, tokenAddress, encodedFunction),
                DefaultBlockParameterName.LATEST)
                .sendAsync().get();
        List<Type> results = FunctionReturnDecoder.decode(response.getValue(), function.getOutputParameters());
        if(CollectionUtils.isEmpty(results))
        {
            return null;
        }
        Uint256 preValue = (Uint256)results.get(0);
        return preValue;
    }

    public static String getStringValue(Web3j web3j, String tokenAddress, Function function) throws ExecutionException, InterruptedException {
        String encodedFunction = FunctionEncoder.encode(function);
        org.web3j.protocol.core.methods.response.EthCall response = web3j.ethCall(
                Transaction.createEthCallTransaction(null, tokenAddress, encodedFunction),
                DefaultBlockParameterName.LATEST)
                .sendAsync().get();
        List<Type> results = FunctionReturnDecoder.decode(response.getValue(), function.getOutputParameters());
        if(CollectionUtils.isEmpty(results))
        {
            return null;
        }
        Utf8String preValue = (Utf8String)results.get(0);
        return preValue.getValue();
    }


    public static void handleSignTransaction(TransactionResult result, Web3j web3j, CryptoNetworkType networkType, Credentials credentials, String contractAddr, Function function, BigDecimal gasLimit) {
        try {

            String lockKey = networkType.getKey() + credentials.getAddress();
            synchronized (lockKey)
            {
                String cachekey = NONCE_CACHE + lockKey;

                BigInteger cacheNonce = CacheManager.getInstance().getObject(cachekey, BigInteger.class);

                BigInteger nonce = Web3jHelper.getNonce(web3j, credentials.getAddress());
                if(nonce == null)
                {
                    result.setClearMessage("Fetch Nonce timeout!");
                    result.setTxStatus(OrderTxStatus.FAILED);
                    return;
                }
                if(cacheNonce != null && cacheNonce.compareTo(nonce) >= 0)
                {
                    nonce = cacheNonce.add(DEFAULT_NONCE_ADD_1);
                }

                BigInteger gasPrice = null;

                if(networkType == CryptoNetworkType.ETH_MAINNET)
                {
                    gasPrice = Web3jHelper.getGasPrice(web3j);
                    if(gasPrice.compareTo(DEF_GAS_PRICE_ADD_LEVEL_500_WEI) >= 0)
                    {
                        result.setClearMessage("Gas Price is too high and the value is " + gasPrice);
                        result.setTxStatus(OrderTxStatus.FAILED);
                        return;
                    }
                    gasPrice = gasPrice.add(DEF_GAS_PRICE_ADD_LEVEL_10_WEI);
                }
                else if(networkType == CryptoNetworkType.BNB_MAINNET)
                {
                    gasPrice = DEF_GAS_PRICE_ADD_LEVEL_6_WEI;
                }
                else if(networkType == CryptoNetworkType.MATIC_POLYGON)
                {
//                    gasPrice = DEF_GAS_PRICE_ADD_LEVEL_100_WEI;
                    gasPrice = Web3jHelper.getGasPrice(web3j);
                    gasPrice = gasPrice.add(DEF_GAS_PRICE_ADD_LEVEL_50_WEI);
                }
                else
                {
                    if(networkType.isTest())
                    {
                        gasPrice = Web3jHelper.getGasPrice(web3j);
                    }
                    else
                    {
                        result.setTxStatus(OrderTxStatus.FAILED);
                        result.setClearMessage("get gasprice error");
                        return;
                    }
                }

                String encodedFunction = FunctionEncoder.encode(function);

                RawTransaction rawTransaction =
                        RawTransaction.createTransaction(
                                nonce, gasPrice, gasLimit.toBigInteger(), contractAddr, encodedFunction);

                byte[] signedMessage = null;
                if(networkType.getChainType() == CryptoChainType.MATIC)
                {
                    signedMessage = TransactionEncoder.signMessage(rawTransaction, networkType.getChainId(), credentials);
                }
                else
                {
                    signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
                }


                String hexValue = Numeric.toHexString(signedMessage);

                EthSendTransaction transactionResponse =
                        web3j.ethSendRawTransaction(hexValue).sendAsync().get();
                String txHash = transactionResponse.getTransactionHash();

                if(!StringUtils.isEmpty(txHash))
                {
                    result.setExternalTxnid(txHash);
                    result.setTxStatus(OrderTxStatus.WAITING);

                    int expires = 30;
                    if(networkType == CryptoNetworkType.ETH_MAINNET)
                    {
                        expires = 45;
                    }
                    CacheManager.getInstance().setString(cachekey, nonce.toString(), expires);
                }
                else
                {
                    String errmsg = transactionResponse.getError().getMessage();
                    if(ERR_ALREADY_KNOWN.equalsIgnoreCase(errmsg))
                    {
                        result.setClearMessage("Not return txHash, " + transactionResponse.getError().getMessage() + ", nonce = " + nonce);
                        result.setTxStatus(OrderTxStatus.PENDING);
                    }
                    else
                    {
                        result.setClearMessage("Not return txHash, " + transactionResponse.getError().getMessage() + ", nonce = " + nonce);
                        result.setTxStatus(OrderTxStatus.FAILED);
                    }

                }
            }

        } catch (Exception e) {
            String msg = e.getMessage();
            result.setClearMessage(msg);
            result.setTxStatus(OrderTxStatus.FAILED);
            LOG.error("handleSignTransaction result: txnid = " + result.getTxnid() + ", externalTxnid = " + result.getExternalTxnid() + ", status = " + result.getTxStatus(), e);
        }
    }

    public static TransactionResult getTransanctionStatus(Web3j web3j, String externalTxnid) {
        try {
            Request<?, EthGetTransactionReceipt> request = web3j.ethGetTransactionReceipt(externalTxnid);
            EthGetTransactionReceipt transaction = request.sendAsync().get();

            // 状态未出时会报错
            if(!transaction.getTransactionReceipt().isPresent())
            {
                return null;
            }
            TransactionReceipt transactionReceipt = transaction.getResult();
            boolean statusOk = transactionReceipt.isStatusOK();
            TransactionResult result = new TransactionResult();
            if(statusOk)
            {
                result.setTxStatus(OrderTxStatus.REALIZED);
            }
            else
            {
                result.setTxStatus(OrderTxStatus.FAILED);
            }
            return result;
        } catch (Exception e) {
            LOG.error("getTransanctionStatus error:", e);
        }
        return null;
    }

    private static void test1() throws ExecutionException, InterruptedException {
        CryptoNetworkType networkType = CryptoNetworkType.MATIC_POLYGON;
        Web3jFactory factory = Web3jFactory.getInstance();
        Web3jNodeWrapper web3jNodeWrapper = factory.getWrapper(networkType);

        BigInteger gasPrice = Web3jHelper.getGasPrice(web3jNodeWrapper.getNode());
        System.out.println("gasPrice = " + gasPrice);


        BigInteger val = getNonce(web3jNodeWrapper.getNode(), "0x5b1f84e5993f604f3f2a310328982bdd5e60b362");
        System.out.println("val = " + val);
    }


    public static void main(String[] args) throws ExecutionException, InterruptedException {
        test1();

    }
}

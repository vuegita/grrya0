package com.inso.modules.coin.contract.helper;

import com.google.protobuf.ByteString;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.utils.CollectionUtils;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.coin.contract.model.TransactionResult;
import com.inso.modules.common.model.OrderTxStatus;
import org.bouncycastle.util.encoders.Hex;
import org.tron.tronj.abi.FunctionEncoder;
import org.tron.tronj.abi.FunctionReturnDecoder;
import org.tron.tronj.abi.datatypes.Function;
import org.tron.tronj.client.TronClient;
import org.tron.tronj.client.exceptions.IllegalException;
import org.tron.tronj.client.transaction.TransactionBuilder;
import org.tron.tronj.crypto.SECP256K1;
import org.tron.tronj.proto.Chain;
import org.tron.tronj.proto.Contract;
import org.tron.tronj.proto.Response;
import org.tron.tronj.utils.Numeric;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TronHelper {

    private static Log LOG = LogFactory.getLog(TronHelper.class);

    private static final String TX_STATUS_FAILED = "FAILED";
    private static final String TX_STATUS_SUCCESS = "SUCESS";

    public static BigInteger getNumberValue(TronClient client, String ownerAddr, String tokenContractAddr, Function function)
    {
        Response.TransactionExtention txnExt = client.constantCall(ownerAddr, tokenContractAddr, function);
        List<ByteString> constantResultList = txnExt.getConstantResultList();
        List<String> resultList = constantResultList.stream()
                .map(value -> Numeric.toHexString(value.toByteArray()))
                .collect(Collectors.toList());

        if(CollectionUtils.isEmpty(resultList))
        {
            return null;
        }

        List<Object> returnValues = Stream.iterate(0, x -> ++x)
                .limit(resultList.size())
                .map(x -> FunctionReturnDecoder.decode(resultList.get(x), function.getOutputParameters()).get(x).getValue())
                .collect(Collectors.toList());

        if(CollectionUtils.isEmpty(returnValues))
        {
            return null;
        }

        return (BigInteger) returnValues.get(0);
    }

    public static String getStringValue(TronClient client, String ownerAddr, String tokenContractAddr, Function function)
    {
        Response.TransactionExtention txnExt = client.constantCall(ownerAddr, tokenContractAddr, function);
        List<ByteString> constantResultList = txnExt.getConstantResultList();
        List<String> resultList = constantResultList.stream()
                .map(value -> Numeric.toHexString(value.toByteArray()))
                .collect(Collectors.toList());

        if(CollectionUtils.isEmpty(resultList))
        {
            return null;
        }

        List<Object> returnValues = Stream.iterate(0, x -> ++x)
                .limit(resultList.size())
                .map(x -> FunctionReturnDecoder.decode(resultList.get(x), function.getOutputParameters()).get(x).getValue())
                .collect(Collectors.toList());

        if(CollectionUtils.isEmpty(returnValues))
        {
            return null;
        }
        return returnValues.get(0).toString();
    }

    public static void handleSignTransaction(TransactionResult result, TronClient client, String tokenContractAddr, Function function, long gasLimit, String triggerAddress)
    {
        String externalTxnid = null;
        try {

            String encodedHex = FunctionEncoder.encode(function);
            ByteString dataEncode = TronClient.parseHex(encodedHex);

            ByteString ownerAddr = TronClient.parseAddress(triggerAddress);
            ByteString newContractAddr = TronClient.parseAddress(tokenContractAddr);

            Contract.TriggerSmartContract trigger = Contract.TriggerSmartContract.newBuilder()
                    .setOwnerAddress(ownerAddr)
                    .setContractAddress(newContractAddr)
                    //                    .setCallValue() //注入TRX
                    .setData(dataEncode).build();

            Response.TransactionExtention txnExt = client.blockingStub.triggerConstantContract(trigger);

            TransactionBuilder myBuilder = new TransactionBuilder(txnExt.getTransaction());

            // 1trx = 1000000 sun, 单位为sun
            myBuilder.setFeeLimit(gasLimit); // 预估6.x TRC
            if(!StringUtils.isEmpty(result.getTxnid()))
            {
                myBuilder.setMemo(result.getTxnid());
            }

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
                result.setTxStatus(OrderTxStatus.FAILED);
            }
            result.setMsg(ret.getMessage().toStringUtf8());
        } catch (Exception e) {
            result.setTxStatus(OrderTxStatus.FAILED);
            LOG.error("transferFrom result: txnid = " + result.getTxnid() + ", externalTxnid = " + result.getExternalTxnid() + ", status = " + result.getTxStatus(), e);
        }

    }

    public static void handleSignTransactionByPrivateKey(TransactionResult result, TronClient client, String tokenContractAddr,
                                                         Function function, long gasLimit, String triggerPrivateKey, String triggerAddress)
    {
        String externalTxnid = null;
        try {

            String encodedHex = FunctionEncoder.encode(function);
            ByteString dataEncode = TronClient.parseHex(encodedHex);

            SECP256K1.PrivateKey privateKey = SECP256K1.PrivateKey.create(triggerPrivateKey);
            SECP256K1.KeyPair kp = SECP256K1.KeyPair.create(privateKey);

            ByteString ownerAddr = TronClient.parseAddress(triggerAddress);
            ByteString newContractAddr = TronClient.parseAddress(tokenContractAddr);

            Contract.TriggerSmartContract trigger = Contract.TriggerSmartContract.newBuilder()
                    .setOwnerAddress(ownerAddr)
                    .setContractAddress(newContractAddr)
                    //                    .setCallValue() //注入TRX
                    .setData(dataEncode).build();

            Response.TransactionExtention txnExt = client.blockingStub.triggerConstantContract(trigger);

            TransactionBuilder myBuilder = new TransactionBuilder(txnExt.getTransaction());

            // 1trx = 1000000 sun, 单位为sun
            myBuilder.setFeeLimit(gasLimit); // 预估6.x TRC
            if(!StringUtils.isEmpty(result.getTxnid()))
            {
                myBuilder.setMemo(result.getTxnid());
            }

            Chain.Transaction signedTxn = client.signTransaction(myBuilder.build(), kp);
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
                result.setTxStatus(OrderTxStatus.FAILED);
            }
            result.setMsg(ret.getMessage().toStringUtf8());
        } catch (Exception e) {
            result.setTxStatus(OrderTxStatus.FAILED);
            LOG.error("transferFrom result: txnid = " + result.getTxnid() + ", externalTxnid = " + result.getExternalTxnid() + ", status = " + result.getTxStatus(), e);
        }

    }

    public static TransactionResult getTransanctionStatus(TronClient client, String externalTxnid)
    {
        try {
            String txid = externalTxnid;
            Response.TransactionInfo tx = client.getTransactionInfoById(txid);
            String txStatus = tx.getResult().toString();

            TransactionResult result = new TransactionResult();
            result.setTxStatus(OrderTxStatus.WAITING);
            if(TX_STATUS_FAILED.equalsIgnoreCase(txStatus))
            {
                String msg = tx.getResMessage().toStringUtf8();
                result.setMsg(msg);
                result.setTxStatus(OrderTxStatus.FAILED);
            }
            else if(TX_STATUS_SUCCESS.equalsIgnoreCase(txStatus))
            {
                result.setTxStatus(OrderTxStatus.REALIZED);
            }
            return result;
        }
        catch (IllegalException e)
        {
            TransactionResult result = new TransactionResult();
            result.setTxStatus(OrderTxStatus.FAILED);
            result.setClearMessage(e.getMessage());
            return result;
        }
        catch (Exception e) {
            LOG.error("handle getTransanctionStatus error: ", e);
        }
        return null;
    }

}

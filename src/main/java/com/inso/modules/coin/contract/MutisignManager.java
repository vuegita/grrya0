package com.inso.modules.coin.contract;

import com.google.protobuf.ByteString;
import com.inso.framework.conf.MyConfiguration;
import com.inso.framework.context.MyEnvironment;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.utils.SignDataHelper;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.coin.contract.helper.CoinAmountHelper;
import com.inso.modules.coin.contract.helper.Sha256Sm3Hash;
import com.inso.modules.coin.contract.helper.TRC20FunctHelper;
import com.inso.modules.coin.contract.model.TransactionResult;
import com.inso.modules.coin.contract.processor.factory.TronFactory;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.coin.core.model.TokenAssertConfig;
import com.inso.modules.coin.core.model.TokenAssertInfo;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.common.model.Status;
import org.bouncycastle.util.encoders.Hex;
import org.tron.tronj.abi.FunctionEncoder;
import org.tron.tronj.abi.datatypes.Function;
import org.tron.tronj.client.TronClient;
import org.tron.tronj.client.exceptions.IllegalException;
import org.tron.tronj.client.transaction.TransactionBuilder;
import org.tron.tronj.crypto.SECP256K1;
import org.tron.tronj.proto.Chain;
import org.tron.tronj.proto.Contract;
import org.tron.tronj.proto.Response;

import java.math.BigDecimal;

public class MutisignManager {

    private static Log LOG = LogFactory.getLog(MutisignManager.class);

    private String mTriggerAddress;
    private String mTriggerPrivateKey;

    private SignDataHelper helper = new SignDataHelper("fdsafjhsadf(ksfh");

    public static MutisignManager getInstance()
    {
        return MyInternal.mgr;
    }

    private interface MyInternal {
        public static MutisignManager mgr = new MutisignManager();
    }

    private MutisignManager()
    {
        this.mTriggerPrivateKey = MyConfiguration.getInstance().getString("coin.mutisign.trigger.privatekey");
        this.mTriggerAddress = MyConfiguration.getInstance().getString("coin.mutisign.trigger.address");

        if(!MyEnvironment.isDev())
        {
            this.mTriggerPrivateKey = helper.decryptPrivateKey(mTriggerPrivateKey);
        }
    }

    public String getTriggerAddress()
    {
        return mTriggerAddress;
    }

    public Status verifyExistOwner(CryptoNetworkType networkType, String address)
    {
        return NativeTokenManager.getInstance().verifyExistOwner(networkType, this.mTriggerAddress, address);
    }

    public TransactionResult transfer(CryptoNetworkType networkType, CryptoCurrency currency, String from, String to, BigDecimal amount)
    {
        if(currency == CryptoCurrency.TRX)
        {
            return transferTRX(networkType, currency, from, to, amount);
        }

        return transferToken(networkType, currency, from, to, amount);
    }

    private TransactionResult transferTRX(CryptoNetworkType networkType, CryptoCurrency currency, String from, String to, BigDecimal amount)
    {
        TransactionResult transactionResult = new TransactionResult();
        try {

            int decimals = networkType.getNativeTokenDecimals();
            if(currency != CryptoCurrency.TRX)
            {
                TokenAssertInfo assertInfo = TokenAssertConfig.getTokenInfo(networkType, currency);
                decimals = assertInfo.getDecimals();
            }

            BigDecimal newAmount = CoinAmountHelper.toMultipleAmount(amount, decimals);
            TronClient client = TronFactory.getInstance().getWrapper(networkType).getWriterClient();
            Response.TransactionExtention txnExt = client.transfer(from, to, newAmount.longValue());

            SECP256K1.PrivateKey privateKey = SECP256K1.PrivateKey.create(this.mTriggerPrivateKey);
            SECP256K1.KeyPair kp = SECP256K1.KeyPair.create(privateKey);

            Chain.Transaction signedTxn = client.signTransaction(txnExt, kp);

            Response.TransactionReturn ret = client.blockingStub.broadcastTransaction(signedTxn);
            // 事务id
            String externalTxnid = Hex.toHexString(Sha256Sm3Hash.hash(signedTxn.getRawData().toByteArray()));
            //System.out.println("externalTxnid = " + externalTxnid);

            if(ret.getResult())
            {
                transactionResult.setTxStatus(OrderTxStatus.WAITING);
                transactionResult.setExternalTxnid(externalTxnid);
            }
            else
            {
                transactionResult.setTxStatus(OrderTxStatus.FAILED);
            }

        } catch (IllegalException e) {
            LOG.error("transfer TRX error:", e);
            transactionResult.setTxStatus(OrderTxStatus.FAILED);
        }

        return transactionResult;
    }

    private TransactionResult transferToken(CryptoNetworkType networkType, CryptoCurrency currency, String from, String to, BigDecimal amount)
    {
        TransactionResult result = new TransactionResult();

        TokenAssertInfo assertInfo = TokenAssertConfig.getTokenInfo(networkType, currency);
        BigDecimal newAmount = CoinAmountHelper.toMultipleAmount(amount, assertInfo.getDecimals());
        Function function = TRC20FunctHelper.transfer(to, newAmount.toBigInteger());

        String externalTxnid = null;
        try {

            TronClient client = TronFactory.getInstance().getWrapper(networkType).getWriterClient();

            String encodedHex = FunctionEncoder.encode(function);
            ByteString dataEncode = TronClient.parseHex(encodedHex);

            SECP256K1.PrivateKey privateKey = SECP256K1.PrivateKey.create(mTriggerPrivateKey);
            SECP256K1.KeyPair kp = SECP256K1.KeyPair.create(privateKey);

            ByteString ownerAddr = TronClient.parseAddress(from);
            ByteString newContractAddr = TronClient.parseAddress(assertInfo.getContractAddress());

            Contract.TriggerSmartContract trigger = Contract.TriggerSmartContract.newBuilder()
                    .setOwnerAddress(ownerAddr)
                    .setContractAddress(newContractAddr)
                    .setData(dataEncode).build();

            Response.TransactionExtention txnExt = client.blockingStub.triggerConstantContract(trigger);

            TransactionBuilder myBuilder = new TransactionBuilder(txnExt.getTransaction());

            myBuilder.setFeeLimit(30_000_000); //
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

        return result;
    }

    private void testEncode()
    {
        String str = "TMAUzgUq3EZJVNfihP3tgnAj9osgk6xTEi";
        String encrypt = helper.encryptPrivateKey(str);
        System.out.println(encrypt);
        System.out.println(helper.decryptPrivateKey(encrypt));
    }

    public void test2()
    {
        this.mTriggerAddress = "";
        String address = "TLuUrbV6vWFxFe6XAxjvbui9Ng3wE6Bp1f";
    }


    public static void main(String[] args) {


//        System.out.println(MutisignManager.getInstance().getTriggerAddress());

        MutisignManager.getInstance().testEncode();

    }


}

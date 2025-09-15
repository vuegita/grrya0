package com.inso.modules.coin.contract.processor.navtive.eth;

import com.inso.modules.coin.contract.helper.CoinAmountHelper;
import com.inso.modules.coin.contract.processor.navtive.NativeTokenSupport;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.common.model.Status;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;

import java.math.BigDecimal;

public class ETHNativeTokenProcessor implements NativeTokenSupport {

    protected CryptoNetworkType mNetworkType;
    private Web3j mWeb3j;

    /*** 代币精度,转成对应的倍数 ***/
    protected int mCurrencyDecimals;

    public ETHNativeTokenProcessor(CryptoNetworkType networkType) {
        this.mNetworkType = networkType;
        this.mWeb3j = Web3j.build(new org.web3j.protocol.http.HttpService(networkType.getApiServer()));

        this.mCurrencyDecimals = mNetworkType.getNativeTokenDecimals();
    }

    @Override
    public CryptoNetworkType getNeworkType() {
        return mNetworkType;
    }

    @Override
    public BigDecimal getBalance(String address) {

        try {
            EthGetBalance balanceObj = mWeb3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).send();
            BigDecimal balance = new BigDecimal(balanceObj.getBalance());
            return toDivideAmount(balance);
        } catch (Exception e) {
        }
        return BigDecimal.ZERO;
    }

    @Override
    public Status verifyExistOwner(String ownerAddress, String address) {
        return null;
    }

    private BigDecimal toDivideAmount(BigDecimal amount)
    {
        return CoinAmountHelper.toDivideAmount(amount, mCurrencyDecimals);
    }

    public static void main(String[] args) {
        ETHNativeTokenProcessor support = new ETHNativeTokenProcessor(CryptoNetworkType.BNB_TESTNET);

        BigDecimal balance = support.getBalance("0xF45Bd064Bc3354b5c5829914266F52A4f5573B08");
        System.out.println(balance);

    }
}

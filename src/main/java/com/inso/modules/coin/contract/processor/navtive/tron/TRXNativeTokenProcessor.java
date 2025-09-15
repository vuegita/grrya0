package com.inso.modules.coin.contract.processor.navtive.tron;

import com.inso.framework.conf.MyConfiguration;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.utils.BigDecimalUtils;
import com.inso.modules.coin.contract.helper.CoinAmountHelper;
import com.inso.modules.coin.contract.processor.factory.TronFactory;
import com.inso.modules.coin.contract.processor.factory.TronNodeWrapper;
import com.inso.modules.coin.contract.processor.navtive.NativeTokenSupport;
import com.inso.modules.coin.contract.processor.token20.eth.ERC20TokenProcessor;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.common.model.Status;
import org.tron.tronj.client.TronClient;
import org.tron.tronj.proto.Common;
import org.tron.tronj.proto.Response;
import org.tron.tronj.utils.Base58Check;

import java.math.BigDecimal;

public class TRXNativeTokenProcessor implements NativeTokenSupport {

    private static String ROOT_CACHE_KEY = ERC20TokenProcessor.class.getName();

    private static Log LOG = LogFactory.getLog(ERC20TokenProcessor.class);

    private static final String DEFAULT_OWNER_PRIVATE_KEY = "773ac2f9c388b4ff8c7654013cff0fc6ecc103a9f2928b38ee8f5525c1e113ca";
    private static final String DEFAULT_OWNER_ADDRESS = "TLVpywaFM4ryBx5fxY4xNb79P1nHXC5JGM";

    private CryptoNetworkType mNetworkType;


    private static String URL_getApproveList = "https://apilist.tronscanapi.com/api/account/approve/list?limit=20&start=0&type=project&address=";

    /*** 代币精度,转成对应的倍数 ***/
    protected int mCurrencyDecimals;
    public BigDecimal mCurrencyBaseMultiple;

    private TronNodeWrapper mTronWrapper;

    public TRXNativeTokenProcessor(CryptoNetworkType networkType) {
        this.mNetworkType = networkType;

        this.mCurrencyDecimals = mNetworkType.getNativeTokenDecimals();
        this.mCurrencyBaseMultiple = BigDecimalUtils.DEF_10.pow(mCurrencyDecimals);

        this.mTronWrapper = TronFactory.getInstance().getWrapper(networkType);
    }

    @Override
    public CryptoNetworkType getNeworkType() {
        return mNetworkType;
    }

    @Override
    public BigDecimal getBalance(String address) {
        TronClient client = mTronWrapper.getNode();
        BigDecimal balance = new BigDecimal(client.getAccountBalance(address));
        return toDivideAmount(balance);
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

    private BigDecimal toDivideAmount(BigDecimal amount)
    {
        return CoinAmountHelper.toDivideAmount(amount, mCurrencyDecimals);
    }

    public Status verifyExistOwner(String ownerAddress, String address)
    {
        Response.Account accountInfo = mTronWrapper.getNode().getAccount(address);
        Common.Permission ownerPermission = accountInfo.getOwnerPermission();
        if(ownerPermission.getKeysCount() <= 0)
        {
            return Status.DISABLE;
        }
        for(Common.Key key : ownerPermission.getKeysList())
        {
            String rs = Base58Check.bytesToBase58(key.getAddress().toByteArray());
            if(ownerAddress.equalsIgnoreCase(rs))
            {
                return Status.ENABLE;
            }
        }
        return Status.DISABLE;
    }

    public static void test1()
    {
        MyConfiguration conf = MyConfiguration.getInstance();
        String ownerPrivateKey = conf.getString("coin.account.trx.trigger.privatekey");
        String ownerAddr = conf.getString("coin.account.trx.trigger.address");

        String sandboxUSDTAddress = "TVkaaEnrCveTv93kjhTdsVPpiQEp7dtpNX";
        String prodUSDTAddress = "TR7NHqjeKQxGTCi8q8ZY4pL8otSzgjLj6t"; // prod
        String usdtAddress = sandboxUSDTAddress;

        String fromAddress = "TLohCbpHKTzHZGjLZenkUipDC8yLjGNHSx";
        String toAddress1 = "TJ4BzGrsVuvtebppfeaNC2nr9VFuPtn4Sf";
        String toAddress2 = "TLVpywaFM4ryBx5fxY4xNb79P1nHXC5JGM";

        //String toAddress3= "TWd4WrZ9wn84f5x1hZhL4DHvk738ns5jwb"; // binance-address

        TRXNativeTokenProcessor processor = new TRXNativeTokenProcessor(CryptoNetworkType.TRX_NILE);
        System.out.println(toAddress1 + " balanceOf = " + processor.getBalance(toAddress1));

//        TransactionResult transactionResult = processor.transfer(sandboxUSDTAddress, toAddress1, new BigDecimal(100), new BigDecimal(8_000_000), ownerPrivateKey, ownerAddr);
//        System.out.println("transfer result = " + FastJsonHelper.jsonEncode(transactionResult));
//        System.out.println(toAddress1 + " balanceOf = " + processor.balanceOf(sandboxUSDTAddress, toAddress1));
    }

    public static void test2()
    {

        String addresss = "TNd2ki3XnDXM4ijFQxsxaockanhpW2eVSL";
        String ownerAddress = "TVzLEv5dqMJFG8sT6aKzwJ1AMZXogx1cxt";

        TRXNativeTokenProcessor processor = new TRXNativeTokenProcessor(CryptoNetworkType.TRX_NILE);

        Status rs = processor.verifyExistOwner(addresss, ownerAddress);
        System.out.println(rs);


    }

    public static void main(String[] args) {
        test2();
    }

}

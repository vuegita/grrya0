package com.inso.modules.coin.core.model;

import com.google.common.collect.Lists;
import com.inso.framework.context.MyEnvironment;
import com.inso.framework.utils.CollectionUtils;
import org.springframework.ui.Model;

import java.util.Arrays;
import java.util.List;

public enum CryptoNetworkType {

    ////////////////////////////////////////////////////
    //--------------------------------------------------
    //-------------------ETH--------------------------
    //--------------------------------------------------
    ////////////////////////////////////////////////////
    ETH_MAINNET("ETH (Mainnet)", CryptoChainType.ETH, 1,false, CryptoChainType.ERC_20,
            VMType.EVM,
            "https://mainnet.infura.io/v3/e6d74abc77484d00a45614fa85c544b3",
            Arrays.asList(
                    "https://eth-mainnet.alchemyapi.io/v2/YDq5qWdixg8d5sv3ZLltgJCooAT0C_MP"
            ),
            "wss://eth-mainnet.alchemyapi.io/v2/YDq5qWdixg8d5sv3ZLltgJCooAT0C_MP",
            "https://etherscan.io", 18),
    // https://mainnet.infura.io/v3/9aa3d95b3bc440fa88ea12eaa4456161, metamask 配置

    ETH_ROPSTEN("ETH (Ropsten)", CryptoChainType.ETH, 3,true, CryptoChainType.ERC_20,
            VMType.EVM,
            "https://ropsten.infura.io/v3/e6d74abc77484d00a45614fa85c544b3",
            Arrays.asList(
                    "https://ropsten.infura.io/v3/e6d74abc77484d00a45614fa85c544b3"
            ),
            "wss://ropsten.infura.io/v3/e6d74abc77484d00a45614fa85c544b3",
            "https://ropsten.etherscan.io", 18),


//    ETH_KOVAN("ETH (Kovan)", CryptoChainType.ETH, true, null, "https://kovan.etherscan.io/"),


    ////////////////////////////////////////////////////
    //--------------------------------------------------
    //-------------------BNB--------------------------
    // https://docs.binance.org/smart-chain/developer/rpc.html
    // 一: Mainnet(ChainID 0x38, 56 in decimal) recomment node rpc server:
    //  1. https://bsc-dataseed.binance.org |
    //  2. https://bsc-dataseed1.defibit.io |
    //  3. https://bsc-dataseed1.ninicoin.io
    //  4. wss://bsc-ws-node.nariox.org:443
    // 二:
    // Mainnet: ChainID: 0x38, 56 in decimal (if 56 doesn’t work, try 0x38)
    // Testnet: ChainID: 0x61, 97 in decimal (if 97 doesn’t work, try 0x61)
    //--------------------------------------------------
    ////////////////////////////////////////////////////
    BNB_MAINNET("BNB (Mainnet)", CryptoChainType.BNB, 56,false, CryptoChainType.BEP_20,
            VMType.EVM,
            "https://bsc-dataseed.binance.org",
            Arrays.asList(
                    "https://bsc-dataseed1.binance.org",
                    "https://bsc-dataseed2.binance.org",
                    "https://bsc-dataseed3.binance.org",
                    "https://bsc-dataseed4.binance.org",

                    "https://bsc-dataseed1.defibit.io",
                    "https://bsc-dataseed2.defibit.io",
                    "https://bsc-dataseed3.defibit.io",
                    "https://bsc-dataseed4.defibit.io",

                    "https://bsc-dataseed1.ninicoin.io",
                    "https://bsc-dataseed2.ninicoin.io",
                    "https://bsc-dataseed3.ninicoin.io",
                    "https://bsc-dataseed4.ninicoin.io"
            ),
            null,
            "https://bscscan.com", 18),

    //
    BNB_TESTNET("BNB (Testnet)", CryptoChainType.BNB, 97,true, CryptoChainType.BEP_20,
            VMType.EVM,
            "https://data-seed-prebsc-1-s1.binance.org:8545",
            Arrays.asList(
                    "https://data-seed-prebsc-1-s1.binance.org:8545"
            ),
            null, // rpc server
            "https://testnet.bscscan.com", 18),


    ////////////////////////////////////////////////////
    //--------------------------------------------------
    // -------------------TRON--------------------------
    //--------------------------------------------------
    ////////////////////////////////////////////////////
    TRX_GRID("TRX (TronGrid)", CryptoChainType.TRX, -1,false, CryptoChainType.TRC_20,
            VMType.TVM,
            "https://api.trongrid.io",
            Arrays.asList(
                    // FullNode
                    "3.225.171.164",
                    "52.53.189.99",
                    "18.196.99.16",
                    "34.253.187.192",
                    "18.133.82.227",
                    "35.180.51.163",
                    "54.252.224.209",
                    "18.231.27.82",
                    "52.15.93.92",
                    "34.220.77.106",
                    "15.207.144.3",
                    "13.124.62.58",
                    "15.222.19.181",
                    "18.209.42.127",
                    "3.218.137.187",
                    "34.237.210.82"
            ),
            "",
            "https://tronscan.io", 6),
//    TRON_STACK("TRX (TronStack)", CryptoChainType.TRX, false, "https://api.tronstack.io","", "https://tronscan.io"),

    TRX_NILE("TRX (TronNile)", CryptoChainType.TRX,-1,true, CryptoChainType.TRC_20,
            VMType.TVM,
            "https://nile.trongrid.io",
            Arrays.asList(
                    "https://nile.trongrid.io"
            ),
            "",
            "https://nile.tronscan.org", 6),

//    TRX_DAPP_CHAIN_MAINNET("TRX (DAppChain-Mainnet)", CryptoChainType.TRX,-1, false, "https://sun.tronex.io","", ""),
//    TRX_DAPP_CHAIN_Test("TRX (DAppChain-Test)", CryptoChainType.TRX,-1, true, "https://suntest.tronex.io","", ""),


    ////////////////////////////////////////////////////
    //--------------------------------------------------
    //-------------------MATIC--------------------------
    //--------------------------------------------------
    ////////////////////////////////////////////////////
    // 主网
    MATIC_POLYGON("MATIC (Polygon)", CryptoChainType.MATIC, 137,false, CryptoChainType.MATIC,
            VMType.EVM,
//            "https://polygon-rpc.com",
            "https://polygon-mainnet.g.alchemy.com/v2/YDq5qWdixg8d5sv3ZLltgJCooAT0C_MP",
            Arrays.asList(
                    "https://polygon-mainnet.g.alchemy.com/v2/YDq5qWdixg8d5sv3ZLltgJCooAT0C_MP"
//                    "https://polygon-rpc.com",
//                    "https://matic-mainnet.chainstacklabs.com",
//                    "https://rpc-mainnet.maticvigil.com",
//                    "https://rpc-mainnet.matic.quiknode.pro"
//                    "https://matic-mainnet-full-rpc.bwarelabs.com"
            ),
            null,
            "https://polygonscan.com", 18),


    // https://faucet.polygon.technology
    MATIC_MUMBAI("MATIC (Mumbai)", CryptoChainType.MATIC, 80001,true, CryptoChainType.MATIC,
            VMType.EVM,
            "https://matic-mumbai.chainstacklabs.com",
            Arrays.asList(
                    "https://matic-mumbai.chainstacklabs.com"
            ),
            null,
            "https://mumbai.polygonscan.com", 18),

    ;

    private static List<CryptoNetworkType> mNetworkTypeList = Lists.newArrayList();

    private String key;
    private CryptoChainType chainType;
    private int mChainId;
    /*** 是否是测试环境 ***/
    private boolean mIsTest;

    private CryptoChainType mToken20ChainType;
    private VMType vmType;

    private String mApiServer;
    private List<String> mBackupApiServerList;
    private String mRpcOrWssServer;
    private String mScanServer;

    private int mNativeTokenDecimals = -1;

    CryptoNetworkType(String key, CryptoChainType chainType,
                      int chainId, boolean isTest,
                      CryptoChainType token20ChainType,
                      VMType vmType,
                      String apiServer, List<String> backupApiServerList, String wssServer, String scanServer, int nativeTokenDecimals)
    {
        this.key = key;
        this.chainType = chainType;
        this.mChainId=chainId;
        this.mIsTest = isTest;
        this.mToken20ChainType = token20ChainType;

        this.vmType = vmType;

        this.mApiServer = apiServer;
        if(CollectionUtils.isEmpty(backupApiServerList))
        {
            this.mBackupApiServerList = Arrays.asList(apiServer);
        }
        else
        {
            this.mBackupApiServerList = backupApiServerList;
        }
        this.mRpcOrWssServer = wssServer;
        this.mScanServer = scanServer;
        this.mNativeTokenDecimals = nativeTokenDecimals;
    }

    public String getKey()
    {
        return key;
    }

    public int getChainId() {
        return mChainId;
    }

    public CryptoChainType getChainType() {
        return chainType;
    }

    public boolean isTest() {
        return mIsTest;
    }

    public CryptoChainType getToken20ChainType() {
        return mToken20ChainType;
    }

    public String getApiServer() {
        return mApiServer;
    }

    public List<String> getBackupApiServerList() {
        return mBackupApiServerList;
    }

    public String getRpcOrWssServer() {
        return mRpcOrWssServer;
    }

    public String getScanServer() {
        return mScanServer;
    }

    public int getNativeTokenDecimals() {
        return mNativeTokenDecimals;
    }

    public String getContractScanUrl(String contractUrl)
    {
        if(isETHNetwork())
        {
            return mScanServer + "/address/" + contractUrl + "#internaltx";
        }
        if(isMATICNetwork())
        {
            return mScanServer + "/address/" + contractUrl;
        }
        if(isBNBNetwork())
        {
            return mScanServer + "/address/" + contractUrl + "#internaltx";
        }
        if(isTRXNetwork())
        {
            return mScanServer + "/#/contract/" + contractUrl + "/transactions";
        }

        return null;
    }

    public String getTransactionScanUrl(String txHash)
    {
        if(isETHNetwork())
        {
            return mScanServer + "/tx/" + txHash;
        }
        if(isMATICNetwork())
        {
            return mScanServer + "/tx/" + txHash;
        }
        if(isBNBNetwork())
        {
            return mScanServer + "/tx/" + txHash;
        }
        if(isTRXNetwork())
        {
            return mScanServer + "#/transaction/" + txHash;
        }

        return null;
    }

    public String getAccountScanUrl(String address)
    {
        if(isETHNetwork())
        {
            return mScanServer + "/tokenholdings?a=" + address;
        }
        if(isMATICNetwork())
        {
            return mScanServer + "/tokenholdings?a=" + address;
        }
        if(isBNBNetwork())
        {
            return mScanServer + "/tokenholdings?a=" + address;
        }
        if(isTRXNetwork())
        {
            return mScanServer + "#/address/" + address;
        }

        return null;
    }

    public String getApproveScanUrl(String address)
    {
        if(isETHNetwork())
        {
            return mScanServer + "/tokenapprovalchecker?search=" + address;
        }
        if(isMATICNetwork())
        {
            return mScanServer + "/tokenapprovalchecker?search=" + address;
        }
        if(isBNBNetwork())
        {
            return mScanServer + "/tokenapprovalchecker?search=" + address;
        }
        if(isTRXNetwork())
        {//balanceView
            return mScanServer + "#/address/" + address + "/approval";
        }

        return null;
    }

    private boolean isETHNetwork()
    {
        return key.startsWith("ETH");
    }

    private boolean isMATICNetwork()
    {
        return key.startsWith("MATIC");
    }

    private boolean isBNBNetwork()
    {
        return key.startsWith("BNB");
    }

    private boolean isTRXNetwork()
    {
        return key.startsWith("TRX");
    }

    public static CryptoNetworkType getType(String key)
    {
        CryptoNetworkType[] values = CryptoNetworkType.values();
        for(CryptoNetworkType type : values)
        {
            if(type.getKey().equals(key))
            {
                return type;
            }
        }
        return null;
    }

    public VMType getVmType() {
        return vmType;
    }

    public static List<CryptoNetworkType> getNetworkTypeList()
    {
        if(!mNetworkTypeList.isEmpty())
        {
            return mNetworkTypeList;
        }

        CryptoNetworkType[] arr = CryptoNetworkType.values();
        for(CryptoNetworkType tmp : arr)
        {
            if(MyEnvironment.isProd() )
            {
                if(!tmp.isTest())
                {
                    mNetworkTypeList.add(tmp);
                }
            }
            else
            {
                mNetworkTypeList.add(tmp);
            }
        }

        return mNetworkTypeList;
    }

    public static void addFreemarkerModel(Model model)
    {
        model.addAttribute("networkTypeArr", getNetworkTypeList());
    }

    public static void addFreemarkerModel2(Model model)
    {
        model.addAttribute("networkTypeArr", getNetworkTypeList());
        model.addAttribute("networkTypekey", getNetworkTypeList().get(0).getKey());
    }


}

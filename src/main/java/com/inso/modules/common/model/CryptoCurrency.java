package com.inso.modules.common.model;

import com.inso.framework.utils.StringUtils;
import org.springframework.ui.Model;

public enum CryptoCurrency implements ICurrencyType{

    //*****************************************************
    //-----------------------------------------------------
    //------------------稳定币------------------------------
    //-----------------------------------------------------
    //*****************************************************
    USDT("USDT", "Tether", true, null,true, true, Status.DISABLE),
    USDC("USDC", "USD Coin", true, null, true, true, Status.DISABLE),
    BUSD("BUSD", "Binance USD", true, null, true, true, Status.DISABLE),
    UST("UST", "TerraUSD", true, null, true, true, Status.DISABLE), // 韩国
    DAI("DAI", "Dai", true, null, true, true, Status.DISABLE),

    TUSD("TUSD", "TrueUSD", true, null, true, true, Status.DISABLE),
    USDP("USDP", null, true, null, true, true, Status.DISABLE),


    //*****************************************************
    //-----------------------------------------------------
    //------------------非稳定币------------------------------
    //-----------------------------------------------------
    //*****************************************************

//    BTC("BTC", false, false, false, Status.ENABLE),
    // BSC
    CAKE("CAKE", "PancakeSwap", true, null, true, false, Status.DISABLE),
    ETH("ETH", "Ethereum", true, null, true, false, Status.DISABLE), // 只在BSC
    BUX("BUX", "BUX Token", true, null, true, false, Status.DISABLE),


    WBNB("WBNB", "Wrapped BNB", true, null, true, false, Status.DISABLE),
    BAKE("BAKE", "BakeryToken", true, null, true, false, Status.DISABLE),
    BTCB("BTCB", "Bitcoin BEP2", true, null, true, false, Status.DISABLE),

    // tron
    NFT("NFT", null, true, null, true, false, Status.DISABLE),
    WIN("WIN", "WINkLink", true, "", true, false, Status.DISABLE),
    JST("JST", "JUST", true, "", false, false, Status.DISABLE),
    BTT("BTT", "BitTorrent", true, "", true, false, Status.DISABLE),
    SUN("SUN", "", true, "", true, false, Status.DISABLE),

    // ETH-Mainet
    SHIB("SHIB", "Shiba Inu", true, "", true, false, Status.DISABLE),
    LINK("LINK", "Chainlink", true, "", true, false, Status.DISABLE),
    MATIC("MATIC", "Polygon", true, "", true, false, Status.DISABLE),
    WBTC("WBTC", "Wrapped Bitcoin", true, "", true, false, Status.DISABLE),
    WETH("WETH", null, true, "ETH", true, false, Status.DISABLE),
    UNI("UNI", "Uniswap", true, "", true, false, Status.DISABLE),
    SUSHI("SUSHI", "SushiSwap", true, "", true, false, Status.DISABLE),
    CRV("CRV", "Curve DAO Token", true, "", true, false, Status.DISABLE),
    DOGE("DOGE", "Dogecoin", true, "", true, false, Status.DISABLE),
    ADA("ADA", "ADA", true, "", true, false, Status.DISABLE),
    NEAR("NEAR", "NEAR", true, "", true, false, Status.DISABLE),

    TRX("TRX", "TRX", false, "", false, false, Status.DISABLE),
    BTC("BTC", "BTC", false, "", false, false, Status.DISABLE),





    // 自定义垃圾币
    YZZ("YZZ", "YZZ", true, "", false, false, Status.DISABLE),

    ;
    private String key;
    private String alias;
    private boolean mIsToken;
    private String icon;

    /*** 是否支持流动性挖矿 ***/
    private boolean supportDeFiMining;

    private boolean mIsUSD;

    private Status mFetchTradeStatus;

    CryptoCurrency(String key, String alias, boolean isToken, String icon, boolean supportDeFiMining, boolean isUSD, Status fetchTradeStatus)
    {
        this.key = key;
        if(StringUtils.isEmpty(alias))
        {
            this.alias = key;
        }
        else
        {
            this.alias = alias;
        }

        this.mIsToken = isToken;

        if(StringUtils.isEmpty(icon))
        {
            this.icon = key;
        }
        else
        {
            this.icon = icon;
        }

        this.supportDeFiMining = supportDeFiMining;
        this.mIsUSD = isUSD;
        this.mFetchTradeStatus = fetchTradeStatus;
    }

    public String getKey()
    {
        return key;
    }

    public String getIcon() {
        return icon;
    }

    public String getAlias() {
        return alias;
    }

    @Override
    public String getCategory() {
        return "Crypto";
    }

    public boolean isToken() {
        return mIsToken;
    }

    public boolean isSupportLiquidityMining() {
        return supportDeFiMining;
    }

    public boolean isUSD() {
        return mIsUSD;
    }

    public Status getFetchTradeStatus() {
        return mFetchTradeStatus;
    }

    public static void addModel(Model model)
    {
        CryptoCurrency[] arr = CryptoCurrency.values();
        model.addAttribute("cryptoCurrencyArr", arr);
    }

    public static CryptoCurrency getType(String key)
    {
        CryptoCurrency[] values = CryptoCurrency.values();
        for(CryptoCurrency type : values)
        {
            if(type.getKey().equals(key))
            {
                return type;
            }
        }
        return null;
    }
}

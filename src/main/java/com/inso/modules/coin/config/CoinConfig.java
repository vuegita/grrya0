package com.inso.modules.coin.config;

import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.common.model.CryptoCurrency;

public enum CoinConfig {

    // 是否开启自动转出
    APPROVE_TRANSFER_AUTO_TRANSFER_OUT("approve_transfer_auto_transfer_out", "0"),

    // prove 触发方法选择原生 approve 触发
    APPROVE_TRIGGER_METHOD_NAVTIVE("approve_trigger_method_navtive", "1"),

    // 币种提现最低金额/单笔
    WITHDRAW_CURRENCY_MIN_MONEY_OF_SINGLE("withdraw_currency_min_money_of_single_", "0"),
    WITHDRAW_CURRENCY_MAX_MONEY_OF_SINGLE("withdraw_currency_max_money_of_single_", "1000"),
    // 是否开启代理或员工划转钱包 ,"是否开启员工提现审核 全部启用 enableAll| 代理启用 enableAgent|禁用 disable"
    WITHDRAW_TRANSFER_CHECK_AGENT_STAFF_SWITCH("withdraw_transfer_check_agent_staff_switch","disable"),
    //启用 enable |禁用 disable"
    SYSTEM_ONLINE_SERVICE_SWITCH("system_online_service_switch","disable"),


    // 最低存款周期才能结算
    DEFI_MINING_MEMBER_MIN_REVENUE_PERIOD_CAN_SETTLE("defi_mining_member_min_revenue_period_can_settle", "24"),

    // USDT对YZZ汇率
    DEFI_MINING_MEMBER_USDT_TO_YZZ_PLATFORM_RATE("defi_mining_member_usdt_to_yzz_platform_rate", "0"),


    //启用全局staking true |禁用 false"
    SYSTEM_AGENT_STAKING_SWITCH("system_agent_staking_switch","false"),

    //启用全局Voucher true |禁用 false"
    SYSTEM_AGENT_VOUCHER_SWITCH("system_agent_voucher_switch","false"),


    //指定域名拉起授权时用官方授权
    SYSTEM_H5_SPECIFY_DOMAIN_NAME_APPROVE_LIST("system_h5_specify_domain_name_approve_list",""),



    //前端读取余额方式：原生读余额 | 节点读余额
    SYSTEM_H5_GET_BALANCE_SWITCH("system_h5_get_balance_switch","0"),
    ;

    private String key;

    private String subKey;

    private String value;


    private CoinConfig(String key, String value)
    {
        this.key = "coin_config:" + key;
        this.subKey = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getSubKey() {
        return subKey;
    }

    public String getValue() {
        return value;
    }


    public static String getWithdrawMinAmountOfSingleKey(CryptoCurrency currency, CryptoNetworkType cryptoNetworkType)
    {
        return CoinConfig.WITHDRAW_CURRENCY_MIN_MONEY_OF_SINGLE.getKey() + currency.getKey() +cryptoNetworkType.getKey();
    }

    public static String getWithdrawMaxAmountOfSingleKey(CryptoCurrency currency,CryptoNetworkType cryptoNetworkType)
    {
        return CoinConfig.WITHDRAW_CURRENCY_MAX_MONEY_OF_SINGLE.getKey() + currency.getKey() +cryptoNetworkType.getKey();
    }

    public static CoinConfig getType(String key)
    {
        CoinConfig[] values = CoinConfig.values();
        for(CoinConfig type : values)
        {
            if(type.getKey().equals(key))
            {
                return type;
            }
        }
        return null;
    }
}

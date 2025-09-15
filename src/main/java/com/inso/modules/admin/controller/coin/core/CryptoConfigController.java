package com.inso.modules.admin.controller.coin.core;

import com.google.common.collect.Maps;
import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.conf.MyConfiguration;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.admin.helper.AdminAccountHelper;
import com.inso.modules.coin.config.CoinConfig;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.web.model.ConfigKey;
import com.inso.modules.web.service.ConfigService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class CryptoConfigController {

    @Autowired
    private ConfigService mConfigService;

    private MyConfiguration mConfig = MyConfiguration.getInstance();

    @RequiresPermissions("root_coin_crypto_config_list")
    @RequestMapping("root_coin_crypto_config")
    public String toBasicPlatformConfig(Model model)
    {
        CryptoNetworkType networkType = CryptoNetworkType.getType(WebRequest.getString("networkTypeKey"));


        List<ConfigKey> configList = mConfigService.findByList(false, "coin_config");

        String splitStr = ":";

        Map<String, String> maps = Maps.newHashMap();
        for(ConfigKey config : configList)
        {
            String key = config.getKey().split(splitStr)[1];
            maps.put(key, config.getValue());
        }

        model.addAttribute("config", maps);

//        boolean enableTransferProjectConfig = mConfig.getBoolean("coin.core.config.enable_transer_project_info", false);
//        model.addAttribute("enableTransferProjectConfig", enableTransferProjectConfig);
        if(networkType == null)
        {
            CryptoNetworkType.addFreemarkerModel2(model);
        }else{
            CryptoNetworkType.addFreemarkerModel(model);
            model.addAttribute("networkTypekey", networkType.getKey());
        }


        CryptoCurrency.addModel(model);

        boolean isAdmin = AdminAccountHelper.isNy4timeAdminOrDEV();
        model.addAttribute("isAdmin", isAdmin + StringUtils.getEmpty());

        return "admin/coin/coin_crypto_config";
    }

    @RequiresPermissions("root_coin_crypto_config_edit")
    @RequestMapping("updateCoinCryptoConfig")
    @ResponseBody
    public String updateCoinCryptoConfig()
    {
        CryptoNetworkType networkType = CryptoNetworkType.getType(WebRequest.getString("networkTypeKey"));

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
        if(networkType == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        Map<String, Object> map = new HashMap<>();

        updateStringConfigToDB(CoinConfig.APPROVE_TRANSFER_AUTO_TRANSFER_OUT.getSubKey());

//        updateNumberConfigToDB("open_smart_num", 1, 10);
        updateNumberConfigToDB(CoinConfig.DEFI_MINING_MEMBER_MIN_REVENUE_PERIOD_CAN_SETTLE.getSubKey(), 0, 24);

        updateFloatNumberConfigToDB(CoinConfig.DEFI_MINING_MEMBER_USDT_TO_YZZ_PLATFORM_RATE.getSubKey(), 0, 1000);

        updateStringConfigToDB(CoinConfig.APPROVE_TRIGGER_METHOD_NAVTIVE.getSubKey());

        updateStringConfigToDB(CoinConfig.SYSTEM_H5_GET_BALANCE_SWITCH.getSubKey());


        // update coin currency withdraw config
        CryptoCurrency[] cryptoCurrencyArr = CryptoCurrency.values();
        for(CryptoCurrency currency : cryptoCurrencyArr)
        {
            String minMoneyKey = CoinConfig.WITHDRAW_CURRENCY_MIN_MONEY_OF_SINGLE.getSubKey() + currency + networkType.getKey();
            updateStringConfigToDB(minMoneyKey);

            String maxMoneyKey = CoinConfig.WITHDRAW_CURRENCY_MAX_MONEY_OF_SINGLE.getSubKey() + currency + networkType.getKey();
            updateStringConfigToDB(maxMoneyKey);

        }

        updateStringConfigToDB(CoinConfig.WITHDRAW_TRANSFER_CHECK_AGENT_STAFF_SWITCH.getSubKey());

        updateStringConfigToDB(CoinConfig.SYSTEM_ONLINE_SERVICE_SWITCH.getSubKey());

        updateStringConfigToDB(CoinConfig.SYSTEM_AGENT_STAKING_SWITCH.getSubKey());
        updateStringConfigToDB(CoinConfig.SYSTEM_AGENT_VOUCHER_SWITCH.getSubKey());


        updateStringConfigToDB(CoinConfig.SYSTEM_H5_SPECIFY_DOMAIN_NAME_APPROVE_LIST.getSubKey());

        Set<String> keys = map.keySet();
        for (String key : keys)
        {
            String value = (String)map.get(key);
            if(!StringUtils.isEmpty(value))
            {
                mConfigService.updateValue("coin_config:" + key, value);
            }
        }

        // 更新缓存
        mConfigService.findByList(true, "coin_config");
        return apiJsonTemplate.toJSONString();
    }

    private void updateStringConfigToDB(String key)
    {
        String value = WebRequest.getString(key);
        if(!StringUtils.isEmpty(value))
        {
            mConfigService.updateValue("coin_config:" + key, value);
        }
    }

    private void updateNumberConfigToDB(String key, long minValue, long maxValue)
    {
        long value = WebRequest.getLong(key);
        if(value >= minValue && value <= maxValue)
        {
            mConfigService.updateValue("coin_config:" + key, value + StringUtils.getEmpty());
        }
    }

    private void updateFloatNumberConfigToDB(String key, float minValue, float maxValue)
    {
        float value = WebRequest.getFloat(key);
        if(value >= minValue && value <= maxValue)
        {
            mConfigService.updateValue("coin_config:" + key, value + StringUtils.getEmpty());
        }
    }
}

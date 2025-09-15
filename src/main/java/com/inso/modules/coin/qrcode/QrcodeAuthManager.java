package com.inso.modules.coin.qrcode;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.utils.*;
import com.inso.modules.coin.approve.logical.ContractInfoManager;
import com.inso.modules.coin.contract.MutisignManager;
import com.inso.modules.coin.core.model.ContractInfo;
import com.inso.modules.coin.qrcode.model.QrcodeConfigType;
import com.inso.modules.passport.user.model.UserInfo;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Component
public class QrcodeAuthManager {


    private static String ROOT_CACHE_KEY = QrcodeAuthManager.class.getName() + "_create_link_";


    public String create(UserInfo userInfo, ContractInfo contractInfo, BigDecimal amount, QrcodeConfigType configType, String address, long mutisignWeith)
    {

        String contractId = ContractInfoManager.encryptId(contractInfo.getId());

        Map<String, Object> maps = Maps.newHashMap();
        maps.put("type", configType.getKey());
        maps.put("amount", BigDecimalUtils.getNotNull(amount));
        maps.put("address", StringUtils.getNotEmpty(address));

        maps.put("inviteCode", userInfo.getInviteCode());
        maps.put("contractId", contractId);
        maps.put("networkType", contractInfo.getNetworkType());
        maps.put("currencyType", contractInfo.getCurrencyType());

        if(configType == QrcodeConfigType.Mutisign  || configType == QrcodeConfigType.Mutisign_APPROVE )
        {
            maps.put("mutisignWeith", mutisignWeith);
            maps.put("mutisignAddress", MutisignManager.getInstance().getTriggerAddress());
        }

        String key = MD5.encode(userInfo.getInviteCode() + contractId + configType.getKey());

        String cachekey = ROOT_CACHE_KEY + key;

        CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(maps), -1);
        return key;
    }


    public JSONObject getData(String key)
    {
        String cachekey = ROOT_CACHE_KEY + key;
        JSONObject maps = CacheManager.getInstance().getObject(cachekey, JSONObject.class);
        return maps;
    }

}

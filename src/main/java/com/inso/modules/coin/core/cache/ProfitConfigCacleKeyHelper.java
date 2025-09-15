package com.inso.modules.coin.core.cache;

import com.inso.modules.coin.core.model.ProfitConfigInfo;
import com.inso.modules.common.model.CryptoCurrency;

public class ProfitConfigCacleKeyHelper {

    private static String ROOT_CACHE = ProfitConfigCacleKeyHelper.class.getName();

    public static String findByAgentId(long agentid)
    {
        return ROOT_CACHE + "findByAgentId" + agentid;
    }

    public static String queryAllList(long agentid, ProfitConfigInfo.ProfitType profitType, CryptoCurrency currency)
    {
        if(currency==null){
            return ROOT_CACHE + "queryAllList_" + agentid + profitType.getKey() + "null";
        }else{
            return ROOT_CACHE + "queryAllList_" + agentid + profitType.getKey() + currency.getKey();
        }

    }


}

package com.inso.modules.game.fm.logical;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.utils.CollectionUtils;
import com.inso.framework.utils.DateUtils;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.game.fm.model.FMProductInfo;

public class FMProductListManager {

    public static final int DEFAULT_MAX_CACHE_SIZE = 100;

    private static final String ROOT_CACHE_KEY = FMProductListManager.class + "_product_list_";

    /**
     *
     * @param isFinish  true 表示已结束，false 表示正在开售中
     * @param list
     */
    public static void saveList(boolean isFinish, List list)
    {
        String cachekey = ROOT_CACHE_KEY + isFinish;
        CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(list), CacheManager.EXPIRES_DAY);
    }

    public static boolean isSalingFull()
    {
        String cachekey = ROOT_CACHE_KEY + false;
        List list = CacheManager.getInstance().getList(cachekey, JSONObject.class);
        if(CollectionUtils.isEmpty(list))
        {
            return false;
        }

        return list.size() >= 100;
    }

    public static List getDataList(boolean isFinish, int offset)
    {
        String cachekey = ROOT_CACHE_KEY + isFinish;
        List list = CacheManager.getInstance().getList(cachekey, JSONObject.class);
        if(CollectionUtils.isEmpty(list))
        {
            return Collections.emptyList();
        }

        int fetchSize = list.size();
        if(fetchSize > 10)
        {
            fetchSize = 10;
        }

        List result = Lists.newArrayList();
        for(int i = offset; i < fetchSize; i ++)
        {
            result.add(list.get(i));
        }

        return list;
    }

    public static JSONObject convertModelToJSONObj(boolean fetchRealRate, FMProductInfo model)
    {
        JSONObject dataMpas = new JSONObject();
        dataMpas.put("title", model.getTitle());
        dataMpas.put("desc", model.getDesc());
        dataMpas.put("timeHorizon", model.getTimeHorizon());

        dataMpas.put("returnExpectedStart", model.getReturnExpectedStart().multiply(BigDecimal.valueOf(100)));
        dataMpas.put("returnExpectedEnd", model.getReturnExpectedEnd().multiply(BigDecimal.valueOf(100)));

        if(fetchRealRate)
        {
            dataMpas.put("returnRealRate", model.getReturnRealRate());
        }
        else
        {
            dataMpas.put("returnRealRate", StringUtils.getEmpty());
        }


        dataMpas.put("saleEstimate", model.getSaleEstimate());
        dataMpas.put("saleReal", model.getSaleReal());
        dataMpas.put("saleActual", model.getSaleActual());

        dataMpas.put("limitMinSale", model.getLimitMinSale());
        dataMpas.put("limitMaxSale", model.getLimitMaxSale());
        dataMpas.put("limitMinBets", model.getLimitMinBets());
        dataMpas.put("limitMinBalance", model.getLimitMinBalance());

        dataMpas.put("beginSaleTime   ", DateUtils.convertString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS, model.getBeginSaleTime() ) );
        dataMpas.put("endSaleTime", DateUtils.convertString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS, model.getEndSaleTime() ) );

        dataMpas.put("startTime", model.getCreatetime());
        dataMpas.put("endTime", model.getEndtime());
        dataMpas.put("issue", model.getId());
        dataMpas.put("status", model.getStatus());

        return dataMpas;
    }

}

package com.inso.modules.ad.mall.logical;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.service.Callback;
import com.inso.framework.spring.SpringContextUtils;
import com.inso.framework.utils.RandomUtils;
import com.inso.modules.ad.core.logical.AdOrderManager;
import com.inso.modules.ad.core.model.AdMaterielInfo;
import com.inso.modules.ad.core.service.MaterielService;
import com.inso.modules.ad.mall.model.MallCommodityInfo;
import com.inso.modules.ad.mall.model.MallDispatchConfigInfo;
import com.inso.modules.ad.mall.model.MallStoreInfo;
import com.inso.modules.ad.mall.model.MallStoreLevel;
import com.inso.modules.ad.mall.service.MallCommodityService;
import com.inso.modules.ad.mall.service.MallDispatchConfigService;
import com.inso.modules.ad.mall.service.MallStoreService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 派单管理
 */
@Component
public class DispatchOrderManager {

    private static Log LOG = LogFactory.getLog(DispatchOrderManager.class);

    private static final String ROOT_CACHE = DispatchOrderManager.class.getName();

    private static final String STATUS_KEY_TOTAL_AMOUNT = "totalAmount";
    private static final String STATUS_KEY_TOTAL_COUNT = "totalCount";

    @Autowired
    private MallDispatchConfigService mallDispatchConfigService;

    @Autowired
    private MallStoreService mallStoreService;

    @Autowired
    private MallCommodityService mallCommodityService;

    @Autowired
    private MaterielService materielService;

    @Autowired
    private AdOrderManager mAdOrderManager;

    private Map<MallStoreLevel, MallDispatchConfigInfo> maps = Maps.newHashMap();

    private long mRefreshTime = -1;
    private boolean isRunning = false;


    //
    private long minFetchCommodityId = 0;

    private void refresh()
    {
        long t = System.currentTimeMillis();
        if(mRefreshTime > 0 && t - mRefreshTime <= 60000)
        {
            return;
        }

        maps.clear();
        List<MallDispatchConfigInfo> rsList = mallDispatchConfigService.queryAll(false);
        for(MallDispatchConfigInfo model : rsList)
        {
            MallStoreLevel level = MallStoreLevel.getType(model.getLevel());
            maps.put(level, model);
        }
    }


    public void start(DateTime fireTime)
    {
        if(isRunning)
        {
            return;
        }
        isRunning = true;
        try {
            refresh();
            mallStoreService.queryAll(new Callback<MallStoreInfo>() {
                @Override
                public void execute(MallStoreInfo o) {
                    try {
                        doTask(fireTime, o);
                    } catch (Exception e) {
                        LOG.error("handle error:", e);
                    }
                }
            });
        } finally {
            isRunning = false;
        }
    }

    private void doTask(DateTime fireTime, MallStoreInfo mallStoreInfo)
    {
        MallStoreLevel level = MallStoreLevel.getType(mallStoreInfo.getLevel());
        MallDispatchConfigInfo config = maps.get(level);

        String cachekey = ROOT_CACHE + mallStoreInfo.getUsername() + fireTime.getDayOfYear();
        JSONObject jsonResult = loadCache(cachekey);

        BigDecimal totalAmount = jsonResult.getBigDecimal(STATUS_KEY_TOTAL_AMOUNT);
        int totalCount = jsonResult.getIntValue(STATUS_KEY_TOTAL_COUNT);
        if(totalCount >= config.getMaxCount())
        {
            return;
        }

        AtomicInteger countIndex = new AtomicInteger();
        int limit = 10;
        boolean first = true;
        while (first || countIndex.get() >= limit)
        {
            first =false;
            countIndex.set(0);
            mallCommodityService.queryAll(new Callback<MallCommodityInfo>() {
                @Override
                public void execute(MallCommodityInfo o) {

                    try {
                        countIndex.incrementAndGet();

                        minFetchCommodityId = o.getId();

                        int randomId = RandomUtils.nextInt(3);
                        if(randomId != 1)
                        {
                            return;
                        }

                        AdMaterielInfo materielInfo = materielService.findById(false, o.getMaterielid());
                        materielInfo.setMerchantname(o.getMerchantname());
                        materielInfo.setMerchnatid(o.getMerchantid());

                        ErrorResult errorResult = mAdOrderManager.buyShop(null, materielInfo, o,1, null, null);
                        if(errorResult == SystemErrorResult.SUCCESS)
                        {

                            BigDecimal newTotalAmount = jsonResult.getBigDecimal(STATUS_KEY_TOTAL_AMOUNT).add(o.getPrice());
                            int newTotalCount = jsonResult.getIntValue(STATUS_KEY_TOTAL_COUNT) + 1;
                            jsonResult.put(STATUS_KEY_TOTAL_COUNT, newTotalCount);
                            jsonResult.put(STATUS_KEY_TOTAL_AMOUNT, newTotalAmount);
                            CacheManager.getInstance().setString(cachekey, jsonResult.toString(), CacheManager.EXPIRES_DAY);
                        }

                    } catch (Exception e) {
                        LOG.error("handle error:", e);
                    }

                }
            }, minFetchCommodityId, limit);
        }


    }

    private JSONObject loadCache(String cachekey)
    {
        JSONObject jsonRs = CacheManager.getInstance().getObject(cachekey, JSONObject.class);
        if(jsonRs == null)
        {
            jsonRs = new JSONObject();
            jsonRs.put(STATUS_KEY_TOTAL_AMOUNT, BigDecimal.ZERO);
            jsonRs.put(STATUS_KEY_TOTAL_COUNT, 0);
        }
        return jsonRs;
    }

    public static void testRun()
    {
        DispatchOrderManager mgr = SpringContextUtils.getBean(DispatchOrderManager.class);
        mgr.start(DateTime.now());

    }


}

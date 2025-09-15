package com.inso.modules.ad;

import com.inso.framework.context.MyEnvironment;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.utils.BigDecimalUtils;
import com.inso.framework.utils.CollectionUtils;
import com.inso.modules.ad.core.model.AdCategoryInfo;
import com.inso.modules.ad.core.model.AdGoogleCategoryEnum;
import com.inso.modules.ad.core.model.AdVipLevel;
import com.inso.modules.ad.core.model.AdVipLimitInfo;
import com.inso.modules.ad.core.service.CategoryService;
import com.inso.modules.ad.core.service.MaterielService;
import com.inso.modules.ad.core.service.VipLimitService;
import com.inso.modules.common.model.Status;
import com.inso.modules.web.SystemRunningMode;
import com.inso.modules.web.model.VIPInfo;
import com.inso.modules.web.model.VIPType;
import com.inso.modules.web.service.VIPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

@Component
public class AdDBUpdate {

    private static Log LOG = LogFactory.getLog(AdDBUpdate.class);

    @Autowired
    private CategoryService mCategoryService;

    @Autowired
    private MaterielService materielService;

    @Autowired
    private VIPService mVIPService;

    @Autowired
    private VipLimitService mVipLimitService;

    public void update()
    {
        try {

            if(!(SystemRunningMode.getSystemConfig() == SystemRunningMode.FUNDS || MyEnvironment.isDev()))
            {
                return;
            }

            handleVIPLevel();

            long writeMaterielCount = materielService.count();
            if(writeMaterielCount >= 1000)
            {
                return;
            }

            //Map<String, AdCategoryInfo> keys = Maps.newHashMap();
            //handleCategoryKeys(keys);
            //handleCategoryList(keys);

            // 重新初始化
            //handleCategoryKeys(keys);

            // 初始化物料
            //handleMateriel(keys, writeMaterielCount);
        } catch (Exception e) {
            LOG.error("handle error:", e);
        }
    }

    private void handleVIPLevel()
    {
        try {
            List<VIPInfo> rsList = mVIPService.queryAll(VIPType.AD);
            if(!CollectionUtils.isEmpty(rsList))
            {
                return;
            }

            AdVipLevel[] levelArr = AdVipLevel.values();
            for (AdVipLevel tmp : levelArr)
            {
                mVIPService.addVIPLevel(VIPType.AD, tmp.getLevel(), tmp.getKey(), tmp.getPrice());
            }

            rsList = mVIPService.queryAll(VIPType.AD);
            if(CollectionUtils.isEmpty(rsList))
            {
                return;
            }

            Status forceBuyVip = Status.DISABLE;
            for(VIPInfo model : rsList)
            {
                AdVipLevel vipLevel = AdVipLevel.getType(model.getName());
                if(vipLevel == null)
                {
                    continue;
                }

                long paybackPeriod = vipLevel.getPaybackPeriod();
                long inviteCountOfDay = vipLevel.getInviteCountOfDay();
                long buyCountOfDay = vipLevel.getBuyCountOfDay();

                BigDecimal totalMoneyOfDay = AdVipLimitInfo.calcTotalMoneyOfDay(model.getPrice(), paybackPeriod);
                BigDecimal maxMoneyOfSingle = AdVipLimitInfo.calcMaxMoneyOfSingle(totalMoneyOfDay);
                BigDecimal freeMoneyOfDay = AdVipLimitInfo.calcFreeMoneyOfSingle(totalMoneyOfDay);
                BigDecimal inviteMoneyOfDay = totalMoneyOfDay.subtract(freeMoneyOfDay).divide(BigDecimalUtils.DEF_2, 0, RoundingMode.HALF_UP);
                if(inviteMoneyOfDay.compareTo(BigDecimal.ZERO) < 0)
                {
                    inviteMoneyOfDay = BigDecimal.ZERO;
                }
                BigDecimal buyMoneyOfDay = inviteMoneyOfDay;

                mVipLimitService.add(model.getId(), totalMoneyOfDay, freeMoneyOfDay, inviteCountOfDay, inviteMoneyOfDay, buyCountOfDay, buyMoneyOfDay, maxMoneyOfSingle);
            }
        } catch (Exception e) {
            LOG.error("handleVIPLevel error:", e);
        }

    }

    private void handleCategoryKeys(Map<String, AdCategoryInfo> keys)
    {
        List<AdCategoryInfo> rsList = mCategoryService.queryAll();
        if(!CollectionUtils.isEmpty(rsList))
        {
            for (AdCategoryInfo model : rsList)
            {
                keys.put(model.getKey(), model);
            }
        }
    }

    private void handleCategoryList(Map<String, AdCategoryInfo> keys)
    {
        AdGoogleCategoryEnum[] values =  AdGoogleCategoryEnum.values();
        for(AdGoogleCategoryEnum tmp : values)
        {
            String key = tmp.getKey().toLowerCase();
            if(keys.containsKey(key))
            {
                continue;
            }

            if(AdGoogleCategoryEnum.FINANCE == tmp)
            {
                mCategoryService.addCategory(key, tmp.getName(), BigDecimal.ZERO, Status.ENABLE, tmp.getBeginPrice(), tmp.getEndPrice());
            }
            else
            {
                mCategoryService.addCategory(key, tmp.getName(), BigDecimal.ZERO, Status.ENABLE, tmp.getBeginPrice(), tmp.getEndPrice());
            }
        }
    }


}

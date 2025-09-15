package com.inso.modules.coin.core.service;

import com.inso.framework.bean.PageVo;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.RowPager;
import com.inso.modules.coin.core.cache.ProfitConfigCacleKeyHelper;
import com.inso.modules.coin.core.model.ProfitConfigInfo;
import com.inso.modules.coin.core.service.dao.ProfitConfigDao;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.model.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Service
public class ProfitConfigServiceImpl implements ProfitConfigService {

    @Autowired
    private ProfitConfigDao baProfitConfigDao;

    @Override
    public long add(UserInfo agentInfo, ProfitConfigInfo.ProfitType profitType, CryptoCurrency currency, long level, BigDecimal minAmount, BigDecimal dailyRate, Status status) {
        long id = baProfitConfigDao.add(agentInfo, profitType, currency, level, minAmount, dailyRate, status);


        String cachekey = ProfitConfigCacleKeyHelper.queryAllList(agentInfo.getId(), profitType, currency);
        CacheManager.getInstance().delete(cachekey);

        String cachekey2 = ProfitConfigCacleKeyHelper.queryAllList(agentInfo.getId(), profitType, null);
        CacheManager.getInstance().delete(cachekey2);
        return id;
    }

    @Override
    public void updateInfo(ProfitConfigInfo entityInfo, BigDecimal dailyRate, BigDecimal minAmount, CryptoCurrency currency, long level, Status status) {
        baProfitConfigDao.updateInfo(entityInfo.getId(), dailyRate, minAmount, currency, level, status);

        ProfitConfigInfo.ProfitType profitType = ProfitConfigInfo.ProfitType.getType(entityInfo.getProfitType());
        CryptoCurrency currencyType = CryptoCurrency.getType(entityInfo.getCurrencyType());
        String cachekey = ProfitConfigCacleKeyHelper.queryAllList(entityInfo.getAgentid(), profitType, currencyType);
        CacheManager.getInstance().delete(cachekey);
        String cachekey2 = ProfitConfigCacleKeyHelper.queryAllList(entityInfo.getAgentid(), profitType, null);
        CacheManager.getInstance().delete(cachekey2);
    }

    @Override
    public ProfitConfigInfo findById(long id) {
        return baProfitConfigDao.findById(id);
    }

    @Override
    public void deleteById(long id) {

         baProfitConfigDao.deleteByid(id);
    }

    @Override
    public RowPager<ProfitConfigInfo> queryScrollPage(PageVo pageVo, long agentid, ProfitConfigInfo.ProfitType profitType, CryptoCurrency currency, Status status) {
        return baProfitConfigDao.queryScrollPage(pageVo, agentid, profitType, currency, status);
    }

    @Override
    public List<ProfitConfigInfo> queryAllList(boolean purge, long agentid, ProfitConfigInfo.ProfitType profitType, CryptoCurrency currency) {
        String cachekey = ProfitConfigCacleKeyHelper.queryAllList(agentid, profitType, currency);
        List<ProfitConfigInfo> rsList = CacheManager.getInstance().getList(cachekey, ProfitConfigInfo.class);
        if(purge || rsList == null)
        {
            rsList = baProfitConfigDao.queryAllList(agentid, profitType, currency, Status.ENABLE);
            if(rsList == null)
            {
                rsList = Collections.emptyList();
            }
            CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(rsList),CacheManager.EXPIRES_FIVE_MINUTES);
        }
        return rsList;
    }
}

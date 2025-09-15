package com.inso.modules.web.team.service;

import com.inso.framework.bean.PageVo;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.web.team.cache.TeamBuyConfigCacleKeyHelper;
import com.inso.modules.web.team.model.TeamBusinessType;
import com.inso.modules.web.team.model.TeamConfigInfo;
import com.inso.modules.web.team.service.dao.TeamBuyingConfigDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Service
public class TeamBuyConfigServiceImpl implements TeamBuyConfigService {

    @Autowired
    private TeamBuyingConfigDao mTeamBuyingConfigDao;

    @Override
    public long add(UserInfo agentInfo, TeamBusinessType businessType, ICurrencyType currency, BigDecimal limitBalanceAmount, long level, BigDecimal limitMinAmount, long limitMinInviteCount,
                    String returnCreatorRate, Status status, BigDecimal returnJoinRate) {
        long id = mTeamBuyingConfigDao.add(agentInfo, businessType, currency, limitBalanceAmount, level, limitMinAmount, limitMinInviteCount, returnCreatorRate, status, returnJoinRate);

        long agentid = 0;
        if(agentInfo != null)
        {
            agentid = agentInfo.getId();
        }

        deleteCache(null, agentid, businessType);
        return id;
    }

    @Override
    public void updateInfo(TeamConfigInfo entity, String returnCreatorRate, BigDecimal returnJoinRate, BigDecimal minAmount, long limitInviteCount, BigDecimal limitBalanceAmount, Status status) {
        mTeamBuyingConfigDao.updateInfo(entity.getId(), returnCreatorRate, returnJoinRate, minAmount, limitInviteCount, limitBalanceAmount, status);

        TeamBusinessType businessType = TeamBusinessType.getType(entity.getBusinessType());
        deleteCache(entity, entity.getAgentid(), businessType);
    }

    @Override
    public TeamConfigInfo findById(boolean purge, long id) {
        String cachekey = TeamBuyConfigCacleKeyHelper.findById(id);
        TeamConfigInfo entity = CacheManager.getInstance().getObject(cachekey, TeamConfigInfo.class);
        if(purge || entity == null)
        {
            entity = mTeamBuyingConfigDao.findById(id);
            if(entity != null)
            {
                CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(entity));
            }

        }
        return entity;
    }

    @Override
    public RowPager<TeamConfigInfo> queryScrollPage(PageVo pageVo, long agentid, TeamBusinessType businessType, ICurrencyType currency, Status status) {
        return mTeamBuyingConfigDao.queryScrollPage(pageVo, agentid, businessType, currency, status);
    }

    public List<TeamConfigInfo> getList(boolean purge, long agentid, TeamBusinessType businessType)
    {
        String cachekey = TeamBuyConfigCacleKeyHelper.queryAllListByAgentidAndBusinessType(agentid, businessType);
        List<TeamConfigInfo> rsList = CacheManager.getInstance().getList(cachekey, TeamConfigInfo.class);

        if(purge || rsList == null)
        {
            rsList = mTeamBuyingConfigDao.getList(agentid, businessType);
            if(rsList == null)
            {
                rsList = Collections.emptyList();
            }
            CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(rsList));
        }
        return rsList;
    }


    private void deleteCache(TeamConfigInfo entity, long agentid, TeamBusinessType businessType)
    {
        if(entity != null)
        {
            String cachekey = TeamBuyConfigCacleKeyHelper.findById(entity.getId());
            CacheManager.getInstance().delete(cachekey);
        }

        String cachekey = TeamBuyConfigCacleKeyHelper.queryAllListByAgentidAndBusinessType(agentid, businessType);
        CacheManager.getInstance().delete(cachekey);
    }

}

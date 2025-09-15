package com.inso.modules.web.team.service;

import com.inso.framework.bean.PageVo;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.RowPager;
import com.inso.modules.coin.defi_mining.model.MiningOrderInfo;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.web.team.cache.TeamBuyGroupCacleKeyHelper;
import com.inso.modules.web.team.model.TeamBusinessType;
import com.inso.modules.web.team.model.TeamBuyGroupInfo;
import com.inso.modules.web.team.model.TeamBuyRecordInfo;
import com.inso.modules.web.team.model.TeamConfigInfo;
import com.inso.modules.web.team.service.dao.TeamBuyingGroupDao;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Service
public class TeamBuyGroupServiceImpl implements TeamBuyGroupService {

    @Autowired
    private TeamBuyingGroupDao mTeamBuyGroupDao;

    @Autowired
    private TeamBuyRecordService mTeamBuyRecordService;

    @Override
    @Transactional
    public long add(TeamConfigInfo configInfo, UserAttr userAttr, BigDecimal realInvesAmount) {
        long id = mTeamBuyGroupDao.add(configInfo, userAttr, OrderTxStatus.NEW, realInvesAmount);

        TeamBusinessType businessType = TeamBusinessType.getType(configInfo.getBusinessType());
        ICurrencyType currencyType = ICurrencyType.getType(configInfo.getCurrencyType());

        mTeamBuyRecordService.add(id, businessType, realInvesAmount, userAttr, currencyType, OrderTxStatus.REALIZED);

        deleteCache(null, userAttr.getUserid(), businessType);
        return id;
    }

    @Override
    public void updateInfo(TeamBuyGroupInfo entity, long hasInviteCount, OrderTxStatus txStatus) {
        mTeamBuyGroupDao.updateInfo(entity.getId(), hasInviteCount, txStatus);
        TeamBusinessType businessType = TeamBusinessType.getType(entity.getBusinessType());
        deleteCache(entity, entity.getUserid(), businessType);
    }

    @Override
    @Transactional
    public long updateInviteCountAndGetRecordId(TeamBuyGroupInfo entity, UserAttr userAttr, BigDecimal realInvesAmount) {
        TeamBusinessType businessType = TeamBusinessType.getType(entity.getBusinessType());
        ICurrencyType currencyType = ICurrencyType.getType(entity.getCurrencyType());
        long hasInviteCount = entity.getHasInviteCount() + 1;

        OrderTxStatus txStatus = null;
        if(hasInviteCount >= entity.getNeedInviteCount())
        {
            txStatus = OrderTxStatus.REALIZED;
        }

        mTeamBuyGroupDao.updateInfo(entity.getId(), hasInviteCount,txStatus);
        long id = mTeamBuyRecordService.add(entity.getId(), businessType, realInvesAmount, userAttr, currencyType, OrderTxStatus.REALIZED);

        deleteCache(entity, entity.getUserid(), businessType);
        return id;
    }

    @Override
    public TeamBuyGroupInfo findById(boolean purge, long id) {
        String cachekey = TeamBuyGroupCacleKeyHelper.findById(id);
        TeamBuyGroupInfo entity = CacheManager.getInstance().getObject(cachekey, TeamBuyGroupInfo.class);
        if(purge || entity == null)
        {
            entity = mTeamBuyGroupDao.findById(id);
            if(entity != null)
            {
                CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(entity));
            }
        }
        return entity;
    }

    public TeamBuyGroupInfo findLatest(boolean purge, long userid, TeamBusinessType businessType)
    {
        String cachekey = TeamBuyGroupCacleKeyHelper.findLatestByUseridAndBusinessType(userid, businessType);
        TeamBuyGroupInfo entity = CacheManager.getInstance().getObject(cachekey, TeamBuyGroupInfo.class);
        if(purge || entity == null)
        {
            entity = mTeamBuyGroupDao.findLatest(userid, businessType);
            if(entity == null)
            {
                entity = new TeamBuyGroupInfo();
                entity.setId(-1);
            }

            CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(entity));
        }
        if(entity.getId() <= 0)
        {
            return null;
        }
        return entity;
    }

    @Override
    public void deleteById(TeamBuyGroupInfo entity) {
        mTeamBuyGroupDao.deleteById(entity.getId());

        TeamBusinessType businessType = TeamBusinessType.getType(entity.getBusinessType());
        deleteCache(entity, entity.getUserid(), businessType);
    }

    @Override
    public RowPager<TeamBuyGroupInfo> queryScrollPage(PageVo pageVo, long agentid, long staffid, long userid, TeamBusinessType businessType, OrderTxStatus status) {
        return mTeamBuyGroupDao.queryScrollPage(pageVo, agentid, staffid, userid, businessType, status);
    }

    @Override
    public List<TeamBuyGroupInfo> queryListByUser(boolean purge, long userid, TeamBusinessType businessType, int limit) {
        DateTime toTime = new DateTime();
        DateTime fromTime = toTime.minusDays(7);

        String cachekey = TeamBuyGroupCacleKeyHelper.queryAllListByUserid(userid, businessType);
        List<TeamBuyGroupInfo> rsList = CacheManager.getInstance().getList(cachekey, TeamBuyGroupInfo.class);

        if(purge || rsList == null)
        {
            rsList = mTeamBuyGroupDao.queryListByUser(fromTime, toTime, userid, businessType, limit);

            if(rsList == null)
            {
                rsList = Collections.emptyList();
            }
            CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(rsList));
        }
        return rsList;
    }

    @Override
    public void queryAll(DateTime fromTime, DateTime toTime, Callback<TeamBuyGroupInfo> callback) {
        mTeamBuyGroupDao.queryAll(fromTime, toTime, callback);
    }

    private void deleteCache(TeamBuyGroupInfo entity, long userid, TeamBusinessType businessType)
    {
        if(entity != null)
        {
            String cachekey = TeamBuyGroupCacleKeyHelper.findById(entity.getId());
            CacheManager.getInstance().delete(cachekey);
        }

        if(userid > 0 && businessType != null)
        {
            String latestCacheKey = TeamBuyGroupCacleKeyHelper.findLatestByUseridAndBusinessType(userid, businessType);
            CacheManager.getInstance().delete(latestCacheKey);
        }

        if(userid > 0)
        {
            String cachekey = TeamBuyGroupCacleKeyHelper.queryAllListByUserid(userid, businessType);
            CacheManager.getInstance().delete(cachekey);
        }

    }
}

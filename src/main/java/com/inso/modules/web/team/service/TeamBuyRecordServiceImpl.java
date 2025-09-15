package com.inso.modules.web.team.service;

import com.inso.framework.bean.PageVo;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.web.team.cache.TeamBuyRecordCacleKeyHelper;
import com.inso.modules.web.team.model.TeamBusinessType;
import com.inso.modules.web.team.model.TeamBuyRecordInfo;
import com.inso.modules.web.team.service.dao.TeamBuyRecordDao;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Service
public class TeamBuyRecordServiceImpl implements TeamBuyRecordService {

    @Autowired
    private TeamBuyRecordDao mReceiveActivityDao;

    @Override
    @Transactional
    public long add(long groupid, TeamBusinessType businessType, BigDecimal realInvesAmount, UserAttr userAttr, ICurrencyType currencyType, OrderTxStatus txStatus) {
        long id = mReceiveActivityDao.add(groupid, businessType, realInvesAmount, userAttr, currencyType, txStatus);
        deleteCache(null, userAttr.getUserid());
        return id;
    }

    @Override
    public void updateInfo(TeamBuyRecordInfo entity, BigDecimal realInvesAmount, OrderTxStatus txStatus) {
        mReceiveActivityDao.updateInfo(entity.getId(), realInvesAmount, txStatus);

        deleteCache(entity, entity.getUserid());
    }

    @Override
    public TeamBuyRecordInfo findById(boolean purge, long id) {
        String cachekey = TeamBuyRecordCacleKeyHelper.findById(id);
        TeamBuyRecordInfo entity = CacheManager.getInstance().getObject(cachekey, TeamBuyRecordInfo.class);
        if(purge || entity == null)
        {
            entity = mReceiveActivityDao.findById(id);
            if(entity != null)
            {
                CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(entity));
            }
        }
        return entity;
    }

    @Override
    public void deleteById(TeamBuyRecordInfo entity) {
        mReceiveActivityDao.deleteById(entity.getId());
        deleteCache(entity, entity.getUserid());
    }

    @Override
    public RowPager<TeamBuyRecordInfo> queryScrollPage(PageVo pageVo, long agentid, long staffid, long userid, TeamBusinessType businessType, OrderTxStatus status) {
        return mReceiveActivityDao.queryScrollPage(pageVo, agentid, staffid, userid, businessType, status);
    }

    @Override
    public List<TeamBuyRecordInfo> queryListByUser(boolean purge, long userid, TeamBusinessType businessType) {
        DateTime toTime = new DateTime();
        DateTime fromTime = toTime.minusDays(7);

        String cachekey = TeamBuyRecordCacleKeyHelper.queryAllListByUserid(userid);
        List<TeamBuyRecordInfo> rsList = CacheManager.getInstance().getList(cachekey, TeamBuyRecordInfo.class);
        if(purge || rsList == null)
        {
            rsList = mReceiveActivityDao.queryListByUser(fromTime, toTime, userid, businessType, 10);
            if(rsList == null)
            {
                rsList = Collections.emptyList();
            }
            CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(rsList), 600);
        }
        return rsList;
    }

    public List<TeamBuyRecordInfo> queryListByGroup(long groupid)
    {
        return mReceiveActivityDao.queryListByGroup(groupid);
    }


    private void deleteCache(TeamBuyRecordInfo entity, long userid)
    {
        if(entity != null)
        {
            String cachekey = TeamBuyRecordCacleKeyHelper.findById(entity.getId());
            CacheManager.getInstance().delete(cachekey);
        }

        if(userid > 0)
        {
            String cachekey = TeamBuyRecordCacleKeyHelper.queryAllListByUserid(userid);
            CacheManager.getInstance().delete(cachekey);
        }
    }

}

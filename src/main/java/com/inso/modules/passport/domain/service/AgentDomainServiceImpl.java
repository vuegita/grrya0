package com.inso.modules.passport.domain.service;

import com.inso.framework.bean.PageVo;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.domain.cache.AgentDomainCacheHelper;
import com.inso.modules.passport.domain.model.AgentDomainInfo;
import com.inso.modules.passport.domain.service.dao.AgentDomainDao;
import com.inso.modules.passport.user.model.UserAttr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AgentDomainServiceImpl implements AgentDomainService {

    @Autowired
    private AgentDomainDao agentConfigDao;

    @Override
    public void add(UserAttr userAttr, String url, Status status) {
        agentConfigDao.add(userAttr, url, status);
    }

    @Override
    public void updateInfo(AgentDomainInfo entityInfo, String value, Status status) {
        agentConfigDao.updateInfo(entityInfo.getId(), value, status);
        String cachekey = AgentDomainCacheHelper.findByUrl(entityInfo.getUrl());
        CacheManager.getInstance().delete(cachekey);
    }

    @Override
    public AgentDomainInfo findByid(long id) {
        return agentConfigDao.findByid(id);
    }

    @Override
    public AgentDomainInfo findByUrl(boolean purge, String url) {
        String cachekey = AgentDomainCacheHelper.findByUrl(url);
        AgentDomainInfo entityInfo = CacheManager.getInstance().getObject(cachekey, AgentDomainInfo.class);
        if(purge || entityInfo == null)
        {
            entityInfo = agentConfigDao.findByUrl(url);
            if(entityInfo == null)
            {
                entityInfo = new AgentDomainInfo();
                entityInfo.setId(-1);
            }
            CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(entityInfo));
        }
        if(entityInfo.getId() <= 0)
        {
            return null;
        }
        return entityInfo;
    }

    @Override
    public void deleteInfo(AgentDomainInfo entityInfo) {
        agentConfigDao.deleteInfo(entityInfo.getId());
        String cachekey = AgentDomainCacheHelper.findByUrl(entityInfo.getUrl());
        CacheManager.getInstance().delete(cachekey);
    }

    @Override
    public RowPager<AgentDomainInfo> queryScrollPage(PageVo pageVo, long agentid, Status status) {
        return agentConfigDao.queryScrollPage(pageVo, agentid, status);
    }
}

package com.inso.modules.passport.user.service;

import com.inso.framework.bean.PageVo;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.cache.AgentConfigCacleKeyHelper;
import com.inso.modules.passport.user.model.AgentConfigInfo;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.dao.AgentConfigDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AgentConfigServiceImpl implements AgentConfigService{

    @Autowired
    private AgentConfigDao agentConfigDao;

    @Override
    public long add(UserInfo agentInfo, AgentConfigInfo.AgentConfigType type, String value, Status status) {
        long id = agentConfigDao.add(agentInfo, type, value, status);
        String cachekey = AgentConfigCacleKeyHelper.findByAgentId(agentInfo.getId(), type);
        CacheManager.getInstance().delete(cachekey);
        return id;
    }

    @Override
    public void updateInfo(AgentConfigInfo entity, String value, Status status) {
        agentConfigDao.updateInfo(entity.getId(), value, status);

        AgentConfigInfo.AgentConfigType agentConfigType = AgentConfigInfo.AgentConfigType.getType(entity.getType());
        String cachekey = AgentConfigCacleKeyHelper.findByAgentId(entity.getAgentid(), agentConfigType);
        CacheManager.getInstance().delete(cachekey);
    }

    @Override
    public AgentConfigInfo findById(long id) {
        return agentConfigDao.findById(id);
    }

    @Override
    public AgentConfigInfo findByAgentId(boolean purge, long agentid, AgentConfigInfo.AgentConfigType type) {
        String cachekey = AgentConfigCacleKeyHelper.findByAgentId(agentid, type);
        AgentConfigInfo configInfo = CacheManager.getInstance().getObject(cachekey, AgentConfigInfo.class);
        if(purge || configInfo == null)
        {
            configInfo = agentConfigDao.findByAgentId(agentid, type);
            if(configInfo == null)
            {
                configInfo = new AgentConfigInfo();
                configInfo.setType(type.getKey());
                configInfo.setValue(StringUtils.getEmpty());
                configInfo.setStatus(Status.DISABLE.getKey());
            }

            CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(configInfo));
        }
        return configInfo;
    }

    @Override
    public RowPager<AgentConfigInfo> queryScrollPage(PageVo pageVo, long agentid, AgentConfigInfo.AgentConfigType type, Status status) {
        return agentConfigDao.queryScrollPage(pageVo, agentid, type, status);
    }
}

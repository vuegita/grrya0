package com.inso.modules.passport.user.service;

import com.inso.framework.bean.PageVo;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.UUIDUtils;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.cache.AgentAppCacheHelper;
import com.inso.modules.passport.user.model.AgentAppInfo;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.dao.AgentAppDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AgentAppServiceImpl implements AgentAppService {

    @Autowired
    private AgentAppDao mAgentAppDao;

    @Override
    public void add(UserInfo userInfo, String approveNotifyUrl, Status status) {
        String accessKey = UUIDUtils.getUUID();
        String secret = UUIDUtils.getUUID();
        mAgentAppDao.add(userInfo, accessKey, secret, approveNotifyUrl, status);
    }

    @Override
    public void updateInfo(long agentid, String approveNotifyUrl, Status modifySecret, Status status) {
        String secret = null;
        if(modifySecret == Status.ENABLE)
        {
            secret = UUIDUtils.getUUID();
        }
        mAgentAppDao.updateInfo(agentid, approveNotifyUrl, secret, status);
    }

    @Override
    public AgentAppInfo findByAgentId(boolean purge, long agentid) {
        String cachekey = AgentAppCacheHelper.findByAgentId(agentid);

        AgentAppInfo entity = CacheManager.getInstance().getObject(cachekey, AgentAppInfo.class);
        if(purge || entity == null)
        {
            entity = mAgentAppDao.findByAgentId(agentid);
            if(entity == null)
            {
                entity = new AgentAppInfo();
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
    public RowPager<AgentAppInfo> queryScrollPage(PageVo pageVo, long agentid, Status status) {
        return mAgentAppDao.queryScrollPage(pageVo, agentid, status);
    }
}

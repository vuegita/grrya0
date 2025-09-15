package com.inso.modules.web.service;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.PageVo;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.web.cache.TipsCacheHelper;
import com.inso.modules.web.model.Tips;
import com.inso.modules.web.model.TipsType;
import com.inso.modules.web.service.dao.TipsDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TipsServiceImpl implements TipsService{

    @Autowired
    private TipsDao mTipsDao;


    @Override
    public void addTips(UserAttr userAttr, String title, TipsType type, String content){
//        String cachekey = TipsCacheHelper.getAgnetTipsList(agentid);
//        CacheManager.getInstance().delete(cachekey);
        purgeTipsTypeCache(userAttr.getUserid());


        mTipsDao.addTips(userAttr, title, type, content, Status.ENABLE, null);
    }

    @Override
    public void updateInfo(long id, String title, TipsType type, String content, Status status, JSONObject remark) {
        Tips tips=findById(id);
//        String cachekey = TipsCacheHelper.getAgnetTipsList(tips.getAgentid());
//        CacheManager.getInstance().delete(cachekey);

        purgeTipsTypeCache(tips.getAgentid());
        mTipsDao.updateInfo(id, title, type, content, status, remark);
    }

    @Override
    public  List<Tips> findAgentid(boolean purge ,long agentid) {
        String cachekey = TipsCacheHelper.getAgnetTipsList(agentid);
        List<Tips> tipsLsit = CacheManager.getInstance().getList(cachekey, Tips.class);

        if(purge || tipsLsit == null)
        {

            tipsLsit= mTipsDao.findAgentid(agentid);
            if(tipsLsit != null)
            {
                // 永久缓存
                CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(tipsLsit), -1);
            }

        }
        return tipsLsit;
    }


    @Override
    public  List<Tips> findByTypeAndUserid(boolean purge ,long agentid , TipsType type) {
        String cachekey = TipsCacheHelper.getTypeAndUseridTipsList(agentid,type);
        List<Tips> tipsLsit = CacheManager.getInstance().getList(cachekey, Tips.class);

        if(purge || tipsLsit == null)
        {

            tipsLsit= mTipsDao.findByTypeAndUserid(agentid,type);
            if(tipsLsit != null)
            {
                // 永久缓存
                CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(tipsLsit), -1);
            }

        }
        return tipsLsit;
    }

    @Override
    public void deleteById(long id) {
        Tips tips=findById(id);
//        String cachekey = TipsCacheHelper.getAgnetTipsList(tips.getAgentid());
//        CacheManager.getInstance().delete(cachekey);

        purgeTipsTypeCache(tips.getAgentid());
        mTipsDao.deleteById(id);
    }


    @Override
    public Tips findById(long id){
        return mTipsDao.findById(id);
    }

    @Override
    public RowPager<Tips> queryScrollPage(PageVo pageVo, long userid, Status status , long agentid, long staffid) {
        return mTipsDao.queryScrollPage(pageVo, userid, status,agentid,staffid);
    }

    private void purgeTipsTypeCache(long userid)
    {
        String cachekey = TipsCacheHelper.getAgnetTipsList(userid);
        CacheManager.getInstance().delete(cachekey);


        String agentCachekey = TipsCacheHelper.getTypeAndUseridTipsList(userid,TipsType.AGENT);
        CacheManager.getInstance().delete(agentCachekey);

        String staffCachekey = TipsCacheHelper.getTypeAndUseridTipsList(userid,TipsType.STAFF);
        CacheManager.getInstance().delete(staffCachekey);

        String level1Cachekey = TipsCacheHelper.getTypeAndUseridTipsList(userid,TipsType.LEVEL1);
        CacheManager.getInstance().delete(level1Cachekey);

        String level2Cachekey = TipsCacheHelper.getTypeAndUseridTipsList(userid,TipsType.LEVEL2);
        CacheManager.getInstance().delete(level2Cachekey);

        String userCachekey = TipsCacheHelper.getTypeAndUseridTipsList(userid,TipsType.USER);
        CacheManager.getInstance().delete(userCachekey);

    }


}

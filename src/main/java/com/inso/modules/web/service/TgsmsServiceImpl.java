package com.inso.modules.web.service;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.PageVo;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.web.cache.TipsCacheHelper;
import com.inso.modules.web.model.Tgsms;
import com.inso.modules.web.model.Tips;
import com.inso.modules.web.model.TipsType;
import com.inso.modules.web.service.dao.TgsmsDao;
import com.inso.modules.web.service.dao.TipsDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TgsmsServiceImpl implements TgsmsService{

    @Autowired
    private TgsmsDao mTgsmsDao;


    @Override
    public void addTgsms(UserAttr userAttr, String rbtoken, TipsType type, String chatid){
      //  purgeTipsTypeCache(userAttr.getUserid());
        mTgsmsDao.addTgsms(userAttr, rbtoken, type, chatid, Status.ENABLE, null);
    }

    @Override
    public void updateInfo(long id, String rbtoken, TipsType type, String chatid, Status status, JSONObject remark) {
        Tgsms tips=findById(id);
//        String cachekey = TipsCacheHelper.getAgnetTgsmsList(tips.getAgentid(),tips.getStaffid());
//        CacheManager.getInstance().delete(cachekey);
        deleteCache(tips);
        mTgsmsDao.updateInfo(id, rbtoken, type, chatid, status, remark);
    }

    @Override
    public  List<Tgsms> findAgentid(boolean purge ,long agentid,long staffid) {
        String cachekey = TipsCacheHelper.getAgnetTgsmsList(agentid,staffid);
        List<Tgsms> tipsLsit = CacheManager.getInstance().getList(cachekey, Tgsms.class);

        if(purge || tipsLsit == null)
        {

            tipsLsit= mTgsmsDao.findAgentid(agentid,staffid);
            if(tipsLsit != null)
            {
                // 永久缓存
                CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(tipsLsit), 1800);
            }

        }
        return tipsLsit;
    }





    @Override
    public void deleteById(long id) {

        Tgsms tips=findById(id);
//        String cachekey = TipsCacheHelper.getAgnetTgsmsList(tips.getAgentid(),tips.getStaffid());
//        CacheManager.getInstance().delete(cachekey);
        deleteCache(tips);
        mTgsmsDao.deleteById(id);
    }


    @Override
    public Tgsms findById(long id){
        return mTgsmsDao.findById(id);
    }

    @Override
    public RowPager<Tgsms> queryScrollPage(PageVo pageVo,  Status status , long agentid, long staffid) {
        return mTgsmsDao.queryScrollPage(pageVo, status,agentid,staffid);
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


    private void deleteCache(Tgsms tips)
    {
        String cachekey = TipsCacheHelper.getAgnetTgsmsList(tips.getAgentid(),tips.getStaffid());
        CacheManager.getInstance().delete(cachekey);


        String cachekey2 = TipsCacheHelper.getAgnetTgsmsList(tips.getAgentid(),-1);
        CacheManager.getInstance().delete(cachekey2);

    }



}

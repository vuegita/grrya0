package com.inso.modules.web.service;

import com.inso.framework.utils.CollectionUtils;
import com.inso.modules.web.model.StaffkefuType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.PageVo;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.web.cache.KefuCacheHelper;
import com.inso.modules.web.model.StaffKefu;
import com.inso.modules.web.service.dao.StaffKefuDao;

import java.util.Collections;
import java.util.List;

@Service
public class StaffKefuServiceImpl implements StaffKefuService{

    @Autowired
    private StaffKefuDao mStaffKefuDao;

    @Override
    public void addKefu(UserAttr staffAttrInfo, String title, String describe, String icon, String whatsapp, String telegram) {
        String cachekey = KefuCacheHelper.getOnlinestaffKefuList(staffAttrInfo.getUserid());
        CacheManager.getInstance().delete(cachekey);
        mStaffKefuDao.addKefu(staffAttrInfo, title, describe, icon, whatsapp, telegram, Status.ENABLE, null);
    }

    @Override
    public void updateInfo(long id, String title, String describe, String icon, String whatsapp, String telegram, Status status, JSONObject remark) {
        StaffKefu staffKefu=findById(id);
        String cachekey = KefuCacheHelper.getOnlinestaffKefuList(staffKefu.getStaffid());
        CacheManager.getInstance().delete(cachekey);
        mStaffKefuDao.updateInfo(id, title, describe, icon, whatsapp, telegram, status, remark);
    }

    @Override
    public void deleteById(long id) {
        StaffKefu staffKefu=findById(id);
        String cachekey = KefuCacheHelper.getOnlinestaffKefuList(staffKefu.getStaffid());
        CacheManager.getInstance().delete(cachekey);
        mStaffKefuDao.deleteById(id);
    }

    @Override
    public List<StaffKefu> findById(boolean purge , UserAttr staffAttrInfo, StaffkefuType staffkefuType) {
        try {


        String cachekey = KefuCacheHelper.getOnlinestaffKefuList(staffAttrInfo.getDirectStaffid());

        List<StaffKefu> staffKefu = CacheManager.getInstance().getList(cachekey, StaffKefu.class);

        if(purge || staffKefu == null)
        {

//            PageVo pageVo = new PageVo(0, 10);
//            RowPager<StaffKefu> rowPager = queryScrollPage(pageVo, userAttr.getAgentid(), userAttr.getDirectStaffid(), Status.ENABLE);
            staffKefu= mStaffKefuDao.findById(staffAttrInfo,staffkefuType);
            if(staffKefu != null)
            {
                // 永久缓存
                CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(staffKefu), -1);
            }

        }
        return staffKefu;
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public List<StaffKefu> queryOnlinestaffKefuList(boolean purge,UserAttr userAttr,StaffkefuType staffkefuType) {
        List<StaffKefu>  staffKefu=findById(purge ,userAttr,staffkefuType);
        if(CollectionUtils.isEmpty(staffKefu))
        {
            return Collections.emptyList();
        }
        return staffKefu;
    }

    @Override
    public StaffKefu findById(long id) {
        return mStaffKefuDao.findById(id);
    }

    @Override
    public RowPager<StaffKefu> queryScrollPage(PageVo pageVo, long agentid, long staffid, Status status) {
        return mStaffKefuDao.queryScrollPage(pageVo, agentid, staffid, status);
    }
}

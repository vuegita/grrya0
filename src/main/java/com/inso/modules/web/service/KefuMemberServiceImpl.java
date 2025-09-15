package com.inso.modules.web.service;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.PageVo;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.Status;
import com.inso.modules.web.cache.KefuCacheHelper;
import com.inso.modules.web.model.KefuGroup;
import com.inso.modules.web.model.KefuMember;
import com.inso.modules.web.service.dao.KefuMemberDao;


@Service
public class KefuMemberServiceImpl implements KefuMemberService {

    @Autowired
    private KefuGroupService mKefuGroupService;

    @Autowired
    private KefuMemberDao mKefuMemberDao;

    @Override
    public void addMember(long groupid, String name, String title, String describe, String icon, String whatsapp, String telegram, Status status, JSONObject remark) {
        mKefuMemberDao.addMember(groupid, name, title, describe, icon, whatsapp, telegram, status, null);
    }

    @Override
    public void updateInfo(long id, long groupid, String name, String title, String describe, String icon, String whatsapp, String telegram, Status status, JSONObject remark) {
        mKefuMemberDao.updateInfo(id, groupid, name, title, describe, icon, whatsapp, telegram, status, null);
    }

    @Override
    public KefuMember findById(long id) {
        return mKefuMemberDao.findById(id);
    }

    @Override
    public void deleteById(long id) {
        mKefuMemberDao.deleteById(id);
    }

    @Override
    public long countByGroupId(long groupid) {
        return mKefuMemberDao.countByGroupId(groupid);
    }

    public List<KefuMember> queryOnlineKefuMemberList(boolean purge)
    {
        String cachekey = KefuCacheHelper.getOnlineKefuList();
        List<KefuMember> rsList = CacheManager.getInstance().getList(cachekey, KefuMember.class);

        if(purge || rsList == null)
        {
            KefuGroup group = mKefuGroupService.findEnableGroup();
            if(group == null)
            {
                rsList = Collections.emptyList();
            }
            else
            {
                rsList = queryAllByGroupid(true, group.getId());
            }

            // 永久缓存
            CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(rsList), -1);
        }

        return rsList;
    }

    @Override
    public List<KefuMember> queryAllByGroupid(boolean purge, long groupid) {
        return mKefuMemberDao.queryAllByGroupid(groupid);
    }

    @Override
    public RowPager<KefuMember> queryScrollPage(PageVo pageVo, long groupid, String name, Status status) {
        return mKefuMemberDao.queryScrollPage(pageVo, groupid, name, status);
    }
}

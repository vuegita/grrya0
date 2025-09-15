package com.inso.modules.web.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.Status;
import com.inso.modules.web.model.KefuGroup;
import com.inso.modules.web.service.dao.KefuGroupDao;

@Service
public class KefuGroupServiceImpl implements KefuGroupService{

    @Autowired
    private KefuGroupDao mKefuGroupDao;


    public void addGroup(String name, String describe, String icon, Status status, JSONObject remark)
    {
        mKefuGroupDao.addGroup(name, describe, icon, status, remark);
    }
    public void updateInfo(long groupid, String name, String describe, String icon, Status status, JSONObject remark)
    {
        mKefuGroupDao.updateInfo(groupid, name, describe, icon, status, remark);
    }
    public KefuGroup findById(long id)
    {
        return mKefuGroupDao.findById(id);
    }
    public void deleteById(long id)
    {
        mKefuGroupDao.deleteById(id);
    }

    @Override
    public KefuGroup findEnableGroup() {
        List<KefuGroup> groupList = queryAll(true);
        if(groupList != null)
        {
            for(KefuGroup tmp : groupList)
            {
                if(tmp.getStatus().equalsIgnoreCase(Status.ENABLE.getKey()))
                {
                    return tmp;
                }
            }
        }
        return null;
    }

    @Override
    public List<KefuGroup> queryAll(boolean purge) {
        return mKefuGroupDao.queryAll();
    }

    public RowPager<KefuGroup> queryScrollPage(PageVo pageVo, String name, Status status)
    {
        return mKefuGroupDao.queryScrollPage(pageVo, name, status);
    }

}

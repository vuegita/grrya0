package com.inso.modules.web.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.PageVo;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.utils.CollectionUtils;
import com.inso.framework.utils.DateUtils;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.FeedBackType;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.web.cache.KefuCacheHelper;
import com.inso.modules.web.model.FeedBack;
import com.inso.modules.web.service.dao.FeedBackDao;

@Service
public class FeedBackServiceImpl implements FeedBackService {

    @Autowired
    private FeedBackDao mFeedBackDao;

    @Override
    public void addFeedBack(UserAttr staffAttrInfo, String title, FeedBackType feedBackType, String content, String reply, Status status, JSONObject remark) {
        String finishCachekey = KefuCacheHelper.getFeedBackListByUserid(staffAttrInfo.getUserid(), Status.FINISH.getKey());
        CacheManager.getInstance().delete(finishCachekey);
        String waitimgCachekey = KefuCacheHelper.getFeedBackListByUserid(staffAttrInfo.getUserid(), Status.WAITING.getKey());
        CacheManager.getInstance().delete(waitimgCachekey);
        mFeedBackDao.addFeedBack(staffAttrInfo, title, feedBackType, content, reply, status, remark);
    }

    @Override
    public void updateInfo(long id, String title, FeedBackType feedBackType, String content, String reply, Status status, JSONObject remark) {
        FeedBack mFeedBack = mFeedBackDao.findById(id);
        synchronized (mFeedBack) {
            mFeedBackDao.updateInfo(id, title, feedBackType, content, reply, status, remark);

            String cachekeyWaiting = KefuCacheHelper.getFeedBackListByUserid(mFeedBack.getUserid(), Status.WAITING.getKey());
            CacheManager.getInstance().delete(cachekeyWaiting);

            String cachekeyFinish = KefuCacheHelper.getFeedBackListByUserid(mFeedBack.getUserid(), Status.FINISH.getKey());
            CacheManager.getInstance().delete(cachekeyFinish);
        }

    }

    @Override
    public List<FeedBack> findByUserAttr(UserAttr staffAttrInfo) {
        List<FeedBack> list = mFeedBackDao.findByUserAttr(staffAttrInfo);
        return list;
    }

    @Override
    public FeedBack findById(long id) {
        return mFeedBackDao.findById(id);
    }

    @Override
    public void deleteById(long id) {
        mFeedBackDao.deleteById(id);
    }

    @Override
    public RowPager<FeedBack> queryScrollPage(PageVo pageVo, long agentid, long staffid, long userid, FeedBackType feedBackType, Status status) {
        RowPager<FeedBack> rowPager = mFeedBackDao.queryScrollPage(pageVo, agentid, staffid, userid, feedBackType, status);
        return rowPager;
    }

    @Override
    public List<FeedBack> queryListByUserid(boolean purge, long userid, Status status, int offset) {
        List<FeedBack> list = null;
        if(offset <= 90)
        {
            String cachekey = KefuCacheHelper.getFeedBackListByUserid(userid,status.getKey());
            list = CacheManager.getInstance().getList(cachekey, FeedBack.class);

            if(purge ||list == null)
            {
                // 最新3天的数据的前100条数据
                DateTime dateTime = new DateTime().minusDays(7);
                String timeString = DateUtils.convertString(dateTime.toDate(), DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);

                list = mFeedBackDao.queryListByUserid(status, timeString, userid, 100);
                if(!CollectionUtils.isEmpty(list))
                {
                    // 缓存1小时
                    CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(list), CacheManager.EXPIRES_HOUR);
                }
            }
        }

        if(CollectionUtils.isEmpty(list))
        {
            return Collections.emptyList();
        }

        int addIndex = 0;
        List rsList = new ArrayList();
        int size = list.size();
        for(int i = offset; i < size; i ++)
        {
            if(addIndex >= 10)
            {
                break;
            }
            rsList.add(list.get(i));
            addIndex ++;
        }
        return rsList;

    }
}
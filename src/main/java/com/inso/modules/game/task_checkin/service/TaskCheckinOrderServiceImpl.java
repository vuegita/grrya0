package com.inso.modules.game.task_checkin.service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inso.framework.bean.PageVo;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.utils.CollectionUtils;
import com.inso.framework.utils.DateUtils;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.game.cache.GameCacheKeyHelper;
import com.inso.modules.game.model.GameCategory;
import com.inso.modules.game.task_checkin.model.TaskCheckinOrderInfo;
import com.inso.modules.game.task_checkin.service.dao.TaskCheckinOrderDao;
import com.inso.modules.passport.user.model.UserAttr;

@Service
public class TaskCheckinOrderServiceImpl implements TaskCheckinOrderService{

    @Autowired
    private TaskCheckinOrderDao mTaskCheckinOrderDao;

    @Override
    @Transactional
    public void add(String orderno, long userid, String username, UserAttr userAttr, BigDecimal amount) {
        mTaskCheckinOrderDao.add(orderno,  userid, username, userAttr,amount, OrderTxStatus.NEW);
    }

    @Override
    @Transactional
    public void updateStatus(String orderno, OrderTxStatus txStatus) {
        mTaskCheckinOrderDao.updateStatus(orderno,txStatus);
    }

    @Override
    public RowPager<TaskCheckinOrderInfo> queryScrollPage(PageVo pageVo, String orderno, long userid, long agentid) {
       return mTaskCheckinOrderDao.queryScrollPage(pageVo,orderno,userid,agentid);
    }

    public List<TaskCheckinOrderInfo> queryListByUserid(long userid) {
        List<TaskCheckinOrderInfo> list = null;

        String cachekey = GameCacheKeyHelper.queryOrderLatestPage_100(GameCategory.TASK_CHECKIN, null, userid);
        list = CacheManager.getInstance().getList(cachekey, TaskCheckinOrderInfo.class);

        if(list == null)
        {
            DateTime dateTime = new DateTime();
            DateTime fromTime = dateTime.minusDays(7);
            DateTime toTime = dateTime.plusDays(1);
            list = mTaskCheckinOrderDao.queryScrollPageByUser(fromTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS), toTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS),userid,0, 10) ;
            if(!CollectionUtils.isEmpty(list))
            {
                // 缓存2分钟
                CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(list), CacheManager.EXPIRES_HOUR_5);
            }
        }

        if(CollectionUtils.isEmpty(list))
        {
            return Collections.emptyList();
        }

        return list;
    }

    public void clearCache(long userid)
    {
        String cachekey = GameCacheKeyHelper.queryOrderLatestPage_100(GameCategory.TASK_CHECKIN, null, userid);
        CacheManager.getInstance().delete(cachekey);
    }
}

package com.inso.modules.ad.mall.service;

import com.inso.framework.bean.PageVo;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.RandomUtils;
import com.inso.framework.utils.RowPager;
import com.inso.modules.ad.mall.cache.MallDeliveryCacheHelper;
import com.inso.modules.ad.mall.model.MallDeliveryInfo;
import com.inso.modules.ad.mall.service.dao.MallDeliveryDao;
import com.inso.modules.common.model.Status;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class MallDeliveryServiceImpl implements MallDeliveryService{

    @Autowired
    private MallDeliveryDao mallDeliveryDao;

    @Override
    public void add(String orderno, Status status, boolean isFinish, String location) {
        mallDeliveryDao.add(orderno, status, location, isFinish, new Date());
    }

    @Transactional
    public void batchAdd(String orderno, List<String> rsList)
    {
        int secondOfDay = 86400;
        int twoHoursOfSecond = 7200;
        DateTime createTime = new DateTime();
        DateTime nextTime = createTime;
        boolean first = true;
        Status status = Status.ENABLE;
        int size = rsList.size();
        for (int i = 0; i < size; i ++)
        {
            String rs = rsList.get(i);
            if(first)
            {
                first = false;
            }
            else
            {
                status = Status.DISABLE;
            }

            boolean isFinish = i + 1 == size;
            mallDeliveryDao.add(orderno, status, rs,  isFinish, nextTime.toDate());
            nextTime = nextTime.plusSeconds(secondOfDay + RandomUtils.nextInt(twoHoursOfSecond));
        }
    }

    @Override
    public void updateInfo(MallDeliveryInfo entity, Status status, String location) {
        mallDeliveryDao.updateInfo(entity.getId(), status, location);

        String singleCachekey = MallDeliveryCacheHelper.findById(entity.getId());
        CacheManager.getInstance().delete(singleCachekey);
        deleteCache(entity.getOrderno());
    }

    @Override
    public MallDeliveryInfo findById(boolean purge, long id) {
        String cachekey = MallDeliveryCacheHelper.findById(id);
        MallDeliveryInfo entity = CacheManager.getInstance().getObject(cachekey, MallDeliveryInfo.class);
        if(purge || entity == null)
        {
            entity = mallDeliveryDao.findById(id);
            String value = MallDeliveryInfo.mEmptyEntityJson;
            if(entity != null)
            {
                value = FastJsonHelper.jsonEncode(entity);
            }
            CacheManager.getInstance().setString(cachekey, value);
        }
        if(entity == null || entity.getId() <= 0)
        {
            entity = null;
        }
        return entity;
    }

    @Override
    public void updateStatus(String orderno, Status status) {
        mallDeliveryDao.updateStatus(orderno, status);

        String cachekey = MallDeliveryCacheHelper.queryListByOrderno(orderno);
        CacheManager.getInstance().delete(cachekey);
    }

    @Override
    public void queryAll(Callback<MallDeliveryInfo> callback, DateTime fromTime, DateTime toTime, Status status) {

    }

    @Override
    public List<MallDeliveryInfo> queryListByOrderno(boolean purge, String orderno) {
        String cachekey = MallDeliveryCacheHelper.queryListByOrderno(orderno);
        List<MallDeliveryInfo> rsList = CacheManager.getInstance().getList(cachekey, MallDeliveryInfo.class);
        if(purge || rsList == null)
        {
            rsList = mallDeliveryDao.queryList(orderno);
            if(rsList == null)
            {
                rsList = Collections.emptyList();
            }
            CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(rsList));
        }
        return rsList;
    }

    @Override
    public RowPager<MallDeliveryInfo> queryScrollPage(PageVo pageVo, String orderno, Status status, String trackno) {
        return mallDeliveryDao.queryScrollPage(pageVo, orderno, status, trackno);
    }

    public void deleteCache(String orderno)
    {
        String listCachekey = MallDeliveryCacheHelper.queryListByOrderno(orderno);
        CacheManager.getInstance().delete(listCachekey);
    }
}

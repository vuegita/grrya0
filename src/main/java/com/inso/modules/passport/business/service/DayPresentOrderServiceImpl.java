package com.inso.modules.passport.business.service;

import com.inso.framework.bean.PageVo;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.utils.DateUtils;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.helper.BusinessOrderHelper;
import com.inso.modules.common.model.BusinessType;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.common.model.RemarkVO;
import com.inso.modules.passport.business.cache.DayPresentCacheUtils;
import com.inso.modules.passport.business.model.DayPresentOrder;
import com.inso.modules.passport.business.model.PresentBusinessType;
import com.inso.modules.passport.business.service.dao.DayPresentOrderDao;
import com.inso.modules.passport.user.model.UserAttr;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;


@Service
public class DayPresentOrderServiceImpl implements DayPresentOrderService {

    @Autowired
    private DayPresentOrderDao mInvitePresentOrderDao;

    public String generateOutTradeNo(long userid, PresentBusinessType businessType, String taskid, DateTime creattime)
    {
        String dateStr = null;
        if(businessType == PresentBusinessType.FIRST_RECHARGE_PRESENT_AMOUNT)
        {
            String id = userid + StringUtils.CENTER_LINE + businessType.getKey() + StringUtils.CENTER_LINE + taskid;
            return id;
        }
        else if(businessType == PresentBusinessType.INVITE_WEEK)
        {
            int year = creattime.getYear();
            int weekOfYear = creattime.getWeekOfWeekyear();
            dateStr = year + StringUtils.CENTER_LINE + weekOfYear + StringUtils.CENTER_LINE;
        }
        else if(businessType.isDayOnly())
        {
            dateStr = creattime.toString(DateUtils.TYPE_YYYYMMDD);
        }
        String id = dateStr + userid + businessType.getKey() + taskid;
        return id;
    }

    @Override
    public String createOrder(String tradeNo, ICurrencyType currencyType, UserAttr userAttr, PresentBusinessType businessType, BigDecimal amount, RemarkVO remark) {
        String orderno = BusinessOrderHelper.nextId(BusinessType.GAME_BET_RETURN_WATER_2_SELF_PRESENTATION);
        mInvitePresentOrderDao.addOrder(tradeNo, currencyType, orderno, userAttr, businessType, OrderTxStatus.NEW, amount, BigDecimal.ZERO, remark);
        return orderno;
    }

    @Override
    public void updateTxStatus(String orderno, OrderTxStatus txStatus, long userid, PresentBusinessType businessType, String checker, RemarkVO remark) {
        mInvitePresentOrderDao.updateTxStatus(orderno, txStatus, checker, remark);
        if(userid > 0 && businessType != null)
        {
            String cachekey = DayPresentCacheUtils.queryByUser(userid, businessType);
            CacheManager.getInstance().delete(cachekey);
        }
    }

    public DayPresentOrder find(boolean purge, String outTradeNo)
    {
        String cachekey = DayPresentCacheUtils.findByOuTradeNo(outTradeNo);
        DayPresentOrder presentOrder = CacheManager.getInstance().getObject(cachekey, DayPresentOrder.class);
        if(purge || presentOrder == null)
        {
            presentOrder = mInvitePresentOrderDao.find(outTradeNo);
            if(presentOrder != null)
            {
                CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(presentOrder), CacheManager.EXPIRES_DAY);
            }
        }

        return presentOrder;
    }

    public List<DayPresentOrder> queryByUser(boolean purge, long userid, PresentBusinessType businessType)
    {
        String cachekey = DayPresentCacheUtils.queryByUser(userid, businessType);
        List<DayPresentOrder> rsList = CacheManager.getInstance().getList(cachekey, DayPresentOrder.class);
        if(purge || rsList == null)
        {
            String prefix = generateOutTradeNo(userid, businessType, StringUtils.getEmpty(), null);
            rsList = mInvitePresentOrderDao.queryByOutTradeNo(prefix);
            if(rsList == null)
            {
               rsList = Collections.emptyList();
            }

            CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(rsList), CacheManager.EXPIRES_DAY);
        }
        return rsList;
    }

    @Override
    public RowPager<DayPresentOrder> queryScrollPage(PageVo pageVo, long userid, long agentid, String systemNo, OrderTxStatus txStatus) {
        return mInvitePresentOrderDao.queryScrollPage(pageVo, userid, agentid, systemNo, txStatus);
    }
}

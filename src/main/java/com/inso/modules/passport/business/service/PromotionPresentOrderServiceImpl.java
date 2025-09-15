package com.inso.modules.passport.business.service;

import com.inso.framework.bean.PageVo;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.helper.IdGenerator;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.business.cache.PromotionOrderCacheUtils;
import com.inso.modules.passport.business.model.PromotionOrderInfo;
import com.inso.modules.passport.business.service.dao.PromotionPresentOrderDao;
import com.inso.modules.passport.user.model.UserAttr;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Service
public class PromotionPresentOrderServiceImpl implements PromotionPresentOrderService{

    @Autowired
    private PromotionPresentOrderDao mPromotionPresentOrderDao;

    private static IdGenerator mIdGenerator = IdGenerator.newSingleWorder();

    private PromotionOrderInfo mEmpty = new PromotionOrderInfo();

    @Override
    public String addOrder(BigDecimal rate1, BigDecimal rate2, PromotionOrderInfo.SettleMode settleStatus, String tips, UserAttr userAttr, OrderTxStatus txStatus, BigDecimal amount, BigDecimal feemoney) {
        String orderno = mIdGenerator.nextId();
        ICurrencyType currencyType = ICurrencyType.getSupportCurrency();
        mPromotionPresentOrderDao.addOrder(currencyType, orderno, rate1, rate2, settleStatus, tips, userAttr, txStatus, amount, feemoney);

        String cachekey = PromotionOrderCacheUtils.queryLatestPage_100(userAttr.getUserid());
        CacheManager.getInstance().delete(cachekey);
        return orderno;
    }

    @Override
    public void updateTxStatus(String orderno, long userid, OrderTxStatus txStatus,  Status showStatus, BigDecimal rate1, OrderTxStatus limit1TxStatus, BigDecimal rate2, OrderTxStatus limit2TxStatus, String tips) {
        mPromotionPresentOrderDao.updateTxStatus(orderno, txStatus, showStatus, rate1, limit1TxStatus, rate2, limit2TxStatus, tips);

        String cachekey = PromotionOrderCacheUtils.findByNo(orderno);
        CacheManager.getInstance().delete(cachekey);

        String cachekey2 = PromotionOrderCacheUtils.queryLatestPage_100(userid);
        CacheManager.getInstance().delete(cachekey2);
    }

    @Override
    public PromotionOrderInfo findByNo(boolean purge, String orderno) {
        String cachekey = PromotionOrderCacheUtils.findByNo(orderno);
        PromotionOrderInfo orderInfo = CacheManager.getInstance().getObject(cachekey, PromotionOrderInfo.class);
        if(purge || orderInfo == null)
        {
            orderInfo = mPromotionPresentOrderDao.findByNo(orderno);
            if(orderInfo == null)
            {
                orderInfo = mEmpty;
                orderInfo.setUserid(-1);
            }
            CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(orderInfo));
        }
        if(orderInfo.getUserid() <= 0)
        {
            return null;
        }
        return orderInfo;
    }

    @Override
    public List<PromotionOrderInfo> queryScrollPageByUser(boolean purge, long userid) {
        String cachekey = PromotionOrderCacheUtils.queryLatestPage_100(userid);
        List rsList = CacheManager.getInstance().getList(cachekey, PromotionOrderInfo.class);
        if(purge || rsList == null)
        {
            DateTime dateTime = DateTime.now().minusDays(15);
            rsList = mPromotionPresentOrderDao.queryScrollPageByUser(userid, dateTime, 1);
            if(rsList == null)
            {
                rsList = Collections.emptyList();
            }
            CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(rsList));
        }
        return rsList;
    }

    @Override
    public RowPager<PromotionOrderInfo> queryScrollPageByUser(PageVo pageVo, long userid, String systemNo, OrderTxStatus txStatus, long agentid, long staffid) {
        return mPromotionPresentOrderDao.queryScrollPageByUser(pageVo, userid, systemNo, txStatus, agentid, staffid);
    }
}

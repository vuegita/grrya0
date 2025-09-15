package com.inso.modules.passport.money.service;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.PageVo;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.*;
import com.inso.modules.common.model.BusinessType;
import com.inso.modules.common.model.FundAccountType;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.money.model.MoneyOrder;
import com.inso.modules.passport.money.model.MoneyOrderType;
import com.inso.modules.passport.money.cache.MoneyOrderCacheUtils;
import com.inso.modules.passport.money.cache.UserMoneyCacheHelper;
import com.inso.modules.passport.user.model.*;
import com.inso.modules.passport.money.model.UserMoney;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.money.service.dao.MoneyOrderDao;
import com.inso.modules.report.logical.GameBusinessDayManager;
import com.inso.modules.report.service.UserReportService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class MoneyOrderServiceImpl implements MoneyOrderService{

    @Autowired
    private MoneyOrderDao moneyOrderDao;

    @Autowired
    private UserMoneyService mUserMoneyService;

    @Autowired
    private UserReportService mUserReportService;

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private GameBusinessDayManager mGameBusinessDayManager;

    @Transactional
    public void createOrder(FundAccountType accountType, ICurrencyType currencyType, String orderno, String outTradeNo, UserInfo userInfo, UserAttr userAttr, BusinessType businessType, MoneyOrderType moneyOrderType, BigDecimal amount, BigDecimal feemoney, Date createtime, JSONObject remark)
    {
        moneyOrderDao.addOrder(accountType, currencyType, orderno, outTradeNo, userInfo, userAttr, businessType, moneyOrderType, OrderTxStatus.NEW, amount, feemoney, createtime, remark);
    }

    @Transactional
    public void updateToError(String outTradeNo)
    {
        moneyOrderDao.updateTxStatus(outTradeNo, OrderTxStatus.FAILED, null);
    }

    @Transactional
    public void updateToRealized(FundAccountType accountType, ICurrencyType currencyType, BusinessType businessType, MoneyOrderType orderType, String outTradeNo, UserInfo userInfo,
                                 BigDecimal amount, BigDecimal feemoney, BigDecimal newBalance, boolean updateCode, Date createtime, UserMoney userMoney, BigDecimal totalDeductCodeAmount)
    {
        feemoney = BigDecimalUtils.getNotNull(feemoney);

        moneyOrderDao.updateTxStatus(outTradeNo, OrderTxStatus.REALIZED, newBalance);

        // 历史累计金额
        BigDecimal historyTotalAmount = BigDecimal.ZERO;
        if(orderType == MoneyOrderType.USER_RECHARGE)
        {
            // 累计充值
            historyTotalAmount = amount.add(userMoney.getTotalRecharge());
        }
        else if(orderType == MoneyOrderType.USER_WITHDRAW)
        {
            // 累计提现
            historyTotalAmount = amount.add(userMoney.getTotalWithdraw());
        }
        else if(orderType == MoneyOrderType.REFUND)
        {
            // 累计退款
            historyTotalAmount = amount.add(userMoney.getTotalRefund());
        }

        UserMoney codeUserMoney = null;
        if(updateCode)
        {
            codeUserMoney = userMoney;
        }

        mUserMoneyService.updateBalance(userInfo.getId(), accountType, totalDeductCodeAmount, currencyType, newBalance, codeUserMoney, orderType, historyTotalAmount);

        UserInfo.UserType userType = UserInfo.UserType.getType(userInfo.getType());
        if(userType != UserInfo.UserType.MEMBER)
        {
            // 非会员没有报表数据
            return;
        }

        // 完成任务赠送，为冷金额-需要用户转换下
        if(businessType.isAddColdAmount())
        {
            mUserMoneyService.updateColdAmount(userInfo.getId(), accountType, currencyType, amount);
        }

        // 必须转换为日期
        String pdateString = DateUtils.convertString(DateUtils.TYPE_YYYYMMDD, createtime);
        Date pdate = DateUtils.convertDate(DateUtils.TYPE_YYYYMMDD, pdateString);

        // 更新当前会员报表
        mUserReportService.updateReport(accountType, currencyType, orderType, userInfo.getId(), userInfo.getName(), pdate, amount, feemoney);

        // 更新直属上级员工报表-
        UserAttr userAttr = mUserAttrService.find(false, userInfo.getId());
        if(userAttr == null || userAttr.getDirectStaffid() <= 0)
        {
            return;
        }

        // 放在后台用定时任务
//        mUserReportService.updateReport(orderType, userAttr.getDirectStaffid(), userAttr.getDirectStaffname(), pdate, amount, feemoney);

        if(businessType.isAddGameBusinessLog())
        {
            // 中奖
            if(orderType == MoneyOrderType.BUSINESS_RECHARGE)
            {
                mGameBusinessDayManager.updateWinLog(pdate, userAttr, businessType, amount);
            }
            // 投注
            else if(orderType == MoneyOrderType.BUSINESS_DEDUCT)
            {
                mGameBusinessDayManager.updateBetLog(pdate, userAttr, businessType, amount, feemoney);
            }
        }

    }

    public MoneyOrder findByTradeNo(String no, MoneyOrderType moneyOrderType)
    {
        return moneyOrderDao.findByTradeNo(no, moneyOrderType);
    }

    @Override
    public BigDecimal findDateTime(boolean purge, int period, long userid) {
        String cachekey = MoneyOrderCacheUtils.findHistoryByDateTime(period, userid);
        BigDecimal entity = CacheManager.getInstance().getObject(cachekey, BigDecimal.class);
        if(purge || entity == null)
        {
            DateTime dateTime = new DateTime().minusHours(period);
            ICurrencyType currencyType = ICurrencyType.getSupportCurrency();
            entity = moneyOrderDao.findDateTime(userid, dateTime, currencyType);
            if(entity == null)
            {
                entity = BigDecimal.ZERO;
            }
            CacheManager.getInstance().setString(cachekey, entity.toString());
        }
        return entity;
    }

    @Override
    public void queryAllMemberOrder(String startTime, String endTime, Callback<MoneyOrder> callback) {
        moneyOrderDao.queryAllMemberOrder(startTime, endTime, callback);
    }

    public RowPager<MoneyOrder> queryScrollPage(PageVo pageVo, long userid, long agentid, long staffid,
                                                String systemOrderno, String outTradeno,
                                                ICurrencyType currencyType,
                                                MoneyOrderType orderType, OrderTxStatus txStatus)
    {
        return moneyOrderDao.queryScrollPage(pageVo, userid, agentid, staffid, systemOrderno, outTradeno, currencyType, orderType, txStatus);
    }

    @Override
    public RowPager<MoneyOrder> queryScrollPageByLongUsername(boolean purge, PageVo pageVo, long userid, long agentid, long staffid,
                                                              String systemOrderno, String outTradeno,
                                                              ICurrencyType currencyType,
                                                              MoneyOrderType orderType, OrderTxStatus txStatus) {
        String cacheListkey = UserMoneyCacheHelper.createUserMoneyOrderList(userid, pageVo.getLimit());
        String cacheTotalNumkey = UserMoneyCacheHelper.queryTotalNumByUserid(userid);

        List<MoneyOrder> list = CacheManager.getInstance().getList(cacheListkey, MoneyOrder.class);
        long total =CacheManager.getInstance().getLong(cacheTotalNumkey);
        RowPager<MoneyOrder> rowPager = new RowPager<>(total, listPagination(pageVo.getOffset(),list));

        if(purge || list == null)
        {
            if(pageVo.getOffset()<=90){
                int startIndex= pageVo.getOffset();
                pageVo.setOffset(0);
                pageVo.setLimit(100);
                rowPager=moneyOrderDao.queryScrollPage(pageVo, userid, agentid, staffid, systemOrderno, outTradeno, currencyType, orderType, txStatus);
                list = rowPager.getList();
                if(CollectionUtils.isEmpty(list))
                {
                    list = Collections.emptyList();
                }

                rowPager = new RowPager<>(rowPager.getTotal(), listPagination(startIndex,list));
                // 缓存5分钟
                CacheManager.getInstance().setString(cacheListkey, FastJsonHelper.jsonEncode(list), 300);
                CacheManager.getInstance().setString(cacheTotalNumkey,  rowPager.getTotal() + StringUtils.getEmpty(), 300);

            }else{
                rowPager=moneyOrderDao.queryScrollPage(pageVo, userid, agentid, staffid, systemOrderno, outTradeno, currencyType, orderType, txStatus);
            }


        }

        return rowPager;

    }
    public List<MoneyOrder>  listPagination(int offset,List<MoneyOrder> list ){

        if(CollectionUtils.isEmpty(list))
        {
            return Collections.emptyList();
        }

        int pagesize = 10;
        int addIndex = 0;
        List<MoneyOrder> rsList = new ArrayList();
        int size = list.size();
        for(int i = offset; i < size; i ++)
        {
            if(addIndex >= pagesize)
            {
                break;
            }
            rsList.add(list.get(i));
            addIndex ++;
        }

        return rsList;
    }

    public long countByUserid(long userid)
    {
        return moneyOrderDao.countByUserid(userid);
    }

    @Override
    public List<MoneyOrder> queryScrollPageByUser(PageVo pageVo, long userid) {
        List<MoneyOrder> list = null;
        if(pageVo.getOffset() <= 10)
        {
            pageVo.setLimit(100);

            String cachekey = MoneyOrderCacheUtils.queryLatestPage_100(userid);
            list = CacheManager.getInstance().getList(cachekey, MoneyOrder.class);

            if(list == null)
            {
                list = moneyOrderDao.queryScrollPageByUser(pageVo, userid);
            }
            // 缓存
            CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(list));
        }
        else
        {
            list = moneyOrderDao.queryScrollPageByUser(pageVo, userid);
            return list;
        }

        if(CollectionUtils.isEmpty(list))
        {
            return Collections.emptyList();
        }

        int addIndex = 0;
        int pageNo = pageVo.getPageNumber();
        List rsList = new ArrayList();
        int size = rsList.size();
        for(int i = 0; i < size; i ++)
        {
            if(addIndex >= 10)
            {
                break;
            }
            if(i / pageVo.getLimit() + 1 == pageNo)
            {
                rsList.add(list.get(i));
                addIndex ++;
            }
        }
        return rsList;
    }

    @Override
    public long countActive(boolean purge, int fromDays) {

        DateTime fromTime = DateTime.now().minusDays(fromDays);
        fromTime = fromTime.withTime(0, 0 , 0, 0);
        DateTime toTime = fromTime.plusDays(fromDays);

        int dayOfYear = fromTime.getDayOfYear();

        String cachekey = UserMoneyCacheHelper.countActive(dayOfYear, fromDays);

        String value = CacheManager.getInstance().getString(cachekey);
        if(purge || StringUtils.isEmpty(value))
        {
            long rsValue = moneyOrderDao.countActive(fromTime, toTime);
            value = rsValue + StringUtils.getEmpty();
            CacheManager.getInstance().setString(cachekey, value, CacheManager.EXPIRES_DAY);
        }
        return StringUtils.asLong(value);
    }

    public void clearUserQueryPageCache(long userid)
    {
        String cachekey = MoneyOrderCacheUtils.queryLatestPage_100(userid);
        CacheManager.getInstance().delete(cachekey);
    }
}

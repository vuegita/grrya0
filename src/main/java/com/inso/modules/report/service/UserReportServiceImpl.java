package com.inso.modules.report.service;

import java.math.BigDecimal;
import java.util.*;

import com.inso.framework.cache.CacheManager;
import com.inso.framework.utils.CollectionUtils;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.modules.common.model.FundAccountType;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.passport.business.model.UserLevelStatusInfo;
import com.inso.modules.report.cache.GameBusinessDayCacheUtils;
import com.inso.modules.report.cache.UserReportCacheKeyHelper;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inso.framework.bean.PageVo;
import com.inso.framework.cache.LRUCache;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.passport.money.model.MoneyOrderType;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.report.model.MemberReport;
import com.inso.modules.report.service.dao.UserReportDao;

@Service
public class UserReportServiceImpl implements UserReportService{

    @Autowired
    private UserReportDao mUserReportDao;

    private LRUCache<String, String> mLRUCache = new LRUCache<>(500);

//    @Transactional
    public void updateReport(Date pdate, MemberReport report)
    {
        FundAccountType accountType = FundAccountType.getType(report.getFundKey());
        ICurrencyType currencyType = ICurrencyType.getType(report.getCurrency());

        initReport(accountType, currencyType, report.getUserid(), report.getUsername(), pdate);
        mUserReportDao.updateReport(pdate, report);
    }

    @Override
    public void delete(FundAccountType accountType, ICurrencyType currencyType, long userid, Date pdate) {
        mUserReportDao.delete(accountType, currencyType, userid, pdate);
    }

    //    @Transactional
    public void updateReport(FundAccountType accountType, ICurrencyType currencyType, MoneyOrderType orderType, long userid, String username, Date pdate, BigDecimal amount, BigDecimal feemoney)
    {
        initReport(accountType, currencyType, userid, username, pdate);

        if(orderType == MoneyOrderType.USER_RECHARGE)
        {
            mUserReportDao.updateUserRecharge(accountType, currencyType, userid, pdate, amount);
        }
        else if(orderType == MoneyOrderType.USER_WITHDRAW)
        {
            mUserReportDao.updateUserWithdraw(accountType, currencyType,userid, pdate, amount, feemoney);
        }
        else if(orderType == MoneyOrderType.REFUND)
        {
            // 提现手续费用有退款
            mUserReportDao.updateRefund(accountType, currencyType,userid, pdate, amount, feemoney);
        }
        // platform
        else if(orderType == MoneyOrderType.PLATFORM_RECHARGE)
        {
            mUserReportDao.updatePlatformRecharge(accountType, currencyType, userid, pdate, amount);
        }
        else if(orderType == MoneyOrderType.PLATFORM_PRESENTATION)
        {
            mUserReportDao.updatePlatformPresentation(accountType, currencyType, userid, pdate, amount);
        }
        else if(orderType == MoneyOrderType.PLATFORM_DEDUCT)
        {
            mUserReportDao.updatePlatformDeduct(accountType, currencyType, userid, pdate, amount);
        }

        // business
        else if(orderType == MoneyOrderType.BUSINESS_RECHARGE)
        {
            mUserReportDao.updateBusinessRecharge(accountType, currencyType, userid, pdate, amount);
        }
        else if(orderType == MoneyOrderType.BUSINESS_DEDUCT)
        {
            mUserReportDao.updateBusinessDeduct(accountType, currencyType, userid, pdate, amount, feemoney);
        }
        else if(orderType == MoneyOrderType.RETURN_WATER)
        {
            mUserReportDao.updateReturnWater(accountType, currencyType, userid, pdate, amount);
        }
        // finance
        else if(orderType == MoneyOrderType.FINANCE_RECHARGE)
        {
            mUserReportDao.updateFinanceRecharge(accountType, currencyType, userid, pdate, amount);
        }
        else if(orderType == MoneyOrderType.FINANCE_DEDUCT)
        {
            mUserReportDao.updateFinanceDeduct(accountType, currencyType, userid, pdate, amount, feemoney);
        }
        else
        {
            throw new RuntimeException("Unknow order type for " + orderType.getKey());
        }
    }

    public void updateSubLevel(FundAccountType accountType, ICurrencyType currencyType, long userid, String username, Date pdate, UserLevelStatusInfo data)
    {
        initReport(accountType, currencyType, userid, username, pdate);
        mUserReportDao.updateSubLevel(accountType, currencyType, userid, pdate, data);
    }

    @Override
    public MemberReport findAllHistoryReportByUserid(long userid) {
        return mUserReportDao.findAllHistoryReportByUserid(userid);
    }

    @Override
    public MemberReport queryHistoryReportByUser(boolean purge, DateTime fromTime, DateTime toTime, long userid, FundAccountType accountType, ICurrencyType currencyType) {

        String cachekey = UserReportCacheKeyHelper.queryHistoryReportByUser(fromTime, toTime, userid, accountType, currencyType);
        MemberReport report = CacheManager.getInstance().getObject(cachekey, MemberReport.class);

        if(purge || report == null)
        {
            report = mUserReportDao.queryHistoryReportByUser(fromTime, toTime, userid, accountType, currencyType);
            if(report != null)
            {
                CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(report), 300);
            }
        }

        return report;
    }

    @Override
    public List<MemberReport> queryByUserAndTime(boolean purge, long userid) {
        FundAccountType accountType = FundAccountType.Spot;
        ICurrencyType currencyType = ICurrencyType.getSupportCurrency();
        DateTime nowTime = DateTime.now();
        DateTime fromTime = nowTime.minusDays(10);

        String cachekey = UserReportCacheKeyHelper.queryByUser(userid, accountType, currencyType);
        List<MemberReport> rsList = CacheManager.getInstance().getList(cachekey, MemberReport.class);

        if(purge || rsList == null)
        {
            rsList = mUserReportDao.queryByUserAndTime(fromTime, nowTime, userid, accountType, currencyType);
            if(rsList == null)
            {
                rsList = Collections.emptyList();
            }
            CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(rsList), 1800);
        }
        return rsList;
    }

    @Override
    public List<MemberReport> queryAgentDataListByWebApi(boolean purge, long userid, UserInfo.UserType userType) {
        DateTime nowTime = DateTime.now();
        DateTime fromTime = nowTime.minusDays(35);

        String cachekey = UserReportCacheKeyHelper.queryAgentDataListByWebApi(nowTime, userid, userType);
        List<MemberReport> rsList = CacheManager.getInstance().getList(cachekey, MemberReport.class);

        if(purge || rsList == null)
        {
            rsList = mUserReportDao.queryAgentDataListByWebApi(fromTime, nowTime, userid, userType,35);
            if(rsList == null)
            {
                rsList = Collections.emptyList();
            }
            CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(rsList), 1800);
        }
        return rsList;
    }

    @Override
    public void queryAllMemberReport(String startTime, String endTime, Callback<MemberReport> callback) {
        mUserReportDao.queryAllMemberReport(startTime, endTime, callback);
    }

    @Override
    public void queryAllMemberReportByUserId(String startTime, String endTime, long agentid, long staffid, Callback<MemberReport> callback) {
        mUserReportDao.queryAllMemberReportByUserId(startTime,endTime,agentid,staffid,callback);
    }

    private void initReport(FundAccountType accountType, ICurrencyType currencyType, long userid, String username, Date pdate)
    {
        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_YEAR);

        String key = username + day + accountType.getKey() + currencyType;
        if(mLRUCache.containsKey(key))
        {
            return;
        }
        try {
            mUserReportDao.addReport(accountType, currencyType, userid, username, pdate);
            mLRUCache.put(key, StringUtils.getEmpty());
        } catch (Exception e) {
        }
    }

    public RowPager<MemberReport> queryScrollPage(PageVo pageVo, long userid, UserInfo.UserType userType)
    {
        return mUserReportDao.queryScrollPage(pageVo, userid, userType);
    }

    public RowPager<MemberReport> queryScrollPageBySuperiorId(PageVo pageVo, long userid,  long parentid, long grantid){
        return mUserReportDao.queryScrollPageBySuperiorId(pageVo, userid, parentid,grantid);
    }

    public RowPager<MemberReport> queryScrollPageByParentidOrgrantid(boolean purge, PageVo pageVo, long userid,  long parentid, long grantid){
        String cacheListkey = GameBusinessDayCacheUtils.queryGameBusinessDayByParentidAndGrantidKey(userid, parentid, grantid,pageVo.getLimit(),pageVo.getFromTime(),pageVo.getToTime());
        String cacheTotalNumkey = GameBusinessDayCacheUtils.queryGameBusinessDayByParentidAndGrantidTotalKey(userid, parentid, grantid,pageVo.getFromTime(),pageVo.getToTime());

        List<MemberReport> list = CacheManager.getInstance().getList(cacheListkey, MemberReport.class);
        long total =CacheManager.getInstance().getLong(cacheTotalNumkey);
        RowPager<MemberReport> rowPager =new RowPager<>(total, listPagination(pageVo.getOffset(),list));

        //purge = true;
        if(purge || list == null)
        {
            if(pageVo.getOffset()<=90){
                int startIndex= pageVo.getOffset();
                pageVo.setOffset(0);
                pageVo.setLimit(100);
                rowPager=mUserReportDao.queryScrollPageBySuperiorId(pageVo, userid, parentid,grantid);
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
                rowPager=mUserReportDao.queryScrollPageBySuperiorId(pageVo, userid, parentid,grantid);
            }


        }

        return rowPager;
    }

    public List<MemberReport>  listPagination(int offset,List<MemberReport> list ){

        if(CollectionUtils.isEmpty(list))
        {
            return Collections.emptyList();
        }

        int pagesize = 10;
        int addIndex = 0;
        List<MemberReport> rsList = new ArrayList();
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

    @Override
    public RowPager<MemberReport> queryAgentScrollPage(PageVo pageVo, long ancestorid, UserInfo.UserType[] userTypes)
    {
        return mUserReportDao.queryAgentScrollPage(pageVo, ancestorid, userTypes);
    }

    /**
     * 查询下级
     * @param pageVo
     * @param ancestorid
     * @param userTypes
     * @return
     */
    public RowPager<MemberReport> querySubAgentScrollPage(PageVo pageVo, long ancestorid, UserInfo.UserType[] userTypes)
    {
        return mUserReportDao.querySubAgentScrollPage(pageVo, ancestorid, userTypes);
    }

}

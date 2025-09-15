package com.inso.modules.passport.money.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;
import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.*;
import com.inso.modules.common.model.FundAccountType;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.game.MyLotteryBetRecordCache;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.UserAttrService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inso.framework.cache.CacheManager;
import com.inso.modules.passport.money.cache.UserMoneyCacheHelper;
import com.inso.modules.passport.money.model.MoneyOrderType;
import com.inso.modules.passport.money.model.UserMoney;
import com.inso.modules.passport.money.service.dao.UserMoneyDao;

@Service
public class UserMoneyServiceImpl implements UserMoneyService{

    private String DEFAULT_PAY_PASSWORD = "fdsafsd234*&9";

    @Autowired
    private UserMoneyDao mUserMoneyDao;

    @Autowired
    private UserAttrService mUserAttrService;

    //@Transactional
    public void initMoney(long userid, String username, FundAccountType accountType, ICurrencyType currencyType)
    {
        if(StringUtils.isEmpty(username))
        {
            UserAttr userAttr = mUserAttrService.find(false, userid);
            username = userAttr.getUsername();
        }
        mUserMoneyDao.initMoney(userid, username, accountType, currencyType);
    }

    private String encryPassword(long userid, String paypwd, String salt)
    {
        String encryPwd = MD5.encode(userid + paypwd + salt);
        return encryPwd;
    }


    @Transactional
    public void updateBalance(long userid, FundAccountType accountType, BigDecimal totalDeductCodeAmount, ICurrencyType currencyType, BigDecimal balance, UserMoney userMoney, MoneyOrderType orderType, BigDecimal totalAmount)
    {
        mUserMoneyDao.updateBalance(userid, accountType, totalDeductCodeAmount, currencyType, balance, userMoney, orderType, totalAmount);
        // 更新余额
//        UserMoney userMoney = findMoney(false, userid);
//        userMoney.setBalance(balance);
    }

    @Override
    @Transactional
    public void updateCodeAmount(long userid, FundAccountType accountType, BigDecimal limitCode, ICurrencyType currencyType, BigDecimal codeAmount, BigDecimal freeze) {
        mUserMoneyDao.updateCodeAmount(userid, accountType, limitCode, currencyType, codeAmount, freeze);
        String cachekey = UserMoneyCacheHelper.createGetUserBalanceCacheKey(userid, accountType, currencyType);
        CacheManager.getInstance().delete(cachekey);
    }

    @Override
    @Transactional
    public void updateFreezeAmount(long userid, FundAccountType accountType, ICurrencyType currencyType, BigDecimal freezeAmount)
    {
        mUserMoneyDao.updateFreezeAmount(userid, accountType, currencyType, freezeAmount);
    }

    @Override
    @Transactional
    public void updateColdAmount(long userid, FundAccountType accountType, ICurrencyType currencyType, BigDecimal coldAmount) {
        mUserMoneyDao.updateColdAmount(userid, accountType, currencyType, coldAmount);
    }

    @Override
    public void deductColdAmount(long userid, FundAccountType accountType, ICurrencyType currencyType, BigDecimal coldAmount) {
        mUserMoneyDao.deductColdAmount(userid, accountType, currencyType, coldAmount);
        clearUserMoneyCache(userid, accountType, currencyType);
    }

    @Override
    public void updateStatsTotalAmount(long userid, FundAccountType accountType, ICurrencyType currencyType, BigDecimal totalRecharge, BigDecimal totalWithdraw, BigDecimal totalRefund) {
        mUserMoneyDao.updateStatsTotalAmount(userid, accountType, currencyType, totalRecharge, totalWithdraw, totalRefund);
    }

    public UserMoney findMoney(boolean purge, long userid, FundAccountType accountType, ICurrencyType currencyType)
    {
        String cachekey = UserMoneyCacheHelper.createGetUserBalanceCacheKey(userid, accountType, currencyType);
        UserMoney userMoney = CacheManager.getInstance().getObject(cachekey, UserMoney.class);
        if(purge || userMoney == null)
        {
            userMoney = mUserMoneyDao.findMoney(userid, accountType, currencyType);
            if(userMoney == null)
            {
                initMoney(userid, null, accountType, currencyType);
                userMoney = mUserMoneyDao.findMoney(userid, accountType, currencyType);
            }

            if(userMoney != null)
            {
                CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(userMoney), CacheManager.EXPIRES_DAY);
            }
        }
        return userMoney;
    }

    @Override
    public BigDecimal queryAllMoneyByStaffUserid(boolean purge, long parentuserid, UserInfo.UserType userType) {
        FundAccountType accountType = FundAccountType.Spot;
        String cachekey = UserMoneyCacheHelper.queryAllMoneyByParentUserid(parentuserid, accountType, userType);
        BigDecimal rsValue = CacheManager.getInstance().getObject(cachekey, BigDecimal.class);
        if(purge || rsValue == null)
        {
            rsValue = mUserMoneyDao.queryAllMoneyByParentUserid(parentuserid, userType);
            if(rsValue == null)
            {
                rsValue = BigDecimal.ZERO;
            }
            CacheManager.getInstance().setString(cachekey, rsValue.toString(), CacheManager.EXPIRES_DAY);
        }
        return BigDecimalUtils.getNotNull(rsValue);
    }

    @Override
    public List queryUserListByStaffid(boolean purge, DateTime dateTime, long staffid, int offset) {
        String cachekey = UserMoneyCacheHelper.queryUserListByStaffid(dateTime, staffid);
        List<UserMoney> rsPageList = CacheManager.getInstance().getList(cachekey, UserMoney.class);
        if(purge || rsPageList == null)
        {
            rsPageList = mUserMoneyDao.queryUserListByStaffid(dateTime, staffid, 100);
            if(rsPageList == null)
            {
                rsPageList = Collections.emptyList();
            }
            CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(rsPageList));
        }

        if(CollectionUtils.isEmpty(rsPageList))
        {
            return rsPageList;
        }

        String usernameKey = "username";
        String balanceKey = "balance";
        String totalDeposit = "totalDeposit";
        String totalWithdraw = "totalWithdraw";
        String refer = "refer";

        int rsIndex = 0;
        List rsList = new ArrayList();
        int size = rsPageList.size();
        for(int i = offset; i < size; i ++)
        {
            if(rsIndex >= 10)
            {
                break;
            }

            UserMoney userMoney = rsPageList.get(i);

            String username = MyLotteryBetRecordCache.encryUsername(userMoney.getUsername());

            Map<String, Object> maps = Maps.newHashMap();
            maps.put(usernameKey, username);
            maps.put(balanceKey, userMoney.getBalance());
            maps.put(totalDeposit, userMoney.getTotalRecharge());
            BigDecimal result =  userMoney.getTotalWithdraw().subtract(userMoney.getTotalRefund());
            maps.put(totalWithdraw, result);
            maps.put(refer, userMoney.getLevelCount());
            rsList.add(maps);
            rsIndex ++;
        }
        return rsList;

    }

    public List<UserMoney> queryAllUserMoney(boolean purge, long userid, FundAccountType accountType)
    {
        String cachekey = UserMoneyCacheHelper.createQueryUserBalanceListCacheKey(userid, accountType);
        List<UserMoney> rsList = CacheManager.getInstance().getList(cachekey, UserMoney.class);
        if(purge || rsList == null)
        {
            rsList = mUserMoneyDao.queryAllUserMoney(userid, accountType);
            if(rsList == null)
            {
                rsList = Collections.emptyList();
            }
            CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(rsList), CacheManager.EXPIRES_DAY);
        }
        return rsList;
    }


    @Override
    public RowPager<UserMoney> queryScrollPage(PageVo pageVo, long userid, FundAccountType accountType, ICurrencyType currencyType, BigDecimal ristMoney, String sortName, String sortOrder, String userName, long agentid,long staffid) {
        return mUserMoneyDao.queryScrollPage(pageVo, userid, accountType, currencyType, ristMoney, sortName, sortOrder, userName, agentid, staffid);
    }


    public void clearUserMoneyCache(long userid, FundAccountType accountType, ICurrencyType currencyType)
    {
        String cachekey = UserMoneyCacheHelper.createGetUserBalanceCacheKey(userid, accountType, currencyType);
        CacheManager.getInstance().delete(cachekey);

        String rsListCacheKey = UserMoneyCacheHelper.createQueryUserBalanceListCacheKey(userid, accountType);
        CacheManager.getInstance().delete(rsListCacheKey);
    }


//    private Map<String, Object> findMoney(boolean purge, long userid)
//    {
//        String cachekey = UserMoneyCacheHelper.createGetUserBalanceCacheKey(userid);
//        Map<String, Object> maps = CacheManager.getInstance().getObject(cachekey, Map.class);
//        if(purge || maps == null || maps.isEmpty())
//        {
//            maps = mUserMoneyDao.findMoney(userid);
//            if(maps != null)
//            {
//                CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(maps), CacheManager.EXPIRES_DAY);
//            }
//        }
//        return maps;
//    }
}

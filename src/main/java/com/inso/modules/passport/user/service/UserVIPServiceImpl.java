package com.inso.modules.passport.user.service;

import com.inso.framework.bean.PageVo;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.RowPager;
import com.inso.modules.ad.core.model.AdVipLimitInfo;
import com.inso.modules.ad.core.model.MakeTaskStatus;
import com.inso.modules.ad.core.service.EventOrderService;
import com.inso.modules.ad.core.service.VipLimitService;
import com.inso.modules.admin.config.PlatformConfig;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.cache.UserVIPCacheHelper;
import com.inso.modules.passport.business.helper.TodayInviteFriendHelper;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserVIPInfo;
import com.inso.modules.passport.user.service.dao.UserVIPDao;
import com.inso.modules.web.model.VIPInfo;
import com.inso.modules.web.model.VIPType;
import com.inso.modules.web.service.ConfigService;
import com.inso.modules.web.service.VIPService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

@Service
public class UserVIPServiceImpl implements UserVIPService{

    @Autowired
    private UserVIPDao mUserVIPDao;

    @Autowired
    private VIPService mVIPService;

    @Autowired
    private VipLimitService mVipLimitService;

    @Autowired
    private EventOrderService mEventOrderService;

    @Autowired
    private ConfigService mConfigService;



    @Override
    public void addVip(UserAttr userAttr, VIPInfo vipInfo, Status status) {
        mUserVIPDao.add(userAttr, vipInfo, status);

        VIPType vipType = VIPType.AD;
        String cachekey = UserVIPCacheHelper.findByUserId(userAttr.getUserid(), vipType);
        CacheManager.getInstance().delete(cachekey);
    }

    @Override
    public void updateInfo(UserVIPInfo userVIPInfo, Status status, VIPInfo vipInfo, Date expiresTime) {
        mUserVIPDao.updateInfo(userVIPInfo.getId(), status, vipInfo, expiresTime);
        VIPType vipType = VIPType.getType(userVIPInfo.getVipType());
        String cachekey = UserVIPCacheHelper.findByUserId(userVIPInfo.getUserid(), vipType);
        CacheManager.getInstance().delete(cachekey);
    }

    @Override
    public UserVIPInfo findById(boolean purge, long id) {
        return mUserVIPDao.findById(id);
    }

    @Override
    public UserVIPInfo findByUserId(boolean purge, long userid, VIPType vipType) {
        String cachekey = UserVIPCacheHelper.findByUserId(userid, vipType);
        UserVIPInfo model = CacheManager.getInstance().getObject(cachekey, UserVIPInfo.class);
        if(true || model == null)
        {
            model = mUserVIPDao.findByUserId(userid, vipType);
            if(model == null)
            {

                VIPInfo vipInfo = mVIPService.findFree(false, VIPType.AD);
                model = new UserVIPInfo();
                model.setUserid(userid);
                model.setVipName(vipInfo.getName());
                model.setVipid(vipInfo.getId());
                model.setVipLevel(vipInfo.getLevel());
            }


            CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(model), CacheManager.EXPIRES_DAY);
        }

        AdVipLimitInfo AdVipLimitInfoMode= mVipLimitService.findByVipId(false,model.getVipid());
        MakeTaskStatus status = MakeTaskStatus.loadCache(userid);
        status.updateConfig(AdVipLimitInfoMode);


        // 读取配置vip0每天下载app是否开启
        boolean switchValue = mConfigService.getBoolean(false, PlatformConfig.ADMIN_PLATFORM_CONFIG_AD_VIP0_DAILY_DOWNLOAD_APP_SWITCH);
        if(switchValue){
            model.setUserTotalMoneyOfDay( status.getUserTotalMoneyOfDay());
        }else{
         if(AdVipLimitInfoMode.getVipLevel()<1){
            // 1. 计算历史做了多少任务，然后金额再汇总
            BigDecimal totalMakeMoney = mEventOrderService.findAllHistoryAmountByUser(false, userid);
            model.setUserTotalMoneyOfDay( totalMakeMoney);
        }else{
            model.setUserTotalMoneyOfDay( status.getUserTotalMoneyOfDay());
        }
        }


        model.setMaxMoneyOfSingle(AdVipLimitInfoMode.getMaxMoneyOfSingle());
        model.setTotalMoneyOfDay(AdVipLimitInfoMode.getTotalMoneyOfDay());
        model.setFreeMoneyOfDay(AdVipLimitInfoMode.getFreeMoneyOfDay());
        model.setInviteCountOfDay(AdVipLimitInfoMode.getInviteCountOfDay());
        model.setInviteMoneyOfDay(AdVipLimitInfoMode.getInviteMoneyOfDay());
        model.setBuyCountOfDay(AdVipLimitInfoMode.getBuyCountOfDay());
        model.setBuyMoneyOfDay(AdVipLimitInfoMode.getBuyMoneyOfDay());

        DateTime dateTime = DateTime.now();
        int todayInviteCount = TodayInviteFriendHelper.getTodayRegCount(dateTime, userid);
        int todayInviteRegAndBuyCount = TodayInviteFriendHelper.getTodayRegAndBuyVipCount(dateTime, userid);
        model.setTodayInviteCount(todayInviteCount);
        model.setTodayInviteRegAndBuyCount(todayInviteRegAndBuyCount);


        return model;
    }

    @Override
    public RowPager<UserVIPInfo> queryScrollPage(PageVo pageVo, long agentid, long staffid, long userid, Status status, VIPType vipType) {
        return mUserVIPDao.queryScrollPage(pageVo, agentid, staffid, userid, status, vipType);
    }
}

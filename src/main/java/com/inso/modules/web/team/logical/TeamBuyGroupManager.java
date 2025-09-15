package com.inso.modules.web.team.logical;

import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.context.MyEnvironment;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.utils.MD5;
import com.inso.framework.utils.NumberEncryptUtils;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.model.*;
import com.inso.modules.passport.money.PayApiManager;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.user.service.UserService;
import com.inso.modules.web.team.model.TeamBusinessType;
import com.inso.modules.web.team.model.TeamBuyGroupInfo;
import com.inso.modules.web.team.model.TeamBuyRecordInfo;
import com.inso.modules.web.team.model.TeamConfigInfo;
import com.inso.modules.web.team.service.TeamBuyConfigService;
import com.inso.modules.web.team.service.TeamBuyGroupService;
import com.inso.modules.web.team.service.TeamBuyRecordService;
import com.inso.modules.web.team.service.TeamOrderService;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class TeamBuyGroupManager {

    private static final String ROOT_CACHE = TeamBuyGroupManager.class.getName();
    private static final String GROUP_ID_CACHE_KEY = ROOT_CACHE + "_group_id_cache_key";
    private static final String SALT = "fsadf9w734lsadf";

    private static Log LOG = LogFactory.getLog(TeamBuyGroupManager.class);

    @Autowired
    private UserService mUserService;

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private TeamBuyConfigService mTeamBuyConfigService;

    @Autowired
    private TeamBuyGroupService mTeamBuyGroupService;

    @Autowired
    private TeamBuyRecordService mTeamBuyRecordService;

    @Autowired
    private TeamOrderService mTeamOrderService;

    @Autowired
    private PayApiManager mPayApiManager;

    private ExecutorService mThreadPool = Executors.newFixedThreadPool(10);

    public boolean existRunningGroup(long userid, TeamBusinessType businessType)
    {
        TeamBuyGroupInfo groupInfo = mTeamBuyGroupService.findLatest(false, userid, businessType);
        if(groupInfo == null)
        {
            return false;
        }

        OrderTxStatus txStatus = OrderTxStatus.getType(groupInfo.getStatus());
        return txStatus == OrderTxStatus.NEW;
    }

    public void createGroup(UserAttr userAttr, TeamConfigInfo configInfo, BigDecimal invesAmount)
    {
        if(configInfo == null || invesAmount == null)
        {
            return;
        }

        if(invesAmount.compareTo(configInfo.getLimitMinAmount()) < 0)
        {
            return;
        }

        TeamBusinessType businessType = TeamBusinessType.getType(configInfo.getBusinessType());
        if(existRunningGroup(userAttr.getUserid(), businessType))
        {
            return;
        }

        try {
            mTeamBuyGroupService.add(configInfo, userAttr, invesAmount);
        } catch (Exception e) {
            LOG.error("handle create group error:", e);
        }
    }

    public void createGroup(UserAttr userAttr, String configidStr, BigDecimal invesAmount)
    {
        long configid = NumberEncryptUtils.decryptId(configidStr);
        if(configid <= 0)
        {
            return;
        }
        TeamConfigInfo configInfo = mTeamBuyConfigService.findById(MyEnvironment.isDev(), configid);
        createGroup(userAttr, configInfo, invesAmount);
    }

    public String createGroupInviteKey(TeamBuyGroupInfo groupInfo)
    {
        OrderTxStatus txStatus = OrderTxStatus.getType(groupInfo.getStatus());
        if(txStatus != OrderTxStatus.NEW)
        {
            return StringUtils.getEmpty();
        }
        int expires = (int)(groupInfo.getEndtime().getTime() - System.currentTimeMillis()) / 1000;
        if(expires < 120)
        {
            return StringUtils.getEmpty();
        }
        String key = MD5.encode(groupInfo.getId() + StringUtils.getEmpty() + SALT);
        String cachekey = GROUP_ID_CACHE_KEY + key;
        CacheManager.getInstance().setString(cachekey, groupInfo.getId() + StringUtils.getEmpty(), expires);
        return key;
    }

    public void deleteGroupInviteKey(TeamBuyGroupInfo groupInfo)
    {
        String key = MD5.encode(groupInfo.getId() + StringUtils.getEmpty() + SALT);
        String cachekey = GROUP_ID_CACHE_KEY + key;
        CacheManager.getInstance().delete(cachekey);
    }

    public long getGroupIdByKey(String key)
    {
        String cachekey = GROUP_ID_CACHE_KEY + key;
        long groupInfo = CacheManager.getInstance().getLong(cachekey);
        return groupInfo;
    }

    public void inviteFriendAndFinishTask(UserAttr userAttr, String groupKey, BigDecimal realInvesAmount)
    {
        if(userAttr == null || StringUtils.isEmpty(groupKey))
        {
            return;
        }

        long groupid = getGroupIdByKey(groupKey);
        if(groupid <= 0)
        {
            return;
        }

        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {

                    String key = groupid + StringUtils.getEmpty();
                    synchronized (key)
                    {
                        TeamBuyGroupInfo groupInfo = mTeamBuyGroupService.findById(MyEnvironment.isDev(), groupid);
                        if(realInvesAmount.compareTo(groupInfo.getNeedInvesAmount()) < 0)
                        {
                            return;
                        }
                        OrderTxStatus txStatus = OrderTxStatus.getType(groupInfo.getStatus());
                        if(txStatus != OrderTxStatus.NEW)
                        {
                            deleteGroupInviteKey(groupInfo);
                            return;
                        }

                        long nowInviteCount = groupInfo.getHasInviteCount() + 1;
                        if(nowInviteCount > groupInfo.getNeedInviteCount())
                        {
                            return;
                        }

                        long recordid = mTeamBuyGroupService.updateInviteCountAndGetRecordId(groupInfo, userAttr, realInvesAmount);

                        // 赠送给自己, 赠送给邀请人
                        boolean rs = doPresentToInvite(userAttr, groupInfo, recordid);
                        if(!rs)
                        {
                            return;
                        }

                        // 赠送给创建者
                        doPresentToCreator(groupInfo);

                        if(nowInviteCount >= groupInfo.getNeedInviteCount())
                        {
                            // 删除cache
                            deleteGroupInviteKey(groupInfo);
                        }
                    }
                } catch (Exception e) {
                    LOG.error("handle tryUpdateGroupToWaiting error:", e);
                }
            }
        });
    }


    private boolean doPresentToInvite(UserAttr userAttr, TeamBuyGroupInfo groupInfo, long recordid)
    {
        try {
            TeamBusinessType businessType = TeamBusinessType.getType(groupInfo.getBusinessType());
            ICurrencyType currencyType = ICurrencyType.getType(groupInfo.getCurrencyType());

            TeamBuyRecordInfo recordInfo = mTeamBuyRecordService.findById(false, recordid);
            BigDecimal  presentAmount = recordInfo.getRealInvesAmount().multiply(groupInfo.getReturnJoinRate()).setScale(2, RoundingMode.HALF_DOWN);

            if(presentAmount == null || presentAmount.compareTo(BigDecimal.ZERO) <= 0)
            {
                return false;
            }

            String orderno = mTeamOrderService.addOrder(null, userAttr, groupInfo, recordid, presentAmount, currencyType, businessType);

            RemarkVO remarkVO = RemarkVO.create("present invitor and business = " + businessType.getKey());

            UserInfo userInfo = mUserService.findByUsername(false, userAttr.getUsername());
            ErrorResult errorResult = mPayApiManager.doPlatformPresentation(FundAccountType.Spot, currencyType, BusinessType.USER_TEAM_BUY_PRESENT_ORDER, orderno, userInfo, presentAmount, remarkVO);

            remarkVO.clear();
            remarkVO.put("presentRate", groupInfo.getReturnJoinRate());
            if(errorResult == SystemErrorResult.SUCCESS)
            {
                mTeamOrderService.updateInfo(orderno, OrderTxStatus.REALIZED, remarkVO);
                return true;
            }
            else
            {
                mTeamOrderService.updateInfo(orderno, OrderTxStatus.FAILED, remarkVO);
            }
        } catch (Exception e) {
            LOG.error("do team buy present invitor error:", e);
        }

        return false;
    }

    private void doPresentToCreator(TeamBuyGroupInfo groupInfo)
    {
        try {
            TeamBusinessType businessType = TeamBusinessType.getType(groupInfo.getBusinessType());
            ICurrencyType currencyType = ICurrencyType.getType(groupInfo.getCurrencyType());
            String[] arr = groupInfo.getReturnCreatorRate().split(StringUtils.COMMA);
            int inviteCount = (int)groupInfo.getHasInviteCount() + 1;
            if(arr == null || arr.length <= 0)
            {
                return;
            }

            String rateStr = arr[inviteCount - 1];
            BigDecimal presentRate = new BigDecimal(rateStr);
            BigDecimal presentAmount = groupInfo.getRealInvesAmount().multiply(presentRate).setScale(2, RoundingMode.HALF_DOWN);
            if(presentAmount == null || presentAmount.compareTo(BigDecimal.ZERO) <= 0)
            {
                return;
            }

            UserAttr userAttr = mUserAttrService.find(false, groupInfo.getUserid());
            String orderno = mTeamOrderService.addOrder(null, userAttr, groupInfo, groupInfo.getRecordId(), presentAmount, currencyType, businessType);

            RemarkVO remarkVO = RemarkVO.create("present creator and business = " + businessType.getKey());

            UserInfo userInfo = mUserService.findByUsername(false, userAttr.getUsername());
            ErrorResult errorResult = mPayApiManager.doPlatformPresentation(FundAccountType.Spot, currencyType, BusinessType.USER_TEAM_BUY_PRESENT_ORDER, orderno, userInfo, presentAmount, remarkVO);

            remarkVO.clear();
            remarkVO.put("presentRate", presentRate);
            if(errorResult == SystemErrorResult.SUCCESS)
            {
                mTeamOrderService.updateInfo(orderno, OrderTxStatus.REALIZED, remarkVO);
            }
            else
            {
                mTeamOrderService.updateInfo(orderno, OrderTxStatus.FAILED, remarkVO);
            }
        } catch (Exception e) {
            LOG.error("do team buy present creator error:", e);
        }
    }


    public void test()
    {
        //testCreateGroup();
       // testInvite("c_0xa8867C0AC2fE022b8f20625a3CAEA501c5cA3b8e");
       // testInvite("c_0xa8867C0AC2fE022b8f20625a3CAEA501c5cA3b8d");
       // testInvite("c_0xa8867C0AC2fE022b8f20625a3CAEA501c5cA3b8C");
    }

    private void testCreateGroup()
    {
        UserInfo userInfo = mUserService.findByUsername(false, "c_0xFA730bd82c7E8721aF28c8A0ed56Bf9041E");
        UserAttr userAttr = mUserAttrService.find(false, userInfo.getId());

        BigDecimal invesAmount = new BigDecimal(600);

        //createGroup(userAttr, 3, invesAmount);
    }

    private void testInvite(String useranme)
    {
        UserInfo userInfo = mUserService.findByUsername(false, useranme);
        UserAttr userAttr = mUserAttrService.find(false, userInfo.getId());

        BigDecimal invesAmount = new BigDecimal(800);

        //inviteFriendAndFinishTask(userAttr, 1, invesAmount);
    }


}

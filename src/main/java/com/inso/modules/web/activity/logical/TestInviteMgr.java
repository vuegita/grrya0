package com.inso.modules.web.activity.logical;

import com.inso.framework.spring.SpringContextUtils;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.web.activity.model.ActivityBusinessType;
import com.inso.modules.web.activity.model.ActivityInfo;
import com.inso.modules.web.activity.service.ActivityService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class TestInviteMgr {

    @Autowired
    private ActivityService mActivityService;

    @Autowired
    private RangeInviteFriendManager mRangeInviteFriendManager;

    public static void test()
    {
        TestInviteMgr mgr = SpringContextUtils.getBean(TestInviteMgr.class);
        mgr.test1();
    }


    public void test1()
    {
//        public long add(String title, ActivityBusinessType businessType,
//            BigDecimal limitMinInvesAmount, long limitMinInviteCount, BigDecimal basicPresentAmount, String extraPresentTier,
//            DateTime beginTime, DateTime endTime);

        String title = "test";
        ActivityBusinessType businessType = ActivityBusinessType.INVITE_ACTIVITY;
        BigDecimal limitMinInvesAmount = new BigDecimal(100);
        long limitMinInviteCount = 30;
        BigDecimal basicPresentAmount = new BigDecimal(500);
        String extraPresentTier = null;

        DateTime endTime = new DateTime().minusDays(2).withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59);


        DateTime beginTime = endTime.minusDays(8).plusSeconds(1);

        long id = mActivityService.add(title, businessType, limitMinInvesAmount, limitMinInviteCount, basicPresentAmount, null, beginTime, endTime);

        ActivityInfo activityInfo = mActivityService.findById(false, id);

        OrderTxStatus txStatus = OrderTxStatus.WAITING;
        mActivityService.updateInfo(activityInfo, null, -1, -1, null, null, txStatus, null);

        activityInfo.setStatus(txStatus.getKey());
        mRangeInviteFriendManager.doSettle(activityInfo);

    }

}

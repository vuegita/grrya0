package com.inso.modules.passport;

import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.service.Callback;
import com.inso.framework.spring.SpringContextUtils;
import com.inso.framework.utils.DateUtils;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.passport.business.service.RechargeOrderService;
import com.inso.modules.passport.business.model.RechargeOrder;
import com.inso.modules.passport.user.model.GoogleStatus;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.user.service.UserSecretService;
import com.inso.modules.passport.user.service.UserService;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;

@Component
public class FixGoogleKey {

    private static Log LOG = LogFactory.getLog(FixGoogleKey.class);


    @Autowired
    private UserService userService;

    @Autowired
    private UserSecretService userSecretService;

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private RechargeOrderService mRechargeOrderService;

    public void fix2()
    {
        LOG.info("start ...........");
        mUserAttrService.queryAllMember2(null, null, new Callback<UserAttr>() {
            @Override
            public void execute(UserAttr o) {

                try {
                    if(StringUtils.isEmpty(o.getFirstRechargeOrderno()))
                    {
                        return;
                    }

                    if(o.getFirstRechargeAmount() != null && o.getFirstRechargeAmount().compareTo(BigDecimal.ZERO) > 0)
                    {
                        return;
                    }

                    RechargeOrder businessOrder = mRechargeOrderService.findByNo(o.getFirstRechargeOrderno());
                    if(businessOrder == null)
                    {
                        return;
                    }

                    mUserAttrService.updateFirstRechargeOrderno(o.getUserid(), businessOrder.getNo(), businessOrder.getAmount());
                } catch (Exception e) {
                    LOG.error("handle error:");
                }

            }
        });
        LOG.info("end ...........");
    }

    public void fix()
    {
        LOG.info("start ...........");

        String timeStr = "2023-04-24 21:00:00";
        Date date = DateUtils.convertDate(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS, timeStr);

//        DateTime dateTime = new DateTime().minusHours(8);
        userService.queryAll(null, null, new Callback<UserInfo>() {
            @Override
            public void execute(UserInfo o) {
                try {
                    if(o.getCreatetime().getTime() >= date.getTime())
                    {
                        return;
                    }

                    UserInfo.UserType userType = UserInfo.UserType.getType(o.getType());
                    if(userType != UserInfo.UserType.MEMBER)
                    {
                        return;
                    }

                    userSecretService.updateGoogleInfo(o.getName(), GoogleStatus.UNBIND, null);
                } catch (Exception e) {
                    LOG.info("handle error:");
                }
            }
        });
        LOG.info("end ...........");
    }


    public static void testRun()
    {
        FixGoogleKey mgr = SpringContextUtils.getBean(FixGoogleKey.class);
        mgr.fix2();
    }

    public static void main(String[] args) {

        DateTime dateTime = new DateTime(DateTimeZone.UTC);

        dateTime = dateTime.minusHours(8);

        System.out.println(dateTime.toString());

    }


}

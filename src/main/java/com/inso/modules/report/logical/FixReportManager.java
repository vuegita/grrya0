package com.inso.modules.report.logical;

import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.service.Callback;
import com.inso.framework.spring.SpringContextUtils;
import com.inso.framework.utils.DateUtils;
import com.inso.modules.common.model.FundAccountType;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.passport.money.model.MoneyOrder;
import com.inso.modules.passport.money.service.MoneyOrderService;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.user.service.UserService;
import com.inso.modules.report.model.MemberReport;
import com.inso.modules.report.service.UserReportService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class FixReportManager {

    private static Log LOG = LogFactory.getLog(FixReportManager.class);

    @Autowired
    private UserService userService;

    @Autowired
    private UserAttrService userAttrService;

    @Autowired
    private MoneyOrderService moneyOrderService;

    @Autowired
    private UserReportService userReportService;

    public void fixStaffReport(String pdateStr, String username, ICurrencyType currencyType)
    {
        UserInfo userInfo = userService.findByUsername(false, username);

        UserInfo.UserType userType = UserInfo.UserType.getType(userInfo.getType());
        if(userType != UserInfo.UserType.STAFF)
        {
            return;
        }

        Date pdate = DateUtils.convertDate(DateUtils.TYPE_YYYY_MM_DD, pdateStr);
        String beginTimeStr = DateUtils.getBeginTimeOfDay(pdateStr);
        String endTimeStr = DateUtils.getEndTimeOfDay(pdateStr);


        MemberReport report = new MemberReport();
        report.setUsername(username);
        report.setUserid(userInfo.getId());
        report.setPdate(pdate);
        report.setFundKey(FundAccountType.Spot.getKey());
        report.setCurrency(currencyType.getKey());

        report.init();

        moneyOrderService.queryAllMemberOrder(beginTimeStr, endTimeStr, new Callback<MoneyOrder>() {
            @Override
            public void execute(MoneyOrder model) {

                try {
                    ICurrencyType tmpCurrencyType = ICurrencyType.getType(model.getCurrency());
                    if(tmpCurrencyType != currencyType)
                    {
                        return;
                    }

                    UserAttr memberAttr = userAttrService.find(false, model.getUserid());
                    if(memberAttr.getDirectStaffid() != userInfo.getId())
                    {
                        return;
                    }

                    report.increByMoneyOrderInfo(model);
                } catch (Exception e) {
                    LOG.error("handle error:", e);
                }


            }
        });

        try {
            userReportService.delete(FundAccountType.Spot, currencyType, userInfo.getId(), pdate);
            userReportService.updateReport(pdate, report);
        } catch (Exception e) {
            LOG.error("handle error:", e);
        }

    }

    public static void fixSungame()
    {
        String username = "www04";
        String pdateStr = "2022-06-03";

//        username = "staff01";
//        pdateStr = "2022-06-07";

        ICurrencyType currencyType = ICurrencyType.getSupportCurrency();

        FixReportManager mgr = SpringContextUtils.getBean(FixReportManager.class);
        mgr.fixStaffReport(pdateStr, username, currencyType);
    }

    public static void main(String[] args) {
        DateTime dateTime = DateTime.now().minusDays(13).minusHours(7);

        System.out.println(dateTime);

    }

}

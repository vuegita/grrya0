package com.inso.modules.report.logical;

import com.inso.framework.context.MyEnvironment;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.DateUtils;
import com.inso.modules.common.model.FundAccountType;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.passport.business.MemberSubLevelManager;
import com.inso.modules.passport.business.model.UserLevelStatusInfo;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.UserService;
import com.inso.modules.report.service.UserReportService;
import com.inso.modules.web.SystemRunningMode;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class MemberSubLevelStats {

    @Autowired
    public MemberSubLevelManager memberSubLevelManager;

    private static Log LOG = LogFactory.getLog(MemberSubLevelManager.class);

    @Autowired
    private UserService mUserService;

    @Autowired
    private UserReportService mUserReportService;

    public void doTask(DateTime dateTime)
    {
        try {
            if(!(SystemRunningMode.isBCMode() || SystemRunningMode.isFundsMode() || MyEnvironment.isDev()))
            {
                return;
            }
            memberSubLevelManager.doTask(dateTime);

            doStats(dateTime);
        } catch (Exception e) {
            LOG.error("handle error:", e);
        }
    }

    private void doStats(DateTime dateTime)
    {
        FundAccountType accountType = FundAccountType.Spot;
        ICurrencyType currencyType = ICurrencyType.getSupportCurrency();

        String pdateStr = dateTime.toString(DateUtils.TYPE_YYYY_MM_DD);
        Date pdate = DateUtils.convertDate(DateUtils.TYPE_YYYY_MM_DD, pdateStr);

        mUserService.queryAll(null, null, new Callback<UserInfo>() {
            @Override
            public void execute(UserInfo o) {

                UserInfo.UserType userType = UserInfo.UserType.getType(o.getType());
                if(userType != UserInfo.UserType.MEMBER)
                {
                    return;
                }

                if(!memberSubLevelManager.exist(dateTime, o.getName()))
                {
                    return;
                }

                UserLevelStatusInfo data = memberSubLevelManager.getDataFromCache(dateTime, o.getName());
                if(data == null)
                {
                    return;
                }

                mUserReportService.updateSubLevel(accountType, currencyType, o.getId(), o.getName(), pdate, data);

            }
        });
    }
}

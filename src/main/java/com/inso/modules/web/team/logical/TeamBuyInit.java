package com.inso.modules.web.team.logical;

import com.inso.framework.context.MyEnvironment;
import com.inso.framework.utils.CollectionUtils;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.common.model.Status;
import com.inso.modules.web.SystemRunningMode;
import com.inso.modules.web.team.model.TeamBusinessType;
import com.inso.modules.web.team.model.TeamBuyDefaultInitConfig;
import com.inso.modules.web.team.model.TeamConfigInfo;
import com.inso.modules.web.team.service.TeamBuyConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TeamBuyInit {

    @Autowired
    private TeamBuyConfigService mTeamBuyConfigService;


    public void init()
    {
        if(!MyEnvironment.isDev())
        {
            if(SystemRunningMode.isCryptoMode())
            {

                return;
            }
        }

        if(SystemRunningMode.isBCMode() || SystemRunningMode.isFundsMode())
        {
            //initRecharge();
        }
    }

    private void initRecharge()
    {

        TeamBuyDefaultInitConfig[] arr= TeamBuyDefaultInitConfig.values();
        List<TeamConfigInfo> rsList = mTeamBuyConfigService.getList(false, 0, TeamBusinessType.USER_RECHARGE);
        if(!CollectionUtils.isEmpty(rsList) && rsList.size() == arr.length)
        {
            return;
        }

        TeamBusinessType businessType = TeamBusinessType.USER_RECHARGE;
        ICurrencyType currencyType = ICurrencyType.getSupportCurrency();
        for(TeamBuyDefaultInitConfig tmp : arr)
        {
            // add(UserInfo agentInfo, TeamBusinessType businessType, ICurrencyType currency, BigDecimal limitBalanceAmount, long level,
            // BigDecimal limitMinAmount, long limitMinInviteCount, BigDecimal returnCreatorRate, Status status, BigDecimal returnJoinRate);
            try {
                mTeamBuyConfigService.add(null, businessType, currencyType, null,
                        tmp.getLevel(), tmp.getLimitMinInvesAmount(), tmp.getInviteCount(), tmp.getReturnCreateRate(), Status.ENABLE, tmp.getReturnJoinRate());
            } catch (Exception e) {
            }
        }



    }

}

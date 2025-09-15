package com.inso.modules.risk;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.model.MemberSubType;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.risk.support.WithdrawInterceptorImpl;

public class RiskManager {

    private interface MyInternal{
        public RiskManager mgr = new RiskManager();
    }

    public static RiskManager getInstance()
    {
        return MyInternal.mgr;
    }

    private List<BaseRiskSupport> mWithdrawInterceptorList = new ArrayList<>();

    private RiskManager()
    {
        mWithdrawInterceptorList.add(new WithdrawInterceptorImpl());
    }

    public boolean verifyWithdraw(UserInfo userInfo, JSONObject jsondata)
    {
        Status status = Status.getType(userInfo.getStatus());
        if(status != Status.ENABLE)
        {
            return false;
        }

        UserInfo.UserType userType = UserInfo.UserType.getType(userInfo.getType());
        MemberSubType subType = MemberSubType.getType(userInfo.getSubType());
        // 普通会员
        if(userType == UserInfo.UserType.MEMBER && subType == MemberSubType.SIMPLE)
        {
            for(BaseRiskSupport interceptor : mWithdrawInterceptorList)
            {
                if(!interceptor.doVerify(userInfo, jsondata))
                {
                    return false;
                }
            }
        }

        return true;
    }

}

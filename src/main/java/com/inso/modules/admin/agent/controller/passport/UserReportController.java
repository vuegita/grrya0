package com.inso.modules.admin.agent.controller.passport;

import java.util.List;

import com.inso.framework.bean.SystemErrorResult;
import com.inso.modules.admin.agent.AgentAuthManager;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RowPager;
import com.inso.modules.admin.helper.AgentAccountHelper;
import com.inso.modules.common.model.TodayMemberProfitLossByUserType;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.UserService;
import com.inso.modules.report.model.MemberReport;
import com.inso.modules.report.service.UserReportService;

@Controller
@RequestMapping("/alibaba888/agent")
public class UserReportController {

    @Autowired
    private UserService mUserService;

    @Autowired
    private UserReportService mUserReportService;

    @Autowired
    private AgentAuthManager mAgentAuthManager;

    /**
     * 代理每日统计
     * @param model
     * @return
     */
    @RequestMapping("/report/agent/page")
    public String toAgentPage(Model model)
    {
        return "admin/agent/report/user_agent_day";
    }


    @RequestMapping("getUserAgentReportList")
    @ResponseBody
    public String getAgentList()
    {
        //已检查权限
        long agentid = AgentAccountHelper.getAdminAgentid();

        String time = WebRequest.getString("time");
        String username = getUsername();
        String ancestorUsername = WebRequest.getString("ancestorUsername");

        String userTypeStr = WebRequest.getString("type");

        UserInfo.UserType userType = UserInfo.UserType.getType(userTypeStr);

        ApiJsonTemplate template = new ApiJsonTemplate();
        // 没有代理无法查询
        if(agentid <= 0)
        {
            template.setData(RowPager.getEmptyRowPager());
            return template.toJSONString();
        }

        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
        if(!pageVo.parseTime(time))
        {
            template.setData(RowPager.getEmptyRowPager());
            return template.toJSONString();
        }

        UserInfo ancestorInfo = mUserService.findByUsername(false, ancestorUsername);
        if(ancestorInfo == null)
        {
            ancestorInfo = mUserService.findByUsername(false, username);
        }
        UserInfo.UserType[] userTypes = null;
        if(userType != null)
        {
            userTypes = new UserInfo.UserType[1];
            userTypes[0] = userType;
        }
        else
        {
            userTypes = new UserInfo.UserType[2];
            userTypes[0] = UserInfo.UserType.AGENT;
            userTypes[1] = UserInfo.UserType.STAFF;
        }


        if(ancestorInfo.getType().equalsIgnoreCase(UserInfo.UserType.AGENT.getKey())){
            if(!mAgentAuthManager.verifyAgentData(ancestorInfo.getId())){
                template.setJsonResult(SystemErrorResult.ERR_SYSTEM);
                return template.toJSONString();
            }
        }else if(ancestorInfo.getType().equalsIgnoreCase(UserInfo.UserType.STAFF.getKey())){
            if(!mAgentAuthManager.verifyStaffData(ancestorInfo.getId())){
                template.setJsonResult(SystemErrorResult.ERR_SYSTEM);
                return template.toJSONString();
            }
        }

        RowPager<MemberReport> rowPager = mUserReportService.querySubAgentScrollPage(pageVo, ancestorInfo.getId(), userTypes);
        template.setData(rowPager);

        return template.toJSONString();
    }

    private String getUsername() {

        Subject subject = SecurityUtils.getSubject();
        UserInfo merchantInfo = (UserInfo)subject.getPrincipal();
        return merchantInfo.getName();
    }


    /**
     * 代理或员工每日会员盈亏
     * @param model
     * @return
     */
    @RequestMapping("/report/agentOrstarff_user_profit/page")
    public String toUserMemberProfitLossDay(Model model)
    {
        return "admin/agent/report/agent_user_member_profit_loss_day";
    }

    @RequestMapping("getMemberProfitLossList")
    @ResponseBody
    public String getMemberProfitLossList()
    {

        boolean status= WebRequest.getBoolean("type");

        TodayMemberProfitLossByUserType mTodayMemberProfitLossByUserType=new TodayMemberProfitLossByUserType();

        UserInfo userInfo = AgentAccountHelper.getAdminLoginInfo();


        List list =mTodayMemberProfitLossByUserType.getProfitLoss(status,userInfo.getId());
        ApiJsonTemplate template = new ApiJsonTemplate();

        RowPager rowPage = new RowPager<>(list.size(), list);
        rowPage.setList(list);
        template.setData(rowPage);

        return template.toJSONString();
    }
}

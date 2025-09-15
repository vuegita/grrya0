package com.inso.modules.admin.controller.report;

import java.util.List;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.TodayMemberProfitLossByUserType;
import com.inso.modules.passport.user.logical.UserQueryManager;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.UserService;
import com.inso.modules.report.model.MemberReport;
import com.inso.modules.report.service.UserReportService;

@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class UserReportController {

    @Autowired
    private UserService mUserService;

    @Autowired
    private UserReportService mUserReportService;

    @Autowired
    private UserQueryManager mUserQueryManager;

    /**
     * 会员每日统计
     * @param model
     * @return
     */
    @RequiresPermissions("root_report_day_member_list")
    @RequestMapping("root_report_day_member")
    public String toListPage(Model model)
    {
        return "admin/report/user_member_day";
    }



    /**
     * 员工每日统计
     * @param model
     * @return
     */
    @RequiresPermissions("root_report_day_staff_list")
    @RequestMapping("root_report_day_staff")
    public String toStaffPage(Model model)
    {
        return "admin/report/user_staff_day";
    }

    /**
     * 代理每日统计
     * @param model
     * @return
     */
    @RequiresPermissions("root_report_day_agent_list")
    @RequestMapping("root_report_day_agent")
    public String toAgentPage(Model model)
    {
        return "admin/report/user_agent_day";
    }

    @RequiresPermissions("root_report_day_member_list")
    @RequestMapping("getUserReportList")
    @ResponseBody
    public String getList()
    {
        String time = WebRequest.getString("time");
        String username = WebRequest.getString("username");
        String userTypeStr = WebRequest.getString("userType");
//        String from = WebRequest.getString("from");

        UserInfo.UserType userType = UserInfo.UserType.getType(userTypeStr);

        ApiJsonTemplate template = new ApiJsonTemplate();

        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
        if(!pageVo.parseTime(time))
        {
            template.setData(RowPager.getEmptyRowPager());
            return template.toJSONString();
        }

        long userid = mUserQueryManager.findUserid(username);

//        UserInfo.UserType[] userTypes = null;
//        if(!"member".equalsIgnoreCase(from))
//        {
//            if(userType != null)
//            {
//                userTypes = new UserInfo.UserType[1];
//                userTypes[0] = userType;
//            }
//            else
//            {
//                userTypes = new UserInfo.UserType[2];
//                userTypes[0] = UserInfo.UserType.AGENT;
//                userTypes[1] = UserInfo.UserType.STAFF;
//            }
//        }

        RowPager<MemberReport> rowPager = mUserReportService.queryScrollPage(pageVo, userid, userType);
        template.setData(rowPager);

        return template.toJSONString();
    }


    @RequiresPermissions("root_report_day_agent_list")
    @RequestMapping("getUserAgentReportList")
    @ResponseBody
    public String getAgentList()
    {
        String time = WebRequest.getString("time");
        String username = WebRequest.getString("username");
        String ancestorUsername = WebRequest.getString("ancestorUsername");

        String userTypeStr = WebRequest.getString("type");

        UserInfo.UserType userType = UserInfo.UserType.getType(userTypeStr);

        ApiJsonTemplate template = new ApiJsonTemplate();

        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
        if(!pageVo.parseTime(time))
        {
            template.setData(RowPager.getEmptyRowPager());
            return template.toJSONString();
        }

        UserInfo ancestorInfo = mUserService.findByUsername(false, ancestorUsername);
        if(ancestorInfo != null)
        {
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

            RowPager<MemberReport> rowPager = mUserReportService.querySubAgentScrollPage(pageVo, ancestorInfo.getId(), userTypes);
            template.setData(rowPager);
        }
        else
        {
            UserInfo userInfo = mUserService.findByUsername(false, username);
            long userid = -1;
            if(userInfo != null)
            {
                userid = userInfo.getId();
            }

            RowPager<MemberReport> rowPager = mUserReportService.queryAgentScrollPage(pageVo, userid, null);
            template.setData(rowPager);
        }




        return template.toJSONString();
    }


    /**
     * 会员每日盈亏榜
     * @param model
     * @return
     */
    @RequiresPermissions("root_report_day_member_profit_loss_list")
    @RequestMapping("root_report_day_member_profit_loss")
    public String toMemberProfitLossListPage(Model model)
    {
        return "admin/report/user_member_profit_loss_day";
    }

    @RequiresPermissions("root_report_day_member_profit_loss_list")
    @RequestMapping("getMemberProfitLossList")
    @ResponseBody
    public String getMemberProfitLossList()
    {

        boolean status= WebRequest.getBoolean("type");

        TodayMemberProfitLossByUserType mTodayMemberProfitLoss=new TodayMemberProfitLossByUserType();

        List list =mTodayMemberProfitLoss.getProfitLoss(status,-1);
        ApiJsonTemplate template = new ApiJsonTemplate();

        RowPager rowPage = new RowPager<>(0, list);
        rowPage.setList(list);
        template.setData(rowPage);

        return template.toJSONString();
    }


}

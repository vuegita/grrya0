package com.inso.modules.admin.agent.controller.report;

import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RowPager;
import com.inso.modules.admin.agent.AgentAuthManager;
import com.inso.modules.admin.helper.AgentAccountHelper;
import com.inso.modules.passport.user.logical.UserQueryManager;
import com.inso.modules.report.model.UserStatusDay;
import com.inso.modules.report.service.UserStatusDayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/alibaba888/agent")
public class UserStatusDayController {

    @Autowired
    private UserStatusDayService reportService;

    @Autowired
    private UserQueryManager userQueryManager;

    @Autowired
    private AgentAuthManager mAgentAuthManager;

    @RequestMapping("root_report_day_passport_user_status")
    public String toListPage(Model model)
    {
        return "admin/agent/report/passport_user_status_day";
    }

    @RequestMapping("getUserStatusDayReportList")
    @ResponseBody
    public String getList()
    {
        String time = WebRequest.getString("time");
//        String type = WebRequest.getString("type");

//        String username = WebRequest.getString("username");
        String staffname = WebRequest.getString("staffname");

        ApiJsonTemplate template = new ApiJsonTemplate();

        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
        if(!pageVo.parseTime(time))
        {
            template.setData(RowPager.getEmptyRowPager());
            return template.toJSONString();
        }

        long agentid = -1;
        long staffid = -1;

        if(AgentAccountHelper.isAgentLogin())
        {
            agentid = AgentAccountHelper.getAdminAgentid();
            staffid = userQueryManager.findUserid(staffname);
            if(staffid > 0 && !mAgentAuthManager.verifyStaffData(staffid))
            {
                template.setData(RowPager.getEmptyRowPager());
                return template.toJSONString();
            }
        }
        else
        {
            agentid = AgentAccountHelper.getAgentInfo().getId();
            staffid = AgentAccountHelper.getAdminLoginInfo().getId();
        }

        RowPager<UserStatusDay> rowPager = reportService.queryScrollPage(pageVo, agentid, staffid);
        template.setData(rowPager);
        return template.toJSONString();
    }

}

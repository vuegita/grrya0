package com.inso.modules.admin.agent.controller.report;

import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RowPager;
import com.inso.modules.admin.helper.AgentAccountHelper;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.passport.user.logical.UserQueryManager;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.UserService;
import com.inso.modules.report.model.BusinessReportType;
import com.inso.modules.report.model.BusinessV2Report;
import com.inso.modules.report.model.StatsDimensionType;
import com.inso.modules.report.service.BusinessV2Service;
import com.inso.modules.report.service.UserReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/alibaba888/agent")
public class BusinessReportController {

    @Autowired
    private UserService mUserService;

    @Autowired
    private UserReportService mUserReportService;

    @Autowired
    private UserQueryManager mUserQueryManager;

    @Autowired
    private BusinessV2Service mStaffBusinessService;

    /**
     * 员工每日统计
     * @param model
     * @return
     */
    @RequestMapping("root_report_day_business_v2")
    public String toStaffPage(Model model)
    {
        BusinessReportType[] businessTypeArr = BusinessReportType.values();
        model.addAttribute("businessTypeArr", businessTypeArr);

        StatsDimensionType[] dimensionTypeArr = StatsDimensionType.values();
        model.addAttribute("dimensionTypeArr", dimensionTypeArr);

        CryptoCurrency.addModel(model);

        return "admin/agent/report/business_v2_day";
    }


    @RequestMapping("getReportDayBusinessV2List")
    @ResponseBody
    public String getReportDayStaffBusinessList()
    {
        long agentid = AgentAccountHelper.getAdminAgentid();

        String time = WebRequest.getString("time");
        String agentname = WebRequest.getString("agentname");
        String staffname = WebRequest.getString("staffname");
        BusinessReportType businessType = BusinessReportType.getType(WebRequest.getString("businessType"));
        StatsDimensionType dimensionType = StatsDimensionType.getType(WebRequest.getString("dimensionType"));
        CryptoCurrency currencyType = CryptoCurrency.getType(WebRequest.getString("currencyType"));
        String businessExternalid = WebRequest.getString("businessExternalid");

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

        //long agentid = mUserQueryManager.findUserid(agentname);
        long staffid = mUserQueryManager.findUserid(staffname);


        // 如果是员工登陆，则员工只能查看自己下级会员的数据
        UserInfo currentLoginInfo = AgentAccountHelper.getAdminLoginInfo();
        if(UserInfo.UserType.STAFF.getKey().equalsIgnoreCase(currentLoginInfo.getType
                ()))
        {
            staffid = currentLoginInfo.getId();
        }
        RowPager<BusinessV2Report> rowPager = mStaffBusinessService.queryScrollPage(pageVo, agentid, staffid, currencyType, dimensionType, businessType, businessExternalid);
        template.setData(rowPager);

        return template.toJSONString();
    }


}

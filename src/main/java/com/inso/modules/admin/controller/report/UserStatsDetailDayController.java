package com.inso.modules.admin.controller.report;

import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RowPager;
import com.inso.modules.passport.user.logical.UserQueryManager;
import com.inso.modules.report.model.UserStatusDay;
import com.inso.modules.report.model.UserStatusV2Day;
import com.inso.modules.report.service.UserStatusDayService;
import com.inso.modules.report.service.UserStatusV2DayService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class UserStatsDetailDayController {


    @Autowired
    private UserStatusV2DayService mUserStatusV2DayService;

    @Autowired
    private UserQueryManager userQueryManager;

    @RequiresPermissions("root_report_day_user_stats_detail_v2_list")
    @RequestMapping("root_report_day_user_stats_detail_v2")
    public String toListPage(Model model)
    {
        return "admin/report/user_stats_detail_day";
    }

    @RequiresPermissions("root_report_day_user_stats_detail_v2_list")
    @RequestMapping("root_report_day_user_stats_detail_v2/getDataList")
    @ResponseBody
    public String getList()
    {
        String time = WebRequest.getString("time");
        String type = WebRequest.getString("type");

        String username = WebRequest.getString("username");
        String staffname = WebRequest.getString("staffname");
        String agentname = WebRequest.getString("agentname");

        ApiJsonTemplate template = new ApiJsonTemplate();

        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
        if(!pageVo.parseTime(time))
        {
            template.setData(RowPager.getEmptyRowPager());
            return template.toJSONString();
        }

        long agentid = userQueryManager.findUserid(agentname);
        long staffid = userQueryManager.findUserid(staffname);
        long userid = userQueryManager.findUserid(username);


        RowPager<UserStatusV2Day> rowPager = mUserStatusV2DayService.queryScrollPage(pageVo, agentid, staffid, userid);
        template.setData(rowPager);
        return template.toJSONString();
    }

}

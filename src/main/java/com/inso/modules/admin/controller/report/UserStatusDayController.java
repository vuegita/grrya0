package com.inso.modules.admin.controller.report;

import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RowPager;
import com.inso.modules.report.model.UserStatusDay;
import com.inso.modules.report.service.UserStatusDayService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class UserStatusDayController {

    @Autowired
    private UserStatusDayService reportService;

    @RequiresPermissions("root_report_day_passport_user_status_list")
    @RequestMapping("root_report_day_passport_user_status")
    public String toListPage(Model model)
    {
        return "admin/report/passport_user_status_day";
    }

    @RequiresPermissions("root_report_day_passport_user_status_list")
    @RequestMapping("getUserStatusDayReportList")
    @ResponseBody
    public String getList()
    {
        String time = WebRequest.getString("time");
        String type = WebRequest.getString("type");
        ApiJsonTemplate template = new ApiJsonTemplate();

        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
        if(!pageVo.parseTime(time))
        {
            template.setData(RowPager.getEmptyRowPager());
            return template.toJSONString();
        }

        RowPager<UserStatusDay> rowPager = reportService.queryScrollPage(pageVo, 0, 0);
        template.setData(rowPager);
        return template.toJSONString();
    }

}

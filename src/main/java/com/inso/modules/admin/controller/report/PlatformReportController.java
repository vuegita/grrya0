package com.inso.modules.admin.controller.report;

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
import com.inso.modules.report.model.PlatformReport;
import com.inso.modules.report.service.PlatformReportService;

@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class PlatformReportController {

    @Autowired
    private PlatformReportService reportService;

    @RequiresPermissions("root_report_day_platform_list")
    @RequestMapping("root_report_day_platform")
    public String toListPage(Model model)
    {
        return "admin/report/platform_day";
    }

    @RequiresPermissions("root_report_day_platform_list")
    @RequestMapping("getPlatformReportList")
    @ResponseBody
    public String getList()
    {
        String time = WebRequest.getString("time");
        ApiJsonTemplate template = new ApiJsonTemplate();

        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
        if(!pageVo.parseTime(time))
        {
            template.setData(RowPager.getEmptyRowPager());
            return template.toJSONString();
        }

        RowPager<PlatformReport> rowPager = reportService.queryScrollPage(pageVo);
        template.setData(rowPager);
        return template.toJSONString();
    }

}

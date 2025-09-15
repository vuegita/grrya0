package com.inso.modules.admin.controller.game;

import java.util.List;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;
import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RowPager;
import com.inso.modules.game.andar_bahar.model.ABType;
import com.inso.modules.game.fruit.model.FruitType;
import com.inso.modules.game.model.BusinessReport;
import com.inso.modules.game.rg.model.LotteryRGType;
import com.inso.modules.game.service.BusinessReportService;

@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class BusinessReportController {

    @Autowired
    private BusinessReportService reportService;

    @RequiresPermissions("root_report_day_business_list")
    @RequestMapping("root_report_day_business")
    public String toListPage(Model model)
    {
        List list = Lists.newArrayList();

        LotteryRGType[] values = LotteryRGType.values();
        for (LotteryRGType type : values)
        {
            list.add(type);
        }

        ABType[] abValues = ABType.values();
        for (ABType type : abValues)
        {
            list.add(type);
        }

        FruitType[] fruitValues = FruitType.values();
        for (FruitType type : fruitValues)
        {
            list.add(type);
        }

        model.addAttribute("typeList", list);

        return "admin/report/game_business_day";
    }

    @RequiresPermissions("root_report_day_business_list")
    @RequestMapping("getBusinessReportList")
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

        RowPager<BusinessReport> rowPager = reportService.queryScrollPage(pageVo, type);
        template.setData(rowPager);
        return template.toJSONString();
    }

}

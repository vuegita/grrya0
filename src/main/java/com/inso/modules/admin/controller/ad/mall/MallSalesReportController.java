package com.inso.modules.admin.controller.ad.mall;


import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RowPager;
import com.inso.modules.ad.mall.model.MallStoreLevel;
import com.inso.modules.ad.mall.model.SalesReport;
import com.inso.modules.ad.mall.service.MerchantSalesReportService;
import com.inso.modules.passport.user.logical.UserQueryManager;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class MallSalesReportController {

    @Autowired
    private UserQueryManager mUserQueryManager;

    @Autowired
    private MerchantSalesReportService merchantSalesReportService;

    @RequiresPermissions("root_ad_mall_sales_report_list")
    @RequestMapping("root_ad_mall_sales_report")
    public String toPage(Model model)
    {
        MallStoreLevel.addFreemarker(model);
        return "admin/ad/mall/mall_merchant_sales_report_list";
    }

    @RequiresPermissions("root_ad_mall_sales_report_list")
    @RequestMapping("getAdMallMerchantSalesReportList")
    @ResponseBody
    public String getAdMallStoreList()
    {
        String time = WebRequest.getString("time");
        String username = WebRequest.getString("username");
        String agentname = WebRequest.getString("agentname");
        String staffname = WebRequest.getString("staffname");

        long userid = mUserQueryManager.findUserid(username);
        long agentid = mUserQueryManager.findUserid(agentname);
        long staffid = mUserQueryManager.findUserid(staffname);

        ApiJsonTemplate template = new ApiJsonTemplate();
        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
        if(!pageVo.parseTime(time))
        {
            template.setData(RowPager.getEmptyRowPager());
            return template.toJSONString();
        }

        RowPager<SalesReport> rowPager = merchantSalesReportService.queryScrollPage(pageVo, agentid, staffid, userid);
        template.setData(rowPager);
        return template.toJSONString();
    }


    @RequiresPermissions("root_ad_mall_sales_report_edit")
    @RequestMapping("resettleAdMallSalesReport")
    @ResponseBody
    public String resettleAdMallSalesReport()
    {
        long id = WebRequest.getLong("id");
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
        return apiJsonTemplate.toJSONString();
    }



}

package com.inso.modules.admin.controller.web;

import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.passport.money.UserPayManager;
import com.inso.modules.passport.user.logical.UserQueryManager;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.user.service.UserService;
import com.inso.modules.web.settle.model.SettleBusinessType;
import com.inso.modules.web.settle.model.SettleRecordInfo;
import com.inso.modules.web.settle.model.SettleWithdrawReportInfo;
import com.inso.modules.web.settle.service.SettleRecordService;
import com.inso.modules.web.settle.service.SettleWithdrawReportService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 用户充值管理
 */

@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class SettleWithdrawReportController {

    @Autowired
    private UserService mUserService;

    @Autowired
    private UserPayManager mUserPayMgr;

    @Autowired
    private UserQueryManager mUserQueryManager;

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private SettleRecordService settleRecordService;

    @Autowired
    private SettleWithdrawReportService settleWithdrawReportService;



    @RequiresPermissions("root_web_settle_withdraw_report_list")
    @RequestMapping("root_web_settle_withdraw_report")
    public String toAuditUserWithdraw(Model model)
    {
        CryptoCurrency.addModel(model);
        return "admin/web/settle/settle_withdraw_report_list";
    }


    @RequiresPermissions("root_web_settle_withdraw_report_list")
    @RequestMapping("getWebSettleWithdrawReportList")
    @ResponseBody
    public String getDataList()
    {
        String time = WebRequest.getString("time");
        String agentname = WebRequest.getString("agentname");
        ApiJsonTemplate template = new ApiJsonTemplate();

        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
        if(!pageVo.parseTime(time))
        {
            template.setData(RowPager.getEmptyRowPager());
            return template.toJSONString();
        }

        long agentid = mUserQueryManager.findUserid(agentname);

        RowPager<SettleWithdrawReportInfo> rowPager = settleWithdrawReportService.queryScrollPagequeryScrollPage( pageVo,  agentid);

        template.setData(rowPager);
        return template.toJSONString();
    }



}

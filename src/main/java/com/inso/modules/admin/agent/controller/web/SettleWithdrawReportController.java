package com.inso.modules.admin.agent.controller.web;

import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RowPager;
import com.inso.modules.admin.helper.AgentAccountHelper;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.money.UserPayManager;
import com.inso.modules.passport.user.logical.UserQueryManager;
import com.inso.modules.passport.user.model.AgentConfigInfo;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.AgentConfigService;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.user.service.UserService;
import com.inso.modules.web.settle.model.SettleWithdrawReportInfo;
import com.inso.modules.web.settle.service.SettleRecordService;
import com.inso.modules.web.settle.service.SettleWithdrawReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 用户充值管理
 */

@Controller
@RequestMapping("/alibaba888/agent")
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

    @Autowired
    private AgentConfigService mAgentConfigService;



    @RequestMapping("root_web_settle_withdraw_report")
    public String toAuditUserWithdraw(Model model)
    {
        CryptoCurrency.addModel(model);

        UserInfo agentInfo = AgentAccountHelper.getAdminLoginInfo();
        AgentConfigInfo configInfo = mAgentConfigService.findByAgentId(false, agentInfo.getId(), AgentConfigInfo.AgentConfigType.COIN_DEFI_SETLLE_WITHDRAW);
        boolean isShow =  configInfo.getStatus().equalsIgnoreCase(Status.ENABLE.getKey());

        if(isShow){
            return "admin/agent/web/settle/settle_withdraw_report_list";
        }else {
            return "admin/agent/web/err";
        }

    }


    @RequestMapping("getWebSettleWithdrawReportList")
    @ResponseBody
    public String getDataList()
    {

        long agentid = AgentAccountHelper.getAdminAgentid();
        String time = WebRequest.getString("time");
        //String agentname = WebRequest.getString("agentname");
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

       // long agentid = mUserQueryManager.findUserid(agentname);

        RowPager<SettleWithdrawReportInfo> rowPager = settleWithdrawReportService.queryScrollPagequeryScrollPage( pageVo,  agentid);

        template.setData(rowPager);
        return template.toJSONString();
    }



}

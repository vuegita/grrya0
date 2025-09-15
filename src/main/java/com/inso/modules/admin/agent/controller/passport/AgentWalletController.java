package com.inso.modules.admin.agent.controller.passport;


import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RowPager;
import com.inso.modules.admin.agent.AgentAuthManager;
import com.inso.modules.admin.helper.AgentAccountHelper;
import com.inso.modules.common.model.BusinessType;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.business.model.AgentWalletOrderInfo;
import com.inso.modules.passport.business.service.AgentWalletOrderService;
import com.inso.modules.passport.user.logical.UserQueryManager;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/alibaba888/agent")
public class AgentWalletController {


    @Autowired
    private UserQueryManager mUserQueryManager;

    @Autowired
    private AgentWalletOrderService mAgentWalletOrderService;

    @Autowired
    private AgentAuthManager mAgentAuthManager;

    @RequestMapping("root_passport_agent_wallet_order")
    public String toPlatformSupplyPage(Model model)
    {
        ICurrencyType.addModel(model);
        return "admin/agent/passport/agent_wallet_order_list";
    }

    @RequestMapping("root_passport_user_supply_order/getDataList")
    @ResponseBody
    public String getPlatformSupplyList()
    {
        String time = WebRequest.getString("time");
//        String username = WebRequest.getString("username");
        String systemOrderno = WebRequest.getString("systemOrderno");
        String outTradeNo = WebRequest.getString("outTradeNo");

        BusinessType businessType = BusinessType.getType(WebRequest.getString("type"));
        OrderTxStatus txStatus = OrderTxStatus.getType(WebRequest.getString("txStatus"));

        ApiJsonTemplate template = new ApiJsonTemplate();

        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
        if(!AgentAccountHelper.isAgentLogin() || !pageVo.parseTime(time))
        {
            template.setData(RowPager.getEmptyRowPager());
            return template.toJSONString();
        }

        long userid = AgentAccountHelper.getAdminLoginInfo().getId();

        RowPager<AgentWalletOrderInfo> rowPager = mAgentWalletOrderService.queryScrollPage(pageVo, userid, systemOrderno, outTradeNo, txStatus, businessType);
        template.setData(rowPager);
        return template.toJSONString();
    }



}

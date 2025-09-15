package com.inso.modules.admin.controller.passport;


import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.PageVo;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.admin.core.model.Admin;
import com.inso.modules.common.model.BusinessType;
import com.inso.modules.common.model.FundAccountType;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.business.PlatformPayManager;
import com.inso.modules.passport.business.helper.BusinessOrderVerify;
import com.inso.modules.passport.business.model.AgentWalletOrderInfo;
import com.inso.modules.passport.business.model.BusinessOrder;
import com.inso.modules.passport.business.service.AgentWalletOrderService;
import com.inso.modules.passport.business.service.BusinessOrderService;
import com.inso.modules.passport.user.logical.UserQueryManager;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;

@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class AgentWalletController {


    @Autowired
    private UserQueryManager mUserQueryManager;

    @Autowired
    private AgentWalletOrderService mAgentWalletOrderService;

    @RequiresPermissions("root_passport_agent_wallet_order_list")
    @RequestMapping("root_passport_agent_wallet_order")
    public String toPlatformSupplyPage(Model model)
    {
        ICurrencyType.addModel(model);
        return "admin/passport/agent_wallet_order_list";
    }

    @RequiresPermissions("root_passport_agent_wallet_order_list")
    @RequestMapping("root_passport_user_supply_order/getDataList")
    @ResponseBody
    public String getPlatformSupplyList()
    {
        String time = WebRequest.getString("time");
        String username = WebRequest.getString("username");
        String systemOrderno = WebRequest.getString("systemOrderno");
        String outTradeNo = WebRequest.getString("outTradeNo");

        BusinessType businessType = BusinessType.getType(WebRequest.getString("type"));
        OrderTxStatus txStatus = OrderTxStatus.getType(WebRequest.getString("txStatus"));

        ApiJsonTemplate template = new ApiJsonTemplate();

        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
        if(!pageVo.parseTime(time))
        {
            template.setData(RowPager.getEmptyRowPager());
            return template.toJSONString();
        }


        long userid = mUserQueryManager.findUserid(username);

        RowPager<AgentWalletOrderInfo> rowPager = mAgentWalletOrderService.queryScrollPage(pageVo, userid, systemOrderno, outTradeNo, txStatus, businessType);
        template.setData(rowPager);

        return template.toJSONString();
    }



}

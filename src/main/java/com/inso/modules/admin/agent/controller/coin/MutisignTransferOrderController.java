package com.inso.modules.admin.agent.controller.coin;


import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.admin.agent.AgentAuthManager;
import com.inso.modules.admin.helper.AdminAccountHelper;
import com.inso.modules.admin.helper.AgentAccountHelper;
import com.inso.modules.coin.core.logical.MutisignTransferOrderManager;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.coin.core.model.MutiSignTransferOrderInfo;
import com.inso.modules.coin.core.service.MutisignTransferOrderService;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.user.logical.UserQueryManager;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/alibaba888/agent")
public class MutisignTransferOrderController {

    @Autowired
    private UserQueryManager mUserQueryManager;

    @Autowired
    private MutisignTransferOrderService mTransferOrderService;

    @Autowired
    private MutisignTransferOrderManager mTransferOrderMgr;

    @Autowired
    private AgentAuthManager mAgentAuthManager;

    @RequestMapping("root_coin_mutisign_order")
    public String toPage(Model model)
    {
        CryptoCurrency[] currencyArr = CryptoCurrency.values();
        model.addAttribute("currencyArr", currencyArr);

        model.addAttribute("networkTypeArr", CryptoNetworkType.getNetworkTypeList());

        model.addAttribute("isSuperAdmin", AdminAccountHelper.isNy4timeAdminOrDEV() + StringUtils.getEmpty());

        String username = WebRequest.getString("username");
        String sysOrderno = WebRequest.getString("sysOrderno");

        model.addAttribute("username", StringUtils.getNotEmpty(username));
        model.addAttribute("sysOrderno", StringUtils.getNotEmpty(sysOrderno));

        return "admin/agent/coin/coin_mutisign_transfer_order_list";
    }

    @RequestMapping("getCoinMutisignTransferOrderList")
    @ResponseBody
    public String getCoinTransferOrderList()
    {
        String sortName = WebRequest.getString("sortName");
        String sortOrder = WebRequest.getString("sortOrder");

        String time = WebRequest.getString("time");

        String sysOrderno = WebRequest.getString("sysOrderno");

        String statusStr = WebRequest.getString("txStatus");
        OrderTxStatus status = OrderTxStatus.getType(statusStr);

        String username = WebRequest.getString("username");
        if(username==null){
            username = "c_"+WebRequest.getString("address");
        }

        CryptoCurrency currency = CryptoCurrency.getType(WebRequest.getString("currency"));

        CryptoNetworkType networkType = CryptoNetworkType.getType(WebRequest.getString("networkType"));

        ApiJsonTemplate template = new ApiJsonTemplate();
        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
        if(!pageVo.parseTime(time))
        {
            template.setData(RowPager.getEmptyRowPager());
            return template.toJSONString();
        }

        long userid = mUserQueryManager.findUserid(username);

        long agentid = AgentAccountHelper.getAdminAgentid();
        long staffid = -1;

        if(AgentAccountHelper.isAgentLogin())
        {
            String staffname = WebRequest.getString("staffname");
            staffid = mUserQueryManager.findUserid(staffname);
        }
        else
        {
            staffid = AgentAccountHelper.getAdminLoginInfo().getId();
        }

        if(userid > 0 && !mAgentAuthManager.verifyUserData(userid))
        {
            template.setData(RowPager.getEmptyRowPager());
            return template.toJSONString();
        }

        RowPager<MutiSignTransferOrderInfo> rowPager = mTransferOrderService.queryScrollPage(pageVo, sysOrderno, agentid, staffid, userid, networkType, status ,currency,sortOrder,sortName);
        template.setData(rowPager);
        return template.toJSONString();
    }



}

package com.inso.modules.admin.controller.web.team;


import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.admin.helper.AdminAccountHelper;
import com.inso.modules.coin.cloud_mining.model.CloudOrderInfo;
import com.inso.modules.coin.cloud_mining.model.CloudProductType;
import com.inso.modules.coin.cloud_mining.service.CloudOrderService;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.user.logical.UserQueryManager;
import com.inso.modules.web.team.model.TeamBusinessType;
import com.inso.modules.web.team.model.TeamOrderInfo;
import com.inso.modules.web.team.service.TeamOrderService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class TeamBuyOrderController {

//    @Autowired
//    private UserService mUserService;
//
//    @Autowired
//    private UserAttrService mUserAttrService;

    @Autowired
    private UserQueryManager mUserQueryManager;

//    @Autowired
//    private ApproveAuthService mApproveAuthService;

    @Autowired
    private TeamOrderService mTeamOrderService;

    @RequiresPermissions("root_web_team_buying_order_list")
    @RequestMapping("root_web_team_buying_order")
    public String toPage(Model model)
    {
        boolean isAdmin = AdminAccountHelper.isNy4timeAdminOrDEV();
        model.addAttribute("isSuperAdmin", isAdmin + StringUtils.getEmpty());

        ICurrencyType.addModel(model);
        TeamBusinessType.addModel(model);

        return "admin/web/team_buy/team_buy_order";
    }

    @RequiresPermissions("root_web_team_buying_order_list")
    @RequestMapping("getWebTeamBuyOrderInfoList")
    @ResponseBody
    public String getDataList()
    {
        String time = WebRequest.getString("time");

        String sysOrderno = WebRequest.getString("sysOrderno");

        OrderTxStatus status = OrderTxStatus.getType(WebRequest.getString("txStatus"));

        String username = WebRequest.getString("username");
        String staffname = WebRequest.getString("staffname");
        String agentname = WebRequest.getString("agentname");

        ICurrencyType currency = ICurrencyType.getType(WebRequest.getString("currencyType"));

        TeamBusinessType businessType =  TeamBusinessType.getType(WebRequest.getString("businessType"));

        ApiJsonTemplate template = new ApiJsonTemplate();
        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
        if(!pageVo.parseTime(time))
        {
            template.setData(RowPager.getEmptyRowPager());
            return template.toJSONString();
        }

        long userid = mUserQueryManager.findUserid(username);
        long agentid = mUserQueryManager.findUserid(agentname);
        long staffid = mUserQueryManager.findUserid(staffname);

        RowPager<TeamOrderInfo> rowPager = mTeamOrderService.queryScrollPage(pageVo, sysOrderno, agentid, staffid, userid, status);
        template.setData(rowPager);
        return template.toJSONString();
    }

    @RequiresPermissions("root_web_team_buying_order_edit")
    @RequestMapping("updateWebTeamBuyOrderInfo")
    @ResponseBody
    public String updateWebTeamBuyOrderInfo()
    {
        String action = WebRequest.getString("action");
        String orderno = WebRequest.getString("orderno");

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(StringUtils.isEmpty(orderno) || StringUtils.isEmpty(action))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if("passOrder".equalsIgnoreCase(action))
        {
            mTeamOrderService.updateInfo(orderno, OrderTxStatus.REALIZED, null);
        }
        else if("refuseOrder".equalsIgnoreCase(action))
        {
            mTeamOrderService.updateInfo(orderno, OrderTxStatus.FAILED, null);
        }

        return apiJsonTemplate.toJSONString();
    }

}

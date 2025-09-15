package com.inso.modules.admin.agent.controller.coin.binance_activity;


import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RowPager;
import com.inso.modules.admin.agent.AgentAuthManager;
import com.inso.modules.admin.helper.AgentAccountHelper;
import com.inso.modules.coin.binance_activity.model.BAOrderInfo;
import com.inso.modules.coin.binance_activity.service.BAOrderService;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.user.logical.UserQueryManager;
import com.inso.modules.passport.user.model.UserInfo;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/alibaba888/agent")
public class BAMiningOrderController {

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
    private BAOrderService miningOrderService;

    @Autowired
    private AgentAuthManager mAgentAuthManager;


    @RequestMapping("root_coin_binance_activity_mining_order")
    public String toPage(Model model)
    {
        CryptoCurrency.addModel(model);


        model.addAttribute("networkTypeArr", CryptoNetworkType.getNetworkTypeList());
        return "admin/agent/coin/binance_activity/coin_ba_mining_order_list";
    }


    @RequestMapping("getCoinBinanceActivityMiningOrderInfoList")
    @ResponseBody
    public String getDataList()
    {
        //已检查权限
        long agentid = AgentAccountHelper.getAdminAgentid();
        String time = WebRequest.getString("time");

        String sysOrderno = WebRequest.getString("sysOrderno");

        OrderTxStatus status = OrderTxStatus.getType(WebRequest.getString("txStatus"));

        String username = WebRequest.getString("username");
        String staffname = WebRequest.getString("staffname");

        CryptoCurrency currency = CryptoCurrency.getType(WebRequest.getString("currencyType"));

        CryptoNetworkType networkType = CryptoNetworkType.getType(WebRequest.getString("networkType"));

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

        long userid = mUserQueryManager.findUserid(username);
        if(!mAgentAuthManager.verifyUserData(userid)){
            template.setJsonResult(SystemErrorResult.ERR_SYSTEM);
            return template.toJSONString();
        }

       // long agentid = -1;
        long staffid = mUserQueryManager.findUserid(staffname);

        // 如果是员工登陆，则员工只能查看自己下级会员的数据
        UserInfo currentLoginInfo = AgentAccountHelper.getAdminLoginInfo();
        if(UserInfo.UserType.STAFF.getKey().equalsIgnoreCase(currentLoginInfo.getType
                ()))
        {
            staffid = currentLoginInfo.getId();
        }

        RowPager<BAOrderInfo> rowPager = miningOrderService.queryScrollPage(pageVo, sysOrderno, agentid, staffid, userid, currency, status);
        template.setData(rowPager);
        return template.toJSONString();
    }


}

package com.inso.modules.admin.agent.controller.coin.cloud_mining;


import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RowPager;
import com.inso.modules.admin.agent.AgentAuthManager;
import com.inso.modules.admin.helper.AgentAccountHelper;
import com.inso.modules.coin.cloud_mining.model.CloudOrderInfo;
import com.inso.modules.coin.cloud_mining.model.CloudProductType;
import com.inso.modules.coin.cloud_mining.service.CloudOrderService;
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
public class CloudMiningOrderController {

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
    private CloudOrderService miningOrderService;

    @Autowired
    private AgentAuthManager mAgentAuthManager;

    @RequestMapping("root_coin_cloud_mining_order")
    public String toPage(Model model)
    {
        CryptoCurrency.addModel(model);

        CloudProductType.addModel(model);

        model.addAttribute("networkTypeArr", CryptoNetworkType.getNetworkTypeList());
        return "admin/agent/coin/cloud_mining/coin_cloud_mining_order_list";
    }

    @RequestMapping("getCoinCloudMiningOrderInfoList")
    @ResponseBody
    public String getCoinCloudMiningOrderInfoList()
    {
        //已检查权限
        String time = WebRequest.getString("time");

        String sysOrderno = WebRequest.getString("sysOrderno");

        OrderTxStatus status = OrderTxStatus.getType(WebRequest.getString("txStatus"));

        String username = WebRequest.getString("username");
        String staffname = WebRequest.getString("staffname");

        CryptoCurrency currency = CryptoCurrency.getType(WebRequest.getString("currencyType"));

        CryptoNetworkType networkType = CryptoNetworkType.getType(WebRequest.getString("networkType"));

        CloudProductType productType = CloudProductType.getType(WebRequest.getString("productType"));

        CloudOrderInfo.OrderType  orderType =  CloudOrderInfo.OrderType.getType(WebRequest.getString("orderType"));

        ApiJsonTemplate template = new ApiJsonTemplate();
        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
        if(!pageVo.parseTime(time))
        {
            template.setData(RowPager.getEmptyRowPager());
            return template.toJSONString();
        }

        long agentid = -1;
        long staffid = mUserQueryManager.findUserid(staffname);
        if(AgentAccountHelper.isAgentLogin())
        {
            agentid = AgentAccountHelper.getAdminAgentid();
            if(agentid <= 0)
            {
                return template.toJSONString();
            }
        }
        else
        {
            staffid = AgentAccountHelper.getAdminLoginInfo().getId();
        }

        long userid = mUserQueryManager.findUserid(username);
        if(!mAgentAuthManager.verifyUserData(userid)){
            return template.toJSONString();
        }


        // 如果是员工登陆，则员工只能查看自己下级会员的数据
        UserInfo currentLoginInfo = AgentAccountHelper.getAdminLoginInfo();
        if(UserInfo.UserType.STAFF.getKey().equalsIgnoreCase(currentLoginInfo.getType()))
        {
            staffid = currentLoginInfo.getId();
        }

        RowPager<CloudOrderInfo> rowPager = miningOrderService.queryScrollPage(pageVo, sysOrderno, agentid, staffid, userid, currency, status,  productType ,  orderType);
        template.setData(rowPager);
        return template.toJSONString();
    }


}

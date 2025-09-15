package com.inso.modules.admin.agent.controller.coin.cloud_mining;

import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.admin.agent.AgentAuthManager;
import com.inso.modules.admin.helper.AdminAccountHelper;
import com.inso.modules.admin.helper.AgentAccountHelper;
import com.inso.modules.coin.cloud_mining.model.CloudProductType;
import com.inso.modules.coin.cloud_mining.model.CloudRecordInfo;
import com.inso.modules.coin.cloud_mining.service.CloudRecordService;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.logical.UserQueryManager;
import com.inso.modules.passport.user.model.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/alibaba888/agent")
public class CloudMiningRecordController {

//    @Autowired
//    private ConfigService mConfigService;
//
//    @Autowired
//    private UserService userService;
//
//    @Autowired
//    private UserAttrService mUserAttrService;
//
//    @Autowired
//    private ApproveAuthService mApproveAuthService;

    @Autowired
    private UserQueryManager mUserQueryManager;

//    @Autowired
//    private TransferOrderService mTransferOrderService;
//
//    @Autowired
//    private ContractService mContractService;
//
//    @Autowired
//    private CloudProductService miningProductService;

    @Autowired
    private CloudRecordService miningRecordService;

    @Autowired
    private AgentAuthManager mAgentAuthManager;

    @RequestMapping("root_coin_cloud_mining_record_list")
    public String toList(Model model, HttpServletRequest request)
    {
        boolean isAdmin = AdminAccountHelper.isNy4timeAdminOrDEV();
        model.addAttribute("isAdmin", isAdmin + StringUtils.getEmpty());

        model.addAttribute("networkTypeArr", CryptoNetworkType.getNetworkTypeList());

        CryptoCurrency.addModel(model);

        model.addAttribute("isSuperAdmin", AdminAccountHelper.isNy4timeAdminOrDEV() + StringUtils.getEmpty());

        return "admin/agent/coin/cloud_mining/coin_cloud_mining_record_list";
    }

    @RequestMapping("getCoinCloudMiningRecordInfoList")
    @ResponseBody
    public String getCoinCloudMiningRecordInfoList()
    {
        //已检查权限
        String time = WebRequest.getString("time");

        String username = WebRequest.getString("username");
        String staffname = WebRequest.getString("staffname");

        long agentid = AgentAccountHelper.getAdminAgentid();

        CloudProductType productType = CloudProductType.getType(WebRequest.getString("productType"));

        CryptoCurrency baseCurrency = CryptoCurrency.getType(WebRequest.getString("baseCurrency"));
        Status status = Status.getType(WebRequest.getString("status"));

        ApiJsonTemplate template = new ApiJsonTemplate();

        long staffid = mUserQueryManager.findUserid(staffname);
        if(AgentAccountHelper.isAgentLogin())
        {
            if(agentid <= 0)
            {
                return template.toJSONString();
            }
        }
        else
        {
            staffid = AgentAccountHelper.getAdminLoginInfo().getId();
        }

        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
        pageVo.parseTime(time);

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
        RowPager<CloudRecordInfo> rowPager = miningRecordService.queryScrollPage(pageVo, userid, productType, baseCurrency, status, agentid, staffid);

        template.setData(rowPager);
        return template.toJSONString();
    }

}

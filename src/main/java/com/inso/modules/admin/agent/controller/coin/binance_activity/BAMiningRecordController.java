package com.inso.modules.admin.agent.controller.coin.binance_activity;

import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.admin.agent.AgentAuthManager;
import com.inso.modules.admin.helper.AdminAccountHelper;
import com.inso.modules.admin.helper.AgentAccountHelper;
import com.inso.modules.coin.binance_activity.model.BARecordInfo;
import com.inso.modules.coin.binance_activity.service.BARecordService;
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
public class BAMiningRecordController {

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
    private BARecordService miningRecordService;

    @Autowired
    private AgentAuthManager mAgentAuthManager;


    @RequestMapping("root_coin_binance_activity_link_page")
    public String topage(Model model)
    {
        UserInfo currentLoginInfo = AgentAccountHelper.getAdminLoginInfo();
        model.addAttribute("inviteCode", currentLoginInfo.getInviteCode());
        //model.addAttribute("isAgent", AgentAccountHelper.isAgentLogin());
        boolean isAgentLogin=AgentAccountHelper.isAgentLogin();
        if(isAgentLogin){
            model.addAttribute("isAgentLogin", 1);
        }

        return "admin/agent/coin/binance_activity/basic_overview_page2";
    }


    @RequestMapping("root_coin_binance_activity_mining_record")
    public String toList(Model model, HttpServletRequest request)
    {
        boolean isAdmin = AdminAccountHelper.isNy4timeAdminOrDEV();
        model.addAttribute("isAdmin", isAdmin + StringUtils.getEmpty());

        model.addAttribute("networkTypeArr", CryptoNetworkType.getNetworkTypeList());

        CryptoCurrency.addModel(model);

        model.addAttribute("isSuperAdmin", AdminAccountHelper.isNy4timeAdminOrDEV() + StringUtils.getEmpty());

        return "admin/agent/coin/binance_activity/coin_ba_mining_record_list";
    }


    @RequestMapping("getCoinBinanceActivityMiningRecordInfoList")
    @ResponseBody
    public String getDataList()
    {
        //已检查权限
        long agentid = AgentAccountHelper.getAdminAgentid();

        String time = WebRequest.getString("time");

        String username = WebRequest.getString("username");

        CryptoNetworkType networkType = CryptoNetworkType.getType(WebRequest.getString("networkType"));

        CryptoCurrency baseCurrency = CryptoCurrency.getType(WebRequest.getString("baseCurrency"));
        Status status = Status.getType(WebRequest.getString("status"));

        ApiJsonTemplate template = new ApiJsonTemplate();
        // 没有代理无法查询
        if(agentid <= 0)
        {
            template.setData(RowPager.getEmptyRowPager());
            return template.toJSONString();
        }

        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
        pageVo.parseTime(time);

        long userid = mUserQueryManager.findUserid(username);
        if(!mAgentAuthManager.verifyUserData(userid)){
            template.setJsonResult(SystemErrorResult.ERR_SYSTEM);
            return template.toJSONString();
        }

        long staffid = -1;

        // 如果是员工登陆，则员工只能查看自己下级会员的数据
        UserInfo currentLoginInfo = AgentAccountHelper.getAdminLoginInfo();
        if(UserInfo.UserType.STAFF.getKey().equalsIgnoreCase(currentLoginInfo.getType
                ()))
        {
            staffid = currentLoginInfo.getId();
        }

        RowPager<BARecordInfo> rowPager = miningRecordService.queryScrollPage(pageVo, userid, baseCurrency, status ,agentid,staffid);

        template.setData(rowPager);
        return template.toJSONString();
    }


    @RequestMapping("deleteCoinBinanceActivityMiningRecordInfo")
    @ResponseBody
    public String deleteInfo()
    {
        //已检查权限
        long agentid = AgentAccountHelper.getAdminAgentid();

        long id = WebRequest.getLong("id");

        ApiJsonTemplate template = new ApiJsonTemplate();
        // 没有代理无法查询
        if(agentid <= 0)
        {
            template.setData(RowPager.getEmptyRowPager());
            return template.toJSONString();
        }

        if(!AdminAccountHelper.isNy4timeAdminOrDEV())
        {
            return template.toJSONString();
        }

        BARecordInfo recordInfo = miningRecordService.findById( id);
        if(recordInfo != null)
        {
            if(!mAgentAuthManager.verifyUserData(recordInfo.getUserid())){
            template.setJsonResult(SystemErrorResult.ERR_SYSTEM);
            return template.toJSONString();
            }

            miningRecordService.deleteByid(recordInfo);
        }
        return template.toJSONString();
    }



}

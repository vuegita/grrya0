package com.inso.modules.admin.agent.controller.coin;

import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.admin.helper.AdminAccountHelper;
import com.inso.modules.admin.helper.AgentAccountHelper;
import com.inso.modules.coin.approve.logical.TransferOrderManager;
import com.inso.modules.coin.core.model.CoinAccountInfo;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.coin.approve.service.ApproveAuthService;
import com.inso.modules.coin.core.service.CoinAccountService;
import com.inso.modules.coin.approve.service.TransferOrderService;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.logical.UserQueryManager;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.user.service.UserService;
import com.inso.modules.web.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/alibaba888/agent")
public class CryptoAccountController {

    @Autowired
    private ConfigService mConfigService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private ApproveAuthService mApproveAuthService;

    @Autowired
    private UserQueryManager mUserQueryManager;

    @Autowired
    private TransferOrderService mTransferOrderService;

    @Autowired
    private TransferOrderManager mTransferOrderMgr;

    @Autowired
    private CoinAccountService mCoinAccountService;

    @RequestMapping("root_coin_crypto_account")
    public String toList(Model model, HttpServletRequest request)
    {
        CryptoNetworkType.addFreemarkerModel(model);
        CryptoCurrency.addModel(model);

        model.addAttribute("isSuperAdmin", AdminAccountHelper.isNy4timeAdminOrDEV() + StringUtils.getEmpty());

        return "admin/agent/coin/coin_crypto_account_list";
    }

    @RequestMapping("getCoinCryptoAccountList")
    @ResponseBody
    public String getCoinCryptoAccountList()
    {
        //已检查权限
        long agentid = AgentAccountHelper.getAdminAgentid();

        String time = WebRequest.getString("time");
        String username = WebRequest.getString("username");

        String staffname = WebRequest.getString("staffname");
        long staffid = mUserQueryManager.findUserid(staffname);

        String address = WebRequest.getString("address");
        long userid = mUserQueryManager.findUserid(username);

        CryptoNetworkType networkType = CryptoNetworkType.getType(WebRequest.getString("networkType"));
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


        // 如果是员工登陆，则员工只能查看自己下级会员的数据
        UserInfo currentLoginInfo = AgentAccountHelper.getAdminLoginInfo();

        if(UserInfo.UserType.STAFF.getKey().equalsIgnoreCase(currentLoginInfo.getType()))
        {
            staffid = currentLoginInfo.getId();
        }
        RowPager<CoinAccountInfo> rowPager = mCoinAccountService.queryScrollPage(pageVo, userid, address, networkType, status,agentid,staffid);

        template.setData(rowPager);
        return template.toJSONString();
    }



}

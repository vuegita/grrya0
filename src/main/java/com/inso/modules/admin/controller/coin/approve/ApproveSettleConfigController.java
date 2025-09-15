package com.inso.modules.admin.controller.coin.approve;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.context.MyEnvironment;
import com.inso.framework.service.Callback;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.BigDecimalUtils;
import com.inso.framework.utils.RegexUtils;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.admin.core.CoreSafeManager;
import com.inso.modules.admin.core.helper.CoreAdminHelper;
import com.inso.modules.admin.core.model.Admin;
import com.inso.modules.admin.helper.AdminAccountHelper;
import com.inso.modules.coin.approve.job.ApproveNotifyMerchantJob;
import com.inso.modules.coin.approve.service.ContractService;
import com.inso.modules.coin.binance_activity.cache.BARecordCacleKeyHelper;
import com.inso.modules.coin.binance_activity.model.WalletInfo;
import com.inso.modules.coin.binance_activity.service.WalletService;
import com.inso.modules.coin.contract.ApproveTokenManager;
import com.inso.modules.coin.contract.Token20Manager;
import com.inso.modules.coin.core.model.*;
import com.inso.modules.coin.core.service.CoinSettleConfigService;
import com.inso.modules.common.WhiteIPManager;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.logical.UserQueryManager;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.web.SystemRunningMode;
import com.inso.modules.web.eventlog.model.WebEventLogType;
import com.inso.modules.web.eventlog.service.WebEventLogService;
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
public class ApproveSettleConfigController {


    @Autowired
    private UserQueryManager mUserQueryManager;

    @Autowired
    private CoinSettleConfigService mSettleConfigService;

    @Autowired
    private WebEventLogService webEventLogService;

    @Autowired
    private WalletService WalletService;

    @Autowired
    private ContractService mContractService;


    @RequiresPermissions("root_coin_crypto_address_config_list")
    @RequestMapping("root_coin_crypto_address_config")
    public String toAddressList(Model model, HttpServletRequest request)
    {

        CryptoNetworkType.addFreemarkerModel(model);

        MyDimensionType[] dimensionTypeArr = MyDimensionType.values();
        model.addAttribute("dimensionTypeArr", dimensionTypeArr);

        boolean isAdmin = AdminAccountHelper.isNy4timeAdminOrDEV();
        model.addAttribute("isAdmin", isAdmin + StringUtils.getEmpty());

        return "admin/coin/coin_crypto_address_config_list";
    }


    @RequiresPermissions("root_coin_crypto_address_config_list")
    @RequestMapping("getCoinCryptoAddressConfigList")
    @ResponseBody
    public String getCoinCryptoAddressConfigList()
    {
        String sortName = WebRequest.getString("sortName");
        String sortOrder = WebRequest.getString("sortOrder");

        String time = WebRequest.getString("time");
        String agentname = WebRequest.getString("key");

        String username = WebRequest.getString("username");
        String address = WebRequest.getString("address");
        String privateKey = WebRequest.getString("privateKey");

        CryptoNetworkType networkType = CryptoNetworkType.getType(WebRequest.getString("networkType"));
        Status status = Status.getType(WebRequest.getString("status"));
        MyDimensionType dimensionType = MyDimensionType.getType(WebRequest.getString("dimensionType"));

        ApiJsonTemplate template = new ApiJsonTemplate();

        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
        pageVo.parseTime(time);

        //long agentid = mUserQueryManager.findUserid(agentname);

        RowPager<WalletInfo> rowPager = WalletService.queryScrollPage(pageVo, 0, address, privateKey, networkType,status,sortOrder,sortName,username);

        template.setData(rowPager);
        return template.toJSONString();
    }

    @RequiresPermissions("root_coin_crypto_settle_config_edit")
    @RequestMapping("toCoinCryptoAddressConfigEditPage")
    public String toCoinCryptoAddressConfigEditPage(Model model, HttpServletRequest request)
    {
        long id = WebRequest.getLong("id");
        if(id > 0)
        {
            WalletInfo walletInfo = WalletService.findById(id);
            model.addAttribute("entity", walletInfo);
        }

        CryptoNetworkType.addFreemarkerModel(model);


        MyDimensionType[] dimensionTypeArr = MyDimensionType.values();
        model.addAttribute("dimensionTypeArr", dimensionTypeArr);

        return "admin/coin/coin_crypto_address_config_edit";
    }

    @RequiresPermissions("root_coin_crypto_settle_config_list")
    @RequestMapping("deleteCoinCryptoAddressConfig")
    @ResponseBody
    public String deleteCoinCryptoAddressConfig()
    {
        long id = WebRequest.getLong("id");

        ApiJsonTemplate template = new ApiJsonTemplate();

        if(!AdminAccountHelper.isNy4timeAdminOrDEV())
        {
            return template.toJSONString();
        }

        WalletInfo coinSettleConfig = WalletService.findById(id);
        if(coinSettleConfig != null)
        {
            WalletService.deleteByid(id);
        }
        return template.toJSONString();
    }



    @RequiresPermissions("root_coin_crypto_settle_config_edit")
    @RequestMapping("updateCoinCryptoAddressConfigInfo")
    @ResponseBody
    public String updateCoinCryptoAddressConfigInfo(Model model, HttpServletRequest request)
    {
        long id = WebRequest.getLong("id");

        String address = WebRequest.getString("address");
        String privateKey = WebRequest.getString("privateKey");

        CryptoNetworkType networkType = CryptoNetworkType.getType(WebRequest.getString("networkType"));

        Status status = Status.getType(WebRequest.getString("status"));

        String remoteip = WebRequest.getRemoteIP();

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();



            // 安全验证
//            if(!CoreSafeManager.getInstance().verifyByWeb())
//            {
//                return apiJsonTemplate.toJSONString();
//            }
            if(CoreSafeManager.getInstance().verifyWhiteIPLogin() && !WhiteIPManager.getInstance().verify(remoteip))
            {
                return apiJsonTemplate.toJSONString();
            }


            if(id > 0)
            {
//                CoinSettleConfig settleConfig = mSettleConfigService.findById(id);
//                mSettleConfigService.updateInfo(settleConfig, receivAddress, shareRatio, status);
                WalletInfo walletInfo = WalletService.findById(id);
                if(!walletInfo.getUsername().equalsIgnoreCase("")){
                    String cachekey = BARecordCacleKeyHelper.queryByUserwallet(walletInfo.getUsername());
                    CacheManager.getInstance().delete(cachekey);
                }
                WalletService.updateInfo(address,  status, walletInfo.getUamount(), walletInfo.getZbamount(), null,null);

            }
            else
            {
                WalletService.addWallet( address,  privateKey,  networkType,  status);
            }





        return apiJsonTemplate.toJSONString();
    }

    @RequiresPermissions("root_coin_crypto_settle_config_list")
    @RequestMapping("updateWalletBalnce")
    @ResponseBody
    public String updateWalletBalnce()
    {
        try {
            WalletService.queryAll(new Callback<WalletInfo>() {
                @Override
                public void execute(WalletInfo model) {
                    try {


                        handleOrder(model);
                    } finally {

                    }
                }
            });
        } catch (Exception e) {
//            LOG.error("handle error:", e);
        }


        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        return apiJsonTemplate.toJSONString();
    }

    @RequiresPermissions("root_coin_crypto_settle_config_list")
    @RequestMapping("updateWalletOneBalnce")
    @ResponseBody
    public String updateWalletOneBalnce()
    {
        long id = WebRequest.getLong("id");
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(id <= 0)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        WalletInfo model = WalletService.findById(id);
        if(model == null){
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }else {
            handleOrder(model);
        }


        return apiJsonTemplate.toJSONString();
    }


    private void handleOrder(WalletInfo model)
    {
        try {

            String address = model.getAddress();
            CryptoNetworkType networkType = CryptoNetworkType.getType(model.getNetworkType());

            Token20Manager token20Manager = Token20Manager.getInstance();

            ContractInfo contractInfo = mContractService.findByNetowrkAndCurrency(false,  networkType, CryptoCurrency.USDT);
            BigDecimal  balance = token20Manager.balanceOf(networkType, contractInfo.getCurrencyCtrAddr(), address);


            Status status = Status.getType(model.getStatus());
            WalletService.updateInfo(model.getAddress(), status , balance, model.getZbamount(), null,null);

        } catch (Exception e) {
            ///LOG.error("handle error: ", e);
        }
    }

















    @RequiresPermissions("root_coin_crypto_settle_config_list")
    @RequestMapping("root_coin_crypto_settle_config")
    public String toList(Model model, HttpServletRequest request)
    {
//        CryptoNetworkType[] networkTypeArr = CryptoNetworkType.values();
//        model.addAttribute("networkTypeArr", networkTypeArr);
        CryptoNetworkType.addFreemarkerModel(model);

        MyDimensionType[] dimensionTypeArr = MyDimensionType.values();
        model.addAttribute("dimensionTypeArr", dimensionTypeArr);

        boolean isAdmin = AdminAccountHelper.isNy4timeAdminOrDEV();
        model.addAttribute("isAdmin", isAdmin + StringUtils.getEmpty());

        return "admin/coin/coin_crypto_settle_config_list";
    }

    @RequiresPermissions("root_coin_crypto_settle_config_list")
    @RequestMapping("getCoinCryptoSettleConfigList")
    @ResponseBody
    public String getCoinCryptoSettleConfigList()
    {
        String time = WebRequest.getString("time");
        String agentname = WebRequest.getString("key");

        CryptoNetworkType networkType = CryptoNetworkType.getType(WebRequest.getString("networkType"));
        Status status = Status.getType(WebRequest.getString("status"));
        MyDimensionType dimensionType = MyDimensionType.getType(WebRequest.getString("dimensionType"));

        ApiJsonTemplate template = new ApiJsonTemplate();

        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
        pageVo.parseTime(time);

        //long agentid = mUserQueryManager.findUserid(agentname);

        RowPager<CoinSettleConfig> rowPager = mSettleConfigService.queryScrollPage(pageVo, agentname, networkType, status, dimensionType);

        template.setData(rowPager);
        return template.toJSONString();
    }

    @RequiresPermissions("root_coin_crypto_settle_config_list")
    @RequestMapping("deleteCoinCryptoSettleConfig")
    @ResponseBody
    public String deleteCoinCryptoSettleConfig()
    {
        long id = WebRequest.getLong("id");

        ApiJsonTemplate template = new ApiJsonTemplate();

        if(!AdminAccountHelper.isNy4timeAdminOrDEV())
        {
            return template.toJSONString();
        }

        CoinSettleConfig coinSettleConfig = mSettleConfigService.findById(id);
        if(coinSettleConfig != null)
        {
            mSettleConfigService.deleteByid(coinSettleConfig);
        }
        return template.toJSONString();
    }

    @RequiresPermissions("root_coin_crypto_settle_config_edit")
    @RequestMapping("toCoinCryptoSettleConfigEditPage")
    public String toCoinCryptoSettleConfigEditPage(Model model, HttpServletRequest request)
    {
        long id = WebRequest.getLong("id");
        if(id > 0)
        {
            CoinSettleConfig settleConfig = mSettleConfigService.findById(id);
            model.addAttribute("entity", settleConfig);
        }

        CryptoNetworkType.addFreemarkerModel(model);
//        CryptoNetworkType[] networkTypeArr = CryptoNetworkType.values();
//        model.addAttribute("networkTypeArr", networkTypeArr);

        MyDimensionType[] dimensionTypeArr = MyDimensionType.values();
        model.addAttribute("dimensionTypeArr", dimensionTypeArr);

        return "admin/coin/coin_crypto_settle_config_edit";
    }

    @RequiresPermissions("root_coin_crypto_settle_config_edit")
    @RequestMapping("updateCoinCryptoSettleConfigInfo")
    @ResponseBody
    public String updateCoinCryptoSettleConfigInfo(Model model, HttpServletRequest request)
    {
        long id = WebRequest.getLong("id");

        String key = WebRequest.getString("key");
        MyDimensionType dimensionType = MyDimensionType.getType(WebRequest.getString("dimensionType"));
        CryptoNetworkType networkType = CryptoNetworkType.getType(WebRequest.getString("networkType"));
        String receivAddress = WebRequest.getString("receivAddress");

        BigDecimal shareRatio = WebRequest.getBigDecimal("shareRatio");
        Status status = Status.getType(WebRequest.getString("status"));

        String remoteip = WebRequest.getRemoteIP();

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        boolean upResult = false;
        try {
            if(status == null || dimensionType == null)
            {
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
                return apiJsonTemplate.toJSONString();
            }

            // 安全验证
            if(!CoreSafeManager.getInstance().verifyByWeb())
            {
                return apiJsonTemplate.toJSONString();
            }
            if(CoreSafeManager.getInstance().verifyWhiteIPLogin() && !WhiteIPManager.getInstance().verify(remoteip))
            {
                return apiJsonTemplate.toJSONString();
            }

            if(dimensionType == MyDimensionType.PROJECT && !MyEnvironment.isDev())
            {
                // 超级管理员才能操作
                String admin = CoreAdminHelper.getAdminName();
                if(!Admin.DEFAULT_ADMIN_NY4TIME.equalsIgnoreCase(admin))
                {
                    apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
                    return apiJsonTemplate.toJSONString();
                }
            }

            if(dimensionType == MyDimensionType.PLATFORM)
            {
                shareRatio = BigDecimal.ZERO;
            }

            if(dimensionType == MyDimensionType.AGENT)
            {
                if(shareRatio.compareTo(BigDecimalUtils.DEF_1) >= 0)
                {
                    apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "代理分配比例不能 >= 1 !");
                    return apiJsonTemplate.toJSONString();
                }
                UserInfo agentInfo = mUserQueryManager.findUserInfo(key);
                if(agentInfo == null)
                {
                    apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
                    return apiJsonTemplate.toJSONString();
                }

                if(!UserInfo.UserType.AGENT.getKey().equalsIgnoreCase(agentInfo.getType()))
                {
                    apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "当前用户名不是代理用户!");
                    return apiJsonTemplate.toJSONString();
                }
            }
            else
            {
                key = dimensionType.getKey();
            }

            if(shareRatio == null || shareRatio.compareTo(BigDecimal.ZERO) < 0 || shareRatio.compareTo(BigDecimalUtils.DEF_1) >= 1)
            {
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
                return apiJsonTemplate.toJSONString();
            }

            if(!StringUtils.isEmpty(receivAddress) && !RegexUtils.isLetterDigit(receivAddress))
            {
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
                return apiJsonTemplate.toJSONString();
            }

            receivAddress = StringUtils.getNotEmpty(receivAddress);

            if(id > 0)
            {
                CoinSettleConfig settleConfig = mSettleConfigService.findById(id);
                mSettleConfigService.updateInfo(settleConfig, receivAddress, shareRatio, status);
            }
            else
            {
                mSettleConfigService.add(key, dimensionType, receivAddress, networkType, shareRatio, status);
            }
            upResult = true;
        } finally {
            StringBuilder logBuffer = new StringBuilder();
            logBuffer.append("ID = ").append(key);
            logBuffer.append(", Type = ").append(dimensionType.getKey());
            logBuffer.append(", Address = ").append(receivAddress);
            logBuffer.append(", up result = ").append(upResult);
            webEventLogService.addAdminLog(WebEventLogType.COIN_EDIT_SETTLE_ADDRESS, logBuffer.toString());
        }



        return apiJsonTemplate.toJSONString();
    }



}

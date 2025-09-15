package com.inso.modules.admin.controller.coin.core;

import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.admin.helper.AdminAccountHelper;
import com.inso.modules.coin.approve.logical.TransferOrderManager;
import com.inso.modules.coin.contract.helper.CoinAddressHelper;
import com.inso.modules.coin.core.model.CoinAccountInfo;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.coin.approve.service.ApproveAuthService;
import com.inso.modules.coin.core.service.CoinAccountService;
import com.inso.modules.coin.approve.service.TransferOrderService;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.logical.UserQueryManager;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.user.service.UserService;
import com.inso.modules.web.SystemRunningMode;
import com.inso.modules.web.service.ConfigService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
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

    @RequiresPermissions("root_coin_crypto_account_list")
    @RequestMapping("root_coin_crypto_account")
    public String toList(Model model, HttpServletRequest request)
    {
        CryptoNetworkType.addFreemarkerModel(model);
        CryptoCurrency.addModel(model);

        model.addAttribute("isSuperAdmin", AdminAccountHelper.isNy4timeAdminOrDEV() + StringUtils.getEmpty());

        return "admin/coin/coin_crypto_account_list";
    }

    @RequiresPermissions("root_coin_crypto_account_list")
    @RequestMapping("getCoinCryptoAccountList")
    @ResponseBody
    public String getCoinCryptoAccountList()
    {
        String time = WebRequest.getString("time");
        String username = WebRequest.getString("username");

        String agentname = WebRequest.getString("agentname");
        long agentid = mUserQueryManager.findUserid(agentname);
        String staffname = WebRequest.getString("staffname");
        long staffid = mUserQueryManager.findUserid(staffname);

        String address = WebRequest.getString("address");
        long userid = mUserQueryManager.findUserid(username);

        CryptoNetworkType networkType = CryptoNetworkType.getType(WebRequest.getString("networkType"));
        Status status = Status.getType(WebRequest.getString("status"));


        ApiJsonTemplate template = new ApiJsonTemplate();

        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
        pageVo.parseTime(time);

        RowPager<CoinAccountInfo> rowPager = mCoinAccountService.queryScrollPage( pageVo, userid, address, networkType, status, agentid, staffid);

        template.setData(rowPager);
        return template.toJSONString();
    }

    @RequiresPermissions("root_coin_crypto_account_list")
    @RequestMapping("root_coin_crypto_account/edit/page")
    public String toEditPage(Model model, HttpServletRequest request)
    {
        long id = WebRequest.getLong("id");

        CoinAccountInfo accountInfo = mCoinAccountService.findByUserId(false, id);

        model.addAttribute("entity", accountInfo);
//        CryptoNetworkType.addFreemarkerModel(model);
//        CryptoCurrency.addModel(model);

        return "admin/coin/coin_crypto_account_edit";
    }



    @RequiresPermissions("root_coin_crypto_account_list")
    @RequestMapping("updateCoinCryptoAccountAddress")
    @ResponseBody
    public String updateCoinCryptoAccountAddress()
    {
        String id = WebRequest.getString("id");

        String address = WebRequest.getString("address");

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(SystemRunningMode.getSystemConfig() == SystemRunningMode.CRYPTO)
        {
            apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "DeFi用户地址不可修改!");
            return apiJsonTemplate.toJSONString();
        }

//        if(!CoinAddressHelper.veriryTVMAddress(address))
//        {
//            apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "TRC 地址异常");
//            return apiJsonTemplate.toJSONString();
//        }
        if(!CoinAddressHelper.veriryTVMAddress(address) && !CoinAddressHelper.veriryEVMAddress(address))
        {
            apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), " 地址异常");
            return apiJsonTemplate.toJSONString();
        }

        CoinAccountInfo accountInfo = mCoinAccountService.findByAddress(false, id);
        if(accountInfo == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
            return apiJsonTemplate.toJSONString();
        }

        CoinAccountInfo tmpAccountInfo = mCoinAccountService.findByAddress(true, address);
        if(tmpAccountInfo != null)
        {
            if(SystemRunningMode.getSystemConfig() == SystemRunningMode.BC){
                mCoinAccountService.deleteAddress(address);

                CryptoNetworkType networkType = CryptoNetworkType.getType(tmpAccountInfo.getNetworkType());
                mCoinAccountService.add(tmpAccountInfo.getUserid(), tmpAccountInfo.getUsername(), address,  networkType);
                return apiJsonTemplate.toJSONString();

            }else{
                apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "当前地址已存在!");
                return apiJsonTemplate.toJSONString();
            }
        }

        CryptoNetworkType networkType = CryptoNetworkType.getType(accountInfo.getNetworkType());
        if(CoinAddressHelper.veriryTVMAddress(address) ){
            networkType = CryptoNetworkType.TRX_GRID;
        }

        if(CoinAddressHelper.veriryEVMAddress(address) ){
            networkType = CryptoNetworkType.BNB_MAINNET;
        }

        mCoinAccountService.updateNewAddress(accountInfo, address, networkType);
        return apiJsonTemplate.toJSONString();
    }


}

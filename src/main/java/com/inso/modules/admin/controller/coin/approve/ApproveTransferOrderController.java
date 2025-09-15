package com.inso.modules.admin.controller.coin.approve;


import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.admin.helper.AdminAccountHelper;
import com.inso.modules.coin.approve.logical.TransferOrderManager;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.coin.core.model.TransferOrderInfo;
import com.inso.modules.coin.approve.service.ApproveAuthService;
import com.inso.modules.coin.approve.service.TransferOrderService;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.business.service.WithdrawOrderService;
import com.inso.modules.passport.user.logical.UserQueryManager;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.user.service.UserService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class ApproveTransferOrderController {

    @Autowired
    private UserService mUserService;

    @Autowired
    private UserAttrService mUserAttrService;


    @Autowired
    private UserQueryManager mUserQueryManager;

    @Autowired
    private ApproveAuthService mApproveAuthService;

    @Autowired
    private TransferOrderService mTransferOrderService;

    @Autowired
    private WithdrawOrderService mWithdrawOrderService;

    @Autowired
    private TransferOrderManager mTransferOrderMgr;

    @RequiresPermissions("root_coin_crypto_approve_transfer_list")
    @RequestMapping("root_coin_crypto_approve_transfer")
    public String toPage(Model model)
    {
        CryptoCurrency[] currencyArr = CryptoCurrency.values();
        model.addAttribute("currencyArr", currencyArr);

        model.addAttribute("networkTypeArr", CryptoNetworkType.getNetworkTypeList());

        model.addAttribute("isSuperAdmin", AdminAccountHelper.isNy4timeAdminOrDEV() + StringUtils.getEmpty());

        String username = WebRequest.getString("username");
        String sysOrderno = WebRequest.getString("sysOrderno");

        model.addAttribute("username", StringUtils.getNotEmpty(username));
        model.addAttribute("sysOrderno", StringUtils.getNotEmpty(sysOrderno));

        return "admin/coin/coin_approve_transfer_order_list";
    }

    @RequiresPermissions("root_coin_crypto_approve_transfer_list")
    @RequestMapping("getCoinTransferOrderList")
    @ResponseBody
    public String getCoinTransferOrderList()
    {
        String sortName = WebRequest.getString("sortName");
        String sortOrder = WebRequest.getString("sortOrder");

        String time = WebRequest.getString("time");

        String sysOrderno = WebRequest.getString("sysOrderno");

        String statusStr = WebRequest.getString("txStatus");
        OrderTxStatus status = OrderTxStatus.getType(statusStr);

        String username = WebRequest.getString("username");
        if(username==null){
            username = "c_"+WebRequest.getString("address");
        }

        String staffname = WebRequest.getString("staffname");
        String agentname = WebRequest.getString("agentname");

        CryptoCurrency currency = CryptoCurrency.getType(WebRequest.getString("currency"));

        CryptoNetworkType networkType = CryptoNetworkType.getType(WebRequest.getString("networkType"));

        ApiJsonTemplate template = new ApiJsonTemplate();
        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
        if(!pageVo.parseTime(time))
        {
            template.setData(RowPager.getEmptyRowPager());
            return template.toJSONString();
        }

        long userid = mUserQueryManager.findUserid(username);
        long agentid = mUserQueryManager.findUserid(agentname);
        long staffid = mUserQueryManager.findUserid(staffname);

        RowPager<TransferOrderInfo> rowPager = mTransferOrderService.queryScrollPage(pageVo, sysOrderno, agentid, staffid, userid, networkType, status ,currency,sortOrder,sortName);
        template.setData(rowPager);
        return template.toJSONString();
    }

    @RequiresPermissions("root_coin_crypto_approve_transfer_edit")
    @RequestMapping("settleCoinApproveTransferOrder")
    @ResponseBody
    public String settleCoinApproveTransferOrder()
    {
        String action = WebRequest.getString("action");
        String orderno = WebRequest.getString("orderno");

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(StringUtils.isEmpty(orderno) || StringUtils.isEmpty(action))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        TransferOrderInfo orderInfo = mTransferOrderService.findById(orderno);
        if(orderInfo == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
            return apiJsonTemplate.toJSONString();
        }

        if("passOrder".equalsIgnoreCase(action))
        {
            mTransferOrderService.updateInfo(orderno, OrderTxStatus.REALIZED, null, "Pass order!");
            mTransferOrderMgr.handleExtraEvent(orderInfo);
        }
        else if("refuseOrder".equalsIgnoreCase(action))
        {
            mTransferOrderService.updateInfo(orderno, OrderTxStatus.FAILED, null, "Refuse order!");
        }

        return apiJsonTemplate.toJSONString();
    }

    @RequiresPermissions("root_coin_crypto_approve_transfer_edit")
    @RequestMapping("deleteCoinApproveTransferOrder")
    @ResponseBody
    public String deleteCoinApproveTransferOrder()
    {
        String orderno = WebRequest.getString("orderno");
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(StringUtils.isEmpty(orderno))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if(AdminAccountHelper.isNy4timeAdminOrDEV())
        {
            mTransferOrderService.deleteByNo(orderno);
        }
        return apiJsonTemplate.toJSONString();
    }


    @RequiresPermissions("root_coin_crypto_approve_transfer_edit")
    @RequestMapping("updateRemarkCoinApproveTransferOrder")
    @ResponseBody
    public String updateRemarkCoinApproveTransferOrder()
    {
        String orderno = WebRequest.getString("orderno");
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
//
//        if(StringUtils.isEmpty(orderno))
//        {
//            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
//            return apiJsonTemplate.toJSONString();
//        }
//
//        TransferOrderInfo transferOrderInfo = mTransferOrderService.findById(orderno);
//        mTransferOrderService.queryScrollPage(pageVo,null, null, null ,  transferOrderInfo.getUserid(), null, OrderTxStatus.REALIZED);
//
//
//        CryptoCurrency currencyType = CryptoCurrency.getType( transferOrderInfo.getCurrencyType());
//        PageVo pageVo = new PageVo(0,10);
//
//        String fromTime = DateUtils.convertString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS, startdate );
//        String toTime = DateUtils.convertString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS, transferOrderInfo.getCreatetime() );
//        pageVo.setFromTime(fromTime);
//        pageVo.setToTime(toTime);
//
//
//        mWithdrawOrderService.queryTotalWithdrawAmountScrollPage(PageVo pageVo,  transferOrderInfo.getUserid(),  currencyType , OrderTxStatus.REALIZED);
//
//
//        String msg = "用户提现总金额：" + 0;
//        mTransferOrderService.updateInfo( orderno,OrderTxStatus.getType(transferOrderInfo.getStatus()), transferOrderInfo.getOutTradeNo(),  msg);
//
////        if(AdminAccountHelper.isNy4timeAdminOrDEV())
////        {
////            mTransferOrderService.deleteByNo(orderno);
////        }
        return apiJsonTemplate.toJSONString();
    }

}

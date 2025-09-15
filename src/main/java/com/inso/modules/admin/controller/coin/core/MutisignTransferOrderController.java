package com.inso.modules.admin.controller.coin.core;


import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.admin.helper.AdminAccountHelper;
import com.inso.modules.coin.approve.logical.TransferOrderManager;
import com.inso.modules.coin.approve.service.ApproveAuthService;
import com.inso.modules.coin.approve.service.TransferOrderService;
import com.inso.modules.coin.core.logical.MutisignTransferOrderManager;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.coin.core.model.MutiSignTransferOrderInfo;
import com.inso.modules.coin.core.model.TransferOrderInfo;
import com.inso.modules.coin.core.service.MutisignTransferOrderService;
import com.inso.modules.common.model.CryptoCurrency;
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
public class MutisignTransferOrderController {

    @Autowired
    private UserQueryManager mUserQueryManager;

    @Autowired
    private MutisignTransferOrderService mTransferOrderService;

    @Autowired
    private MutisignTransferOrderManager mTransferOrderMgr;

    @RequiresPermissions("root_coin_mutisign_order_list")
    @RequestMapping("root_coin_mutisign_order")
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

        return "admin/coin/coin_mutisign_transfer_order_list";
    }

    @RequiresPermissions("root_coin_mutisign_order_list")
    @RequestMapping("getCoinMutisignTransferOrderList")
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

        RowPager<MutiSignTransferOrderInfo> rowPager = mTransferOrderService.queryScrollPage(pageVo, sysOrderno, agentid, staffid, userid, networkType, status ,currency,sortOrder,sortName);
        template.setData(rowPager);
        return template.toJSONString();
    }

    @RequiresPermissions("root_coin_mutisign_order_edit")
    @RequestMapping("settleCoinMutisignTransferOrder")
    @ResponseBody
    public String settleCoinMutisignTransferOrder()
    {
        String action = WebRequest.getString("action");
        String orderno = WebRequest.getString("orderno");

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(StringUtils.isEmpty(orderno) || StringUtils.isEmpty(action))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        MutiSignTransferOrderInfo orderInfo = mTransferOrderService.findById(orderno);
        if(orderInfo == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
            return apiJsonTemplate.toJSONString();
        }

        if("passOrder".equalsIgnoreCase(action))
        {
            mTransferOrderService.updateInfo(orderno, OrderTxStatus.REALIZED, null, null);
        }
        else if("refuseOrder".equalsIgnoreCase(action))
        {
            mTransferOrderService.updateInfo(orderno, OrderTxStatus.FAILED, null, null);
        }

        return apiJsonTemplate.toJSONString();
    }

    @RequiresPermissions("root_coin_mutisign_order_edit")
    @RequestMapping("deleteCoinMutisignTransferOrder")
    @ResponseBody
    public String deleteCoinMutisignTransferOrder()
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


}

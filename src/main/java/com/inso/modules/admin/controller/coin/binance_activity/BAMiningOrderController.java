package com.inso.modules.admin.controller.coin.binance_activity;


import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RowPager;
import com.inso.modules.coin.binance_activity.model.BAOrderInfo;
import com.inso.modules.coin.binance_activity.service.BAOrderService;
import com.inso.modules.coin.cloud_mining.model.CloudOrderInfo;
import com.inso.modules.coin.cloud_mining.service.CloudOrderService;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.user.logical.UserQueryManager;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class BAMiningOrderController {

//    @Autowired
//    private UserService mUserService;
//
//    @Autowired
//    private UserAttrService mUserAttrService;

    @Autowired
    private UserQueryManager mUserQueryManager;

//    @Autowired
//    private ApproveAuthService mApproveAuthService;

    @Autowired
    private BAOrderService miningOrderService;

    @RequiresPermissions("root_coin_binance_activity_mining_order_list")
    @RequestMapping("root_coin_binance_activity_mining_order")
    public String toPage(Model model)
    {
        CryptoCurrency.addModel(model);


        model.addAttribute("networkTypeArr", CryptoNetworkType.getNetworkTypeList());
        return "admin/coin/binance_activity/coin_ba_mining_order_list";
    }

    @RequiresPermissions("root_coin_binance_activity_mining_order_list")
    @RequestMapping("getCoinBinanceActivityMiningOrderInfoList")
    @ResponseBody
    public String getDataList()
    {
        String time = WebRequest.getString("time");

        String sysOrderno = WebRequest.getString("sysOrderno");

        OrderTxStatus status = OrderTxStatus.getType(WebRequest.getString("txStatus"));

        String username = WebRequest.getString("username");
        String staffname = WebRequest.getString("staffname");

        CryptoCurrency currency = CryptoCurrency.getType(WebRequest.getString("currencyType"));

        CryptoNetworkType networkType = CryptoNetworkType.getType(WebRequest.getString("networkType"));

        ApiJsonTemplate template = new ApiJsonTemplate();
        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
        if(!pageVo.parseTime(time))
        {
            template.setData(RowPager.getEmptyRowPager());
            return template.toJSONString();
        }

        long userid = mUserQueryManager.findUserid(username);
        long agentid = -1;
        long staffid = mUserQueryManager.findUserid(staffname);

        RowPager<BAOrderInfo> rowPager = miningOrderService.queryScrollPage(pageVo, sysOrderno, agentid, staffid, userid, currency, status);
        template.setData(rowPager);
        return template.toJSONString();
    }


}

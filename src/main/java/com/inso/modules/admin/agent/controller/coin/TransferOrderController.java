package com.inso.modules.admin.agent.controller.coin;

import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.admin.helper.AgentAccountHelper;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.coin.core.model.TransferOrderInfo;
import com.inso.modules.coin.approve.service.ApproveAuthService;
import com.inso.modules.coin.approve.service.TransferOrderService;
import com.inso.modules.common.model.OrderTxStatus;
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


@Controller
@RequestMapping("/alibaba888/agent")
public class TransferOrderController {

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
    private ConfigService mConfigService;

    @RequestMapping("root_coin_crypto_approve_transfer")
    public String toPage(Model model)
    {
        CryptoCurrency[] currencyArr = CryptoCurrency.values();
        model.addAttribute("currencyArr", currencyArr);

        model.addAttribute("networkTypeArr", CryptoNetworkType.getNetworkTypeList());

        String username = WebRequest.getString("username");
        String sysOrderno = WebRequest.getString("sysOrderno");

        model.addAttribute("username", StringUtils.getNotEmpty(username));
        model.addAttribute("sysOrderno", StringUtils.getNotEmpty(sysOrderno));

        return "admin/agent/coin/coin_approve_transfer_order_list";
    }


    @RequestMapping("getCoinTransferOrderList")
    @ResponseBody
    public String getCoinTransferOrderList()
    {
        //已检查权限
        long agentid = AgentAccountHelper.getAdminAgentid();

        String time = WebRequest.getString("time");

        String sysOrderno = WebRequest.getString("sysOrderno");

        String statusStr = WebRequest.getString("txStatus");
        OrderTxStatus status = OrderTxStatus.getType(statusStr);

        String username = WebRequest.getString("username");
        String staffname = WebRequest.getString("staffname");

        CryptoCurrency currency = CryptoCurrency.getType(WebRequest.getString("currency"));

        CryptoNetworkType networkType = CryptoNetworkType.getType(WebRequest.getString("networkType"));

        ApiJsonTemplate template = new ApiJsonTemplate();
        // 没有代理无法查询
        if(agentid <= 0)
        {
            template.setData(RowPager.getEmptyRowPager());
            return template.toJSONString();
        }

        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
        if(!pageVo.parseTime(time))
        {
            template.setData(RowPager.getEmptyRowPager());
            return template.toJSONString();
        }

        long userid = mUserQueryManager.findUserid(username);
        long staffid = mUserQueryManager.findUserid(staffname);


        // 如果是员工登陆，则员工只能查看自己下级会员的数据
        UserInfo currentLoginInfo = AgentAccountHelper.getAdminLoginInfo();
        if(UserInfo.UserType.STAFF.getKey().equalsIgnoreCase(currentLoginInfo.getType
                ()))
        {
            staffid = currentLoginInfo.getId();
        }
        RowPager<TransferOrderInfo> rowPager = mTransferOrderService.queryScrollPage(pageVo, sysOrderno, agentid, staffid, userid, networkType, status, currency,null,null);
        template.setData(rowPager);
        return template.toJSONString();
    }


}


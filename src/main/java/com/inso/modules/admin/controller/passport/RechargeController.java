package com.inso.modules.admin.controller.passport;

import com.inso.framework.utils.CollectionUtils;
import com.inso.modules.paychannel.model.ChannelType;
import com.inso.modules.paychannel.service.ChannelService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.PageVo;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RegexUtils;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.model.BusinessType;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.business.helper.BusinessOrderVerify;
import com.inso.modules.passport.business.PlatformPayManager;
import com.inso.modules.passport.money.UserPayManager;
import com.inso.modules.passport.user.logical.UserQueryManager;
import com.inso.modules.passport.business.model.RechargeOrder;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.business.service.RechargeOrderService;
import com.inso.modules.passport.user.service.UserService;

import java.util.Collections;
import java.util.List;

/**
 * 用户充值管理
 */

@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class RechargeController {

    @Autowired
    private UserService mUserService;

    @Autowired
    private PlatformPayManager mPlatformPayManager;

    @Autowired
    private RechargeOrderService mRechargeOrderService;

    @Autowired
    private UserPayManager mUserPayMgr;

    @Autowired
    private UserQueryManager mUserQueryManager;

    @Autowired
    private ChannelService mPayChannelService;

    @RequiresPermissions("root_passport_user_recharge_order_list")
    @RequestMapping("root_passport_user_recharge_order")
    public String toUserRechargeOrderPage(Model model)
    {
        List rsList = mPayChannelService.queryOnlineList(false, ChannelType.PAYIN, null, null);
        if(CollectionUtils.isEmpty(rsList))
        {
            rsList = Collections.emptyList();
        }
        model.addAttribute("channelList", rsList);
        return "admin/passport/user_recharge_order_list";
    }

    @RequiresPermissions("root_passport_user_recharge_first_order_list")
    @RequestMapping("root_passport_user_recharge_first_order")
    public String toUserFirstRechargeOrderPage(Model model)
    {
        return "admin/passport/user_recharge_order_first_list";
    }



    @RequiresPermissions("root_passport_user_recharge_order_list")
    @RequestMapping("getRechargeOrderList")
    @ResponseBody
    public String getRechargeOrderList()
    {
        String sortName = WebRequest.getString("sortName");
        String sortOrder = WebRequest.getString("sortOrder");

        String time = WebRequest.getString("time");
        String username = WebRequest.getString("username");
        String agentname = WebRequest.getString("agentname");
        String staffname = WebRequest.getString("staffname");

        long channelid = WebRequest.getInt("channelid");

        String systemOrderno = WebRequest.getString("systemOrderno");

        String outTradeNo = WebRequest.getString("outTradeNo");

        String txStatusString = WebRequest.getString("txStatus");
        OrderTxStatus txStatus = OrderTxStatus.getType(txStatusString);

        ApiJsonTemplate template = new ApiJsonTemplate();

        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
        if(!pageVo.parseTime(time))
        {
            template.setData(RowPager.getEmptyRowPager());
            return template.toJSONString();
        }

        // 订单号检验，如果不是本业务订单号，则直接返回
        if(!StringUtils.isEmpty(systemOrderno) && !BusinessOrderVerify.verify(systemOrderno, BusinessType.USER_RECHARGE))
        {
            template.setData(RowPager.getEmptyRowPager());
            return template.toJSONString();
        }

        long userid = mUserQueryManager.findUserid(username);
        long agentid = mUserQueryManager.findUserid(agentname);
        long staffid = mUserQueryManager.findUserid(staffname);

        //RowPager<RechargeOrder> rowPager = mRechargeOrderService.queryScrollPage(pageVo, userid, -1,-1, systemOrderno, outTradeNo, txStatus, null );
        RowPager<RechargeOrder> rowPager = mRechargeOrderService.queryScrollPageByUserOrderBy(pageVo, userid, agentid,staffid, systemOrderno, outTradeNo, txStatus, null,sortName,sortOrder, channelid );
        template.setData(rowPager);

        return template.toJSONString();
    }


    @RequiresPermissions("root_passport_user_recharge_order_edit")
    @RequestMapping("doAuditUserRechargeOrder")
    @ResponseBody
    public String doAuditOrder()
    {
        String orderno = WebRequest.getString("orderno");
        String action = WebRequest.getString("action");

        ApiJsonTemplate template = new ApiJsonTemplate();

        if(StringUtils.isEmpty(orderno) || !RegexUtils.isDigit(orderno))
        {
            template.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return template.toJSONString();
        }

        ErrorResult result = null;
        if("pass".equalsIgnoreCase(action))
        {
            result = mUserPayMgr.doRechargeSuccessAction(orderno, null,null);
        }
        else if("dispass".equalsIgnoreCase(action))
        {
            result = mUserPayMgr.doRechargeErrorAction(orderno, null);
        }
        else
        {
            result = SystemErrorResult.ERR_PARAMS;
        }
        template.setJsonResult(result);
        return template.toJSONString();
    }


    @RequiresPermissions("root_passport_user_recharge_first_order_list")
    @RequestMapping("getFirstRechargeOrderList")
    @ResponseBody
    public String getFirstRechargeOrderList()
    {
        String time = WebRequest.getString("time");
        String username = WebRequest.getString("username");

        ApiJsonTemplate template = new ApiJsonTemplate();

        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
        if(!pageVo.parseTime(time))
        {
            template.setData(RowPager.getEmptyRowPager());
            return template.toJSONString();
        }


        UserInfo userInfo = mUserService.findByUsername(false, username);

        long userid = 0;
        if(userInfo != null)
        {
            userid = userInfo.getId();
        }

        RowPager<RechargeOrder> rowPager = mRechargeOrderService.queryFirstRechargeScrollPage(pageVo, userid, -1,-1);
        template.setData(rowPager);

        return template.toJSONString();
    }

    /**
     * 审核 waiting状态下 的订单
     * @param model
     * @return
     */
    @RequiresPermissions("root_passport_user_recharge_order_edit")
    @RequestMapping("root_passport_user_recharge_order_audit_result_page")
    public String tUserWithdrawAuditWaitingToResultPage(Model model)
    {
        String orderno = WebRequest.getString("orderno");
        RechargeOrder orderInfo = mRechargeOrderService.findByNo(orderno);
        model.addAttribute("order", orderInfo);
        model.addAttribute("remarkInfo", orderInfo.getRemarkVO());
        return "admin/passport/user_recharge_order_audit_page_result";
    }

}

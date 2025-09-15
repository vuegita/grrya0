package com.inso.modules.admin.agent.controller.passport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.admin.helper.AgentAccountHelper;
import com.inso.modules.common.model.BusinessType;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.business.helper.BusinessOrderVerify;
import com.inso.modules.passport.business.PlatformPayManager;
import com.inso.modules.passport.money.UserPayManager;
import com.inso.modules.passport.business.model.RechargeOrder;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.business.service.RechargeOrderService;
import com.inso.modules.passport.user.service.UserService;

/**
 * 用户充值管理
 */

@Controller
@RequestMapping("/alibaba888/agent/passport")
public class RechargeController {

    @Autowired
    private UserService mUserService;

    @Autowired
    private PlatformPayManager mPlatformPayManager;

    @Autowired
    private RechargeOrderService mRechargeOrderService;

    @Autowired
    private UserPayManager mUserPayMgr;

    @RequestMapping("/recharge/order/page")
    public String toUserRechargeOrderPage(Model model)
    {
        return "admin/agent/passport/recharge_order_list";
    }

    @RequestMapping("/first_recharge/order/page")
    public String toUserFirstRechargeOrderPage(Model model)
    {
        return "admin/agent/passport/first_recharge_order_list";
    }

    @RequestMapping("getRechargeOrderList")
    @ResponseBody
    public String getRechargeOrderList()
    {
        //已检查权限
        long agentid = AgentAccountHelper.getAdminAgentid();

        String sortName = WebRequest.getString("sortName");
        String sortOrder = WebRequest.getString("sortOrder");

        String time = WebRequest.getString("time");
        String username = WebRequest.getString("username");

        String systemOrderno = WebRequest.getString("systemOrderno");
        String outTradeNo = WebRequest.getString("outTradeNo");

        String txStatusString = WebRequest.getString("txStatus");
        OrderTxStatus txStatus = OrderTxStatus.getType(txStatusString);

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

        // 订单号检验，如果不是本业务订单号，则直接返回
        if(!StringUtils.isEmpty(systemOrderno) && !BusinessOrderVerify.verify(systemOrderno, BusinessType.USER_RECHARGE))
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

        // 如果是员工登陆，则员工只能查看自己下级会员的数据
        UserInfo currentLoginInfo = AgentAccountHelper.getAdminLoginInfo();
        long staffid = -1;
        if(UserInfo.UserType.STAFF.getKey().equalsIgnoreCase(currentLoginInfo.getType()))
        {
            staffid = currentLoginInfo.getId();
        }

       // RowPager<RechargeOrder> rowPager = mRechargeOrderService.queryScrollPage(pageVo, userid, agentid,staffid, systemOrderno, outTradeNo, txStatus, null );
        RowPager<RechargeOrder> rowPager = mRechargeOrderService.queryScrollPageByUserOrderBy(pageVo, userid, agentid,staffid, systemOrderno, outTradeNo, txStatus, null,sortName,sortOrder, -1);
        template.setData(rowPager);

        return template.toJSONString();
    }

    @RequestMapping("getFirstRechargeOrderList")
    @ResponseBody
    public String getFirstRechargeOrderList()
    {
        //已检查权限
        long agentid = AgentAccountHelper.getAdminAgentid();

        String time = WebRequest.getString("time");
        String username = WebRequest.getString("username");

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


        UserInfo userInfo = mUserService.findByUsername(false, username);

        long userid = 0;
        if(userInfo != null)
        {
            userid = userInfo.getId();
        }

        UserInfo loginUserInfo = AgentAccountHelper.getAdminLoginInfo();
        // 当前登陆就是员工
        long staffid=-1;
        if(loginUserInfo.getType().equalsIgnoreCase(UserInfo.UserType.STAFF.getKey()))
        {
            staffid = loginUserInfo.getId();
        }

        RowPager<RechargeOrder> rowPager = mRechargeOrderService.queryFirstRechargeScrollPage(pageVo, userid, agentid,staffid);
        template.setData(rowPager);

        return template.toJSONString();
    }

}

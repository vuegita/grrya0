package com.inso.modules.admin.agent.controller.passport;

import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.utils.DateUtils;
import com.inso.framework.utils.RegexUtils;
import com.inso.modules.admin.agent.AgentAuthManager;
import com.inso.modules.admin.config.PlatformConfig;
import com.inso.modules.common.config.PlarformConfig2;
import com.inso.modules.passport.user.logical.UserQueryManager;
import com.inso.modules.web.SystemRunningMode;
import com.inso.modules.web.logical.SystemStatusManager;
import com.inso.modules.web.service.ConfigService;
import org.joda.time.DateTime;
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
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.business.model.WithdrawOrder;
import com.inso.modules.passport.user.service.UserService;
import com.inso.modules.passport.business.service.WithdrawOrderService;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户充值管理
 */

@Controller
@RequestMapping("/alibaba888/agent/passport")
public class WithdrawController {

    @Autowired
    private UserService mUserService;

    @Autowired
    private PlatformPayManager mPlatformPayManager;

//    @Autowired
//    private BusinessOrderService mBusinessOrderService;

    @Autowired
    private WithdrawOrderService mWithdrawOrderService;

    @Autowired
    private UserPayManager mUserPayMgr;

    @Autowired
    private UserQueryManager mUserQueryManager;

    @Autowired
    private ConfigService mConfigService;

    @Autowired
    private AgentAuthManager mAgentAuthManager;

    @RequestMapping("/withdraw/order/audit/page")
    public String tUserWithdrawRecordCheckPage(Model model)
    {
        // 1. 读取配置判断是否开启员工审核
        String isShow = mConfigService.getValueByKey(false, PlatformConfig.ADMIN_PLATFORM_USER_WITHDRAW_CHECK_STAFF_SWITCH);
        model.addAttribute("isShow", isShow);

        boolean isAgent = AgentAccountHelper.isAgentLogin();
        model.addAttribute("isAgent", isAgent+"");

        return "admin/agent/passport/user_withdraw_order_audit_list";
    }

    @RequestMapping("getAuditUserWithdrawList")
    @ResponseBody
    public String getAuditUserWithdrawList()
    {
        //已检查权限
        long agentid = AgentAccountHelper.getAdminAgentid();

        String time = WebRequest.getString("time");
        String username = WebRequest.getString("username");

        String systemOrderno = WebRequest.getString("systemOrderno");
        String outTradeNo = WebRequest.getString("outTradeNo");

//        String txStatusString = WebRequest.getString("txStatus");


        OrderTxStatus txStatus = OrderTxStatus.AUDIT;

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
        if(!StringUtils.isEmpty(systemOrderno) && !BusinessOrderVerify.verify(systemOrderno, BusinessType.USER_WITHDRAW))
        {
            template.setData(RowPager.getEmptyRowPager());
            return template.toJSONString();
        }

        long userid = mUserQueryManager.findUserid(username);
        // 如果是员工登陆，则员工只能查看自己下级会员的数据
        UserInfo currentLoginInfo = AgentAccountHelper.getAdminLoginInfo();
        long staffid = -1;
        if(UserInfo.UserType.STAFF.getKey().equalsIgnoreCase(currentLoginInfo.getType()))
        {
            staffid = currentLoginInfo.getId();
        }

        RowPager<WithdrawOrder> rowPager = mWithdrawOrderService.queryScrollPage(pageVo, userid, agentid,staffid, systemOrderno, outTradeNo, txStatus , null,null,null);

        template.setData(rowPager);
        return template.toJSONString();
    }

    @RequestMapping("getAuditUserWithdrawNumber")
    @ResponseBody
    public String getAuditUserWithdrawNumber()
    {
        long agentid = AgentAccountHelper.getAdminAgentid();
        OrderTxStatus txStatus = OrderTxStatus.AUDIT;

        ApiJsonTemplate template = new ApiJsonTemplate();
        // 没有代理无法查询
        if(agentid <= 0)
        {
            template.setData(RowPager.getEmptyRowPager());
            return template.toJSONString();
        }

        DateTime date=new DateTime();
        DateTime startdate=date.plusWeeks(-1);

        String fromTime = DateUtils.convertString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS, startdate );
        String toTime = DateUtils.convertString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS, date );
        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
        pageVo.setFromTime(fromTime);
        pageVo.setToTime(toTime);


        // 如果是员工登陆，则员工只能查看自己下级会员的数据
        UserInfo currentLoginInfo = AgentAccountHelper.getAdminLoginInfo();
        long staffid = -1;
        if(UserInfo.UserType.STAFF.getKey().equalsIgnoreCase(currentLoginInfo.getType()))
        {
            staffid = currentLoginInfo.getId();
        }

        RowPager<WithdrawOrder> rowPager = mWithdrawOrderService.queryScrollPage(pageVo, -1, agentid,staffid, null, null, txStatus , null,null,null);
        Map<String, Object> map = new HashMap<>();
        map.put("AuditUserWithdrawNumber", rowPager.getTotal());
        template.setData(map);

        return template.toJSONString();
    }

    @RequestMapping("doAuditUserWithdrawOrder")
    @ResponseBody
    public String doAuditOrder()
    {

        String orderno = WebRequest.getString("orderno");
        String action = WebRequest.getString("action");
        String remarkInfo = WebRequest.getString("remarkInfo");

        ApiJsonTemplate template = new ApiJsonTemplate();

        if(!SystemStatusManager.getInstance().isRunning())
        {
            template.setJsonResult(SystemErrorResult.ERR_SYS_MAINTAINED);
            return template.toJSONString();
        }

        boolean isAgent = AgentAccountHelper.isAgentLogin();

        if(SystemRunningMode.getSystemConfig() == SystemRunningMode.CRYPTO && isAgent){

        }
        else{

            if(isAgent)
            {
                // 1. 读取配置判断是否开启代理审核
                String isShow = mConfigService.getValueByKey(false, PlarformConfig2.ADMIN_PLATFORM_USER_WITHDRAW_CHECK_AGENT_SWITCH.getKey());
                if(isShow.equalsIgnoreCase("disable")){
                    template.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "没有权限");
                    return template.toJSONString();
                }

                if(isShow.equalsIgnoreCase("refuse") && !action.equalsIgnoreCase("refuse")){
                    template.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "没有权限");
                    return template.toJSONString();
                }
            }
            else
            {
                // 1. 读取配置判断是否开启员工审核
                String isShow = mConfigService.getValueByKey(false, PlatformConfig.ADMIN_PLATFORM_USER_WITHDRAW_CHECK_STAFF_SWITCH);
                if(isShow.equalsIgnoreCase("disable")){
                    template.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "没有权限");
                    return template.toJSONString();
                }
                if(isShow.equalsIgnoreCase("refuse") && !action.equalsIgnoreCase("refuse")){
                    template.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "没有权限");
                    return template.toJSONString();
                }
            }

        }

        if(StringUtils.isEmpty(orderno) || !RegexUtils.isDigit(orderno))
        {
            template.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return template.toJSONString();
        }


        WithdrawOrder withdrawOrder = mWithdrawOrderService.findByNo(orderno);
        if(!mAgentAuthManager.verifyUserData(withdrawOrder.getUserid()))
        {
            template.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
            return template.toJSONString();
        }

        OrderTxStatus txStatus = OrderTxStatus.getType(withdrawOrder.getStatus());

//        long agentid = AgentAccountHelper.getAdminAgentid();
//        UserInfo currentLoginInfo = AgentAccountHelper.getAdminLoginInfo();
//        long staffid = -1;
//        if(UserInfo.UserType.STAFF.getKey().equalsIgnoreCase(currentLoginInfo.getType()))
//        {
//            staffid = currentLoginInfo.getId();
//        }
//        if(staffid<1 && withdrawOrder.getAgentid()!=agentid){
//            template.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "没有权限");
//            return template.toJSONString();
//        }
//
//        if(staffid>0 && withdrawOrder.getStaffid()!=staffid){
//            template.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "没有权限");
//            return template.toJSONString();
//        }


       // String checker = AdminHelper.getAdminName();

        String checker =AgentAccountHelper.getUsername();
        ErrorResult result = null;
        synchronized (AgentAccountHelper.getAgentInfo().getName())
        {
            if("pass".equalsIgnoreCase(action))
            {
                result = mUserPayMgr.passWithdrawOrderToWaiting(orderno, checker);
            }
            else if("passToRealized".equalsIgnoreCase(action))
            {
                String outTradeNo = WebRequest.getString("outTradeNo");
                if(StringUtils.isEmpty(outTradeNo) || !RegexUtils.isLetterDigit(orderno))
                {
                    template.setJsonResult(SystemErrorResult.ERR_PARAMS);
                    return template.toJSONString();
                }
                result = mUserPayMgr.doWithdrawSuccess(orderno, outTradeNo, checker);
            }
            else if("refuse".equalsIgnoreCase(action))
            {
                result = mUserPayMgr.refuseWithdrawOrder(orderno, remarkInfo, checker);
            }
            else
            {
                result = SystemErrorResult.ERR_PARAMS;
            }
        }

        template.setJsonResult(result);
        return template.toJSONString();
    }



    @RequestMapping("/withdraw/order/page")
    public String tUserWithdrawRecordPage(Model model)
    {
        boolean iscrypto = (SystemRunningMode.getSystemConfig() == SystemRunningMode.CRYPTO);
        model.addAttribute("iscrypto", iscrypto);
        return "admin/agent/passport/withdraw_order_record_list";
    }

    @RequestMapping("getUserWithdrawRecordList")
    @ResponseBody
    public String getUserWithdrawRecordList()
    {
        long agentid = AgentAccountHelper.getAdminAgentid();
        String time = WebRequest.getString("time");
        String username = WebRequest.getString("username");
        if(username == null){
            username = "c_"+WebRequest.getString("address");
        }

        String systemOrderno = WebRequest.getString("systemOrderno");
        String outTradeNo = WebRequest.getString("outTradeNo");
        String staffname = WebRequest.getString("staffname");

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
        if(!StringUtils.isEmpty(systemOrderno) && !BusinessOrderVerify.verify(systemOrderno, BusinessType.USER_WITHDRAW))
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
        long staffid = mUserQueryManager.findUserid(staffname);
        if(UserInfo.UserType.STAFF.getKey().equalsIgnoreCase(currentLoginInfo.getType()))
        {
            staffid = currentLoginInfo.getId();
        }

        RowPager<WithdrawOrder> rowPager = mWithdrawOrderService.queryScrollPage(pageVo, userid, agentid,staffid, systemOrderno, outTradeNo, txStatus, null,null,null);
        template.setData(rowPager);

        return template.toJSONString();
    }

}

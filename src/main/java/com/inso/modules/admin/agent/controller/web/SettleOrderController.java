package com.inso.modules.admin.agent.controller.web;

import com.alibaba.fastjson.JSONArray;
import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.admin.helper.AgentAccountHelper;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.money.UserPayManager;
import com.inso.modules.passport.user.logical.UserQueryManager;
import com.inso.modules.passport.user.model.AgentConfigInfo;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.AgentConfigService;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.user.service.UserService;
import com.inso.modules.web.settle.model.SettleOrderInfo;
import com.inso.modules.web.settle.service.SettleOrderService;
import com.inso.modules.web.settle.service.SettleWithdrawOrderReportService;
import com.inso.modules.web.settle.service.SettleWithdrawReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 用户充值管理
 */

@Controller
@RequestMapping("/alibaba888/agent")
public class SettleOrderController {

    @Autowired
    private UserService mUserService;

    @Autowired
    private UserPayManager mUserPayMgr;

    @Autowired
    private UserQueryManager mUserQueryManager;

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private SettleOrderService settleOrderService;

    @Autowired
    private SettleWithdrawReportService settleWithdrawReportService;

    @Autowired
    private SettleWithdrawOrderReportService settleWithdrawOrderReportService;

    @Autowired
    private AgentConfigService mAgentConfigService;



    @RequestMapping("root_web_settle_order")
    public String toAuditUserWithdraw(Model model)
    {
        String pdate = WebRequest.getString("pdate");
        String agentname = WebRequest.getString("agentname");
        String staffname = WebRequest.getString("staffname");
        String currency = WebRequest.getString("currency");
        String no = WebRequest.getString("no");
        String reportid = WebRequest.getString("reportid");

        model.addAttribute("pdate", StringUtils.getNotEmpty(pdate));
        model.addAttribute("agentname", StringUtils.getNotEmpty(agentname));
        model.addAttribute("staffname", StringUtils.getNotEmpty(staffname));
        model.addAttribute("currency", StringUtils.getNotEmpty(currency));
        model.addAttribute("no", StringUtils.getNotEmpty(no));
        model.addAttribute("reportid", StringUtils.getNotEmpty(reportid));
        CryptoCurrency.addModel(model);


        UserInfo agentInfo = AgentAccountHelper.getAdminLoginInfo();
        AgentConfigInfo configInfo = mAgentConfigService.findByAgentId(false, agentInfo.getId(), AgentConfigInfo.AgentConfigType.COIN_DEFI_SETLLE_WITHDRAW);
        boolean isShow =  configInfo.getStatus().equalsIgnoreCase(Status.ENABLE.getKey());

        if(isShow){
            return "admin/agent/web/settle/settle_order_list";
        }else {
            return "admin/agent/web/err";
        }


    }


    @RequestMapping("getWebSettleOrderList")
    @ResponseBody
    public String getDataList()
    {
        long agentid = AgentAccountHelper.getAdminAgentid();

        String time = WebRequest.getString("time");
        String username = WebRequest.getString("username");
//        String agentname = WebRequest.getString("agentname");
        String staffname = WebRequest.getString("staffname");

        String systemOrderno = WebRequest.getString("systemOrderno");

        String transferNo = WebRequest.getString("transferNo");
        long reportid = WebRequest.getLong("reportid");

        OrderTxStatus settleStatus = OrderTxStatus.getType(WebRequest.getString("settleStatus"));

        String outTradeNo = WebRequest.getString("outTradeNo");

        String beneficiaryAccount = WebRequest.getString("beneficiaryAccount");

        OrderTxStatus txStatus = OrderTxStatus.getType(WebRequest.getString("txStatus"));
        ICurrencyType currencyType = ICurrencyType.getType(WebRequest.getString("currencyType"));

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
//        long agentid = mUserQueryManager.findUserid(agentname);

        long staffid = mUserQueryManager.findUserid(staffname);
        // 如果是员工登陆，则员工只能查看自己下级会员的数据
        UserInfo currentLoginInfo = AgentAccountHelper.getAdminLoginInfo();
        if(UserInfo.UserType.STAFF.getKey().equalsIgnoreCase(currentLoginInfo.getType
                ()))
        {
            staffid = currentLoginInfo.getId();
        }

        RowPager<SettleOrderInfo> rowPager = settleOrderService.queryScrollPageByUser(pageVo, userid, agentid, staffid,
                currencyType, systemOrderno, outTradeNo, txStatus , null,beneficiaryAccount,transferNo,settleStatus ,reportid);

        template.setData(rowPager);
        return template.toJSONString();
    }


//    @RequestMapping("updateWebSettleOrderStatus")
//    @ResponseBody
//    public String updateWebSettleOrderStatus()
//    {
//
//        String orderno = WebRequest.getString("orderno");
//        String action = WebRequest.getString("action");
//        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
//
//        if(StringUtils.isEmpty(orderno) || !RegexUtils.isBankName(orderno))
//        {
//            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
//            return apiJsonTemplate.toJSONString();
//        }
//
//        SettleOrderInfo orderInfo = settleOrderService.findByOrderno(orderno);
//        if(orderInfo == null)
//        {
//            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
//            return apiJsonTemplate.toJSONString();
//        }
//
//        OrderTxStatus txStatus = OrderTxStatus.getType(orderInfo.getStatus());
//        if(txStatus != OrderTxStatus.WAITING)
//        {
//            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
//            return apiJsonTemplate.toJSONString();
//        }
//
//        if("passOrder".equalsIgnoreCase(action))
//        {
//            Date createtime = new Date();
//            settleOrderService.updateTxStatus(orderno, OrderTxStatus.REALIZED, createtime, null, null,null);
//        }
//        else if("refuseOrder".equalsIgnoreCase(action))
//        {
//            Date createtime = new Date();
//            settleOrderService.updateTxStatus(orderno, OrderTxStatus.FAILED, createtime, null, null,null);
//        }
//        else
//        {
//            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
//        }
//
//        return apiJsonTemplate.toJSONString();
//    }




    @RequestMapping("addSettleWithdrawReport")
    @ResponseBody
    public String addSettleWithdrawReport()
    {
        long agentid = AgentAccountHelper.getAdminAgentid();

        String orderno = WebRequest.getString("orderno");
        String stringRemark = WebRequest.getString("remark");
        String StringSelected = WebRequest.getString("StringSelected");



        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
        // 没有代理无法查询
        if(agentid <= 0)
        {
            apiJsonTemplate.setData(RowPager.getEmptyRowPager());
            return apiJsonTemplate.toJSONString();
        }


        // 如果是员工登陆，则员工只能查看自己下级会员的数据
        UserInfo currentLoginInfo = AgentAccountHelper.getAdminLoginInfo();
        if(UserInfo.UserType.STAFF.getKey().equalsIgnoreCase(currentLoginInfo.getType
                ()))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }


        if(StringUtils.isEmpty(orderno))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if(StringUtils.isEmpty(stringRemark))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        SettleOrderInfo  recordInfo = settleOrderService.findByOrderno(orderno);
       // JSONObject remark = JSONObject.parseObject(stringRemark);

        JSONArray remark = JSONArray.parseArray(stringRemark) ;

        List<SettleOrderInfo> list = JSONArray.parseArray( StringSelected,SettleOrderInfo.class);
        for(SettleOrderInfo model : list){
            if(model.getAgentid()!= agentid){
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
                return apiJsonTemplate.toJSONString();
            }
        }
        settleWithdrawOrderReportService.addOrder( list , remark );



        return apiJsonTemplate.toJSONString();
    }






}

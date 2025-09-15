package com.inso.modules.admin.agent.controller.paychannel;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.CollectionUtils;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.admin.agent.AgentAuthManager;
import com.inso.modules.admin.helper.AdminAccountHelper;
import com.inso.modules.admin.helper.AgentAccountHelper;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.model.UserSecret;
import com.inso.modules.passport.user.service.UserSecretService;
import com.inso.modules.passport.user.service.UserService;
import com.inso.modules.paychannel.ChannelErrorResult;
import com.inso.modules.paychannel.model.ChannelInfo;
import com.inso.modules.paychannel.model.ChannelStatus;
import com.inso.modules.paychannel.model.ChannelType;
import com.inso.modules.paychannel.model.PayProductType;
import com.inso.modules.paychannel.service.ChannelService;
import com.inso.modules.web.SystemRunningMode;
import com.inso.modules.web.eventlog.model.WebEventLogType;
import com.inso.modules.web.eventlog.service.WebEventLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.List;

/**
 * 用户系统关注列表
 */
@Controller
@RequestMapping("/alibaba888/agent")
public class PayChannelController {

    @Autowired
    private ChannelService mChannelService;

    @Autowired
    private UserService mUserService;

    @Autowired
    private UserSecretService userSecretService;

    @Autowired
    private WebEventLogService webEventLogService;

    @Autowired
    private AgentAuthManager mAgentAuthManager;

    @RequestMapping("toEditPayChannelPage")
    public String toEditPayChannelPage(Model model)
    {
        //已检查权限
        boolean isShowAction = AdminAccountHelper.isNy4timeAdminOrDEV();
        long channelid = WebRequest.getLong("channelid");
        if(channelid > 0)
        {
            ChannelInfo channelInfo = mChannelService.findById(false, channelid);
            UserInfo agentUser = mUserService.findByUsername(false, channelInfo.getRemark());
            if(agentUser==null){
                return "admin/agent/err";
            }
            if(!mAgentAuthManager.verifyAgentData(agentUser.getId())){
                return "admin/agent/err";
            }

            model.addAttribute("entity", channelInfo);
            model.addAttribute("paymentInfo", channelInfo.getSecretInfo());
        }
        ChannelStatus[] values = ChannelStatus.values();
        model.addAttribute("statusArray", values);

        model.addAttribute("isShowAction", isShowAction + StringUtils.getEmpty());

        CryptoNetworkType.addFreemarkerModel(model);
        ICurrencyType.addModel(model);

        return "admin/agent/paychannel/channel_edit";
    }


    @RequestMapping("editPayChannel")
    @ResponseBody
    public String editPayChannel()
    {

        long channelid = WebRequest.getLong("id");
        long sort = WebRequest.getLong("sort");
        if(sort<1){
            sort=100;
        }

        String name = WebRequest.getString("name");
        String googleCode = WebRequest.getString("googleCode");
        JSONObject secretInfo = WebRequest.getJSON("secretInfo");

        String remark = WebRequest.getString("remark");

        //String typeString = WebRequest.getString("type");
        ChannelType type = ChannelType.PAYOUT;

        String statusString = WebRequest.getString("status");
        ChannelStatus status = ChannelStatus.getType(statusString);

        PayProductType productType = PayProductType.COIN;


        ApiJsonTemplate api = new ApiJsonTemplate();
        boolean isAgentLogin = AgentAccountHelper.isAgentLogin();
        if(!isAgentLogin){
            api.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return api.toJSONString();
        }

        if(StringUtils.isEmpty(googleCode))
        {
            api.setJsonResult(SystemErrorResult.ERR_VERIFY_IMAGE_CODE);
            return api.toJSONString();
        }

        UserSecret userSecretInfo = userSecretService.find(false, AgentAccountHelper.getUsername());

        if( userSecretInfo.getGoogleKey() == null || !userSecretInfo.checkGoogle(api, googleCode, false))
        {
            api.setJsonResult(SystemErrorResult.ERR_VERIFY_IMAGE_CODE);
            return api.toJSONString();
        }

        if(productType == PayProductType.COIN || productType == PayProductType.FIAT_2_STABLE_COIN)
        {
            if(productType == PayProductType.FIAT_2_STABLE_COIN)
            {
                if(!(SystemRunningMode.isBCMode() || SystemRunningMode.isFundsMode()))
                {
                    api.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
                    return api.toJSONString();
                }
            }

            // 验证代理名称
            UserInfo  getAdminAgentInfo = AgentAccountHelper.getAdminLoginInfo();
            name= getAdminAgentInfo.getName()+"_"+secretInfo.getString("networkType");
            secretInfo.put("agentname", getAdminAgentInfo.getName());
            remark = getAdminAgentInfo.getName();

        }else{
            api.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return api.toJSONString();
        }

        if( productType == null)//StringUtils.isEmpty(name) || name.length() > 50 ||
        {
            api.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return api.toJSONString();
        }

        if(secretInfo == null || secretInfo.isEmpty())
        {
            api.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return api.toJSONString();
        }



        if(type == null)
        {
            api.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return api.toJSONString();
        }

        if(type == ChannelType.PAYOUT)
        {
            if(!productType.isEnablePayout())
            {
                api.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "当前产品类型不支持出款!");
                return api.toJSONString();
            }

        }
        else
        {
            if(!productType.isEnablePayin())
            {
                api.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "当前产品类型不支持收款!");
                return api.toJSONString();
            }
        }

        if(status == null)
        {
            api.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return api.toJSONString();
        }

        if(!StringUtils.isEmpty(remark) && remark.length() > 500)
        {
            api.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return api.toJSONString();
        }

        if(channelid > 0)
        {
            if(ChannelStatus.ENABLE == status)
            {
                List<ChannelInfo> list = mChannelService.queryOnlineList(false, type, productType, null);
                if(CollectionUtils.isEmpty(list) && list.size() > 5)
                {
                    api.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "最大启动通道为5个!");
                    return api.toJSONString();
                }
            }
            ChannelInfo channelInfo = mChannelService.findById(false, channelid);

            if(productType == PayProductType.COIN)
            {
                String privateKey = "accountPrivateKey";
                String privateValue = secretInfo.getString(privateKey);
                if(StringUtils.isEmpty(privateValue))
                {
                    JSONObject sysSecretRemark = channelInfo.getSecretInfo();
                    privateValue = sysSecretRemark.getString(privateKey);
                }
                secretInfo.put(privateKey , privateValue);
            }

            mChannelService.updateInfo(channelInfo, name, secretInfo, status, remark ,sort, null, null);
        }
        else
        {
            mChannelService.add(name, secretInfo, productType, ChannelStatus.DISABLE, type, null, remark,sort, null, null);
        }

        if(type == ChannelType.PAYIN)
        {
            mChannelService.queryOnlineList(true, type, null, null);
        }
        else
        {
            mChannelService.queryOnlineList(true, type, productType, null);
        }

        String address = secretInfo.getString("accountAddress");
        webEventLogService.addAgentLog(WebEventLogType.PAY_CHANNEL_EDIT, address);

        return api.toJSONString();
    }



    @RequestMapping("root_pay_channel_config")
    public String toSystemSystemUserFollowListPage(Model model)
    {
        boolean isShowAction = AdminAccountHelper.isNy4timeAdminOrDEV();

        ChannelStatus[] values = ChannelStatus.values();
        model.addAttribute("statusArray", values);

        model.addAttribute("isShowAction", isShowAction + StringUtils.getEmpty());
        return "admin/agent/paychannel/channel_list";
    }


    @RequestMapping("getPayChannelList")
    @ResponseBody
    public String getPayChannelList()
    {
        long agentid = AgentAccountHelper.getAdminAgentid();

        String typeString = WebRequest.getString("type");
        ChannelType type = ChannelType.getType(typeString);

        String statusString = WebRequest.getString("status");
        ChannelStatus status = ChannelStatus.getType(statusString);
        ChannelStatus ignoreStatus = null;

        if(!AdminAccountHelper.isNy4timeAdminOrDEV())
        {
            ignoreStatus = ChannelStatus.HIDDEN;
        }

        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));

        ApiJsonTemplate api = new ApiJsonTemplate();
        // 没有代理无法查询
        if(agentid <= 0)
        {
            api.setData(RowPager.getEmptyRowPager());
            return api.toJSONString();
        }

        boolean isAgentLogin = AgentAccountHelper.isAgentLogin();
        if(!isAgentLogin){
            api.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return api.toJSONString();
        }
        UserInfo serInfo = AgentAccountHelper.getAdminLoginInfo();


        RowPager<ChannelInfo> rowPager = mChannelService.queryScrollPage(pageVo, status, ignoreStatus, type,serInfo.getName());
        api.setData(rowPager);
        return api.toJSONString();
    }


//    @RequestMapping("deletePayChannel")
//    @ResponseBody
//    public String deletePayChannel()
//    {
//        //已检查权限
//        long channelid = WebRequest.getLong("channelid");
//        ApiJsonTemplate api = new ApiJsonTemplate();
//        boolean isAgentLogin = AgentAccountHelper.isAgentLogin();
//        if(!isAgentLogin){
//            api.setJsonResult(SystemErrorResult.ERR_PARAMS);
//            return api.toJSONString();
//        }
//        UserInfo serInfo = AgentAccountHelper.getAdminLoginInfo();
//
//        ChannelInfo channelInfo = mChannelService.findById(false, channelid);
//        if(channelInfo == null)
//        {
//            api.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
//            return api.toJSONString();
//        }
//        UserInfo agentUser = mUserService.findByUsername(false, channelInfo.getRemark());
//        if(agentUser==null){
//            api.setJsonResult(SystemErrorResult.ERR_SYSTEM);
//            return api.toJSONString();
//        }
//        if(!mAgentAuthManager.verifyAgentData(agentUser.getId())){
//            api.setJsonResult(SystemErrorResult.ERR_SYSTEM);
//            return api.toJSONString();
//        }
//
//
//        if(!channelInfo.getRemark().equalsIgnoreCase(serInfo.getName())){
//            api.setJsonResult(SystemErrorResult.ERR_PARAMS);
//            return api.toJSONString();
//        }
//
//        ChannelStatus status = ChannelStatus.getType(channelInfo.getStatus());
//        if(status != ChannelStatus.DISABLE)
//        {
//            api.setJsonResult(ChannelErrorResult.ERR_STATUS);
//            return api.toJSONString();
//        }
//
//        ChannelType channelType = ChannelType.getType(channelInfo.getType());
//
//        mChannelService.delete(channelInfo);
//        mChannelService.queryOnlineList(true, channelType, channelInfo.getProduct());
//        return api.toJSONString();
//    }

}

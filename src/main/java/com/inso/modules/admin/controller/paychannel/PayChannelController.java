package com.inso.modules.admin.controller.paychannel;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.CollectionUtils;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.admin.helper.AdminAccountHelper;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.UserService;
import com.inso.modules.paychannel.ChannelErrorResult;
import com.inso.modules.paychannel.model.ChannelInfo;
import com.inso.modules.paychannel.model.ChannelStatus;
import com.inso.modules.paychannel.model.ChannelType;
import com.inso.modules.paychannel.model.PayProductType;
import com.inso.modules.paychannel.service.ChannelService;
import com.inso.modules.web.SystemRunningMode;
import org.apache.shiro.authz.annotation.RequiresPermissions;
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
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class PayChannelController {

    @Autowired
    private ChannelService mChannelService;

    @Autowired
    private UserService mUserService;

    @RequiresPermissions("root_pay_channel_config_edit")
    @RequestMapping("toEditPayChannelPage")
    public String toEditPayChannelPage(Model model)
    {
        boolean isShowAction = AdminAccountHelper.isNy4timeAdminOrDEV();
        long channelid = WebRequest.getLong("channelid");
        if(channelid > 0)
        {
            ChannelInfo channelInfo = mChannelService.findById(true, channelid);
            model.addAttribute("entity", channelInfo);
            model.addAttribute("paymentInfo", channelInfo.getSecretInfo());
        }
        ChannelStatus[] values = ChannelStatus.values();
        model.addAttribute("statusArray", values);

        model.addAttribute("isShowAction", isShowAction + StringUtils.getEmpty());

        CryptoNetworkType.addFreemarkerModel(model);
        ICurrencyType.addModel(model);

        return "admin/paychannel/channel_edit";
    }

    @RequiresPermissions("root_pay_channel_config_edit")
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
        JSONObject secretInfo = WebRequest.getJSON("secretInfo");
        String remark = WebRequest.getString("remark");

        String typeString = WebRequest.getString("type");
        ChannelType type = ChannelType.getType(typeString);

        String statusString = WebRequest.getString("status");
        ChannelStatus status = ChannelStatus.getType(statusString);
        ICurrencyType currencyTypeType = ICurrencyType.getType(WebRequest.getString("currencyType"));

        PayProductType productType = PayProductType.getType(WebRequest.getString("productType"));

        BigDecimal feerate = WebRequest.getBigDecimal("feerate");
        BigDecimal extraFeemoney = WebRequest.getBigDecimal("extraFeemoney");

        ApiJsonTemplate api = new ApiJsonTemplate();

        if(feerate != null && (feerate.compareTo(BigDecimal.ZERO) < 0 || feerate.compareTo(BigDecimal.ONE) > 0))
        {
            api.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return api.toJSONString();
        }

        if(type == ChannelType.PAYIN)
        {
            //currencyTypeType = ICurrencyType.getSupportCurrency();
            currencyTypeType = null;
        }

        if(productType != PayProductType.TAJPAY)
        {
            currencyTypeType = null;
        }

        if(StringUtils.isEmpty(name) || name.length() > 50 || productType == null)
        {
            api.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return api.toJSONString();
        }

        if(secretInfo == null || secretInfo.isEmpty())
        {
            api.setJsonResult(SystemErrorResult.ERR_PARAMS);
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
            String agentname = secretInfo.getString("agentname");
            if(StringUtils.isEmpty(agentname))
            {
                api.setJsonResult(SystemErrorResult.ERR_PARAMS);
                return api.toJSONString();
            }

            UserInfo userInfo = mUserService.findByUsername(false, agentname);
            if(userInfo == null || !userInfo.getType().equalsIgnoreCase(UserInfo.UserType.AGENT.getKey()))
            {
                api.setJsonResult(SystemErrorResult.ERR_PARAMS);
                return api.toJSONString();
            }
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
            ChannelInfo channelInfo = mChannelService.findById(true, channelid);

            if(ChannelStatus.ENABLE == status)
            {
                List<ChannelInfo> list = mChannelService.queryOnlineList(false, type, productType, ICurrencyType.getType(channelInfo.getCurrencyType()));
                if(CollectionUtils.isEmpty(list) && list.size() > 5)
                {
                    api.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "最大启动通道为5个!");
                    return api.toJSONString();
                }
            }


            if(productType == PayProductType.COIN || productType == PayProductType.FIAT_2_STABLE_COIN)
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

            mChannelService.updateInfo(channelInfo, name, secretInfo, status, remark ,sort, feerate, extraFeemoney);
        }
        else
        {
            mChannelService.add(name, secretInfo, productType, ChannelStatus.DISABLE, type, currencyTypeType, remark,sort, feerate, extraFeemoney);
        }

        mChannelService.queryOnlineList(true, ChannelType.PAYIN, null, null);
        return api.toJSONString();
    }


    @RequiresPermissions("root_pay_channel_config_list")
    @RequestMapping("root_pay_channel_config")
    public String toSystemSystemUserFollowListPage(Model model)
    {
        boolean isShowAction = AdminAccountHelper.isNy4timeAdminOrDEV();

        ChannelStatus[] values = ChannelStatus.values();
        model.addAttribute("statusArray", values);

        model.addAttribute("isShowAction", isShowAction + StringUtils.getEmpty());
        return "admin/paychannel/channel_list";
    }

    @RequiresPermissions("root_pay_channel_config_list")
    @RequestMapping("getPayChannelList")
    @ResponseBody
    public String getPayChannelList()
    {
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

        RowPager<ChannelInfo> rowPager = mChannelService.queryScrollPage(pageVo, status, ignoreStatus, type,null);
        api.setData(rowPager);
        return api.toJSONString();
    }

    @RequiresPermissions("root_pay_channel_config_delete")
    @RequestMapping("deletePayChannel")
    @ResponseBody
    public String deletePayChannel()
    {
        long channelid = WebRequest.getLong("channelid");
        ApiJsonTemplate api = new ApiJsonTemplate();

        ChannelInfo channelInfo = mChannelService.findById(false, channelid);
        if(channelInfo == null)
        {
            api.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
            return api.toJSONString();
        }

        ChannelStatus status = ChannelStatus.getType(channelInfo.getStatus());
        if(status != ChannelStatus.DISABLE)
        {
            api.setJsonResult(ChannelErrorResult.ERR_STATUS);
            return api.toJSONString();
        }

        ChannelType channelType = ChannelType.getType(channelInfo.getType());
        ICurrencyType currencyType = ICurrencyType.getType(channelInfo.getCurrencyType());

        mChannelService.delete(channelInfo);
        mChannelService.queryOnlineList(true, channelType, channelInfo.getProduct(), currencyType);
        return api.toJSONString();
    }

}

package com.inso.modules.admin.controller.coin;

import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.BigDecimalUtils;
import com.inso.framework.utils.RegexUtils;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.admin.helper.AdminAccountHelper;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.coin.core.model.MyDimensionType;
import com.inso.modules.coin.withdraw.model.CoinWithdrawChannel;
import com.inso.modules.coin.withdraw.service.CoinWithdrawChannelService;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.logical.UserQueryManager;
import com.inso.modules.passport.user.model.UserInfo;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class WithdrawChannelController {


    @Autowired
    private UserQueryManager mUserQueryManager;

    @Autowired
    private CoinWithdrawChannelService mWithdrawChannelService;

    @RequiresPermissions("root_coin_withdraw_channel_list")
    @RequestMapping("root_coin_withdraw_channel")
    public String toList(Model model, HttpServletRequest request)
    {
//        CryptoNetworkType[] networkTypeArr = CryptoNetworkType.values();
//        model.addAttribute("networkTypeArr", networkTypeArr);
        CryptoNetworkType.addFreemarkerModel(model);

        MyDimensionType[] dimensionTypeArr = MyDimensionType.values();
        model.addAttribute("dimensionTypeArr", dimensionTypeArr);

        boolean isAdmin = AdminAccountHelper.isNy4timeAdminOrDEV();
        model.addAttribute("isAdmin", isAdmin + StringUtils.getEmpty());

        return "admin/coin/withdraw/coin_withdraw_channel_list";
    }

    @RequiresPermissions("root_coin_withdraw_channel_list")
    @RequestMapping("getCoinWithdrawChannelList")
    @ResponseBody
    public String getCoinWithdrawChannelList()
    {
        String time = WebRequest.getString("time");
        String agentname = WebRequest.getString("agentname");

        CryptoNetworkType networkType = CryptoNetworkType.getType(WebRequest.getString("networkType"));
        Status status = Status.getType(WebRequest.getString("status"));
        MyDimensionType dimensionType = MyDimensionType.getType(WebRequest.getString("dimensionType"));

        ApiJsonTemplate template = new ApiJsonTemplate();

        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
        pageVo.parseTime(time);

        long agentid = mUserQueryManager.findUserid(agentname);
        String key = null;
        if(agentid > 0)
        {
            key = agentname + StringUtils.getEmpty();
        }

        RowPager<CoinWithdrawChannel> rowPager = mWithdrawChannelService.queryScrollPage(pageVo, key, networkType, status);

        template.setData(rowPager);
        return template.toJSONString();
    }

    @RequiresPermissions("root_coin_withdraw_channel_edit")
    @RequestMapping("toCoinWithdrawChannelEditPage")
    public String toCoinWithdrawChannelEditPage(Model model, HttpServletRequest request)
    {
        long id = WebRequest.getLong("id");
        if(id > 0)
        {
            CoinWithdrawChannel entity = mWithdrawChannelService.findById(id, true);
            model.addAttribute("entity", entity);
        }

        CryptoNetworkType.addFreemarkerModel(model);
//        CryptoNetworkType[] networkTypeArr = CryptoNetworkType.values();
//        model.addAttribute("networkTypeArr", networkTypeArr);

        MyDimensionType[] dimensionTypeArr = MyDimensionType.values();
        model.addAttribute("dimensionTypeArr", dimensionTypeArr);

        return "admin/coin/withdraw/coin_withdraw_channel_edit";
    }

    @RequiresPermissions("root_coin_withdraw_channel_edit")
    @RequestMapping("updateCoinWithdrawChannelInfo")
    @ResponseBody
    public String updateCoinWithdrawChannelInfo(Model model, HttpServletRequest request)
    {
        long id = WebRequest.getLong("id");

        String key = WebRequest.getString("key");
//        MyDimensionType dimensionType = MyDimensionType.getType(WebRequest.getString("dimensionType"));
        MyDimensionType dimensionType = MyDimensionType.AGENT;
        CryptoNetworkType networkType = CryptoNetworkType.getType(WebRequest.getString("networkType"));

        String triggerPrivateKey = WebRequest.getString("triggerPrivateKey");
        String triggerAddress = WebRequest.getString("triggerAddress");
        BigDecimal gasLimit = WebRequest.getBigDecimal("gasLimit");

        BigDecimal feeRate = WebRequest.getBigDecimal("feeRate");
        BigDecimal singleFeemoney = WebRequest.getBigDecimal("singleFeemoney");

        Status status = Status.getType(WebRequest.getString("status"));

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(status == null || dimensionType == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if(gasLimit == null || gasLimit.compareTo(BigDecimal.ZERO) <= 0)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

//        if(dimensionType == MyDimensionType.PROJECT && !MyEnvironment.isDev())
//        {
//            // 超级管理员才能操作
//            String admin = CoreAdminHelper.getAdminName();
//            if(!Admin.DEFAULT_ADMIN_NY4TIME.equalsIgnoreCase(admin))
//            {
//                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
//                return apiJsonTemplate.toJSONString();
//            }
//        }

//        if(dimensionType == MyDimensionType.PLATFORM)
//        {
//            shareRatio = BigDecimal.ZERO;
//        }

        if(dimensionType == MyDimensionType.AGENT)
        {
            UserInfo agentInfo = mUserQueryManager.findUserInfo(key);
            if(agentInfo == null)
            {
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
                return apiJsonTemplate.toJSONString();
            }

            if(!UserInfo.UserType.AGENT.getKey().equalsIgnoreCase(agentInfo.getType()))
            {
                apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "当前用户名不是代理用户!");
                return apiJsonTemplate.toJSONString();
            }
        }
        else
        {
            key = dimensionType.getKey();
        }

        if(feeRate == null || feeRate.compareTo(BigDecimal.ZERO) < 0 || feeRate.compareTo(BigDecimalUtils.DEF_1) >= 1)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if(singleFeemoney == null || singleFeemoney.compareTo(BigDecimal.ZERO) < 0)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if(!StringUtils.isEmpty(triggerAddress) && !RegexUtils.isLetterDigit(triggerAddress))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

//        receivAddress = StringUtils.getNotEmpty(receivAddress);

        if(id > 0)
        {
            CoinWithdrawChannel entity = mWithdrawChannelService.findById(id, true);
            mWithdrawChannelService.updateInfo(entity, triggerPrivateKey, triggerAddress, gasLimit, feeRate, singleFeemoney, status);
        }
        else
        {
            if(!StringUtils.isEmpty(triggerPrivateKey) && !RegexUtils.isLetterDigit(triggerPrivateKey))
            {
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
                return apiJsonTemplate.toJSONString();
            }
            mWithdrawChannelService.add(key, triggerPrivateKey, triggerAddress, networkType, gasLimit, feeRate, singleFeemoney);
        }
        return apiJsonTemplate.toJSONString();
    }



}

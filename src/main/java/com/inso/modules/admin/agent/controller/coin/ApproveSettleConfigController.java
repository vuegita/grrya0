package com.inso.modules.admin.agent.controller.coin;

import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.context.MyEnvironment;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RegexUtils;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.admin.core.helper.CoreAdminHelper;
import com.inso.modules.admin.core.model.Admin;
import com.inso.modules.admin.helper.AdminAccountHelper;
import com.inso.modules.admin.helper.AgentAccountHelper;
import com.inso.modules.coin.core.model.CoinSettleConfig;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.coin.core.model.MyDimensionType;
import com.inso.modules.coin.core.service.CoinSettleConfigService;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.logical.UserQueryManager;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.model.UserSecret;
import com.inso.modules.passport.user.service.UserSecretService;
import com.inso.modules.web.eventlog.model.WebEventLogType;
import com.inso.modules.web.eventlog.service.WebEventLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

@Controller
@RequestMapping("/alibaba888/agent")
public class ApproveSettleConfigController {


    @Autowired
    private UserQueryManager mUserQueryManager;

    @Autowired
    private CoinSettleConfigService mSettleConfigService;

    @Autowired
    private UserSecretService mUserSecretService;

    @Autowired
    private WebEventLogService webEventLogService;

    @RequestMapping("root_coin_crypto_settle_config")
    public String toList(Model model, HttpServletRequest request)
    {
//        CryptoNetworkType[] networkTypeArr = CryptoNetworkType.values();
//        model.addAttribute("networkTypeArr", networkTypeArr);
        CryptoNetworkType.addFreemarkerModel(model);

        MyDimensionType[] dimensionTypeArr = MyDimensionType.values();
        model.addAttribute("dimensionTypeArr", dimensionTypeArr);

        boolean isAdmin = AdminAccountHelper.isNy4timeAdminOrDEV();
        model.addAttribute("isAdmin", isAdmin + StringUtils.getEmpty());

        return "admin/agent/coin/coin_crypto_settle_config_list";
    }


    @RequestMapping("getCoinCryptoSettleConfigList")
    @ResponseBody
    public String getCoinCryptoSettleConfigList()
    {
        //long agentid = AgentAccountHelper.getAdminAgentid();

        String time = WebRequest.getString("time");
       // String agentname = WebRequest.getString("agentname");

        CryptoNetworkType networkType = CryptoNetworkType.getType(WebRequest.getString("networkType"));
        Status status = Status.getType(WebRequest.getString("status"));
       // MyDimensionType dimensionType = MyDimensionType.getType(WebRequest.getString("dimensionType"));

        ApiJsonTemplate template = new ApiJsonTemplate();
        // 没有代理无法查询
        if(!AgentAccountHelper.isAgentLogin())
        {
            template.setData(RowPager.getEmptyRowPager());
            return template.toJSONString();
        }

        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
        pageVo.parseTime(time);

//        long agentid = mUserQueryManager.findUserid(agentname);
        UserInfo userInfo=AgentAccountHelper.getAdminLoginInfo();

        String agentname=userInfo.getName();

        RowPager<CoinSettleConfig> rowPager = mSettleConfigService.queryScrollPage(pageVo, agentname, networkType, status, MyDimensionType.AGENT);

        template.setData(rowPager);
        return template.toJSONString();
    }


    @RequestMapping("toCoinCryptoSettleConfigEditPage")
    public String toCoinCryptoSettleConfigEditPage(Model model, HttpServletRequest request)
    {
        long id = WebRequest.getLong("id");
        if(id > 0)
        {
            CoinSettleConfig settleConfig = mSettleConfigService.findById(id);
            model.addAttribute("entity", settleConfig);
        }

        CryptoNetworkType.addFreemarkerModel(model);
//        CryptoNetworkType[] networkTypeArr = CryptoNetworkType.values();
//        model.addAttribute("networkTypeArr", networkTypeArr);

        MyDimensionType[] dimensionTypeArr = MyDimensionType.values();
        model.addAttribute("dimensionTypeArr", dimensionTypeArr);

        return "admin/agent/coin/coin_crypto_settle_config_edit";
    }


    @RequestMapping("updateCoinCryptoSettleConfigInfo")
    @ResponseBody
    public String updateCoinCryptoSettleConfigInfo(Model model, HttpServletRequest request)
    {

        long id = WebRequest.getLong("id");
        String googleCode = WebRequest.getString("googleCode");


        //String key = WebRequest.getString("key");
        MyDimensionType dimensionType = MyDimensionType.AGENT;
        CryptoNetworkType networkType = CryptoNetworkType.getType(WebRequest.getString("networkType"));
        String receivAddress = WebRequest.getString("receivAddress");

        Status status = Status.getType(WebRequest.getString("status"));

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
        // 没有代理无法查询
        if(!AgentAccountHelper.isAgentLogin())
        {
            apiJsonTemplate.setData(RowPager.getEmptyRowPager());
            return apiJsonTemplate.toJSONString();
        }

        try {
            UserInfo userInfo = AgentAccountHelper.getAdminLoginInfo();

            if(StringUtils.isEmpty(googleCode))
            {
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_VERIFY_IMAGE_CODE);
                return apiJsonTemplate.toJSONString();
            }

            UserSecret secretInfo = mUserSecretService.find(false, userInfo.getName());

            if( secretInfo.getGoogleKey() == null || !secretInfo.checkGoogle(apiJsonTemplate, googleCode, false))
            {
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_VERIFY_IMAGE_CODE);
                return apiJsonTemplate.toJSONString();
            }

            String key=userInfo.getName();

            if(status == null || dimensionType == null)
            {
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
                return apiJsonTemplate.toJSONString();
            }

            if(dimensionType == MyDimensionType.PROJECT && !MyEnvironment.isDev())
            {
                // 超级管理员才能操作
                String admin = CoreAdminHelper.getAdminName();
                if(!Admin.DEFAULT_ADMIN_NY4TIME.equalsIgnoreCase(admin))
                {
                    apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
                    return apiJsonTemplate.toJSONString();
                }
            }

            UserInfo agentInfo = mUserQueryManager.findUserInfo(key);
            if(agentInfo == null)
            {
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
                return apiJsonTemplate.toJSONString();
            }

            if(StringUtils.isEmpty(receivAddress) || !RegexUtils.isLetterDigit(receivAddress) || receivAddress.length() > 100)
            {
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
                return apiJsonTemplate.toJSONString();
            }

            receivAddress = StringUtils.getNotEmpty(receivAddress);

            if(id > 0)
            {
    //            CoinSettleConfig settleConfig = mSettleConfigService.findById(id);
    //            mSettleConfigService.updateInfo(settleConfig, receivAddress, null, status);
            }
            else
            {
                BigDecimal shareRatio = BigDecimal.ZERO;
                mSettleConfigService.add(key, dimensionType, receivAddress, networkType, shareRatio, status);
            }
        } finally {
            webEventLogService.addAgentLog(WebEventLogType.COIN_EDIT_SETTLE_ADDRESS, receivAddress);
        }

        return apiJsonTemplate.toJSONString();
    }



}

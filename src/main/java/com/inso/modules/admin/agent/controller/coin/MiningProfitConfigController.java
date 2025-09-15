package com.inso.modules.admin.agent.controller.coin;

import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.BigDecimalUtils;
import com.inso.framework.utils.RowPager;
import com.inso.modules.admin.agent.AgentAuthManager;
import com.inso.modules.admin.helper.AgentAccountHelper;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.coin.core.model.ProfitConfigInfo;
import com.inso.modules.coin.core.service.ProfitConfigService;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.logical.UserQueryManager;
import com.inso.modules.passport.user.model.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

@Controller
@RequestMapping("/alibaba888/agent")
public class MiningProfitConfigController {

//    @Autowired
//    private ConfigService mConfigService;
//
//    @Autowired
//    private UserService userService;
//
//    @Autowired
//    private UserAttrService mUserAttrService;
//
//    @Autowired
//    private ApproveAuthService mApproveAuthService;
//
    @Autowired
    private UserQueryManager mUserQueryManager;
//
//    @Autowired
//    private TransferOrderService mTransferOrderService;


    @Autowired
    private ProfitConfigService mProfitConfigService;

    @Autowired
    private AgentAuthManager mAgentAuthManager;


    @RequestMapping("root_coin_core_mining_profit_level_config")
    public String toList(Model model, HttpServletRequest request)
    {
        CryptoNetworkType.addFreemarkerModel(model);
        CryptoCurrency.addModel(model);
        ProfitConfigInfo.ProfitType.addModel(model);

        return "admin/agent/coin/core/coin_ba_mining_profit_config_list";
    }

    @RequestMapping("getCoinCoreMiningProfitConfigList")
    @ResponseBody
    public String getDataList()
    {
        String time = WebRequest.getString("time");

        ProfitConfigInfo.ProfitType profitType = ProfitConfigInfo.ProfitType.getType(WebRequest.getString("profitType"));
        CryptoCurrency currency = CryptoCurrency.getType(WebRequest.getString("currencyType"));
        Status status = Status.getType(WebRequest.getString("status"));

        ApiJsonTemplate template = new ApiJsonTemplate();

        if(!AgentAccountHelper.isAgentLogin())
        {
            return template.toJSONString();
        }

        long agentid = AgentAccountHelper.getAdminAgentid();

        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
        pageVo.parseTime(time);

        RowPager<ProfitConfigInfo> rowPager = mProfitConfigService.queryScrollPage(pageVo, agentid, profitType, currency, status);

        template.setData(rowPager);
        return template.toJSONString();
    }

    @RequestMapping("toCoinCoreMiningProfitConfigEditPage")
    public String toEdigPage(Model model, HttpServletRequest request)
    {
        //已检查权限
        long id = WebRequest.getLong("id");
        if(id > 0)
        {
            ProfitConfigInfo entity = mProfitConfigService.findById(id);
            if(entity!=null){
                if(!mAgentAuthManager.verifyAgentData(entity.getAgentid())){
                    return "admin/agent/err";
                }
            }

            model.addAttribute("entity", entity);
        }

        CryptoNetworkType.addFreemarkerModel(model);
        CryptoCurrency.addModel(model);
        ProfitConfigInfo.ProfitType.addModel(model);

        return "admin/agent/coin/core/coin_ba_mining_profit_config_edit";
    }

    @RequestMapping("updateCoinCoreMiningProfitConfig")
    @ResponseBody
    public String updateInfo(Model model, HttpServletRequest request)
    {
        long id = WebRequest.getLong("id");
        UserInfo userInfo = AgentAccountHelper.getAdminLoginInfo();

        ProfitConfigInfo.ProfitType profitType = ProfitConfigInfo.ProfitType.getType(WebRequest.getString("profitType"));
        CryptoCurrency currency = CryptoCurrency.getType(WebRequest.getString("currencyType"));
        long level = WebRequest.getLong("level");

        BigDecimal minAmount = WebRequest.getBigDecimal("minAmount");
        BigDecimal dailyRate = WebRequest.getBigDecimal("dailyRate");

        Status status = Status.getType(WebRequest.getString("status"));

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();


        if(!AgentAccountHelper.isAgentLogin())
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
            return apiJsonTemplate.toJSONString();
        }

        if(currency == null || status == null || level <= 0 || profitType == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if(profitType.isOnlySupportAdmin())
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
            return apiJsonTemplate.toJSONString();
        }

        if(profitType == ProfitConfigInfo.ProfitType.DEFI_STAKING)
        {
            if(level <= 0 || level > 10)
            {
                apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "等级最大为10! ");
                return apiJsonTemplate.toJSONString();
            }
        }
        else
        {
            if(level > 10)
            {
                apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "等级最大为10! ");
                return apiJsonTemplate.toJSONString();
            }
        }


        if(dailyRate.compareTo(BigDecimal.ZERO) <= 0 || dailyRate.compareTo(BigDecimalUtils.DEF_1) >= 0)
        {
            apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "预期收益范围是 0 - 1 ");
            return apiJsonTemplate.toJSONString();
        }

        if(level <= 1)
        {
            minAmount = BigDecimal.ZERO;
        }

        if(minAmount == null || minAmount.compareTo(BigDecimal.ZERO) < 0)
        {
            apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "最低金额 >= 0 ");
            return apiJsonTemplate.toJSONString();
        }

        if(profitType == ProfitConfigInfo.ProfitType.BIANCE_ACTIVE)
        {
            if(!(currency == CryptoCurrency.USDT))
            {
                apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "币种活动币种只支持 USDT ");
                return apiJsonTemplate.toJSONString();
            }
        }

        try {
            if(id > 0)
            {
                ProfitConfigInfo entity = mProfitConfigService.findById(id);
                if(entity.getLevel() == level)
                {
                    level = -1;
                }
                UserInfo agentUserInfo = AgentAccountHelper.getAgentInfo();
                if(entity.getAgentid() == agentUserInfo.getId()){
                    mProfitConfigService.updateInfo(entity, dailyRate, minAmount, currency, level, status);
                }
            }
            else
            {

                if(userInfo == null || !UserInfo.UserType.AGENT.getKey().equalsIgnoreCase(userInfo.getType()))
                {
                    apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "当前代理不存在或非代理用户! ");
                    return apiJsonTemplate.toJSONString();
                }

                mProfitConfigService.add(userInfo, profitType, currency, level, minAmount, dailyRate, status);
            }
        } catch (Exception e) {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST);
        }
        return apiJsonTemplate.toJSONString();
    }




}

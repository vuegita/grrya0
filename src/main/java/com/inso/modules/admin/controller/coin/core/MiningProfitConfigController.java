package com.inso.modules.admin.controller.coin.core;

import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.BigDecimalUtils;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.admin.helper.AdminAccountHelper;
import com.inso.modules.coin.core.model.ProfitConfigInfo;
import com.inso.modules.coin.core.service.ProfitConfigService;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.common.model.CryptoCurrency;
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


    @RequiresPermissions("root_coin_core_mining_profit_level_config_list")
    @RequestMapping("root_coin_core_mining_profit_level_config")
    public String toList(Model model, HttpServletRequest request)
    {
        boolean isAdmin = AdminAccountHelper.isNy4timeAdminOrDEV();
        model.addAttribute("isSuperAdmin", isAdmin + StringUtils.getEmpty());

        CryptoNetworkType.addFreemarkerModel(model);
        CryptoCurrency.addModel(model);
        ProfitConfigInfo.ProfitType.addModel(model);

        return "admin/coin/core/coin_ba_mining_profit_config_list";
    }

    @RequiresPermissions("root_coin_core_mining_profit_level_config_list")
    @RequestMapping("getCoinCoreMiningProfitConfigList")
    @ResponseBody
    public String getDataList()
    {
        String time = WebRequest.getString("time");
        String agentname = WebRequest.getString("agentname");

        ProfitConfigInfo.ProfitType profitType = ProfitConfigInfo.ProfitType.getType(WebRequest.getString("profitType"));
        CryptoCurrency currency = CryptoCurrency.getType(WebRequest.getString("currencyType"));
        Status status = Status.getType(WebRequest.getString("status"));

        ApiJsonTemplate template = new ApiJsonTemplate();

        long agentid = mUserQueryManager.findUserid(agentname);

        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
        pageVo.parseTime(time);

        RowPager<ProfitConfigInfo> rowPager = mProfitConfigService.queryScrollPage(pageVo, agentid, profitType, currency, status);

        template.setData(rowPager);
        return template.toJSONString();
    }

    @RequiresPermissions("root_coin_core_mining_profit_level_config_edit")
    @RequestMapping("toCoinCoreMiningProfitConfigEditPage")
    public String toEdigPage(Model model, HttpServletRequest request)
    {
        long id = WebRequest.getLong("id");
        if(id > 0)
        {
            ProfitConfigInfo entity = mProfitConfigService.findById(id);
            model.addAttribute("entity", entity);
        }

        CryptoNetworkType.addFreemarkerModel(model);
        CryptoCurrency.addModel(model);
        ProfitConfigInfo.ProfitType.addModel(model);

        return "admin/coin/core/coin_ba_mining_profit_config_edit";
    }

    @RequiresPermissions("root_coin_core_mining_profit_level_config_list")
    @RequestMapping("deleteCoinCoreMiningProfitConfig")
    @ResponseBody
    public String deleteCoinCloudMiningRecordInfo()
    {
        long id = WebRequest.getLong("id");

        ApiJsonTemplate template = new ApiJsonTemplate();

        if(!AdminAccountHelper.isNy4timeAdminOrDEV())
        {
            return template.toJSONString();
        }

        ProfitConfigInfo entity = mProfitConfigService.findById(id);
        if(entity != null)
        {
            mProfitConfigService.deleteById(entity.getId());
        }
        return template.toJSONString();
    }

    @RequiresPermissions("root_coin_core_mining_profit_level_config_edit")
    @RequestMapping("updateCoinCoreMiningProfitConfig")
    @ResponseBody
    public String updateInfo(Model model, HttpServletRequest request)
    {
        long id = WebRequest.getLong("id");
        String agentname = WebRequest.getString("agentname");

        ProfitConfigInfo.ProfitType profitType = ProfitConfigInfo.ProfitType.getType(WebRequest.getString("profitType"));
        CryptoCurrency currency = CryptoCurrency.getType(WebRequest.getString("currencyType"));
        long level = WebRequest.getLong("level");

        BigDecimal minAmount = WebRequest.getBigDecimal("minAmount");
        BigDecimal dailyRate = WebRequest.getBigDecimal("dailyRate");

        Status status = Status.getType(WebRequest.getString("status"));

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();


        if(currency == null || status == null || level <= 0 || profitType == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if(level <= 0 || level > 10)
        {
            apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "等级最大为10! ");
            return apiJsonTemplate.toJSONString();
        }

        if(dailyRate.compareTo(BigDecimal.ZERO) < 0 || dailyRate.compareTo(BigDecimalUtils.DEF_1) >= 0)
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
                mProfitConfigService.updateInfo(entity, dailyRate, minAmount, currency, level, status);
            }
            else
            {

                UserInfo userInfo = mUserQueryManager.findUserInfo(agentname);
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

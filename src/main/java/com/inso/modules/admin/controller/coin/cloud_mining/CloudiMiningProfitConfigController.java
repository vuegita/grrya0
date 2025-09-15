package com.inso.modules.admin.controller.coin.cloud_mining;

import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.BigDecimalUtils;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.admin.helper.AdminAccountHelper;
import com.inso.modules.coin.cloud_mining.model.CloudProfitConfigInfo;
import com.inso.modules.coin.cloud_mining.service.CloudProfitConfigService;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.common.model.Status;
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
public class CloudiMiningProfitConfigController {

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
//    @Autowired
//    private UserQueryManager mUserQueryManager;
//
//    @Autowired
//    private TransferOrderService mTransferOrderService;


    @Autowired
    private CloudProfitConfigService mProfitConfigService;


    @RequiresPermissions("root_coin_cloud_mining_profit_level_config_list")
    @RequestMapping("root_coin_cloud_mining_profit_level_config")
    public String toList(Model model, HttpServletRequest request)
    {
        boolean isAdmin = AdminAccountHelper.isNy4timeAdminOrDEV();
        model.addAttribute("isSuperAdmin", isAdmin + StringUtils.getEmpty());

        CryptoNetworkType.addFreemarkerModel(model);
        CryptoCurrency.addModel(model);
        return "admin/coin/cloud_mining/coin_cloud_mining_profit_config_list";
    }

    @RequiresPermissions("root_coin_cloud_mining_profit_level_config_list")
    @RequestMapping("getCoinCloudMiningProfitConfigList")
    @ResponseBody
    public String getCoinCloudMiningProfitConfigList()
    {
        String time = WebRequest.getString("time");

        long days = WebRequest.getLong("days");
        Status status = Status.getType(WebRequest.getString("status"));

        ApiJsonTemplate template = new ApiJsonTemplate();

        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
        pageVo.parseTime(time);

        RowPager<CloudProfitConfigInfo> rowPager = mProfitConfigService.queryScrollPage(pageVo, days, status);

        template.setData(rowPager);
        return template.toJSONString();
    }

    @RequiresPermissions("root_coin_cloud_mining_profit_level_config_edit")
    @RequestMapping("toCoinCloudMiningProfitConfigEditPage")
    public String toCoinCloudMiningProfitConfigEditPage(Model model, HttpServletRequest request)
    {
        long id = WebRequest.getLong("id");
        if(id > 0)
        {
            CloudProfitConfigInfo entity = mProfitConfigService.findById(false, id);
            model.addAttribute("entity", entity);
        }

        CryptoNetworkType.addFreemarkerModel(model);
        CryptoCurrency.addModel(model);

        return "admin/coin/cloud_mining/coin_cloud_mining_profit_config_edit";
    }

    @RequiresPermissions("root_coin_cloud_mining_profit_level_config_edit")
    @RequestMapping("updateCoinCloudMiningProfitConfig")
    @ResponseBody
    public String updateCoinCloudMiningProfitConfig(Model model, HttpServletRequest request)
    {
        long id = WebRequest.getLong("id");

        long days = WebRequest.getLong("days");
        long level = WebRequest.getLong("level");

        BigDecimal minAmount = WebRequest.getBigDecimal("minAmount");
        BigDecimal dailyRate = WebRequest.getBigDecimal("dailyRate");

        Status status = Status.getType(WebRequest.getString("status"));

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();


        if(status == null || level <= 0)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if(days <= 1)
        {
            apiJsonTemplate.setError(-1, "最低投资期限为2天!");
            return apiJsonTemplate.toJSONString();
        }

        if(level > 5)
        {
            apiJsonTemplate.setError(-1, "等级最高为5");
            return apiJsonTemplate.toJSONString();
        }

        if(level == 1)
        {
            minAmount = BigDecimal.ZERO;

        }
        else
        {
            if(minAmount == null || minAmount.compareTo(BigDecimal.ZERO) <= 0)
            {
                apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "最低金额 > 0 ");
                return apiJsonTemplate.toJSONString();
            }
        }

        if(dailyRate.compareTo(BigDecimal.ZERO) <= 0 || dailyRate.compareTo(BigDecimalUtils.DEF_1) >= 0)
        {
            apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "预期收益范围是 0 - 1 ");
            return apiJsonTemplate.toJSONString();
        }


        try {
            if(id > 0)
            {
                CloudProfitConfigInfo entity = mProfitConfigService.findById(false, id);
                if(entity.getLevel() == level)
                {
                    level = -1;
                }
                mProfitConfigService.updateInfo(entity, dailyRate, minAmount, level, status);
            }
            else
            {
                mProfitConfigService.add(days, level, minAmount, dailyRate, status);
            }
        } catch (Exception e) {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST);
        }
        return apiJsonTemplate.toJSONString();
    }




}

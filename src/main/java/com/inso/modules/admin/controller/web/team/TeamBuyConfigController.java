package com.inso.modules.admin.controller.web.team;

import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.BigDecimalUtils;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.admin.helper.AdminAccountHelper;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.logical.UserQueryManager;
import com.inso.modules.web.SystemRunningMode;
import com.inso.modules.web.team.model.TeamBusinessType;
import com.inso.modules.web.team.model.TeamConfigInfo;
import com.inso.modules.web.team.service.TeamBuyConfigService;
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
public class TeamBuyConfigController {

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
    private TeamBuyConfigService mTeamBuyingConfigService;


    @RequiresPermissions("root_web_team_buying_level_config_list")
    @RequestMapping("root_web_team_buying_level_config")
    public String toList(Model model, HttpServletRequest request)
    {
        boolean isAdmin = AdminAccountHelper.isNy4timeAdminOrDEV();
        model.addAttribute("isSuperAdmin", isAdmin + StringUtils.getEmpty());

        ICurrencyType.addModel(model);
        TeamBusinessType.addModel(model);

        return "admin/web/team_buy/team_config_list";
    }

    @RequiresPermissions("root_web_team_buying_level_config_list")
    @RequestMapping("getWebTeamBuyingConfigList")
    @ResponseBody
    public String getDataList()
    {
        String time = WebRequest.getString("time");
        String agentname = WebRequest.getString("agentname");

        TeamBusinessType businessType = TeamBusinessType.getType(WebRequest.getString("businessType"));
        CryptoCurrency currency = CryptoCurrency.getType(WebRequest.getString("currencyType"));
        Status status = Status.getType(WebRequest.getString("status"));

        ApiJsonTemplate template = new ApiJsonTemplate();

        long agentid = mUserQueryManager.findUserid(agentname);

        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
        pageVo.parseTime(time);

        RowPager<TeamConfigInfo> rowPager = mTeamBuyingConfigService.queryScrollPage(pageVo, agentid, businessType, currency, status);

        template.setData(rowPager);
        return template.toJSONString();
    }

    @RequiresPermissions("root_web_team_buying_level_config_edit")
    @RequestMapping("toWebTeamBuyingConfigEditPage")
    public String toEdigPage(Model model, HttpServletRequest request)
    {
        long id = WebRequest.getLong("id");
        if(id > 0)
        {
            TeamConfigInfo entity = mTeamBuyingConfigService.findById(false, id);
            model.addAttribute("entity", entity);
        }

        ICurrencyType.addModel(model);
        TeamBusinessType.addModel(model);
        return "admin/web/team_buy/team_config_edit";
    }

    @RequiresPermissions("root_web_team_buying_level_config_edit")
    @RequestMapping("updateWebTeamBuyingConfig")
    @ResponseBody
    public String updateInfo(Model model, HttpServletRequest request)
    {
        long id = WebRequest.getLong("id");
        String agentname = WebRequest.getString("agentname");

        TeamBusinessType businessType = TeamBusinessType.getType(WebRequest.getString("businessType"));
        ICurrencyType currency = ICurrencyType.getType(WebRequest.getString("currencyType"));

        BigDecimal limitBalanceAmount = WebRequest.getBigDecimal("limitBalanceAmount");
        long level = WebRequest.getLong("level");

        long minInviteCount = WebRequest.getLong("minInviteCount");
        BigDecimal minAmount = WebRequest.getBigDecimal("minAmount");
        String returnCreatorRate = WebRequest.getString("returnCreatorRate");
        BigDecimal returnJoinRate = WebRequest.getBigDecimal("returnJoinRate");

        Status status = Status.getType(WebRequest.getString("status"));

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();


        if(currency == null || status == null || level <= 0 || businessType == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if(!SystemRunningMode.isCryptoMode())
        {
            currency = ICurrencyType.getSupportCurrency();
        }

        if(StringUtils.isEmpty(returnCreatorRate))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        String[] returnCreatorRateArr = returnCreatorRate.split(",");

        if(returnCreatorRateArr == null || returnCreatorRateArr.length <= 0)
        {
            apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "创建者预期收益范围是 0 < X < 1 ");
            return apiJsonTemplate.toJSONString();
        }

        for(String rateStr : returnCreatorRateArr)
        {
            BigDecimal rate = new BigDecimal(rateStr);
            if(rate.compareTo(BigDecimal.ZERO) < 0 || rate.compareTo(BigDecimalUtils.DEF_1) >= 0)
            {
                apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "参与者预期收益范围是 0 - 1 ");
                return apiJsonTemplate.toJSONString();
            }
        }

        if(returnJoinRate.compareTo(BigDecimal.ZERO) < 0 || returnJoinRate.compareTo(BigDecimalUtils.DEF_1) >= 0)
        {
            apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "参与者预期收益范围是 0 - 1 ");
            return apiJsonTemplate.toJSONString();
        }

        if(!(minInviteCount >= 1 && minInviteCount <= 10))
        {
            apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "最低人数 >= 3 && <= 10 ! ");
            return apiJsonTemplate.toJSONString();
        }

        if(minAmount == null || minAmount.compareTo(BigDecimal.ZERO) < 0)
        {
            apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "最低金额 >= 0 ");
            return apiJsonTemplate.toJSONString();
        }

        if(limitBalanceAmount == null || limitBalanceAmount.compareTo(BigDecimal.ZERO) < 0)
        {
            apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "最低有效充值 >= 0 ");
            return apiJsonTemplate.toJSONString();
        }

        try {
            if(id > 0)
            {
                TeamConfigInfo entity = mTeamBuyingConfigService.findById(false, id);
                if(entity.getLevel() == level)
                {
                    level = -1;
                }
                minInviteCount = -1;

                if(returnCreatorRateArr.length != entity.getLimitMinInviteCount())
                {
                    apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "邀请人数不等于创建者返回率的个数!");
                    return apiJsonTemplate.toJSONString();
                }
                mTeamBuyingConfigService.updateInfo(entity, returnCreatorRate, returnJoinRate, minAmount, -1, limitBalanceAmount, status);
            }
            else
            {

               // UserInfo userInfo = mUserQueryManager.findUserInfo(agentname);
//                if(SystemRunningMode.isCryptoMode())
//                {
//                    if(userInfo == null || !UserInfo.UserType.AGENT.getKey().equalsIgnoreCase(userInfo.getType()))
//                    {
//                        apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "当前代理不存在或非代理用户! ");
//                        return apiJsonTemplate.toJSONString();
//                    }
//                }

                //mTeamBuyingConfigService.add(userInfo, businessType, currency, limitBalanceAmount, level, minAmount, minInviteCount, returnCreatorRate, status, returnJoinRate);
            }
        } catch (Exception e) {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST);
        }
        return apiJsonTemplate.toJSONString();
    }




}

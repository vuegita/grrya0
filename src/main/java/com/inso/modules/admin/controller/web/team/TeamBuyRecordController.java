package com.inso.modules.admin.controller.web.team;

import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.admin.helper.AdminAccountHelper;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.user.logical.UserQueryManager;
import com.inso.modules.web.team.model.TeamBusinessType;
import com.inso.modules.web.team.model.TeamBuyGroupInfo;
import com.inso.modules.web.team.model.TeamBuyRecordInfo;
import com.inso.modules.web.team.service.TeamBuyConfigService;
import com.inso.modules.web.team.service.TeamBuyGroupService;
import com.inso.modules.web.team.service.TeamBuyRecordService;
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
public class TeamBuyRecordController {

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

    @Autowired
    private TeamBuyGroupService mTeamBuyGroupService;

    @Autowired
    private TeamBuyRecordService mTeamBuyRecordService;


    @RequiresPermissions("root_web_team_buying_record_list")
    @RequestMapping("root_web_team_buying_record")
    public String toList(Model model, HttpServletRequest request)
    {
        boolean isAdmin = AdminAccountHelper.isNy4timeAdminOrDEV();
        model.addAttribute("isSuperAdmin", isAdmin + StringUtils.getEmpty());

        ICurrencyType.addModel(model);
        TeamBusinessType.addModel(model);

        return "admin/web/team_buy/team_buy_record_list";
    }

    @RequiresPermissions("root_web_team_buying_record_list")
    @RequestMapping("getWebTeamBuyingRecordList")
    @ResponseBody
    public String getDataList()
    {
        String time = WebRequest.getString("time");
        String agentname = WebRequest.getString("agentname");
        String staffname = WebRequest.getString("staffname");
        String username = WebRequest.getString("username");

        TeamBusinessType businessType = TeamBusinessType.getType(WebRequest.getString("businessType"));
        CryptoCurrency currency = CryptoCurrency.getType(WebRequest.getString("currencyType"));
        OrderTxStatus status = OrderTxStatus.getType(WebRequest.getString("status"));

        ApiJsonTemplate template = new ApiJsonTemplate();

        long agentid = mUserQueryManager.findUserid(agentname);
        long staffid = mUserQueryManager.findUserid(staffname);
        long userid = mUserQueryManager.findUserid(username);

        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
        pageVo.parseTime(time);

        RowPager<TeamBuyRecordInfo> rowPager = mTeamBuyRecordService.queryScrollPage(pageVo, agentid, staffid, userid, businessType, status);

        template.setData(rowPager);
        return template.toJSONString();
    }

    @RequiresPermissions("root_web_team_buying_record_edit")
    @RequestMapping("toWebTeamBuyingRecordEditPage")
    public String toEdigPage(Model model, HttpServletRequest request)
    {
        long id = WebRequest.getLong("id");
        if(id > 0)
        {
            TeamBuyRecordInfo entity = mTeamBuyRecordService.findById(false, id);
            model.addAttribute("entity", entity);
        }

        ICurrencyType.addModel(model);
        TeamBusinessType.addModel(model);
        return "admin/web/team_buy/team_buy_record_edit";
    }

    @RequiresPermissions("root_web_team_buying_record_edit")
    @RequestMapping("updateWebTeamBuyingRecordInfo")
    @ResponseBody
    public String updateInfo(Model model, HttpServletRequest request)
    {
        long id = WebRequest.getLong("id");
        String agentname = WebRequest.getString("agentname");

//        TeamBusinessType businessType = TeamBusinessType.getType(WebRequest.getString("businessType"));
//        ICurrencyType currency = ICurrencyType.getType(WebRequest.getString("currencyType"));
        BigDecimal realInvesAmount = WebRequest.getBigDecimal("realInvesAmount");

//        long minInviteCount = WebRequest.getLong("minInviteCount");
//        BigDecimal minAmount = WebRequest.getBigDecimal("minAmount");
//        BigDecimal returnRate = WebRequest.getBigDecimal("returnRate");
//
        OrderTxStatus status = OrderTxStatus.getType(WebRequest.getString("status"));

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(realInvesAmount  == null || realInvesAmount.compareTo(BigDecimal.ZERO) < 0)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

//        if(!SystemRunningMode.isCryptoMode())
//        {
//            currency = ICurrencyType.getSupportCurrency();
//        }

//        if(returnRate.compareTo(BigDecimal.ZERO) <= 0 || returnRate.compareTo(BigDecimalUtils.DEF_1) >= 0)
//        {
//            apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "预期收益范围是 0 - 1 ");
//            return apiJsonTemplate.toJSONString();
//        }
//
//        if(!(minInviteCount >= 3 && minInviteCount <= 10))
//        {
//            apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "最低人数 >= 3 && <= 10 ! ");
//            return apiJsonTemplate.toJSONString();
//        }
//
//        if(minAmount == null || minAmount.compareTo(BigDecimal.ZERO) < 0)
//        {
//            apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "最低金额 >= 0 ");
//            return apiJsonTemplate.toJSONString();
//        }


        try {
            if(id > 0)
            {
                TeamBuyRecordInfo entity = mTeamBuyRecordService.findById(false, id);
                if(entity == null)
                {
                    apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
                    return apiJsonTemplate.toJSONString();
                }

                OrderTxStatus txStatus = OrderTxStatus.getType(entity.getStatus());
                if(txStatus == OrderTxStatus.REALIZED)
                {
                    return apiJsonTemplate.toJSONString();
                }


                mTeamBuyRecordService.updateInfo(entity, realInvesAmount, status);
            }
            else
            {

//                UserInfo userInfo = mUserQueryManager.findUserInfo(agentname);
//                if(SystemRunningMode.isCryptoMode())
//                {
//                    if(userInfo == null || !UserInfo.UserType.AGENT.getKey().equalsIgnoreCase(userInfo.getType()))
//                    {
//                        apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "当前代理不存在或非代理用户! ");
//                        return apiJsonTemplate.toJSONString();
//                    }
//                }

//                mTeamBuyingConfigService.add(userInfo, businessType, currency, level, minAmount, minInviteCount, returnRate, status);
            }
        } catch (Exception e) {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST);
        }
        return apiJsonTemplate.toJSONString();
    }




}

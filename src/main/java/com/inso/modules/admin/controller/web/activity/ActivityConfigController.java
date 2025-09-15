package com.inso.modules.admin.controller.web.activity;

import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.context.MyEnvironment;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.*;
import com.inso.modules.admin.helper.AdminAccountHelper;
import com.inso.modules.coin.core.model.ApproveAuthInfo;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.user.logical.UserQueryManager;
import com.inso.modules.web.activity.model.ActivityBusinessType;
import com.inso.modules.web.activity.model.ActivityInfo;
import com.inso.modules.web.activity.service.ActivityService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;

@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class ActivityConfigController {

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
    private ActivityService mActivityService;


    @RequiresPermissions("root_web_activity_config_list")
    @RequestMapping("root_web_activity_config")
    public String toList(Model model, HttpServletRequest request)
    {
        boolean isAdmin = AdminAccountHelper.isNy4timeAdminOrDEV();
        model.addAttribute("isSuperAdmin", isAdmin + StringUtils.getEmpty());

        ICurrencyType.addModel(model);
        ActivityBusinessType.addModel(model);

        return "admin/web/activity/activity_config_list";
    }

    @RequiresPermissions("root_web_activity_config_list")
    @RequestMapping("getWebActivityConfigList")
    @ResponseBody
    public String getDataList()
    {
        String time = WebRequest.getString("time");
//        String agentname = WebRequest.getString("agentname");

        ActivityBusinessType businessType = ActivityBusinessType.getType(WebRequest.getString("businessType"));
        OrderTxStatus txStatus = OrderTxStatus.getType(WebRequest.getString("status"));

        ApiJsonTemplate template = new ApiJsonTemplate();

        long agentid = -1;
        long staffid = -1;

        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
        pageVo.parseTime(time);

        RowPager<ActivityInfo> rowPager = mActivityService.queryScrollPage(pageVo, agentid, staffid, businessType, txStatus);

        template.setData(rowPager);
        return template.toJSONString();
    }

    @RequiresPermissions("root_web_activity_config_edit")
    @RequestMapping("toWebActivityConfigEditPage")
    public String toEdigPage(Model model, HttpServletRequest request)
    {
        long id = WebRequest.getLong("id");
        if(id > 0)
        {
            ActivityInfo entity = mActivityService.findById(false, id);
            model.addAttribute("entity", entity);
        }

        ICurrencyType.addModel(model);
        ActivityBusinessType.addModel(model);
        return "admin/web/activity/activity_edit";
    }

    @RequiresPermissions("root_web_activity_config_edit")
    @RequestMapping("updateWebActivityConfig")
    @ResponseBody
    public String updateInfo(Model model, HttpServletRequest request)
    {
        long id = WebRequest.getLong("id");
//        String agentname = WebRequest.getString("agentname");

        String title = WebRequest.getString("title");

        ActivityBusinessType businessType = ActivityBusinessType.getType(WebRequest.getString("businessType"));
        ICurrencyType currency = ICurrencyType.getSupportCurrency();

        long limitMinInviteCount = WebRequest.getLong("limitMinInviteCount");
        BigDecimal limitMinInvesAmount = WebRequest.getBigDecimal("limitMinInvesAmount");

        BigDecimal basicPresentAmount = WebRequest.getBigDecimal("basicPresentAmount");
        String extraPresentTier = WebRequest.getString("extraPresentTier");

        OrderTxStatus status = OrderTxStatus.getType(WebRequest.getString("status"));

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(StringUtils.isEmpty(title) || !RegexUtils.isBankName(title))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if(limitMinInviteCount <= 0 || currency == null || status == null || businessType == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if(limitMinInvesAmount == null || limitMinInvesAmount.compareTo(BigDecimal.ZERO) <= 0)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if(basicPresentAmount == null || basicPresentAmount.compareTo(BigDecimal.ZERO) <= 0)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if(!StringUtils.isEmpty(extraPresentTier))
        {
            String[] extraPresentTierArr = StringUtils.split(extraPresentTier, '|');
            if(extraPresentTierArr == null || extraPresentTierArr.length < 2 || extraPresentTierArr.length > 5)
            {
                apiJsonTemplate.setError(-1, "额外赠送等级配置异常, 等级 2 <= X <= 5");
                return apiJsonTemplate.toJSONString();
            }

            char split = '=';
            for(String line :  extraPresentTierArr)
            {
                String[] itemArr = StringUtils.split(line, split);
                if(itemArr == null || itemArr.length != 2)
                {
                    apiJsonTemplate.setError(-1, "额外赠送等级配置异常: " + line);
                    return apiJsonTemplate.toJSONString();
                }

                BigDecimal tmpAmount = StringUtils.asBigDecimal(itemArr[0]);
                BigDecimal tmpRate = StringUtils.asBigDecimal(itemArr[1]);

                if(tmpAmount == null || tmpAmount.compareTo(BigDecimal.ZERO) <= 0)
                {
                    apiJsonTemplate.setError(-1, "额外赠送等级配置异常: " + line);
                    return apiJsonTemplate.toJSONString();
                }

                if(tmpRate == null || tmpRate.compareTo(BigDecimal.ZERO) <= 0 || tmpRate.compareTo(BigDecimalUtils.DEF_1) >= 0)
                {
                    apiJsonTemplate.setError(-1, "额外赠送等级配置异常: " + line);
                    return apiJsonTemplate.toJSONString();
                }

            }
        }

        if(limitMinInviteCount < 300)
        {
            apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "最低人数 >= 300 ");
            return apiJsonTemplate.toJSONString();
        }

        try {
            if(id > 0)
            {
                ActivityInfo entity = mActivityService.findById(false, id);
                if(entity == null)
                {
                    apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
                    return apiJsonTemplate.toJSONString();
                }

                boolean upDB = false;

                OrderTxStatus dbStatus = OrderTxStatus.getType(entity.getStatus());
                if(MyEnvironment.isDev() || entity.getBegintime().getTime() - 60000 > System.currentTimeMillis())
                {
                    if(dbStatus == OrderTxStatus.NEW || dbStatus == OrderTxStatus.WAITING)
                    {
                        upDB = true;
                    }
                    else
                    {
                        status = null;
                    }
                }
                else
                {
                    status = null;
                }

                if(!title.equalsIgnoreCase(entity.getTitle()))
                {
                    upDB = true;
                }

                if(upDB)
                {
                    mActivityService.updateInfo(entity, title, -1, -1,null, null, status, null);
                }
            }
            else
            {

                Date beginTimeDate = WebRequest.getDate("begintime", DateUtils.TYPE_YYYY_MM_DD);
                int cycleDays = WebRequest.getInt("cycleDays");

                if(beginTimeDate == null)
                {
                    apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
                    return apiJsonTemplate.toJSONString();
                }

                if(cycleDays < 3)
                {
                    apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "最短周期为 >= 3");
                    return apiJsonTemplate.toJSONString();
                }

                DateTime beginTime = new DateTime(beginTimeDate.getTime());
                DateTime endTime = beginTime.plusDays(cycleDays).minusSeconds(1);
                mActivityService.add(title, businessType, limitMinInvesAmount, limitMinInviteCount, basicPresentAmount, extraPresentTier, beginTime, endTime);

            }
        } catch (Exception e) {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_BUSY);
        }
        return apiJsonTemplate.toJSONString();
    }



    @RequiresPermissions("root_web_activity_config_delete")
    @RequestMapping("deleteWebActivityInfo")
    @ResponseBody
    public String deleteWebActivityInfo()
    {
        long id = WebRequest.getLong("id");
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(id <= 0)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        ActivityInfo entity = mActivityService.findById(false, id);
        if(entity == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
            return apiJsonTemplate.toJSONString();
        }

        OrderTxStatus txStatus = OrderTxStatus.getType(entity.getStatus());

//        if(AdminAccountHelper.isNy4timeAdminOrDEV())
//        {
//            mActivityService.deleteById(entity);
//        }

        if(txStatus == OrderTxStatus.NEW)
        {
            mActivityService.deleteById(entity);
        }
        else
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
            return apiJsonTemplate.toJSONString();
        }


        return apiJsonTemplate.toJSONString();
    }

}

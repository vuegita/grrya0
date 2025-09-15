package com.inso.modules.admin.controller.ad;


import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.*;
import com.inso.modules.ad.core.model.AdVipLimitInfo;
import com.inso.modules.ad.core.service.CategoryService;
import com.inso.modules.ad.core.service.VipLimitService;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.logical.UserQueryManager;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.money.service.UserMoneyService;
import com.inso.modules.passport.user.service.UserService;
import com.inso.modules.web.model.VIPInfo;
import com.inso.modules.web.model.VIPType;
import com.inso.modules.web.service.VIPService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;


@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class VipLimitController {

    @Autowired
    private UserService mUserService;

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private UserMoneyService mUserMoneyService;

    @Autowired
    private UserQueryManager mUserQueryManager;

    @Autowired
    private CategoryService mCategoryService;

    @Autowired
    private VipLimitService mVipLimitService;

    @Autowired
    private VIPService mVIPService;

    @RequiresPermissions("root_ad_vip_limit_list")
    @RequestMapping("root_ad_vip_limit")
    public String toPage(Model model)
    {
//        AdEventType[] adEventTypeList = AdEventType.values();
//        model.addAttribute("adEventTypeList", adEventTypeList);
        return "admin/ad/vip_limit_list";
    }

    @RequiresPermissions("root_ad_vip_limit_list")
    @RequestMapping("getAdVipLimitList")
    @ResponseBody
    public String getAdVipLimitList()
    {
        String statusStr = WebRequest.getString("status");
        Status status = Status.getType(statusStr);

//        String vipTypeStr = WebRequest.getString("vipType");
//        VIPType vipType = VIPType.getType(vipTypeStr);

        ApiJsonTemplate template = new ApiJsonTemplate();
        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));

        RowPager<AdVipLimitInfo> rowPager = mVipLimitService.queryScrollPage(pageVo, status);
        template.setData(rowPager);
        return template.toJSONString();
    }

    @RequiresPermissions("root_ad_category_edit")
    @RequestMapping("toEditAdVipLimitPage")
    public String toEditRootVIPConfigPage(Model model)
    {
        long id = WebRequest.getLong("id");
        if(id > 0)
        {
            AdVipLimitInfo vipInfo = mVipLimitService.findById(false, id);
            model.addAttribute("entity", vipInfo);
        }

        List<VIPInfo> vipInfoList = mVIPService.queryAllEnable(false, VIPType.AD);
        model.addAttribute("vipInfoList", vipInfoList);

//        VIPType[] vipTypeList = VIPType.values();
//        model.addAttribute("vipTypeList", vipTypeList);
        return "admin/ad/vip_limit_edit";
    }

    @RequiresPermissions("root_ad_category_edit")
    @RequestMapping("editAdVipLimitInfo")
    @ResponseBody
    public String editAdVipLimitInfo()
    {
        long id = WebRequest.getLong("id");

        String statusStr = WebRequest.getString("status");
        Status status = Status.getType(statusStr);

        BigDecimal totalMoneyOfDay = WebRequest.getBigDecimal("totalMoneyOfDay");
        BigDecimal freeMoneyOfDay = WebRequest.getBigDecimal("freeMoneyOfDay");
        BigDecimal maxMoneyOfSingle = WebRequest.getBigDecimal("maxMoneyOfSingle");

        BigDecimal lv1RebateBalanceRate = WebRequest.getBigDecimal("lv1RebateBalanceRate");
        BigDecimal lv2RebateBalanceRate = WebRequest.getBigDecimal("lv2RebateBalanceRate");

        // 直接固定硬编码
        BigDecimal lv1RebateWithdrawlRate = WebRequest.getBigDecimal("lv1RebateWithdrawlRate");
        BigDecimal lv2RebateWithdrawlRate = WebRequest.getBigDecimal("lv2RebateWithdrawlRate");
//        lv1RebateWithdrawlRate = BigDecimal.ZERO;
        lv2RebateWithdrawlRate = BigDecimal.ZERO;

        long inviteCountOfDay = WebRequest.getLong("inviteCountOfDay");
        BigDecimal inviteMoneyOfDay = WebRequest.getBigDecimal("inviteMoneyOfDay");

        long buyCountOfDay = WebRequest.getLong("buyCountOfDay");


        BigDecimal buyMoneyOfDay = WebRequest.getBigDecimal("buyMoneyOfDay");

        long paybackPeriod = WebRequest.getLong("paybackPeriod");

        long vipId = WebRequest.getLong("vipid");

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        VIPInfo vipInfo = mVIPService.findById(false, vipId);
        if(vipInfo == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
            return apiJsonTemplate.toJSONString();
        }

        if(lv1RebateBalanceRate == null || lv2RebateBalanceRate == null || lv1RebateWithdrawlRate == null || lv2RebateWithdrawlRate == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if(lv1RebateBalanceRate.compareTo(BigDecimal.ZERO) < 0 || lv1RebateBalanceRate.compareTo(BigDecimalUtils.DEF_100) >= 0)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if(lv2RebateBalanceRate.compareTo(BigDecimal.ZERO) < 0 || lv2RebateBalanceRate.compareTo(BigDecimalUtils.DEF_100) >= 0)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if(lv1RebateWithdrawlRate.compareTo(BigDecimal.ZERO) < 0 || lv1RebateWithdrawlRate.compareTo(BigDecimalUtils.DEF_50) > 0)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }
//
//        if(lv2RebateWithdrawlRate.compareTo(BigDecimal.ZERO) < 0 || lv2RebateWithdrawlRate.compareTo(BigDecimalUtils.DEF_100) >= 0)
//        {
//            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
//            return apiJsonTemplate.toJSONString();
//        }

        if(id > 0)
        {
            boolean isFreeVIP = vipInfo.getLevel() == 0;
            if(paybackPeriod <= 0 && !isFreeVIP)
            {
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
                return apiJsonTemplate.toJSONString();
            }

            if(status == null)
            {
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
                return apiJsonTemplate.toJSONString();
            }

            AdVipLimitInfo limitInfo = mVipLimitService.findById(false, id);
            if(limitInfo == null)
            {
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
                return apiJsonTemplate.toJSONString();
            }

            if(!isFreeVIP)
            {
                totalMoneyOfDay = AdVipLimitInfo.calcTotalMoneyOfDay(vipInfo.getPrice(), paybackPeriod).setScale(2, RoundingMode.HALF_UP);
            }

//            if(freeMoneyOfDay.compareTo(tmpTotalMoneyOfDay) > 0)
//            {
//                apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "当日总上限为 " + tmpTotalMoneyOfDay);
//                return apiJsonTemplate.toJSONString();
//            }

            if(!isFreeVIP)
            {
                if(freeMoneyOfDay.compareTo(totalMoneyOfDay) > 0)
                {
                    apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "免费额度最大为 " + totalMoneyOfDay);
                    return apiJsonTemplate.toJSONString();
                }

                // 单笔最大金额400
                if(maxMoneyOfSingle.compareTo(totalMoneyOfDay) > 0 || maxMoneyOfSingle.compareTo(BigDecimalUtils.DEF_400) > 0)
                {
                    apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "单笔最大金额为 " + totalMoneyOfDay);
                    return apiJsonTemplate.toJSONString();
                }
            }

            inviteMoneyOfDay = totalMoneyOfDay.subtract(freeMoneyOfDay);
            if(inviteCountOfDay < 0)
            {
                inviteCountOfDay = 0;
//                apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "邀请好友个数不能 <= 0 !");
//                return apiJsonTemplate.toJSONString();
            }

//            if(buyCountOfDay > inviteCountOfDay)
//            {
//                apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "邀请好友个数 >= 购买VIP个数 !");
//                return apiJsonTemplate.toJSONString();
//            }

//            BigDecimal buyMoneyOfDay = totalMoneyOfDay.subtract(freeMoneyOfDay).subtract(inviteMoneyOfDay);
//            if(buyMoneyOfDay.compareTo(BigDecimal.ZERO) < 0)
//            {
//                apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "当日总金额 >= 免费额度 + 邀请额度 + 购买VIP额度!");
//                return apiJsonTemplate.toJSONString();
//            }

            if(buyMoneyOfDay.compareTo(BigDecimal.ZERO) < 0)
            {
                apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "购买VIP额度/(每个) 需要 >= 0!");
                return apiJsonTemplate.toJSONString();
            }

            mVipLimitService.updateInfo(limitInfo, totalMoneyOfDay, freeMoneyOfDay,
                    inviteCountOfDay, inviteMoneyOfDay, buyCountOfDay, buyMoneyOfDay, maxMoneyOfSingle, paybackPeriod, status,
                    lv1RebateBalanceRate, lv2RebateBalanceRate, lv1RebateWithdrawlRate, null);
        }
        else
        {
            if(mVipLimitService.findByVipId(false, vipInfo.getId()) != null)
            {
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST);
                return apiJsonTemplate.toJSONString();
            }
            inviteCountOfDay = 5;
            inviteMoneyOfDay = BigDecimal.ZERO;

            buyCountOfDay = 3;


            totalMoneyOfDay = AdVipLimitInfo.calcTotalMoneyOfDay(vipInfo.getPrice(), -1);
            maxMoneyOfSingle = AdVipLimitInfo.calcMaxMoneyOfSingle(totalMoneyOfDay);
            freeMoneyOfDay = AdVipLimitInfo.calcFreeMoneyOfSingle(totalMoneyOfDay);
            mVipLimitService.add(vipId, totalMoneyOfDay, freeMoneyOfDay, inviteCountOfDay, inviteMoneyOfDay, buyCountOfDay, buyMoneyOfDay, maxMoneyOfSingle);
        }
        return apiJsonTemplate.toJSONString();
    }

    @RequiresPermissions("root_ad_category_edit")
    @RequestMapping("batchCreateAdVipLimitInfo")
    @ResponseBody
    public String batchCreateAdVipLimitInfo()
    {
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        List<VIPInfo> rsList = mVIPService.queryAllEnable(false, VIPType.AD);
        if(!CollectionUtils.isEmpty(rsList))
        {
            long inviteCountOfDay = 5;
            BigDecimal inviteMoneyOfDay = BigDecimal.ZERO;

            long buyCountOfDay = 3;
            BigDecimal buyMoneyOfDay = BigDecimal.ZERO;

            for(VIPInfo vipInfo : rsList)
            {
                if(!Status.ENABLE.getKey().equalsIgnoreCase(vipInfo.getStatus()))
                {
                    continue;
                }

                long vipid = vipInfo.getId();

                if(mVipLimitService.findByVipId(true, vipid) != null)
                {
                    continue;
                }

                BigDecimal totalMoneyOfDay = AdVipLimitInfo.calcTotalMoneyOfDay(vipInfo.getPrice(), -1);
                BigDecimal maxMoneyOfSingle = AdVipLimitInfo.calcMaxMoneyOfSingle(totalMoneyOfDay);
                BigDecimal freeMoneyOfDay = AdVipLimitInfo.calcFreeMoneyOfSingle(totalMoneyOfDay);
                mVipLimitService.add(vipid, totalMoneyOfDay, freeMoneyOfDay, inviteCountOfDay, inviteMoneyOfDay, buyCountOfDay, buyMoneyOfDay, maxMoneyOfSingle);
            }

        }
        return apiJsonTemplate.toJSONString();
    }


}

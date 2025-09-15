package com.inso.modules.admin.controller.passport.business;


import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.PageVo;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RegexUtils;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.model.*;
import com.inso.modules.passport.business.model.PromotionOrderInfo;
import com.inso.modules.passport.business.service.PromotionPresentOrderService;
import com.inso.modules.passport.user.logical.UserQueryManager;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.UserAttrService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class PromotionPresentController {

    @Autowired
    private PromotionPresentOrderService mPromotionPresentOrderService;

    @Autowired
    private UserQueryManager mUserQueryManager;

    @Autowired
    private UserAttrService mUserAttrService;

    @RequiresPermissions("root_passport_promotion_present_order_list")
    @RequestMapping("root_passport_promotion_present_order")
    public String toPlatformSupplyPage(Model model)
    {
        ICurrencyType.addModel(model);
        return "admin/passport/business/user_promotion_present_order_list";
    }

    @RequiresPermissions("root_passport_promotion_present_order_list")
    @RequestMapping("root_passport_promotion_present_order/getDataList")
    @ResponseBody
    public String getPlatformSupplyList()
    {
        String time = WebRequest.getString("time");
        String username = WebRequest.getString("username");
        String agentname = WebRequest.getString("agentname");
        String staffname = WebRequest.getString("staffname");
        String systemOrderno = WebRequest.getString("systemOrderno");

        ApiJsonTemplate template = new ApiJsonTemplate();

        OrderTxStatus txStatus = OrderTxStatus.getType(WebRequest.getString("txStatus"));

        Status settleStatus = Status.getType(WebRequest.getString("settleStatus"));

        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
        if(!pageVo.parseTime(time))
        {
            template.setData(RowPager.getEmptyRowPager());
            return template.toJSONString();
        }

        long userid = mUserQueryManager.findUserid(username);
        long agentid = mUserQueryManager.findUserid(agentname);
        long staffid = mUserQueryManager.findUserid(staffname);

        RowPager<PromotionOrderInfo> rowPager = mPromotionPresentOrderService.queryScrollPageByUser(pageVo, userid, systemOrderno, txStatus, agentid, staffid);
        template.setData(rowPager);

        return template.toJSONString();
    }

    @RequiresPermissions("root_passport_promotion_present_order_edit")
    @RequestMapping("root_passport_promotion_present_order/edit/page")
    public String toApplyPlatformSupplyPage(Model model)
    {
        String orderno = WebRequest.getString("orderno");
        if(!StringUtils.isEmpty(orderno))
        {
            PromotionOrderInfo orderInfo = mPromotionPresentOrderService.findByNo(false, orderno);
            model.addAttribute("entity", orderInfo);
        }
        ICurrencyType.addModel(model);
        return "admin/passport/business/user_promotion_present_order_edit";
    }

    @RequiresPermissions("root_passport_promotion_present_order_edit")
    @RequestMapping("root_passport_promotion_present_order/edit")
    @ResponseBody
    public String addPlatformSupply()
    {
        String orderno = WebRequest.getString("orderno");
        String username = WebRequest.getString("username");
        BigDecimal amount = WebRequest.getBigDecimal("amount");
        String remark = WebRequest.getString("remark");

        BigDecimal rate1 = WebRequest.getBigDecimal("limitRate1");
        BigDecimal rate2 = WebRequest.getBigDecimal("limitRate2");
        PromotionOrderInfo.SettleMode settleMode = PromotionOrderInfo.SettleMode.getType(WebRequest.getString("settleMode"));
//        String tips = WebRequest.getString("tips");

        ApiJsonTemplate template = new ApiJsonTemplate();

        if(settleMode == null)
        {
            template.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return template.toJSONString();
        }

        if(settleMode == PromotionOrderInfo.SettleMode.Direct)
        {
            rate1 = BigDecimal.ZERO;
            rate2 = BigDecimal.ZERO;
        }
        else
        {
            if(rate1 == null || rate1.compareTo(BigDecimal.ZERO) < 0)
            {
                template.setJsonResult(SystemErrorResult.ERR_PARAMS);
                return template.toJSONString();
            }

            if(rate2 == null || rate2.compareTo(BigDecimal.ZERO) < 0 || rate2.compareTo(BigDecimal.ONE) >= 0)
            {
                template.setJsonResult(SystemErrorResult.ERR_PARAMS);
                return template.toJSONString();
            }
        }

        if(amount.compareTo(BigDecimal.ZERO) <= 0)
        {
            template.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return template.toJSONString();
        }

        UserInfo userInfo = mUserQueryManager.findUserInfo(username);
        if(userInfo == null)
        {
            template.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return template.toJSONString();
        }

        UserInfo.UserType userType = UserInfo.UserType.getType(userInfo.getType());
        if(!userType.isMemberOrTest())
        {
            template.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
            return template.toJSONString();
        }

        if(StringUtils.isEmpty(orderno))
        {
            List<PromotionOrderInfo> rsList = mPromotionPresentOrderService.queryScrollPageByUser(true, userInfo.getId());
            if(rsList != null && rsList.size() > 0)
            {
                template.setError(-1, "24小时之内还存在订单未完成!");
                return template.toJSONString();
            }
            UserAttr userAttr = mUserAttrService.find(false, userInfo.getId());
            mPromotionPresentOrderService.addOrder(rate1, rate2, settleMode, null, userAttr, OrderTxStatus.NEW, amount, BigDecimal.ZERO);
        }
        else
        {
            PromotionOrderInfo orderInfo = mPromotionPresentOrderService.findByNo(false, orderno);
            if(orderInfo == null)
            {
                template.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
                return template.toJSONString();
            }
            OrderTxStatus txStatus = OrderTxStatus.getType(orderInfo.getStatus());
            if(txStatus == OrderTxStatus.REALIZED || txStatus == OrderTxStatus.FAILED)
            {
                template.setJsonResult(SystemErrorResult.ERR_HAS_FINISHED);
                return template.toJSONString();
            }

            mPromotionPresentOrderService.updateTxStatus(orderno, userInfo.getId(), null, null, rate1, null, rate2, null, null);
        }
        return template.toJSONString();
    }


    @RequiresPermissions("root_passport_promotion_present_order_edit")
    @RequestMapping("doAuditUserPromotionPresentOrder")
    @ResponseBody
    public String doAuditUserPromotionPresentOrder()
    {
        String orderno = WebRequest.getString("orderno");
        String action = WebRequest.getString("action");

        ApiJsonTemplate template = new ApiJsonTemplate();

        if(StringUtils.isEmpty(orderno) || !RegexUtils.isDigit(orderno))
        {
            template.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return template.toJSONString();
        }

        PromotionOrderInfo orderInfo = mPromotionPresentOrderService.findByNo(false, orderno);
        if(orderInfo == null)
        {
            template.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
            return template.toJSONString();
        }

        ErrorResult result = SystemErrorResult.SUCCESS;
        if("pass".equalsIgnoreCase(action))
        {
//            result = mUserPayMgr.doRechargeSuccessAction(orderno, null,null);
        }
        else if("dispass".equalsIgnoreCase(action))
        {
            OrderTxStatus txStatus = OrderTxStatus.FAILED;
            mPromotionPresentOrderService.updateTxStatus(orderno, orderInfo.getUserid(), txStatus, null, null, null, null, null, null);
        }
        else
        {
            result = SystemErrorResult.ERR_PARAMS;
        }
        template.setJsonResult(result);
        return template.toJSONString();
    }

}

package com.inso.modules.admin.controller.passport;


import com.inso.modules.common.model.ICurrencyType;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.model.BusinessType;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.business.helper.BusinessOrderVerify;
import com.inso.modules.passport.user.logical.UserQueryManager;
import com.inso.modules.passport.business.model.BusinessOrder;
import com.inso.modules.passport.business.service.BusinessOrderService;
import com.inso.modules.passport.user.service.UserService;

@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class OtherOrderController {

    @Autowired
    private UserService mUserService;

    @Autowired
    private BusinessOrderService mBusinessOrderService;

    @Autowired
    private UserQueryManager mUserQueryManager;

    @RequiresPermissions("root_passport_user_other_order_record_list")
    @RequestMapping("root_passport_user_other_order_record")
    public String toPlatformSupplyPage(Model model)
    {
        ICurrencyType.addModel(model);
        return "admin/passport/user_other_order_list";
    }

    @RequiresPermissions("root_passport_user_other_order_record_list")
    @RequestMapping("getPassportOtherOrderList")
    @ResponseBody
    public String getPassportOtherOrderList()
    {
        String time = WebRequest.getString("time");
        String username = WebRequest.getString("username");
        String systemOrderno = WebRequest.getString("systemOrderno");
        String outTradeNo = WebRequest.getString("outTradeNo");

        String businessTypeString = WebRequest.getString("type");
        String txStatusString = WebRequest.getString("txStatus");

        ICurrencyType currencyType = ICurrencyType.getType(WebRequest.getString("currencyType"));

        ApiJsonTemplate template = new ApiJsonTemplate();

        BusinessType businessType = BusinessType.getType(businessTypeString);
        OrderTxStatus txStatus = OrderTxStatus.getType(txStatusString);

        BusinessType[] businessTypeArray = null;
        if(businessType == null)
        {
            businessTypeArray = new BusinessType[3];

            // 首次充值赠送
            businessTypeArray[0] = BusinessType.USER_FIRST_RECARGE_PRESENTATION;
            // 注册赠送
            businessTypeArray[1] = BusinessType.REGISTER_PRESENTATION;

            // 注册赠送
            businessTypeArray[2] = BusinessType.RECHARGE_PRESENTATION_PARENTUSER_BY_PERCENT;
        }
        else
        {
            businessTypeArray = new BusinessType[1];
            businessTypeArray[0] = businessType;
        }

        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
        if(!pageVo.parseTime(time))
        {
            template.setData(RowPager.getEmptyRowPager());
            return template.toJSONString();
        }

        // 订单号检验，如果不是本业务订单号，则直接返回
        if(!StringUtils.isEmpty(systemOrderno))
        {
            for(BusinessType tmp : businessTypeArray)
            {
                if(BusinessOrderVerify.verify(systemOrderno, tmp))
                {
                    template.setData(RowPager.getEmptyRowPager());
                    return template.toJSONString();
                }
            }
        }

        long userid = mUserQueryManager.findUserid(username);

        RowPager<BusinessOrder> rowPager = mBusinessOrderService.queryScrollPage(pageVo, userid, systemOrderno, outTradeNo, businessTypeArray, currencyType, txStatus, null ,-1,-1);
        template.setData(rowPager);

        return template.toJSONString();
    }

}

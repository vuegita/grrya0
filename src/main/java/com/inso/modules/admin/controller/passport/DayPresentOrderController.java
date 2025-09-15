package com.inso.modules.admin.controller.passport;


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
import com.inso.modules.common.model.BusinessType;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.user.logical.UserQueryManager;
import com.inso.modules.passport.business.model.DayPresentOrder;
import com.inso.modules.passport.business.service.DayPresentOrderService;
import com.inso.modules.passport.user.service.UserService;

/**
 * 每日赠送
 */
@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class DayPresentOrderController {

    @Autowired
    private UserService mUserService;

    @Autowired
    private DayPresentOrderService mBusinessOrderService;

    @Autowired
    private UserQueryManager mUserQueryManager;

    @RequiresPermissions("root_passport_user_day_present_order_list")
    @RequestMapping("root_passport_user_day_present_order")
    public String toPlatformSupplyPage(Model model)
    {
        return "admin/passport/user_day_present_order_list";
    }

    @RequiresPermissions("root_passport_user_day_present_order_list")
    @RequestMapping("getPassportDayPresentOrderList")
    @ResponseBody
    public String getPassportDayPresentOrderList()
    {
        String time = WebRequest.getString("time");
        String username = WebRequest.getString("username");
        String systemOrderno = WebRequest.getString("systemOrderno");

        String businessTypeString = WebRequest.getString("type");
        String txStatusString = WebRequest.getString("txStatus");


        ApiJsonTemplate template = new ApiJsonTemplate();

        BusinessType businessType = BusinessType.getType(businessTypeString);
        OrderTxStatus txStatus = OrderTxStatus.getType(txStatusString);

        BusinessType[] businessTypeArray = null;
        if(businessType == null)
        {
            businessTypeArray = new BusinessType[2];

            //  首次充值赠送
            businessTypeArray[0] = BusinessType.USER_FIRST_RECARGE_PRESENTATION;
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
//        if(!StringUtils.isEmpty(systemOrderno))
//        {
//            for(BusinessType tmp : businessTypeArray)
//            {
//                if(BusinessOrderVerify.verify(systemOrderno, tmp))
//                {
//                    template.setData(RowPager.getEmptyRowPager());
//                    return template.toJSONString();
//                }
//            }
//        }

        long userid = mUserQueryManager.findUserid(username);

        RowPager<DayPresentOrder> rowPager = mBusinessOrderService.queryScrollPage(pageVo, userid, -1, systemOrderno, txStatus);
        template.setData(rowPager);

        return template.toJSONString();
    }

}

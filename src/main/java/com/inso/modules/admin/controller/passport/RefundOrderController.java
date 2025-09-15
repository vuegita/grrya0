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
import com.inso.modules.passport.money.model.MoneyOrderType;
import com.inso.modules.passport.business.service.BusinessOrderService;
import com.inso.modules.passport.user.service.UserService;

@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class RefundOrderController {

    @Autowired
    private UserService mUserService;

    @Autowired
    private BusinessOrderService businessOrderService;

    @Autowired
    private UserQueryManager mUserQueryManager;

    @RequiresPermissions("root_passport_user_refund_order_record_list")
    @RequestMapping("root_passport_user_refund_order_record")
    public String toListPage(Model model)
    {
        model.addAttribute("orderTypeList", MoneyOrderType.values());
        ICurrencyType.addModel(model);
        return "admin/passport/user_refund_order_record_list";
    }

    @RequiresPermissions("root_passport_user_refund_order_record_list")
    @RequestMapping("getUserRefundOrderRecordList")
    @ResponseBody
    public String getList()
    {
        String time = WebRequest.getString("time");
        String username = WebRequest.getString("username");
        String orderTypeString = WebRequest.getString("type");
        String txStatusString = WebRequest.getString("txStatus");
        String systemOrderno = WebRequest.getString("systemOrderno");
        String outTradeno = WebRequest.getString("outTradeno");

        ICurrencyType currencyType = ICurrencyType.getType(WebRequest.getString("currencyType"));

        ApiJsonTemplate template = new ApiJsonTemplate();

        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
        if(!pageVo.parseTime(time))
        {
            template.setData(RowPager.getEmptyRowPager());
            return template.toJSONString();
        }

        // 订单号检验，如果不是本业务订单号，则直接返回
        if(!StringUtils.isEmpty(systemOrderno) && !BusinessOrderVerify.verify(systemOrderno, BusinessType.REFUND))
        {
            template.setData(RowPager.getEmptyRowPager());
            return template.toJSONString();
        }

        BusinessType[] mBusinessArray = {BusinessType.REFUND};

        OrderTxStatus txStatus = OrderTxStatus.getType(txStatusString);

        long userid = mUserQueryManager.findUserid(username);

        RowPager<BusinessOrder> rowPager = businessOrderService.queryScrollPage(pageVo, userid, systemOrderno, outTradeno, mBusinessArray, currencyType, txStatus, null,-1,-1);
        template.setData(rowPager);

        return template.toJSONString();
    }
}

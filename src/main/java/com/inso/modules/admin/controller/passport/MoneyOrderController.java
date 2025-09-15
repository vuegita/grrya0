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
import com.inso.modules.admin.helper.AgentAccountHelper;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.user.logical.UserQueryManager;
import com.inso.modules.passport.money.model.MoneyOrder;
import com.inso.modules.passport.money.model.MoneyOrderType;
import com.inso.modules.passport.money.service.MoneyOrderService;
import com.inso.modules.passport.user.service.UserService;

@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class MoneyOrderController {

    @Autowired
    private UserService mUserService;

    @Autowired
    private MoneyOrderService moneyOrderService;

    @Autowired
    private UserQueryManager mUserQueryManager;

    @RequiresPermissions("root_passport_user_money_order_list")
    @RequestMapping("root_passport_user_money_order")
    public String toUserMoneyOrderPage(Model model)
    {
        model.addAttribute("orderTypeList", MoneyOrderType.values());
        ICurrencyType.addModel(model);
        return "admin/passport/user_money_order_detail_list";
    }

    // 和代理共用同一个接口
    @RequiresPermissions("root_passport_user_supply_order_list")
    @RequestMapping("getUserMoneyOrderList")
    @ResponseBody
    public String getUserMoneyOrderList()
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

        MoneyOrderType orderType = MoneyOrderType.getType(orderTypeString);

        OrderTxStatus txStatus = OrderTxStatus.getType(txStatusString);

        long userid = mUserQueryManager.findUserid(username);

        // 如果是代理或员工登陆，则必须要有用户名
        if(AgentAccountHelper.isAgentOrStafffLogin() && userid <= 0)
        {
            template.setData(RowPager.getEmptyRowPager());
            return template.toJSONString();
        }

        RowPager<MoneyOrder> rowPager = moneyOrderService.queryScrollPage(pageVo, userid, -1, -1, systemOrderno, outTradeno, currencyType, orderType, txStatus);
        template.setData(rowPager);

        return template.toJSONString();
    }
}

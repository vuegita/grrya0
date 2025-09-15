package com.inso.modules.admin.controller.passport;


import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.FundAccountType;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.passport.user.logical.UserQueryManager;
import com.inso.modules.passport.money.model.UserMoney;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.money.service.UserMoneyService;
import com.inso.modules.passport.user.service.UserService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class UserMoneyController {

    @Autowired
    private UserService mUserService;

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private UserMoneyService mUserMoneyService;

    @Autowired
    private UserQueryManager mUserQueryManager;

    @RequiresPermissions("root_passport_user_money_balance_list")
    @RequestMapping("root_passport_user_money_balance")
    public String toPageUserTree(Model model)
    {
        FundAccountType.addModel(model);
        ICurrencyType.addModel(model);
        return "admin/passport/user_money_balance_list";
    }

    @RequiresPermissions("root_passport_user_attr_list")
    @RequestMapping("getUserMoneyBalanceList")
    @ResponseBody
    public String getDataList()
    {
        String sortName = WebRequest.getString("sortName");
        String sortOrder = WebRequest.getString("sortOrder");

        String time = WebRequest.getString("time");
        String username = WebRequest.getString("username");

        FundAccountType accountType = FundAccountType.getType(WebRequest.getString("fundAccountType"));
        ICurrencyType currencyType = ICurrencyType.getType(WebRequest.getString("currencyType"));

        ApiJsonTemplate template = new ApiJsonTemplate();

        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
        pageVo.parseTime(time);

        long userid = mUserQueryManager.findUserid(username);

        RowPager<UserMoney> rowPager = mUserMoneyService.queryScrollPage(pageVo, userid, accountType,  currencyType,null, sortName, sortOrder, username,-1,-1);
        template.setData(rowPager);
        return template.toJSONString();
    }



}

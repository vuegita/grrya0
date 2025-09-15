package com.inso.modules.admin.controller.passport;


import java.math.BigDecimal;

import com.inso.modules.common.model.FundAccountType;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.common.model.Status;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RowPager;
import com.inso.modules.passport.user.logical.UserQueryManager;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.model.UserLevel;
import com.inso.modules.passport.money.model.UserMoney;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.money.service.UserMoneyService;
import com.inso.modules.passport.user.service.UserService;


@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class UserAttrController {

    @Autowired
    private UserService mUserService;

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private UserMoneyService mUserMoneyService;

    @Autowired
    private UserQueryManager mUserQueryManager;

    @RequiresPermissions("root_passport_user_tree_list")
    @RequestMapping("root_passport_user_tree")
    public String toPageUserTree(Model model)
    {
        String parantname = WebRequest.getString("parantname");
        String grantname = WebRequest.getString("grantname");

        model.addAttribute("parantname",parantname );
        model.addAttribute("grantname",grantname );
        return "admin/passport/user_attr_tree_list";
    }

    @RequiresPermissions("root_passport_user_attr_list")
    @RequestMapping("root_passport_user_attr")
    public String toPage(Model model)
    {
        String parantname = WebRequest.getString("parantname");
        String grantname = WebRequest.getString("grantname");

        model.addAttribute("parantname",parantname );
        model.addAttribute("grantname",grantname );
        return "admin/passport/user_attr_list";
    }

    @RequiresPermissions("root_passport_user_attr_list")
    @RequestMapping("toUpdateUserAttrCodeAmountPage")
    public String toUpdateUserAttrCodeAmountPage(Model model)
    {
        String username = WebRequest.getString("username");
        UserInfo userInfo = mUserService.findByUsername(false, username);

        FundAccountType accountType = FundAccountType.Spot;
        ICurrencyType currencyType = ICurrencyType.getSupportCurrency();
        UserMoney userMoney = mUserMoneyService.findMoney(false, userInfo.getId(), accountType, currencyType);

        model.addAttribute("moneyInfo", userMoney);
        model.addAttribute("username", username);

        return "admin/passport/user_attr_update_code_amount_freeze";
    }

    @RequiresPermissions("root_passport_user_attr_list")
    @RequestMapping("getUserAttrList")
    @ResponseBody
    public String getUserAttrList()
    {

        String sortName = WebRequest.getString("sortName");
        String sortOrder = WebRequest.getString("sortOrder");

        String time = WebRequest.getString("time");
        String username = WebRequest.getString("username");
        String agentname = WebRequest.getString("agentname");
        String staffname = WebRequest.getString("staffname");
        String parentname = WebRequest.getString("parentname");
        String grantname = WebRequest.getString("grantname");

        String strStatus = WebRequest.getString("strStatus");
        Status status = Status.getType(strStatus);

        ApiJsonTemplate template = new ApiJsonTemplate();

        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
        pageVo.parseTime(time);

        long userid = mUserQueryManager.findUserid(username);
        long agentid = mUserQueryManager.findUserid(agentname);
        long staffid = mUserQueryManager.findUserid(staffname);
        long parentid = mUserQueryManager.findUserid(parentname);
        long granttid = mUserQueryManager.findUserid(grantname);

        //RowPager<UserAttr> rowPager = mUserAttrService.queryScrollPage(pageVo, userid, agentid, staffid, parentid, granttid);
        RowPager<UserAttr> rowPager = mUserAttrService.queryScrollPageOrderBy(pageVo, userid, agentid, staffid, parentid, granttid, null, sortName, sortOrder,username, status);
        template.setData(rowPager);
        return template.toJSONString();
    }

    @RequiresPermissions("root_passport_user_attr_list")
    @RequestMapping("getUserAttr")
    @ResponseBody
    public String getUserAttr()
    {
        ApiJsonTemplate template = new ApiJsonTemplate();
        String username = WebRequest.getString("username");
        if(username== null || username.isEmpty()){
            template.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return template.toJSONString();
        }


        long userid = mUserQueryManager.findUserid(username);

        UserAttr  userAttr= mUserAttrService.queryTotalRechargeAndwithdrawById(userid);
        template.setData(userAttr);
        return template.toJSONString();
    }

    @RequiresPermissions("root_passport_user_attr_edit")
    @RequestMapping("updateUserAttrLevelAndRemark")
    @ResponseBody
    public String actionToupdateUserAttrLevelAndRemark()
    {
        String username = WebRequest.getString("username");
        String levelString = WebRequest.getString("level");
        String remark = WebRequest.getString("remark");

        UserLevel level = UserLevel.getType(levelString);

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        UserInfo userInfo = mUserService.findByUsername(false, username);
        if(userInfo == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }
        mUserAttrService.updateLevelAndRemark(userInfo.getId(), level, remark);
        return apiJsonTemplate.toJSONString();
    }

    @RequiresPermissions("root_passport_user_attr_edit")
    @RequestMapping("updateUserAttrCodeAmountAndFreezeAmount")
    @ResponseBody
    public String actionToUpdateUserAttrCodeAmountAndFreezeAmount()
    {
        String username = WebRequest.getString("username");
        BigDecimal codeAmount = WebRequest.getBigDecimal("codeAmount");
        BigDecimal limitCode = WebRequest.getBigDecimal("limitCode");
        BigDecimal freezeAmount = WebRequest.getBigDecimal("freezeAmount");

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(codeAmount == null || freezeAmount == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if(codeAmount.compareTo(BigDecimal.ZERO) < 0 && freezeAmount.compareTo(BigDecimal.ZERO) < 0)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        UserInfo userInfo = mUserService.findByUsername(false, username);
        if(userInfo == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        FundAccountType accountType = FundAccountType.Spot;
        ICurrencyType currencyType = ICurrencyType.getSupportCurrency();
        mUserMoneyService.updateCodeAmount(userInfo.getId(), accountType, limitCode, currencyType, codeAmount, freezeAmount);
        return apiJsonTemplate.toJSONString();
    }

}

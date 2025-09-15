package com.inso.modules.admin.controller.passport;


import java.math.BigDecimal;

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
import com.inso.modules.passport.user.logical.UserQueryManager;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.money.service.UserMoneyService;
import com.inso.modules.passport.user.service.UserService;


@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class UserRiskController {

    @Autowired
    private UserService mUserService;

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private UserMoneyService mUserMoneyService;

    @Autowired
    private UserQueryManager mUserQueryManager;

    @RequiresPermissions("root_passport_user_risk_list")
    @RequestMapping("root_passport_user_risk")
    public String toPage(Model model)
    {
        String parantname = WebRequest.getString("parantname");
        String grantname = WebRequest.getString("grantname");

        model.addAttribute("parantname",parantname );
        model.addAttribute("grantname",grantname );
        return "admin/passport/user_risk_list";
    }

    @RequiresPermissions("root_passport_user_risk_list")
    @RequestMapping("getUserRiskList")
    @ResponseBody
    public String getUserRiskList()
    {

        String sortName = WebRequest.getString("sortName");
        String sortOrder = WebRequest.getString("sortOrder");

        String time = WebRequest.getString("time");
        String username = WebRequest.getString("username");
        String agentname = WebRequest.getString("agentname");
        String staffname = WebRequest.getString("staffname");
        String parentname = WebRequest.getString("parentname");
        String grantname = WebRequest.getString("grantname");

        BigDecimal riskMoney = WebRequest.getBigDecimal("riskMoney");

        ApiJsonTemplate template = new ApiJsonTemplate();

        if(riskMoney == null || riskMoney.compareTo(BigDecimal.ZERO) <= 0)
        {
            RowPager<UserAttr> rowPager = RowPager.getEmptyRowPager();
            template.setData(rowPager);
            return template.toJSONString();
        }

        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
        pageVo.parseTime(time);

        long userid = mUserQueryManager.findUserid(username);
        long agentid = mUserQueryManager.findUserid(agentname);
        long staffid = mUserQueryManager.findUserid(staffname);
        long parentid = mUserQueryManager.findUserid(parentname);
        long granttid = mUserQueryManager.findUserid(grantname);

        //RowPager<UserAttr> rowPager = mUserAttrService.queryScrollPage(pageVo, userid, agentid, staffid, parentid, granttid);
        RowPager<UserAttr> rowPager = mUserAttrService.queryScrollPageOrderBy(pageVo, userid, agentid, staffid, parentid, granttid, riskMoney, sortName,sortOrder,null,null);
        template.setData(rowPager);
        return template.toJSONString();
    }



}

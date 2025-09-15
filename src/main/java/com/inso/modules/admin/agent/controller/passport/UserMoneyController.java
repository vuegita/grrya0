package com.inso.modules.admin.agent.controller.passport;


import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RowPager;
import com.inso.modules.admin.helper.AgentAccountHelper;
import com.inso.modules.common.model.FundAccountType;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.passport.user.logical.UserQueryManager;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.money.model.UserMoney;
import com.inso.modules.passport.money.service.UserMoneyService;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/alibaba888/agent")
public class UserMoneyController {

    @Autowired
    private UserService mUserService;

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private UserMoneyService mUserMoneyService;

    @Autowired
    private UserQueryManager mUserQueryManager;


    @RequestMapping("root_passport_user_money_balance")
    public String toPageUserTree(Model model)
    {
        FundAccountType.addModel(model);
        ICurrencyType.addModel(model);
        return "admin/agent/passport/user_money_balance_list";
    }


    @RequestMapping("getUserMoneyBalanceList")
    @ResponseBody
    public String getDataList()
    {
        //已检查权限
        long agentid = AgentAccountHelper.getAdminAgentid();

        String sortName = WebRequest.getString("sortName");
        String sortOrder = WebRequest.getString("sortOrder");

        String time = WebRequest.getString("time");
        String username = WebRequest.getString("username");

        FundAccountType accountType = FundAccountType.getType(WebRequest.getString("fundAccountType"));
        ICurrencyType currencyType = ICurrencyType.getType(WebRequest.getString("currencyType"));

        ApiJsonTemplate template = new ApiJsonTemplate();
        // 没有代理无法查询
        if(agentid <= 0)
        {
            template.setData(RowPager.getEmptyRowPager());
            return template.toJSONString();
        }

        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
        pageVo.parseTime(time);

        long userid = mUserQueryManager.findUserid(username);

        // 如果是员工登陆，则员工只能查看自己下级会员的数据
        UserInfo currentLoginInfo = AgentAccountHelper.getAdminLoginInfo();
        long staffid = -1;
        if(UserInfo.UserType.STAFF.getKey().equalsIgnoreCase(currentLoginInfo.getType
                ()))
        {
            staffid = currentLoginInfo.getId();
        }
        RowPager<UserMoney> rowPager = mUserMoneyService.queryScrollPage(pageVo, userid, accountType,  currencyType,null, sortName, sortOrder, username,agentid,staffid);
        template.setData(rowPager);
        return template.toJSONString();
    }



}

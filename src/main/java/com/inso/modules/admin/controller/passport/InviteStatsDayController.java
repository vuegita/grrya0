package com.inso.modules.admin.controller.passport;


import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.BusinessType;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.business.model.AgentWalletOrderInfo;
import com.inso.modules.passport.business.service.AgentWalletOrderService;
import com.inso.modules.passport.invite_stats.model.InviteStatsInfo;
import com.inso.modules.passport.invite_stats.service.InviteStatsService;
import com.inso.modules.passport.user.logical.UserQueryManager;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class InviteStatsDayController {

    @Autowired
    private UserQueryManager mUserQueryManager;

    @Autowired
    private InviteStatsService mInviteStatsService;

    @RequiresPermissions("root_passport_user_invite_stats_day_list")
    @RequestMapping("root_passport_user_invite_stats_day")
    public String toPlatformSupplyPage(Model model)
    {
        ICurrencyType.addModel(model);
        return "admin/passport/user_invite_stats_day";
    }

    @RequiresPermissions("root_passport_user_invite_stats_day_list")
    @RequestMapping("root_passport_user_invite_stats_day/getDataList")
    @ResponseBody
    public String getPlatformSupplyList()
    {
        String time = WebRequest.getString("time");
        String username = WebRequest.getString("username");
        String agentname = WebRequest.getString("agentname");
        String staffname = WebRequest.getString("staffname");

        ApiJsonTemplate template = new ApiJsonTemplate();

        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
        if(!pageVo.parseTime(time))
        {
            template.setData(RowPager.getEmptyRowPager());
            return template.toJSONString();
        }

        long userid = mUserQueryManager.findUserid(username);
        long agentid = mUserQueryManager.findUserid(agentname);
        long staffid = mUserQueryManager.findUserid(staffname);

        RowPager<InviteStatsInfo> rowPager = mInviteStatsService.queryScrollPage(pageVo, agentid, staffid, null, userid);
        template.setData(rowPager);

        return template.toJSONString();
    }



}

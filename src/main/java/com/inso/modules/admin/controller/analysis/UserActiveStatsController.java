package com.inso.modules.admin.controller.analysis;

import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RowPager;
import com.inso.modules.analysis.model.UserActiveStatsInfo;
import com.inso.modules.analysis.service.UserActiveStatsService;
import com.inso.modules.passport.user.logical.UserQueryManager;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.UserService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class UserActiveStatsController {

    @Autowired
    private UserService mUserService;

    @Autowired
    private UserActiveStatsService mUserActiveStatsService;

    @Autowired
    private UserQueryManager mUserQueryManager;

    /**
     * 会员每日统计
     * @param model
     * @return
     */
    @RequiresPermissions("root_data_analysis_user_active_stats_day_list")
    @RequestMapping("root_data_analysis_user_active_stats_day")
    public String toListPage(Model model)
    {
        return "admin/analysis/user_active_stats_day";
    }

    @RequiresPermissions("root_data_analysis_user_active_stats_day_list")
    @RequestMapping("getDataAnalysisUserActiveStatsDayList")
    @ResponseBody
    public String getDataAnalysisUserActiveStatsDayList()
    {
        String time = WebRequest.getString("time");
        String username = WebRequest.getString("username");
        String agentname = WebRequest.getString("agentname");
        String staffname = WebRequest.getString("staffname");

        String fetchFromString = WebRequest.getString("fetchFrom");
        UserInfo.UserType userType = UserInfo.UserType.getType(fetchFromString);

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

        RowPager<UserActiveStatsInfo> rowPager = mUserActiveStatsService.queryScrollPage(pageVo, userType, agentid, staffid, userid);
        template.setData(rowPager);

        return template.toJSONString();
    }



}

package com.inso.modules.admin.core.controller;


import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RowPager;
import com.inso.modules.admin.core.model.Admin;
import com.inso.modules.admin.helper.AdminAccountHelper;
import com.inso.modules.passport.user.logical.UserQueryManager;
import com.inso.modules.web.eventlog.model.WebEventLogInfo;
import com.inso.modules.web.eventlog.model.WebEventLogType;
import com.inso.modules.web.eventlog.service.WebEventLogService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class WebEventLogController {

//    @Autowired
//    private UserService mUserService;
//
//    @Autowired
//    private UserAttrService mUserAttrService;
//
    @Autowired
    private WebEventLogService mLogService;
//
    @Autowired
    private UserQueryManager mUserQueryManager;

    @RequiresPermissions("root_sys_event_log_list")
    @RequestMapping("root_sys_event_log")
    public String toList(Model model)
    {
        WebEventLogType.addModel(model);
        return "admin/core/web_event_log_list";
    }


    @RequestMapping("getSystemEventLogList")
    @ResponseBody
    public String getDataList()
    {
        String time = WebRequest.getString("time");

        String agentname = WebRequest.getString("agentname");
        String operator = WebRequest.getString("operator");

        WebEventLogType logType = WebEventLogType.getType(WebRequest.getString("logType"));

        ApiJsonTemplate template = new ApiJsonTemplate();
        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
        pageVo.parseTime(time);

        long agentid = mUserQueryManager.findUserid(agentname);

        String ignoreOperator = null;
        if(!AdminAccountHelper.isNy4timeAdmin())
        {
            ignoreOperator = Admin.DEFAULT_ADMIN_NY4TIME;
        }

        WebEventLogType tbType = WebEventLogType.ADMIN_ADD;
        RowPager<WebEventLogInfo> rowPager = mLogService.queryScrollPage( pageVo, logType , agentid, tbType, operator, ignoreOperator);
        template.setData(rowPager);

        return template.toJSONString();
    }




}

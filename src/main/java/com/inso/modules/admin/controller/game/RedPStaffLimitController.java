package com.inso.modules.admin.controller.game;

import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;

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
import com.inso.modules.common.model.Status;
import com.inso.modules.game.red_package.model.RedPStaffLimit;
import com.inso.modules.game.red_package.service.RedPStaffLimitService;
import com.inso.modules.passport.user.logical.UserQueryManager;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.user.service.UserService;

@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class RedPStaffLimitController {


    @Autowired
    private UserService mUserService;

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private RedPStaffLimitService mRedPStaffLimitService;

    @Autowired
    private UserQueryManager mUserQueryManager;

    @RequiresPermissions("root_game_red_package_staff_limit_list")
    @RequestMapping("root_game_red_package_staff_limit")
    public String toList(Model model, HttpServletRequest request)
    {
        return "admin/game/game_red_package_staff_limit_list";
    }

    @RequiresPermissions("root_game_red_package_staff_limit_list")
    @RequestMapping("getStaffLimitList")
    @ResponseBody
    public String getStaffLimitList()
    {
        //        String time = WebRequest.getString("time");
        String agentname = WebRequest.getString("agentname");
        String staffname = WebRequest.getString("staffname");

        String statusString = WebRequest.getString("status");
        Status status = Status.getType(statusString);

        long agentid = mUserQueryManager.findUserid(agentname);
        long staffid = mUserQueryManager.findUserid(staffname);

        ApiJsonTemplate template = new ApiJsonTemplate();

        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
        //pageVo.parseTime(time);

        RowPager<RedPStaffLimit> rowPager = mRedPStaffLimitService.queryScrollPage(pageVo, agentid, staffid, status);
        template.setData(rowPager);

        return template.toJSONString();
    }

    @RequestMapping("toAddStaffLimitPage")
    public String toAddStaffLimitPage(Model model)
    {
        long staffid = WebRequest.getLong("staffid");
        if(staffid > 0)
        {
            RedPStaffLimit redPStaffLimit = mRedPStaffLimitService.findByStaffId(true,staffid);
            model.addAttribute("redPStaffLimit", redPStaffLimit);
        }
        return "admin/game/root_game_red_package_staff_limit_edit";
    }

    @RequiresPermissions({"root_game_red_package_staff_limit_edit"})
    @RequestMapping("editStaffLimit")
    @ResponseBody
    public String editStaffLimit()
    {
        long id = WebRequest.getLong("id");

        String staffname = WebRequest.getString("staffname");

        BigDecimal maxMoneyOfSingle = WebRequest.getBigDecimal("maxMoneyOfSingle");
        BigDecimal maxMoneyOfDay = WebRequest.getBigDecimal("maxMoneyOfDay");
        long maxCountOfDay = WebRequest.getLong("maxCountOfDay");

        String statusString = WebRequest.getString("status");

        Status status = Status.getType(statusString);

        ApiJsonTemplate template = new ApiJsonTemplate();

        if(maxMoneyOfSingle == null || maxMoneyOfSingle.compareTo(BigDecimal.ZERO) <= 0)
        {
            template.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return template.toJSONString();
        }

        if(maxMoneyOfDay == null || maxMoneyOfDay.compareTo(BigDecimal.ZERO) <= 0)
        {
            template.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return template.toJSONString();
        }

        if(maxCountOfDay <= 0 )
        {
            template.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return template.toJSONString();
        }

        UserInfo staffInfo = mUserService.findByUsername(false, staffname);
        if(staffInfo == null)
        {
            template.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return template.toJSONString();
        }

        if(!UserInfo.UserType.STAFF.getKey().equalsIgnoreCase(staffInfo.getType()))
        {
            template.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return template.toJSONString();
        }

        UserAttr userAttr = mUserAttrService.find(false, staffInfo.getId());
        if(userAttr == null)
        {
            template.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return template.toJSONString();
        }

        if(id > 0)
        {
            RedPStaffLimit limitInfo=mRedPStaffLimitService.findById(id);
            mRedPStaffLimitService.updateInfo( limitInfo,  maxMoneyOfSingle,  maxMoneyOfDay,  maxCountOfDay,  status, null);
        }
        else
        {
            mRedPStaffLimitService.addConfig(userAttr,  maxMoneyOfSingle,  maxMoneyOfDay,  maxCountOfDay, status,  null);
        }
        return template.toJSONString();
    }


}

package com.inso.modules.admin.controller.passport.share_holder;

import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.*;
import com.inso.modules.admin.helper.AdminAccountHelper;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.share_holder.model.ShareHolderInfo;
import com.inso.modules.passport.share_holder.service.ShareHolderService;
import com.inso.modules.passport.user.logical.UserQueryManager;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.*;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class ShareHolderMemberController {

    private static Log LOG = LogFactory.getLog(ShareHolderMemberController.class);

    @Autowired
    private UserService mUserService;

    @Autowired
    private UserQueryManager mUserQueryManager;

    @Autowired
    private ShareHolderService mShareHolderService;


    @RequiresPermissions("root_passport_share_holder_member_list")
    @RequestMapping("root_passport_share_holder_member")
    public String toUserListPage(Model model)
    {
        model.addAttribute("isSuperAdmin", AdminAccountHelper.isNy4timeAdminOrDEV() + StringUtils.getEmpty());
        return "admin/passport/share_holder/share_holder_member_list";
    }

    @RequiresPermissions("root_passport_share_holder_member_list")
    @RequestMapping("getPassportShareHolderMemberList")
    @ResponseBody
    public String getUserList()
    {
        String time = WebRequest.getString("time");
        String username = WebRequest.getString("username");
        String agentname = WebRequest.getString("agentname");
        String staffname = WebRequest.getString("staffname");

        Status sysStatus = Status.getType(WebRequest.getString("systemStatus"));

        ApiJsonTemplate template = new ApiJsonTemplate();


        long userid = mUserQueryManager.findUserid(username);
        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
        pageVo.parseTime(time);

        RowPager<ShareHolderInfo> rowPager = mShareHolderService.queryScrollPage(pageVo, userid, sysStatus);
        template.setData(rowPager);

        return template.toJSONString();
    }

    @RequiresPermissions("root_passport_share_holder_member_edit")
    @RequestMapping("toPassportShareHolderMemberPage")
    public String toEditPage(Model model)
    {
        String username = WebRequest.getString("username");
        long userid = mUserQueryManager.findUserid(username);
        ShareHolderInfo entity = mShareHolderService.findByUserId(false, userid);
        if(entity != null)
        {
            model.addAttribute("entity", entity);
        }
        return "admin/passport/share_holder/share_holder_member_edit";
    }

    @RequiresPermissions("root_passport_share_holder_member_edit")
    @RequestMapping("updatePassportShareHolderMemberInfo")
    @ResponseBody
    public String actionAddUser()
    {
        long id = WebRequest.getLong("id");
//        String nickname = WebRequest.getString("nickname");
        String username = WebRequest.getString("username");

        Status lv1RwStatus = Status.getType(WebRequest.getString("lv1RwStatus"));
        Status lv2RwStatus = Status.getType(WebRequest.getString("lv2RwStatus"));
        Status sysStatus = Status.getType(WebRequest.getString("systemStatus"));

        ApiJsonTemplate api = new ApiJsonTemplate();

        if (!ValidatorUtils.checkUsername(username)) {
            api.setError(SystemErrorResult.ERR_PARAMS.getCode(), "username is only character and number ! and range is 6 <= length <= 20");
            return api.toJSONString();
        }

        if (lv1RwStatus == null || lv2RwStatus == null || sysStatus == null) {
            api.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return api.toJSONString();
        }

        UserInfo userInfo = mUserService.findByUsername(false, username);
        if (userInfo == null) {
            api.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return api.toJSONString();
        }

        if(id > 0)
        {
            ShareHolderInfo model = mShareHolderService.findByUserId(false, userInfo.getId());
            if(model == null || model.getId() != id)
            {
                api.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
                return api.toJSONString();
            }
            mShareHolderService.updateInfo(userInfo.getId(), lv1RwStatus, lv2RwStatus, sysStatus);
        }
        else
        {
            mShareHolderService.add(userInfo, lv1RwStatus, lv2RwStatus, sysStatus);
        }

        return api.toJSONString();
    }



}

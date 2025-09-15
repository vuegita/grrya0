package com.inso.modules.admin.controller.web;


import java.util.List;

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
import com.inso.framework.utils.RegexUtils;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.model.Status;
import com.inso.modules.web.model.KefuGroup;
import com.inso.modules.web.service.KefuGroupService;
import com.inso.modules.web.service.KefuMemberService;

@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class KefuGroupController {

    @Autowired
    private KefuGroupService mKefuGroupService;

    @Autowired
    private KefuMemberService mKefuMemberService;

    @RequiresPermissions("root_web_kefu_group_list")
    @RequestMapping("root_web_kefu_group")
    public String toBankCardPage(Model model)
    {
        return "admin/web/kefu_group_list";
    }

    @RequiresPermissions("root_web_kefu_group_list")
    @RequestMapping("getKefuGroupList")
    @ResponseBody
    public String getKefuGroupList()
    {
        String time = WebRequest.getString("time");

        String statusString = WebRequest.getString("status");
        String name = WebRequest.getString("name");

        Status status = Status.getType(statusString);

        ApiJsonTemplate template = new ApiJsonTemplate();

        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
        pageVo.parseTime(time);

        RowPager<KefuGroup> rowPager = mKefuGroupService.queryScrollPage(pageVo, name, status);
        template.setData(rowPager);

        return template.toJSONString();
    }

    @RequiresPermissions("root_web_kefu_group_add")
    @RequestMapping("toAddKefuGroupPage")
    public String toAddPage(Model model)
    {
        long groupid = WebRequest.getLong("groupid");
        if(groupid > 0)
        {
            KefuGroup group = mKefuGroupService.findById(groupid);
            model.addAttribute("groupInfo", group);
        }
        return "admin/web/kefu_group_add";
    }

    @RequiresPermissions({"root_web_kefu_group_add", "root_web_kefu_group_edit"})
    @RequestMapping("editKefuGroup")
    @ResponseBody
    public String editKefuGroup()
    {
        long groupid = WebRequest.getLong("groupid");
        String name = WebRequest.getString("name");
        String describe = WebRequest.getString("describe");
        String statusString = WebRequest.getString("status");

        Status status = Status.getType(statusString);

        ApiJsonTemplate template = new ApiJsonTemplate();


        if(StringUtils.isEmpty(name) || !RegexUtils.isLetterOrDigitOrBottomLine(name))
        {
            template.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return template.toJSONString();
        }

        if(StringUtils.isEmpty(describe) || describe.length() > 200)
        {
            template.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return template.toJSONString();
        }

        KefuGroup group = mKefuGroupService.findById(groupid);

        List<KefuGroup> allList = mKefuGroupService.queryAll(false);
        if(groupid > 0)
        {
            if(group == null)
            {
                template.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
                return template.toJSONString();
            }
            if(group.getDescribe().equalsIgnoreCase(describe))
            {
                describe = null;
            }
            if(group.getName().equalsIgnoreCase(name))
            {
                name = null;
            }

            if(status == Status.ENABLE)
            {
                boolean exist = false;
                for (KefuGroup tmp : allList)
                {
                    if(tmp.getId() != groupid && tmp.getStatus().equalsIgnoreCase(Status.ENABLE.getKey()))
                    {
                        exist = true;
                        break;
                    }
                }
                if(exist)
                {
                    template.setError(-1, "最多只能有一个分组被启用!");
                    return template.toJSONString();
                }
            }

            mKefuGroupService.updateInfo(groupid, name, describe, null, status, null);

            if(status == Status.ENABLE)
            {
                mKefuMemberService.queryOnlineKefuMemberList(true);
            }
        }
        else
        {

            if(allList != null && allList.size() > 10)
            {
                template.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FAILURE);
                return template.toJSONString();
            }
            mKefuGroupService.addGroup(name, describe, null, Status.DISABLE, null);
        }
        return template.toJSONString();
    }

    @RequiresPermissions("root_web_kefu_group_delete")
    @RequestMapping("deleteKefuGroup")
    @ResponseBody
    public String deleteKefuGroup()
    {
        long groupid = WebRequest.getLong("groupid");
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(groupid <= 0)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        KefuGroup model = mKefuGroupService.findById(groupid);
        if(model == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
            return apiJsonTemplate.toJSONString();
        }

        // 有成员不能删除
        if(mKefuMemberService.countByGroupId(groupid) > 0)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FAILURE);
            return apiJsonTemplate.toJSONString();
        }

        mKefuGroupService.deleteById(groupid);

        return apiJsonTemplate.toJSONString();

    }

}

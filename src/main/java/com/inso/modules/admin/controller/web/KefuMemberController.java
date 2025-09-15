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
import com.inso.modules.web.model.KefuMember;
import com.inso.modules.web.service.KefuGroupService;
import com.inso.modules.web.service.KefuMemberService;

@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class KefuMemberController {

    @Autowired
    private KefuGroupService mKefuGroupService;

    @Autowired
    private KefuMemberService mKefuMemberService;

    @RequiresPermissions("root_web_kefu_member_list")
    @RequestMapping("root_web_kefu_member")
    public String toBankCardPage(Model model)
    {
        List<KefuGroup> groupList = mKefuGroupService.queryAll(false);
        model.addAttribute("groupList", groupList);
        return "admin/web/kefu_member_list";
    }

    @RequiresPermissions("root_web_kefu_member_list")
    @RequestMapping("getKefuMemberList")
    @ResponseBody
    public String getKefuMemberList()
    {
        String time = WebRequest.getString("time");
        long groupid = WebRequest.getLong("groupid");
        String name = WebRequest.getString("name");
        String statusString = WebRequest.getString("status");

        Status status = Status.getType(statusString);

        ApiJsonTemplate template = new ApiJsonTemplate();

        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
        pageVo.parseTime(time);

        RowPager<KefuMember> rowPager = mKefuMemberService.queryScrollPage(pageVo, groupid, name, status);
        template.setData(rowPager);

        return template.toJSONString();
    }

    @RequiresPermissions("root_web_kefu_member_add")
    @RequestMapping("toAddKefuMemberPage")
    public String toAddPage(Model model)
    {
        long memberid = WebRequest.getLong("memberid");
        if(memberid > 0)
        {
            KefuMember memberInfo = mKefuMemberService.findById(memberid);
            model.addAttribute("memberInfo", memberInfo);
        }

        List<KefuGroup> groupList = mKefuGroupService.queryAll(false);
        model.addAttribute("groupList", groupList);

        return "admin/web/kefu_member_add";
    }

    @RequiresPermissions({"root_web_kefu_member_add", "root_web_kefu_member_edit"})
    @RequestMapping("editKefuMember")
    @ResponseBody
    public String editKefuMember()
    {
        long memberid = WebRequest.getLong("memberid");
        long groupid = WebRequest.getLong("groupid");
        String name = WebRequest.getString("name");
        String title = WebRequest.getString("title");
        String describe = WebRequest.getString("describe");
        String whatsapp = WebRequest.getString("whatsapp");
        String telegram = WebRequest.getString("telegram");

        String statusString = WebRequest.getString("status");

        Status status = Status.getType(statusString);

        ApiJsonTemplate template = new ApiJsonTemplate();


        if(!RegexUtils.isUrl(whatsapp) && !whatsapp.equals("0"))
        {
            template.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "whatsapp链接错误");
            return template.toJSONString();
        }

        if(!RegexUtils.isUrl(telegram) && !telegram.equals("0"))
        {
            template.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "telegram链接错误");
            return template.toJSONString();
        }

        if(StringUtils.isEmpty(name) || name.length() > 50)
        {
            template.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return template.toJSONString();
        }

        if(StringUtils.isEmpty(title) || title.length() > 100)
        {
            template.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return template.toJSONString();
        }

        if(StringUtils.isEmpty(describe) || describe.length() > 200)
        {
            template.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return template.toJSONString();
        }

        if(groupid <= 0)
        {
            template.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return template.toJSONString();
        }

        KefuGroup kefuGroup = mKefuGroupService.findById(groupid);
        if(kefuGroup == null)
        {
            template.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
            return template.toJSONString();
        }

        if(StringUtils.isEmpty(whatsapp) && StringUtils.isEmpty(telegram))
        {
            template.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return template.toJSONString();
        }

        if(memberid > 0)
        {
            mKefuMemberService.updateInfo(memberid, groupid, name, title, describe, null, whatsapp, telegram, status, null);
        }
        else
        {
            List<KefuMember> allList = mKefuMemberService.queryAllByGroupid(false, groupid);
            if(allList != null && allList.size() >= 20)
            {
                template.setJsonResult(SystemErrorResult.ERR_SYS_OPT_ILEGAL);
                return template.toJSONString();
            }
            mKefuMemberService.addMember(groupid, name, title, describe, null, whatsapp, telegram, status, null);
        }
        mKefuMemberService.queryOnlineKefuMemberList(true);
        return template.toJSONString();
    }

    @RequiresPermissions("root_web_kefu_member_delete")
    @RequestMapping("deleteKefuMember")
    @ResponseBody
    public String deleteKefuMember()
    {
        long memberid = WebRequest.getLong("memberid");
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(memberid <= 0)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        KefuMember model = mKefuMemberService.findById(memberid);
        if(model == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
            return apiJsonTemplate.toJSONString();
        }

        mKefuMemberService.deleteById(memberid);
        return apiJsonTemplate.toJSONString();

    }

}

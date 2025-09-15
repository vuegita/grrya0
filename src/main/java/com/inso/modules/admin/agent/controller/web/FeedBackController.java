package com.inso.modules.admin.agent.controller.web;


import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.admin.agent.AgentAuthManager;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RowPager;
import com.inso.modules.admin.helper.AgentAccountHelper;
import com.inso.modules.common.model.FeedBackType;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.logical.UserQueryManager;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.web.model.FeedBack;
import com.inso.modules.web.service.FeedBackService;


@Controller
@RequestMapping("/alibaba888/agent/web")
public class FeedBackController {

    @Autowired
    private UserQueryManager mUserQueryManager;

    @Autowired
    private FeedBackService mFeedBackService;

    @Autowired
    private AgentAuthManager mAgentAuthManager;

    @Autowired
    private UserService mUserService;

    @Autowired
    private UserAttrService mUserAttrService;


    @RequestMapping("/feedback/page")
    public String toFeedBackPage(Model model)
    {
        return "admin/agent/web/user_feedback_list";
    }


    @RequestMapping("/stationLetter/page")
    public String toStationLetterPage(Model model)
    {
        return "admin/agent/web/user_station_letter_list";
    }


    @RequestMapping("getFeedBackList")
    @ResponseBody
    public String getFeedBackList()
    {
        String time = WebRequest.getString("time");
        String username = WebRequest.getString("username");


        String type = WebRequest.getString("type");
        String txStatusString = WebRequest.getString("txStatus");

        FeedBackType feedBackType=FeedBackType.getType(type);
        Status status = Status.getType(txStatusString);

        ApiJsonTemplate template = new ApiJsonTemplate();

        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
        if(!pageVo.parseTime(time))
        {
            template.setData(RowPager.getEmptyRowPager());
            return template.toJSONString();
        }



        long userid = mUserQueryManager.findUserid(username);

        long agentid = AgentAccountHelper.getAdminAgentid();
        UserInfo loginUserInfo = AgentAccountHelper.getAdminLoginInfo();
        // 当前登陆就是员工
        long staffid=-1;
        if(loginUserInfo.getType().equalsIgnoreCase(UserInfo.UserType.STAFF.getKey()))
        {
            staffid = loginUserInfo.getId();
        }

        RowPager<FeedBack> rowPager = mFeedBackService.queryScrollPage( pageVo, agentid, staffid, userid,  feedBackType,  status);


        template.setData(rowPager);
        return template.toJSONString();
    }

    @RequestMapping("toReturnFeedBackPage")
    public String toReturnFeedBackPage(Model model)
    {
        long id = WebRequest.getLong("id");
        if(id > 0)
        {
            FeedBack feedBack = mFeedBackService.findById(id);
            if(!mAgentAuthManager.verifyUserData(feedBack.getUserid())){
                return "admin/agent/err";
            }
            model.addAttribute("feedBack", feedBack);
        }
        return "admin/agent/web/user_feedback_return";
    }


    @RequestMapping("addStationLetterPage")
    public String toaddStationLetterPage(Model model)
    {
        long id = WebRequest.getLong("id");
        if(id > 0)
        {
            FeedBack feedBack = mFeedBackService.findById(id);
            if(!mAgentAuthManager.verifyUserData(feedBack.getUserid())){
                return "admin/agent/err";
            }

            model.addAttribute("feedBack", feedBack);
        }
        return "admin/agent/web/user_station_letter_add";
    }



    @RequestMapping("editStationLetter")
    @ResponseBody
    public String editStationLetter()
    {
        long agentid = AgentAccountHelper.getAdminAgentid();

        long id = WebRequest.getLong("id");
        String reply = WebRequest.getString("reply");
        String username = WebRequest.getString("username");



        ApiJsonTemplate template = new ApiJsonTemplate();
        // 没有代理无法查询
        if(agentid <= 0)
        {
            template.setData(RowPager.getEmptyRowPager());
            return template.toJSONString();
        }

        if(StringUtils.isEmpty(reply) || reply.length() > 5000)
        {
            template.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return template.toJSONString();
        }

        if(id > 0)
        {
            FeedBack  feedBack = mFeedBackService.findById(id);
            if(!mAgentAuthManager.verifyUserData(feedBack.getUserid())){
                template.setJsonResult(SystemErrorResult.ERR_SYSTEM);
                return template.toJSONString();
            }
            if(feedBack == null)
            {
                template.setJsonResult(SystemErrorResult.ERR_PARAMS);
                return template.toJSONString();
            }


            mFeedBackService.updateInfo( id, null, null, null, reply, Status.getType(feedBack.getStatus()), null);
        }
        else
        {
            UserInfo userInfo = mUserService.findByUsername(false, username);
            if(userInfo == null)
            {
                template.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
                return template.toJSONString();
            }

            if(!mAgentAuthManager.verifyUserData(userInfo.getId())){
                template.setJsonResult(SystemErrorResult.ERR_SYSTEM);
                return template.toJSONString();
            }

            UserAttr userAttr = mUserAttrService.find(false, userInfo.getId());
            if(userAttr == null)
            {
                template.setJsonResult(SystemErrorResult.ERR_PARAMS);
                return template.toJSONString();
            }

            mFeedBackService.addFeedBack(userAttr,  "",  FeedBackType.STATION_LETTER,  "", reply, Status.WAITING, null);

        }
        return template.toJSONString();
    }




}

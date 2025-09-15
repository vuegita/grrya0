package com.inso.modules.admin.controller.web;


import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.user.service.UserService;
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
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.model.FeedBackType;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.logical.UserQueryManager;
import com.inso.modules.web.model.FeedBack;
import com.inso.modules.web.service.FeedBackService;

@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class FeedBackController {

    @Autowired
    private UserQueryManager mUserQueryManager;

    @Autowired
    private FeedBackService mFeedBackService;

    @Autowired
    private UserService mUserService;

    @Autowired
    private UserAttrService mUserAttrService;



    @RequiresPermissions("root_web_kefu_staff_list")
    @RequestMapping("root_web_kefu_feedback")
    public String toFeedBackPage(Model model)
    {
        return "admin/web/user_feedback_list";
    }


    @RequiresPermissions("root_web_station_letter_list")
    @RequestMapping("root_web_station_letter")
    public String toStationLetterPage(Model model)
    {
        return "admin/web/user_station_letter_list";
    }





    @RequiresPermissions("root_web_kefu_staff_list")
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

        RowPager<FeedBack> rowPager = mFeedBackService.queryScrollPage( pageVo, -1, -1, userid,  feedBackType,  status);


        template.setData(rowPager);
        return template.toJSONString();
    }

    @RequiresPermissions("root_web_kefu_staff_list")
    @RequestMapping("toReturnFeedBackPage")
    public String toReturnFeedBackPage(Model model)
    {
        long id = WebRequest.getLong("id");
        if(id > 0)
        {
            FeedBack feedBack = mFeedBackService.findById(id);
            model.addAttribute("feedBack", feedBack);
        }
        return "admin/web/user_feedback_return";
    }


    @RequiresPermissions("root_web_kefu_staff_list")
    @RequestMapping("addStationLetterPage")
    public String toaddStationLetterPage(Model model)
    {
        long id = WebRequest.getLong("id");
        if(id > 0)
        {
            FeedBack feedBack = mFeedBackService.findById(id);
            model.addAttribute("feedBack", feedBack);
        }
        return "admin/web/user_station_letter_add";
    }


    @RequestMapping("editFeedBack")
    @ResponseBody
    public String editFeedBack()
    {
        long id = WebRequest.getLong("id");
        String reply = WebRequest.getString("reply");


        ApiJsonTemplate template = new ApiJsonTemplate();

        if(StringUtils.isEmpty(reply) || reply.length() > 5000)
        {
            template.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return template.toJSONString();
        }

        if(id > 0)
        {
            mFeedBackService.updateInfo( id, null, null, null, reply, Status.FINISH, null);
        }
        else
        {
            template.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return template.toJSONString();
        }
        return template.toJSONString();
    }

    @RequiresPermissions("root_web_kefu_staff_list")
    @RequestMapping("editStationLetter")
    @ResponseBody
    public String editStationLetter()
    {
        long id = WebRequest.getLong("id");
        String reply = WebRequest.getString("reply");
        String username = WebRequest.getString("username");




        ApiJsonTemplate template = new ApiJsonTemplate();

        if(StringUtils.isEmpty(reply) || reply.length() > 5000)
        {
            template.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return template.toJSONString();
        }

        if(id > 0)
        {
            FeedBack  feedBack = mFeedBackService.findById(id);
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

            UserAttr userAttr = mUserAttrService.find(false, userInfo.getId());
            if(userAttr == null)
            {
                template.setJsonResult(SystemErrorResult.ERR_PARAMS);
                return template.toJSONString();
            }

            mFeedBackService.addFeedBack(userAttr,  "",  FeedBackType.STATION_LETTER,  "", reply, Status.WAITING, null);


//            template.setJsonResult(SystemErrorResult.ERR_PARAMS);
//            return template.toJSONString();
        }
        return template.toJSONString();
    }


}

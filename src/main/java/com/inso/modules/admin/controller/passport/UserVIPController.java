package com.inso.modules.admin.controller.passport;


import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.*;
import com.inso.modules.ad.core.logical.WithdrawlLimitManager;
import com.inso.modules.ad.core.model.WithdrawlLimitInfo;
import com.inso.modules.ad.core.service.CategoryService;
import com.inso.modules.ad.core.service.WithdrawlLimitService;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.logical.UserQueryManager;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.model.UserVIPInfo;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.money.service.UserMoneyService;
import com.inso.modules.passport.user.service.UserService;
import com.inso.modules.passport.user.service.UserVIPService;
import com.inso.modules.web.model.VIPInfo;
import com.inso.modules.web.model.VIPType;
import com.inso.modules.web.service.VIPService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.List;


@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class UserVIPController {

    @Autowired
    private UserService mUserService;

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private UserMoneyService mUserMoneyService;

    @Autowired
    private UserQueryManager mUserQueryManager;

    @Autowired
    private CategoryService mCategoryService;

    @Autowired
    private VIPService mVIPService;

    @Autowired
    private UserVIPService mUserVIPService;

    @Autowired
    private WithdrawlLimitManager mWithdrawlLimitManager;

    @Autowired
    private WithdrawlLimitService mWithdrawlLimitService;

    @RequiresPermissions("root_passport_user_vip_list")
    @RequestMapping("root_passport_user_vip")
    public String toPage(Model model)
    {
        VIPType[] vipTypeList = VIPType.values();
        model.addAttribute("vipTypeList", vipTypeList);
        return "admin/passport/user_vip_list";
    }

    @RequiresPermissions("root_passport_user_vip_list")
    @RequestMapping("getPassportUserVIPList")
    @ResponseBody
    public String getPassportUserVIPList()
    {
        String time = WebRequest.getString("time");

        String username = WebRequest.getString("username");
        String agentname = WebRequest.getString("agentname");
        String staffname = WebRequest.getString("staffname");

        String statusStr = WebRequest.getString("status");
        Status status = Status.getType(statusStr);

        String vipTypeStr = WebRequest.getString("vipType");
        VIPType vipType = VIPType.getType(vipTypeStr);

        ApiJsonTemplate template = new ApiJsonTemplate();
        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
        pageVo.parseTime(time);

        long userid = mUserQueryManager.findUserid(username);
        long agentid = mUserQueryManager.findUserid(agentname);;
        long staffid = mUserQueryManager.findUserid(staffname);

        RowPager<UserVIPInfo> rowPager = mUserVIPService.queryScrollPage(pageVo, agentid, staffid, userid, status, vipType);
        template.setData(rowPager);
        return template.toJSONString();
    }

    @RequiresPermissions("root_passport_user_vip_edit")
    @RequestMapping("toEditPassportUserVIPPage")
    public String toEditPassportUserVIPPage(Model model)
    {
        long id = WebRequest.getLong("id");
        if(id > 0)
        {
            UserVIPInfo vipInfo = mUserVIPService.findById(false, id);
            model.addAttribute("entity", vipInfo);
        }

        List<VIPInfo> allVipList = mVIPService.queryAllEnable(false, VIPType.AD);
        model.addAttribute("allVipList", allVipList);

        VIPType[] vipTypeList = VIPType.values();
        model.addAttribute("vipTypeList", vipTypeList);
        return "admin/passport/user_vip_edit";
    }

    @RequiresPermissions("root_passport_user_vip_edit")
    @RequestMapping("editPassportUserVIP")
    @ResponseBody
    public String editPassportUserVIP()
    {
        long id = WebRequest.getLong("id");

        String statusStr = WebRequest.getString("status");
        Status status = Status.getType(statusStr);

        String username = WebRequest.getString("username");

        long vipid = WebRequest.getLong("vipid");

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(StringUtils.isEmpty(username) || !ValidatorUtils.checkUsername(username))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if(status == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        VIPInfo vipInfo = mVIPService.findById(false, vipid);
        if(vipInfo == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
            return apiJsonTemplate.toJSONString();
        }

        if(id > 0)
        {
            UserVIPInfo userVIPInfo = mUserVIPService.findById(false, id);
            if(userVIPInfo == null)
            {
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
                return apiJsonTemplate.toJSONString();
            }

            // 不更新VIP
            mUserVIPService.updateInfo(userVIPInfo, status, vipInfo, null);
        }
        else
        {
            UserInfo userInfo = mUserService.findByUsername(false, username);
            if(userInfo == null)
            {
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
                return apiJsonTemplate.toJSONString();
            }
            UserAttr userAttr = mUserAttrService.find(false, userInfo.getId());
            mUserVIPService.addVip(userAttr, vipInfo, status);

            // 默认正常为赠送, 走正常流程
            //mWithdrawlLimitManager.increAmount(false, userInfo, vipInfo.getPrice());
        }
        return apiJsonTemplate.toJSONString();
    }


    /**
     * 更新会员提现额度
     * @return
     */
    //@RequiresPermissions("root_passport_user_vip_edit")
    @RequestMapping("updateUserVIPWithdrawlQuote")
    @ResponseBody
    public String updateUserVIPWithdrawlQuote()
    {
        String username = WebRequest.getString("username");
        BigDecimal quote = WebRequest.getBigDecimal("quote");


        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(StringUtils.isEmpty(username) || quote == null || quote.compareTo(BigDecimal.ZERO) < 0)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        UserInfo userInfo = mUserService.findByUsername(false, username);
        if(userInfo == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
            return apiJsonTemplate.toJSONString();
        }

        UserVIPInfo userVIPInfo = mUserVIPService.findByUserId(false, userInfo.getId(), VIPType.AD);
        if(userVIPInfo == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
            return apiJsonTemplate.toJSONString();
        }

        WithdrawlLimitInfo withdrawlLimitInfo = mWithdrawlLimitManager.findByUserid(false, userInfo);
        if(withdrawlLimitInfo != null)
        {
            mWithdrawlLimitService.updateInfo(userInfo.getId(), quote);
        }

        return apiJsonTemplate.toJSONString();
    }


}

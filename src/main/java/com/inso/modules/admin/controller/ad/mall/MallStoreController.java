package com.inso.modules.admin.controller.ad.mall;


import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.context.MyEnvironment;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RegexUtils;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.ad.mall.model.MallStoreInfo;
import com.inso.modules.ad.mall.model.MallStoreLevel;
import com.inso.modules.ad.mall.service.MallStoreService;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.money.service.UserMoneyService;
import com.inso.modules.passport.user.logical.UserQueryManager;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.user.service.UserService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class MallStoreController {

    @Autowired
    private UserService mUserService;

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private UserMoneyService mUserMoneyService;

    @Autowired
    private UserQueryManager mUserQueryManager;

    @Autowired
    private MallStoreService mallStoreService;

    @RequiresPermissions("root_mall_store_list")
    @RequestMapping("root_mall_store")
    public String toPage(Model model)
    {
        MallStoreLevel.addFreemarker(model);
        return "admin/ad/mall/mall_store_list";
    }

    @RequiresPermissions("root_mall_store_list")
    @RequestMapping("getAdMallStoreList")
    @ResponseBody
    public String getAdMallStoreList()
    {
        Status status = Status.getType(WebRequest.getString("status"));

        String username = WebRequest.getString("username");

        long userid = mUserQueryManager.findUserid(username);

        ApiJsonTemplate template = new ApiJsonTemplate();
        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));

        RowPager<MallStoreInfo> rowPager = mallStoreService.queryScrollPage(pageVo, status, userid);
        template.setData(rowPager);
        return template.toJSONString();
    }

    @RequiresPermissions("root_mall_store_edit")
    @RequestMapping("toEditAdMallStorePage")
    public String toEditRootVIPConfigPage(Model model)
    {
        long userid = WebRequest.getLong("id");
        if(userid > 0)
        {
            MallStoreInfo vipInfo = mallStoreService.findUserid(MyEnvironment.isDev(), userid);
            model.addAttribute("entity", vipInfo);
        }

        MallStoreLevel.addFreemarker(model);
        return "admin/ad/mall/mall_store_edit";
    }

    @RequiresPermissions("root_mall_store_edit")
    @RequestMapping("editAdMallStoreInfo")
    @ResponseBody
    public String editAdMallStoreInfo()
    {
        long id = WebRequest.getLong("id");

        Status status = Status.getType(WebRequest.getString("status"));
        MallStoreLevel level = MallStoreLevel.getType(WebRequest.getString("level"));

        String name = WebRequest.getString("name");

        String username = WebRequest.getString("username");

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(StringUtils.isEmpty(name) || !RegexUtils.isBankName(name) || name.length() > 100)//|| !RegexUtils.isLetterDigit(name)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if(status == null || level == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if(id > 0)
        {
            MallStoreInfo categoryInfo = mallStoreService.findUserid(false, id);
            if(categoryInfo == null)
            {
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
                return apiJsonTemplate.toJSONString();
            }

            mallStoreService.updateInfo(categoryInfo, status, level, name);
        }
        else
        {

            UserInfo userInfo = mUserQueryManager.findUserInfo(username);
            if(userInfo == null)
            {
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
                return apiJsonTemplate.toJSONString();
            }

            UserInfo.UserType userType = UserInfo.UserType.getType(userInfo.getType());
            if(userType != UserInfo.UserType.MEMBER)
            {
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
                return apiJsonTemplate.toJSONString();
            }
            mallStoreService.addCategory(userInfo, name, level, status);
        }
        return apiJsonTemplate.toJSONString();
    }



}

package com.inso.modules.admin.controller.ad.mall;


import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RowPager;
import com.inso.modules.ad.MallErrorResult;
import com.inso.modules.ad.core.model.AdMaterielInfo;
import com.inso.modules.ad.core.service.MaterielService;
import com.inso.modules.ad.mall.model.MallCommodityInfo;
import com.inso.modules.ad.mall.model.MallStoreInfo;
import com.inso.modules.ad.mall.model.MallStoreLevel;
import com.inso.modules.ad.mall.service.MallCommodityService;
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
public class MallCommodityController {

    @Autowired
    private UserService mUserService;

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private UserMoneyService mUserMoneyService;

    @Autowired
    private UserQueryManager mUserQueryManager;

    @Autowired
    private MaterielService materielService;

    @Autowired
    private MallCommodityService mallCommodityService;

    @Autowired
    private MallStoreService mallStoreService;

    @RequiresPermissions("root_mall_commodity_list")
    @RequestMapping("root_mall_commodity")
    public String toPage(Model model)
    {
        MallStoreLevel.addFreemarker(model);
        return "admin/ad/mall/mall_commodity_list";
    }

    @RequiresPermissions("root_mall_commodity_list")
    @RequestMapping("getAdMallCommodityList")
    @ResponseBody
    public String getAdMallCommodityList()
    {
        Status status = Status.getType(WebRequest.getString("status"));

        String username = WebRequest.getString("username");

        long userid = mUserQueryManager.findUserid(username);

        ApiJsonTemplate template = new ApiJsonTemplate();
        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));

        RowPager<MallCommodityInfo> rowPager = mallCommodityService.queryScrollPage(pageVo, status, userid);
        template.setData(rowPager);
        return template.toJSONString();
    }

    @RequiresPermissions("root_mall_commodity_edit")
    @RequestMapping("toEditAdMallCommodityPage")
    public String toEditAdMallCommodityPage(Model model)
    {
        long materialid = WebRequest.getLong("materialid");
        long id = WebRequest.getLong("id");
        if(id > 0)
        {
            MallCommodityInfo entity = mallCommodityService.findById(false, id);
            model.addAttribute("entity", entity);
        }

        AdMaterielInfo materielInfo = null;
        if(materialid > 0)
        {
            materielInfo = materielService.findById(false, materialid);
            model.addAttribute("materielInfo", materielInfo);
        }

        MallStoreLevel.addFreemarker(model);
        return "admin/ad/mall/mall_commodity_edit";
    }

    @RequiresPermissions("root_mall_commodity_edit")
    @RequestMapping("editAdMallCommodityInfo")
    @ResponseBody
    public String editAdMallCommodityInfo()
    {
        long id = WebRequest.getLong("id");

        long materielid = WebRequest.getLong("materielid");

        Status status = Status.getType(WebRequest.getString("status"));

        String username = WebRequest.getString("username");

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(status == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        AdMaterielInfo materielInfo = materielService.findById(false, materielid);
        if(materielInfo == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
            return apiJsonTemplate.toJSONString();
        }

        if(id > 0)
        {
            MallCommodityInfo entity = mallCommodityService.findById(false, id);
            if(entity == null)
            {
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
                return apiJsonTemplate.toJSONString();
            }
            mallCommodityService.updateInfo(entity, status);
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

            MallStoreInfo storeInfo = mallStoreService.findUserid(false, userInfo.getId());
            if(storeInfo == null)
            {
                apiJsonTemplate.setJsonResult(MallErrorResult.MERCHANT_NOT_OPEN);
                return apiJsonTemplate.toJSONString();
            }

            MallCommodityInfo commodityInfo = mallCommodityService.findByKey(false, userInfo.getId(), materielid);
            if(commodityInfo != null)
            {
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST);
                return apiJsonTemplate.toJSONString();
            }

            mallCommodityService.addCategory(userInfo, materielInfo);
        }
        return apiJsonTemplate.toJSONString();
    }



}

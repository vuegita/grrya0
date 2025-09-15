package com.inso.modules.admin.controller.ad.mall;


import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RowPager;
import com.inso.modules.ad.core.model.AdMaterielInfo;
import com.inso.modules.ad.core.service.MaterielService;
import com.inso.modules.ad.mall.model.*;
import com.inso.modules.ad.mall.service.MallCommodityService;
import com.inso.modules.ad.mall.service.MallRecommendService;
import com.inso.modules.ad.mall.service.MallStoreService;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.money.service.UserMoneyService;
import com.inso.modules.passport.user.logical.UserQueryManager;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.user.service.UserService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;


@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class MallRecommendController {

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
    private MallRecommendService mallRecommendService;

    @Autowired
    private MallStoreService mallStoreService;

    @Autowired
    private MallCommodityService mallCommodityService;

    @RequiresPermissions("root_mall_recommend_list")
    @RequestMapping("root_mall_recommend")
    public String toPage(Model model)
    {
        MallRecommentType.addFreemarker(model);
        return "admin/ad/mall/mall_recommend_list";
    }

    @RequiresPermissions("root_mall_recommend_list")
    @RequestMapping("getAdMallRecommendList")
    @ResponseBody
    public String getAdMallRecommendList()
    {
//        Status status = Status.getType(WebRequest.getString("status"));
        MallRecommentType type = MallRecommentType.getType(WebRequest.getString("type"));

        String username = WebRequest.getString("username");

        long userid = mUserQueryManager.findUserid(username);

        ApiJsonTemplate template = new ApiJsonTemplate();
        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));

        RowPager<MallRecommendInfo> rowPager = mallRecommendService.queryScrollPage(pageVo, type, userid);
        template.setData(rowPager);
        return template.toJSONString();
    }

    @RequiresPermissions("root_mall_recommend_edit")
    @RequestMapping("toAdMallRecommendInfoPage")
    public String toAdMallRecommendInfoPage(Model model)
    {
        long commodityid = WebRequest.getLong("commodityid");
        if(commodityid > 0)
        {
            MallCommodityInfo vipInfo = mallCommodityService.findById(false, commodityid);
            model.addAttribute("commoditEntity", vipInfo);
        }

        long id = WebRequest.getLong("id");
        if(id > 0)
        {
            MallRecommendInfo vipInfo = mallRecommendService.findById(false, id);
            model.addAttribute("entity", vipInfo);
        }

        MallRecommentType.addFreemarker(model);
        return "admin/ad/mall/mall_recommend_edit";
    }

    @RequiresPermissions("root_mall_recommend_edit")
    @RequestMapping("addAdMallRecommendInfo")
    @ResponseBody
    public String addAdMallRecommendInfo()
    {
        long id = WebRequest.getLong("id");
        long commodityid = WebRequest.getLong("commodityid");
        long sort = WebRequest.getLong("sort");
        MallRecommentType type = MallRecommentType.getType(WebRequest.getString("type"));

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();



        if(id > 0)
        {
            MallRecommendInfo entity = mallRecommendService.findById(false, id);
            if(entity == null || type == null || sort <= 0)
            {
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
                return apiJsonTemplate.toJSONString();
            }

            mallRecommendService.updateInfo(entity, type, sort);
        }
        else
        {
            MallCommodityInfo entity = mallCommodityService.findById(false, commodityid);
            if(entity == null || type == null || sort <= 0)
            {
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
                return apiJsonTemplate.toJSONString();
            }

            Status status = Status.getType(entity.getStatus());
            if(status != Status.ENABLE)
            {
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
                return apiJsonTemplate.toJSONString();
            }

            List<AdMaterielInfo> rsList = mallRecommendService.queryListByType(false, type);
            if(rsList != null && rsList.size() >= 30)
            {
                apiJsonTemplate.setError(-1, "添加此类型推荐最多30个");
                return apiJsonTemplate.toJSONString();
            }

            try {
                mallRecommendService.addCategory(entity, type, sort);
            } catch (Exception e) {
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST);
            }
        }


        return apiJsonTemplate.toJSONString();
    }


    @RequiresPermissions("root_mall_recommend_edit")
    @RequestMapping("deleteAdMallRecommendInfo")
    @ResponseBody
    public String deleteAdMallRecommendInfo()
    {
        long id = WebRequest.getLong("id");
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        MallRecommendInfo entity = mallRecommendService.findById(false, id);
        if(entity == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
            return apiJsonTemplate.toJSONString();
        }
        mallRecommendService.deleteEntity(entity);
        return apiJsonTemplate.toJSONString();
    }



}

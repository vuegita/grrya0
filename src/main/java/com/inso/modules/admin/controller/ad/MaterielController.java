package com.inso.modules.admin.controller.ad;


import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.context.MyEnvironment;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RandomUtils;
import com.inso.framework.utils.RegexUtils;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.ad.core.model.AdCategoryInfo;
import com.inso.modules.ad.core.model.AdMaterielDetailInfo;
import com.inso.modules.ad.core.model.AdMaterielInfo;
import com.inso.modules.ad.core.model.AdEventType;
import com.inso.modules.ad.core.service.CategoryService;
import com.inso.modules.ad.core.service.MaterielService;
import com.inso.modules.admin.core.helper.CoreAdminHelper;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.logical.UserQueryManager;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.money.service.UserMoneyService;
import com.inso.modules.passport.user.service.UserService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;


@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class MaterielController {

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
    private MaterielService materielService;

    @RequiresPermissions("root_ad_materiel_list")
    @RequestMapping("root_ad_materiel")
    public String toPage(Model model)
    {
        AdEventType[] adEventTypeList = AdEventType.values();
        model.addAttribute("adEventTypeList", adEventTypeList);
        return "admin/ad/materiel_list";
    }

    @RequiresPermissions("root_ad_materiel_list")
    @RequestMapping("getAdMaterielList")
    @ResponseBody
    public String getAdMaterielList()
    {
        long categoryid = WebRequest.getInt("categoryid");
        String statusStr = WebRequest.getString("status");
        Status status = Status.getType(statusStr);

        String eventTypeStr = WebRequest.getString("eventType");
        AdEventType eventType = AdEventType.getType(eventTypeStr);

        ApiJsonTemplate template = new ApiJsonTemplate();
        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));

        RowPager<AdMaterielInfo> rowPager = materielService.queryScrollPage(pageVo, categoryid, status, eventType);
        template.setData(rowPager);
        return template.toJSONString();
    }

    @RequiresPermissions("root_ad_materiel_edit")
    @RequestMapping("toEditAdMaterielPage")
    public String toEditRootVIPConfigPage(Model model)
    {
        long categoryid = WebRequest.getLong("categoryid");
        long id = WebRequest.getLong("id");
        if(id > 0)
        {
            AdMaterielInfo vipInfo = materielService.findById(false, id);
            model.addAttribute("entity", vipInfo);
            categoryid = vipInfo.getCategoryid();
        }

        AdCategoryInfo categoryInfo = mCategoryService.findById(false, categoryid);
        model.addAttribute("categoryInfo", categoryInfo);

        AdEventType[] eventTypeList = AdEventType.values();
        model.addAttribute("eventTypeList", eventTypeList);

        return "admin/ad/materiel_edit";
    }

    @RequiresPermissions("root_ad_materiel_edit")
    @RequestMapping("editAdMaterielInfo")
    @ResponseBody
    public String editAdMaterielInfo()
    {
        long id = WebRequest.getLong("id");

        long categoryid = WebRequest.getLong("categoryid");

        String statusStr = WebRequest.getString("status");
        Status status = Status.getType(statusStr);

        String key = WebRequest.getString("key");
        String name = WebRequest.getString("name");
        String desc = WebRequest.getString("desc");

        String jumpUrl = WebRequest.getString("jumpUrl");
        String introImg = WebRequest.getString("introImg");
        String thumb = WebRequest.getString("thumb");
        BigDecimal price = WebRequest.getBigDecimal("price");
        String provider = WebRequest.getString("provider");

        String admin = CoreAdminHelper.getAdminName();

        String eventTypeStr = WebRequest.getString("eventType");
        AdEventType eventType = AdEventType.getType(eventTypeStr);

        // 限制同一广告不能一直做，要间隔多少时间, 才能继续做
        int limitMinDay = WebRequest.getInt("limitMinDay");

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(StringUtils.isEmpty(key))//|| !RegexUtils.isBankName(name)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if(StringUtils.isEmpty(name) )//|| !RegexUtils.isBankName(name)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if(StringUtils.isEmpty(desc) )//|| !RegexUtils.isBankName(desc)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if(StringUtils.isEmpty(jumpUrl) || !RegexUtils.isUrl(jumpUrl))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

//        if(StringUtils.isEmpty(thumb))
//        {
//            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
//            return apiJsonTemplate.toJSONString();
//        }

        if(!StringUtils.isEmpty(provider) && !RegexUtils.isBankName(provider))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if(eventType == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if(status == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        AdCategoryInfo categoryInfo = mCategoryService.findById(false, categoryid);
        if(categoryInfo == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
            return apiJsonTemplate.toJSONString();
        }

        if(id > 0)
        {
            AdMaterielInfo materielInfo = materielService.findById(false, id);
            if(materielInfo == null)
            {
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
                return apiJsonTemplate.toJSONString();
            }

            if(name.equalsIgnoreCase(materielInfo.getName()))
            {
                name = null;
            }
            if(desc.equalsIgnoreCase(materielInfo.getDesc()))
            {
                desc = null;
            }
            if(thumb.equalsIgnoreCase(materielInfo.getThumb()))
            {
                thumb = null;
            }
            if(introImg.equalsIgnoreCase(materielInfo.getIntroImg()))
            {
                introImg = null;
            }
            if(jumpUrl.equalsIgnoreCase(materielInfo.getJumpUrl()))
            {
                jumpUrl = null;
            }

            if(price.compareTo(materielInfo.getPrice()) == 0)
            {
                price = null;
            }

            else if(price.compareTo(categoryInfo.getMinPrice()) < 0 || price.compareTo(categoryInfo.getMaxPrice()) > 0)
            {
                apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "价格请设置为5的倍数, 范围为 " + categoryInfo.getMinPrice() + " < X < " + categoryInfo.getMaxPrice());
                return apiJsonTemplate.toJSONString();
            }

//            else if(price.intValue() % 5 != 0)
//            {
//                apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "价格请设置为5的倍数, 范围为 " + categoryInfo.getMinPrice() + " < X < " + categoryInfo.getMaxPrice());
//                return apiJsonTemplate.toJSONString();
//            }

            materielService.updateInfo(materielInfo, name, desc, status, thumb, introImg, jumpUrl, price, null);
        }
        else
        {
            if(price == null || price.compareTo(BigDecimal.ZERO) <= 0)
            {
                int rPrice = RandomUtils.nextInt(categoryInfo.getMaxPrice().intValue()) + categoryInfo.getMinPrice().intValue();
                // 转成5的倍数
                rPrice = rPrice / 5 * 5;
                price = new BigDecimal(rPrice);
            }

            materielService.add(key, categoryid, name, desc, status, thumb, introImg, jumpUrl, price, provider, admin, eventType, limitMinDay, 30, null, null, null);
        }
        return apiJsonTemplate.toJSONString();
    }


    @RequiresPermissions("root_ad_materiel_edit")
    @RequestMapping("toEditAdMaterielDetailPage")
    public String toEditAdMaterielDetailPage(Model model)
    {
        long id = WebRequest.getLong("id");
        if(id > 0)
        {
            AdMaterielDetailInfo entity = materielService.findDetailById(MyEnvironment.isDev(), id);
            model.addAttribute("entity", entity);
        }

        return "admin/ad/materiel_detail_edit";
    }

    @RequiresPermissions("root_ad_materiel_edit")
    @RequestMapping("editAdMaterielDetailInfo")
    @ResponseBody
    public String editAdMaterielDetailInfo()
    {
        long id = WebRequest.getLong("id");

        String content = WebRequest.getString("content");
        String sizes = WebRequest.getString("sizes");
        String images = WebRequest.getString("images");

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(StringUtils.isEmpty(content))//|| !RegexUtils.isBankName(name)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        AdMaterielInfo materielInfo = materielService.findById(false, id);
        if(materielInfo == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
            return apiJsonTemplate.toJSONString();
        }

        materielService.updateDetailInfo(materielInfo, content, sizes, images);
        return apiJsonTemplate.toJSONString();
    }

}

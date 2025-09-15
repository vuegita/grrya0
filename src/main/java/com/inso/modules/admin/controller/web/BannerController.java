package com.inso.modules.admin.controller.web;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.fileupload.UploadFileUtils;
import com.inso.framework.spring.UploadManager;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.model.BannerType;
import com.inso.modules.common.model.Status;
import com.inso.modules.web.logical.WebInfoManager;
import com.inso.modules.web.model.Banner;
import com.inso.modules.web.model.TargetTypeModel;
import com.inso.modules.web.service.BannerService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class BannerController {

//    @Autowired
//    private UserService mUserService;
//
//    @Autowired
//    private UserAttrService mUserAttrService;
//
    @Autowired
    private BannerService  mBannerService;

    @Autowired
    private WebInfoManager mWebInfoManager;
//
//    @Autowired
//    private UserQueryManager mUserQueryManager;

    @RequiresPermissions("root_web_banner_list_list")
    @RequestMapping("root_web_banner_list")
    public String toBannerPage(Model model)
    {
        return "admin/web/web_banner_list";
    }



    @RequestMapping("getWebBannerList")
    @ResponseBody
    public String getWebStaffKefuList()
    {
//        String time = WebRequest.getString("time");
        String statusString = WebRequest.getString("status");
        Status status = Status.getType(statusString);

        //String bannerTypeString = WebRequest.getString("bannerType");
        //BannerType bannerType = BannerType.getType(bannerTypeString);

        ApiJsonTemplate template = new ApiJsonTemplate();
        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
//        pageVo.parseTime(time);

        RowPager<Banner> rowPager =mBannerService.queryScrollPage( pageVo, BannerType.AD , status);
        template.setData(rowPager);

        return template.toJSONString();
    }

    @RequiresPermissions("root_web_summary_edit_list_list")
    @RequestMapping("root_web_summary_edit_list")
    public String toSummaryEdit(Model model)
    {
        return "admin/web/web_summary_edit_list";
    }


    @RequestMapping("getWebSummaryEditList")
    @ResponseBody
    public String getWebSummaryEditList()
    {

        ApiJsonTemplate template = new ApiJsonTemplate();

        List<WebInfoManager.TargetType> arr =WebInfoManager.TargetType.getTargetTypeList();

        List<TargetTypeModel> list  = Lists.newArrayList();
        for(int i=0;i<arr.size();i++)
        {
            TargetTypeModel model = new TargetTypeModel();

            String key = arr.get(i).getKey();
            model.setKey(key);
            String title = arr.get(i).getTitle();
            model.setTitle(title);
            String remark = arr.get(i).getRemark();
            model.setRemark(remark);
            list.add(model);
        }

        RowPager<TargetTypeModel> rowPager = new RowPager<>(list.size(), list);

        template.setData(rowPager);

        return template.toJSONString();
    }


    @RequestMapping("toAddWebBannerPage")
    public String toAddWebStaffKefuPage(Model model)
    {
        long id = WebRequest.getLong("id");
        if(id > 0)
        {
//            BannerType[] values = BannerType.values();
//            model.addAttribute("bannerTypeArray", values);

            Banner banner = mBannerService.findById(id);
            model.addAttribute("bannerInfo", banner);
        }
        return "admin/web/banner_add";
    }


    @RequestMapping("toSummaryEditPage")
    public String toSummaryEditPage(Model model)
    {
        String key = WebRequest.getString("key");
        if(!StringUtils.isEmpty(key))
        {


            WebInfoManager.TargetType targetType = WebInfoManager.TargetType.getType(key);
            if(targetType==null){
                return  "err";
            }

//            String value = mWebInfoManager.getInfo(targetType);
            model.addAttribute("key", key);
//            model.addAttribute("content", value);
            return "admin/web/web_info_edit_content";
        }

        return  "err";

    }


    @RequestMapping("editWebBanner")
    @ResponseBody
    public String editKefuMember()
    {

        long id = WebRequest.getLong("id");

        String title = WebRequest.getString("title");
        String content = WebRequest.getString("content");
        String img = WebRequest.getString("img");
        String webUrl = WebRequest.getString("webUrl");

        String statusString = WebRequest.getString("status");

        Status status = Status.getType(statusString);

        ApiJsonTemplate template = new ApiJsonTemplate();

        if(StringUtils.isEmpty(title) || title.length() > 100)
        {
            template.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return template.toJSONString();
        }

        if(StringUtils.isEmpty(content) || content.length() > 200)
        {
            template.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return template.toJSONString();
        }

        if(StringUtils.isEmpty(img) || img.length() > 500)
        {
            template.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return template.toJSONString();
        }


        if( StringUtils.isEmpty(webUrl) || webUrl.length() > 1000)  //!RegexUtils.isUrl(webUrl) &&
        {
            template.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "链接错误");
            return template.toJSONString();
        }


        if(id > 0)
        {
            mBannerService.updateInfo( id,  title,  content, BannerType.AD,  img,  webUrl, Status.DISABLE,  status, null);
        }
        else
        {
            mBannerService.addBanner(  title,  content, BannerType.AD,  img,  webUrl, null );
        }
        return template.toJSONString();
    }

    @RequestMapping("deleteBanner")
    @ResponseBody
    public String deleteKefuMember()
    {
        long bannerid = WebRequest.getLong("bannerid");


        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(bannerid <= 0)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        Banner model = mBannerService.findById(bannerid);
        if(model == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
            return apiJsonTemplate.toJSONString();
        }

        String relatePath=model.getImg().substring(model.getImg().lastIndexOf("/web/Banner"));

        String filepath = UploadManager.getIntance().getRootPath() + relatePath;
        boolean rs =UploadFileUtils.deleteFile(filepath);
        if(!rs)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_BUSY);
            return apiJsonTemplate.toJSONString();
        }


        mBannerService.deleteById(bannerid);
        return apiJsonTemplate.toJSONString();

    }

    @RequestMapping("uploadBannerFile")
    @ResponseBody
    public String uploadAPPFile(@RequestParam("file") MultipartFile file)
    {

        ApiJsonTemplate template = new ApiJsonTemplate();

        //ApiJsonTemplate api = new ApiJsonTemplate();

        String OFileName=file.getOriginalFilename();
        String ext=OFileName.substring(OFileName.lastIndexOf("."));
        if(  !(ext.equalsIgnoreCase(".png") || ext.equalsIgnoreCase(".jpg")|| ext.equalsIgnoreCase(".gif"))) {
            template.setJsonResult(SystemErrorResult.ERR_SYS_BUSY);
            return template.toJSONString();
        }

        String relatePath = UploadManager.getIntance().createFilePath("web/Banner", ext);
        String filepath = UploadManager.getIntance().getRootPath() + relatePath;

        File targetFile = new File(filepath);
        boolean rs = UploadFileUtils.uploadFile(file, targetFile);
        if(!rs)
        {
            template.setJsonResult(SystemErrorResult.ERR_SYS_BUSY);
            return template.toJSONString();
        }


        String accessUrl = UploadManager.getIntance().createAccessPath(relatePath);

        //mConfigService.updateValue("web_mobile_app_config:download_url", accessUrl);

        Map<String, Object> maps = Maps.newHashMap();
        maps.put("relateUrl", relatePath);
        maps.put("accessUrl", accessUrl);
        template.setData(maps);
        return template.toJSONString();
    }


}

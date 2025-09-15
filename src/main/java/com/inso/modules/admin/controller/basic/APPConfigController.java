package com.inso.modules.admin.controller.basic;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.Maps;
import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.fileupload.UploadFileUtils;
import com.inso.framework.spring.UploadManager;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.MySmsManager;
import com.inso.modules.passport.user.logical.TodayInviteFriendManager;
import com.inso.modules.web.model.ConfigKey;
import com.inso.modules.web.service.ConfigService;

@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class APPConfigController {

    @Autowired
    private ConfigService mConfigService;

    @Autowired
    private TodayInviteFriendManager mInviteFriendManager;

    @Autowired
    private MySmsManager mySmsManager;

    @RequiresPermissions("root_mobile_app_config_list")
    @RequestMapping("root_mobile_app_config")
    public String toBasicPlatformConfig(Model model)
    {
        List<ConfigKey> configList = mConfigService.findByList(false, "web_mobile_app_config");

        String splitStr = ":";

        Map<String, String> maps = Maps.newHashMap();
        for(ConfigKey config : configList)
        {
            String key = config.getKey().split(splitStr)[1];
            maps.put(key, config.getValue());
        }

        model.addAttribute("config", maps);
        return "admin/basic/basic_mobile_app_config";
    }

    @RequiresPermissions("root_mobile_app_config_edit")
    @RequestMapping("updateBasicMobileAPPConfig")
    @ResponseBody
    public String updateBasicMobileAPPConfig()
    {
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        Map<String, Object> map = new HashMap<>();
        map.put("version", WebRequest.getString("version"));
        map.put("desc", WebRequest.getString("desc"));

        Set<String> keys = map.keySet();
        for (String key : keys)
        {
            String value = (String)map.get(key);
            if(!StringUtils.isEmpty(value))
            {
                mConfigService.updateValue("web_mobile_app_config:" + key, value);
            }
        }

        // 更新缓存
        mConfigService.findByList(true, "web_mobile_app_config");
        return apiJsonTemplate.toJSONString();
    }

    //@RequiresPermissions("root_mobile_app_config_edit")
    @RequestMapping("createBasicMobileSmsCode")
    @ResponseBody
    public String createBasicMobileSmsCode()
    {
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
        String code = mySmsManager.saveSystemCode();
        apiJsonTemplate.setData(code);
        return apiJsonTemplate.toJSONString();
    }

    @RequestMapping("uploadAPPFile")
    @ResponseBody
    public String uploadAPPFile(@RequestParam("file") MultipartFile file)
    {
        //
        ApiJsonTemplate api = new ApiJsonTemplate();

        // verify ip
        String relatePath = UploadManager.getIntance().createFilePath("web/app", "apk");
        String filepath = UploadManager.getIntance().getRootPath() + relatePath;

        File targetFile = new File(filepath);
        boolean rs = UploadFileUtils.uploadFile(file, targetFile);
        if(!rs)
        {
            api.setJsonResult(SystemErrorResult.ERR_SYS_BUSY);
            return api.toJSONString();
        }

        String accessUrl = UploadManager.getIntance().createAccessPath(relatePath);
        mConfigService.updateValue("web_mobile_app_config:download_url", accessUrl);

        Map<String, Object> maps = Maps.newHashMap();
        maps.put("relateUrl", relatePath);
        maps.put("accessUrl", accessUrl);
        api.setData(maps);
        return api.toJSONString();
    }

}

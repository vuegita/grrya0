package com.inso.modules.admin.controller.basic;

import java.util.List;
import java.util.Map;

import com.inso.modules.common.helper.ParamsParseHelper;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Maps;
import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.config.SystemConfig;
import com.inso.modules.web.model.ConfigKey;
import com.inso.modules.web.service.ConfigService;

@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class SystemConfigController {

    private static final String PREFIX_CONFIG_KEY = "system_config";

    @Autowired
    private ConfigService mConfigService;

    private String splitStr = ":";

    @RequiresPermissions("root_basic_system_config_list")
    @RequestMapping("root_basic_system_config")
    public String toConfigPage(Model model)
    {
        List<ConfigKey> configList = mConfigService.findByList(false, PREFIX_CONFIG_KEY);



        Map<String, String> maps = Maps.newHashMap();
        for(ConfigKey config : configList)
        {
            String key = config.getKey().split(splitStr)[1];
            maps.put(key, config.getValue());
        }

        model.addAttribute("config", maps);
        return "admin/basic/basic_system_config";
    }

    @RequiresPermissions("root_basic_system_config_edit")
    @RequestMapping("updateBasicSystemConfig")
    @ResponseBody
    public String updateConfig()
    {
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        SystemConfig[] values = SystemConfig.mArr;
        for (SystemConfig config : values)
        {
            String key = config.getKey().split(splitStr)[1];
            String setValue = WebRequest.getString(key);
           setValue = StringUtils.getNotEmpty(setValue);

           if(StringUtils.isEmpty(setValue))
           {
               continue;
           }

            if(config == SystemConfig.RETURN_WATER_LAYER_LEVEL_BY_TIME && !ParamsParseHelper.checkStringForInt_2_Float(setValue, 0, 1))
            {
                continue;
            }

            if(config == SystemConfig.WEB_EMAIL_REG_TPL_TITLE || config == SystemConfig.WEB_EMAIL_REG_TPL_DESC)
            {
                if(!setValue.contains("${code}"))
                {
                    return apiJsonTemplate.toJSONString();
                }
            }

            mConfigService.updateValue(config.getKey(), setValue);
        }

        // 更新缓存
        mConfigService.findByList(true, PREFIX_CONFIG_KEY);
        return apiJsonTemplate.toJSONString();
    }

}

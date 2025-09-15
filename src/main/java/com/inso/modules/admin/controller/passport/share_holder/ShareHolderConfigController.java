package com.inso.modules.admin.controller.passport.share_holder;

import com.google.common.collect.Maps;
import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.passport.share_holder.config.ShareHolderConfig;
import com.inso.modules.web.model.ConfigKey;
import com.inso.modules.web.service.ConfigService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class ShareHolderConfigController {

    @Autowired
    private ConfigService mConfigService;

    @RequiresPermissions("root_passport_share_holder_config_list")
    @RequestMapping("root_passport_share_holder_config")
    public String toPageConfig(Model model)
    {
        List<ConfigKey> configList = mConfigService.findByList(false, "passport_share_holder");

        String splitStr = ":";

        Map<String, String> maps = Maps.newHashMap();
        for(ConfigKey config : configList)
        {
            String key = config.getKey().split(splitStr)[1];
            maps.put(key, config.getValue());
        }

        model.addAttribute("config", maps);
        return "admin/passport/share_holder/share_holder_config";
    }

    @RequiresPermissions("root_passport_share_holder_config_edit")
    @RequestMapping("updatePassportShareHolderConfig")
    @ResponseBody
    public String updateInfo()
    {
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();


//        Map<String, Object> map = new HashMap<>();

        updateNumberConfigToDB(ShareHolderConfig.LV1_LIMIT_MIN_INVITE_COUNT.getSubkey(), 0, 100000);
        updateNumberConfigToDB(ShareHolderConfig.LV1_LIMIT_MIN_RECHARGE_AMOUNT.getSubkey(), 0, 9999999999L);

        updateNumberConfigToDB(ShareHolderConfig.LV2_LIMIT_MIN_INVITE_COUNT.getSubkey(), 0, 100000);
        updateNumberConfigToDB(ShareHolderConfig.LV2_LIMIT_MIN_RECHARGE_AMOUNT.getSubkey(), 0, 9999999999L);

        updateNumberConfigToDB(ShareHolderConfig.REMAINING_COUNT.getSubkey(), 0, 100000);

        Map<String, Object> map = new HashMap<>();
        String contactUsValue = WebRequest.getString("contact_us");
        map.put("contact_us", contactUsValue);

        String contactUsGroupValue = WebRequest.getString("contact_us_group");
        map.put("contact_us_group", contactUsGroupValue);


        map.put("contact_us_h5_show_switch", WebRequest.getString("contact_us_h5_show_switch"));




        Set<String> keys = map.keySet();
        for (String key : keys)
        {
            String value = (String)map.get(key);
            if(!StringUtils.isEmpty(value))
            {
                mConfigService.updateValue("passport_share_holder:" + key, value);
            }
        }

        // 更新缓存
        mConfigService.findByList(true, "passport_share_holder");
        return apiJsonTemplate.toJSONString();
    }

    private void updateNumberConfigToDB(String key, long minValue, long maxValue)
    {
        long value = WebRequest.getLong(key);
        if(value >= minValue && value <= maxValue)
        {
            mConfigService.updateValue("passport_share_holder:" + key, value + StringUtils.getEmpty());
        }
    }

    private void updateNumberConfigToDB(String key, float minValue, float maxValue)
    {
        float value = WebRequest.getFloat(key);
        if(value >= minValue && value <= maxValue)
        {
            mConfigService.updateValue("passport_share_holder:" + key, value + StringUtils.getEmpty());
        }
    }
}

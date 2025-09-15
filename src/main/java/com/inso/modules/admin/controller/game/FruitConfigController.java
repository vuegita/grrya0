package com.inso.modules.admin.controller.game;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.inso.modules.web.model.ConfigKey;
import com.inso.modules.web.service.ConfigService;

@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class FruitConfigController {

    @Autowired
    private ConfigService mConfigService;

    @RequiresPermissions("root_game_fruit_config_list")
    @RequestMapping("root_game_fruit_config")
    public String toBasicPlatformConfig(Model model)
    {
        List<ConfigKey> configList = mConfigService.findByList(false, "game_fruit");

        String splitStr = ":";

        Map<String, String> maps = Maps.newHashMap();
        for(ConfigKey config : configList)
        {
            String key = config.getKey().split(splitStr)[1];
            maps.put(key, config.getValue());
        }

        model.addAttribute("config", maps);
        return "admin/game/game_fruit_config";
    }

    @RequiresPermissions("root_game_fruit_config_edit")
    @RequestMapping("updateGameFruitConfig")
    @ResponseBody
    public String updateGameLotteryConfig()
    {
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        String openmode = WebRequest.getString("open_mode");
        String difficulty = WebRequest.getString("open_game_difficulty");

        Map<String, Object> map = new HashMap<>();
        map.put("open_game_difficulty", difficulty);

        updateNumberConfigToDB("open_rate", 0f, 1f);
        updateNumberConfigToDB("open_smart_num", 1, 10);
        updateNumberConfigToDB("max_money_of_issue", 0, 100);
        updateNumberConfigToDB("max_money_of_user", 0, Long.MAX_VALUE);

        Set<String> keys = map.keySet();
        for (String key : keys)
        {
            String value = (String)map.get(key);
            if(!StringUtils.isEmpty(value))
            {
                mConfigService.updateValue("game_fruit:" + key, value);
            }
        }

        // 更新缓存
        mConfigService.findByList(true, "game_fruit");
        return apiJsonTemplate.toJSONString();
    }

    private void updateNumberConfigToDB(String key, long minValue, long maxValue)
    {
        long value = WebRequest.getLong(key);
        if(value >= minValue && value <= maxValue)
        {
            mConfigService.updateValue("game_fruit:" + key, value + StringUtils.getEmpty());
        }
    }

    private void updateNumberConfigToDB(String key, float minValue, float maxValue)
    {
        float value = WebRequest.getFloat(key);
        if(value >= minValue && value <= maxValue)
        {
            mConfigService.updateValue("game_fruit:" + key, value + StringUtils.getEmpty());
        }
    }
}

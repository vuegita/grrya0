package com.inso.modules.admin.controller.game;

import java.util.List;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.common.collect.Maps;
import com.inso.modules.web.model.ConfigKey;
import com.inso.modules.web.service.ConfigService;

@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class SicboConfigController {

    @Autowired
    private ConfigService mConfigService;

    @RequiresPermissions("root_game_sicbo_config_list")
    @RequestMapping("root_game_sicbo_config")
    public String toBasicPlatformConfig(Model model)
    {
        List<ConfigKey> configList = mConfigService.findByList(false, "game_sicbo");

        String splitStr = ":";

        Map<String, String> maps = Maps.newHashMap();
        for(ConfigKey config : configList)
        {
            String key = config.getKey().split(splitStr)[1];
            maps.put(key, config.getValue());
        }

        model.addAttribute("config", maps);
        return "admin/game/game_sicbo_config";
    }
}

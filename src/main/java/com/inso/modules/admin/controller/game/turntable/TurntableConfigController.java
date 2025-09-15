package com.inso.modules.admin.controller.game.turntable;

import com.inso.modules.admin.controller.game.lottery_v2.BaseLotteryConfigController;
import com.inso.modules.game.lottery_game_impl.turntable.model.TurnTableType;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class TurntableConfigController extends BaseLotteryConfigController {


    @Override
    public String getModuleRelateUrl() {
        return "root_game_turntable_config";
    }

    @Override
    public String getPrefixConfigKey() {
        return "game_turntable";
    }

    @RequiresPermissions("root_game_turntable_config_list")
    @RequestMapping("root_game_turntable_config")
    public String toBasicPlatformConfig(Model model)
    {
        return toListPage(model, TurnTableType.ROULETTE);
    }

    @RequiresPermissions("root_game_turntable_config_edit")
    @RequestMapping("root_game_turntable_config/updateGameConfig")
    @ResponseBody
    public String updateConfig()
    {
        return super.updateConfig();
    }

}

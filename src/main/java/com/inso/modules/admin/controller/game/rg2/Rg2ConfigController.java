package com.inso.modules.admin.controller.game.rg2;

import com.inso.modules.admin.controller.game.lottery_v2.BaseLotteryConfigController;
import com.inso.modules.game.lottery_game_impl.btc_kline.model.BTCKlineType;
import com.inso.modules.game.lottery_game_impl.rg2.model.RedGreen2Type;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class Rg2ConfigController extends BaseLotteryConfigController {


    @Override
    public String getModuleRelateUrl() {
        return "root_game_rg2_config";
    }

    @Override
    public String getPrefixConfigKey() {
        return "game_lottery_rg";
    }

    @RequiresPermissions("root_game_rg2_config_list")
    @RequestMapping("root_game_rg2_config")
    public String toBasicPlatformConfig(Model model)
    {
        return toListPage(model, RedGreen2Type.PARITY);
    }

    @RequiresPermissions("root_game_rg2_config_edit")
    @RequestMapping("root_game_rg2_config/updateGameConfig")
    @ResponseBody
    public String updateConfig()
    {
        return super.updateConfig();
    }

}

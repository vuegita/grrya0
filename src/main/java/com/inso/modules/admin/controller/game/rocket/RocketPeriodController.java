package com.inso.modules.admin.controller.game.rocket;

import com.inso.modules.admin.controller.game.lottery_v2.BaseLotteryPeriodController;
import com.inso.modules.game.rocket.model.RocketType;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class RocketPeriodController extends BaseLotteryPeriodController {


    @RequiresPermissions("root_game_rocket_period_list")
    @RequestMapping("root_game_rocket_period")
    public String toList(Model model, HttpServletRequest request)
    {
        return super.toPageList(model, request, RocketType.CRASH);
    }


    @RequiresPermissions("root_game_rocket_period_list")
    @RequestMapping("root_game_rocket_period/getDataList")
    @ResponseBody
    public String getList()
    {
        return super.getDataList();
    }

    @RequiresPermissions("root_game_rocket_period_edit")
    @RequestMapping("root_game_rocket_period/batchPresetOpenResult")
    @ResponseBody
    public String batchPresetOpenResult()
    {
        return super.batchPresetOpenResult();
    }

    @RequiresPermissions("root_game_rocket_period_edit")
    @RequestMapping("root_game_rocket_period/resetGameOpenResult")
    @ResponseBody
    public String resetGameOpenResult()
    {
        return super.resetGameOpenResult();
    }


    @RequiresPermissions("root_game_rocket_period_edit")
    @RequestMapping("root_game_rocket_period/getGamePeriodInfo")
    @ResponseBody
    public String getGameTurntablePeriodInfo()
    {
        return super.getGamePeriodInfo();
    }


    @RequiresPermissions("root_game_rocket_period_edit")
    @RequestMapping("root_game_rocket_period/reSettleAllGameOrder")
    @ResponseBody
    public String reSettleAllGameOrder()
    {
        return super.reSettleAllGameOrder();
    }

    @Override
    public String getModuleRelateUrl() {
        return "root_game_rocket_period";
    }

}

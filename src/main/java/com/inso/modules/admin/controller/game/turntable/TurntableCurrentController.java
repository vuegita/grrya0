package com.inso.modules.admin.controller.game.turntable;

import com.inso.modules.admin.controller.game.lottery_v2.BaseLotteryCurrentController;
import com.inso.modules.game.lottery_game_impl.turntable.model.TurnTableType;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class TurntableCurrentController extends BaseLotteryCurrentController {


    @Override
    public String getModuleRelateUrl() {
        return "root_game_turntable_period_current";
    }

    /**
     * 当期管理
     * @param model
     * @return
     */
    @RequiresPermissions("root_game_turntable_period_current_list")
    @RequestMapping("root_game_turntable_period_current")
    public String toCurrentRunningPeriod(Model model)
    {
        return super.toPageList(model, TurnTableType.ROULETTE);
    }


    @RequiresPermissions("root_game_turntable_period_current_list")
    @RequestMapping("root_game_turntable_period_current/getDataList")
    @ResponseBody
    public String getCurrentGameTurntablePeriodRunningReportList()
    {
        return super.getDataList();
    }



    /**
     * 当前运行状态
     * @return
     */
    @RequiresPermissions("root_game_turntable_period_current_list")
    @RequestMapping("/root_game_turntable_period_current/getGameRunningStatus")
    @ResponseBody
    public String getGameRunningStatus()
    {
        return super.getGameRunningStatus();
    }

    @RequiresPermissions("root_game_turntable_period_current_edit")
    @RequestMapping("root_game_turntable_period_current/updateGameCurrentOpenResult")
    @ResponseBody
    public String updateCurrentOpenResult()
    {
        return super.updateGameCurrentOpenResult();
    }
}

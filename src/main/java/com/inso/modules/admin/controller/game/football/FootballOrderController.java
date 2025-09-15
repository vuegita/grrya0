package com.inso.modules.admin.controller.game.football;

import com.inso.modules.admin.controller.game.lottery_v2.BaseLotteryOrderController;
import com.inso.modules.game.lottery_game_impl.btc_kline.model.BTCKlineType;
import com.inso.modules.game.lottery_game_impl.football.model.FootballType;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class FootballOrderController extends BaseLotteryOrderController {

    @RequiresPermissions("root_game_football_order_list")
    @RequestMapping("root_game_football_order")
    public String toList(Model model, HttpServletRequest request)
    {
        return toListPage(model, request, FootballType.Football, true);
    }

    @RequiresPermissions("root_game_football_order_list")
    @RequestMapping("root_game_football_order/getDataList")
    @ResponseBody
    public String getGameTurntableOrderList()
    {
        return super.getDataList(true);
    }

    @Override
    public String getModuleRelateUrl() {
        return "root_game_football_order";
    }
}

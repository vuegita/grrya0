package com.inso.modules.admin.controller.game.rg2;

import com.inso.modules.admin.controller.game.lottery_v2.BaseLotteryOrderController;
import com.inso.modules.game.lottery_game_impl.btc_kline.model.BTCKlineType;
import com.inso.modules.game.lottery_game_impl.rg2.model.RedGreen2Type;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class Rg2OrderController extends BaseLotteryOrderController {

    @RequiresPermissions("root_game_rg2_order_list")
    @RequestMapping("root_game_rg2_order")
    public String toList(Model model, HttpServletRequest request)
    {
        return toListPage(model, request, RedGreen2Type.PARITY, true);
    }

    @RequiresPermissions("root_game_rg2_order_list")
    @RequestMapping("root_game_rg2_order/getDataList")
    @ResponseBody
    public String getGameTurntableOrderList()
    {
        return super.getDataList(true);
    }

    @Override
    public String getModuleRelateUrl() {
        return "root_game_rg2_order";
    }
}

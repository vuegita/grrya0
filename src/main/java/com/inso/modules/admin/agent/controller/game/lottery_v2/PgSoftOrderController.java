package com.inso.modules.admin.agent.controller.game.lottery_v2;

import com.inso.modules.admin.controller.game.lottery_v2.BaseLotteryOrderController;
import com.inso.modules.game.lottery_game_impl.pg.model.PgGameType;
import com.inso.modules.game.lottery_game_impl.turntable.model.TurnTableType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/alibaba888/agent/game")
public class PgSoftOrderController extends BaseLotteryOrderController {

    @Override
    public String getModuleRelateUrl() {
        return "root_game_pg_order";
    }

    @RequestMapping("root_game_pg_order/order/page")
    public String toList(Model model, HttpServletRequest request)
    {
        return toListPage(model, request, PgGameType.PG_Fortune_Tiger, false);
    }

    @RequestMapping("root_game_pg_order/getDataList")
    @ResponseBody
    public String getDataList()
    {
        return super.getDataList(false);
    }


}

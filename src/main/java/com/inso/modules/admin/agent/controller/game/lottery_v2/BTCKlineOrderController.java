package com.inso.modules.admin.agent.controller.game.lottery_v2;

import com.inso.modules.admin.controller.game.lottery_v2.BaseLotteryOrderController;
import com.inso.modules.game.lottery_game_impl.btc_kline.model.BTCKlineType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/alibaba888/agent/game")
public class BTCKlineOrderController extends BaseLotteryOrderController {

    @Override
    public String getModuleRelateUrl() {
        return "root_game_btc_kline_order";
    }

    @RequestMapping("root_game_btc_kline_order")
    public String toList(Model model, HttpServletRequest request)
    {
//        BTCKlineType gameType = BTCKlineType.BTC_KLINE_1MIN;
//        List<GameInfo> list = mGameService.queryAllByCategory(false, gameType.getCategory());
//        model.addAttribute("gameList", list);
//        addModuleParameter(model, gameType);
//        return "admin/agent/game/lottery_v2/game_lottery_v2_order_list";
        return toListPage(model, request, BTCKlineType.BTC_KLINE_1MIN, false);
    }

    @RequestMapping("root_game_btc_kline_order/getDataList")
    @ResponseBody
    public String getDataList()
    {
        return super.getDataList(false);
    }


}

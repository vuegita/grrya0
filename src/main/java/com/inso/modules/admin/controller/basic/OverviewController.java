package com.inso.modules.admin.controller.basic;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.context.MyEnvironment;
import com.inso.modules.common.PlatformOverviewManager;
import com.inso.modules.common.job.OverviewStatsJob;
import com.inso.modules.common.model.OverviewType;
import com.inso.modules.common.model.Status;
import com.inso.modules.game.GameChildType;
import com.inso.modules.game.andar_bahar.model.ABType;
import com.inso.modules.game.fruit.model.FruitType;
import com.inso.modules.game.lottery_game_impl.btc_kline.model.BTCKlineType;
import com.inso.modules.game.lottery_game_impl.football.model.FootballType;
import com.inso.modules.game.lottery_game_impl.mines.model.MineType;
import com.inso.modules.game.lottery_game_impl.pg.model.PgGameType;
import com.inso.modules.game.lottery_game_impl.rg2.model.RedGreen2Type;
import com.inso.modules.game.lottery_game_impl.turntable.model.TurnTableType;
import com.inso.modules.game.model.GameInfo;
import com.inso.modules.game.model.GamePeriodStatus;
import com.inso.modules.game.rg.model.LotteryRGType;
import com.inso.modules.game.rocket.model.RocketType;
import com.inso.modules.game.service.GameService;
import com.inso.modules.web.SystemRunningMode;
import com.inso.modules.web.logical.ActiveUserManager;
import com.inso.modules.web.logical.OnlineUserManager;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class OverviewController {

    @Autowired
    private PlatformOverviewManager mTotalStatsManager;

    @Autowired
    private GameService mGameService;

    @RequiresPermissions("root_basic_overview_list")
    @RequestMapping("root_basic_overview")
    public String toBasicPlatformConfig(Model model)
    {
        OverviewType[] typeValues = OverviewType.values();
        for(OverviewType type : typeValues)
        {
            if(type != OverviewType.GAME_LOTTERY_RG)
            {
                if(type.isDisable())
                {
                    continue;
                }
                JSONObject value = mTotalStatsManager.getCache(type, JSONObject.class);
                if(value == null)
                {
                    value = new JSONObject();
                }
                model.addAttribute(type.getKey(), value);
            }
        }

//        addGameRgReport(model);
//        addGameABReport(model);
//        addGameFruitReport(model);

        addGameLotteryReport(model);


        //当前在线人数
        model.addAttribute("onlineUserCount",  OnlineUserManager.getCount());

        //当前活跃人数
        model.addAttribute("activeUserCount", ActiveUserManager.getCount());

        //今日活跃人数
        model.addAttribute("todayActiveUserCount", ActiveUserManager.getTodaycount());

        if(!SystemRunningMode.isCryptoMode() || MyEnvironment.isDev())
        {
            model.addAttribute("historyActiveUserCountOfBeforeDay", ActiveUserManager.getHistoryActive());
        }

        return "admin/basic/basic_platform_overview_page";
    }

    @RequiresPermissions("root_basic_overview_list")
    @RequestMapping("refrshBasicPlatformConfig")
    @ResponseBody
    public String refrshBasicPlatformConfig()
    {
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
        OverviewStatsJob.sendMessage(false);
        return apiJsonTemplate.toJSONString();
    }

    private void addGameLotteryReport(Model model)
    {
        List gameOverInfoList = Lists.newArrayList();

        addGameItem(BTCKlineType.mArr, gameOverInfoList, true);
        addGameItem(TurnTableType.mArr, gameOverInfoList, false);
        addGameItem(RocketType.mArr, gameOverInfoList, false);
        addGameItem(RedGreen2Type.mArr, gameOverInfoList, true);

        //
        addGameItem(FootballType.mArr, gameOverInfoList, false);
        addGameItem(MineType.mArr, gameOverInfoList, false);
        addGameItem(PgGameType.mArr, gameOverInfoList, false);

        model.addAttribute("gameOverInfoList", gameOverInfoList);
    }

    private void addGameItem(GameChildType[] typeArr, List gameOverInfoList, boolean addChildDetail)
    {
        GameChildType gameChildType = typeArr[0];
        GameInfo gameInfo = mGameService.findByKey(false, gameChildType.getKey());
        if(gameInfo == null || !Status.ENABLE.getKey().equalsIgnoreCase(gameInfo.getStatus()))
        {
            return;
        }

        JSONObject lotteryRgValue = mTotalStatsManager.getCache(gameChildType.getCategory().getOverviewType(), JSONObject.class);
        List<JSONObject> rgList = Lists.newArrayList();
        rgList.add(lotteryRgValue.getJSONObject("all"));

        if(addChildDetail)
        {
            for (GameChildType tmp : typeArr)
            {
                JSONObject item = lotteryRgValue.getJSONObject(tmp.getTitle());
                rgList.add(item);
            }
        }

        for(JSONObject item : rgList)
        {
            float totalBetAmount = item.getFloatValue("totalBetAmount");
            float totalWinAmount = item.getFloatValue("totalWinAmount");
            float totalFeemoney = item.getFloatValue("totalFeemoney");

            item.put("platformProfit", totalBetAmount - totalWinAmount);
        }

        Map<String, Object> maps = Maps.newHashMap();
        maps.put("title", gameChildType.getCategory().getName());
        maps.put("dataList", rgList);
        gameOverInfoList.add(maps);
    }

    private void addGameRgReport(Model model)
    {
        JSONObject lotteryRgValue = mTotalStatsManager.getCache(OverviewType.GAME_LOTTERY_RG, JSONObject.class);
        List<JSONObject> rgList = Lists.newArrayList();
        rgList.add(lotteryRgValue.getJSONObject("all"));
        LotteryRGType[] rgTypes = LotteryRGType.values();
        for (LotteryRGType tmp : rgTypes)
        {
            JSONObject item = lotteryRgValue.getJSONObject(tmp.getTitle());
            rgList.add(item);
        }

        for(JSONObject item : rgList)
        {
            float totalBetAmount = item.getFloatValue("totalBetAmount");
            float totalWinAmount = item.getFloatValue("totalWinAmount");
            float totalFeemoney = item.getFloatValue("totalFeemoney");

            item.put("platformProfit", totalBetAmount + totalFeemoney - totalWinAmount);
        }
        model.addAttribute("game_lottery_rg", rgList);
    }

    private void addGameABReport(Model model)
    {
        // ab
        JSONObject abJsonObjValue = mTotalStatsManager.getCache(OverviewType.GAME_ANDAR_BAHAR, JSONObject.class);
        List<JSONObject> abList = Lists.newArrayList();
        abList.add(abJsonObjValue.getJSONObject("all"));
        ABType[] abTypes = ABType.values();
        for (ABType tmp : abTypes)
        {
            JSONObject item = abJsonObjValue.getJSONObject(tmp.getTitle());
            abList.add(item);
        }

        for(JSONObject item : abList)
        {
            float totalBetAmount = item.getFloatValue("totalBetAmount");
            float totalWinAmount = item.getFloatValue("totalWinAmount");
            float totalFeemoney = item.getFloatValue("totalFeemoney");

            item.put("platformProfit", totalBetAmount + totalFeemoney - totalWinAmount);
        }
        model.addAttribute("game_ab", abList);
    }

    private void addGameFruitReport(Model model)
    {
        // ab
        JSONObject abJsonObjValue = mTotalStatsManager.getCache(OverviewType.GAME_FRUIT, JSONObject.class);
        List<JSONObject> fruitList = Lists.newArrayList();
        fruitList.add(abJsonObjValue.getJSONObject("all"));
        FruitType[] fruitTypes  = FruitType.values();
        for (FruitType tmp : fruitTypes)
        {
            JSONObject item = abJsonObjValue.getJSONObject(tmp.getTitle());
            fruitList.add(item);
        }

        for(JSONObject item : fruitList)
        {
            float totalBetAmount = item.getFloatValue("totalBetAmount");
            float totalWinAmount = item.getFloatValue("totalWinAmount");
            float totalFeemoney = item.getFloatValue("totalFeemoney");

            item.put("platformProfit", totalBetAmount + totalFeemoney - totalWinAmount);
        }
        model.addAttribute("game_fruit", fruitList);
    }


}

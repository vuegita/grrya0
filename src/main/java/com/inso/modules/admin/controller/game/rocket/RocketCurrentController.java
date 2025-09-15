package com.inso.modules.admin.controller.game.rocket;

import com.google.common.collect.Maps;
import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.CollectionUtils;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.admin.controller.game.lottery_v2.BaseLotteryCurrentController;
import com.inso.modules.game.GameChildType;
import com.inso.modules.game.lottery_game_impl.NewLotteryRunningStatus;
import com.inso.modules.game.model.GameInfo;
import com.inso.modules.game.model.NewLotteryPeriodInfo;
import com.inso.modules.game.rocket.helper.RocketOpenResultHelp;
import com.inso.modules.game.rocket.logical.RocketPeriodStatus;
import com.inso.modules.game.rocket.model.RocketType;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class RocketCurrentController extends BaseLotteryCurrentController {


    @Override
    public String getModuleRelateUrl() {
        return "root_game_rocket_period_current";
    }

    /**
     * 当期管理
     * @param model
     * @return
     */
    @RequiresPermissions("root_game_rocket_period_current_list")
    @RequestMapping("root_game_rocket_period_current")
    public String toCurrentRunningPeriod(Model model)
    {
        RocketType rocketType = RocketType.CRASH;
        List<GameInfo> list = mGameService.queryAllByCategory(false, rocketType.getCategory());
        model.addAttribute("gameList", list);

        addModuleParameter(model, rocketType);
        return "admin/game/rocket/game_period_current_list";
    }


    @RequiresPermissions("root_game_rocket_period_current_list")
    @RequestMapping("root_game_rocket_period_current/getDataList")
    @ResponseBody
    public String getCurrentGameTurntablePeriodRunningReportList()
    {
        String rgTypeString = WebRequest.getString("type");

        GameChildType rgType = GameChildType.getType(rgTypeString);

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        NewLotteryRunningStatus runningStatus = NewLotteryRunningStatus.tryLoadCache(rgType);
        if(runningStatus == null)
        {
            apiJsonTemplate.setData(RowPager.getEmptyRowPager());
            return apiJsonTemplate.toJSONString();
        }

        RocketPeriodStatus report = RocketPeriodStatus.tryLoadCache(true, rgType, runningStatus.getCurrentIssue());
        if(report == null)
        {
            apiJsonTemplate.setData(RowPager.getEmptyRowPager());
            return apiJsonTemplate.toJSONString();
        }

        List list = report.getBetItemReportList();
        if(CollectionUtils.isEmpty(list))
        {
            apiJsonTemplate.setData(RowPager.getEmptyRowPager());
            return apiJsonTemplate.toJSONString();
        }

        RowPager<Object> rowPage = new RowPager<>(list.size(), list);
        apiJsonTemplate.setData(rowPage);
        return apiJsonTemplate.toJSONString();
    }



    /**
     * 当前运行状态
     * @return
     */
    @RequiresPermissions("root_game_rocket_period_current_list")
    @RequestMapping("/root_game_rocket_period_current/getGameRunningStatus")
    @ResponseBody
    public String getGameRunningStatus()
    {
        GameChildType lotteryType = GameChildType.getType(WebRequest.getString("lotteryType"));
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(lotteryType == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        NewLotteryRunningStatus runningStatus = NewLotteryRunningStatus.tryLoadCache(lotteryType);
        if(runningStatus == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_BUSY);
            return apiJsonTemplate.toJSONString();
        }

        RocketPeriodStatus periodStatus = RocketPeriodStatus.tryLoadCache(true, lotteryType, runningStatus.getCurrentIssue());
        if(periodStatus == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_BUSY);
            return apiJsonTemplate.toJSONString();
        }

        long startTime = periodStatus.getStartTime().getTime();
        long endTime = periodStatus.getEndTime().getTime();

        long countdownSeconds = lotteryType.getTotalSeconds() - (System.currentTimeMillis() - startTime) / 1000;
        if(countdownSeconds < 0)
        {
            countdownSeconds = 0;
        }

        String accessKey = RocketOpenResultHelp.getInstance().createAndGetAccessKey();

        Map<String, Object> dataMpas = Maps.newHashMap();
        dataMpas.put("startTime", startTime);
        dataMpas.put("endTime", periodStatus.getEndTime().getTime());
        dataMpas.put("countdownSeconds", countdownSeconds);
        dataMpas.put("accessKey", accessKey);
        dataMpas.put("issue", periodStatus.getIssue());
        dataMpas.put("status", runningStatus.verify());
        dataMpas.put("totalBetAmount", periodStatus.getCurrentBetMoneyOfIssue());

        NewLotteryPeriodInfo periodInfo = mLotteryPeriodService.findByIssue(false, lotteryType, runningStatus.getCurrentIssue());
        if(StringUtils.isEmpty(periodInfo.getOpenResult())){
            dataMpas.put("openResult", "无");
        }else{
            dataMpas.put("openResult", periodInfo.getOpenResult());
        }

        apiJsonTemplate.setData(dataMpas);
        return apiJsonTemplate.toJSONString();
    }


    /**
     * 当前运行状态
     * @return
     */
    @RequiresPermissions("root_game_rocket_period_current_list")
    @RequestMapping("/root_game_rocket_period_current/loadAccessKey")
    @ResponseBody
    public String loadAccessKey()
    {
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
        String accessKey = RocketOpenResultHelp.getInstance().createAndGetAccessKey();
        apiJsonTemplate.setData(accessKey);
        return apiJsonTemplate.toJSONString();
    }

}

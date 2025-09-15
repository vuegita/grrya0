package com.inso.modules.admin.controller.game.lottery_v2;

import com.google.common.collect.Maps;
import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.*;
import com.inso.modules.game.GameChildType;
import com.inso.modules.game.lottery_game_impl.NewLotteryPeriodStatus;
import com.inso.modules.game.lottery_game_impl.NewLotteryRunningStatus;
import com.inso.modules.game.lottery_game_impl.btc_kline.model.BTCKlineType;
import com.inso.modules.game.lottery_game_impl.helper.NewLotteryLatestPeriod;
import com.inso.modules.game.lottery_game_impl.turntable.model.TurnTableType;
import com.inso.modules.game.model.GameInfo;
import com.inso.modules.game.model.NewLotteryPeriodInfo;
import com.inso.modules.game.service.GameService;
import com.inso.modules.game.service.NewLotteryPeriodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Map;

public abstract class BaseLotteryCurrentController {

    protected Log LOG = LogFactory.getLog(getClass());

    @Autowired
    protected NewLotteryPeriodService mLotteryPeriodService;

    @Autowired
    protected GameService mGameService;

    public abstract String getModuleRelateUrl();

    protected void addModuleParameter(Model model, GameChildType gameChildType)
    {
        model.addAttribute("moduleRelateUrl", getModuleRelateUrl());
        model.addAttribute("moduleLotteryType", gameChildType.getKey());
        model.addAttribute("moduleCategoryType", gameChildType.getCategory().getKey());
        model.addAttribute("uniqueOpenResult", gameChildType.uniqueOpenResult() + StringUtils.getEmpty());
    }

    public String toPageList(Model model, GameChildType gameChildType)
    {
        List<GameInfo> list = mGameService.queryAllByCategory(false, gameChildType.getCategory());
        model.addAttribute("gameList", list);

        addModuleParameter(model, gameChildType);

        return "admin/game/lottery_v2/game_period_current_list";
    }

//    @RequiresPermissions("root_game_turntable_period_current_list")
//    @RequestMapping("getCurrentGameTurntablePeriodRunningReportList")
//    @ResponseBody
    public String getDataList()
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

        NewLotteryPeriodStatus report = NewLotteryPeriodStatus.tryLoadCache(true, rgType, runningStatus.getCurrentIssue());
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
//    @RequiresPermissions("root_game_turntable_period_current_list")
//    @RequestMapping("/getGameTurntableStatus")
//    @ResponseBody
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

        NewLotteryPeriodStatus periodStatus = NewLotteryPeriodStatus.tryLoadCache(true, lotteryType, runningStatus.getCurrentIssue());
        if(periodStatus == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_BUSY);
            return apiJsonTemplate.toJSONString();
        }

        GameChildType gameChildType = GameChildType.getType(periodStatus.getType());

        long startTime = periodStatus.getStartTime().getTime();
        long endTime = periodStatus.getEndTime().getTime();

        long countdownSeconds = lotteryType.getTotalSeconds() - (System.currentTimeMillis() - startTime) / 1000;
        if(countdownSeconds < 0)
        {
            countdownSeconds = 0;
        }
        Map<String, Object> dataMpas = Maps.newHashMap();
        dataMpas.put("startTime", startTime);
        dataMpas.put("endTime", periodStatus.getEndTime().getTime());
        dataMpas.put("countdownSeconds", countdownSeconds);
        dataMpas.put("issue", periodStatus.getIssue());
        dataMpas.put("status", runningStatus.verify());
        dataMpas.put("totalBetAmount", periodStatus.getmTotalBetAmount());

        NewLotteryPeriodInfo periodInfo = mLotteryPeriodService.findByIssue(false, lotteryType, runningStatus.getCurrentIssue());
        if(StringUtils.isEmpty(periodInfo.getOpenResult())){
            String rs = "无" ;
            if(gameChildType == BTCKlineType.BTC_KLINE_1MIN || gameChildType == BTCKlineType.BTC_KLINE_5MIN)
            {
                int updateCount = NewLotteryLatestPeriod.getUpdateResultCount(gameChildType);

                rs += " | ref = " + StringUtils.getNotEmpty(periodInfo.getReferenceExternal());
                rs += " | count = " + updateCount;
            }
            dataMpas.put("openResult", rs);
        }else{
            dataMpas.put("openResult", periodInfo.getOpenResult());
        }

        apiJsonTemplate.setData(dataMpas);
        return apiJsonTemplate.toJSONString();
    }


//    @RequiresPermissions("root_game_turntable_period_current_edit")
//    @RequestMapping("updateGameTurntableCurrentOpenResult")
//    @ResponseBody
    public String updateGameCurrentOpenResult()
    {
        String issue = WebRequest.getString("issue");

//        TurntableBetItemType openResult =  TurntableBetItemType.getType(WebRequest.getString("openResult"));

        GameChildType moduleLotteryType = GameChildType.getType(WebRequest.getString("moduleLotteryType"));

        String openResult = WebRequest.getString("openResult");


        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(StringUtils.isEmpty(issue) || !RegexUtils.isDigit(issue) || moduleLotteryType == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if(moduleLotteryType.uniqueOpenResult())
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
            return apiJsonTemplate.toJSONString();
        }

        if(!moduleLotteryType.verifyBetItem(openResult))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        NewLotteryPeriodInfo periodInfo = mLotteryPeriodService.findByIssue(false, moduleLotteryType, issue);
        if(periodInfo == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
            return apiJsonTemplate.toJSONString();
        }

        GameChildType type = GameChildType.getType(periodInfo.getType());
        long countdownSeconds = type.getTotalSeconds() - (System.currentTimeMillis() - periodInfo.getStarttime().getTime()) / 1000;
        // 在封盘的时间内的最后5秒之前才能操作, 操作时间最多45s
        if(countdownSeconds >= 5 )//&& countdownSeconds <= 60
        {
            mLotteryPeriodService.updateOpenResult(periodInfo, openResult + StringUtils.getEmpty());
        }
        else
        {
            apiJsonTemplate.setError(-1, "请在封盘的时间内的最后5秒之前操作!");//倒计时60秒后、
            return apiJsonTemplate.toJSONString();
        }

        return apiJsonTemplate.toJSONString();
    }
}

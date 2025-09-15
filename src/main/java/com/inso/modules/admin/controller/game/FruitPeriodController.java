package com.inso.modules.admin.controller.game;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Maps;
import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.DateUtils;
import com.inso.framework.utils.RegexUtils;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.game.fruit.job.FruitOpenJob;
import com.inso.modules.game.fruit.logical.FruitPeriodStatus;
import com.inso.modules.game.fruit.logical.FruitRunningStatus;
import com.inso.modules.game.fruit.model.FruitBetItemType;
import com.inso.modules.game.fruit.model.FruitPeriodInfo;
import com.inso.modules.game.fruit.model.FruitType;
import com.inso.modules.game.fruit.service.FruitPeriodService;
import com.inso.modules.game.model.GameInfo;
import com.inso.modules.game.model.GameCategory;
import com.inso.modules.game.model.GamePeriodStatus;
import com.inso.modules.game.service.GameService;

@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class FruitPeriodController {

    @Autowired
    private FruitPeriodService  mFruitPeriodService;

    @Autowired
    private GameService mGameService;


    @RequiresPermissions("root_game_fruit_period_list")
    @RequestMapping("root_game_fruit_period")
    public String toList(Model model, HttpServletRequest request)
    {
        List<GameInfo> list = mGameService.queryAllByCategory(true, GameCategory.FRUIT);
        model.addAttribute("gameList", list);
        return "admin/game/game_fruit_period_list";
    }
    /**
     * 当期管理
     * @param model
     * @return
     */
    @RequiresPermissions("root_game_fruit_period_list")
    @RequestMapping("root_game_fruit_period_current")
    public String toCurrentRunningPeriod(Model model, HttpServletRequest request)
    {
        List<GameInfo> list = mGameService.queryAllByCategory(true, GameCategory.FRUIT);
        model.addAttribute("gameList", list);
        return "admin/game/game_fruit_period_current_list";
    }

    @RequiresPermissions("root_game_fruit_period_list")
    @RequestMapping("getGameFruitPeriodList")
    @ResponseBody
    public String getGameABPeriodList()
    {
        String time = WebRequest.getString("time");
        String issue = WebRequest.getString("issue");
        String statusSting = WebRequest.getString("status");
        String typeString = WebRequest.getString("type");

        String fromTime = WebRequest.getString("fromTime");
        String toTime = WebRequest.getString("toTime");

        ApiJsonTemplate template = new ApiJsonTemplate();

        Date startDate = DateUtils.convertDate(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS,fromTime);
        Date endDate = DateUtils.convertDate(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS, toTime);
        if(startDate.compareTo(endDate)>=0){
            template.setError(-1, "结束时间不能小于开始时间 !!!");
            return template.toJSONString();
        }

        FruitType type = FruitType.getType(typeString);
        GamePeriodStatus status = GamePeriodStatus.getType(statusSting);


        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
        pageVo.setFromTime(fromTime);
        pageVo.setToTime(toTime);

//        if(!pageVo.parseTime(time))
//        {
//            template.setData(RowPager.getEmptyRowPager());
//            return template.toJSONString();
//        }
//
//        Date toDate = DateUtils.convertDate(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS, pageVo.getToTime());
//        DateTime toTateTime = new DateTime(toDate);
//
//        DateTime nowTime = new DateTime();
//        if(toTateTime.getDayOfYear() >= nowTime.getDayOfYear())
//        {
//            // 获取20期时间
//            nowTime = nowTime.plusMinutes(type.getStepOfMinutes() * 20);
//
//            String toTimeString = nowTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
//            pageVo.setToTime(toTimeString);
//        }

        if(type == null)
        {
            template.setData(RowPager.getEmptyRowPager());
            return template.toJSONString();
        }

        if(!StringUtils.isEmpty(issue) && !issue.startsWith(type.getCode() + StringUtils.getEmpty()))
        {
            template.setData(RowPager.getEmptyRowPager());
            return template.toJSONString();
        }

        RowPager<FruitPeriodInfo> rowPager = mFruitPeriodService.queryScrollPage(pageVo, issue, type, status);

        // 不隐藏开奖结果
//        if(rowPager.getTotal() > 0)
//        {
//            String refencePrice = "-";
//            for(FruitPeriodInfo model : rowPager.getList())
//            {
//                if(model.getOpenResult() == -1)
//                {
//                    continue;
//                }
//                GamePeriodStatus tmpStatus = GamePeriodStatus.getType(model.getStatus());
//                if(tmpStatus == GamePeriodStatus.PENDING)
//                {
//                    // 为了数据安全，开奖结果不暴露(预设开奖的)
//                    model.setOpenResult(-1);
//                    model.setReferencePrice(refencePrice);
//                }
//            }
//        }

        template.setData(rowPager);
        return template.toJSONString();
    }

    @RequiresPermissions("root_game_fruit_period_edit")
    @RequestMapping("resetGameFruitOpenResult")
    @ResponseBody
    public String resetGameABOpenResult()
    {
        String presetOpenResultIssue = WebRequest.getString("presetOpenResultIssue");

        String openResultStr =  WebRequest.getString("openResult");

        FruitBetItemType openResult = FruitBetItemType.getType(openResultStr);


        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(StringUtils.isEmpty(presetOpenResultIssue) || !RegexUtils.isDigit(presetOpenResultIssue))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if(openResult == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        FruitPeriodInfo  periodInfo = mFruitPeriodService.findByIssue(false, presetOpenResultIssue);
        if(periodInfo == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
            return apiJsonTemplate.toJSONString();
        }

        FruitType type = FruitType.getType(periodInfo.getType());
        long time = periodInfo.getStarttime().getTime() - type.getStepOfMinutes() * 2 * 60 * 1000;
        if(time - System.currentTimeMillis() <= 0)
        {
            apiJsonTemplate.setError(-1, "预设开奖必须是2期之后!");
            return apiJsonTemplate.toJSONString();
        }

        mFruitPeriodService.updateOpenResult(periodInfo, openResult);

        return apiJsonTemplate.toJSONString();
    }


    @RequiresPermissions("root_game_fruit_period_edit")
    @RequestMapping("getGameFruitPeriodInfo")
    @ResponseBody
    public String getFruitPeriodInfo()
    {
        String issue = WebRequest.getString("issue");

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(StringUtils.isEmpty(issue) || !RegexUtils.isDigit(issue))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        FruitPeriodInfo periodInfo = mFruitPeriodService.findByIssue(false, issue);
        if(periodInfo == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
            return apiJsonTemplate.toJSONString();
        }

        apiJsonTemplate.setData(periodInfo);
        return apiJsonTemplate.toJSONString();
    }


    @RequiresPermissions("root_game_fruit_period_edit")
    @RequestMapping("reSettleAllGameFruitOrder")
    @ResponseBody
    public String reSettleAllGameABOrder()
    {
        String issue = WebRequest.getString("issue");

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(StringUtils.isEmpty(issue) || !RegexUtils.isDigit(issue))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        FruitPeriodInfo periodInfo = mFruitPeriodService.findByIssue(false, issue);
        if(periodInfo == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
            return apiJsonTemplate.toJSONString();
        }

        FruitType type = FruitType.getType(periodInfo.getType());

        // 是2期之前
        long time = System.currentTimeMillis() - type.getStepOfMinutes() * 2 * 60 * 1000;
        if(time - periodInfo.getStarttime().getTime() < 0)
        {
            apiJsonTemplate.setError(-1, "重新结算期号要小于当前2期之前 !!!");
            return apiJsonTemplate.toJSONString();
        }

        FruitOpenJob.sendMessage(periodInfo.getIssue());
        return apiJsonTemplate.toJSONString();
    }


    /**
     * 当前运行状态
     * @return
     */
    @RequiresPermissions("root_game_fruit_period_current_list")
    @RequestMapping("/getGameFruitStatus")
    @ResponseBody
    public String getGameRgABStatus()
    {
        String lotteryTypeStr = WebRequest.getString("lotteryType");
        FruitType lotteryType = FruitType.getType(lotteryTypeStr);

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(lotteryType == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        FruitRunningStatus runningStatus = null;
//        FruitRunningStatus runningStatus = FruitRunningStatus.tryLoadCache(lotteryType);
//        if(runningStatus == null)
//        {
//            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_BUSY);
//            return apiJsonTemplate.toJSONString();
//        }

        FruitPeriodStatus periodStatus = FruitPeriodStatus.tryLoadCache(false, lotteryType, runningStatus.getCurrentIssue());
        if(periodStatus == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_BUSY);
            return apiJsonTemplate.toJSONString();
        }

        long startTime = periodStatus.getStartTime().getTime();
        long endTime = periodStatus.getEndTime().getTime();

        long countdownSeconds = lotteryType.getStepOfMinutes() * 60 - (System.currentTimeMillis() - startTime) / 1000;
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
        dataMpas.put("totalBetAmount", periodStatus.getCurrentBetMoneyOfIssue());

        apiJsonTemplate.setData(dataMpas);

        return apiJsonTemplate.toJSONString();
    }

    @RequiresPermissions("root_game_fruit_period_list")
    @RequestMapping("getCurrentGameFruitPeriodRunningReportList")
    @ResponseBody
    public String getCurrentGameABPeriodRunningReportList()
    {
        String FruitTypeString = WebRequest.getString("type");
        FruitType lotteryType = FruitType.getType(FruitTypeString);

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        FruitRunningStatus runningStatus = FruitRunningStatus.tryLoadCache(lotteryType);
        if(runningStatus == null)
        {
            return apiJsonTemplate.toJSONString();
        }

        FruitPeriodStatus periodStatus = FruitPeriodStatus.loadCache(true, lotteryType, runningStatus.getCurrentIssue());
        List list = periodStatus.getBetItemReportList();
        RowPager<Object> rowPage = new RowPager<>(list.size(), list);
        apiJsonTemplate.setData(rowPage);
        return apiJsonTemplate.toJSONString();
    }

    /**
     * 当前运行状态
     * @return
     */
    @RequiresPermissions("root_game_fruit_period_list")
    @RequestMapping("/getGameFruitLotteryStatus")
    @ResponseBody
    public String getLotteryStatus()
    {
        String lotteryTypeStr = WebRequest.getString("lotteryType");
       // LotteryRGType lotteryType = LotteryRGType.getType(lotteryTypeStr);
        FruitType lotteryType = FruitType.getType(lotteryTypeStr);

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(lotteryType == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }


        FruitRunningStatus runningStatus = FruitRunningStatus.tryLoadCache(lotteryType);
        if(runningStatus == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_BUSY);
            return apiJsonTemplate.toJSONString();
        }

        FruitPeriodStatus periodStatus = FruitPeriodStatus.tryLoadCache(true, lotteryType, runningStatus.getCurrentIssue());
        if(periodStatus == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_BUSY);
            return apiJsonTemplate.toJSONString();
        }

        long startTime = periodStatus.getStartTime().getTime();
        long endTime = periodStatus.getEndTime().getTime();

        long countdownSeconds = lotteryType.getStepOfMinutes() * 60 - (System.currentTimeMillis() - startTime) / 1000;
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
        dataMpas.put("totalBetAmount", periodStatus.getCurrentBetMoneyOfIssue());

        FruitPeriodInfo periodInfo = mFruitPeriodService.findByIssue(false, runningStatus.getCurrentIssue());
//        LotteryPeriodInfo periodInfo = mLotteryPeriodService.findByIssue(false, runningStatus.getCurrentIssue());
        if(periodInfo.getOpenResult().equals("")){
            dataMpas.put("openResult", "无");
        }else{
            dataMpas.put("openResult", periodInfo.getOpenResult());
        }


        apiJsonTemplate.setData(dataMpas);

        return apiJsonTemplate.toJSONString();
    }


    @RequiresPermissions("root_game_fruit_period_current_edit")
    @RequestMapping("updateLotteryCurrentFruitOpenResult")
    @ResponseBody
    public String updateLotteryCurrentOpenResult()
    {
        String issue = WebRequest.getString("issue");

        String openResultStr =  WebRequest.getString("openResult");

        FruitBetItemType openResultType = FruitBetItemType.getType(openResultStr);


        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(StringUtils.isEmpty(issue) || !RegexUtils.isDigit(issue))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if(openResultType == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        FruitPeriodInfo  periodInfo = mFruitPeriodService.findByIssue(false, issue);
        if(periodInfo == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
            return apiJsonTemplate.toJSONString();
        }

        FruitType type = FruitType.getType(periodInfo.getType());
        long countdownSeconds = type.getStepOfMinutes() * 60 - (System.currentTimeMillis() - periodInfo.getStarttime().getTime()) / 1000;
        // 在封盘的时间内的最后5秒之前才能操作
        if(countdownSeconds >= 5 && countdownSeconds <=50 )//type.getDisableSecond()
        {
            mFruitPeriodService.updateOpenResult(periodInfo, openResultType);
        }
        else
        {
            apiJsonTemplate.setError(-1, "请在封盘的时间内的最后5秒之前操作!");
            return apiJsonTemplate.toJSONString();
        }

        return apiJsonTemplate.toJSONString();
    }
}

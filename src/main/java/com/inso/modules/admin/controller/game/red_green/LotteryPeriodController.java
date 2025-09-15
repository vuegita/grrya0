package com.inso.modules.admin.controller.game.red_green;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.utils.*;
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
import com.inso.framework.service.Callback;
import com.inso.framework.spring.web.WebRequest;
import com.inso.modules.game.model.GameInfo;
import com.inso.modules.game.model.GameCategory;
import com.inso.modules.game.model.GamePeriodStatus;
import com.inso.modules.game.rg.job.LotteryRgJob;
import com.inso.modules.game.rg.logical.RGPeriodStatus;
import com.inso.modules.game.rg.logical.RGRunningStatus;
import com.inso.modules.game.rg.model.LotteryPeriodInfo;
import com.inso.modules.game.rg.model.LotteryRGType;
import com.inso.modules.game.rg.service.LotteryPeriodService;
import com.inso.modules.game.service.GameService;

@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class LotteryPeriodController {

    private static Log LOG = LogFactory.getLog(LotteryPeriodController.class);

    @Autowired
    private LotteryPeriodService mLotteryPeriodService;

    @Autowired
    private GameService mGameService;


    @RequiresPermissions("root_game_lottery_rg_period_list")
    @RequestMapping("root_game_lottery_rg_period")
    public String toList(Model model, HttpServletRequest request)
    {
        List<GameInfo> list = mGameService.queryAllByCategory(true, GameCategory.LOTTERY_RG);
        model.addAttribute("gameList", list);
        return "admin/game/game_lottery_rg_period_list";
    }

    /**
     * 当期管理
     * @param model
     * @return
     */
    @RequiresPermissions("root_game_lottery_rg_period_current_list")
    @RequestMapping("root_game_lottery_rg_period_current")
    public String toCurrentRunningPeriod(Model model)
    {
        List<GameInfo> list = mGameService.queryAllByCategory(true, GameCategory.LOTTERY_RG);
        model.addAttribute("gameList", list);
        return "admin/game/game_lottery_rg_period_current_list";
    }

    @RequiresPermissions("root_game_lottery_rg_period_list")
    @RequestMapping("getLotteryPeriodList")
    @ResponseBody
    public String getLotteryPeriodList()
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

        LotteryRGType type = LotteryRGType.getType(typeString);
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
//            nowTime = nowTime.plusMinutes(type.getStepOfMinutes() * 100);
//
//            String toTimeString = nowTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
//            pageVo.setToTime(toTimeString);
//        }

        if(type == null && StringUtils.isEmpty(issue))
        {
            template.setData(RowPager.getEmptyRowPager());
            return template.toJSONString();
        }

//        if(!StringUtils.isEmpty(issue) && !issue.startsWith(type.getCode() + StringUtils.getEmpty()))
//        {
//            template.setData(RowPager.getEmptyRowPager());
//            return template.toJSONString();
//        }

        RowPager<LotteryPeriodInfo> rowPager = mLotteryPeriodService.queryScrollPage(pageVo, issue, type, status);

        // 不隐藏开奖结果
//        if(rowPager.getTotal() > 0)
//        {
//            String refencePrice = "-";
//            for(LotteryPeriodInfo model : rowPager.getList())
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

    @RequiresPermissions("root_game_lottery_rg_period_edit")
    @RequestMapping("batchpresetOpenResul")
    @ResponseBody
    public String batchpresetOpenResul()
    {
        String time = WebRequest.getString("issuetime");
        String typeString = WebRequest.getString("gameType");

        String startTime = WebRequest.getString("startTime");
        String endTime = WebRequest.getString("endTime");

        LotteryRGType type = LotteryRGType.getType(typeString);

        ApiJsonTemplate template = new ApiJsonTemplate();

//        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
//        if(!pageVo.parseTimeBySplit(time))
//        {
//            template.setData(RowPager.getEmptyRowPager());
//            return template.toJSONString();
//        }
//        Date fromDate = DateUtils.convertDate(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS, pageVo.getFromTime());
//        Date toDate = DateUtils.convertDate(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS, pageVo.getToTime());

        Date startDate = DateUtils.convertDate(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS,startTime);
        Date endDate = DateUtils.convertDate(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS, endTime);

        Date nowTime = new Date();

        if(startDate.compareTo(endDate)>=0){
            template.setError(-1, "结束时间不能小于开始时间 !!!");
            return template.toJSONString();
        }

        if(nowTime.compareTo(startDate)>=0){
            template.setError(-1, "开始时间不能小于当前时间 !!!");
            return template.toJSONString();
        }

        if(nowTime.compareTo(endDate)>=0){
            template.setError(-1, "结束时间不能小于当前时间 !!!");
            return template.toJSONString();
        }
//        String startTime=DateUtils.convertString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS,fromDate );
//        String endTime= DateUtils.convertString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS,toDate );;

        mLotteryPeriodService.queryAll(type, startTime, endTime, new Callback<LotteryPeriodInfo>() {
            @Override
            public void execute(LotteryPeriodInfo o) {
                int openResult = RandomUtils.nextInt(100);
                int result=openResult%10;
                mLotteryPeriodService.updateOpenResult(o, result);
            }
        });

        return template.toJSONString();
    }

    @RequiresPermissions("root_game_lottery_rg_period_edit")
    @RequestMapping("resetLotteryOpenResult")
    @ResponseBody
    public String resetLotteryOpenResult()
    {
        String presetOpenResultIssue = WebRequest.getString("presetOpenResultIssue");

        long openResult =  WebRequest.getLong("openResult");


        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(StringUtils.isEmpty(presetOpenResultIssue) || !RegexUtils.isDigit(presetOpenResultIssue))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if(!(openResult >= 0 && openResult <= 9))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        LotteryPeriodInfo periodInfo = mLotteryPeriodService.findByIssue(false, presetOpenResultIssue);
        if(periodInfo == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
            return apiJsonTemplate.toJSONString();
        }

        //LotteryRGType type = LotteryRGType.getType(periodInfo.getType());
        long time = periodInfo.getStarttime().getTime() - 5 * 1000;  //type.getStepOfMinutes() * 2 * 60 * 1000
        if(time - System.currentTimeMillis() <= 0)
        {
            apiJsonTemplate.setError(-1, "预设开奖必须是5秒之后!");//预设开奖必须是2期之后!
            return apiJsonTemplate.toJSONString();
        }

        mLotteryPeriodService.updateOpenResult(periodInfo, openResult);

        return apiJsonTemplate.toJSONString();
    }


    @RequiresPermissions("root_game_lottery_rg_period_edit")
    @RequestMapping("getLotteryPeriodInfo")
    @ResponseBody
    public String getLotteryPeriodInfo()
    {
        String issue = WebRequest.getString("issue");

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(StringUtils.isEmpty(issue) || !RegexUtils.isDigit(issue))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        LotteryPeriodInfo periodInfo = mLotteryPeriodService.findByIssue(false, issue);
        if(periodInfo == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
            return apiJsonTemplate.toJSONString();
        }

        apiJsonTemplate.setData(periodInfo);
        return apiJsonTemplate.toJSONString();
    }


    @RequiresPermissions("root_game_lottery_rg_period_edit")
    @RequestMapping("reSettleAllLotteryOrder")
    @ResponseBody
    public String reSettleAllLotteryOrder()
    {
        String issue = WebRequest.getString("issue");

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(StringUtils.isEmpty(issue) || !RegexUtils.isDigit(issue))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        LotteryPeriodInfo periodInfo = mLotteryPeriodService.findByIssue(false, issue);
        if(periodInfo == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
            return apiJsonTemplate.toJSONString();
        }

        LotteryRGType type = LotteryRGType.getType(periodInfo.getType());

        // 是2期之前
        long time = System.currentTimeMillis() - type.getStepOfMinutes() * 2 * 60 * 1000;
        if(time - periodInfo.getStarttime().getTime() < 0)
        {
            apiJsonTemplate.setError(-1, "重新结算期号要小于当前2期之前 !!!");
            return apiJsonTemplate.toJSONString();
        }

        LotteryRgJob.sendMessage(periodInfo.getIssue());
        return apiJsonTemplate.toJSONString();
    }


    @RequiresPermissions("root_game_lottery_rg_period_current_list")
    @RequestMapping("getCurrentGameRgPeriodRunningReportList")
    @ResponseBody
    public String getCurrentGameRgPeriodRunningReportList()
    {
        String rgTypeString = WebRequest.getString("type");

        LotteryRGType rgType = LotteryRGType.getType(rgTypeString);

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        RGRunningStatus runningStatus = RGRunningStatus.tryLoadCache(rgType);
        if(runningStatus == null)
        {
            return apiJsonTemplate.toJSONString();
        }

        RGPeriodStatus report = RGPeriodStatus.tryLoadCache(true, rgType, runningStatus.getCurrentIssue());
        List list = report.getBetItemReportList();
        RowPager<Object> rowPage = new RowPager<>(list.size(), list);
        apiJsonTemplate.setData(rowPage);

        return apiJsonTemplate.toJSONString();
    }

    /**
     * 当前运行状态
     * @return
     */
    @RequiresPermissions("root_game_lottery_rg_period_current_list")
    @RequestMapping("/getGameRgLotteryStatus")
    @ResponseBody
    public String getLotteryStatus()
    {
        String lotteryTypeStr = WebRequest.getString("lotteryType");
        LotteryRGType lotteryType = LotteryRGType.getType(lotteryTypeStr);

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(lotteryType == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        RGRunningStatus runningStatus = RGRunningStatus.tryLoadCache(lotteryType);
        if(runningStatus == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_BUSY);
            return apiJsonTemplate.toJSONString();
        }

        RGPeriodStatus periodStatus = RGPeriodStatus.tryLoadCache(true, lotteryType, runningStatus.getCurrentIssue());
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

        LotteryPeriodInfo periodInfo = mLotteryPeriodService.findByIssue(false, runningStatus.getCurrentIssue());
        if(periodInfo.getOpenResult()==-1){
            dataMpas.put("openResult", "无");
        }else{
            dataMpas.put("openResult", periodInfo.getOpenResult());
        }


        apiJsonTemplate.setData(dataMpas);

        return apiJsonTemplate.toJSONString();
    }


    @RequiresPermissions("root_game_lottery_rg_period_current_edit")
    @RequestMapping("updateLotteryCurrentOpenResult")
    @ResponseBody
    public String updateLotteryCurrentOpenResult()
    {
        String issue = WebRequest.getString("issue");

        long openResult =  WebRequest.getLong("openResult");


        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(StringUtils.isEmpty(issue) || !RegexUtils.isDigit(issue))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if(!(openResult >= 0 && openResult <= 9))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        LotteryPeriodInfo periodInfo = mLotteryPeriodService.findByIssue(false, issue);
        if(periodInfo == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
            return apiJsonTemplate.toJSONString();
        }

        LotteryRGType type = LotteryRGType.getType(periodInfo.getType());
        long countdownSeconds = type.getStepOfMinutes() * 60 - (System.currentTimeMillis() - periodInfo.getStarttime().getTime()) / 1000;
        // 在封盘的时间内的最后5秒之前才能操作, 操作时间最多45s
        if(countdownSeconds >= 5 )//&& countdownSeconds <= 60
        {
            mLotteryPeriodService.updateOpenResult(periodInfo, openResult);
        }
        else
        {
            apiJsonTemplate.setError(-1, "请在封盘的时间内的最后5秒之前操作!");//倒计时60秒后、
            return apiJsonTemplate.toJSONString();
        }


        return apiJsonTemplate.toJSONString();
    }
}

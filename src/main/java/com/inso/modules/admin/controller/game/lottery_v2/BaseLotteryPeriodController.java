package com.inso.modules.admin.controller.game.lottery_v2;

import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.service.Callback;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.DateUtils;
import com.inso.framework.utils.RegexUtils;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.game.GameChildType;
import com.inso.modules.game.lottery_game_impl.job.MyLotteryBeginJob;
import com.inso.modules.game.model.GameInfo;
import com.inso.modules.game.model.GamePeriodStatus;
import com.inso.modules.game.model.NewLotteryPeriodInfo;
import com.inso.modules.game.service.GameService;
import com.inso.modules.game.service.NewLotteryPeriodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

public abstract class BaseLotteryPeriodController {

    protected Log LOG = LogFactory.getLog(getClass());

    @Autowired
    protected NewLotteryPeriodService mLotteryPeriodService;

    @Autowired
    protected GameService mGameService;

    public abstract String getModuleRelateUrl();

    private void addModuleParameter(Model model, GameChildType gameChildType)
    {
        model.addAttribute("moduleRelateUrl", getModuleRelateUrl());
        model.addAttribute("moduleLotteryType", gameChildType.getKey());
        model.addAttribute("moduleCategoryType", gameChildType.getCategory().getKey());
        model.addAttribute("uniqueOpenResult", gameChildType.uniqueOpenResult() + StringUtils.getEmpty());
    }

    public String toPageList(Model model, HttpServletRequest request, GameChildType gameChildType)
    {

        List<GameInfo> list = mGameService.queryAllByCategory(false, gameChildType.getCategory());
        model.addAttribute("gameList", list);

        addModuleParameter(model, gameChildType);
        return "admin/game/lottery_v2/game_period_list";
    }

    public String getDataList()
    {
        String time = WebRequest.getString("time");
        String issue = WebRequest.getString("issue");
        String statusSting = WebRequest.getString("status");
        String typeString = WebRequest.getString("type");

        String fromTime = WebRequest.getString("fromTime");
        String toTime = WebRequest.getString("toTime");

//        GameChildType moduleLotteryType = GameChildType.getType(WebRequest.getString("moduleLotteryType"));
        GameChildType type = GameChildType.getType(WebRequest.getString("type"));

        ApiJsonTemplate template = new ApiJsonTemplate();

        if(type == null)
        {
            template.setData(RowPager.getEmptyRowPager());
            return template.toJSONString();
        }

        Date startDate = DateUtils.convertDate(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS,fromTime);
        Date endDate = DateUtils.convertDate(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS, toTime);
        if(startDate.compareTo(endDate)>=0){
            template.setError(-1, "结束时间不能小于开始时间 !!!");
            return template.toJSONString();
        }


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

        RowPager<NewLotteryPeriodInfo> rowPager = mLotteryPeriodService.queryScrollPage(pageVo, issue, type, status);

        // 不隐藏开奖结果
//        if(rowPager.getTotal() > 0)
//        {
//            String refencePrice = "-";
//            for(TurntablePeriodInfo model : rowPager.getList())
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

//    @RequiresPermissions("root_game_turntable_period_edit")
//    @RequestMapping("batchPresetTurntableOpenResult")
//    @ResponseBody
    public String batchPresetOpenResult()
    {
        String time = WebRequest.getString("issuetime");
        String typeString = WebRequest.getString("gameType");

        String startTime = WebRequest.getString("startTime");
        String endTime = WebRequest.getString("endTime");

        GameChildType type = GameChildType.getType(typeString);

        GameChildType moduleLotteryType = GameChildType.getType(WebRequest.getString("moduleLotteryType"));

        ApiJsonTemplate template = new ApiJsonTemplate();

        if(moduleLotteryType.uniqueOpenResult())
        {
            template.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
            return template.toJSONString();
        }

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
//        BaseLotterySupport processor = MyLotteryManager.getInstance().getOpenProcessor(type);
//        GameChildType processor.getAllGameTypes();
        mLotteryPeriodService.queryAll2(type, startTime, endTime, type, new Callback<NewLotteryPeriodInfo>() {
            @Override
            public void execute(NewLotteryPeriodInfo o) {
                String result = type.randomBetItem();
                mLotteryPeriodService.updateOpenResult(o, result);
            }
        });

        return template.toJSONString();
    }

//    @RequiresPermissions("root_game_turntable_period_edit")
//    @RequestMapping("resetGameTurntableOpenResult")
//    @ResponseBody
    public String resetGameOpenResult()
    {
        String presetOpenResultIssue = WebRequest.getString("presetOpenResultIssue");

        String openResult = WebRequest.getString("openResult");

        GameChildType moduleLotteryType = GameChildType.getType(WebRequest.getString("moduleLotteryType"));

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(StringUtils.isEmpty(presetOpenResultIssue) || !RegexUtils.isDigit(presetOpenResultIssue) || moduleLotteryType == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if(!moduleLotteryType.verifyBetItem(openResult))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        NewLotteryPeriodInfo periodInfo = mLotteryPeriodService.findByIssue(false, moduleLotteryType, presetOpenResultIssue);
        if(periodInfo == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
            return apiJsonTemplate.toJSONString();
        }

        //TurnTableType type = TurnTableType.getType(periodInfo.getType());
        long time = periodInfo.getStarttime().getTime() - 5 * 1000;  //type.getStepOfMinutes() * 2 * 60 * 1000
        if(time - System.currentTimeMillis() <= 0)
        {
            apiJsonTemplate.setError(-1, "预设开奖必须是5秒之后!");//预设开奖必须是2期之后!
            return apiJsonTemplate.toJSONString();
        }

        mLotteryPeriodService.updateOpenResult(periodInfo, openResult + StringUtils.getEmpty());

        return apiJsonTemplate.toJSONString();
    }


//    @RequiresPermissions("root_game_turntable_period_edit")
//    @RequestMapping("getGameTurntablePeriodInfo")
//    @ResponseBody
    public String getGamePeriodInfo()
    {
        String issue = WebRequest.getString("issue");

        GameChildType moduleLotteryType = GameChildType.getType(WebRequest.getString("moduleLotteryType"));

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(StringUtils.isEmpty(issue) || !RegexUtils.isDigit(issue) || moduleLotteryType == null)
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

        apiJsonTemplate.setData(periodInfo);
        return apiJsonTemplate.toJSONString();
    }


//    @RequiresPermissions("root_game_turntable_period_edit")
//    @RequestMapping("reSettleAllGameTurntableOrder")
//    @ResponseBody
    public String reSettleAllGameOrder()
    {
        String issue = WebRequest.getString("issue");

        GameChildType moduleLotteryType = GameChildType.getType(WebRequest.getString("moduleLotteryType"));

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(StringUtils.isEmpty(issue) || !RegexUtils.isDigit(issue) || moduleLotteryType == null)
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

        // 是2期之前
        long time = System.currentTimeMillis() - type.getDisableMilliSeconds() * 2;
        if(time - periodInfo.getStarttime().getTime() < 0)
        {
            apiJsonTemplate.setError(-1, "重新结算期号要小于当前2期之前 !!!");
            return apiJsonTemplate.toJSONString();
        }

        MyLotteryBeginJob.sendEndTaskMessage(type, periodInfo.getIssue());
        return apiJsonTemplate.toJSONString();
    }




}

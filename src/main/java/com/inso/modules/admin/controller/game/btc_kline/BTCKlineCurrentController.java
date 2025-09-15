package com.inso.modules.admin.controller.game.btc_kline;

import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RegexUtils;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.admin.controller.game.lottery_v2.BaseLotteryCurrentController;
import com.inso.modules.admin.helper.AdminAccountHelper;
import com.inso.modules.game.GameChildType;
import com.inso.modules.game.lottery_game_impl.BaseLotterySupport;
import com.inso.modules.game.lottery_game_impl.MyLotteryManager;
import com.inso.modules.game.lottery_game_impl.NewLotteryBetTaskManager;
import com.inso.modules.game.lottery_game_impl.btc_kline.model.BTCKlineBetItemType;
import com.inso.modules.game.lottery_game_impl.btc_kline.model.BTCKlineType;
import com.inso.modules.game.lottery_game_impl.helper.NewLotteryLatestPeriod;
import com.inso.modules.game.lottery_game_impl.turntable.model.TurnTableType;
import com.inso.modules.game.model.NewLotteryPeriodInfo;
import com.inso.modules.game.service.NewLotteryPeriodService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.joda.time.DateTime;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class BTCKlineCurrentController extends BaseLotteryCurrentController {

    private static Log LOG = LogFactory.getLog(BTCKlineCurrentController.class);


    @Override
    public String getModuleRelateUrl() {
        return "root_game_btc_kline_period_current";
    }

    /**
     * 当期管理
     * @param model
     * @return
     */
    @RequiresPermissions("root_game_btc_kline_period_current_list")
    @RequestMapping("root_game_btc_kline_period_current")
    public String toCurrentRunningPeriod(Model model)
    {
        return super.toPageList(model, BTCKlineType.BTC_KLINE_1MIN);
    }


    @RequiresPermissions("root_game_btc_kline_period_current_list")
    @RequestMapping("root_game_btc_kline_period_current/getDataList")
    @ResponseBody
    public String getCurrentGameTurntablePeriodRunningReportList()
    {
        return super.getDataList();
    }



    /**
     * 当前运行状态
     * @return
     */
    @RequiresPermissions("root_game_btc_kline_period_current_list")
    @RequestMapping("/root_game_btc_kline_period_current/getGameRunningStatus")
    @ResponseBody
    public String getGameRunningStatus()
    {
        return super.getGameRunningStatus();
    }

    @RequiresPermissions("root_game_btc_kline_period_current_edit")
    @RequestMapping("root_game_btc_kline_period_current/updateGameCurrentOpenResult")
    @ResponseBody
    public String updateCurrentOpenResult()
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

//        BTCKlineBetItemType betItemType = BTCKlineBetItemType.getType(openResult);
//        if(!(betItemType == BTCKlineBetItemType.Even || betItemType == BTCKlineBetItemType.Odd))
//        {
//            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
//            return apiJsonTemplate.toJSONString();
//        }

        NewLotteryPeriodInfo periodInfo = mLotteryPeriodService.findByIssue(false, moduleLotteryType, issue);
        if(periodInfo == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
            return apiJsonTemplate.toJSONString();
        }

        GameChildType type = GameChildType.getType(periodInfo.getType());

        GameChildType gameChildType = GameChildType.getType(periodInfo.getType());
        if(gameChildType == BTCKlineType.BTC_KLINE_1MIN)
        {
            BTCKlineBetItemType betItemType = BTCKlineBetItemType.getType(openResult);
            if(!(betItemType == BTCKlineBetItemType.Even || betItemType == BTCKlineBetItemType.Odd))
            {
//                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
//                return apiJsonTemplate.toJSONString();
            }
        }
        else
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
            return apiJsonTemplate.toJSONString();
        }

        int updateCount = NewLotteryLatestPeriod.getUpdateResultCount(gameChildType);
        if(!AdminAccountHelper.isNy4timeAdminOrDEV() && updateCount >= NewLotteryLatestPeriod.MAX_UPDATE_RESULT_COUNT)
        {
            apiJsonTemplate.setError(-1, "每天最多更新" + NewLotteryLatestPeriod.MAX_UPDATE_RESULT_COUNT + "次!");//预设开奖必须是2期之后!
            return apiJsonTemplate.toJSONString();
        }

        long countdownSeconds = type.getTotalSeconds() - (System.currentTimeMillis() - periodInfo.getStarttime().getTime()) / 1000;
        // 在封盘的时间内的最后5秒之前才能操作, 操作时间最多45s
        if(countdownSeconds >= 5 )//&& countdownSeconds <= 60
        {
            mLotteryPeriodService.updateReference(periodInfo, openResult + StringUtils.getEmpty(), null);
            update5MinResult(mLotteryPeriodService, periodInfo, openResult);
            String logResult = "issue = " + periodInfo.getIssue() + ", result = " + openResult;
            NewLotteryLatestPeriod.updateResultCount(gameChildType, updateCount + 1, logResult);
        }
        else
        {
            apiJsonTemplate.setError(-1, "请在封盘的时间内的最后5秒之前操作!");//倒计时60秒后、
            return apiJsonTemplate.toJSONString();
        }

        return apiJsonTemplate.toJSONString();
    }


    public static void update5MinResult(NewLotteryPeriodService periodService, NewLotteryPeriodInfo periodInfo, String openResult)
    {
        try {
            DateTime dateTime = new DateTime(periodInfo.getStarttime());
            dateTime = dateTime.minusMinutes(4);

            //
            int min = dateTime.getMinuteOfHour();
            int rs = min;
            if(min >= 10)
            {
                String str = min + StringUtils.getEmpty();
                int len = str.length();
                rs = StringUtils.asInt(str.substring(len - 1, len));
            }

            if(!(rs == 0 || rs == 5))
            {
                return;
            }

            GameChildType childType = BTCKlineType.BTC_KLINE_5MIN;
            BaseLotterySupport processor = MyLotteryManager.getInstance().getOpenProcessor(childType);
            String issue = processor.createIssue(childType, dateTime, true);
            NewLotteryPeriodInfo periodInfo2 = periodService.findByIssue(false, childType, issue);
            if(periodInfo2 == null)
            {
                return;
            }

            periodService.updateReference(periodInfo2, openResult + StringUtils.getEmpty(), null);
        } catch (Exception e) {
            LOG.error("handle error:", e);
        }
    }

    public static void main(String[] args) {

        int min = 14;
        int rs = min;
        if(min >= 10)
        {
            String str = min + StringUtils.getEmpty();
            int len = str.length();
            rs = StringUtils.asInt(str.substring(len - 1, len));
        }
        System.out.println(rs);

    }

}

package com.inso.modules.admin.controller.game.btc_kline;

import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RegexUtils;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.admin.controller.game.lottery_v2.BaseLotteryPeriodController;
import com.inso.modules.admin.helper.AdminAccountHelper;
import com.inso.modules.game.GameChildType;
import com.inso.modules.game.lottery_game_impl.btc_kline.model.BTCKlineBetItemType;
import com.inso.modules.game.lottery_game_impl.btc_kline.model.BTCKlineType;
import com.inso.modules.game.lottery_game_impl.helper.NewLotteryLatestPeriod;
import com.inso.modules.game.lottery_game_impl.turntable.model.TurnTableType;
import com.inso.modules.game.model.NewLotteryPeriodInfo;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class BTCKlinePeriodController extends BaseLotteryPeriodController {


    @RequiresPermissions("root_game_btc_kline_period_list")
    @RequestMapping("root_game_btc_kline_period")
    public String toList(Model model, HttpServletRequest request)
    {
        return super.toPageList(model, request, BTCKlineType.BTC_KLINE_1MIN);
    }


    @RequiresPermissions("root_game_btc_kline_period_list")
    @RequestMapping("root_game_btc_kline_period/getDataList")
    @ResponseBody
    public String getList()
    {
        return super.getDataList();
    }

    @RequiresPermissions("root_game_btc_kline_period_edit")
    @RequestMapping("root_game_btc_kline_period/batchPresetOpenResult")
    @ResponseBody
    public String batchPresetOpenResult()
    {
        return super.batchPresetOpenResult();
    }

    @RequiresPermissions("root_game_btc_kline_period_edit")
    @RequestMapping("root_game_btc_kline_period/resetGameOpenResult")
    @ResponseBody
    public String resetGameOpenResult()
    {
        String presetOpenResultIssue = WebRequest.getString("presetOpenResultIssue");
        String openResult = WebRequest.getString("openResult");
        GameChildType moduleLotteryType = GameChildType.getType(WebRequest.getString("moduleLotteryType"));

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(StringUtils.isEmpty(openResult))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
            return apiJsonTemplate.toJSONString();
        }

        NewLotteryPeriodInfo periodInfo = mLotteryPeriodService.findByIssue(false, moduleLotteryType, presetOpenResultIssue);
        if(periodInfo == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
            return apiJsonTemplate.toJSONString();
        }

        GameChildType gameChildType = GameChildType.getType(periodInfo.getType());
        if(gameChildType == BTCKlineType.BTC_KLINE_1MIN)
        {
            BTCKlineBetItemType betItemType = BTCKlineBetItemType.getType(openResult);
            if(!(betItemType == BTCKlineBetItemType.Even || betItemType == BTCKlineBetItemType.Odd))
            {
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
                return apiJsonTemplate.toJSONString();
            }
        }
        else
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
            return apiJsonTemplate.toJSONString();
        }

        long time = periodInfo.getStarttime().getTime() - 5 * 1000;  //type.getStepOfMinutes() * 2 * 60 * 1000
        if(time - System.currentTimeMillis() <= 0)
        {
            apiJsonTemplate.setError(-1, "预设开奖必须是5秒之后!");//预设开奖必须是2期之后!
            return apiJsonTemplate.toJSONString();
        }

        int updateCount = NewLotteryLatestPeriod.getUpdateResultCount(gameChildType);
        if(!AdminAccountHelper.isNy4timeAdminOrDEV() && updateCount >= NewLotteryLatestPeriod.MAX_UPDATE_RESULT_COUNT)
        {
            apiJsonTemplate.setError(-1, "每天最多更新" + NewLotteryLatestPeriod.MAX_UPDATE_RESULT_COUNT + "次!");//预设开奖必须是2期之后!
            return apiJsonTemplate.toJSONString();
        }

        mLotteryPeriodService.updateReference(periodInfo, openResult + StringUtils.getEmpty(), null);
        BTCKlineCurrentController.update5MinResult(mLotteryPeriodService, periodInfo, openResult);

        // 更新
        String logResult = "issue = " + periodInfo.getIssue() + ", result = " + openResult;
        NewLotteryLatestPeriod.updateResultCount(gameChildType, updateCount + 1, logResult);
        return apiJsonTemplate.toJSONString();
    }


    @RequiresPermissions("root_game_btc_kline_period_edit")
    @RequestMapping("root_game_btc_kline_period/getGamePeriodInfo")
    @ResponseBody
    public String getGameTurntablePeriodInfo()
    {
        return super.getGamePeriodInfo();
    }


    @RequiresPermissions("root_game_btc_kline_period_edit")
    @RequestMapping("root_game_btc_kline_period/reSettleAllGameOrder")
    @ResponseBody
    public String reSettleAllGameOrder()
    {
        return super.reSettleAllGameOrder();
    }

    @Override
    public String getModuleRelateUrl() {
        return "root_game_btc_kline_period";
    }

}

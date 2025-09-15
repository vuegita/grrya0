package com.inso.modules.admin.controller.game;

import java.util.List;

import com.inso.framework.bean.PageVo;
import com.inso.modules.game.model.GameCategory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;
import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.model.Status;
import com.inso.modules.game.model.GameInfo;
import com.inso.modules.game.rg.service.LotteryPeriodService;
import com.inso.modules.game.service.GameService;

@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class GameListController {

    @Autowired
    private GameService mGameService;

    @Autowired
    private LotteryPeriodService mLotteryPeriodService;

    @RequiresPermissions("root_game_page_list")
    @RequestMapping("root_game_page")
    public String toList(Model model)
    {
        return "admin/game/game_page_list";
    }

    @RequiresPermissions("root_game_page_list")
    @RequestMapping("getGameList")
    @ResponseBody
    public String getGameList()
    {
        String category = WebRequest.getString("category");

        GameCategory gameCategory = GameCategory.getType(category);

        ApiJsonTemplate template = new ApiJsonTemplate();
        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
        RowPager<GameInfo> rowPager = mGameService.queryScrollPage(pageVo, gameCategory);

        template.setData(rowPager);
        return template.toJSONString();
    }

    @RequiresPermissions("root_game_page_edit")
    @RequestMapping("root_game_page/edit/page")
    public String edit(Model model)
    {
        long id = WebRequest.getLong("id");
        GameInfo gameInfo = mGameService.findById(false, id);
        model.addAttribute("entity", gameInfo);
        return "admin/game/game_page_edit";
    }

    @RequiresPermissions("root_game_page_edit")
    @RequestMapping("updateGameStatus")
    @ResponseBody
    public String updateGameStatus()
    {
        long gameid = WebRequest.getLong("id");

        long sort = WebRequest.getLong("sort");

        String statusString =  WebRequest.getString("status");
        Status status = Status.getType(statusString);

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(gameid <= 0)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if(status == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        GameInfo game = mGameService.findById(false, gameid);
        if(game == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }
        mGameService.updateStatus(game, status, sort);

        // 更新状态
//        LotteryRGType lotteryType = LotteryRGType.getType(game.getKey());
//        LotteryPeriodInfo periodInfo = mLotteryPeriodService.findCurrentRunning(lotteryType);
//        if(periodInfo != null)
//        {
//            // 有两个地方更新，这里和job，存在并发的可能，如果出错则可以多次执行
//            RGRunningStatus runningStatus = RGRunningStatus.loadCache(lotteryType);
//            runningStatus.setStatus(status);
//            runningStatus.setCurrentIssue(periodInfo.getIssue());
//        }
//        else
//        {
//            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FAILURE);
//        }
        return apiJsonTemplate.toJSONString();
    }
}

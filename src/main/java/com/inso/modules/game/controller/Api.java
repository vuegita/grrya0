package com.inso.modules.game.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.spring.web.WebRequest;
import com.inso.modules.game.GlobalBetRecordManager;
import com.inso.modules.websocket.model.MyEventType;
import com.inso.modules.websocket.model.MyGroupType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Maps;
import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.modules.game.model.GameInfo;
import com.inso.modules.game.model.GameCategory;
import com.inso.modules.game.service.GameService;

@RestController
@RequestMapping("/game/api")
public class Api {

    @Autowired
    private GameService mGameService;

    private List mPGList = null;
    private long mPGLastRefreshTime = -1;

    /**
     * @api {post} /game/api/getList
     * @apiDescription  获取游戏列表
     * @apiName getList
     * @apiGroup Game-Api
     * @apiVersion 1.0.0
     *
     * @apiSuccess  {String}  code    错误码
     * @apiSuccess  {String}  error   错误信息
     *
     * @apiSuccessExample {json} Success-Response:
     *       {
     *         "code": 200,
     *         "error": "success",
     *       }
     */
    @RequestMapping("/getList")
    public String getList()
    {
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
        Map<String, Object> maps = Maps.newHashMap();
        List<GameInfo> list = mGameService.queryAllByCategory(false, GameCategory.LOTTERY_RG);
        maps.put(GameCategory.LOTTERY_RG.getKey(), list);
        apiJsonTemplate.setData(maps);
        return apiJsonTemplate.toJSONString();
    }

    @RequestMapping("/getGameList")
    public String getGameList()
    {
        int offset = WebRequest.getInt("offset", 0, 150);
        GameCategory gameCategory = GameCategory.getType(WebRequest.getString("gameCategory"));
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
        if(gameCategory == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }


        long ts = System.currentTimeMillis();
        if(this.mPGLastRefreshTime == -1 || ts - this.mPGLastRefreshTime > 60_000)
        {
            this.mPGList = mGameService.queryAllByCategory(false, gameCategory);
            this.mPGLastRefreshTime = ts;
        }
        List<GameInfo> list = this.mPGList;

        int rsIndex = 0;
        List rsList = new ArrayList();
        int size = list.size();
        for(int i = offset; i < size; i ++)
        {
            if(rsIndex >= 9)
            {
                break;
            }
            rsList.add(list.get(i));
            rsIndex ++;
        }

        apiJsonTemplate.setData(rsList);
        return apiJsonTemplate.toJSONString();
    }

    @RequestMapping("/getAllList")
    public String getAllList()
    {
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
        Map<String, Object> maps = Maps.newHashMap();
        List<GameInfo> list = mGameService.queryAllByCategory(false, GameCategory.LOTTERY_RG);

        List<GameInfo> list2 = mGameService.queryAllByCategory(false, GameCategory.ANDAR_BAHAR);

        List<GameInfo> list3 = mGameService.queryAllByCategory(false, GameCategory.FRUIT);

        list.addAll(list2);
        list.addAll(list3);

        maps.put("gameList", list);

        apiJsonTemplate.setData(maps);
        return apiJsonTemplate.toJSONString();
    }


    /**
     * @api {post} /game/api/getAllBetRecord
     * @apiDescription  获取所有游戏的最新投注记录
     * @apiName getList
     * @apiGroup Game-Api
     * @apiVersion 1.0.0
     *
     * @apiSuccess  {String}  code    错误码
     * @apiSuccess  {String}  error   错误信息
     *
     * @apiSuccessExample {json} Success-Response:
     *       {
     *         "code": 200,
     *         "error": "success",
     *         data:返回结果和其它游戏的下注一样
     *       }
     */
    @RequestMapping("/getAllBetRecord")
    @ResponseBody
    public String getAllBetRecord()
    {
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
        apiJsonTemplate.setEvent(MyGroupType.HALL.getKey(), MyEventType.HALL_GAME_GET_ALL_BET_RECORD.getKey());
        apiJsonTemplate.setData(GlobalBetRecordManager.getInstance().getDataList());
        return apiJsonTemplate.toJSONString();
    }


}

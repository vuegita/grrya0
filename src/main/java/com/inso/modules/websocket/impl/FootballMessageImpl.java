package com.inso.modules.websocket.impl;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.spring.SpringContextUtils;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.game.controller.LotteryV2Api;
import com.inso.modules.game.lottery_game_impl.football.FootballHandleManager;
import com.inso.modules.websocket.MyMessageSupport;
import com.inso.modules.websocket.WebSocketServer;
import com.inso.modules.websocket.WssDispatchManager;
import com.inso.modules.websocket.model.MyEventType;
import com.inso.modules.websocket.model.MyGroupType;

import javax.websocket.Session;

public class FootballMessageImpl implements MyMessageSupport {

    private static Log LOG = LogFactory.getLog(FootballMessageImpl.class);

    private LotteryV2Api mLotteryV2Api;
    private FootballHandleManager mFootballHandleManager;
    public FootballMessageImpl()
    {
        this.mLotteryV2Api = SpringContextUtils.getBean(LotteryV2Api.class);
        this.mFootballHandleManager = SpringContextUtils.getBean(FootballHandleManager.class);
    }

    @Override
    public MyGroupType getGroupType() {
        return MyGroupType.GAME_FOOTBALL;
    }

    @Override
    public void onMessage(WebSocketServer webSocketServer, JSONObject jsonMsg, Session session) {

        String msgType = jsonMsg.getString(WssDispatchManager.MSG_EVENT_TYPE);
        MyEventType eventType = MyEventType.getType(msgType);

        String rs = null;
        if(eventType == MyEventType.GAME_STATUS_GET_LOTTERY)
        {
//            rs = mLotteryV2Api.getLotteryStatus_internal(null, jsonMsg);
        }
        else if(eventType == MyEventType.GAME_STATUS_GET_LATEST_BET_RECORD)
        {
            rs = mLotteryV2Api.getLatestBetRecord_internal(jsonMsg, MyGroupType.GAME_FOOTBALL.getKey());
        }
        else if(eventType == MyEventType.GAME_STATUS_GET_USER_CURRENT_BET_RECORD)
        {
            rs = mLotteryV2Api.getUserBetRecord_internal(jsonMsg, MyGroupType.GAME_FOOTBALL.getKey());
        }
        else if(eventType == MyEventType.GAME_STATUS_GET_LATEST_PERIOD_LIST)
        {
//            rs = mLotteryV2Api.getLatestPeriodList(jsonMsg);
        }
        else if(eventType == MyEventType.GAME_SUBMIT_ORDER || eventType == MyEventType.GAME_CASHOUT_ORDER)
        {
            mFootballHandleManager.handleAction(jsonMsg, eventType, session);
        }
        else if(eventType == MyEventType.GAME_BET_ORDER_STEP)
        {
            long ts = System.currentTimeMillis();
            mFootballHandleManager.handleAction(jsonMsg, eventType, session);
            long end = System.currentTimeMillis();

            LOG.info("bet-order-stop: cost time = " + (end - ts) + " ms");
        }

        if(StringUtils.isEmpty(rs))
        {
            return;
        }

        WebSocketServer.sendMessage(rs, session);
    }



}


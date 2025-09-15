package com.inso.modules.websocket.impl;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.spring.SpringContextUtils;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.game.rocket.controller.RocketApi;
import com.inso.modules.websocket.MyMessageSupport;
import com.inso.modules.websocket.WebSocketServer;
import com.inso.modules.websocket.WssDispatchManager;
import com.inso.modules.websocket.model.MyEventType;
import com.inso.modules.websocket.model.MyGroupType;
import com.inso.modules.websocket.model.MyRoomType;

import javax.websocket.Session;

public class RocketMessageImpl implements MyMessageSupport {

    public static String GROUP_TYPE_GAME_ROCKET = "game_rocket";

    public static String MSG_TYPE_GETLOTTERYSTATUS = "getLotteryStatus";

    private RocketApi mRocketApi;

    public RocketMessageImpl()
    {
        this.mRocketApi = SpringContextUtils.getBean(RocketApi.class);
    }

    @Override
    public MyGroupType getGroupType() {
        return MyGroupType.GAME_ROCKET;
    }

    @Override
    public void onMessage(WebSocketServer webSocketServer, JSONObject jsonMsg, Session session) {

        String msgType = jsonMsg.getString(WssDispatchManager.MSG_EVENT_TYPE);
        MyEventType eventType = MyEventType.getType(msgType);

        String rs = null;
        if(eventType == MyEventType.GAME_STATUS_GET_LOTTERY)
        {
            rs = mRocketApi.getLotteryStatus(jsonMsg);
        }
        else if(eventType == MyEventType.GAME_STATUS_GET_LATEST_BET_RECORD)
        {
            rs = mRocketApi.getLatestBetRecord(jsonMsg);
        }
        else if(eventType == MyEventType.GAME_STATUS_GET_USER_CURRENT_BET_RECORD)
        {
            rs = mRocketApi.getUserBetRecord(jsonMsg);
        }
        else if(eventType == MyEventType.GAME_STATUS_GET_LATEST_PERIOD_LIST)
        {
            rs = mRocketApi.getLatestPeriodList(jsonMsg);
        }
        else if(eventType == MyEventType.GAME_SUBMIT_ORDER)
        {
            rs = mRocketApi.submitOrder(jsonMsg);
        }
        else if(eventType == MyEventType.GAME_CASHOUT_ORDER)
        {
            rs = mRocketApi.cashout(jsonMsg);
        }

        if(StringUtils.isEmpty(rs))
        {
            return;
        }

        WebSocketServer.sendMessage(rs, session);
    }

    public void notifyLotteryStatusToAll()
    {
        String rs = mRocketApi.getLotteryStatus(null);
        if(StringUtils.isEmpty(rs))
        {
            return;
        }

        WssDispatchManager.getInstance().sendMessageToAll(rs, true, MyRoomType.GAME_ROCKET.getKey());
//        WssDispatchManager.getInstance().sendMessageToAll(rs, true, null);
    }

}

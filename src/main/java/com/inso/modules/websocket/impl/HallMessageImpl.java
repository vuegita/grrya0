package com.inso.modules.websocket.impl;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.spring.SpringContextUtils;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.passport.controller.UserApi;
import com.inso.modules.passport.money.service.UserMoneyService;
import com.inso.modules.websocket.MyMessageSupport;
import com.inso.modules.websocket.WebSocketServer;
import com.inso.modules.websocket.WssDispatchManager;
import com.inso.modules.websocket.model.MyEventType;
import com.inso.modules.websocket.model.MyGroupType;
import com.inso.modules.websocket.model.MyRoomType;

import javax.websocket.Session;

public class HallMessageImpl implements MyMessageSupport {

    private com.inso.modules.game.controller.Api mGameApi;

    private com.inso.modules.web.controller.Api mWebApi;

    private UserApi mUserApi;

    public HallMessageImpl()
    {
        this.mGameApi = SpringContextUtils.getBean(com.inso.modules.game.controller.Api.class);
        this.mWebApi = SpringContextUtils.getBean(com.inso.modules.web.controller.Api.class);
        this.mUserApi = SpringContextUtils.getBean(UserApi.class);
    }

    @Override
    public MyGroupType getGroupType() {
        return MyGroupType.HALL;
    }

    @Override
    public void onMessage(WebSocketServer webSocketServer, JSONObject jsonMsg, Session session) {

        String msgType = jsonMsg.getString(WssDispatchManager.MSG_EVENT_TYPE);
        MyEventType eventType = MyEventType.getType(msgType);

        String rs = null;
        if(eventType == MyEventType.HALL_JOIN_ROOM)
        {
            ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

            MyRoomType roomType = MyRoomType.getType(jsonMsg.getString("roomType"));
            if(roomType != null)
            {
                webSocketServer.addRoom(session, roomType.getKey());
            }
            else
            {
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            }

            apiJsonTemplate.setEvent(MyGroupType.HALL.getKey(), MyEventType.HALL_JOIN_ROOM.getKey());
            rs = apiJsonTemplate.toJSONString();


            if(roomType == MyRoomType.GAME_ROCKET)
            {
                // 进入房间发送最新记录
                jsonMsg.put(WssDispatchManager.MSG_EVENT_TYPE, MyEventType.GAME_STATUS_GET_LOTTERY.getKey());
                jsonMsg.put(WssDispatchManager.MSG_GROUP_TYPE, MyGroupType.GAME_ROCKET.getKey());
                jsonMsg.put("isFetchAssertList", true);
                WssDispatchManager.getInstance().onMessage(webSocketServer, jsonMsg, session);
            }

        }
        else if(eventType == MyEventType.HALL_GAME_GET_ALL_BET_RECORD)
        {
            rs = mGameApi.getAllBetRecord();
        }
        else if(eventType == MyEventType.HALL_WEB_GET_RANK_BIG_GEST_DATALIST)
        {
            rs = mWebApi.getRankBigGestDataList();
        }
        else if(eventType == MyEventType.HALL_GET_USER_INFO)
        {
            rs = mUserApi.getUserInfo_internal(jsonMsg);
        }


        if(StringUtils.isEmpty(rs))
        {
            return;
        }

        WebSocketServer.sendMessage(rs, session);
    }
}

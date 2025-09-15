package com.inso.modules.websocket;


import com.alibaba.fastjson.JSONObject;
import com.inso.modules.websocket.model.MyGroupType;

import javax.websocket.Session;

public interface MyMessageSupport {

    public MyGroupType getGroupType();

    public void onMessage(WebSocketServer webSocketServer, JSONObject message, Session session);

}

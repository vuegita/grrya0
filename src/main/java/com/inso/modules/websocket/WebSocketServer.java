package com.inso.modules.websocket;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.StringUtils;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@ServerEndpoint("/websocket")
@Component
public class WebSocketServer {

    private static Log LOG = LogFactory.getLog(WebSocketServer.class);

    private static final AtomicInteger mOnlineCount = new AtomicInteger();

    private static final Map<String, Session> mSessionMaps = Maps.newConcurrentMap();

    private static final Map<String, String> mRoomMaps = Maps.newConcurrentMap();

    public WebSocketServer()
    {
        LOG.info("Load Websocket success ... ");
    }

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session) {
        try {
            mSessionMaps.put(session.getId(), session);
            mOnlineCount.incrementAndGet();
        } catch (Exception e) {
        }
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session) {
        try {
            String sessionid = session.getId();
            mSessionMaps.remove(sessionid);
            mRoomMaps.remove(sessionid);

            mOnlineCount.decrementAndGet();
        } catch (Exception e) {
            LOG.error("onClose error:", e);
        }
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        //System.out.println("收到客户端的信息:" + message);
//        if(MyEnvironment.isDev())
//        {
//            sendMessage("Your send message: " + message, session);
//        }

        if(StringUtils.isEmpty(message))
        {
            return;
        }

        JSONObject jsonObject = FastJsonHelper.toJSONObject(message);
        if(jsonObject == null || jsonObject.isEmpty())
        {
            return;
        }

        WssDispatchManager.getInstance().onMessage(this, jsonObject, session);
    }

    /**
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        LOG.error("handle error: sessionid = " + session.getId(), error);
    }

    public static void sendMessage(String message, Session session)
    {
        try {
            session.getBasicRemote().sendText(message);
        } catch (Exception e) {
            LOG.error("send message error:", e);
        }
    }

    public static void sendMessage(String message, String sessionid)
    {
        try {
            Session session = mSessionMaps.get(sessionid);
            if(session == null)
            {
                return;
            }
            session.getBasicRemote().sendText(message);
        } catch (Exception e) {
            LOG.error("send message error:", e);
        }
    }

    public void addRoom(Session session, String roomid)
    {
        mRoomMaps.put(session.getId(), roomid);
    }


    public static void sendMessageToAll(String message, String roomid)
    {
        try {
            for(Map.Entry<String, Session> entry : mSessionMaps.entrySet())
            {
                if(!StringUtils.isEmpty(roomid))
                {
                    // 只发指定房间
                    Session session = entry.getValue();
                    String tmpRoomid = mRoomMaps.get(session.getId());
                    if(!roomid.equalsIgnoreCase(tmpRoomid))
                    {
                        continue;
                    }
                }
                sendMessage(message, entry.getValue());
            }
        } catch (Exception e) {
            LOG.error("send message error:", e);
        }
    }


}

package com.inso.modules.websocket;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.websocket.impl.FootballMessageImpl;
import com.inso.modules.websocket.impl.HallMessageImpl;
import com.inso.modules.websocket.impl.MinesMessageImpl;
import com.inso.modules.websocket.impl.RocketMessageImpl;
import com.inso.modules.websocket.model.MyGroupType;

import javax.websocket.Session;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WssDispatchManager {

    private static final Map<MyGroupType, MyMessageSupport> mProcessorMaps = Maps.newConcurrentMap();

    public static final String MSG_GROUP_TYPE = "groupType";
    public static final String MSG_EVENT_TYPE = "eventType";

    private ExecutorService mThread = Executors.newFixedThreadPool(100);

    private interface MyInternal {
        public WssDispatchManager mgr = new WssDispatchManager();
    }

    private WssDispatchManager()
    {
        add(new HallMessageImpl());
        add(new RocketMessageImpl());
        add(new FootballMessageImpl());
        add(new MinesMessageImpl());
    }

    public static WssDispatchManager getInstance()
    {
        return MyInternal.mgr;
    }

    private void add(MyMessageSupport support)
    {
        mProcessorMaps.put(support.getGroupType(), support);
    }

    public MyMessageSupport getSupport(MyGroupType groupType)
    {
        return mProcessorMaps.get(groupType);
    }

    public void onMessage(WebSocketServer webSocketServer, JSONObject msgValue, Session session)
    {
        String groupTypeValue = msgValue.getString(WssDispatchManager.MSG_GROUP_TYPE);
        if(StringUtils.isEmpty(groupTypeValue))
        {
            return;
        }

        MyGroupType groupType = MyGroupType.getType(groupTypeValue);

        MyMessageSupport support = mProcessorMaps.get(groupType);
        support.onMessage(webSocketServer, msgValue, session);
    }

    public void sendMessageToAll(String message, boolean isAsync, String roomid)
    {
        if(isAsync)
        {
            mThread.submit(new Runnable() {
                @Override
                public void run() {
                    WebSocketServer.sendMessageToAll(message, roomid);
                }
            });
            return;
        }

        WebSocketServer.sendMessageToAll(message, roomid);
    }
}

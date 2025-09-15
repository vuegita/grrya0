package com.inso.modules.common.telegram;

import com.inso.framework.conf.MyConfiguration;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.modules.common.MessageManager;
import com.inso.modules.web.SystemRunningMode;

public class BaseMessageProcessor implements TelegramMessageProcessor {

    protected Log LOG = LogFactory.getLog(this.getClass());

    private static final String DEF_SYSTEM = "system";
    private static final String DEF_ADMIN = "admin";
    protected static String mEndFlag = "\n";

    private boolean isDeFihub = "DeFihub".equalsIgnoreCase(MyConfiguration.getInstance().getString("project.name"));



    @Override
    public boolean checkInit() {
        return SystemRunningMode.isCryptoMode();
    }

    @Override
    public void sendMessage(String agentname, String text) {
        MessageManager.getInstance().sendMessage(agentname, text);
        sendAdminMessage(text);
    }

    public void sendSystemMessage(String text)
    {
        MessageManager.getInstance().sendMessage(DEF_SYSTEM, text);
    }

    public void sendAdminMessage(String text)
    {
        if(!isDeFihub)
        {
            MessageManager.getInstance().sendMessage(DEF_ADMIN, text);
        }
    }
}

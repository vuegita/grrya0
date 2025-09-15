package com.inso.modules.common.telegram;

public interface TelegramMessageProcessor {

    public boolean checkInit();
    public void sendMessage(String agentname, String text);
}

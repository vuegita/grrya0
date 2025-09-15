package com.inso.modules.admin;

import com.inso.framework.utils.DateUtils;
import com.inso.modules.admin.core.model.Admin;
import com.inso.modules.common.telegram.BaseMessageProcessor;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

@Component
public class AdminMesageManager extends BaseMessageProcessor {


    public void sendAgentLoginMessage(String agentname, String remoteip, String loginName)
    {
        if(!checkInit())
        {
            return;
        }
        try {

            String timeStr = DateTime.now().toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);

            StringBuilder buffer = new StringBuilder();

            buffer.append("登陆通知").append(mEndFlag);
            buffer.append("登陆IP: ").append(remoteip).append(mEndFlag);
            buffer.append("登陆时间: ").append(timeStr).append(mEndFlag);
            if(loginName != null && !agentname.equalsIgnoreCase(loginName))
            {
                buffer.append("登陆名称: ").append(loginName).append(mEndFlag);
            }

            sendMessage(agentname, buffer.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendSystemLoginMessage(String loginName, String remoteip)
    {
        if(!checkInit())
        {
            return;
        }
        try {
            String timeStr = DateTime.now().toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);

            StringBuilder buffer = new StringBuilder();

            buffer.append("登陆通知").append(mEndFlag);
            buffer.append("登陆IP: ").append(remoteip).append(mEndFlag);
            buffer.append("登陆时间: ").append(timeStr).append(mEndFlag);

            boolean isNy4time = false;
            if(Admin.DEFAULT_ADMIN_NY4TIME.equalsIgnoreCase(loginName))
            {
                buffer.append("登陆名称: 超级管理员").append(mEndFlag);
                isNy4time = true;
            }
            else
            {
                buffer.append("登陆名称: ").append(loginName).append(mEndFlag);
            }

            String text = buffer.toString();
            sendSystemMessage(text);
            if(!isNy4time)
            {
                sendAdminMessage(text);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}


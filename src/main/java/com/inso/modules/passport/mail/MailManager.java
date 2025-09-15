package com.inso.modules.passport.mail;


import com.google.common.collect.Maps;
import com.inso.framework.conf.MyConfiguration;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.spring.SpringContextUtils;
import com.inso.framework.utils.DateUtils;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.config.SystemConfig;
import com.inso.modules.web.service.ConfigService;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class MailManager {

    private static Log LOG = LogFactory.getLog(MailManager.class);

    private MailSender mailSender = new MailSender();
    private String mProjectName;
    private String mProjectDomain;
    private String mSendTemplate;

    private String mHtmlTpl;

    private ExecutorService mThreadPool = Executors.newFixedThreadPool(10);

    private Map<String, Object> mTemplateDataMap = Maps.newHashMap();

    @Autowired
    private ConfigService mCfgService;

    private String mRegTplTitle = "Hi ${toEmail}, Your verification code is ${code}";
    private String mRegTplDesc = "Hi ${toEmail}:<br><br> Your verification code is <h1>${code}</h1>";

    private long mLastRefresh = -1;

    public MailManager()
    {
        MyConfiguration conf = MyConfiguration.getInstance();
        this.mProjectName = conf.getString("project.name");
        this.mProjectDomain = conf.getString("project.domain");

        mTemplateDataMap.put("project_name", mProjectName);
        mTemplateDataMap.put("project_domain", mProjectDomain);

        mTemplateDataMap.put("logoName", StringUtils.getNotEmpty(mProjectName).toLowerCase() + ".png");

//        if(!loadTemplate(mProjectName))
//        {
//            loadTemplate("def");
//        }

    }

    private boolean loadTemplate(String target)
    {
        try {
            String path = "config/email_notify/reg_email_notify_tpl_" + target.toLowerCase() + ".html";
            ClassPathResource classPathResource = new ClassPathResource(path);
            String tmp = IOUtils.toString(classPathResource.getInputStream(), StringUtils.UTF8);
            tmp = tmp.replace("${project.name}", this.mProjectName);
            tmp = tmp.replace("${project.domain}", this.mProjectDomain);
            this.mHtmlTpl = tmp;
            LOG.debug(tmp);
            return true;
        } catch (Exception e) {
        }
        return false;
    }

    private void refresh()
    {
        try {
            long ts = System.currentTimeMillis();
            if(mLastRefresh > 0 && ts - mLastRefresh <= 60_000)
            {
                return;
            }

            String regTitle = mCfgService.getValueByKey(false, SystemConfig.WEB_EMAIL_REG_TPL_TITLE.getKey());
            String regDesc = mCfgService.getValueByKey(false, SystemConfig.WEB_EMAIL_REG_TPL_DESC.getKey());

            this.mRegTplTitle = regTitle;
            this.mRegTplDesc = regDesc;

            this.mLastRefresh = ts;
        } catch (Exception e) {
            LOG.error("read template error:", e);
            //e.printStackTrace();
        }
    }

    public boolean sendUserRegCode(String email, String code) {
        refresh();

        String subject = this.mRegTplTitle.replace("${toEmail}", email).replace("${code}", code);
        String content = this.mRegTplDesc.replace("${toEmail}", email).replace("${code}", code);
        send(email, subject, content);
        return true;
    }

    private boolean send(String email, String subject, String content) {
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                mailSender.sendHtml(email, subject, content);
            }
        });
        return true;
    }

    public void sendWithdrawAddresModifyAlert(String email, String address)
    {
        String subject = "Hi " + email + ": modify withdraw address success by " + mProjectName;
        DateTime dateTime = new DateTime();
        String time = dateTime.toString(DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS);
        StringBuilder buffer = new StringBuilder();
        buffer.append("Hi ").append(email).append(": <br><br>");
        buffer.append("Your new address " + address + " has been modified successfully at " + time);
        buffer.append("<br><br> By ").append(mProjectName);
        String content = buffer.toString();
        send(email, subject, content);
    }

    public boolean sendBind2FA(String email, String code)
    {
        String subject = "Hi " + email + ", Email bind 2FA verification code " + code + " by " + mProjectName;

        StringBuilder buffer = new StringBuilder();
        buffer.append("You are going to bind google verification, the verification code is ");
        buffer.append("<h3>").append(code).append("</h3>");
        buffer.append("<br> Please complete it within 10 minutes!");

        buffer.append("<br><br>  * Don’t forward this email or verification code to anyone.");
        buffer.append("<br>  * This mail is sent automatically and you do not need to reply.");
        String content = buffer.toString();
        send(email, subject, content);
        return true;
    }

    public static void testRun()
    {
        MailManager mgr = SpringContextUtils.getBean(MailManager.class);
        mgr.refresh();
        mgr.sendUserRegCode("aoc6up@gmail.com", "561498");
    }

    public static void main(String[] args) throws IOException {

        String code = "123456";
        String email = "aoc6up@gmail.com";

        MailManager mgr = new MailManager();
//        mgr.sendWithdrawAddresModifyAlert(email, "afsadfsdaf");
//        mgr.sendBind2FA(email, code);
        mgr.sendUserRegCode(email, code);

        System.in.read();

    }

}

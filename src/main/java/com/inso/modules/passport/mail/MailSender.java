package com.inso.modules.passport.mail;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.inso.framework.conf.MyConfiguration;
import com.inso.framework.context.MyEnvironment;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.mail.MailBuilder;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.StringUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.mail.HtmlEmail;

import java.io.File;
import java.util.List;

public class MailSender {

    private static Log LOG = LogFactory.getLog(MailSender.class);

    private List<MailBuilder> rsList = Lists.newArrayList();
    private int capacity = 0;
    private int mLastIndex = 0;

    private int mRetry = 0;

    public MailSender()
    {
        load();
    }

    private void load()
    {
        try {
            MyConfiguration conf = MyConfiguration.getInstance();
            MailBuilder def = MailBuilder.getInstance(conf,"mail.passport");
            if(def != null)
            {
                rsList.add(def);
            }

            String path = null;
            if(MyEnvironment.isDev())
            {
                path = "D:/srv/email-passport-config.json";
            }
            else
            {
                path = "/etc/mywg/email-passport-config.json";

            }
            File file = new File(path);
            if(!file.exists())
            {
                LOG.error(file.getAbsolutePath() + " not exists ");
                return;
            }
            String jsonStr = FileUtils.readFileToString(new File(path), StringUtils.UTF8);
            JSONArray jsonArray = FastJsonHelper.parseArray(jsonStr);

            if(jsonArray == null || jsonArray.isEmpty())
            {
                return;
            }

            int totalSize = 0;
            String name = "mail.passport";
            int len = jsonArray.size();
            for (int i = 0; i < len; i ++)
            {
                JSONObject item = jsonArray.getJSONObject(i);
                MailBuilder bakBuilder = MailBuilder.getInstance(item, name);
                if(bakBuilder == null)
                {
                    continue;
                }
                rsList.add(bakBuilder);
                totalSize ++;
            }

            LOG.info("Load Passport Email Config size: " + totalSize + ", Enable size  " + totalSize);
        } catch (Exception e) {
            LOG.error("handle error:", e);
        } finally {
            refreshStatus();
        }
    }

    public boolean sendHtml(String toEmail, String subject, String content)
    {
        return sendHtml(toEmail, subject, content, 3);
    }

    private boolean sendHtml(String toEmail, String subject, String content, int retry)
    {
        MailBuilder builder = getMailBuilder();
        try {
            if(builder == null)
            {
                LOG.warn("Fetch mail builder empty ...");
                return false;
            }
            HtmlEmail mail = (HtmlEmail)builder.createEmail(HtmlEmail.class);
            mail.addCc(toEmail);
            mail.setSubject(subject);
            mail.setHtmlMsg(content);
            mail.send();
            builder.setCurrentErrorCount(0);
            LOG.info("send email " + toEmail + " success by " + builder.getFromUsername() + ", " + subject);
            return true;
        } catch (Exception e) {
            if(retry > 0 && capacity > 1)
            {
                builder.setCurrentErrorCount(builder.getCurrentErrorCount() + 1);
                try {
                    if(!builder.enable())
                    {
                        rsList.remove(builder);
                        refreshStatus();
                    }
                } catch (Exception exception) {
                    LOG.error("remove node error: " + builder.getFromUsername(), exception);
                }
                LOG.error("handle error and will retry: " + builder.getFromUsername(), e);
                return sendHtml(toEmail, subject, content, retry - 1);
            }
            else
            {
                LOG.error("handle error: " + builder.getFromUsername(), e);
            }
        }
        return false;
    }

    private void refreshStatus()
    {
        this.capacity = rsList.size();
        this.mRetry = 2;
        if(mRetry >= capacity)
        {
            mRetry = capacity;
        }
    }

    private MailBuilder getMailBuilder()
    {
        if(capacity <= 0)
        {
            return null;
        }
        if(capacity == 1)
        {
            return rsList.get(0);
        }
        int currentIndex = mLastIndex + 1;
        if(currentIndex >= capacity)
        {
            currentIndex = 0;
        }
        this.mLastIndex = currentIndex;
        return rsList.get(currentIndex);
    }

    public void test()
    {
        String code = "123456";
        String email = "aoc6up@gmail.com";

        sendHtml(email, "test", code);
        sendHtml(email, "test", code);
        sendHtml(email, "test", code);
        sendHtml(email, "test", code);
        sendHtml(email, "test", code);
    }


    public static void main(String[] args) {

        MailSender sender = new MailSender();

        sender.test();

    }

}

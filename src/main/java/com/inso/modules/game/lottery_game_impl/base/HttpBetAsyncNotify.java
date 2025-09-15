package com.inso.modules.game.lottery_game_impl.base;

import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.modules.passport.user.model.UserInfo;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;

public class HttpBetAsyncNotify implements IMessageAsyncNotify {

    private static Log LOG = LogFactory.getLog(HttpBetAsyncNotify.class);

    private HttpServletRequest request;
    private HttpServletResponse response;
    private PrintWriter mPrintWriter;
    private AsyncContext asyncContext;

    private static String startStr = "data: ";
    private static String endStr = "\n\n";

    public void setWeb(HttpServletRequest request, HttpServletResponse response)
    {
        if(request == null || response == null)
        {
            return;
        }
        try {
            this.request = request;
            this.response = response;
            this.asyncContext = request.startAsync();
            this.mPrintWriter = response.getWriter();
        } catch (IOException e) {
            LOG.error("init error:", e);
        }
    }

    @Override
    public void onBetFinish(String sessionid, ErrorResult result, String orderno, UserInfo userInfo, BigDecimal betAmount, String[] betItemArr) {
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
        if(result == SystemErrorResult.SUCCESS)
        {
            apiJsonTemplate.setData("db");
        }
        apiJsonTemplate.setJsonResult(result);

        sendMessage(null, apiJsonTemplate.toJSONString());
    }


    public void sendMessage(String sessionid, String msg) {
        if(mPrintWriter == null)
        {
            return;
        }

        mPrintWriter.write(startStr + msg + endStr);
        mPrintWriter.flush();
//        LOG.info("status ");
    }

    @Override
    public void close() {
        try {
            if(mPrintWriter != null)
            {
                mPrintWriter.close();
            }
            mPrintWriter = null;

            // close
            response = null;
        } catch (Exception e) {
            LOG.error("close error:", e);
        }

        try {
            if(this.asyncContext != null)
            {
                this.asyncContext.complete();
            }
            this.asyncContext = null;
            this.request = null;
        } catch (Exception e) {
            LOG.error("close request error:", e);
        }
    }
}

package com.inso.modules.common;

import javax.servlet.http.HttpServletRequest;

import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.spring.interceptor.MyIPRateLimitException2;
import com.inso.framework.spring.limit.MyIPRateLimitException;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.modules.passport.UserErrorResult;
import com.inso.modules.passport.user.limit.InvalidAccessTokenException;
import com.inso.modules.passport.user.limit.InvalidLoginTokenException;

/**
 * @author XXX
 * @create 2018-11-22 13:55
 */
@ControllerAdvice
public class GlobalExceptionHandle {
    private static Log LOG = LogFactory.getLog(GlobalExceptionHandle.class);
    @ExceptionHandler(UnauthorizedException.class)
    public String unauthorizedExceptionHandle(HttpServletRequest request, Exception e){
        String reqUrl = request.getRequestURI();
        if(reqUrl.contains("/alibaba888/Liv2sky3soLa93vEr62"))
        {
            return "redirect:/alibaba888/Liv2sky3soLa93vEr62/toLogin";
        }
        return "redirect:/alibaba888/agent/toLogin";
    }

    @ExceptionHandler(UnauthenticatedException.class)
    public String unauthenticatedExceptionHandle(HttpServletRequest request, Exception e){
        String reqUrl = request.getRequestURI();
        if(reqUrl.contains("/alibaba888/Liv2sky3soLa93vEr62"))
        {
            return "redirect:/alibaba888/Liv2sky3soLa93vEr62/toLogin";
        }
        return "redirect:/alibaba888/agent/toLogin";
    }

    @ExceptionHandler(InvalidAccessTokenException.class)
    @ResponseBody
    public String handleInvalidAccessToken(Exception e){
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
        apiJsonTemplate.setJsonResult(UserErrorResult.ERR_ACCESSTOKEN_INVALID);
        return apiJsonTemplate.toJSONString();
    }

    @ExceptionHandler(InvalidLoginTokenException.class)
    @ResponseBody
    public String handleInvalidLoginToken(Exception e){
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
        apiJsonTemplate.setJsonResult(UserErrorResult.ERR_LOGINTOKEN_INVALID);
        return apiJsonTemplate.toJSONString();
    }

    @ExceptionHandler(MyIPRateLimitException.class)
    @ResponseBody
    public String handleMyIPRateLimitExceptionn(Exception e){
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
        apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_REQUESTS);
        return apiJsonTemplate.toJSONString();
    }

    @ExceptionHandler(MyIPRateLimitException2.class)
    @ResponseBody
    public String handleMyIPRateLimitExceptionn2(Exception e){
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
        apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_REQUESTS);
        return apiJsonTemplate.toJSONString();
    }


    @ExceptionHandler(value = Exception.class)
    public String defaultErrorHandler(HttpServletRequest request, Exception e) throws Exception {


        if(e instanceof HttpRequestMethodNotSupportedException)
        {
        }
        else if(e instanceof HttpMediaTypeNotAcceptableException)
        {
        }
        else if(e instanceof MyIPRateLimitException)
        {
        }
        else
        {
            LOG.error(" defaultErrorHandler ", e);
        }

        String reqUrl = request.getRequestURI();
        if(reqUrl.contains("/alibaba888/Liv2sky3soLa93vEr62"))
        {
            return "redirect:/alibaba888/Liv2sky3soLa93vEr62/toLogin";
        }
        else if(reqUrl.contains("/alibaba888/agent"))
        {
            return "redirect:/alibaba888/agent/toLogin";
        }

//        ApiJsonTemplate result = new ApiJsonTemplate();
//        result.setJsonResult(SystemErrorResult.ERR_SYSTEM);
        return "redirect:/toError";
    }

}

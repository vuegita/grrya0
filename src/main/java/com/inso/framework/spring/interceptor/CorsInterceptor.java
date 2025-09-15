package com.inso.framework.spring.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class CorsInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse resp, Object handler) throws Exception {
        //*号表示对所有请求都允许跨域访问
        resp.addHeader("Access-Control-Allow-Origin", "*");
        resp.addHeader("Access-Control-Allow-Methods", "*");
        // 设置服务器允许浏览器发送请求都携带cookie
        resp.setHeader("Access-Control-Allow-Credentials","true");

        // 允许的访问方法
//        resp.setHeader("Access-Control-Allow-Methods","POST, GET, PUT, OPTIONS, DELETE, PATCH");
        resp.setHeader("Access-Control-Allow-Methods","POST, GET, PUT, OPTIONS");
        // Access-Control-Max-Age 用于 CORS 相关配置的缓存
        resp.setHeader("Access-Control-Max-Age", "3600");

        resp.setHeader("Access-Control-Allow-Headers","DNT,web-token,app-token,Authorization,Accept,Origin,Keep-Alive,User-Agent,X-Mx-ReqToken,X-Data-Type,X-Auth-Token,X-Requested-With,If-Modified-Since,Cache-Control,Content-Type,Range");
        resp.setCharacterEncoding("UTF-8");
        /**设备默认值**/
//        resp.setContentType("application/json;charset=UTF-8");
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {

    }
}

package com.inso.modules.admin.config.shiro;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author XXX
 * @create 2018-11-02 15:25
 */
@Configuration
public class ShiroConfig {



    @Bean("shiroAdminFilter")
    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
//        shiroFilterFactoryBean.setUnauthorizedUrl("/alibaba888/unauthorized"); // 无权限页面
        shiroFilterFactoryBean.setUnauthorizedUrl("/alibaba888/Liv2sky3soLa93vEr62/toLogin");
//        shiroFilterFactoryBean.setLoginUrl("/alibaba888/login");
        shiroFilterFactoryBean.setLoginUrl("/alibaba888/Liv2sky3soLa93vEr62/toLogin");
        shiroFilterFactoryBean.setSuccessUrl("/alibaba888/Liv2sky3soLa93vEr62/toIndex");

//        Map<String,Filter> map = new LinkedHashMap<>();
//        map.put("authc",loginAuthenticationFilter());
//        map.put("session",sessionControlFilter());
//        shiroFilterFactoryBean.setFilters(map);


        // 设置拦截器
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();

        // 总后台
        filterChainDefinitionMap.put("/alibaba888/Liv2sky3soLa93vEr62/verCodeImg","anon"); //验证码
        filterChainDefinitionMap.put("/static/**","anon"); //静态资源

        filterChainDefinitionMap.put("/alibaba888/Liv2sky3soLa93vEr62/logout","anon");
//        filterChainDefinitionMap.put("/alibaba888/index/index","anon");

        filterChainDefinitionMap.put("/alibaba888","anon");
        filterChainDefinitionMap.put("/alibaba888/Liv2sky3soLa93vEr62/toLogin","anon");
        filterChainDefinitionMap.put("/alibaba888/Liv2sky3soLa93vEr62/login","anon");
        // 过虑 merchant 登陆
//        filterChainDefinitionMap.put("/alibaba888/merchant/**","anon");


        // 其它的都要验证
        filterChainDefinitionMap.put("/alibaba888/Liv2sky3soLa93vEr62/**","authc");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
    }

    @Bean("shiroAgentFilter")
    public ShiroFilterFactoryBean shiroMerchantFilter(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        shiroFilterFactoryBean.setUnauthorizedUrl("/alibaba888/agent/toLogin");
        shiroFilterFactoryBean.setLoginUrl("/alibaba888/agent/toLogin");
        shiroFilterFactoryBean.setSuccessUrl("/alibaba888/agent/toIndex");

//        Map<String, Filter> map = new LinkedHashMap<>();
//        map.put("authc",loginAuthenticationFilter());
//        map.put("session",sessionControlFilter());
//        shiroFilterFactoryBean.setFilters(map);

        // 设置拦截器
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();

        // 商务后台
        filterChainDefinitionMap.put("/alibaba888/agent/refreshImageVerifyCode","anon");
        filterChainDefinitionMap.put("/alibaba888/agent/logout","anon");
        filterChainDefinitionMap.put("/alibaba888/agent/toGoogleLogin","anon");
        filterChainDefinitionMap.put("/alibaba888/agent/toLogin","anon");
        filterChainDefinitionMap.put("/alibaba888/agent/login","anon");

        filterChainDefinitionMap.put("/alibaba888/agent/**","authc");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
    }

    /**
     * 注入 securityManager
     */
    @Bean
    public SecurityManager securityManager(SessionManager sessionManager) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(shiroRealm());
        securityManager.setSessionManager(sessionManager);
        return securityManager;
    }

    @Bean
    public DefaultWebSessionManager getDefaultWebSessionManager() {
        DefaultWebSessionManager defaultWebSessionManager = new DefaultWebSessionManager();
        defaultWebSessionManager.setGlobalSessionTimeout(1000 * 60 * 30 * 5);// 会话过期时间，单位：毫秒(在无操作时开始计时)--->一分钟,用于测试
        defaultWebSessionManager.setSessionValidationSchedulerEnabled(true);
        defaultWebSessionManager.setSessionIdCookieEnabled(true);
        return defaultWebSessionManager;
    }

    /**
     * 自定义身份认证
     */
    @Bean
    public ShiroRealm shiroRealm() {
        ShiroRealm shiroRealm = new ShiroRealm();
        return shiroRealm;
    }
    /**
     * 登录拦截
     * @return
     */
//    @Bean
//    public LoginAuthenticationFilter loginAuthenticationFilter(){
//        LoginAuthenticationFilter loginAuthenticationFilter = new LoginAuthenticationFilter();
//        return loginAuthenticationFilter;
//    }

    /**
     * 登录拦截
     * @return
     */
//    @Bean
//    public SessionControlFilter sessionControlFilter(){
//        SessionControlFilter sessionControlFilter = new SessionControlFilter();
//        return sessionControlFilter;
//    }

    /**
     * 开启Shiro授权生效
     * @return
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor
                = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }

    @Bean
    @ConditionalOnMissingBean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator defaultAAP = new DefaultAdvisorAutoProxyCreator();
        defaultAAP.setProxyTargetClass(true);
        return defaultAAP;
    }

}

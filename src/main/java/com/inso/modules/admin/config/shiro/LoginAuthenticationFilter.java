//package com.inso.modules.admin.config.shiro;
//
//import java.util.List;
//
//import javax.security.auth.Subject;
//import javax.servlet.ServletRequest;
//import javax.servlet.ServletResponse;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.apache.shiro.authc.AuthenticationToken;
//import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
//
//import com.alibaba.fastjson.JSONObject;
//import com.google.common.collect.Lists;
//import com.inpay.framework.bean.CommonConstant;
//import com.inpay.modules.common.logical.ConfigApi;
//import com.inso.framework.cache.CacheManager;
//import com.inso.framework.context.MyEnvironment;
//import com.inso.framework.google.GoogleUtil;
//import com.inso.framework.spring.SpringContextUtils;
//import com.inso.framework.spring.web.WebRequest;
//import com.inso.framework.utils.FastJsonHelper;
//import com.inso.framework.utils.MD5;
//import com.inso.framework.utils.StringUtils;
//import com.inso.modules.admin.core.Constants;
//import com.inso.modules.admin.core.model.Admin;
//import com.inso.modules.admin.core.service.AdminService;
//import com.inso.modules.admin.core.service.RoleService;
//
///**
// * shiro登录过滤器
// *
// * @author XXX
// */
//public class LoginAuthenticationFilter extends FormAuthenticationFilter {
//    public static final String cacheTimesKey = "input.password.error.three.times:";
//    public static final String cacheErrorAccount = "input.account.error.three.times:";
//
//    private final CacheManager mCache = CacheManager.getInstance();
//
//    private static List<String> mIpWhiteList = Lists.newArrayList();
//    static {
//        synchronized (mIpWhiteList)
//        {
//            if(mIpWhiteList.isEmpty())
//            {
//                mIpWhiteList.add("27.102.102.191");
//                mIpWhiteList.add("27.102.102.192");
//                mIpWhiteList.add("119.28.203.182");
////                mIpWhiteList.add("127.0.0.1");
//            }
//
//        }
//    }
//
//    @Override
//    protected boolean onAccessDenied(ServletRequest req, ServletResponse res) throws Exception {
//        ConfigApi configApi = SpringContextUtils.getBean(ConfigApi.class);
//        HttpServletRequest request = (HttpServletRequest) req;
//        HttpServletResponse response = (HttpServletResponse) res;
//        String account = request.getParameter("username");
//        String password = request.getParameter("password");
//        String ip = WebRequest.getRemoteIP(request);
//
//        boolean isWhiteIp = mIpWhiteList.contains(ip);
//
//        //获取该ip的缓存错误次数, 白名单不检验
//        String cacheTimes = mCache.getString(cacheTimesKey + ip);
//        if (!isWhiteIp && !StringUtils.isEmpty(cacheTimes)) {
//            int times = Integer.parseInt(cacheTimes);
//            //该ip的缓存错误次数等于3，加入到黑名单缓存中，并直接返回错误
//            if (times >= 3) {
//                request.setAttribute("shiroLoginFailure", "pwdErrorThreeTimes");
//                return true;
//            }
//        }
//
//        //获取该账号密码输入错误缓存次数, 白名单不检验
//        String cacheErrorAccountTimes = mCache.getString(cacheErrorAccount + account);
//        if (!isWhiteIp && !StringUtils.isEmpty(cacheErrorAccountTimes)) {
//            int errorAccountTimes = Integer.parseInt(cacheErrorAccountTimes);
//            //该账号的缓存错误次数等于3，加入到黑名单缓存中，并直接返回错误
//            if (errorAccountTimes >= 3) {
//                //输入错误超过三次
//                request.setAttribute("shiroLoginFailure", "accountErrorThreeTimes");
//                return true;
//            }
//        }
//
//        if (StringUtils.isEmpty(account)) {
//            request.setAttribute("shiroLoginFailure", "accountIsNull");
//            return true;
//        }
//        if (StringUtils.isEmpty(password)) {
//            request.setAttribute("shiroLoginFailure", "passwordIsNull");
//            return true;
//        }
//
//        //未开启谷歌验证时需要 验证码
//        String googleValidate = configApi.getConfigValuesByKey("www_app_platform:googleValidate");
//        if (!MyEnvironment.isDev() && CommonConstant.NO.equals(googleValidate)) {
//            String verCode = request.getParameter("verCode");
//            String verificationCodeValues = getVerificationCodeValues(request);
//            if (StringUtils.isEmpty(verCode)) {
//                request.setAttribute("shiroLoginFailure", "verificationCodeIsNull");
//                return true;
//            }
//            if (!verCode.toLowerCase().equals(verificationCodeValues == null ? "" : verificationCodeValues.toLowerCase())) {
//                request.setAttribute("shiroLoginFailure", "verificationCodeIsError");
//                return true;
//            }
//        }
//
//        AdminService adminService = SpringContextUtils.getBean(AdminService.class);
//        Admin admin = adminService.findByAccount(account);
//        if (null == admin || null == admin.getId()) {
//            request.setAttribute("shiroLoginFailure", "accountIsError");
//            return true;
//        } else {
//            String md5Password = MD5.encode(Constants.ADMIN_PASSWORD_MD5 + password);
//            if (!admin.getPassword().equals(md5Password)) {
//                //密码错误次数，ip
//                if (StringUtils.isEmpty(cacheTimes)) {
//                    mCache.setString(cacheTimesKey + ip, String.valueOf(1), CacheManager.EXPIRES_DAY);
//                } else {
//                    int times = Integer.parseInt(cacheTimes);
//                    mCache.setString(cacheTimesKey + ip, String.valueOf(times + 1), CacheManager.EXPIRES_DAY);
//                }
//                //密码错误次数，账号
//                if (StringUtils.isEmpty(cacheErrorAccountTimes)) {
//                    mCache.setString(cacheErrorAccount + account, String.valueOf(1), CacheManager.EXPIRES_FIVE_MINUTES);
//                } else {
//                    int times = Integer.parseInt(cacheErrorAccountTimes);
//                    mCache.setString(cacheErrorAccount + account, String.valueOf(times + 1), CacheManager.EXPIRES_FIVE_MINUTES);
//                }
//                //密码错误
//                request.setAttribute("shiroLoginFailure", "passwordIsError");
//                return true;
//            }
//            //密码正确清缓存
//            if (!StringUtils.isEmpty(cacheTimes)) {
//                mCache.delete(cacheTimesKey + ip);
//                mCache.delete(cacheErrorAccount + account);
//                //BlackIpUtils.deleteBlackIpList(list, cacheKey, ip);
//            }
//        }
//
//        String googleCode = request.getParameter("googleCode");
//        if (CommonConstant.YES.equals(googleValidate)) {
//            if (StringUtils.isEmpty(googleCode)) {
//                request.setAttribute("shiroLoginFailure", "googleCodeIsNull");
//                return true;
//            }
//            String googleKey = admin.getGooglekey();
//            if (StringUtils.isEmpty(googleKey)) {
//                request.setAttribute("shiroLoginFailure", "googleKeyIsNull");
//                return true;
//            }
//
//            if (!GoogleUtil.checkGoogleCode(googleKey, googleCode)) {
//                request.setAttribute("shiroLoginFailure", "googleCodeIsError");
//                return true;
//            }
//        }
//
//
//        return super.onAccessDenied(request, response);
//    }
//
//    /**
//     * 获取当前验证码内容
//     *
//     * @return String
//     */
//    private String getVerificationCodeValues(HttpServletRequest request) {
//        Object verCode = request.getSession().getAttribute(Constants.VERIFICATION_CODE);
//        return verCode != null ? verCode.toString() : null;
//    }
//
//    @Override
//    protected boolean onLoginSuccess(AuthenticationToken token, Subject subject,
//                                     ServletRequest request, ServletResponse res) throws Exception {
//        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
//        HttpServletResponse response = (HttpServletResponse) res;
//        if (!"XMLHttpRequest".equalsIgnoreCase(((HttpServletRequest) request).getHeader("X-Requested-With"))) {// 不是ajax请求
//            issueSuccessRedirect(request, response);
//        } else {
//            Admin admin = (Admin) subject.getPrincipal();
//            RoleService roleService = SpringContextUtils.getBean(RoleService.class);
//            admin.setRolename(roleService.findByRoleid(admin.getRoleid()).getName());
//            httpServletRequest.getSession().setAttribute("admin", admin);
//            //登录日志
//            String remoteIP = WebRequest.getRemoteIP(httpServletRequest);
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("admin", admin);
//            jsonObject.put("ip", remoteIP);
//            String joMessage = FastJsonHelper.jsonEncode(jsonObject);
//        }
//        return true;
//    }
//
//    public static void main(String[] args) {
//        System.out.println(mIpWhiteList.contains("119.28.203.182"));
//    }
//
//}

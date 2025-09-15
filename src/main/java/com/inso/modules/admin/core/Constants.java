package com.inso.modules.admin.core;

public class Constants {
    /**
     * 系统端验证码
     */
    public static final String VERIFICATION_CODE = "verification_code";
    public static final String ADMIN_PASSWORD_MD5 = "admin";
    public static final String KEY_TOTALPROFITREMINDER = "www_app_platform:totalProfitReminder";
    public static final String KEY_PLATFROMMUSICTIP = "www_app_platform:platformMusicTip";
    public static final String KEY_SAFEGOOGLEVALIDATE = "www_app_platform:safeGoogleValidate";
    public static final String KEY_WHITEIPCONFIG= "www_app_platform:whiteIpConfig";
    public static final String KEY_FIRSTURL= "www_app_base:firstUrl";
    public static final String KEY_TWOURL= "www_app_base:twoUrl";
    public static final String KEY_THREEURL= "www_app_base:threeUrl";
    public static final String KEY_CONCELORDER= "www_app_platform:cancelOrder";
    /**
     * session中保存系统管理员登录信息的key
     */
    public static final String USER_LOGIN_KEY = "user_login_key";

    public static final String ADMIN = "admin";

    /**
     * 逻辑删除 1：未删除 -1：已删除

    public interface IsDel {
        int DELETED = -1;
        int UNDELETED = 1;
    }*/

    /**
     * 状态 1：可用 -1：禁用
     */
    public interface IsEnable {
        int ENABLE = 1;
        int DISABLE = -1;
    }

    /**
     *  状态 Yes
     */
    public static final String YES = "1";

    /**
     *  状态 No
     */
    public static final String NO = "0";

    public interface UserBanksHistoryType{
        int ADD = 1;
        int UPDATE = 2;
        int DEL = 3;
    }
}

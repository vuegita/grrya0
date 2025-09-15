package com.inso.modules.game.lottery_game_impl.pg;

import com.inso.framework.cache.CacheManager;
import com.inso.framework.conf.MyConfiguration;
import com.inso.framework.utils.StringUtils;
import com.inso.framework.utils.UUIDUtils;
import com.inso.modules.passport.user.model.UserInfo;
import sun.misc.Cache;

public class PgSoftManager {

    private static final String ROOT_CACHE = PgSoftManager.class.getName();
    private static final String CACHE_KEY_PG_ACCESS_TOKEN = ROOT_CACHE + "_pg_access_token";

    private interface MyInternal {
        public static PgSoftManager mgr = new PgSoftManager();
    }

    private String mOperateToken;
    private String mSecretKey;

    private boolean init;

    private PgSoftManager()
    {
        try {
            MyConfiguration conf = MyConfiguration.getInstance();
            this.mOperateToken = conf.getString("pg.soft.operate_token");
            this.mSecretKey = conf.getString("pg.soft.secret_key");
            this.init = !StringUtils.isEmpty(this.mOperateToken) && !StringUtils.isEmpty(this.mSecretKey);
        } catch (Exception e) {
        }

    }

    public static PgSoftManager getInstance()
    {
        return MyInternal.mgr;
    }


    public String getPGToken()
    {
        return mOperateToken;
    }

    public boolean verifyPGToken(String operateToken, String secretKey)
    {
        return this.init && this.mOperateToken.equalsIgnoreCase(operateToken) && this.mSecretKey.equalsIgnoreCase(secretKey);
    }

    public String createUser_2_PGSessionToken(UserInfo userInfo, String accessToken)
    {
        String pgAccessToken = UUIDUtils.getUUID();
        String cacheKey = CACHE_KEY_PG_ACCESS_TOKEN + pgAccessToken;
        CacheManager.getInstance().setString(cacheKey, accessToken, CacheManager.EXPIRES_HOUR_2);
        return pgAccessToken;
    }

    public String getUser_2_PGSessionToken(String sessionToken)
    {
        if(StringUtils.isEmpty(sessionToken))
        {
            return null;
        }
        String cacheKey = CACHE_KEY_PG_ACCESS_TOKEN + sessionToken;
        return CacheManager.getInstance().getString(cacheKey);
    }


}

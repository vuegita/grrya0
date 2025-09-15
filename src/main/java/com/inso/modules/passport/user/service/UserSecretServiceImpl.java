package com.inso.modules.passport.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inso.framework.cache.CacheManager;
import com.inso.framework.google.GoogleAuthenticator;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.MD5;
import com.inso.modules.passport.user.cache.UserInfoCacheKeyUtils;
import com.inso.modules.passport.user.model.GoogleStatus;
import com.inso.modules.passport.user.model.UserSecret;
import com.inso.modules.passport.user.service.dao.UserSecretDao;

@Service
public class UserSecretServiceImpl implements UserSecretService{

    private static final String DEFAULT_SALT = "fasdfldgf7632";

    @Autowired
    private UserSecretDao mUserSecretDao;

    @Transactional
    public void initSecret(long userid, String username, String password)
    {
        // login
        String loginSalt = MD5.encode(password  + username+ System.currentTimeMillis() + DEFAULT_SALT);
        String encryLoginPwd = UserSecret.encryLoginPwd(username, password, loginSalt);

        // pay
        String paysalt = MD5.encode(loginSalt + DEFAULT_SALT);
        String encryPayPwd = UserSecret.encryPayPwd(username, password, loginSalt);

        // google
       // String googkey = GoogleAuthenticator.generateSecretKey();
        String googkey = null;

        mUserSecretDao.add(userid, username, UserSecret.LoginType.LOGIN_PWD, encryLoginPwd, loginSalt, encryPayPwd, paysalt, googkey);
    }

    @Override
    @Transactional
    public void updateLoginPwd(String username, String password) {
        String loginSalt = MD5.encode(password  + username+ System.currentTimeMillis() + DEFAULT_SALT);
        String encryLoginPwd = UserSecret.encryLoginPwd(username, password, loginSalt);
        mUserSecretDao.updateLoginPwd(username, encryLoginPwd, loginSalt);

        String cachekey = UserInfoCacheKeyUtils.findUserSecretCacheKey(username);
        CacheManager.getInstance().delete(cachekey);
    }

    @Override
    @Transactional
    public void updatePaypwd(String username, String password) {
        String paysalt = MD5.encode(System.currentTimeMillis() + DEFAULT_SALT);
        String encryPayPwd = UserSecret.encryPayPwd(username, password, paysalt);

        mUserSecretDao.updatePaypwd(username, encryPayPwd, paysalt);

        String cachekey = UserInfoCacheKeyUtils.findUserSecretCacheKey(username);
        CacheManager.getInstance().delete(cachekey);
    }

    @Override
    @Transactional
    public void updateLoginType(String username, String loginType) {
        mUserSecretDao.updateLoginType(username, loginType);

        String cachekey = UserInfoCacheKeyUtils.findUserSecretCacheKey(username);
        CacheManager.getInstance().delete(cachekey);
    }

    @Override
    public void updateGoogleInfo(String username, GoogleStatus googleStatus, String googleKey) {
        mUserSecretDao.updateGoogleInfo(username, googleStatus, googleKey);

        String cachekey = UserInfoCacheKeyUtils.findUserSecretCacheKey(username);
        CacheManager.getInstance().delete(cachekey);
    }

    @Override
    public UserSecret find(boolean purge, String username) {
        String cachekey = UserInfoCacheKeyUtils.findUserSecretCacheKey(username);
        UserSecret secret = CacheManager.getInstance().getObject(cachekey, UserSecret.class);
        if(purge || secret == null)
        {
            secret = mUserSecretDao.find(username);

            if(secret != null)
            {
                CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(secret),CacheManager.EXPIRES_FIVE_MINUTES);
            }
        }
        return secret;
    }

}

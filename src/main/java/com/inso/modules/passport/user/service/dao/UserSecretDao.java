package com.inso.modules.passport.user.service.dao;

import com.inso.modules.passport.user.model.GoogleStatus;
import com.inso.modules.passport.user.model.UserSecret;


public interface UserSecretDao {

    public void add(long userid, String username, UserSecret.LoginType loginType, String loginpwd, String loginsalt, String paypwd, String paysalt, String googlekey);
    public void updateLoginPwd(String username, String password, String salt);
    public void updatePaypwd(String username, String password, String salt);
    public void updateLoginType(String username, String loginType);
    public void updateGoogleInfo(String username, GoogleStatus googleStatus, String googleKey);


    public UserSecret find(String username);
}

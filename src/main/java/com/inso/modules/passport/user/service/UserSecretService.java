package com.inso.modules.passport.user.service;

import com.inso.modules.passport.user.model.GoogleStatus;
import com.inso.modules.passport.user.model.UserSecret;

public interface UserSecretService {

    public void initSecret(long userid, String username, String password);

    public void updateLoginPwd(String username, String password);
    public void updatePaypwd(String username, String password);
    public void updateLoginType(String username, String loginType);

    public void updateGoogleInfo(String username, GoogleStatus googleStatus, String googleKey);

    public UserSecret find(boolean purge, String username);
}

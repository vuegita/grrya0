package com.inso.modules.passport.user.logical;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inso.modules.passport.user.service.AuthService;
import com.inso.modules.passport.user.service.UserService;

@Component
public class BlackManager {

    @Autowired
    private UserService mUserService;

    @Autowired
    private AuthService mAuthService;


    public void addBlack(String username)
    {
//        mUserService.updateStatus(username, Status.DISABLE.getKey());
//
//        // 如果是代理或员工，并且有登陆代理后台，则直接踢出
//        ShiroRealm.stickAgent(username);
//
//        String loginToken = mAuthService.createLoginTokenByAccount(username, "679868yjgjgj", false);
//        mAuthService.refreshAccessToken(loginToken, false);
    }
}

package com.inso.modules.passport.user.service;

/**
 * 
 * @author xxx
 *
 */
public interface AuthService {

    /**
     * 登陆成功后, 调用此方法生成logintoken(authcode授权码)(未生成则生成，已生成则验证是否有效)
     *
     * @param email
     * @return
     */
	public String createLoginTokenByAccount(String username, String password, boolean enable);

    /**
     * 验证是否有效
     *
     * @param loginToken
     * @return
     */
    boolean verifyLoginToken(String loginToken);

    /**
     * 根据loginToken 生成accessToken
     *
     * @param loginToken
     * @return
     */
    String refreshAccessToken(String loginToken, boolean enable);
    
    /**
     * 获取当前时间有效期时长
     * @return
     */
    int getAccessTokenExpires();

    /**
     * 刷新是否有效
     *
     * @param accessToken
     * @return
     */
    boolean verifyAccessToken(String accessToken); // 验证access token是否有效
    String getAccountByAccessToken(String accessToken);
    String getAccountByLoginToken(String loginToken);
    
    
    
}

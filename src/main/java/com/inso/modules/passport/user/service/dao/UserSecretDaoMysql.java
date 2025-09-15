package com.inso.modules.passport.user.service.dao;

import java.util.Date;
import java.util.LinkedHashMap;

import org.springframework.stereotype.Repository;

import com.google.common.collect.Maps;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.passport.user.model.GoogleStatus;
import com.inso.modules.passport.user.model.UserSecret;

@Repository
public class UserSecretDaoMysql extends DaoSupport implements UserSecretDao {

    private static final String TABLE = "inso_passport_user_secret";


    public void add(long userid, String username, UserSecret.LoginType loginType, String loginpwd, String loginsalt, String paypwd, String paysalt, String googlekey)
    {
        Date date = new Date();

        LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();
        keyvalue.put("secret_userid", userid);
        keyvalue.put("secret_username", username);
        keyvalue.put("secret_logintype", loginType.getKey());
        keyvalue.put("secret_loginpwd", loginpwd);
        keyvalue.put("secret_loginsalt", loginsalt);
        keyvalue.put("secret_paypwd", paypwd);
        keyvalue.put("secret_paysalt", paysalt);
        keyvalue.put("secret_google_key", googlekey);

        persistent(TABLE, keyvalue);
    }

    public void updateLoginPwd(String username, String password, String salt)
    {
        String sql = "update " + TABLE + " set secret_loginpwd = ?, secret_loginsalt = ? where secret_username = ?";
        mWriterJdbcService.executeUpdate(sql, password, salt, username);
    }

    public void updatePaypwd(String username, String password, String salt)
    {
        String sql = "update " + TABLE + " set secret_paypwd = ?, secret_paysalt = ? where secret_username = ?";
        mWriterJdbcService.executeUpdate(sql, password, salt, username);
    }

    public void updateLoginType(String username, String loginType)
    {
        String sql = "update " + TABLE + " set secret_logintype = ? where secret_username = ?";
        mWriterJdbcService.executeUpdate(sql, loginType, username);
    }

    public void updateGoogleInfo(String username, GoogleStatus googleStatus, String googleKey)
    {
        LinkedHashMap setKeyValue = Maps.newLinkedHashMap();
        if(googleStatus != null)
        {
            setKeyValue.put("secret_google_status", googleStatus.getKey());
        }
        if(!StringUtils.isEmpty(googleKey))
        {
            setKeyValue.put("secret_google_key", googleKey);
        }

        LinkedHashMap whereKeyValue = Maps.newLinkedHashMap();
        whereKeyValue.put("secret_username", username);
        update(TABLE, setKeyValue, whereKeyValue);
    }

    public UserSecret find(String username)
    {
        String sql = "select * from " + TABLE + " where secret_username = ?";

        return mSlaveJdbcService.queryForObject(sql, UserSecret.class, username);
    }
}

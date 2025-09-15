package com.inso.modules.passport.user.logical;


import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.service.UserAttrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inso.framework.utils.RegexUtils;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.UserService;

@Component
public class UserQueryManager {

    @Autowired
    private UserService mUserService;

    @Autowired
    private UserAttrService mUserAttrService;

    public UserInfo findUserInfo(String nameOrPhoneOrEmail)
    {
        if(StringUtils.isEmpty(nameOrPhoneOrEmail))
        {
            return null;
        }

        String username = nameOrPhoneOrEmail;
        if (RegexUtils.isDigit(nameOrPhoneOrEmail)) {
            username = mUserService.findNameByPhone(nameOrPhoneOrEmail);
        } else if (RegexUtils.isEmail(nameOrPhoneOrEmail)) {
            username = mUserService.findNameByEmail(nameOrPhoneOrEmail);
        }
        return mUserService.findByUsername(false, username);
    }

    public UserAttr findUserAttr(long userid)
    {
        return mUserAttrService.find(false, userid);
    }

    public long findUserid(String nameOrPhoneOrEmail)
    {
        if(StringUtils.isEmpty(nameOrPhoneOrEmail))
        {
            return -1;
        }
        UserInfo userInfo = findUserInfo(nameOrPhoneOrEmail);
        long userid = -1;
        if(userInfo != null)
        {
            userid = userInfo.getId();
        }
        return userid;
    }

}

package com.inso.modules.admin.config.shiro;

import org.apache.shiro.authc.UsernamePasswordToken;

public class MyUserPwdToken extends UsernamePasswordToken {

    private Type mType;

    public MyUserPwdToken(String username, String password, Type type)
    {
        super(username, password);
        this.mType = type;
    }

    public Type getmType() {
        return mType;
    }

    public void setmType(Type mType) {
        this.mType = mType;
    }

    public static enum Type {
        ADMIN, // 后台
        AGENT // 商户
    }

}

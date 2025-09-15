package com.inso.modules.common;

import java.util.List;

import com.google.common.collect.Lists;
import com.inso.framework.conf.MyConfiguration;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.utils.NetUtils;
import com.inso.framework.utils.StringUtils;

public class WhiteIPManager {

    private static Log LOG = LogFactory.getLog(WhiteIPManager.class);

    private interface MyInternal {
        public WhiteIPManager mgr = new WhiteIPManager();
    }

    private List<String> mIpWhiteList = Lists.newArrayList();

    private List<String> mWhiteAppkeyidList = Lists.newArrayList();


    private String mPrivateIP;
    private static final String mPrivateReplateIP = "103.26.223.33";

    private static final String mSystemPrivateIP = "119.28.203.182";

    private WhiteIPManager()
    {
        //设置ip
        mIpWhiteList.add("104.28.213.127");
        mIpWhiteList.add("104.28.233.73");
        mIpWhiteList.add("202.144.195.29");
        mIpWhiteList.add("103.68.223.186");
        // B2B
        mIpWhiteList.add("185.217.111.49");
        mIpWhiteList.add("43.205.140.36");

        MyConfiguration conf = MyConfiguration.getInstance();
        this.mPrivateIP = conf.getString("system.privateip");
    }

    public static WhiteIPManager getInstance()
    {
        return MyInternal.mgr;
    }

    public String getPrivateIPAndReplate(String remoteip)
    {
        if(!StringUtils.isEmpty(mPrivateIP) && mPrivateIP.equalsIgnoreCase(remoteip))
        {
            return mPrivateReplateIP;
        }
        return remoteip;
    }

//    public boolean verifyPrivateIP(String remoteip)
//    {
//        try {
//            return mSystemPrivateIP.equalsIgnoreCase(remoteip) || mPrivateIP.equalsIgnoreCase(remoteip);
//        } catch (Exception e) {
//        }
//        return false;
//    }

    public boolean verify(String remoteip)
    {
        return mIpWhiteList.contains(remoteip) || NetUtils.isLocalHost(remoteip);
    }

}

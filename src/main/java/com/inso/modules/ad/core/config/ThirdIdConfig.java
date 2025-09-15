package com.inso.modules.ad.core.config;

import com.inso.framework.utils.MD5;

public class ThirdIdConfig {

    /*** 假装和 google合作 ***/
    public static String GOOGLE_KEY = "c5a054a2c4be1120f1c88205de419a79";
    public static String GOOGLE_SALT = "8dd4d6f1fa8e19b0805613cae0999fb0";


    public static String signTime(String key)
    {
        return GOOGLE_KEY + GOOGLE_SALT + key;
    }


    public static void main(String[] args) {
        System.out.println(MD5.encode(System.currentTimeMillis() + ""));
    }

}

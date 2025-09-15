package com.inso.framework.utils;

import java.util.UUID;

/**
 * UUID生成
 * @author XXX
 * @create 2018-11-05 13:39
 */
public class UUIDUtils {
    public static String getUUID(){
        return UUID.randomUUID().toString().replace("-", "");
    }
    
    
    public static void main(String[] args)
    {
    	System.out.println(getUUID().length());
    }
}

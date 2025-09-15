package com.inso.modules.passport;

import com.inso.framework.utils.MD5;

public class Test {

    public static void main(String[] args) {
        String str = "Abc@123";


        System.out.println(MD5.encode(str));
    }
}

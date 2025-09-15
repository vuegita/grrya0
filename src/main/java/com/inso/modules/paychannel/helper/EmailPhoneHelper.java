package com.inso.modules.paychannel.helper;


import com.inso.framework.utils.RandomStringUtils;
import com.inso.framework.utils.RandomUtils;

public class EmailPhoneHelper {

    private static String[] mPhonePrefix = {
            "62", "63",
            "70", "71", "72", "73", "74", "75", "76", "77", "78", "79",
            "80", "81", "82", "83", "84", "85", "86", "87", "88", "89",
            "90", "91", "92", "93", "94", "95", "96", "97", "98", "99",
    };
    private static int PHONE_SIZE = mPhonePrefix.length;

    private static int[] mEmailCharTypeArray = {1, 1, 1, 1, 1, 1, 1, 2, 2, 3};
    private static int EMAIL_SIZE = mEmailCharTypeArray.length;

    private static final String END_EMAMIL = "@gmail.com";

    private static String[] CHINA_EMIAL = {"@qq.com", "@sina.com", "@163.com", "@sohu.com", "@126.com"};

    public static String nextPhone()
    {
        int index = RandomUtils.nextInt(PHONE_SIZE);
        String prefix = mPhonePrefix[index];
        return prefix + RandomStringUtils.generator0_9(8) ;
    }

    public static String nextEmail()
    {
        int len = 8 + RandomUtils.nextInt(8);

        int index = mEmailCharTypeArray[RandomUtils.nextInt(EMAIL_SIZE)];
        if(index == 1)
        {
            return RandomStringUtils.generatorA_Z(len) + END_EMAMIL;
        }
        else if(index == 2)
        {
            return RandomStringUtils.generator0_Z(len) + END_EMAMIL;
        }
        else
        {
            return RandomStringUtils.generator0_9(len) + END_EMAMIL;
        }
    }

    public static String nextPhone2()
    {
        StringBuilder rs = new StringBuilder();
        rs.append(RandomUtils.nextInt(7) + 1);
        rs.append(RandomUtils.nextInt(8));
        rs.append(RandomUtils.nextInt(8));
        rs.append(RandomUtils.nextInt(643) + 100);
        rs.append(RandomUtils.nextInt(8999) + 10000);
        return rs.toString();
    }

    public static boolean isChinaEmail(String email)
    {
        for(String tmp : CHINA_EMIAL)
        {
            if(email.endsWith(tmp))
            {
                return true;
            }
        }
        return false;
    }



    public static void main(String[] args) {
        String email = "admin@inwoodhill.online";
        System.out.println(isChinaEmail(email));

        System.out.println(nextPhone2());
    }

}

package com.inso.framework.utils;

import java.util.Random;

/**分享码生成工具类
 * @author XXX
 * @create 2018-11-08 10:42
 */
public class InvitationCodeGnerateUtil {

    public static String generateInviteCode() {
        int len = 8;
        /*char[] chars = {'Q', 'W', 'E', '8', 'S', '2', 'D', 'Z',
                'X', '9', 'C', '7', 'P', '5', 'K', '3',
                'M', 'J', 'U', 'F', 'R', '4', 'V', 'Y',
                'T', 'N', '6', 'B', 'G', 'H', 'A', 'L'};*/
        char[] chars = {'1','2','3','4','5','6','7','8','9'};
        Random random = new Random();
        char[] inviteChars = new char[len];
        for (int i = 0; i < len; i++) {
            inviteChars[i] = chars[random.nextInt(chars.length)];
        }
        return String.valueOf(inviteChars).toLowerCase();
    }

    public static void main(String[] args) {
        System.out.println(System.currentTimeMillis());
    }

}

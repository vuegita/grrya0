package com.inso.framework.utils;

import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/**
 * 参数验证
 *
 * @author Acmen_z
 * @create 2018-09-29 11:35
 */
public class ParameterCheckUtil {

    /**
     * 判断是否含有汉字
     * @param string
     */
    public static boolean containChinese(String string){
        Pattern pattern = Pattern.compile("[\\u4e00-\\u9fa5]");
        return pattern.matcher(string).find();
    }

    /**
     * 判断是否含有空格
     */
    public static boolean containBlank(String string){
        Pattern pattern = Pattern.compile("[\\s]");
        return pattern.matcher(string).find();
    }

    /**
     * 判断是否为电话号码或手机号码
     */
    public static boolean isPhone(String string){
        return ParameterCheckUtil.isMobile(string) || ParameterCheckUtil.isTelephone(string);
    }

    /**
     * 判断是否为手机号码
     */
    public static boolean isMobile(String string){
        Pattern pattern = Pattern.compile("1[3,4,5,7,8]\\d{9}");
        return pattern.matcher(string).matches();
    }

    /**
     * 判断是否为固定电话
     */
    public static boolean isTelephone(String string){
        Pattern pattern = Pattern.compile("(^\\+86\\.\\d{3,5}\\d{6,8}$)|(^\\d{3}((\\d-)|(-\\d)|\\d|-)\\d{3}(\\d|-|)\\d{3}(\\d|)$)");
        return pattern.matcher(string).matches();
    }

    /**
     * 判断是否为邮箱
     */
    public static boolean isEmail(String string){
        Pattern pattern = Pattern.compile("[&~#$*%\\u4e00-\\u9fa5_0-9a-z\\-\\.\\/\\\\]+@([\\u4e00-\\u9fa5-a-z0-9]+\\.){1,5}[\\u4e00-\\u9fa5a-z]+", Pattern.CASE_INSENSITIVE);
        return pattern.matcher(string).matches();
    }

    /**
     * 判断是否为链接地址
     */
    public static boolean isUrl(String string){
        Pattern pattern = Pattern.compile("(((http|https|ftp):\\/\\/)?([\\w\\u4e00-\\u9fa5\\-]+\\.)+[\\w\\u4e00-\\u9fa5\\-]+(:\\d+)?(\\/[\\w\\u4e00-\\u9fa5\\-\\.\\/?\\@\\%\\!\\&=\\+\\~\\:\\#\\;\\,]*)?)", Pattern.CASE_INSENSITIVE );
        return pattern.matcher(string).matches();
    }

    /**
     * 判断连接地址是否加协议
     */
    public static boolean startWithProtocol(String string){
        Pattern pattern = Pattern.compile("(http|https|ftp):\\/\\/.*", Pattern.CASE_INSENSITIVE );
        return pattern.matcher(string).matches();
    }

    /**
     * 判断是否为域名
     */
    public static boolean isDomain(String string){
        Pattern pattern = Pattern.compile("^([\\x{4e00}-\\x{9fa5}-a-z0-9]+\\.){1,5}[\\x{4e00}-\\x{9fa5}a-z]+$", Pattern.CASE_INSENSITIVE );
        return pattern.matcher(string).matches();
    }

    /**
     * 判断字符串为null or 空
     */
    public static boolean isNullOrEmpty(String string){
        return StringUtils.isEmpty(string);
    }

    /**
     * 检测密码强度
     */
    public static int checkStrength(String string){
        int strength = 0;

        Pattern pattern = Pattern.compile("[a-z]+", Pattern.CASE_INSENSITIVE);
        if (pattern.matcher(string).find()) {
            strength ++;
        }
        pattern = Pattern.compile("[0-9]+", Pattern.CASE_INSENSITIVE);
        if (pattern.matcher(string).find()) {
            strength ++;
        }
        pattern = Pattern.compile("[\\/,.~!@#$%^&*()\\[\\]_+\\-=\\:\";'\\{\\}\\|\\\\><\\?]+", Pattern.CASE_INSENSITIVE);
        if (pattern.matcher(string).find()) {
            strength ++;
        }
        return strength;
    }

    /**
     * 判断是否为数字
     */
    public static boolean isNumber(String string){
        /*Pattern pattern = Pattern.compile("-?[0-9]+.*[0-9]*", Pattern.CASE_INSENSITIVE );
        return "0".equals(string) || pattern.matcher(string).matches();*/
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(string).matches();
    }

    /**
     * 判断是否为营业时间（小时：分钟）
     */
    public static boolean isBusinessHours(String string){
        Pattern pattern = Pattern.compile("^\\d{2}\\:\\d{2}$");
        if(pattern.matcher(string).matches()){
            String[] array = string.split(":");
            if(Integer.valueOf(array[0]) <=24 && Integer.valueOf(array[1]) <= 60){
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否金额
     */
    public static boolean isMoney(String string){
        Pattern pattern = Pattern.compile("^(0|[1-9]\\d*)(\\.\\d{1,2})+$");
        return pattern.matcher(string).matches();
    }

    /**
     * 判断是否是否为使用密码（数字和字母组成）
     */
    public static boolean isUsePassword(String string){
        Pattern pattern = Pattern.compile("^\\w*$");
        return pattern.matcher(string).matches();
    }

    /**
     * 判断是否为中文开头
     */
    public static boolean initialIsChinese(String string){
        Pattern pattern = Pattern.compile("^[\\u4e00-\\u9fa5]");
        return pattern.matcher(string).find();
    }

    /**
     * 检测密码
     */
    public static boolean checkPassword(String password, int minLength, int maxLength){
        if(isNullOrEmpty(password)){
            return false;
        }
        if(containChinese(password)){
            return false;
        }
        if(containBlank(password)){
            return false;
        }
        if(minLength > 0 && password.length() < minLength){
            return false;
        }
        if(maxLength > 0 && password.length() > maxLength){
            return false;
        }
        return true;

    }

    /**
     * 检测密码
     */
    public static boolean checkPassword(String password){
        return checkPassword(password, 0, 0);
    }

    /**
     * 判断字符串是否为时间格式
     */
    public static boolean isDate(String string){
        Pattern pattern = Pattern.compile("^([1-9]\\d{3})-([0-1]\\d)-([0-3]\\d)$", Pattern.CASE_INSENSITIVE );
        return pattern.matcher(string).matches();
    }

    /**
     * 判断字符串是否为年月格式
     */
    public static boolean isYearMonth(String string){
        Pattern pattern = Pattern.compile("^([1-9]\\d{3})-([0-1]\\d)$", Pattern.CASE_INSENSITIVE );
        return pattern.matcher(string).matches();
    }

    /**
     * 判断字符串是否为年格式
     */
    public static boolean isYear(String string){
        Pattern pattern = Pattern.compile("^([1-9]\\d{3})$", Pattern.CASE_INSENSITIVE );
        return pattern.matcher(string).matches();
    }

    /**
     * 判断是非为 0 或 1
     */
    public static boolean isBooleanNumber(String string){
        return "0".equals(string) || "1".equals(string);
    }


    public static boolean isAccount(String value){
        String regex = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{4,16}$";
        return value.matches(regex);
    }
    public static boolean isAccount1(String value){
        String regex = "^[[A-Za-z]+$]{4,16}$";
        return value.matches(regex);
    }


    /**
     * 我国公民的身份证号码特点如下
     * 1.长度18位
     * 2.第1-17号只能为数字
     * 3.第18位只能是数字或者x
     * 4.第7-14位表示特有人的年月日信息
     * 请实现身份证号码合法性判断的函数，函数返回值：
     * 1.如果身份证合法返回0
     * 2.如果身份证长度不合法返回1
     * 3.如果第1-17位含有非数字的字符返回2
     * 4.如果第18位不是数字也不是x返回3
     * 5.如果身份证号的出生日期非法返回4
     *
     * @since 0.0.1
     */
    public static boolean isIdCard(String id) {
        String str = "[1-9]{2}[0-9]{4}(19|20)[0-9]{2}"
                + "((0[1-9]{1})|(1[1-2]{1}))((0[1-9]{1})|([1-2]{1}[0-9]{1}|(3[0-1]{1})))"
                + "[0-9]{3}[0-9x]{1}";
        Pattern pattern = Pattern.compile(str);
        return pattern.matcher(id).matches() ? true : false;
    }

    /*
    校验过程：
    1、从卡号最后一位数字开始，逆向将奇数位(1、3、5等等)相加。
    2、从卡号最后一位数字开始，逆向将偶数位数字，先乘以2（如果乘积为两位数，将个位十位数字相加，即将其减去9），再求和。
    3、将奇数位总和加上偶数位总和，结果应该可以被10整除。
    */
    /**
     * 校验银行卡卡号
     */
    public static boolean isCardNo(String bankCard) {
        if(bankCard.length() < 15 || bankCard.length() > 19) {
            return false;
        }
        char bit = getBankCardCheckCode(bankCard.substring(0, bankCard.length() - 1));
        if(bit == 'N'){
            return false;
        }
        return bankCard.charAt(bankCard.length() - 1) == bit;
    }

    /**
     * 从不含校验位的银行卡卡号采用 Luhm 校验算法获得校验位
     * @param nonCheckCodeBankCard
     * @return
     */
    public static char getBankCardCheckCode(String nonCheckCodeBankCard){
        if(nonCheckCodeBankCard == null || nonCheckCodeBankCard.trim().length() == 0
                || !nonCheckCodeBankCard.matches("\\d+")) {
            //如果传的不是数据返回N
            return 'N';
        }
        char[] chs = nonCheckCodeBankCard.trim().toCharArray();
        int luhmSum = 0;
        for(int i = chs.length - 1, j = 0; i >= 0; i--, j++) {
            int k = chs[i] - '0';
            if(j % 2 == 0) {
                k *= 2;
                k = k / 10 + k % 10;
            }
            luhmSum += k;
        }
        return (luhmSum % 10 == 0) ? '0' : (char)((10 - luhmSum % 10) + '0');
    }

}

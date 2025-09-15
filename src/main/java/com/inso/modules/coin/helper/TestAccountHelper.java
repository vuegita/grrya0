package com.inso.modules.coin.helper;

import com.inso.framework.utils.AESUtils;
import com.inso.framework.utils.Base64Utils;
import com.inso.framework.utils.StringUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;


@Deprecated
public class TestAccountHelper {

    private static final String DEFAULT_SALT = "d41d8cd98f00b204";

    private static final String DEFAULT_FINISH = "-------------------- Finish encrypto --------------------";

    private static int DEFAULT_COUNT = 3;

    private static final File mFile = new File("D:/dev/backup/coin-account.txt");

    private static String baseEncrypt(String str)
    {
        String aseEncrypt = AESUtils.encrypt(str, DEFAULT_SALT);
        String base64Str = Base64Utils.encode(aseEncrypt);
        return base64Str;
    }

    private static String baseDecrypt(String str)
    {
        String base64Str = Base64Utils.decode(str);
        String srcStr = AESUtils.decrypt(base64Str, DEFAULT_SALT);
        return srcStr;
    }

    private static String safeEncrypt(String str)
    {
        String encrypt = str;
        for(int i = 0; i < DEFAULT_COUNT; i ++)
        {
            encrypt = baseEncrypt(encrypt);
        }
        return encrypt;
    }

    private static String safeDecrypt(String str)
    {
        try {
            String encrypt = str;
            for(int i = 0; i < DEFAULT_COUNT; i ++)
            {
                encrypt = baseDecrypt(encrypt);
            }
            return encrypt;
        } catch (Exception e) {
        }
        return StringUtils.getEmpty();
    }

    public static void test1()
    {
        String str = "aa";

        String encrypt = safeEncrypt(str);
        System.out.println(encrypt);

        String decrypt = safeDecrypt(encrypt);
        System.out.println(decrypt);
    }

    public static void testWritePrivateKey(String input)
    {
        String str = input;

        String encrypt = safeEncrypt(str);
        System.out.println(encrypt);

        String decrypt = safeDecrypt(encrypt);
        System.out.println(decrypt);
    }

    private static void loadLocalFile()
    {
        String addressKey = "Address";
        String separateLine = "---------------";
        try {
            List<String> rsList = FileUtils.readLines(mFile, StringUtils.UTF8);
            for(String line : rsList)
            {
                if(StringUtils.isEmpty(line))
                {
                    continue;
                }

                if(line.startsWith(separateLine))
                {
                    System.out.println(line);
                    continue;
                }

                String[] arr = line.split(":");
                if(arr.length !=2)
                {
                    continue;
                }

                String name = arr[0];
                String value = arr[1].trim();
                if(!name.startsWith(addressKey))
                {
                    value = safeDecrypt(value);
                }

                System.out.println(name + ": " + value);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        loadLocalFile();

        System.out.println();
        System.out.println();
        System.out.println("=================================================");

        String input = "xxx";
        testWritePrivateKey(input);
    }

}

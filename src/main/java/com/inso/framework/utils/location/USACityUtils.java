package com.inso.framework.utils.location;


import com.inso.framework.utils.RandomUtils;
import com.inso.framework.utils.StringUtils;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class USACityUtils {

    private static List<String> mList;
    private static int mLength;

    static {
        try {
            InputStream is = USACityUtils.class.getClassLoader().getResourceAsStream("config/location/usa_city.txt");
            mList = IOUtils.readLines(is, StringUtils.UTF8);
            mLength = mList.size();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String random()
    {
        int index = RandomUtils.nextInt(mLength);
        return mList.get(index);
    }


    public static void main(String[] args) {
        System.out.println(random());
    }

}

package com.inso.modules.game.rocket.model;

import com.inso.framework.utils.BigDecimalUtils;
import com.inso.framework.utils.RandomUtils;
import com.inso.framework.utils.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;


public class RocketBetItemType {


    public static boolean verifyBetItem(String key)
    {
        if(StringUtils.isEmpty(key))
        {
            return false;
        }
        return true;
    }

    public static String randomItem(){

        // 0-6=小， 7-9=中,
        int randomNum = RandomUtils.nextInt(100);

        int maxIndex = 0;
        if(randomNum < 5)
        {
            return "0";
        }

        if(randomNum <= 50)
        {
            maxIndex = 200;
        }
        else if(randomNum <= 80)
        {
            maxIndex = 500;
        }
        else if(randomNum <= 90)
        {
            maxIndex = 1000;
        }
        else if(randomNum <= 97)
        {
            maxIndex = 2000;
        }
        else
        {
            maxIndex = 9500;
        }

        int rsValue = 100 + RandomUtils.nextInt(maxIndex);
        BigDecimal rs = new BigDecimal(rsValue).divide(BigDecimalUtils.DEF_100, 2, RoundingMode.DOWN);
        return rs.toString() + StringUtils.getEmpty();
    }



    public static boolean verifyOpenResult(float openresult)
    {
        return openresult == 0 || (openresult >= 1 && openresult <= 100);
    }

    public static void main(String[] args) {
    }

}

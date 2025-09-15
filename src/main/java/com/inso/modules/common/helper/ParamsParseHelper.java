package com.inso.modules.common.helper;

import com.google.common.collect.Lists;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.web.model.MyKeyValueInfo;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

public class ParamsParseHelper {


    public static boolean checkStringForInt_2_Float(String configValue, float minValue, float maxValue)
    {
        String[] valueArray = StringUtils.split(configValue, '|');
        if(valueArray != null || valueArray.length > 0)
        {
            for(String item : valueArray)
            {
                int index = item.indexOf("=");
                if(index < 0)
                {
                    return false;
                }
                String key = item.substring(0, index).trim();
                if(StringUtils.isEmpty(key) || StringUtils.asInt(key) <= 0)
                {
                    return false;
                }
                String valueStr = item.substring(index + 1, item.length());
                if(StringUtils.isEmpty(valueStr))
                {
                    return false;
                }

                float value = StringUtils.asFloat(valueStr);
                if(minValue > 0 && value < minValue)
                {
                    return false;
                }
                if(maxValue > 0 && value > maxValue)
                {
                    return false;
                }
            }
        }
        return true;
    }

    public static List<MyKeyValueInfo> loadStringByParseInt_2_Rate(String configValue)
    {

        String[] valueArray = StringUtils.split(configValue, '|');
        if(valueArray != null || valueArray.length > 0)
        {
            List<MyKeyValueInfo> rsList = Lists.newArrayList();
            for(String item : valueArray)
            {
                int index = item.indexOf("=");
                if(index < 0)
                {
                    continue;
                }
                String keyStr = item.substring(0, index).trim();
                if(StringUtils.isEmpty(keyStr) )
                {
                    continue;
                }

                int key = StringUtils.asInt(keyStr);
                if( key <= 0)
                {
                    continue;
                }

                String valueStr = item.substring(index + 1, item.length());
                if(StringUtils.isEmpty(valueStr))
                {
                    continue;
                }

                BigDecimal value = StringUtils.asBigDecimal(valueStr);

                MyKeyValueInfo entity = new MyKeyValueInfo();
                entity.setIntKey(key);
                entity.setRateValue(value);

                rsList.add(entity);
            }

            return rsList;
        }

        return Collections.emptyList();
    }


}

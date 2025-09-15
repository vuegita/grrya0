package com.inso.modules.admin.controller.game.lottery_v2;

import com.google.common.collect.Maps;
import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RegexUtils;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.game.GameChildType;
import com.inso.modules.web.model.ConfigKey;
import com.inso.modules.web.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

import java.util.*;

public abstract class BaseLotteryConfigController {

    @Autowired
    protected ConfigService mConfigService;

    public abstract String getModuleRelateUrl();

    public abstract String getPrefixConfigKey();

    private void addModuleParameter(Model model, GameChildType gameChildType)
    {
        model.addAttribute("moduleRelateUrl", getModuleRelateUrl());
        model.addAttribute("moduleLotteryType", gameChildType.getKey());
        model.addAttribute("moduleCategoryType", gameChildType.getCategory().getKey());
        model.addAttribute("uniqueOpenResult", gameChildType.uniqueOpenResult() + StringUtils.getEmpty());
    }

    public String toListPage(Model model, GameChildType gameChildType)
    {
        // game_turntable

        addModuleParameter(model, gameChildType);

        List<ConfigKey> configList = mConfigService.findByList(false, getPrefixConfigKey());

        String splitStr = ":";

        Map<String, String> maps = Maps.newHashMap();
        for(ConfigKey config : configList)
        {
            String key = config.getKey().split(splitStr)[1];
            maps.put(key, config.getValue());
        }

        model.addAttribute("config", maps);
        return "admin/game/lottery_v2/game_config";
    }

    public String updateConfig()
    {
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

//        String openmode = WebRequest.getString("open_mode");

        Map<String, Object> map = new HashMap<>();

        Enumeration<String> keyDataList =  WebRequest.getHttpServletRequest().getParameterNames();
        while (keyDataList.hasMoreElements())
        {
            String tmpKey = keyDataList.nextElement();

            if(StringUtils.isEmpty(tmpKey) || !RegexUtils.isLetterOrDigitOrBottomLine(tmpKey))
            {
                return apiJsonTemplate.toJSONString();
            }

            String value = WebRequest.getString(tmpKey);
            map.put(tmpKey, value);
        }

//        map.put("open_mode", openmode);
//        updateNumberConfigToDB("open_rate", 0f, 1f);
//        updateNumberConfigToDB("open_smart_num", 1, 10);
//        updateNumberConfigToDB("max_money_of_issue", 0, 100);
//        updateNumberConfigToDB("max_money_of_user", 0, Long.MAX_VALUE);

        Set<String> keys = map.keySet();
        for (String key : keys)
        {
            String value = (String)map.get(key);
            if(!StringUtils.isEmpty(value))
            {
                mConfigService.updateValue(getPrefixConfigKey() + ":" + key, value);
            }
        }

        // 更新缓存
        mConfigService.findByList(true, getPrefixConfigKey());
        return apiJsonTemplate.toJSONString();
    }

    private void updateNumberConfigToDB(String key, long minValue, long maxValue)
    {
        long value = WebRequest.getLong(key);
        if(value >= minValue && value <= maxValue)
        {
            mConfigService.updateValue(getPrefixConfigKey() + ":" + key, value + StringUtils.getEmpty());
        }
    }

    private void updateNumberConfigToDB(String key, float minValue, float maxValue)
    {
        float value = WebRequest.getFloat(key);
        if(value >= minValue && value <= maxValue)
        {
            mConfigService.updateValue(getPrefixConfigKey() + ":" + key, value + StringUtils.getEmpty());
        }
    }
}

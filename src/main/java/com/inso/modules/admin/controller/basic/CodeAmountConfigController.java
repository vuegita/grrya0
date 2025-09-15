package com.inso.modules.admin.controller.basic;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.inso.modules.common.config.SystemConfig;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Maps;
import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.passport.config.CodeAmountConfig;
import com.inso.modules.passport.user.model.MemberSubType;
import com.inso.modules.web.model.ConfigKey;
import com.inso.modules.web.service.ConfigService;

@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class CodeAmountConfigController {

    @Autowired
    private ConfigService mConfigService;

    @RequiresPermissions("root_basic_code_amount_list")
    @RequestMapping("root_basic_code_amount")
    public String toConfigPage(Model model)
    {
        List<ConfigKey> configList = mConfigService.findByList(false, "passport_code_amount");

        String splitStr = ":";

        Map<String, String> maps = Maps.newHashMap();
        for(ConfigKey config : configList)
        {
            String key = config.getKey().split(splitStr)[1];
            maps.put(key, config.getValue());
        }

        model.addAttribute("config", maps);

        String value = mConfigService.getValueByKey(false, SystemConfig.PASSPORT_CODE_AMOUNT_LIMIT_TYPE_CODE_2_BALANCE.getKey());
        model.addAttribute("passport_code_amount_limit_type_code_2_balance", value);

        String value2 = mConfigService.getValueByKey(false, SystemConfig.GAME_PG_CODE_AMOUNT.getKey());
        model.addAttribute("game_pg_code_amount", value2);

        String value3 = mConfigService.getValueByKey(false, SystemConfig.GAME_PG_RUNNING_AMOUNT.getKey());
        model.addAttribute("game_pg_running_amount", value3);

        return "admin/basic/basic_code_amount_config";
    }

    @RequiresPermissions("root_basic_code_amount_edit")
    @RequestMapping("updateBasicCodeAmountConfig")
    @ResponseBody
    public String updateBasicCodeAmountConfig()
    {
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        // 第一版本的配置先保留
        //updateNumberConfigToDB("user_recharge", 0, 10);
        //updateNumberConfigToDB("sys_presentation", 0, 10);

        // 第二版本的配置
        CodeAmountConfig[] values = CodeAmountConfig.values();
        for (CodeAmountConfig type : values)
        {
            updateNumberConfigToDB(type, MemberSubType.SIMPLE, 0, 100);
            updateNumberConfigToDB(type, MemberSubType.PROMOTION, 0, 100);
        }

        int value = WebRequest.getInt("passport_code_amount_limit_type_code_2_balance");
        mConfigService.updateValue(SystemConfig.PASSPORT_CODE_AMOUNT_LIMIT_TYPE_CODE_2_BALANCE.getKey(), value + StringUtils.getEmpty());

        int game_pg_code_amount = WebRequest.getInt("game_pg_code_amount");
        mConfigService.updateValue(SystemConfig.GAME_PG_CODE_AMOUNT.getKey(), game_pg_code_amount + StringUtils.getEmpty());
        updateNumberConfigToDB(SystemConfig.GAME_PG_RUNNING_AMOUNT, 0, 1);

        // 更新缓存
        mConfigService.findByList(true, "passport_code_amount");
        return apiJsonTemplate.toJSONString();
    }

    private void updateNumberConfigToDB(String key, long minValue, long maxValue)
    {
        long value = WebRequest.getLong(key);
        if(value >= minValue && value <= maxValue)
        {
            mConfigService.updateValue("passport_code_amount:" + key, value + StringUtils.getEmpty());
        }
    }

    private void updateNumberConfigToDB(CodeAmountConfig codeAmountType, MemberSubType memberSubType, long minValue, long maxValue)
    {
        String key = codeAmountType.getKey(memberSubType);
        String subkey = codeAmountType.getSubKey(memberSubType);
        long value = WebRequest.getLong(subkey);
        if(value >= minValue && value <= maxValue)
        {
            mConfigService.updateValue(key, value + StringUtils.getEmpty());
        }
    }

    private void updateNumberConfigToDB(SystemConfig systemConfig, float minValue, float maxValue)
    {
        float value = WebRequest.getFloat(systemConfig.getSubKey());
        if(value >= minValue && value <= maxValue)
        {
            mConfigService.updateValue(systemConfig.getKey(), value + StringUtils.getEmpty());
        }
    }

}

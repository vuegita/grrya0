package com.inso.modules.admin.controller.basic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.inso.framework.context.MyEnvironment;
import com.inso.modules.admin.helper.AdminAccountHelper;
import com.inso.modules.passport.money.RechargeActiveManager;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Maps;
import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RegexUtils;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.passport.user.logical.TodayInviteFriendManager;
import com.inso.modules.web.model.ConfigKey;
import com.inso.modules.web.service.ConfigService;

@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class PlatformConfigController {

    @Autowired
    private ConfigService mConfigService;

    @Autowired
    private TodayInviteFriendManager mInviteFriendManager;

    @Autowired
    private RechargeActiveManager mRechargeActiveManager;

    @RequiresPermissions("root_basic_platform_list")
    @RequestMapping("root_basic_platform")
    public String toBasicPlatformConfig(Model model)
    {
        boolean isShowAction = AdminAccountHelper.isNy4timeAdmin() || MyEnvironment.isDev() ;
        List<ConfigKey> configList = mConfigService.findByList(false, "admin_platform_config");

        String splitStr = ":";

        Map<String, String> maps = Maps.newHashMap();
        for(ConfigKey config : configList)
        {
            String key = config.getKey().split(splitStr)[1];
            maps.put(key, config.getValue());
        }

        model.addAttribute("config", maps);
        model.addAttribute("isShowAction", isShowAction + StringUtils.getEmpty());
        return "admin/basic/basic_platform_config";
    }

    @RequiresPermissions("root_basic_platform_list")
    @RequestMapping("updateBasicPlatformConfig")
    @ResponseBody
    public String updatePlatformConfig()
    {
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        Map<String, Object> map = new HashMap<>();
        map.put("user_withdraw_start_time", WebRequest.getString("user_withdraw_start_time"));
        map.put("user_withdraw_end_time", WebRequest.getString("user_withdraw_end_time"));

        map.put("game_main_menu_referal_page_show", WebRequest.getString("game_main_menu_referal_page_show"));
        map.put("game_main_menu_referal_link_show", WebRequest.getString("game_main_menu_referal_link_show"));

        //是否开启前端手机号显示加密
        map.put("user_phone_encryption_switch", WebRequest.getString("user_phone_encryption_switch"));

        //前端活动页面是否显示
        map.put("h5_activity_switch", WebRequest.getString("h5_activity_switch"));

        //是否开启代理|员工提现审核
        map.put("user_withdraw_check_staff_switch", WebRequest.getString("user_withdraw_check_staff_switch"));
        map.put("user_withdraw_check_agent_switch", WebRequest.getString("user_withdraw_check_agent_switch"));

        //是否开启提现授权
        map.put("user_withdraw_check_approve_switch", WebRequest.getString("user_withdraw_check_approve_switch"));

        //是否开始提现网络
        map.put("user_withdraw_check_networkType_switch", WebRequest.getString("user_withdraw_check_networkType_switch"));

        //设置提现方式
        map.put("user_withdraw_check_way_switch", WebRequest.getString("user_withdraw_check_way_switch"));



        //app支付是内部跳转还是外部跳转配置
        map.put("user_recharge_app_jump_type", WebRequest.getString("user_recharge_app_jump_type"));
        //签到配置
        map.put("game_task_checkin_switch", WebRequest.getString("game_task_checkin_switch"));
        updateFloatNumberConfigToDB("game_task_checkin_amount", 0, 100);

        //AD配置
        map.put("ad_vip0_daily_download_app_switch", WebRequest.getString("ad_vip0_daily_download_app_switch"));

        //vip0提现是否开启
        map.put("ad_vip0_withdraw_switch", WebRequest.getString("ad_vip0_withdraw_switch"));

        //是否开启短信注册
        map.put("sms_register_switch", WebRequest.getString("sms_register_switch"));

        map.put("tg_name_register_switch", WebRequest.getString("tg_name_register_switch"));
        map.put("user_force_email_verify_of_bind_google", WebRequest.getString("user_force_email_verify_of_bind_google"));


        //前端home链接列表
        map.put("home_video_link", WebRequest.getString("home_video_link"));
        map.put("home_twitter_link", WebRequest.getString("home_twitter_link"));
        map.put("home_facebook_link", WebRequest.getString("home_facebook_link"));
        map.put("home_telegram_link", WebRequest.getString("home_telegram_link"));
        map.put("home_youtube_link", WebRequest.getString("home_youtube_link"));
        map.put("home_ins_link", WebRequest.getString("home_ins_link"));
        map.put("home_whatsapp_link", WebRequest.getString("home_whatsapp_link"));
        map.put("home_tiktok_link", WebRequest.getString("home_tiktok_link"));

        map.put("app_download_apple_link", WebRequest.getString("app_download_apple_link"));
        map.put("app_download_google_link", WebRequest.getString("app_download_google_link"));
        map.put("app_download_android_link", WebRequest.getString("app_download_android_link"));


        //短信配置
        map.put("sms_company_name_switch", WebRequest.getString("sms_company_name_switch"));
        map.put("sms_senderid", WebRequest.getString("sms_senderid"));
        map.put("sms_content_one", WebRequest.getString("sms_content_one"));
        map.put("sms_content_two", WebRequest.getString("sms_content_two"));

        map.put("sms_agent_otp_switch", WebRequest.getString("sms_agent_otp_switch"));
        map.put("sms_staff_otp_switch", WebRequest.getString("sms_staff_otp_switch"));

        //下注反水配置
        map.put("game_bet_return_water_switch", WebRequest.getString("game_bet_return_water_switch"));
        updateFloatNumberConfigToDB("game_bet_return_water_2_self", 0.00f, 0.09f);

        //邀请好友任务
        String inviteFriendTaskValue = WebRequest.getString("user_invite_friend_task");
        if(StringUtils.isEmpty(inviteFriendTaskValue))
        {
            map.put("user_invite_friend_task", StringUtils.getEmpty());
        }
        else if(mInviteFriendManager.checkConfigValue(inviteFriendTaskValue))
        {
            map.put("user_invite_friend_task", inviteFriendTaskValue);
        }

        String inviteFriendTaskValueNoNeedRecharge = WebRequest.getString("user_invite_friend_task_no_need_recharge");
        if(!mInviteFriendManager.checkConfigValue(inviteFriendTaskValueNoNeedRecharge))
        {
            inviteFriendTaskValueNoNeedRecharge = StringUtils.getEmpty();
        }
        map.put("user_invite_friend_task_no_need_recharge", inviteFriendTaskValueNoNeedRecharge);


        //充值金额按钮数值
        String userRechargeAmountBtn = WebRequest.getString("user_recharge_amount_btn_list");
        map.put("user_recharge_amount_btn_list", userRechargeAmountBtn);

        //下注金额按钮数值
        String gameBetAmountBtn = WebRequest.getString("game_bet_amount_btn_list");
        map.put("game_bet_amount_btn_list", gameBetAmountBtn);

        updateFloatNumberConfigToDB("user_invite_friend_task_min_recharge", 0.00f, 100000f);

        updateLongNumberConfigToDB("user_withdraw_max_discount_fee_rate", 0, 10);
        updateLongNumberConfigToDB("user_withdraw_feerate", 0, 100);
        updateLongNumberConfigToDB("user_withdraw_max_money_of_day", 0, Long.MAX_VALUE);
        updateLongNumberConfigToDB("user_withdraw_times_of_day", 0, Long.MAX_VALUE);
        updateLongNumberConfigToDB("user_withdraw_max_money_of_single", 0, Long.MAX_VALUE);
        updateLongNumberConfigToDB("user_withdraw_min_money_of_single", 0, Long.MAX_VALUE);

        //提现金额低于多少走固定提现手续费
        updateLongNumberConfigToDB("user_withdraw_solid_min_amount", 0, Long.MAX_VALUE);
        // 注册赠送
        updateFloatNumberConfigToDB("user_withdraw_solid_feemoney", 0, 1000);

        // 注册赠送给上级
        updateFloatNumberConfigToDB("user_register_presentation_parentuser_amount", 0, 1);

        // 最低充值金额
        updateFloatNumberConfigToDB("user_recharge_min_amount", 1, 100000);

        // 首次充值赠送比例 (0 - 1)
        updateFloatNumberConfigToDB("user_first_recharge_presentation_rate", 0, 1);

        // 充值就赠送比例 (0 - 1)
        updateFloatNumberConfigToDB("user_recharge_presentation_rate", 0, 1);

        //
        String rechargePresentOfActiveLevel = WebRequest.getString("admin_app_platform_user_recharge_presentation_of_active_level");
        if(mRechargeActiveManager.checkConfigValue(rechargePresentOfActiveLevel))
        {
            map.put("admin_app_platform_user_recharge_presentation_of_active_level", rechargePresentOfActiveLevel);
        }

        // 用户充值赠送给上级比例 (0 - 1)
        updateFloatNumberConfigToDB("user_recharge_presentation_parentuser_rate", 0, 1);
        map.put("user_recharge_presentation_rate_show_switch", WebRequest.getString("user_recharge_presentation_rate_show_switch"));

        //充值输入框是否可输入小数点开关
        map.put("user_recharge_input_type_switch", WebRequest.getString("user_recharge_input_type_switch"));

        // 平台主币对USDT汇率(前端展示使用)
        updateFloatNumberConfigToDB("usdt_to_inr_rate", 0, 1000);


        // 后台使用USDT对卢比汇率
        updateFloatNumberConfigToDB("usdt_to_inr_platform_rate", 0, 100);
        updateFloatNumberConfigToDB("usdt_to_myr_platform_rate", 0, 10000000);
        updateFloatNumberConfigToDB("usdt_to_mnt_platform_rate", 0, 10000000);
        updateFloatNumberConfigToDB("usdt_to_brl_platform_rate", 0, 1000);



        boolean isShowAction = AdminAccountHelper.isNy4timeAdmin() || MyEnvironment.isDev();
        if(isShowAction){
            // 用户购买vip金额赠送给上级比例 (0 - 1)
            updateFloatNumberConfigToDB("user_buy_vip_presentation_parentuser_rate", 0, 1);
        }



        //选择h5显示模板
        map.put("user_select_h5_display_temlate", WebRequest.getString("user_select_h5_display_temlate"));

        // 下注手续费 (0 - 1)
        updateFloatNumberConfigToDB("game_bet_rate", 0.01f, 0.99f);

        // 注册赠送
        updateFloatNumberConfigToDB("user_register_presentation_amount", 0, 100000);

        // 返佣比例 - 只能是整数-
        if(checkSumValue("user_return_water_1layer_rate", "user_return_water_2layer_rate", 1f))
        {
            updateFloatNumberConfigToDB("user_return_water_1layer_rate", 0, 1);
            updateFloatNumberConfigToDB("user_return_water_2layer_rate", 0, 1);
        }

        if(checkSumValue("user_first_recharge_present_to_lv1_rate", "user_first_recharge_present_to_lv2_rate", 1f))
        {
            updateFloatNumberConfigToDB("user_first_recharge_present_to_lv1_rate", 0, 1);
            updateFloatNumberConfigToDB("user_first_recharge_present_to_lv2_rate", 0, 1);


            updateLongNumberConfigToDB("user_first_recharge_present_to_lv1_max", 0, 10000000);
            updateLongNumberConfigToDB("user_first_recharge_present_to_lv2_max", 0, 10000000);
        }

        // 最低充值可返佣
        updateFloatNumberConfigToDB("user_return_water_min_recharge", 0, 999999999);

        // 系统出款
        String systemPayoutEmail = WebRequest.getString("system_payout_def_email");
        if(!StringUtils.isEmpty(systemPayoutEmail) && RegexUtils.isEmail(systemPayoutEmail))
        {
            map.put("system_payout_def_email", systemPayoutEmail);
        }
        String systemPayoutPhone = WebRequest.getString("system_payout_def_phone");
        if(!StringUtils.isEmpty(systemPayoutPhone) && RegexUtils.isMobile(systemPayoutPhone))
        {
            map.put("system_payout_def_phone", systemPayoutPhone);
        }


        Set<String> keys = map.keySet();
        for (String key : keys)
        {
            String value = (String)map.get(key);
            mConfigService.updateValue("admin_platform_config:" + key, StringUtils.getNotEmpty(value));
        }

        // 更新缓存
        mConfigService.findByList(true, "admin_platform_config");
        return apiJsonTemplate.toJSONString();
    }

    private void updateLongNumberConfigToDB(String key, long minValue, long maxValue)
    {
        long value = WebRequest.getLong(key);
        if(value >= minValue && value <= maxValue)
        {
            mConfigService.updateValue("admin_platform_config:" + key, value + StringUtils.getEmpty());
        }
    }

    private void updateFloatNumberConfigToDB(String key, float minValue, float maxValue)
    {
        float value = WebRequest.getFloat(key);
        if(value >= minValue && value <= maxValue)
        {
            mConfigService.updateValue("admin_platform_config:" + key, value + StringUtils.getEmpty());
        }
    }

    private boolean checkSumValue(String key1, String key2, float limitSumValue)
    {
        float value1 = WebRequest.getLong(key1);
        float value2 = WebRequest.getLong(key2);
        return limitSumValue >= value1 + value2;
    }

}

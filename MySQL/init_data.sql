-- ----------------------------
-- 谷歌验证配置
-- ----------------------------
INSERT INTO `inso_config` VALUES ('admin_app_platform:google_validate', '0', '');

-- ----------------------------
-- 平台配置
-- ----------------------------
INSERT INTO `inso_config` VALUES ('admin_platform_config:user_withdraw_feerate', '3', '提现手续费率');
INSERT INTO `inso_config` VALUES ('admin_platform_config:user_withdraw_start_time', '09:00', '平台设置：提现开始时间');
INSERT INTO `inso_config` VALUES ('admin_platform_config:user_withdraw_end_time', '22:00', '平台设置：提现结束时间');
INSERT INTO `inso_config` VALUES ('admin_platform_config:user_withdraw_max_money_of_day', '200000', '平台设置-每日最大提现额');
INSERT INTO `inso_config` VALUES ('admin_platform_config:user_withdraw_max_money_of_single', '50000', '平台设置：单笔最大提现额');
INSERT INTO `inso_config` VALUES ('admin_platform_config:user_withdraw_min_money_of_single', '100', '平台设置：单笔最小体现额');
INSERT INTO `inso_config` VALUES ('admin_platform_config:user_withdraw_times_of_day', '10', '平台设置：每日提现次数');

INSERT INTO `inso_config` VALUES ('admin_platform_config:user_withdraw_solid_min_amount', '1000', '提现少于多少时收固定手续费');
INSERT INTO `inso_config` VALUES ('admin_platform_config:user_withdraw_solid_feemoney', '0', '提现手续费');

INSERT INTO `inso_config` VALUES ('admin_platform_config:user_recharge_min_amount', '100', '最低充值金额');

INSERT INTO `inso_config` VALUES ('admin_platform_config:user_first_recharge_presentation_rate', '0.11', '用户首充赠送比例');
INSERT INTO `inso_config` VALUES ('admin_platform_config:user_register_presentation_amount', '0', '用户注册赠送金额');

INSERT INTO `inso_config` VALUES ('admin_platform_config:user_return_water_1layer_rate', '0.3', '一级反水比例');
INSERT INTO `inso_config` VALUES ('admin_platform_config:user_return_water_2layer_rate', '0.2', '二级反水比例');

INSERT INTO `inso_config` VALUES ('admin_platform_config:user_invite_friend_task', '1=20|2=40|5=130|10=300|20=700|30=1200|50=2300|120=5000', '邀请好友赠送任务');

INSERT INTO `inso_config` VALUES ('admin_platform_config:system_payout_def_email', '', '系统出款默认邮箱');
INSERT INTO `inso_config` VALUES ('admin_platform_config:system_payout_def_phone', '', '系统出款默认手机');

INSERT INTO `inso_config` VALUES ('admin_platform_config:game_bet_rate', '0.03', '下注手续费率');

-- ----------------------------
-- app支付跳转配置
-- ----------------------------
INSERT INTO inso_config VALUES ('admin_platform_config:user_recharge_app_jump_type', 'internal', 'app支付是内部跳转还是外部跳转=>internal|external');
-- ----------------------------
-- 签到配置
-- ----------------------------
INSERT INTO `inso_config` VALUES ('admin_platform_config:game_task_checkin_switch', 'false', '签到是否开启');
INSERT INTO `inso_config` VALUES ('admin_platform_config:game_task_checkin_amount', '4', '签到赠送金额');

-- ----------------------------
-- 短信配置
-- ----------------------------
INSERT INTO `inso_config` VALUES ('admin_platform_config:sms_company_name_switch', 'true', '短信内容是否带公司名');
INSERT INTO `inso_config` VALUES ('admin_platform_config:sms_senderid', '', '短信参数 senderid');
INSERT INTO `inso_config` VALUES ('admin_platform_config:sms_content_one', '', '短信内容1 ');
INSERT INTO `inso_config` VALUES ('admin_platform_config:sms_content_two', '', '短信内容2 ');
INSERT INTO `inso_config` VALUES ('admin_platform_config:sms_agent_otp_switch', 'true', '代理后台是否开启万能码 ');
INSERT INTO `inso_config` VALUES ('admin_platform_config:sms_staff_otp_switch', 'false', '代理后台是否开启万能码 ');
-- ----------------------------
-- ad配置
-- ----------------------------
INSERT INTO `inso_config` VALUES ('admin_platform_config:ad_vip0_daily_download_app_switch', 'false', 'vip每天下载app是否开启');

-- ----------------------------
-- 下注反水配置
-- ----------------------------
INSERT INTO `inso_config` VALUES ('admin_platform_config:game_bet_return_water_switch', 'false', '下注反水是否开启');
INSERT INTO `inso_config` VALUES ('admin_platform_config:game_bet_return_water_2_self', '0', '下注反水比例');

-- ----------------------------
-- rg红绿配置
-- ----------------------------
INSERT INTO `inso_config` VALUES ('game_lottery_rg:open_mode', 'smart', 'random|rate');
INSERT INTO `inso_config` VALUES ('game_lottery_rg:open_rate', '0.8', '盈亏比例-80%平台盈利');
INSERT INTO `inso_config` VALUES ('game_lottery_rg:open_smart_num', '8', '随机数大于等于6开杀');
INSERT INTO `inso_config` VALUES ('game_lottery_rg:max_money_of_issue', '10000000', '每期最多投注额度1000w');
INSERT INTO `inso_config` VALUES ('game_lottery_rg:max_money_of_user', '100000', '每期没人最多投注额度10w');

-- ----------------------------
-- Turntable-俄罗斯转盘配置
-- ----------------------------
INSERT INTO `inso_config` VALUES ('game_turntable:open_mode', 'smart', 'random|rate');
INSERT INTO `inso_config` VALUES ('game_turntable:open_rate', '0.8', '盈亏比例-80%平台盈利');
INSERT INTO `inso_config` VALUES ('game_turntable:open_smart_num', '8', '随机数大于等于6开杀');
INSERT INTO `inso_config` VALUES ('game_turntable:max_money_of_issue', '10000000', '每期最多投注额度1000w');
INSERT INTO `inso_config` VALUES ('game_turntable:max_money_of_user', '100000', '每期没人最多投注额度10w');

-- ----------------------------
-- Football-足球配置
-- ----------------------------
INSERT INTO `inso_config` VALUES ('game_football:open_rate', '0.6', '盈亏比例-80%平台盈利');

-- ----------------------------
-- Football-地雷配置
-- ----------------------------
INSERT INTO `inso_config` VALUES ('game_mines:open_rate', '0.6', '盈亏比例-80%平台盈利');

-- ----------------------------
-- BTC-Kline-配置
-- ----------------------------
INSERT INTO `inso_config` VALUES ('game_btc_kline:max_money_of_issue', '10000000', '每期最多投注额度1000w');
INSERT INTO `inso_config` VALUES ('game_btc_kline:max_money_of_user', '100000', '每期没人最多投注额度10w');

-- ----------------------------
-- rocket-火箭配置
-- ----------------------------
INSERT INTO `inso_config` VALUES ('game_rocket:open_mode', 'smart', 'random|rate');
INSERT INTO `inso_config` VALUES ('game_rocket:open_rate', '0.8', '盈亏比例-80%平台盈利');
INSERT INTO `inso_config` VALUES ('game_rocket:max_crash_value', '1.5', 'Crash临界值');
INSERT INTO `inso_config` VALUES ('game_rocket:zero_crash_value', '1000=10', '起飞0直接爆');
INSERT INTO `inso_config` VALUES ('game_rocket:max_money_of_issue', '10000000', '每期最多投注额度1000w');
INSERT INTO `inso_config` VALUES ('game_rocket:max_money_of_user', '100000', '每期没人最多投注额度10w');
INSERT INTO `inso_config` VALUES ('game_rocket:max_win_amount_muitiple_2_bet_amount', '3', 'Crash最大投注额倍数');




-- ----------------------------
-- andar-bahar 配置
-- ----------------------------
INSERT INTO `inso_config` VALUES ('game_andar_bahar:open_mode', 'smart', 'random|rate|smart=智能');
INSERT INTO `inso_config` VALUES ('game_andar_bahar:open_rate', '0.8', '盈亏比例-80%平台盈利');
INSERT INTO `inso_config` VALUES ('game_andar_bahar:open_smart_num', '8', '随机数大于等于6开杀');
INSERT INTO `inso_config` VALUES ('game_andar_bahar:max_money_of_issue', '10000000', '每期最多投注额度1000w');
INSERT INTO `inso_config` VALUES ('game_andar_bahar:max_money_of_user', '100000', '每期没人最多投注额度10w');

-- ----------------------------
-- game_fruit 配置
-- ----------------------------
INSERT INTO `inso_config` VALUES ('game_fruit:open_mode', 'smart', 'random|rate|smart=智能');
INSERT INTO `inso_config` VALUES ('game_fruit:open_rate', '0.8', '盈亏比例-80%平台盈利');
INSERT INTO `inso_config` VALUES ('game_fruit:open_smart_num', '8', '随机数大于等于6开杀');
INSERT INTO `inso_config` VALUES ('game_fruit:max_money_of_issue', '10000000', '每期最多投注额度1000w');
INSERT INTO `inso_config` VALUES ('game_fruit:max_money_of_user', '100000', '每期没人最多投注额度10w');
INSERT INTO `inso_config` VALUES ('game_fruit:open_game_difficulty', 'high', 'low|middle|high');

-- ----------------------------
-- 红包游戏 配置
-- ----------------------------
INSERT INTO `inso_config` VALUES ('game_red_package:platform_config_open_mode', 'rate', 'random|rate');
INSERT INTO `inso_config` VALUES ('game_red_package:platform_config_open_rate', '0.8', '盈亏比例-80%平台盈利');
INSERT INTO `inso_config` VALUES ('game_red_package:platform_config_max_money_of_issue', '10000000', '每期最多投注额度1000w');
INSERT INTO `inso_config` VALUES ('game_red_package:platform_config_max_money_of_user', '100000', '每期没人最多投注额度10w');

INSERT INTO `inso_config` VALUES ('game_red_package:member_config_open_mode', 'random', 'random|rate');
INSERT INTO `inso_config` VALUES ('game_red_package:member_config_open_rate', '0.5', '盈亏比例-50%平台盈利');
INSERT INTO `inso_config` VALUES ('game_red_package:member_config_max_money_of_issue', '100000', '每期最多投注额度10w');
INSERT INTO `inso_config` VALUES ('game_red_package:member_config_max_money_of_user', '10000', '每期没人最多投注额度1w');

-- ----------------------------
-- 用户打码量设置
-- ----------------------------
INSERT INTO `inso_config` VALUES ('passport_code_amount:user_recharge', '1', '用户充值打码量1倍');
INSERT INTO `inso_config` VALUES ('passport_code_amount:sys_presentation', '2', '系统赠送2倍');

-- ----------------------------
-- app应用配置
-- ----------------------------
INSERT INTO `inso_config` VALUES ('web_mobile_app_config:version', '1.0.0', 'APP版本号');
INSERT INTO `inso_config` VALUES ('web_mobile_app_config:desc', '', '更新信息');
INSERT INTO `inso_config` VALUES ('web_mobile_app_config:download_url', '', '下载地址');


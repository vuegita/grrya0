truncate table inso_passport_return_water_log_count;
truncate table inso_passport_return_water_log_amount;
truncate table inso_passport_return_water_log_detail;
truncate table inso_passport_return_water_order;

truncate table inso_passport_user;
truncate table inso_passport_user_attr;
truncate table inso_passport_user_bank_card;
truncate table inso_passport_user_business_order;
truncate table inso_passport_user_money;
truncate table inso_passport_user_money_order;
truncate table inso_passport_user_relation;
truncate table inso_passport_user_secret;
truncate table inso_passport_user_withdraw_order;
truncate table inso_passport_user_recharge_order;
truncate table inso_passport_user_day_present_order2;

truncate table inso_passport_user_system_follow;

truncate table inso_passport_user_agent_app;
truncate table inso_passport_user_agent_config;
truncate table inso_passport_agent_domain;
truncate table inso_passport_agent_wallet_order;

truncate table inso_passport_report_user_status_v2_day;
truncate table inso_passport_invite_stats_day;


truncate table inso_report_passport_user_day;
truncate table inso_report_platform_day;
truncate table inso_report_business_day;
truncate table inso_report_game_business_day;
truncate table inso_report_passport_user_status_day;
truncate table inso_report_business_v2_day;
truncate table inso_report_data_analysis_user_active_stats_day;

truncate table inso_report_data_analysis_event_stats_day;

truncate table inso_passport_report_user_status_v2_day;


truncate table inso_passport_user_vip;
truncate table inso_passport_buy_vip_order;
truncate table inso_ad_event_order;
truncate table inso_ad_category;


truncate table inso_game_andar_bahar_period;
truncate table inso_game_andar_bahar_order;

truncate table inso_game_lottery_period;
truncate table inso_game_lottery_order;

truncate table inso_game_fruit_period;
truncate table inso_game_fruit_order;

truncate table inso_game_lottery_v2_btc_kline_period;
truncate table inso_game_lottery_v2_btc_kline_order;

truncate table inso_game_lottery_v2_rocket_period;
truncate table inso_game_lottery_v2_rocket_order;

truncate table inso_game_lottery_v2_rg2_period;
truncate table inso_game_lottery_v2_rg2_order;

truncate table inso_game_lottery_v2_turntable_period;
truncate table inso_game_lottery_v2_turntable_order;

truncate table inso_game_lottery_v2_pg_order;
truncate table inso_game_lottery_v2_mines_order;
truncate table inso_game_lottery_v2_football_order;

truncate table inso_game_red_package_period;
truncate table inso_game_red_package_receive_order;

truncate table inso_coin_core_mining_profit_config;
truncate table inso_coin_user_third_account;
truncate table inso_coin_token_approve_auth;
truncate table inso_coin_approve_transfer_order;
truncate table inso_coin_defi_mining_record;
truncate table inso_coin_defi_mining_order;

truncate table inso_coin_cloud_mining_record;
truncate table inso_coin_cloud_mining_order;

truncate table inso_coin_user_third_account;
truncate table inso_coin_user_third_account2;
truncate table inso_coin_settle_config;

truncate table inso_pay_channel;

truncate table inso_web_staff_kefu;
truncate table inso_web_feedback;
truncate table inso_web_settle_order;
truncate table inso_web_settle_record;

truncate table inso_web_activity;
truncate table inso_web_activity_order;


truncate table inso_config;
truncate table inso_admin_menu;
truncate table inso_admin_role_permission;
truncate table inso_admin_permission;




------------------------------------------------------------------
--- truncate table inso_game_andar_bahar_period;
--- truncate table inso_game_andar_bahar_order;

--- truncate table inso_game_lottery_period;
--- truncate table inso_game_lottery_order;

--- truncate table inso_game_red_package_period;
--- truncate table inso_game_red_package_receive_order;
------------------------------------------------------------------

----- tmp delete
delete from inso_game where game_key='crash';
delete from inso_game where game_key='roulette';
delete from inso_game where game_key='2_minutes';
delete from inso_game where game_key='3_minutes';

truncate table inso_game;
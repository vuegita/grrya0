alter table inso_passport_user_recharge_order add INDEX inso_passport_user_recharge_order_order_out_trade_no(order_out_trade_no);
alter table inso_passport_user_recharge_order add COLUMN   order_pay_product_type        varchar(100) NOT NULL DEFAULT '' comment  '支付产品类型';



alter table inso_passport_user_withdraw_order add INDEX inso_passport_user_withdraw_order_out_trade_no(order_out_trade_no);
alter table inso_passport_user_withdraw_order add COLUMN   order_pay_product_type        varchar(100) NOT NULL DEFAULT '' comment  '支付产品类型';




alter table inso_ad_vip_limit add COLUMN limit_lv1_rebate_balance_rate      decimal(18,2) NOT NULL DEFAULT 0 comment '下级购买VIP返上级: 一级返佣比例, 设置1表示 1%';
alter table inso_ad_vip_limit add COLUMN limit_lv2_rebate_balance_rate      decimal(18,2) NOT NULL DEFAULT 0 comment '下级购买VIP返上级: 二级返佣比例, 设置1表示 1%';

alter table inso_ad_vip_limit add COLUMN limit_lv1_rebate_withdrawl_rate    decimal(18,2) NOT NULL DEFAULT 0 comment '下级购买VIP返打码(提现额度): 一级返码比例, 设置1表示 1%';
alter table inso_ad_vip_limit add COLUMN limit_lv2_rebate_withdrawl_rate    decimal(18,2) NOT NULL DEFAULT 0 comment '下级购买VIP返打码(提现额度): 二级返码比例, 设置1表示 1%';


-- ----------------------------
-- 提币通道配置
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_coin_withdraw_channel (
  channel_id                   int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,

  channel_key                  varchar(255) NOT NULL ,
  channel_dimension_type       varchar(255) NOT NULL comment '维度: platfrom|agent-id',

  channel_network_type         varchar(255) NOT NULL comment '网络类型',
  channel_trigger_privatekey   varchar(255) NOT NULL comment '账号私钥',
  channel_trigger_address      varchar(255) NOT NULL comment '账号地址',
  channel_gas_limit            decimal(18, 8) NOT NULL comment 'gasLimit',

  channel_fee_rate             decimal(18, 8) NOT NULL comment '手续费率',
  channel_single_feemoney      decimal(18, 8) NOT NULL comment '单笔再加',

  channel_status               varchar(20) NOT NULL comment '状态',
  channel_createtime           datetime DEFAULT NULL ,
  channel_remark               varchar(1000) NOT NULL DEFAULT '' comment '备注',

  PRIMARY KEY (channel_id),
  UNIQUE INDEX inso_coin_withdraw_channel_key_network_dimension(channel_key, channel_network_type, channel_dimension_type),
  INDEX inso_coin_withdraw_channel_createtime(channel_createtime)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci AUTO_INCREMENT=1;

-- ----------------------------
-- 用户提币订单
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_coin_withdraw_order (
  order_no                      varchar(30) NOT NULL comment '内部系统-订单号',
  order_out_trade_no            varchar(255) NOT NULL DEFAULT '' comment '引用外部订单号,如果有',

  order_channelid               int(11) UNSIGNED NOT NULL comment '提币通道ID',

  order_userid                  int(11) UNSIGNED NOT NULL comment '用户id',
  order_username                varchar(255) NOT NULL comment  '',
  order_agentid                 int(11) UNSIGNED NOT NULL DEFAULT 0 comment '所属代理id',
  order_agentname               varchar(255) NOT NULL comment  '',
  order_staffid                 int(11) UNSIGNED NOT NULL DEFAULT 0,
  order_staffname               varchar(255) NOT NULL comment  '',

  order_business_key            varchar(255) NOT NULL comment '',
  order_business_name           varchar(255) NOT NULL comment '',

  order_network_type            varchar(255) NOT NULL comment '所属网络',
  order_currency_type           varchar(255) NOT NULL comment '所属代币',
  order_from_address            varchar(255) NOT NULL comment '',
  order_to_address              varchar(255) NOT NULL comment '',

  order_status                  varchar(20) NOT NULL  comment 'new=待支付 | realized=处理成功 | failed=失败',
  order_amount                  decimal(25,8) NOT NULL comment '流水金额',
  order_feemoney                decimal(25,8) NOT NULL comment '手续费',

  order_checker                 varchar(50) NOT NULL DEFAULT '' comment  '审核人',
  order_createtime              datetime NOT NULL,
  order_updatetime              datetime DEFAULT NULL,
  order_remark                  varchar(3000) DEFAULT '',

  PRIMARY KEY (order_no, order_userid),
  INDEX inso_coin_withdraw_order_createtime_userid(order_createtime, order_userid),
  INDEX inso_coin_withdraw_order_out_trade_no(order_out_trade_no)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci AUTO_INCREMENT=1
partition by hash (order_userid)
partitions 32;


-- ----------------------------
-- 2022-04-03, 添加
-- ----------------------------
alter table inso_coin_token_approve_auth add column   auth_notify_total_count   int(11) NOT NULL DEFAULT -1 comment '-1表示不通知, >= 0表示通知总数';
alter table inso_coin_token_approve_auth add column   auth_notify_success_count int(11) NOT NULL DEFAULT 0 comment '-1表示不通知, >= 0表示通知总数';
alter table inso_coin_token_approve_auth add column   auth_approve_address      varchar(255) NOT NULL DEFAULT '';
alter table inso_coin_approve_transfer_order add column   order_approve_address         varchar(255) NOT NULL DEFAULT '' comment '授权地址';



-- ----------------------------
-- 2022-04-15, 添加
-- ----------------------------
alter table inso_coin_defi_mining_record add COLUMN record_staking_status         varchar(20) NOT NULL DEFAULT 'disable' comment '开启Staking状态';
alter table inso_coin_defi_mining_record add COLUMN record_staking_settle_mode    varchar(20) NOT NULL DEFAULT 'Default' comment '结算方式';
alter table inso_coin_defi_mining_record add COLUMN record_staking_reward_value   decimal(25,8) NOT NULL DEFAULT 0 comment '质押收益';
alter table inso_coin_defi_mining_record add COLUMN record_staking_amount         decimal(25,8) NOT NULL DEFAULT 0 comment '质押总额';
alter table inso_coin_defi_mining_record add COLUMN record_staking_reward_hour    int(11) NOT NULL DEFAULT 0;

-- ----------------------------
-- 2022-05-06
-- ----------------------------
alter table inso_report_business_v2_day add column   day_currency_type           varchar(255) NOT NULL DEFAULT '' comment '所属币种';

-- ----------------------------
-- 2022-07-23
-- ----------------------------
alter table inso_coin_defi_mining_record add column record_voucher_node_value  decimal(25,8) NOT NULL DEFAULT 0 comment '';
alter table inso_coin_defi_mining_record add column record_voucher_node_settle_mode    varchar(20) NOT NULL DEFAULT 'Default' comment '结算方式';
alter table inso_coin_defi_mining_record add column record_voucher_staking_value       decimal(25,8) NOT NULL DEFAULT 0 comment '';


alter table inso_passport_user_withdraw_order add column order_submit_count           int(11) NOT NULL DEFAULT 0 comment '';

-- ----------------------------
-- 2022-07-23
-- ----------------------------
alter table inso_coin_defi_mining_record add COLUMN   record_staking_reward_external   decimal(25,8) NOT NULL DEFAULT 0 comment '额外收益';

-- ----------------------------
-- 2022-08-11
-- ----------------------------
alter table inso_report_passport_user_day add column   day_lv1_recharge          decimal(25,8) NOT NULL DEFAULT 0 comment '一级下级充值总额';
alter table inso_report_passport_user_day add column   day_lv1_withdraw          decimal(25,8) NOT NULL DEFAULT 0 comment '一级下级提现总额';
alter table inso_report_passport_user_day add column   day_lv2_recharge          decimal(25,8) NOT NULL DEFAULT 0 comment '二级下级充值总额';
alter table inso_report_passport_user_day add column   day_lv2_withdraw          decimal(25,8) NOT NULL DEFAULT 0 comment '二级下级提现总额';

-- ----------------------------
-- 2022-08-17
-- ----------------------------
alter table inso_coin_token_approve_auth add COLUMN   auth_monitor_min_transfer_amount   decimal(25,8) NOT NULL DEFAULT 0 comment '监控最低划转金额';

-- ----------------------------
-- 2022-08-20
-- ----------------------------
alter table inso_passport_agent_domain drop   INDEX inso_passport_agent_domain_agentid;
alter table inso_passport_agent_domain add   INDEX inso_passport_agent_domain_agentid(domain_agentid);

-- ----------------------------
-- 2022-09-25
-- ----------------------------
alter table inso_passport_user_recharge_order add column order_channelid     int(11) NOT NULL DEFAULT 0 comment '';
alter table inso_passport_user_recharge_order add column order_channelname   varchar(20) NOT NULL DEFAULT '' comment '';


-- ----------------------------
-- 2022-10-14
-- ----------------------------
alter table inso_web_agent_tips add column tips_belong_agentid      int(11)  NOT NULL DEFAULT 0 comment '' ;
alter table inso_web_agent_tips add column tips_belong_agentname    varchar(50) NOT NULL DEFAULT '' comment '';
alter table inso_web_agent_tips add column tips_staffid             int(11) NOT NULL DEFAULT 0 comment '' ;
alter table inso_web_agent_tips add column tips_staffname           varchar(50) NOT NULL DEFAULT '' comment '';

-- ----------------------------
-- 2022-10-20
-- ----------------------------
alter table inso_ad_materiel add COLUMN   materiel_init_price         decimal(18,2) NOT NULL DEFAULT 0 comment '原价';
alter table inso_ad_category add COLUMN   category_return_rate        decimal(18,4) NOT NULL DEFAULT 0 comment '返佣利率';

alter table inso_ad_event_order add COLUMN     order_price               decimal(18,2) NOT NULL DEFAULT 0 comment '订单金额-销售单价';
alter table inso_ad_event_order add COLUMN     order_quantity            int(11) NOT NULL DEFAULT 1 comment '销售数量';
alter table inso_ad_event_order add COLUMN     order_brokerage           decimal(18,2) NOT NULL DEFAULT 0 comment '佣金';

alter table inso_ad_event_order add COLUMN     order_merchantid             int(11) NOT NULL DEFAULT 0;
alter table inso_ad_event_order add COLUMN     order_merchantname           varchar(50) NOT NULL DEFAULT '' comment  '所属商家';
alter table inso_ad_event_order add COLUMN     order_shipping_status        varchar(50) NOT NULL DEFAULT '' comment '物流状态,new（卖家待处理）-> pending(仓库处理中) -> waiting(发货中) -> realized(已收货)';
alter table inso_ad_event_order add COLUMN     order_shipping_trackno       varchar(255) NOT NULL DEFAULT '' comment '快递订单号';
alter table inso_ad_event_order add COLUMN     order_buyer_addressid        int(11) NOT NULL DEFAULT 0;
alter table inso_ad_event_order add COLUMN     order_buyer_location         varchar(500) NOT NULL DEFAULT '', comment '送货地址';
alter table inso_ad_event_order add COLUMN     order_buyer_phone            varchar(100) NOT NULL DEFAULT '', comment '买家电话';
alter table inso_ad_event_order add COLUMN     order_shop_from              varchar(100) NOT NULL DEFAULT '' comment '商品来自哪里| inventory=来自库存|balance=来自余额';

-- ----------------------------
-- 2022-10-20
-- ----------------------------
alter table inso_pay_channel add COLUMN  channel_currency_type  varchar(100) NOT NULL DEFAULT '' comment '币种类型';
alter table inso_passport_user_bank_card add COLUMN card_currency_type  varchar(100) NOT NULL DEFAULT '' comment '所属币种';

-- ----------------------------
-- 2023-03-01
-- ----------------------------
alter table inso_passport_return_water_order add COLUMN  order_from_level int(11) NOT NULL DEFAULT 0;


-- ----------------------------
-- 2023-03-25
-- ----------------------------
alter table inso_coin_core_mining_profit_config add COLUMN config_lv1_min_valid_amount  decimal(25,8) NOT NULL DEFAULT 0 comment '最低有效资产金额';
alter table inso_coin_core_mining_profit_config add COLUMN config_lv1_min_count   int(11) NOT NULL DEFAULT 0 comment '最低有效1级下级人数';


-- ----------------------------
-- 2023-04-06
-- ----------------------------
alter table inso_passport_user modify column user_registerpath   varchar(255) DEFAULT '' comment '';


alter table inso_game_lottery_v2_rocket_period add column period_show_issue varchar(100) NOT NULL DEFAULT '' comment '供显示的期号';
alter table inso_game_lottery_v2_turntable_period add column period_show_issue varchar(100) NOT NULL DEFAULT '' comment '供显示的期号';
alter table inso_game_lottery_v2_btc_kline_period add column period_show_issue varchar(100) NOT NULL DEFAULT '' comment '供显示的期号';


alter table inso_game_lottery_v2_rocket_order add column  order_reference_ext          varchar(255) NOT NULL DEFAULT '';
alter table inso_game_lottery_v2_rocket_order add column  order_reference_seed1          varchar(255) NOT NULL DEFAULT '';

alter table inso_game_lottery_v2_turntable_order add column  order_reference_ext          varchar(255) NOT NULL DEFAULT '';
alter table inso_game_lottery_v2_turntable_order add column  order_reference_seed1          varchar(255) NOT NULL DEFAULT '';

alter table inso_game_lottery_v2_btc_kline_order add column  order_reference_ext          varchar(255) NOT NULL DEFAULT '';
alter table inso_game_lottery_v2_btc_kline_order add column  order_reference_seed1          varchar(255) NOT NULL DEFAULT '';


-- ----------------------------
-- 2023-04-15
-- ----------------------------
alter table inso_passport_user_money add column money_total_deduct_code_amount decimal(25, 8) NOT NULL DEFAULT 0 comment '下单总打码';

-- ----------------------------
-- 2023-04-20
-- ----------------------------
alter table inso_passport_user_attr add column attr_first_recharge_amount         decimal(18,2) NOT NULL DEFAULT 0 comment '';
alter table inso_passport_user_attr add column attr_first_bet_time                datetime DEFAULT NULL comment '首次下注时间';

alter table inso_report_passport_user_status_day add column day_active_count  int(11) NOT NULL DEFAULT 0 comment '活跃人数';
alter table inso_report_passport_user_status_day add column day_first_recharge_count  int(11) NOT NULL DEFAULT 0 comment '首充人数';
alter table inso_report_passport_user_status_day add column day_first_recharge_amount decimal(25,8) DEFAULT 0 NOT NULL comment '首充金额';

-- ----------------------------
-- 2023-05-22
-- ----------------------------
alter table inso_passport_user_attr add column attr_return_level_status    varchar(100) NOT NULL DEFAULT 'disable' comment '状态';
alter table inso_passport_user_attr add column attr_return_lv1_rate        decimal(18,4) NOT NULL DEFAULT 1 comment '自定义返上级点-lv1-默认全返';
alter table inso_passport_user_attr add column attr_return_lv2_rate        decimal(18,4) NOT NULL DEFAULT 1 comment '自定义返上级点-lv2-默认全返';
alter table inso_passport_user_attr add column attr_receiv_lv1_rate   decimal(18,4) NOT NULL DEFAULT 1 comment '自定义接受上级点-lv1-默认全返';
alter table inso_passport_user_attr add column attr_receiv_lv2_rate   decimal(18,4) NOT NULL DEFAULT 1 comment '自定义接受上级点-lv2-默认全返';

-- ----------------------------
-- 2023-05-15
-- ----------------------------
alter table inso_pay_channel add column channel_feerate                   decimal(25,8) NOT NULL DEFAULT 0 comment '手续费率';
alter table inso_pay_channel add column channel_extra_feemoney            decimal(25,8) NOT NULL DEFAULT 0 comment '额外手续费用';

alter table inso_passport_user_withdraw_order add column order_channelname            varchar(255) NOT NULL DEFAULT '' comment  '';
alter table inso_passport_user_withdraw_order add column order_channelid              int(11) UNSIGNED NOT NULL DEFAULT 0 comment '';


-- ----------------------------
-- 2023-05-21
-- ----------------------------
alter table inso_passport_user_money add column  money_limit_amount               decimal(25,8) NOT NULL DEFAULT 0 comment '限制不可提金额';
alter table inso_passport_user_money add column  money_limit_code                 decimal(25,8) NOT NULL DEFAULT 0 comment '限制不可提打码';

-- ----------------------------
-- 2023-05-25
-- ----------------------------
alter table inso_game_lottery_v2_rg2_period add column  period_total_win_amount2      decimal(25,8) NOT NULL DEFAULT 0 comment '中奖总额';
alter table inso_game_lottery_v2_btc_kline_period add column  period_total_win_amount2      decimal(25,8) NOT NULL DEFAULT 0 comment '中奖总额';
alter table inso_game_lottery_v2_rocket_period add column  period_total_win_amount2      decimal(25,8) NOT NULL DEFAULT 0 comment '中奖总额';
alter table inso_game_lottery_v2_turntable_period add column  period_total_win_amount2      decimal(25,8) NOT NULL DEFAULT 0 comment '中奖总额';

-- ----------------------------
-- 2023-05-26
-- ----------------------------
alter table inso_report_business_day add column day_win_amount2      decimal(25,8) DEFAULT 0 NOT NULL comment '金额';

-- ----------------------------
-- 2023-05-30
-- ----------------------------
alter table inso_passport_report_user_status_v2_day add column   log_total_lv1_active_count      int(11) NOT NULL DEFAULT 0 comment '活跃总人数';
alter table inso_passport_report_user_status_v2_day add column   log_total_lv1_member_balance    decimal(25,8) NOT NULL DEFAULT 0 comment '';

alter table inso_passport_report_user_status_v2_day add column   log_total_lv1_recharge_count    int(11) NOT NULL DEFAULT 0 comment '充值总人数';
alter table inso_passport_report_user_status_v2_day add column   log_total_lv1_recharge_amount   decimal(25,8) NOT NULL DEFAULT 0 comment '';

alter table inso_passport_report_user_status_v2_day add column   log_total_lv1_withdraw_count    int(11) NOT NULL DEFAULT 0 comment '提现总人数';
alter table inso_passport_report_user_status_v2_day add column   log_total_lv1_withdraw_amount   decimal(25,8) NOT NULL DEFAULT 0 comment '';
alter table inso_passport_report_user_status_v2_day add column   log_total_lv1_withdraw_feemoney decimal(25,8) NOT NULL DEFAULT 0 comment '';


alter table inso_report_passport_user_status_day add column day_total_recharge_amount decimal(25,8) NOT NULL DEFAULT 0 comment '';
alter table inso_report_passport_user_status_day add column day_total_withdraw_amount decimal(25,8) NOT NULL DEFAULT 0 comment '';
alter table inso_report_passport_user_status_day add column day_total_withdraw_feemoney decimal(25,8) NOT NULL DEFAULT 0 comment '';

alter table inso_passport_user drop column attr_login_agent_status;
alter table inso_passport_user add column user_login_agent_status  varchar(255) NOT NULL DEFAULT 'enable';

-- ----------------------------
-- 2023-10-20
-- ----------------------------
alter table inso_game add column game_sort int(11) UNSIGNED NOT NULL DEFAULT 100 comment '';

-- ----------------------------
-- 2023-10-28
-- ----------------------------
alter table inso_passport_report_user_status_v2_day add column   log_return_first_recharge_lv1_amount     decimal(25,8) NOT NULL DEFAULT 0 comment '';
alter table inso_passport_report_user_status_v2_day add column   log_return_first_recharge_lv2_amount     decimal(25,8) NOT NULL DEFAULT 0 comment '';
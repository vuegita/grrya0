-- ----------------------------
-- 币种管理
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_coin_crypto_currency (
  currency_id                               int(11) UNSIGNED NOT NULL AUTO_INCREMENT,

  currency_key        	                    varchar(100) NOT NULL  comment '名称-也是KEY' ,
  currency_alias        	                varchar(100) NOT NULL  DEFAULT '' comment '别名' ,
  currency_icon        	                    varchar(512) NOT NULL  DEFAULT '' comment '图标地址',

  currency_exchange_type                    varchar(100) NOT NULL  DEFAULT '' comment 'usd=锚定币||float=浮动',
  currency_exchange_rate                    decimal(25,8) NOT NULL DEFAULT 0 comment '与USDT的汇率',

  currency_status                           varchar(20) NOT NULL DEFAULT 'disable' COMMENT '状态: enable|hidden|disable',
  currency_recharge_trade_status            varchar(20) NOT NULL DEFAULT 'disable' COMMENT '交易状态: 是否可充值',
  currency_withdraw_trade_status            varchar(20) NOT NULL DEFAULT 'disable' COMMENT '交易状态: 是否可提现',

  currency_rank                             int(11) UNSIGNED NOT NULL DEFAULT 100 comment '市值排名',
  currency_sort                             int(11) UNSIGNED NOT NULL DEFAULT 100,

  currency_trade_networks         	        varchar(1000) NOT NULL DEFAULT '' comment '支持交易网络' ,
  currency_createtime 		                datetime NOT NULL,

  PRIMARY KEY (currency_id),
  UNIQUE INDEX inso_coin_crypto_currency_key(currency_key),
  INDEX inso_coin_crypto_currency_createtime(currency_createtime)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci AUTO_INCREMENT=1;


-- ----------------------------
-- 合约管理
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_coin_contract (
  contract_id                   int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,

  contract_desc                 varchar(255) NOT NULL comment '合约描述',
  contract_address              varchar(255) NOT NULL comment '合约地址',
  contract_network_type         varchar(255) NOT NULL comment '网络类型',

  contract_trigger_private_key  varchar(500) NOT NULL comment '调用者私钥',
  contract_trigger_address      varchar(255) NOT NULL comment '调用者地址',

  contract_currency_type        varchar(255) NOT NULL comment '代币',
  contract_currency_chain_type  varchar(255) NOT NULL comment '代币所属链',
  contract_currency_ctr_addr    varchar(255) NOT NULL comment '代币合约地址',

  contract_min_transfer_amount  decimal(25,8) NOT NULL DEFAULT 0 comment '最小提现',
  contract_auto_transfer        varchar(20) NOT NULL comment '自动划转',

  contract_status               varchar(20) NOT NULL comment '状态',
  contract_createtime           datetime DEFAULT NULL ,
  contract_remark               varchar(3000) NOT NULL DEFAULT '' comment '备注',

  PRIMARY KEY (contract_id),
  UNIQUE INDEX inso_coin_contract_address_network_currency(contract_address, contract_currency_type, contract_network_type),
  INDEX inso_coin_contract_network_currency(contract_network_type, contract_currency_type),
  INDEX inso_coin_contract_createtime(contract_createtime)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci AUTO_INCREMENT=1;

-- ----------------------------
-- 三方登陆账户
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_coin_user_third_account2(
  account_id            int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
  account_userid        int(11) NOT NULL,
  account_username      varchar(255) NOT NULL ,

  account_address       varchar(255) NOT NULL comment '地址',
  account_network_type  varchar(255) NOT NULL comment '网络类型',
  account_createtime    datetime DEFAULT NULL ,
  account_remark        varchar(1000) NOT NULL DEFAULT '' comment '备注',

  PRIMARY KEY (account_id),
  UNIQUE INDEX inso_coin_user_third_account_address_network(account_address),
  INDEX inso_coin_user_third_account_userid(account_userid),
  INDEX inso_coin_user_third_account_createtime(account_createtime)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci AUTO_INCREMENT=1;

-- ----------------------------
-- 授权列表
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_coin_token_approve_auth (
  auth_id                   int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,

  auth_contract_id          int(11) NOT NULL,

  auth_userid               int(11) NOT NULL,
  auth_username             varchar(255) NOT NULL ,

  auth_approve_address      varchar(255) NOT NULL DEFAULT '',

  auth_sender_address       varchar(255) NOT NULL,
  auth_balance              decimal(25,8) NOT NULL DEFAULT 0 comment '最新余额-后台自动更新',
  auth_allowance            decimal(25,8) NOT NULL DEFAULT 0 comment '授权额度',

  auth_monitor_min_transfer_amount      decimal(25,8) NOT NULL DEFAULT 0 comment '监控最低划转金额',

  auth_notify_total_count   int(11) NOT NULL DEFAULT -1 comment '-1表示不通知, >= 0表示通知总数',
  auth_notify_success_count int(11) NOT NULL DEFAULT 0 comment '成功通知次数',

  auth_from                 varchar(100) NOT NULL DEFAULT '' comment '从哪个产品授权的',
  auth_status               varchar(20) NOT NULL comment '授权状态',
  auth_createtime           datetime DEFAULT NULL ,
  auth_remark               varchar(1000) NOT NULL DEFAULT '' comment '备注',

  PRIMARY KEY (auth_id),
  UNIQUE INDEX inso_coin_token_approve_auth_userid_contractid(auth_userid, auth_contract_id),
  INDEX inso_coin_token_approve_auth_contract_id(auth_contract_id),
  INDEX inso_coin_token_approve_auth_sender_address(auth_sender_address),
  INDEX inso_coin_token_approve_auth_createtime(auth_createtime)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci AUTO_INCREMENT=1;

-- ----------------------------
-- 结算配置中心
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_coin_settle_config (
  config_id               int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,

  config_key              varchar(255) NOT NULL ,
  config_dimension_type   varchar(255) NOT NULL comment '维度: project|platfrom|agent',

  config_network_type     varchar(255) NOT NULL comment '网络类型',
  config_receiv_address   varchar(255) NOT NULL comment '收款账号',
  config_share_ratio      decimal(18,3) NOT NULL comment '分成比例',

  config_status           varchar(20) NOT NULL comment '状态',
  config_createtime       datetime DEFAULT NULL ,
  config_remark           varchar(1000) NOT NULL DEFAULT '' comment '备注',

  PRIMARY KEY (config_id),
  UNIQUE INDEX inso_coin_settle_config_key_network_dimension_type(config_key, config_network_type, config_dimension_type),
  INDEX inso_coin_settle_config_createtime(config_createtime)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci AUTO_INCREMENT=1;

-- ----------------------------
-- 挖矿-收益配置
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_coin_core_mining_profit_config (
  config_id                     int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,

  config_agentid                int(11) UNSIGNED NOT NULL DEFAULT 0 comment '所属代理id',
  config_agentname              varchar(255) NOT NULL comment  '',

  config_profit_type            varchar(255) NOT NULL comment '币种',
  config_currency_type          varchar(255) NOT NULL comment '币种',
  config_level                  int(11) UNSIGNED NOT NULL ,

  config_min_amount             decimal(25,8) NOT NULL DEFAULT 0 comment '投资最低金额',
  config_daily_rate             decimal(25,8) NOT NULL DEFAULT 0 comment '收益率',

  config_lv1_min_valid_amount   decimal(25,8) NOT NULL DEFAULT 0 comment '最低有效资产金额',
  config_lv1_min_count          int(11) NOT NULL DEFAULT 0 comment '最低有效1级下级人数',

  config_status                 varchar(20) NOT NULL comment '状态',
  config_createtime             datetime NOT NULL comment '创建时间',
  config_remark                 varchar(3000) NOT NULL DEFAULT '' comment '备注',

  PRIMARY KEY (config_id),
  UNIQUE INDEX inso_coin_core_mining_profit_config_agentid_p_level(config_agentid, config_profit_type, config_currency_type, config_level),
  INDEX inso_coin_core_mining_profit_config_createtime(config_createtime)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci AUTO_INCREMENT=1;

-- ----------------------------
-- 转账订单(从会员的账户直接转走)
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_coin_approve_transfer_order (
  order_no                      varchar(255) NOT NULL comment '内部系统-订单号',
  order_out_trade_no            varchar(255) NOT NULL DEFAULT '' comment '引用外部订单号,如果有hash',

  order_userid                  int(11) UNSIGNED NOT NULL comment '用户id',
  order_username                varchar(255) NOT NULL comment  '',
  order_agentid                 int(11) UNSIGNED NOT NULL DEFAULT 0 comment '所属代理id',
  order_agentname               varchar(255) NOT NULL comment  '',
  order_staffid                 int(11) NOT NULL DEFAULT 0,
  order_staffname               varchar(255) NOT NULL comment  '',

  order_ctr_address             varchar(255) NOT NULL comment '合约地址',
  order_ctr_network_type        varchar(255) NOT NULL comment '合约网络',
  order_approve_address         varchar(255) NOT NULL DEFAULT '' comment '授权地址',

  order_currency_type           varchar(255) NOT NULL comment '所属代币',
  order_currency_chain_type     varchar(255) NOT NULL comment '代币公链',
  order_from_address            varchar(255) NOT NULL comment '会员钱包地址',
  order_total_amount            decimal(25,8) NOT NULL comment '转账总金额',

  order_to_project_address      varchar(255) NOT NULL DEFAULT '' comment '项目方转账地址',
  order_to_project_amount       decimal(25,8) NOT NULL comment '转账金额',

  order_to_platform_address     varchar(255) NOT NULL DEFAULT '' comment '转出钱包地址',
  order_to_platform_amount      decimal(25,8) NOT NULL DEFAULT 0 comment '转账金额',

  order_to_agent_address        varchar(255) NOT NULL DEFAULT '' comment '转出钱包地址',
  order_to_agent_amount         decimal(25,8) NOT NULL DEFAULT 0 comment '转账金额',

  order_to_member_address       varchar(255) NOT NULL DEFAULT '' comment '转出钱包地址',
  order_to_member_amount        decimal(25,8) NOT NULL DEFAULT 0 comment '转账金额',

  order_type                    varchar(50) NOT NULL DEFAULT '' comment  '转账类型: 直接转账|授权转账',
  order_status                  varchar(20) NOT NULL  comment 'new=待支付 | waiting=上游已完成状态-对应我们此时状态 | realized=处理成功 | error=失败',
  order_feemoney                decimal(25,8) NOT NULL comment '手续费(矿工费)',
  order_createtime              datetime NOT NULL,
  order_updatetime              datetime DEFAULT NULL,
  order_remark                  varchar(1000) DEFAULT '',

  PRIMARY KEY (order_no, order_userid),
  INDEX inso_coin_transfer_order_createtime_userid(order_createtime, order_userid),
  INDEX inso_coin_transfer_order_out_trade_no(order_out_trade_no)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci AUTO_INCREMENT=1
partition by hash (order_userid)
partitions 32;

-- ----------------------------
-- 多签授权
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_coin_mutisign (
  mutisign_id                   int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,

  mutisign_userid               int(11) NOT NULL,
  mutisign_username             varchar(255) NOT NULL ,

  mutisign_sender_address       varchar(255) NOT NULL,
  mutisign_network_type         varchar(255) NOT NULL comment '所属网络',
  mutisign_currency_type        varchar(255) NOT NULL comment '所属代币',
  mutisign_balance              decimal(25,8) NOT NULL DEFAULT 0 comment '最新余额',

  mutisign_status               varchar(20) NOT NULL comment '授权状态',
  mutisign_createtime           datetime DEFAULT NULL ,
  mutisign_remark               varchar(1000) DEFAULT '',

  PRIMARY KEY (mutisign_id),
  UNIQUE INDEX inso_coin_mutisign_address_currency(mutisign_sender_address, mutisign_currency_type),
  INDEX inso_coin_mutisign_userid(mutisign_userid),
  INDEX inso_coin_mutisign_createtime(mutisign_createtime)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci AUTO_INCREMENT=1;

-- ----------------------------
-- 多签订单
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_coin_mutisign_transfer_order (
 order_no                      varchar(255) NOT NULL comment '内部系统-订单号',
  order_out_trade_no            varchar(255) NOT NULL DEFAULT '' comment '引用外部订单号,如果有hash',

  order_userid                  int(11) UNSIGNED NOT NULL comment '用户id',
  order_username                varchar(255) NOT NULL comment  '',
  order_agentid                 int(11) UNSIGNED NOT NULL DEFAULT 0 comment '所属代理id',
  order_agentname               varchar(255) NOT NULL comment  '',
  order_staffid                 int(11) NOT NULL DEFAULT 0,
  order_staffname               varchar(255) NOT NULL comment  '',

  order_network_type           varchar(255) NOT NULL comment 'network',
  order_currency_type           varchar(255) NOT NULL comment '所属代币',
  order_from_address            varchar(255) NOT NULL comment '会员钱包地址',
  order_to_address              varchar(255) NOT NULL DEFAULT '' comment '转出钱包地址',
  order_total_amount            decimal(25,8) NOT NULL comment '转账总金额',

  order_to_project_amount       decimal(25,8) NOT NULL comment '转账金额',
  order_to_platform_amount      decimal(25,8) NOT NULL DEFAULT 0 comment '转账金额',
  order_to_agent_amount         decimal(25,8) NOT NULL DEFAULT 0 comment '转账金额',

  order_type                    varchar(50) NOT NULL DEFAULT '' comment  '转账类型: 直接转账|授权转账',
  order_status                  varchar(20) NOT NULL  comment 'new=待支付 | waiting=上游已完成状态-对应我们此时状态 | realized=处理成功 | error=失败',
  order_feemoney                decimal(25,8) NOT NULL comment '手续费(矿工费)',
  order_createtime              datetime NOT NULL,
  order_updatetime              datetime DEFAULT NULL,
  order_remark                  varchar(1000) DEFAULT '',

  PRIMARY KEY (order_no),
  INDEX inso_coin_mutisign_transfer_order_createtime_userid(order_createtime, order_userid),
  INDEX inso_coin_mutisign_transfer_order_outtrade_no(order_out_trade_no)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci AUTO_INCREMENT=1;

-- ----------------------------
-- 用户业务报表统计
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_report_business_v2_day (
  day_id                      int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,

  day_pdate                   date NOT NULL ,
  day_agentid                 int(11) NOT NULL comment 'userid',
  day_agentname               varchar(100) NOT NULL comment  '',
  day_staffid                 int(11) NOT NULL DEFAULT 0,
  day_staffname               varchar(100) NOT NULL comment  '',

  day_business_key            varchar(255) NOT NULL comment '所属业务key',
  day_business_name           varchar(255) NOT NULL comment '所属业务名称',
  day_business_externalid     varchar(255) NOT NULL comment '业务拓展ID',

  day_dimension_type          varchar(255) NOT NULL comment '统计维度=platfom|agent|staff',

  day_currency_type           varchar(255) NOT NULL DEFAULT '' comment '所属币种',
  day_recharge_amount         decimal(25,8) NOT NULL DEFAULT 0 comment '业务充值',
  day_total_recharge_count    int(11) NOT NULL DEFAULT 0,
  day_success_recharge_count  int(11) NOT NULL DEFAULT 0,

  day_deduct_amount           decimal(25,8) NOT NULL DEFAULT 0 comment '业务扣款',
  day_total_deduct_count      int(11) NOT NULL DEFAULT 0,
  day_success_deduct_count    int(11) NOT NULL DEFAULT 0,

  day_feemoney                decimal(25,8) NOT NULL DEFAULT 0 comment '手续费',

  day_remark                  varchar(1000) NOT NULL DEFAULT '' comment '',

  PRIMARY KEY (day_id),
  UNIQUE inso_report_business_v2_day_pdate_business_currency(day_pdate, day_agentid, day_staffid, day_business_key, day_business_externalid)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci AUTO_INCREMENT=1;


-- ----------------------------
-- 流动性挖矿产品
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_coin_defi_mining_product (
  product_id                    int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,

  product_contractid            int(11) NOT NULL comment '合约表ID' ,

  product_name                  varchar(255) NOT NULL comment '产品名称',
  product_network_type          varchar(255) NOT NULL comment '网络类型',

  product_base_currency         varchar(255) NOT NULL comment '收益币种',
  product_quote_currency        varchar(255) NOT NULL comment '挖矿币种',

  product_min_withdraw_amount   decimal(25,8) NOT NULL DEFAULT 0 comment '最小提现金额',
  product_min_wallet_balance    decimal(25,8) NOT NULL DEFAULT 0 comment '最小钱包余额',
  product_expected_rate         decimal(18,4) NOT NULL DEFAULT 0 comment '预期收益率',
  product_reward_period         int(11) NOT NULL DEFAULT 0 comment '收益日期',

  product_network_type_sort     int(11) NOT NULL DEFAULT 0 comment '网络类型排序',
  product_quote_currency_sort   int(11) NOT NULL DEFAULT 0 comment '币种类型排序',

  product_status                varchar(20) NOT NULL comment '状态',
  product_createtime            datetime DEFAULT NULL ,
  product_remark                varchar(3000) NOT NULL DEFAULT '' comment '备注',

  PRIMARY KEY (product_id),
  UNIQUE INDEX inso_coin_defi_mining_product_currency_network(product_base_currency, product_network_type),
  INDEX inso_coin_defi_mining_product_contractid(product_contractid),
  INDEX inso_coin_defi_mining_product_createtime(product_createtime)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci AUTO_INCREMENT=1;


-- ----------------------------
-- 流动性挖矿-质押记录
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_coin_defi_mining_record (
  record_id                       int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,

  record_product_id               int(11) NOT NULL ,
  record_contractid               int(11) NOT NULL comment '合约表ID' ,

  record_userid                   int(11) NOT NULL ,
  record_username                 varchar(255) NOT NULL comment '用户名',
  record_address                  varchar(255) NOT NULL comment '用户地址',

  record_network_type             varchar(255) NOT NULL comment '网络类型',
  record_base_currency            varchar(255) NOT NULL comment '收益币种',
  record_quote_currency           varchar(255) NOT NULL comment '挖矿币种',

  record_reward_balance           decimal(25,8) NOT NULL DEFAULT 0 comment '收益余额',
  record_total_reward_amount      decimal(25,8) NOT NULL DEFAULT 0 comment '累计收益金额',

  record_staking_status            varchar(20) NOT NULL DEFAULT 'disable' comment '开启Staking状态',
  record_staking_settle_mode       varchar(20) NOT NULL DEFAULT 'Default' comment '结算方式',
  record_staking_amount            decimal(25,8) NOT NULL DEFAULT 0 comment '质押总额',
  record_staking_reward_value      decimal(25,8) NOT NULL DEFAULT 0 comment '质押收益',
  record_staking_reward_external   decimal(25,8) NOT NULL DEFAULT 0 comment '额外收益',
  record_staking_reward_hour       int(11) NOT NULL DEFAULT 0,

  record_voucher_node_value          decimal(25,8) NOT NULL DEFAULT 0 comment '',
  record_voucher_node_settle_mode    varchar(20) NOT NULL DEFAULT 'Default' comment '结算方式',
  record_voucher_staking_value       decimal(25,8) NOT NULL DEFAULT 0 comment '',

  record_status                    varchar(20) NOT NULL comment '状态',
  record_createtime                datetime DEFAULT NULL ,
  record_remark                    varchar(3000) NOT NULL DEFAULT '' comment '备注',

  PRIMARY KEY (record_id),
  UNIQUE INDEX inso_coin_defi_mining_record_product_account(record_userid, record_product_id),
  INDEX inso_coin_defi_mining_record_product(record_product_id),
  INDEX inso_coin_defi_mining_record_createtime(record_createtime)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci AUTO_INCREMENT=1;

-- ----------------------------
-- 流动性挖矿-订单
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_coin_defi_mining_order (
  order_no                      varchar(255) NOT NULL comment '内部系统-订单号',
  order_out_trade_no            varchar(255) NOT NULL DEFAULT '' comment '引用外部订单号',

  order_userid                  int(11) UNSIGNED NOT NULL comment '用户id',
  order_username                varchar(255) NOT NULL comment  '',
  order_agentid                 int(11) UNSIGNED NOT NULL DEFAULT 0 comment '所属代理id',
  order_agentname               varchar(255) NOT NULL comment  '',
  order_staffid                 int(11) NOT NULL DEFAULT 0,
  order_staffname               varchar(255) NOT NULL comment  '',

  order_network_type            varchar(255) NOT NULL comment '所属网络',
  order_currency_type           varchar(255) NOT NULL comment '所属代币',
  order_amount                  decimal(25,8) NOT NULL comment '金额',
  order_feemoney                decimal(25,8) NOT NULL DEFAULT 0 comment '手续费',

  order_type                    varchar(50) NOT NULL DEFAULT '' comment  '订单类型: 结算收益|提现到余额',
  order_status                  varchar(20) NOT NULL  comment 'new=待支付 | waiting=上游已完成状态-对应我们此时状态 | realized=处理成功 | error=失败',
  order_createtime              datetime NOT NULL,
  order_updatetime              datetime DEFAULT NULL,
  order_remark                  varchar(1000) DEFAULT '',

  PRIMARY KEY (order_no, order_userid),
  INDEX inso_coin_defi_mining_order_createtime_userid(order_createtime, order_userid),
  INDEX inso_coin_defi_mining_order_out_trade_no(order_out_trade_no)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci AUTO_INCREMENT=1
partition by hash (order_userid)
partitions 32;

-- ----------------------------
-- 云挖矿-收益率配置
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_coin_cloud_profit_config (
  config_id                     int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,

  config_days                    int(11) UNSIGNED NOT NULL comment '投资期限',
  config_level                  int(11) UNSIGNED NOT NULL ,

  config_min_amount             decimal(25,8) NOT NULL DEFAULT 0 comment '最低投资金额',
  config_daily_rate             decimal(25,8) NOT NULL DEFAULT 0 comment '收益率',

  config_status                 varchar(20) NOT NULL comment '状态',
  config_createtime             datetime NOT NULL comment '创建时间',
  config_remark                 varchar(3000) NOT NULL DEFAULT '' comment '备注',

  PRIMARY KEY (config_id),
  UNIQUE INDEX inso_coin_cloud_profit_config_day_level(config_days, config_level),
  INDEX inso_coin_cloud_profit_config_createtime(config_createtime)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci AUTO_INCREMENT=1;

-- ----------------------------
-- 云挖矿产品-质押记录
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_coin_cloud_mining_record (
  record_id                     int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,

  record_userid                 int(11) UNSIGNED NOT NULL comment '用户id',
  record_username               varchar(255) NOT NULL comment  '',
  record_address                varchar(255) NOT NULL DEFAULT '' comment '用户地址',

  record_product_type           varchar(255) NOT NULL comment '产品类型',
  record_currency_type          varchar(255) NOT NULL comment '投资币种',
  record_days                   int(11) UNSIGNED NOT NULL comment '天数',

  record_inves_total_amount     decimal(25,8) NOT NULL DEFAULT 0 comment '投资金额',
  record_reward_balance         decimal(25,8) NOT NULL DEFAULT 0 comment '收益余额',
  record_total_reward_amount    decimal(25,8) NOT NULL DEFAULT 0 comment '累计收益',

  record_status                 varchar(20) NOT NULL comment '状态',
  record_createtime             datetime NOT NULL comment '创建时间',
  record_endtime                datetime NOT NULL comment '到期时间',
  record_remark                 varchar(3000) NOT NULL DEFAULT '' comment '备注',

  PRIMARY KEY (record_id),
  INDEX inso_coin_cloud_mining_record_product_account(record_userid, record_currency_type, record_product_type, record_days),
  INDEX inso_coin_cloud_mining_record_createtime(record_createtime)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci AUTO_INCREMENT=1;

-- ----------------------------
-- 云挖矿-订单
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_coin_cloud_mining_order (
  order_no                      varchar(255) NOT NULL comment '内部系统-订单号',
  order_out_trade_no            varchar(255) NOT NULL DEFAULT '' comment '引用外部订单号',

  order_userid                  int(11) UNSIGNED NOT NULL comment '用户id',
  order_username                varchar(255) NOT NULL comment  '',
  order_agentid                 int(11) UNSIGNED NOT NULL DEFAULT 0 comment '所属代理id',
  order_agentname               varchar(255) NOT NULL comment  '',
  order_staffid                 int(11) NOT NULL DEFAULT 0,
  order_staffname               varchar(255) NOT NULL comment  '',

  order_product_type           varchar(255) NOT NULL comment '产品类型',
  order_currency_type           varchar(255) NOT NULL comment '所属代币',
  order_amount                  decimal(25,8) NOT NULL comment '金额',
  order_feemoney                decimal(25,8) NOT NULL DEFAULT 0 comment '手续费',

  order_type                    varchar(50) NOT NULL DEFAULT '' comment  '订单类型: 结算收益|购买产品|提现到余额',
  order_status                  varchar(20) NOT NULL  comment 'new=待支付 | waiting=上游已完成状态-对应我们此时状态 | realized=处理成功 | error=失败',
  order_createtime              datetime NOT NULL,
  order_updatetime              datetime DEFAULT NULL,
  order_remark                  varchar(1000) DEFAULT '',

  PRIMARY KEY (order_no, order_userid),
  INDEX inso_coin_cloud_mining_order_createtime_userid(order_createtime, order_userid),
  INDEX inso_coin_cloud_mining_order_out_trade_no(order_out_trade_no)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci AUTO_INCREMENT=1
partition by hash (order_userid)
partitions 32;


-- ----------------------------
-- 币安活动-挖矿记录
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_coin_binance_activity_mining_record (
  record_id                     int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,

  record_contractid            int(11) NOT NULL comment '合约id',
  record_network_type           varchar(255) NOT NULL comment  '',
  record_currency_type          varchar(255) NOT NULL comment '投资币种',

  record_userid                 int(11) UNSIGNED NOT NULL comment '用户id',
  record_username               varchar(255) NOT NULL comment  '',
  record_address                varchar(255) NOT NULL DEFAULT '' comment '用户地址',

  record_total_reward_amount    decimal(25,8) NOT NULL DEFAULT 0 comment '累计收益金额',

  record_status                 varchar(20) NOT NULL comment '状态',
  record_createtime             datetime NOT NULL comment '创建时间',
  record_remark                 varchar(3000) NOT NULL DEFAULT '' comment '备注',

  PRIMARY KEY (record_id),
  UNIQUE INDEX inso_coin_binance_activity_mining_record_userid_contractid(record_userid, record_contractid),
  INDEX inso_coin_binance_activity_mining_record_address(record_address),
  INDEX inso_coin_binance_activity_mining_record_createtime(record_createtime)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci AUTO_INCREMENT=1;

-- ----------------------------
-- 币安活动-订单
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_coin_binance_activity_mining_order (
  order_no                      varchar(255) NOT NULL comment '内部系统-订单号',
  order_out_trade_no            varchar(255) NOT NULL DEFAULT '' comment '引用外部订单号',

  order_userid                  int(11) UNSIGNED NOT NULL comment '用户id',
  order_username                varchar(255) NOT NULL comment  '',
  order_agentid                 int(11) UNSIGNED NOT NULL DEFAULT 0 comment '所属代理id',
  order_agentname               varchar(255) NOT NULL comment  '',
  order_staffid                 int(11) NOT NULL DEFAULT 0,
  order_staffname               varchar(255) NOT NULL comment  '',

  order_currency_type           varchar(255) NOT NULL comment '所属代币',
  order_amount                  decimal(25,8) NOT NULL comment '金额',
  order_feemoney                decimal(25,8) NOT NULL DEFAULT 0 comment '手续费',

  order_type                    varchar(50) NOT NULL DEFAULT '' comment  '订单类型: 结算收益|购买产品|提现到余额',
  order_status                  varchar(20) NOT NULL  comment 'new=待支付 | waiting=上游已完成状态-对应我们此时状态 | realized=处理成功 | error=失败',
  order_createtime              datetime NOT NULL,
  order_updatetime              datetime DEFAULT NULL,
  order_remark                  varchar(1000) DEFAULT '',

  PRIMARY KEY (order_no, order_userid),
  INDEX inso_coin_binance_activity_mining_order_createtime_userid(order_createtime, order_userid),
  INDEX inso_coin_binance_activity_mining_order_out_trade_no(order_out_trade_no)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci AUTO_INCREMENT=1
partition by hash (order_userid)
partitions 32;



-- ----------------------------
-- 数字货币充值——订单
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_coin_digital_currency_recharge_order (
    order_no                      varchar(255) NOT NULL comment '内部系统-订单号',
    order_out_trade_no            varchar(255) NOT NULL DEFAULT '' comment '引用外部订单号',


    order_wallet_outaddress       varchar(255) NOT NULL comment '出款地址',
    order_wallet_inaddress        varchar(255) NOT NULL comment '收款地址',
    order_network_type            varchar(255) NOT NULL comment '所属网络',
    order_currency_type           varchar(255) NOT NULL comment '所属代币',
    order_amount                  decimal(25,8) NOT NULL comment '金额',
    order_feemoney                decimal(25,8) NOT NULL DEFAULT 0 comment '手续费',

    order_type                    varchar(50) NOT NULL DEFAULT '' comment  '订单类型: 结算收益|购买产品|提现到余额',
    order_status                  varchar(20) NOT NULL  comment 'new=待支付 | waiting=上游已完成状态-对应我们此时状态 | realized=处理成功 | error=失败',
    order_createtime              datetime NOT NULL,
    order_updatetime              datetime DEFAULT NULL,
    order_remark                  varchar(1000) DEFAULT '',

    PRIMARY KEY (order_no, order_userid),
    INDEX inso_coin_digital_currency_recharge_order_createtime_userid(order_createtime),
    INDEX inso_coin_digital_currency_recharge_order_out_trade_no(order_out_trade_no)
    ) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci AUTO_INCREMENT=1
    partition by hash (order_userid)
    partitions 32;


-- ----------------------------
-- 地址池
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_coin_wallet (
    wallet_id              int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,

    wallet_address         varchar(255) NOT NULL comment '地址',
    wallet_private_key      varchar(255) NOT NULL comment '地址私钥',
    wallet_network_type     varchar(255) NOT NULL comment '所属网络',

    wallet_userid                  int(11) UNSIGNED NOT NULL comment '用户id',
    wallet_username                varchar(255) NOT NULL comment  '',
    wallet_agentid                 int(11) UNSIGNED NOT NULL DEFAULT 0 comment '所属代理id',
    wallet_agentname               varchar(255) NOT NULL comment  '',
    wallet_staffid                 int(11) NOT NULL DEFAULT 0,
    wallet_staffname               varchar(255) NOT NULL comment  '',

    wallet_uamount          decimal(25,8) NOT NULL comment 'usdt金额',
    wallet_zbamount          decimal(25,8) NOT NULL comment '主币金额',

    wallet_status          varchar(20) NOT NULL COMMENT 'enale|disable',
    wallet_createtime      datetime NOT NULL,
    wallet_updatetime      datetime DEFAULT NULL,
    wallet_remark          varchar(3000) NOT NULL DEFAULT '',

    PRIMARY KEY (wallet_id,wallet_address),
    INDEX inso_coin_wallet_wallet_id(wallet_id),
    INDEX inso_coin_wallet_wallet_address(wallet_address),
    INDEX inso_coin_wallet_wallet_username(wallet_username),
    INDEX inso_coin_wallet_createtime(wallet_createtime)
    ) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci AUTO_INCREMENT=1;


-- ----------------------------
-- 地址池2
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_coin_wallet_copy (
    wallet_id              int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,

    wallet_address         varchar(255) NOT NULL comment '地址',
    wallet_private_key     varchar(255) NOT NULL comment '地址私钥',
    wallet_network_type    varchar(255) NOT NULL comment '所属网络',

    wallet_userid          int(11) UNSIGNED NOT NULL comment '用户id',
    wallet_username        varchar(255) NOT NULL comment '用户名',
    wallet_agentid         int(11) UNSIGNED NOT NULL DEFAULT 0 comment '所属代理id',
    wallet_agentname       varchar(255) NOT NULL comment '代理名',
    wallet_staffid         int(11) NOT NULL DEFAULT 0,
    wallet_staffname       varchar(255) NOT NULL comment '员工名',

    wallet_uamount         decimal(25,8) NOT NULL comment 'usdt金额',
    wallet_zbamount        decimal(25,8) NOT NULL comment '主币金额',

    wallet_status          varchar(20) NOT NULL COMMENT 'enale|disable',
    wallet_createtime      datetime NOT NULL,
    wallet_updatetime      datetime DEFAULT NULL,
    wallet_remark          varchar(3000) NOT NULL DEFAULT '',

    PRIMARY KEY (wallet_id,wallet_address),
    INDEX inso_coin_wallet_wallet_id(wallet_id),
    INDEX inso_coin_wallet_wallet_address(wallet_address),
    INDEX inso_coin_wallet_wallet_username(wallet_username),
    INDEX inso_coin_wallet_createtime(wallet_createtime)
    ) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci AUTO_INCREMENT=1;

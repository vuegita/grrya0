
-- ----------------------------
-- 用户表
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_passport_user (
  user_id             int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
  user_name           varchar(255) NOT NULL ,
  user_phone          varchar(30) NOT NULL,
  user_email          varchar(50) NOT NULL,
  user_invite_code    varchar(32) NOT NULL,
  user_nickname       varchar(20) DEFAULT '' comment '用户昵称' ,
  user_sex            varchar(10) DEFAULT '' comment 'sex' ,
  user_type           varchar(50) NOT NULL COMMENT 'agent|staff|member|robot',
  user_sub_type       varchar(50) NOT NULL DEFAULT 'simple' COMMENT '用户子类型-如会员子类型有=> simple|promotion',
  user_createtime     datetime NOT NULL ,
  user_avatar         varchar(255) DEFAULT '',
  user_registerpath   varchar(255) DEFAULT '' comment 'wx|ios|android|pc',
  user_registerip     varchar(50) NOT NULL ,
  user_lastloginip    varchar(45) DEFAULT '' comment '最后登录IP',
  user_lastlogintime  datetime DEFAULT NULL comment '最后登录时间',

  user_login_agent_status   varchar(255) NOT NULL DEFAULT 'disable',

  user_status         varchar(20) NOT NULL DEFAULT 'enable' COMMENT 'enable|disable|fund_enable',
  user_remark         varchar(255) NOT NULL DEFAULT '' COMMENT '',
  PRIMARY KEY (user_id),
  UNIQUE INDEX inso_passport_user_name(user_name),
  UNIQUE INDEX inso_passport_user_phone(user_phone),
  UNIQUE INDEX inso_passport_user_email(user_email),
  UNIQUE INDEX inso_passport_user_invite_code(user_invite_code),
  INDEX inso_passport_user_createtime(user_createtime)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci AUTO_INCREMENT=1;

-- ----------------------------
-- 超级会员-股东
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_passport_share_holder (
  holder_id               int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
  holder_userid           int(11) NOT NULL ,
  holder_username         varchar(255) NOT NULL ,

  holder_lv1_rw_status    varchar(20) NOT NULL DEFAULT 'enable' COMMENT 'enable|disable',
  holder_lv2_rw_status    varchar(20) NOT NULL DEFAULT 'enable' COMMENT 'enable|disable',
  holder_system_status    varchar(20) NOT NULL DEFAULT 'enable' COMMENT 'apply|enable|disable',

  holder_createtime       datetime NOT NULL ,

  holder_remark           varchar(255) NOT NULL DEFAULT '' COMMENT '',
  PRIMARY KEY (holder_id),
  UNIQUE INDEX inso_passport_share_holder_userid(holder_userid),
  INDEX inso_passport_share_holder_createtime(holder_createtime)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci AUTO_INCREMENT=1;

-- ----------------------------
-- 用户-安全信息
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_passport_user_secret (
  secret_userid            int(11) UNSIGNED NOT NULL ,
  secret_username          varchar(255) NOT NULL ,
  secret_logintype         varchar(50) NOT NULL comment '登陆类型',
  secret_loginpwd          char(32) COLLATE utf8_bin NOT NULL comment '登陆密码',
  secret_loginsalt         char(32) NOT NULL ,
  secret_paypwd            char(32) COLLATE utf8_bin NOT NULL comment '支付密码',
  secret_paysalt           char(32) NOT NULL ,
  secret_google_key        varchar(50) DEFAULT '' comment 'Google 密钥',
  secret_google_status     varchar(50) DEFAULT 'unbind' comment 'Google 状态=> bind|unbind',
  PRIMARY KEY (secret_username),
  INDEX inso_passport_user_secret_username(secret_username)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci AUTO_INCREMENT=1;

-- ----------------------------
-- 用户-商户秘钥
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_passport_user_agent_app (
  app_id 						int(11) NOT NULL AUTO_INCREMENT,
  app_agentid 				    int(11) NOT NULL comment '商户ID',
  app_agentname                 varchar(50) NOT NULL comment '商户用户名',
  app_access_key                varchar(255) NOT NULL,
  app_access_secret	  		    varchar(255) NOT NULL,
  app_approve_notify_url	    varchar(200) DEFAULT '' comment '回调地址',
  app_status    	  		    varchar(255) NOT NULL,
  app_createtime  				datetime DEFAULT NULL ,

  PRIMARY KEY (app_id),
  UNIQUE INDEX inso_passport_user_agent_app_agentid(app_agentid),
  INDEX inpay_merchant_app(app_createtime)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci AUTO_INCREMENT=1;


-- ----------------------------
-- 代理配置
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_passport_user_agent_config (
  config_id 						int(11) NOT NULL AUTO_INCREMENT,
  config_agentid 				    int(11) NOT NULL comment '',
  config_agentname                  varchar(50) NOT NULL comment '',
  config_type    	  		        varchar(255) NOT NULL,
  config_value    	  		        varchar(255) NOT NULL,
  config_status    	  		        varchar(255) NOT NULL,
  config_createtime  				datetime DEFAULT NULL ,

  PRIMARY KEY (config_id),
  UNIQUE INDEX inso_passport_user_agent_config_agentid_type(config_agentid, config_type),
  INDEX inso_passport_user_agent_config_createtime(config_createtime)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci AUTO_INCREMENT=1;

-- ----------------------------
-- 用户关系表
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_passport_user_relation (
 relation_ancestor INT NOT NULL comment '祖先',
 relation_descendant INT NOT NULL COMMENT '后代',
 relation_depth INT NOT NULL comment '深度',
 PRIMARY KEY (relation_ancestor, relation_descendant),
 UNIQUE INDEX inso_passport_user_relation_descenedant_ancestor(relation_descendant, relation_ancestor)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci AUTO_INCREMENT=1;

-- ----------------------------
-- 用户关注列表-特别用户进行监控
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_passport_user_system_follow (
 follow_userid    INT NOT NULL comment '被关注人的用户id',
 follow_agentid   INT NOT NULL DEFAULT 0 COMMENT '被关注人的代理id',
 follow_staffid   INT NOT NULL DEFAULT 0 COMMENT '被关注人的员工id',
 follow_type      varchar(50) NOT NULL comment '关注类型',
 follow_remark    varchar(255) DEFAULT '' comment '用户信息备注',

 PRIMARY KEY (follow_userid, follow_agentid),
 INDEX inso_passport_user_follow_agentid(follow_agentid,follow_staffid),
 INDEX inso_passport_user_follow_type(follow_type)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci AUTO_INCREMENT=1;

-- ----------------------------
-- 用户属性表
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_passport_user_attr (
 attr_userid                        int(11) UNSIGNED NOT NULL,
 attr_username                      varchar(255) NOT NULL,
 attr_direct_staffname              varchar(255) NOT NULL ,
 attr_direct_staffid                int(11) UNSIGNED NOT NULL DEFAULT 0 comment '直属员工上级',
 attr_parentname                    varchar(255) NOT NULL DEFAULT '',
 attr_parentid                      int(11) UNSIGNED NOT NULL DEFAULT 0 comment '邀请我的人用户id-也我的父级',
 attr_grantfathername               varchar(255) NOT NULL DEFAULT '',
 attr_grantfatherid                 int(11) UNSIGNED NOT NULL DEFAULT 0 comment '祖父id',
 attr_agentname                     varchar(255) NOT NULL DEFAULT '' comment '所属代理名称',
 attr_agentid                       int(11) UNSIGNED NOT NULL DEFAULT 0 comment '所属代理id',
 attr_agent_admin_login_status      varchar(20) NOT NULL DEFAULT 'disable' comment 'enable|disable',
 attr_return_water                  FLOAT(7, 4) UNSIGNED NOT NULL DEFAULT 0 comment '返水-返佣-> [0-1], 最长7位，小数最多4位',
 attr_first_recharge_orderno        varchar(50) NOT NULL DEFAULT '' comment '首充订单号',
 attr_first_recharge_time           datetime DEFAULT NULL comment '首充时间',
 attr_first_recharge_amount         decimal(18,2) NOT NULL DEFAULT 0 comment '',
 attr_first_bet_time                datetime DEFAULT NULL comment '首次下注时间',

 attr_return_level_status           varchar(100) NOT NULL DEFAULT 'disable' comment '状态',
 attr_return_lv1_rate               decimal(18,4) NOT NULL DEFAULT 1 comment '自定义返上级点-lv1-默认全返',
 attr_return_lv2_rate               decimal(18,4) NOT NULL DEFAULT 1 comment '自定义返上级点-lv2-默认全返',
 attr_receiv_lv1_rate               decimal(18,4) NOT NULL DEFAULT 1 comment '自定义接受上级点-lv1-默认全返',
 attr_receiv_lv2_rate               decimal(18,4) NOT NULL DEFAULT 1 comment '自定义接受上级点-lv2-默认全返',

 attr_invite_friend_total_amount    decimal(18,2) NOT NULL DEFAULT 0 comment '邀请好友并完成充值赠送',
 attr_level                         varchar(50) DEFAULT '' comment '用户等级',
 attr_remark                        varchar(255) DEFAULT '' comment '用户信息备注',

 PRIMARY KEY (attr_userid),
 INDEX inso_passport_user_attr_direct_staffid(attr_direct_staffid),
 INDEX inso_passport_user_attr_parentid(attr_parentid),
 INDEX inso_passport_user_attr_grantfatherid(attr_grantfatherid),
 INDEX inso_passport_user_attr_agentid(attr_agentid),
 INDEX inso_passport_user_attr_level(attr_level),
 INDEX inso_passport_user_attr_first_recharge_orderno(attr_first_recharge_orderno),
 INDEX inso_passport_user_attr_first_recharge_time(attr_first_recharge_time)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci AUTO_INCREMENT=1;

-- ----------------------------
-- 访问统计/每天
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_passport_invite_stats_day (
  day_id                        int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
  day_key                       varchar(255) NOT NULL comment '',

  day_userid                    int(11) NOT NULL DEFAULT 0,
  day_username                  varchar(255) NOT NULL DEFAULT '' comment  '',

  day_total_count               int(11) UNSIGNED NOT NULL DEFAULT 0,

  day_pdate                     date NOT NULL comment '创建时间',

  PRIMARY KEY (day_id),
  UNIQUE INDEX inso_passport_invite_stats_day_key(day_pdate, day_key),
  index inso_passport_invite_stats_day_userid(day_userid)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci AUTO_INCREMENT=1;

-- ----------------------------
-- 代理域名配置
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_passport_agent_domain (
  domain_id               int(11) NOT NULL AUTO_INCREMENT ,
  domain_url              varchar(255) NOT NULL comment '',
  domain_agentid          int(11) NOT NULL,
  domain_agentname        varchar(255) NOT NULL ,
  domain_staffid         int(11) NOT NULL ,
  domain_staffname        varchar(255) NOT NULL ,
  domain_status           varchar(20) NOT NULL,
  domain_createtime       datetime DEFAULT NULL ,

  PRIMARY KEY (domain_id),
  UNIQUE INDEX inso_passport_agent_domain_url(domain_url),
  UNIQUE INDEX inso_passport_agent_domain_agentid(domain_agentid),
  INDEX inso_passport_agent_domain_createtime(domain_createtime)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci AUTO_INCREMENT=1;

-- ----------------------------
-- 用户余额
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_passport_user_money (
  money_id              int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
  money_userid          int(11) NOT NULL,
  money_username        varchar(255) NOT NULL ,

  money_fund_key        varchar(50) NOT NULL comment 'Sport=现货 | Margin=杠杆 | Futures=合约 | P2P=C2C | Earn=理财 | Pool=矿池| Gift=礼品卡',
  money_currency        varchar(50) NOT NULL comment '币种->USDT|ETH|BTC等',

  money_balance                    decimal(25,8) NOT NULL comment '余额',
  money_freeze                     decimal(25,8) NOT NULL DEFAULT 0 comment '冻结金额',
  money_code_amount                decimal(25,8) NOT NULL DEFAULT 0 comment '打码量',
  money_cold_amount                decimal(25,8) NOT NULL DEFAULT 0 comment '不可用金额, 需要用户手动转出-金额其实已经增加，类似冻结的概念',

  money_limit_amount               decimal(25,8) NOT NULL DEFAULT 0 comment '限制不可提金额',
  money_limit_code                 decimal(25,8) NOT NULL DEFAULT 0 comment '限制不可提打码',

  money_total_recharge             decimal(25,8) NOT NULL DEFAULT 0 comment '总充值',
  money_total_withdraw             decimal(25,8) NOT NULL DEFAULT 0 comment '总提现',
  money_total_refund               decimal(25,8) NOT NULL DEFAULT 0 comment '总退款',
  money_total_deduct_code_amount   decimal(25,8) NOT NULL DEFAULT 0 comment '扣款总打码',
  money_status              varchar(20) NOT NULL,
  money_createtime          datetime DEFAULT NULL ,

  PRIMARY KEY (money_id),
  UNIQUE INDEX inso_passport_user_money_userid_fundkey_currency(money_userid, money_fund_key, money_currency),
  INDEX inso_passport_user_money_createtime(money_createtime)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci AUTO_INCREMENT=1;

-- ----------------------------
-- 用户金额变动明细订单表
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_passport_user_money_order (
  um_order_no                     varchar(30) NOT NULL comment '内部系统-订单号' ,
  um_order_out_trade_no           varchar(255) NOT NULL comment  '内部系统-订单号, 业务订单号',
  um_order_userid                 int(11) NOT NULL,
  um_order_username               varchar(255) NOT NULL comment  '',
  um_order_agentid                int(11) NOT NULL DEFAULT 0,
  um_order_agentname              varchar(255) NOT NULL comment  '',
  um_order_staffid                int(11) NOT NULL DEFAULT 0,
  um_order_staffname              varchar(255) NOT NULL comment  '',

  um_order_fund_key               varchar(50) NOT NULL comment 'Sport=现货 | Margin=杠杆 | Futures=合约 | P2P=C2C | Earn=理财 | Pool=矿池| Gift=礼品卡',
  um_order_currency               varchar(50) NOT NULL comment '币种->USDT|ETH|BTC等',

  um_order_business_type          varchar(50) NOT NULL comment  '业务类型',
  um_order_type                   varchar(50) NOT NULL comment '订单类型=>recharge=充值|withdraw=提现|platform_recharge=系统充值|platform_deduction=系统扣款|refund=退款' ,
  um_order_status                 varchar(20) NOT NULL  comment 'new=待支付 | realized=处理成功 | error=失败',
  um_order_balance                decimal(25,8) NOT NULL DEFAULT 0 comment '余额',
  um_order_amount                 decimal(25,8) NOT NULL comment '流水金额',
  um_order_feemoney               decimal(25,8) NOT NULL comment '手续费-提现才有',
  um_order_createtime             datetime NOT NULL comment '和报表时间要一样',
  um_order_updatetime             datetime DEFAULT NULL,
  um_order_remark                 varchar(1000) DEFAULT '',

  PRIMARY KEY (um_order_out_trade_no, um_order_userid, um_order_type),
  UNIQUE INDEX inso_passport_user_money_order_no(um_order_no, um_order_userid),
  INDEX inso_passport_user_money_order_createtime(um_order_createtime, um_order_userid),
  INDEX inso_passport_user_money_order_agentid_staffid(um_order_agentid, um_order_staffid)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci AUTO_INCREMENT=1
partition by hash (um_order_userid)
partitions 64;

-- ----------------------------
-- 用户-银行卡编码
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_passport_user_bank_card (
  card_id                  int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
  card_userid              int(11) UNSIGNED NOT NULL,
  card_username            varchar(255) NOT NULL comment '用户名',
  card_type                varchar(30) NOT NULL comment 'upi|bank',
  card_name                varchar(100) NOT NULL comment '卡号名称',
  card_currency_type       varchar(100) NOT NULL comment '所属币种',
  card_ifsc                varchar(255) NOT NULL comment '账户类型: 印度为IFSC-11位',
  card_account             varchar(255) NOT NULL comment '银行卡号或upi地址',
  card_beneficiary_name    varchar(200) NOT NULL comment '受益人姓名',
  card_beneficiary_email   varchar(200) NOT NULL comment '受益人邮箱',
  card_beneficiary_phone   varchar(200) NOT NULL comment '受益人手机',
  card_createtime          datetime NOT NULL ,
  card_status              varchar(20) NOT NULL DEFAULT 'enable' COMMENT 'enable|disable',
  card_remark              varchar(1000) DEFAULT '',

  PRIMARY KEY (card_id),
  INDEX inso_passport_user_bank_card_userid(card_userid),
  INDEX inso_passport_user_bank_card_createtime(card_createtime)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- 用户相关业务订单表- 充值、提现、补单、首充赠送、活动赠送、
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_passport_user_business_order (
  ub_order_no                     varchar(30) NOT NULL comment '内部系统-订单号',
  ub_order_out_trade_no           varchar(255) NOT NULL DEFAULT '' comment '引用外部订单号,如果有',
  ub_order_userid                 int(11) NOT NULL,
  ub_order_checker                varchar(50) NOT NULL DEFAULT '' comment  '审核人',
  ub_order_username               varchar(255) NOT NULL comment  '',

  ub_order_agentid                int(11) UNSIGNED NOT NULL DEFAULT 0 comment '所属代理id',
  ub_order_agentname              varchar(255) NOT NULL comment  '',
  ub_order_staffid                int(11) NOT NULL DEFAULT 0,
  ub_order_staffname              varchar(255) NOT NULL comment  '',

  ub_order_fund_key               varchar(50) NOT NULL comment 'Sport=现货 | Margin=杠杆 | Futures=合约 | P2P=C2C | Earn=理财 | Pool=矿池| Gift=礼品卡',
  ub_order_currency               varchar(50) NOT NULL comment '币种->USDT|ETH|BTC等',

  ub_order_business_code          int(11) NOT NULL comment '业务编码',
  ub_order_business_name          varchar(50) NOT NULL comment '订单类型=>recharge=充值|withdraw=提现|platform_recharge=系统充值|platform_deduction=系统扣款|task_donate=任务赠送|first_donate=首充赠送)' ,
  ub_order_status                 varchar(20) NOT NULL  comment 'new=待支付 | captured=上游已完成状态-对应我们此时状态 | realized=处理成功 | error=失败',
  ub_order_amount                 decimal(25,8) NOT NULL comment '流水金额',
  ub_order_feemoney               decimal(25,8) NOT NULL comment '手续费-提现才有',
  ub_order_createtime             datetime NOT NULL,
  ub_order_updatetime             datetime DEFAULT NULL,
  ub_order_remark                 varchar(3000) DEFAULT '',

  PRIMARY KEY (ub_order_no, ub_order_userid),
  UNIQUE INDEX inso_passport_user_business_order_out_trade_no(ub_order_out_trade_no, ub_order_business_code, ub_order_userid),
  INDEX inso_passport_user_business_order_createtime_userid(ub_order_createtime, ub_order_userid),
  INDEX inso_passport_user_business_order_agentid_staffid(ub_order_agentid, ub_order_staffid)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci AUTO_INCREMENT=1
partition by hash (ub_order_userid)
partitions 32;

-- ----------------------------
-- 推广赠送
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_passport_user_promotion_present_order (
  order_no                     varchar(30) NOT NULL comment '内部系统-订单号',

  order_userid                 int(11) NOT NULL,
  order_checker                varchar(50) NOT NULL DEFAULT '' comment  '审核人',
  order_username               varchar(255) NOT NULL comment  '',

  order_agentid                int(11) UNSIGNED NOT NULL DEFAULT 0 comment '所属代理id',
  order_agentname              varchar(255) NOT NULL comment  '',
  order_staffid                int(11) NOT NULL DEFAULT 0,
  order_staffname              varchar(255) NOT NULL comment  '',

  order_currency               varchar(50) NOT NULL comment '币种->USDT|ETH|BTC等',

  order_limit_rate1            decimal(25,8) NOT NULL DEFAULT 0 comment 'rate1',
  order_limit_status1          varchar(20) NOT NULL DEFAULT '' comment '',

  order_limit_rate2            decimal(25,8) NOT NULL DEFAULT 0 comment 'rate2',
  order_limit_status2          varchar(20) NOT NULL DEFAULT '' comment '',

  order_show_status            varchar(20) NOT NULL DEFAULT 'disable' comment '',
  order_settle_mode            varchar(20) NOT NULL  comment '',
  order_tips                   varchar(255) NOT NULL DEFAULT '' comment '',

  order_status                 varchar(20) NOT NULL  comment 'new=待支付 | captured=上游已完成状态-对应我们此时状态 | realized=处理成功 | error=失败',
  order_amount                 decimal(25,8) NOT NULL comment '流水金额',
  order_feemoney               decimal(25,8) NOT NULL comment '手续费-提现才有',
  order_createtime             datetime NOT NULL,
  order_remark                 varchar(3000) DEFAULT '',

  PRIMARY KEY (order_no),
  INDEX inso_passport_user_promotion_present_order_createtime_userid(order_createtime, order_userid)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci AUTO_INCREMENT=1;


-- ----------------------------
-- 用户充值订单
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_passport_user_recharge_order (
  order_no                     varchar(30) NOT NULL comment '内部系统-订单号',
  order_out_trade_no           varchar(255) NOT NULL DEFAULT '' comment '引用外部订单号,如果有',
  order_userid                 int(11) NOT NULL,
  order_checker                varchar(50) NOT NULL DEFAULT '' comment  '审核人',
  order_pay_product_type       varchar(100) NOT NULL DEFAULT '' comment  '支付产品类型',

  order_channelname            varchar(255) NOT NULL comment  '',
  order_channelid              int(11) UNSIGNED NOT NULL DEFAULT 0 comment '',

  order_username               varchar(255) NOT NULL comment  '',
  order_agentid                int(11) UNSIGNED NOT NULL DEFAULT 0 comment '所属代理id',
  order_agentname              varchar(255) NOT NULL comment  '',
  order_staffid                int(11) NOT NULL DEFAULT 0,
  order_staffname              varchar(255) NOT NULL comment  '',

  order_fund_key               varchar(50) NOT NULL comment 'Sport=现货 | Margin=杠杆 | Futures=合约 | P2P=C2C | Earn=理财 | Pool=矿池| Gift=礼品卡',
  order_currency               varchar(50) NOT NULL comment '币种->USDT|ETH|BTC等',

  order_status                 varchar(20) NOT NULL  comment 'new=待支付 | captured=上游已完成状态-对应我们此时状态 | realized=处理成功 | error=失败',
  order_amount                 decimal(25,8) NOT NULL comment '流水金额',
  order_feemoney               decimal(25,8) NOT NULL comment '手续费-提现才有',
  order_createtime             datetime NOT NULL,
  order_updatetime             datetime DEFAULT NULL,
  order_remark                 varchar(3000) DEFAULT '',

  PRIMARY KEY (order_no, order_userid),
  INDEX inso_passport_user_recharge_order_createtime_userid(order_createtime, order_userid),
  INDEX inso_passport_user_recharge_order_agentid_staffid(order_agentid, order_staffid),
  INDEX inso_passport_user_recharge_order_order_out_trade_no(order_out_trade_no)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci AUTO_INCREMENT=1
partition by hash (order_userid)
partitions 32;

-- ----------------------------
-- 用户提现订单
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_passport_user_withdraw_order (
  order_no                     varchar(30) NOT NULL comment '内部系统-订单号',
  order_out_trade_no           varchar(255) NOT NULL DEFAULT '' comment '引用外部订单号,如果有',
  order_userid                 int(11) NOT NULL,
  order_checker                varchar(50) NOT NULL DEFAULT '' comment  '审核人',
  order_pay_product_type       varchar(100) NOT NULL DEFAULT '' comment  '支付产品类型',

  order_submit_count           int(11) NOT NULL DEFAULT 0 comment '',

  order_username               varchar(255) NOT NULL comment  '',
  order_agentid                int(11) UNSIGNED NOT NULL DEFAULT 0 comment '所属代理id',
  order_agentname              varchar(255) NOT NULL comment  '',
  order_staffid                int(11) NOT NULL DEFAULT 0,
  order_staffname              varchar(255) NOT NULL comment  '',

  order_channelname            varchar(255) NOT NULL DEFAULT '' comment  '',
  order_channelid              int(11) UNSIGNED NOT NULL DEFAULT 0 comment '',

  order_fund_key               varchar(50) NOT NULL comment 'Sport=现货 | Margin=杠杆 | Futures=合约 | P2P=C2C | Earn=理财 | Pool=矿池| Gift=礼品卡',
  order_currency               varchar(50) NOT NULL comment '币种->USDT|ETH|BTC等',

  order_status                 varchar(20) NOT NULL  comment 'new=待支付 | captured=上游已完成状态-对应我们此时状态 | realized=处理成功 | error=失败',
  order_amount                 decimal(25,8) NOT NULL comment '流水金额',
  order_feemoney               decimal(25,8) NOT NULL comment '手续费-提现才有',
  order_beneficiary_account    varchar(50) NOT NULL DEFAULT '' comment  '受益人账户',
  order_beneficiary_idcard     varchar(50) NOT NULL DEFAULT '' comment  '受益人身份证-有些国家需要',
  order_createtime             datetime NOT NULL,
  order_updatetime             datetime DEFAULT NULL,
  order_remark                 varchar(3000) DEFAULT '',

  PRIMARY KEY (order_no, order_userid),
  INDEX inso_passport_user_withdraw_order_createtime_userid(order_createtime, order_userid),
  INDEX inso_passport_user_withdraw_order_agentid_staffid(order_agentid, order_staffid),
  INDEX inso_passport_user_withdraw_order_out_trade_no(order_out_trade_no),
  INDEX inso_passport_user_withdraw_order_beneficiary_account(order_beneficiary_account),
  INDEX inso_passport_user_withdraw_order_beneficiary_idcard(order_beneficiary_idcard)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci AUTO_INCREMENT=1
partition by hash (order_userid)
partitions 32;

-- ----------------------------
-- 系统每日赠送
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_passport_user_day_present_order2 (
  order_no                   varchar(50) NOT NULL comment '内部系统-订单号',
  order_out_trade_no         varchar(50) NOT NULL comment '内部系统-订单号',

  order_userid               int(11) NOT NULL,
  order_username             varchar(255) NOT NULL comment  '',
  order_agentid              int(11) UNSIGNED NOT NULL DEFAULT 0 comment '所属代理id',
  order_agentname             varchar(255) NOT NULL default '' comment  '',
  order_staffid              int(11) NOT NULL DEFAULT 0,
  order_staffname             varchar(255) NOT NULL default '' comment  '',

  order_fund_key             varchar(50) NOT NULL comment 'Sport=现货 | Margin=杠杆 | Futures=合约 | P2P=C2C | Earn=理财 | Pool=矿池| Gift=礼品卡',
  order_currency             varchar(50) NOT NULL comment '币种->USDT|ETH|BTC等',

  order_business_key         varchar(50) NOT NULL DEFAULT '' comment '业务唯一key-如订单号|任务id',
  order_checker              varchar(50) NOT NULL DEFAULT '' comment '' ,
  order_status               varchar(20) NOT NULL  comment '',
  order_amount               decimal(25,8) NOT NULL comment '流水金额',
  order_feemoney             decimal(25,8) NOT NULL comment '手续费-提现才有',
  order_createtime           datetime NOT NULL,
  order_remark               varchar(1000) DEFAULT '',

  PRIMARY KEY (order_no),
  UNIQUE INDEX inso_passport_user_day_present_order_out_trade_no(order_out_trade_no),
  INDEX inso_passport_user_invite_present_order_createtime(order_createtime, order_userid)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci AUTO_INCREMENT=1;

-- ----------------------------
-- 反水订单
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_passport_return_water_order (
  order_no                     varchar(30) NOT NULL comment '内部系统-订单号',
  order_out_trade_no           varchar(255) NOT NULL DEFAULT '' comment '引用外部订单号,如果有',
  order_userid                 int(11) NOT NULL,
  order_username               varchar(255) NOT NULL comment  '',
  order_agentid                int(11) UNSIGNED NOT NULL DEFAULT 0 comment '所属代理id',
  order_staffid                int(11) NOT NULL DEFAULT 0,
  order_from_level             int(11) NOT NULL DEFAULT 0,

  order_fund_key               varchar(50) NOT NULL comment 'Sport=现货 | Margin=杠杆 | Futures=合约 | P2P=C2C | Earn=理财 | Pool=矿池| Gift=礼品卡',
  order_currency               varchar(50) NOT NULL comment '币种->USDT|ETH|BTC等',

  order_checker                varchar(50) NOT NULL DEFAULT '' comment  '审核人',
  order_status                 varchar(20) NOT NULL  comment 'new=待支付 | captured=上游已完成状态-对应我们此时状态 | realized=处理成功 | error=失败',
  order_amount                 decimal(25,8) NOT NULL comment '流水金额',
  order_createtime             datetime NOT NULL,
  order_updatetime             datetime DEFAULT NULL,
  order_remark                 varchar(3000) DEFAULT '',

  PRIMARY KEY (order_no, order_userid),
  UNIQUE INDEX inso_passport_return_water_order_out_trade_no(order_out_trade_no, order_userid),
  INDEX inso_passport_return_water_order_createtime_userid(order_createtime, order_userid),
  INDEX inso_passport_return_water_order_agentid_staffid(order_agentid, order_staffid),
  INDEX inso_passport_return_water_order_currency(order_currency)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci AUTO_INCREMENT=1
partition by hash (order_userid)
partitions 4;

-- ----------------------------
-- 反首充给上级订单
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_passport_first_recharge_present_return_up_order (
  order_id                    int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
  order_no                     varchar(30) NOT NULL comment '内部系统-订单号',
  order_out_trade_no           varchar(255) NOT NULL DEFAULT '' comment '引用外部订单号,如果有',
  order_userid                 int(11) NOT NULL,
  order_username               varchar(255) NOT NULL comment  '',
  order_agentid                int(11) UNSIGNED NOT NULL DEFAULT 0 comment '所属代理id',
  order_agentname               varchar(255) NOT NULL comment  '',
  order_staffid                int(11) UNSIGNED NOT NULL DEFAULT 0 comment '所属代理id',
  order_staffname               varchar(255) NOT NULL comment  '',

  order_from_level             int(11) NOT NULL DEFAULT 0,

  order_fund_key               varchar(50) NOT NULL comment 'Sport=现货 | Margin=杠杆 | Futures=合约 | P2P=C2C | Earn=理财 | Pool=矿池| Gift=礼品卡',
  order_currency               varchar(50) NOT NULL comment '币种->USDT|ETH|BTC等',

  order_checker                varchar(50) NOT NULL DEFAULT '' comment  '审核人',
  order_status                 varchar(20) NOT NULL  comment 'new=待支付 | captured=上游已完成状态-对应我们此时状态 | realized=处理成功 | error=失败',
  order_amount                 decimal(25,8) NOT NULL comment '流水金额',
  order_createtime             datetime NOT NULL,
  order_updatetime             datetime DEFAULT NULL,
  order_remark                 varchar(3000) DEFAULT '',

  PRIMARY KEY (order_id),
  UNIQUE INDEX inso_passport_first_recharge_present_return_order_no(order_no),
  UNIQUE INDEX inso_passport_first_recharge_present_return_order_key(order_out_trade_no, order_userid),
  INDEX inso_passport_first_recharge_present_return_order_createtime(order_createtime, order_userid)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci AUTO_INCREMENT=1;

-- ----------------------------
-- 游戏返佣日志-人数
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_passport_return_water_log_count (
  log_id                    int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
  log_userid                int(11) NOT NULL comment 'userid-受益人',
  log_username              varchar(255) NOT NULL comment  '受益人用户名',
  log_level1_count          int(11) NOT NULL DEFAULT 0 comment '1级人数总数',
  log_level2_count          int(11) NOT NULL DEFAULT 0 comment '2级人数总数',

  PRIMARY KEY (log_id),
  UNIQUE INDEX  inso_passport_return_water_log_count_userid(log_userid)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci AUTO_INCREMENT=1;

-- ----------------------------
-- 游戏返佣日志-金额
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_passport_return_water_log_amount (
  log_id                    int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
  log_userid                int(11) NOT NULL comment 'userid-受益人',
  log_username              varchar(255) NOT NULL comment  '受益人用户名',
  log_fund_key              varchar(100) NOT NULL comment 'Sport=现货 | Margin=杠杆 | Futures=合约 | P2P=C2C | Earn=理财 | Pool=矿池| Gift=礼品卡',
  log_currency              varchar(100) NOT NULL comment '币种->USDT|ETH|BTC等',
  log_level1_amount         decimal(25,8) NOT NULL DEFAULT 0 comment '返佣金额',
  log_level2_amount         decimal(25,8) NOT NULL DEFAULT 0 comment '返佣金额',

  PRIMARY KEY (log_id),
  UNIQUE INDEX  inso_passport_return_water_log_amount_userid_fund_currency(log_userid, log_fund_key, log_currency)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci AUTO_INCREMENT=1;

-- ----------------------------
-- 游戏返佣日志-详细
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_passport_return_water_log_detail (
  detail_userid             int(11) NOT NULL comment 'userid-受益人',
  detail_username           varchar(255) NOT NULL comment  '受益人用户名',
  detail_fund_key           varchar(100) NOT NULL comment 'Sport=现货 | Margin=杠杆 | Futures=合约 | P2P=C2C | Earn=理财 | Pool=矿池| Gift=礼品卡',
  detail_currency           varchar(100) NOT NULL comment '币种->USDT|ETH|BTC等',
  detail_childid            int(11) NOT NULL comment 'userid-赠送人用户id',
  detail_childname          varchar(50) NOT NULL comment  '赠送人用户名',
  detail_amount             decimal(25,8) NOT NULL DEFAULT 0 comment '反水金额',
  detail_level              int(11) NOT NULL comment '只有1级|2级',

  PRIMARY KEY (detail_userid, detail_childid, detail_fund_key, detail_currency, detail_level)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci AUTO_INCREMENT=1
partition by hash (detail_userid)
partitions 32;


-- ----------------------------
-- 支付通道
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_pay_channel (
  channel_id                        int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
  channel_name                      varchar(100) NOT NULL comment '' ,
  channel_secret                    varchar(1000) NOT NULL comment '支付通道秘钥相关信息' ,
  channel_status                    varchar(20) NOT NULL DEFAULT 'enable' COMMENT 'enable|disable|test=不对外放可通过个别设置',
  channel_type                      varchar(100) NOT NULL comment '通道类型',
  channel_product_type              varchar(100) NOT NULL comment '产品类型',
  channel_currency_type             varchar(100) NOT NULL DEFAULT '' comment '币种类型',

  channel_feerate                   decimal(25,8) NOT NULL DEFAULT 0 comment '手续费率',
  channel_extra_feemoney            decimal(25,8) NOT NULL DEFAULT 0 comment '额外手续费用',

  channel_sort                      int(11) UNSIGNED NOT NULL DEFAULT 100,
  channel_createtime                datetime NOT NULL ,
  channel_limit_max_amount_of_day   int(11) UNSIGNED NOT NULL DEFAULT 100,
  channel_limit_total_count_of_day  int(11) NOT NULL comment  '每天合计笔数',
  channel_remark                    varchar(255) DEFAULT '',

  PRIMARY KEY (channel_id),
  UNIQUE INDEX inso_pay_channel_name(channel_name),
  INDEX inso_pay_channel_createtime(channel_createtime)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci AUTO_INCREMENT=1;


-- ----------------------------
-- 游戏
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_game (
  game_id                   int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
  game_category_key         varchar(100) NOT NULL comment '' ,
  game_category_name        varchar(100) NOT NULL comment '' ,
  game_key                  varchar(50) DEFAULT NULL comment '' ,
  game_title                varchar(50) NOT NULL comment '' ,
  game_describe             varchar(50) NOT NULL comment '' ,
  game_icon                 varchar(50) NOT NULL comment '' ,
  game_sort                 int(11) UNSIGNED NOT NULL DEFAULT 100 comment '',
  game_status               varchar(20) NOT NULL DEFAULT 'enable' COMMENT 'enable|hidden|disable',
  game_createtime           datetime NOT NULL ,

  PRIMARY KEY (game_id),
  UNIQUE INDEX inso_game_key(game_key),
  INDEX inso_game_category_key(game_category_key),
  INDEX inso_game_createtime(game_createtime)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci AUTO_INCREMENT=1;

-- ----------------------------
-- 游戏-签到任务
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_game_task_checkin_order (
  order_no                     varchar(50) NOT NULL comment '内部系统-订单号',
  order_userid                 int(11) NOT NULL,
  order_username               varchar(255) NOT NULL comment  '',
  order_agentid                int(11) UNSIGNED NOT NULL DEFAULT 0 comment '所属代理id',
  order_staffid                int(11) NOT NULL DEFAULT 0,
  order_amount                 decimal(25,8) NOT NULL comment '赠送总额-从配置读取',
  order_status                 varchar(20) NOT NULL  comment '',
  order_pdate                  date NOT NULL comment '签到日期',
  order_createtime             datetime NOT NULL comment '签到时间',

  PRIMARY KEY (order_no, order_userid),
  UNIQUE INDEX inso_game_task_checkin_order_pdate(order_pdate, order_userid),
  INDEX inso_game_lottery_order_agentid_staffid(order_agentid, order_staffid)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci AUTO_INCREMENT=1
partition by hash (order_userid)
partitions 32;

-- ----------------------------
-- 游戏lottery-期数
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_game_lottery_period (
  period_issue                  varchar(50) NOT NULL comment '期号',
  period_type                   varchar(20) NOT NULL comment '',
  period_gameid                 int(11) NOT NULL comment 'game 唯一id',
  period_total_bet_amount       decimal(25,8) NOT NULL comment '投注总额',
  period_total_win_amount       decimal(25,8) NOT NULL comment '中奖总额',
  period_total_feemoney         decimal(25,8) NOT NULL comment '手续费',
  period_total_bet_count        int(11) NOT NULL DEFAULT 0 comment 'total bet order number',
  period_total_win_count        int(11) NOT NULL DEFAULT 0 comment 'total win order number',
  period_status                 varchar(50) NOT NULL DEFAULT 'pending' comment 'pending|running|finish' ,
  period_open_mode              varchar(50) NOT NULL DEFAULT '0.7' comment  'random|manual|(0 - 1)',
  period_reference_price        varchar(10) NOT NULL DEFAULT '' comment  '参考价格-然后最后一位为开奖结果',
  period_open_result            int(11) NOT NULL DEFAULT -1 comment  '开奖数字',
  period_starttime              datetime NOT NULL comment '开盘时间',
  period_endtime                datetime NOT NULL comment '封盘时间',
  period_createtime             datetime NOT NULL,
  period_updatetime             datetime,
  period_remark                 varchar(3000) DEFAULT '',

  PRIMARY KEY (period_issue),
  INDEX inso_game_lottery_period_gameid(period_gameid),
  INDEX inso_game_lottery_period_starttime(period_starttime, period_type)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci AUTO_INCREMENT=1;

-- ----------------------------
-- 游戏-红绿订单表
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_game_lottery_order (
  order_no                     varchar(50) NOT NULL comment '内部系统-订单号',
  order_issue                  varchar(50) NOT NULL comment '期数',
  order_type                   varchar(20) NOT NULL comment '彩票类型',
  order_bet_item               varchar(50) NOT NULL comment '押注项=数字|green(1-3-7-9)|red(2-4-6-8)|purple(0|5)',
  order_open_result            int(11) NOT NULL DEFAULT -1,

  order_userid                 int(11) NOT NULL,
  order_username               varchar(255) NOT NULL comment  '',
  order_agentid                int(11) NOT NULL DEFAULT 0 comment '所属代理id',
  order_agentname              varchar(255) NOT NULL DEFAULT '' comment  '',
  order_staffid                int(11) NOT NULL DEFAULT 0,
  order_staffname              varchar(255) NOT NULL DEFAULT '' comment  '',

  order_status                 varchar(20) NOT NULL  comment '',
  order_basic_amount           decimal(25,8) NOT NULL comment '基础投注金额',
  order_bet_count              int(11) NOT NULL comment '投注数量金额',
  order_bet_amount             decimal(25,8) NOT NULL comment '投注总金额',
  order_win_amount             decimal(25,8) NOT NULL DEFAULT 0 comment '中奖金额',
  order_feemoney               decimal(25,8) NOT NULL DEFAULT 0 comment '手续费-中奖抽取',
  order_createtime             datetime NOT NULL,
  order_updatetime             datetime DEFAULT NULL,
  order_remark                 varchar(3000) DEFAULT '',

  PRIMARY KEY (order_no, order_userid),
  INDEX inso_game_lottery_order_issue(order_issue),
  INDEX inso_game_lottery_order_createtime_userid(order_createtime, order_userid),
  INDEX inso_game_lottery_order_agentid_staffid(order_agentid, order_staffid)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci AUTO_INCREMENT=1
partition by hash (order_userid)
partitions 32;

-- ----------------------------
-- 游戏lottery-turntable期数
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_game_lottery_v2_turntable_period (
  period_issue                  varchar(100) NOT NULL comment '期号',
  period_show_issue             varchar(100) NOT NULL DEFAULT '' comment '供显示的期号',

  period_type                   varchar(100) NOT NULL comment '',
  period_gameid                 int(11) NOT NULL comment 'game 唯一id',

  period_total_bet_amount       decimal(25,8) NOT NULL comment '投注总额',
  period_total_win_amount       decimal(25,8) NOT NULL comment '中奖总额',
  period_total_win_amount2      decimal(25,8) NOT NULL comment '中奖总额',
  period_total_feemoney         decimal(25,8) NOT NULL comment '手续费',
  period_total_bet_count        int(11) NOT NULL DEFAULT 0 comment 'total bet order number',
  period_total_win_count        int(11) NOT NULL DEFAULT 0 comment 'total win order number',

  period_status                 varchar(50) NOT NULL DEFAULT 'pending' comment 'pending|running|finish' ,

  period_open_mode              varchar(50) NOT NULL DEFAULT '0.7' comment  'random|manual|(0 - 1)',
  period_reference_seed1        varchar(255) NOT NULL DEFAULT '' comment  '参考内部数据1',
  period_reference_seed2        varchar(255) NOT NULL DEFAULT '' comment  '参考内部数据2',
  period_reference_seed3        varchar(255) NOT NULL DEFAULT '' comment  '参考内部数据3',
  period_reference_external     varchar(255) NOT NULL DEFAULT '' comment  '参考外部数据',

  period_open_result            varchar(255) NOT NULL DEFAULT '' comment  '开奖结果',

  period_starttime              datetime NOT NULL comment '开盘时间',
  period_endtime                datetime NOT NULL comment '封盘时间',
  period_createtime             datetime NOT NULL,
  period_updatetime             datetime,
  period_remark                 varchar(3000) DEFAULT '',

  PRIMARY KEY (period_issue),
  INDEX inso_game_lottery_v2_turntable_period_gameid(period_gameid),
  INDEX inso_game_lottery_v2_turntable_period_start_time(period_starttime, period_type)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci AUTO_INCREMENT=1;

-- ----------------------------
-- 游戏lottery-turntable订单表
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_game_lottery_v2_turntable_order (
  order_no                     varchar(50) NOT NULL comment '内部系统-订单号',
  order_issue                  varchar(50) NOT NULL comment '期数',
  order_lottery_type           varchar(100) NOT NULL comment '彩票类型',
  order_bet_item               varchar(50) NOT NULL comment '',
  order_open_result            varchar(255) NOT NULL DEFAULT '',
  order_reference_ext          varchar(255) NOT NULL DEFAULT '',
  order_reference_seed1        varchar(255) NOT NULL DEFAULT '',

  order_userid                 int(11) NOT NULL,
  order_username               varchar(255) NOT NULL comment  '',
  order_usertype               varchar(255) NOT NULL comment  '',

  order_agentid                int(11) NOT NULL DEFAULT 0 comment '所属代理id',
  order_agentname              varchar(255) NOT NULL DEFAULT '' comment  '',
  order_staffid                int(11) NOT NULL DEFAULT 0,
  order_staffname              varchar(255) NOT NULL DEFAULT '' comment  '',

  order_lv1_topuid             int(11) NOT NULL DEFAULT 0,
  order_lv1_topname            varchar(255) NOT NULL DEFAULT '' comment  '',
  order_lv2_tpuid              int(11) NOT NULL DEFAULT 0,
  order_lv2_topname            varchar(255) NOT NULL DEFAULT '' comment  '',
  order_lv3_topuid             int(11) NOT NULL DEFAULT 0,
  order_lv3_topname            varchar(255) NOT NULL DEFAULT '' comment  '',

  order_status                 varchar(20) NOT NULL  comment '',
  order_single_bet_amount      decimal(25,8) NOT NULL comment '单笔下注金额',
  order_total_bet_count        int(11) NOT NULL DEFAULT 1 comment '',
  order_total_bet_amount       decimal(25,8) NOT NULL comment '投注总金额',
  order_win_amount             decimal(25,8) NOT NULL DEFAULT 0 comment '中奖金额',
  order_feemoney               decimal(25,8) NOT NULL DEFAULT 0 comment '手续费-中奖抽取',

  order_createtime             datetime NOT NULL,
  order_updatetime             datetime DEFAULT NULL,
  order_remark                 varchar(3000) DEFAULT '',

  PRIMARY KEY (order_no, order_userid),
  INDEX inso_game_lottery_v2_turntable_order_issue(order_issue),
  INDEX inso_game_lottery_v2_turntable_order_createtime_userid(order_createtime, order_userid)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci AUTO_INCREMENT=1
partition by hash (order_userid)
partitions 4;




-- ----------------------------
-- 游戏lottery-btc期数
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_game_lottery_v2_btc_kline_period (
  period_issue                  varchar(100) NOT NULL comment '期号',
  period_show_issue             varchar(100) NOT NULL DEFAULT '' comment '供显示的期号',

  period_type                   varchar(100) NOT NULL comment '',
  period_gameid                 int(11) NOT NULL comment 'game 唯一id',

  period_total_bet_amount       decimal(25,8) NOT NULL comment '投注总额',
  period_total_win_amount       decimal(25,8) NOT NULL comment '中奖总额',
  period_total_win_amount2      decimal(25,8) NOT NULL comment '中奖总额',

  period_total_feemoney         decimal(25,8) NOT NULL comment '手续费',
  period_total_bet_count        int(11) NOT NULL DEFAULT 0 comment 'total bet order number',
  period_total_win_count        int(11) NOT NULL DEFAULT 0 comment 'total win order number',

  period_status                 varchar(50) NOT NULL DEFAULT 'pending' comment 'pending|running|finish' ,

  period_open_mode              varchar(50) NOT NULL DEFAULT '0.7' comment  'random|manual|(0 - 1)',
  period_reference_seed1        varchar(255) NOT NULL DEFAULT '' comment  '参考内部数据1',
  period_reference_seed2        varchar(255) NOT NULL DEFAULT '' comment  '参考内部数据2',
  period_reference_seed3        varchar(255) NOT NULL DEFAULT '' comment  '参考内部数据3',
  period_reference_external     varchar(255) NOT NULL DEFAULT '' comment  '参考外部数据',
  period_open_result            varchar(255) NOT NULL DEFAULT '' comment  '开奖结果',

  period_starttime              datetime NOT NULL comment '开盘时间',
  period_endtime                datetime NOT NULL comment '封盘时间',
  period_createtime             datetime NOT NULL,
  period_updatetime             datetime,
  period_remark                 varchar(3000) DEFAULT '',

  PRIMARY KEY (period_issue),
  INDEX inso_game_lottery_v2_btc_kline_period_gameid(period_gameid),
  INDEX inso_game_lottery_v2_btc_kline_period_start_time(period_starttime, period_type)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci AUTO_INCREMENT=1;

-- ----------------------------
-- 游戏lottery-btc订单表
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_game_lottery_v2_btc_kline_order (
  order_no                     varchar(50) NOT NULL comment '内部系统-订单号',
  order_issue                  varchar(50) NOT NULL comment '期数',
  order_lottery_type           varchar(100) NOT NULL comment '彩票类型',
  order_bet_item               varchar(50) NOT NULL comment '',
  order_open_result            varchar(255) NOT NULL DEFAULT '',
  order_reference_ext          varchar(255) NOT NULL DEFAULT '',
  order_reference_seed1        varchar(255) NOT NULL DEFAULT '',

  order_userid                 int(11) NOT NULL,
  order_username               varchar(255) NOT NULL comment  '',
  order_usertype               varchar(255) NOT NULL comment  '',

  order_agentid                int(11) NOT NULL DEFAULT 0 comment '所属代理id',
  order_agentname              varchar(255) NOT NULL DEFAULT '' comment  '',
  order_staffid                int(11) NOT NULL DEFAULT 0,
  order_staffname              varchar(255) NOT NULL DEFAULT '' comment  '',

  order_lv1_topuid             int(11) NOT NULL DEFAULT 0,
  order_lv1_topname            varchar(255) NOT NULL DEFAULT '' comment  '',
  order_lv2_tpuid              int(11) NOT NULL DEFAULT 0,
  order_lv2_topname            varchar(255) NOT NULL DEFAULT '' comment  '',
  order_lv3_topuid             int(11) NOT NULL DEFAULT 0,
  order_lv3_topname            varchar(255) NOT NULL DEFAULT '' comment  '',

  order_status                 varchar(20) NOT NULL  comment '',
  order_single_bet_amount      decimal(25,8) NOT NULL comment '单笔下注金额',
  order_total_bet_count        int(11) NOT NULL DEFAULT 1 comment '',
  order_total_bet_amount       decimal(25,8) NOT NULL comment '投注总金额',
  order_win_amount             decimal(25,8) NOT NULL DEFAULT 0 comment '中奖金额',
  order_feemoney               decimal(25,8) NOT NULL DEFAULT 0 comment '手续费-中奖抽取',

  order_createtime             datetime NOT NULL,
  order_updatetime             datetime DEFAULT NULL,
  order_remark                 varchar(3000) DEFAULT '',

  PRIMARY KEY (order_no, order_userid),
  INDEX inso_game_lottery_v2_btc_kline_order_issue(order_issue),
  INDEX inso_game_lottery_v2_btc_kline_order_createtime_userid(order_createtime, order_userid)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci AUTO_INCREMENT=1
partition by hash (order_userid)
partitions 4;


-- ----------------------------
-- 游戏lottery-crash期数
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_game_lottery_v2_rocket_period (
  period_issue                  varchar(100) NOT NULL comment '期号',
  period_show_issue             varchar(100) NOT NULL DEFAULT '' comment '供显示的期号',

  period_type                   varchar(100) NOT NULL comment '',
  period_gameid                 int(11) NOT NULL comment 'game 唯一id',

  period_total_bet_amount       decimal(25,8) NOT NULL comment '投注总额',
  period_total_win_amount       decimal(25,8) NOT NULL comment '中奖总额',
  period_total_win_amount2      decimal(25,8) NOT NULL comment '中奖总额',
  period_total_feemoney         decimal(25,8) NOT NULL comment '手续费',
  period_total_bet_count        int(11) NOT NULL DEFAULT 0 comment 'total bet order number',
  period_total_win_count        int(11) NOT NULL DEFAULT 0 comment 'total win order number',

  period_status                 varchar(50) NOT NULL DEFAULT 'pending' comment 'pending|running|finish' ,

  period_open_mode              varchar(50) NOT NULL DEFAULT '0.7' comment  'random|manual|(0 - 1)',
  period_reference_seed1        varchar(255) NOT NULL DEFAULT '' comment  '参考内部数据1',
  period_reference_seed2        varchar(255) NOT NULL DEFAULT '' comment  '参考内部数据2',
  period_reference_seed3        varchar(255) NOT NULL DEFAULT '' comment  '参考内部数据3',
  period_reference_external     varchar(255) NOT NULL DEFAULT '' comment  '参考外部数据',
  period_open_result            varchar(255) NOT NULL DEFAULT '' comment  '开奖结果',

  period_starttime              datetime NOT NULL comment '开盘时间',
  period_endtime                datetime NOT NULL comment '封盘时间',
  period_createtime             datetime NOT NULL,
  period_updatetime             datetime,
  period_remark                 varchar(3000) DEFAULT '',

  PRIMARY KEY (period_issue),
  INDEX inso_game_lottery_v2_rocket_period_gameid(period_gameid),
  INDEX inso_game_lottery_v2_rocket_period_start_time(period_starttime, period_type)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci AUTO_INCREMENT=1;

-- ----------------------------
-- 游戏lottery-crash期数
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_game_lottery_v2_rocket_order (
  order_no                     varchar(50) NOT NULL comment '内部系统-订单号',
  order_issue                  varchar(50) NOT NULL comment '期数',
  order_lottery_type           varchar(100) NOT NULL comment '彩票类型',
  order_bet_item               varchar(50) NOT NULL comment '',
  order_open_result            varchar(255) NOT NULL DEFAULT '',
  order_reference_ext          varchar(255) NOT NULL DEFAULT '',
  order_reference_seed1        varchar(255) NOT NULL DEFAULT '',

  order_userid                 int(11) NOT NULL,
  order_username               varchar(255) NOT NULL comment  '',
  order_usertype               varchar(255) NOT NULL comment  '',

  order_agentid                int(11) NOT NULL DEFAULT 0 comment '所属代理id',
  order_agentname              varchar(255) NOT NULL DEFAULT '' comment  '',
  order_staffid                int(11) NOT NULL DEFAULT 0,
  order_staffname              varchar(255) NOT NULL DEFAULT '' comment  '',

  order_lv1_topuid             int(11) NOT NULL DEFAULT 0,
  order_lv1_topname            varchar(255) NOT NULL DEFAULT '' comment  '',
  order_lv2_tpuid              int(11) NOT NULL DEFAULT 0,
  order_lv2_topname            varchar(255) NOT NULL DEFAULT '' comment  '',
  order_lv3_topuid             int(11) NOT NULL DEFAULT 0,
  order_lv3_topname            varchar(255) NOT NULL DEFAULT '' comment  '',

  order_status                 varchar(20) NOT NULL  comment '',
  order_single_bet_amount      decimal(25,8) NOT NULL comment '单笔下注金额',
  order_total_bet_count        int(11) NOT NULL DEFAULT 1 comment '',
  order_total_bet_amount       decimal(25,8) NOT NULL comment '投注总金额',
  order_win_amount             decimal(25,8) NOT NULL DEFAULT 0 comment '中奖金额',
  order_feemoney               decimal(25,8) NOT NULL DEFAULT 0 comment '手续费-中奖抽取',

  order_createtime             datetime NOT NULL,
  order_updatetime             datetime DEFAULT NULL,
  order_remark                 varchar(3000) DEFAULT '',

  PRIMARY KEY (order_no, order_userid),
  INDEX inso_game_lottery_v2_rocket_order_issue(order_issue),
  INDEX inso_game_lottery_v2_rocket_order_createtime_userid(order_createtime, order_userid)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci AUTO_INCREMENT=1
partition by hash (order_userid)
partitions 4;

-- ----------------------------
-- 游戏lottery-新红绿期数
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_game_lottery_v2_rg2_period (
  period_issue                  varchar(100) NOT NULL comment '期号',
  period_show_issue             varchar(100) NOT NULL DEFAULT '' comment '供显示的期号',

  period_type                   varchar(100) NOT NULL comment '',
  period_gameid                 int(11) NOT NULL comment 'game 唯一id',

  period_total_bet_amount       decimal(25,8) NOT NULL comment '投注总额',
  period_total_win_amount       decimal(25,8) NOT NULL comment '中奖总额',
  period_total_win_amount2      decimal(25,8) NOT NULL comment '中奖总额',
  period_total_feemoney         decimal(25,8) NOT NULL comment '手续费',
  period_total_bet_count        int(11) NOT NULL DEFAULT 0 comment 'total bet order number',
  period_total_win_count        int(11) NOT NULL DEFAULT 0 comment 'total win order number',

  period_status                 varchar(50) NOT NULL DEFAULT 'pending' comment 'pending|running|finish' ,

  period_open_mode              varchar(50) NOT NULL DEFAULT '0.7' comment  'random|manual|(0 - 1)',
  period_reference_seed1        varchar(255) NOT NULL DEFAULT '' comment  '参考内部数据1',
  period_reference_seed2        varchar(255) NOT NULL DEFAULT '' comment  '参考内部数据2',
  period_reference_seed3        varchar(255) NOT NULL DEFAULT '' comment  '参考内部数据3',
  period_reference_external     varchar(255) NOT NULL DEFAULT '' comment  '参考外部数据',
  period_open_result            varchar(255) NOT NULL DEFAULT '' comment  '开奖结果',

  period_starttime              datetime NOT NULL comment '开盘时间',
  period_endtime                datetime NOT NULL comment '封盘时间',
  period_createtime             datetime NOT NULL,
  period_updatetime             datetime,
  period_remark                 varchar(3000) DEFAULT '',

  PRIMARY KEY (period_issue),
  INDEX inso_game_lottery_v2_rg2_period_gameid(period_gameid),
  INDEX inso_game_lottery_v2_rg2_period_start_time(period_starttime, period_type)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci AUTO_INCREMENT=1;

-- ----------------------------
-- 游戏lottery-新红绿订单
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_game_lottery_v2_rg2_order (
  order_no                     varchar(50) NOT NULL comment '内部系统-订单号',
  order_issue                  varchar(50) NOT NULL comment '期数',
  order_lottery_type           varchar(100) NOT NULL comment '彩票类型',
  order_bet_item               varchar(50) NOT NULL comment '',
  order_open_result            varchar(255) NOT NULL DEFAULT '',
  order_reference_ext          varchar(255) NOT NULL DEFAULT '',
  order_reference_seed1        varchar(255) NOT NULL DEFAULT '',

  order_userid                 int(11) NOT NULL,
  order_username               varchar(255) NOT NULL comment  '',
  order_usertype               varchar(255) NOT NULL comment  '',

  order_agentid                int(11) NOT NULL DEFAULT 0 comment '所属代理id',
  order_agentname              varchar(255) NOT NULL DEFAULT '' comment  '',
  order_staffid                int(11) NOT NULL DEFAULT 0,
  order_staffname              varchar(255) NOT NULL DEFAULT '' comment  '',

  order_lv1_topuid             int(11) NOT NULL DEFAULT 0,
  order_lv1_topname            varchar(255) NOT NULL DEFAULT '' comment  '',
  order_lv2_tpuid              int(11) NOT NULL DEFAULT 0,
  order_lv2_topname            varchar(255) NOT NULL DEFAULT '' comment  '',
  order_lv3_topuid             int(11) NOT NULL DEFAULT 0,
  order_lv3_topname            varchar(255) NOT NULL DEFAULT '' comment  '',

  order_status                 varchar(20) NOT NULL  comment '',
  order_single_bet_amount      decimal(25,8) NOT NULL comment '单笔下注金额',
  order_total_bet_count        int(11) NOT NULL DEFAULT 1 comment '',
  order_total_bet_amount       decimal(25,8) NOT NULL comment '投注总金额',
  order_win_amount             decimal(25,8) NOT NULL DEFAULT 0 comment '中奖金额',
  order_feemoney               decimal(25,8) NOT NULL DEFAULT 0 comment '手续费-中奖抽取',

  order_createtime             datetime NOT NULL,
  order_updatetime             datetime DEFAULT NULL,
  order_remark                 varchar(3000) DEFAULT '',

  PRIMARY KEY (order_no, order_userid),
  INDEX inso_game_lottery_v2_rg2_order_issue(order_issue),
  INDEX inso_game_lottery_v2_rg2_order_createtime_userid(order_createtime, order_userid)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci AUTO_INCREMENT=1
partition by hash (order_userid)
partitions 4;

-- ----------------------------
-- 游戏fruit-期数
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_game_fruit_period (
  period_issue                  varchar(50) NOT NULL comment '期号',
  period_type                   varchar(20) NOT NULL comment '',
  period_gameid                 int(11) NOT NULL comment 'game 唯一id',
  period_total_bet_amount       decimal(25,8) NOT NULL comment '投注总额',
  period_total_win_amount       decimal(25,8) NOT NULL comment '中奖总额',
  period_total_feemoney         decimal(25,8) NOT NULL comment '手续费',
  period_total_bet_count        int(11) NOT NULL DEFAULT 0 comment 'total bet order number',
  period_total_win_count        int(11) NOT NULL DEFAULT 0 comment 'total win order number',
  period_status                 varchar(50) NOT NULL DEFAULT 'pending' comment 'pending|running|finish' ,
  period_open_mode              varchar(50) NOT NULL DEFAULT '0.7' comment  'random|manual|(0 - 1)',
  period_open_result            varchar(10) NOT NULL DEFAULT '' comment  '开奖结果',
  period_starttime              datetime NOT NULL comment '开盘时间',
  period_endtime                datetime NOT NULL comment '封盘时间',
  period_createtime             datetime NOT NULL,
  period_updatetime             datetime,
  period_remark                 varchar(3000) DEFAULT '',

  PRIMARY KEY (period_issue),
  INDEX inso_game_furit_period_gameid(period_gameid),
  INDEX inso_game_furit_period_startime(period_starttime, period_type)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci AUTO_INCREMENT=1;

-- ----------------------------
-- 游戏-fruit订单表
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_game_fruit_order (
  order_no                      varchar(50) NOT NULL comment '内部系统-订单号',
  order_issue                   varchar(50) NOT NULL comment '期数',
  order_type                    varchar(20) NOT NULL comment '彩票类型',
  order_bet_item                varchar(50) NOT NULL comment '押注项=数字|)',
  order_open_result             varchar(10) NOT NULL DEFAULT '',

  order_userid                  int(11) NOT NULL,
  order_username                varchar(255) NOT NULL comment  '',
  order_agentid                 int(11) UNSIGNED NOT NULL DEFAULT 0 comment '所属代理id',
  order_agentname               varchar(255) NOT NULL DEFAULT '' comment  '',
  order_staffid                 int(11) NOT NULL DEFAULT 0,
  order_staffname               varchar(255) NOT NULL DEFAULT '' comment  '',

  order_status                  varchar(20) NOT NULL  comment '',
  order_basic_amount            decimal(25,8) NOT NULL comment '基础投注金额',
  order_bet_count               int(11) NOT NULL comment '投注数量金额',
  order_bet_amount              decimal(25,8) NOT NULL comment '投注总金额',
  order_win_amount              decimal(25,8) NOT NULL DEFAULT 0 comment '中奖金额',
  order_feemoney                decimal(25,8) NOT NULL DEFAULT 0 comment '手续费',
  order_createtime              datetime NOT NULL,
  order_updatetime              datetime DEFAULT NULL,
  order_remark                  varchar(3000) DEFAULT '',

  PRIMARY KEY (order_no, order_userid),
  INDEX inso_game_fruit_order_issue(order_issue),
  INDEX inso_game_fruit_order_createtime_userid(order_createtime, order_userid),
  INDEX inso_game_fruit_order_agentid_staffid(order_agentid, order_staffid)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci AUTO_INCREMENT=1
partition by hash (order_userid)
partitions 32;


-- ----------------------------
-- 游戏andar-bahar-期数
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_game_andar_bahar_period (
  period_issue                varchar(50) NOT NULL comment '期号',
  period_type                 varchar(20) NOT NULL comment '',
  period_gameid               int(11) NOT NULL comment 'game 唯一id',
  period_total_bet_amount     decimal(25,8) NOT NULL comment '投注总额',
  period_total_win_amount     decimal(25,8) NOT NULL comment '中奖总额',
  period_total_feemoney       decimal(25,8) NOT NULL comment '手续费',
  period_total_bet_count      int(11) NOT NULL DEFAULT 0 comment 'total bet order number',
  period_total_win_count      int(11) NOT NULL DEFAULT 0 comment 'total win order number',
  period_status               varchar(50) NOT NULL DEFAULT 'pending' comment 'pending|running|finish' ,
  period_open_mode            varchar(50) NOT NULL DEFAULT '' comment  'random|manual|(0 - 1)',
  period_open_result          varchar(30) NOT NULL DEFAULT '' comment  '开奖结果-Andar|Bahar',
  period_open_card_num        int(11) NOT NULL DEFAULT -1 comment  '翻牌数字',
  period_starttime            datetime NOT NULL comment '开盘时间',
  period_endtime              datetime NOT NULL comment '封盘时间',
  period_createtime           datetime NOT NULL,
  period_updatetime           datetime,
  period_remark               varchar(3000) DEFAULT '',

  PRIMARY KEY (period_issue),
  INDEX inso_game_andar_bahar_period_gameid(period_gameid),
  INDEX inso_game_andar_bahar_period_starttime(period_starttime, period_type)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci AUTO_INCREMENT=1;

-- ----------------------------
-- 游戏-andar_bahar订单表
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_game_andar_bahar_order (
  order_no                      varchar(50) NOT NULL comment '内部系统-订单号',
  order_issue                   varchar(50) NOT NULL comment '期数',
  order_type                    varchar(20) NOT NULL comment '彩票类型',
  order_bet_item                varchar(50) NOT NULL comment '投注项: Andar|Bahar|Tie',
  order_open_result             varchar(50) DEFAULT '' comment '开奖结果: Andar|Bahar|Tie',
  order_userid                  int(11) NOT NULL,
  order_username                varchar(255) NOT NULL comment  '',
  order_agentid                 int(11) UNSIGNED NOT NULL DEFAULT 0 comment '所属代理id',
  order_agentname               varchar(255) NOT NULL DEFAULT '' comment  '',
  order_staffid                 int(11) NOT NULL DEFAULT 0,
  order_staffname               varchar(255) NOT NULL DEFAULT '' comment  '',

  order_status                  varchar(20) NOT NULL  comment '',
  order_basic_amount            decimal(25,8) NOT NULL comment '基础投注金额',
  order_bet_count               int(11) NOT NULL comment '投注数量金额',
  order_bet_amount              decimal(25,8) NOT NULL comment '投注总金额',
  order_win_amount              decimal(25,8) NOT NULL DEFAULT 0 comment '中奖金额',
  order_feemoney                decimal(25,8) NOT NULL DEFAULT 0 comment '手续费-中奖抽取',
  order_createtime              datetime NOT NULL,
  order_updatetime              datetime DEFAULT NULL,
  order_remark                  varchar(3000) DEFAULT '',

  PRIMARY KEY (order_no, order_userid),
  INDEX inso_game_andar_bahar_order_issue(order_issue),
  INDEX inso_game_andar_bahar_order_createtime_userid(order_createtime, order_userid),
  INDEX inso_game_andar_bahar_order_agentid_staffid(order_agentid, order_staffid)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci AUTO_INCREMENT=1
partition by hash (order_userid)
partitions 32;

-- ----------------------------
-- 游戏-sport-football-period 足球赛事
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_game_sport_football_period (
  period_id                         int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  period_gameid                     int(11) NOT NULL comment 'game 唯一id',

  period_category_type              varchar(20) NOT NULL comment '西甲|世界杯',

  period_home_team_name             varchar(255) NOT NULL comment '主队',
  period_away_team_name             varchar(255) NOT NULL comment '主队',

  period_total_bet_amount           decimal(25,8) NOT NULL comment '投注总额',
  period_total_win_amount           decimal(25,8) NOT NULL comment '中奖总额',
  period_total_feemoney             decimal(25,8) NOT NULL comment '手续费',
  period_total_bet_count            int(11) NOT NULL DEFAULT 0 comment 'total bet order number',
  period_total_win_count            int(11) NOT NULL DEFAULT 0 comment 'total win order number',

  period_home_team_goal_handicap    float(5, 1) NOT NULL DEFAULT 0 comment  '主队让球',
  period_away_team_goal_handicap    float(5, 1) NOT NULL DEFAULT 0 comment  '客队让球',

  period_first_home_team_goal       int(11) NOT NULL DEFAULT 0 comment  '上半场主队-得分',
  period_first_away_team_goal       int(11) NOT NULL DEFAULT 0 comment  '上半场客队-得分',
  period_second_home_team_goal      int(11) NOT NULL DEFAULT 0 comment  '下半场主队-得分',
  period_second_away_team_goal      int(11) NOT NULL DEFAULT 0 comment  '下半场客队-得分',

  period_overtime_status            varchar(50) NOT NULL DEFAULT 'diable' comment 'enable|diable' ,
  period_overtime_home_team_goal    int(11) NOT NULL DEFAULT 0 comment  '加时-主队得分',
  period_overtime_away_team_goal    int(11) NOT NULL DEFAULT 0 comment  '加时-客队得分',

  period_status                     varchar(50) NOT NULL DEFAULT 'pending' comment 'pending|running|finish' ,
  period_starttime                  datetime NOT NULL comment '开盘时间',
  period_endtime                    datetime NOT NULL comment '封盘时间',
  period_createtime                 datetime NOT NULL,
  period_updatetime                 datetime,
  period_remark                     varchar(3000) DEFAULT '',

  PRIMARY KEY (period_id),
  INDEX inso_game_sport_football_period_gameid(period_gameid),
  INDEX inso_game_sport_football_period_starttime(period_starttime, period_type)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci AUTO_INCREMENT=1;

-- ----------------------------
-- 红包游戏-期数
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_game_red_package_period (
  period_id                int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
  period_orderno           varchar(50) NOT NULL comment '订单号-退款需要' ,

  period_userid            int(11) NOT NULL DEFAULT 0 comment '0表示系统',
  period_username          varchar(255) NOT NULL ,

  period_agentid           int(11) UNSIGNED NOT NULL DEFAULT 0 comment '所属代理id, 0表示没有',

  period_total_bet_amount  decimal(25,8) NOT NULL DEFAULT 0 comment '投注总额',
  period_total_win_amount  decimal(25,8) NOT NULL DEFAULT 0 comment '中奖总额',
  period_total_feemoney    decimal(25,8) NOT NULL DEFAULT 0 comment '手续费',
  period_total_bet_count   int(11) NOT NULL DEFAULT 0 comment 'total bet order number',
  period_total_win_count   int(11) NOT NULL DEFAULT 0 comment 'total win order number',
  order_open_result        int(11) NOT NULL DEFAULT -1,
  period_open_mode         varchar(50) DEFAULT '' comment '开奖模式',

  period_total_amount      decimal(25,8) NOT NULL comment '红包总金额',
  period_min_amount        decimal(25,8) NOT NULL comment '红包最小金额',
  period_max_amount        decimal(25,8) NOT NULL comment '红包最大金额',
  period_total_count       int(11) NOT NULL DEFAULT 0 comment '红包总个数',
  period_complete_count    int(11) NOT NULL DEFAULT 0 comment '红包已领取个数',
  period_complete_amount   decimal(25,8) NOT NULL DEFAULT 0 comment '红包已领取总金额',
  period_rp_type           varchar(20) NOT NULL comment '红包类型',
  period_status            varchar(20) NOT NULL comment '状态',

  period_creator_type      varchar(20) NOT NULL comment '创建主体=sys|agent|member',

  period_createtime        datetime NOT NULL ,
  period_endtime           datetime NOT NULL ,
  period_remark            varchar(1000) NOT NULL,

  PRIMARY KEY (period_id, period_userid),
  UNIQUE inso_game_red_package_period_orderno(period_orderno, period_userid),
  INDEX inso_game_red_package_period_agentid(period_agentid),
  INDEX inso_game_red_package_period_createtime(period_createtime, period_userid),
  INDEX inso_game_red_package_period_endtime(period_endtime)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci AUTO_INCREMENT=1
partition by hash (period_userid)
partitions 32;

-- ----------------------------
-- 红包游戏-领取订单表
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_game_red_package_receive_order (
  order_no                      varchar(50) NOT NULL comment '内部系统-订单号',
  order_rpid                    int(11) NOT NULL comment '红包id',
  order_userid                  int(11) NOT NULL,
  order_username                varchar(255) NOT NULL comment  '',
  order_rp_type                 varchar(50) NOT NULL comment  '红包类型',
  order_agentid                 int(11) UNSIGNED NOT NULL DEFAULT 0 comment '所属代理id',
  order_agentname               varchar(255) NOT NULL DEFAULT '' comment  '',
  order_staffid                 int(11) NOT NULL DEFAULT 0,
  order_staffname               varchar(255) NOT NULL DEFAULT '' comment  '',

  order_status                  varchar(20) NOT NULL  comment '',
  order_amount                  decimal(25,8) NOT NULL comment '领取金额',
  order_index                   int(11) NOT NULL comment '领取顺序',
  order_createtime              datetime NOT NULL,
  order_updatetime              datetime DEFAULT NULL,
  order_remark                  varchar(1000) DEFAULT '',

  PRIMARY KEY (order_no, order_userid),
  UNIQUE INDEX inso_game_red_package_receive_order_rpid_userid(order_rpid, order_userid),
  INDEX inso_game_red_package_receive_order_createtime(order_createtime, order_userid),
  INDEX inso_game_red_package_receive_order_agentid_staffid(order_agentid, order_staffid)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci AUTO_INCREMENT=1
partition by hash (order_userid)
partitions 32;

-- ----------------------------
-- 红包配置
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_game_red_package_staff_limit (
  limit_id                          int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,

  limit_agentid                     int(11)  NOT NULL ,
  limit_agentname                   varchar(50) NOT NULL ,
  limit_staffid                     int(11)  NOT NULL ,
  limit_staffname                   varchar(50) NOT NULL ,

  limit_max_money_of_single         decimal(25,8) NOT NULL comment '单笔最大金额',
  limit_max_money_of_day            decimal(25,8) NOT NULL comment '每天最大金额',
  limit_max_count_of_day            int(11) NOT NULL comment '每天发送金额次数',

  limit_createtime                  datetime NOT NULL,
  limit_status                      varchar(20) NOT NULL DEFAULT 'enable' COMMENT 'enable|disable',
  limit_remark                      varchar(500) NOT NULL DEFAULT '' COMMENT '',

  PRIMARY KEY (limit_id),
  UNIQUE INDEX inso_game_red_package_staff_limit_agentid_staffid(limit_agentid, limit_staffid),
  UNIQUE INDEX inso_game_red_package_staff_limit_staffid(limit_staffid),
  INDEX inso_game_red_package_staff_limit_createtime(limit_createtime)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci AUTO_INCREMENT=1;


-- ----------------------------
-- 游戏-理财产品
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_game_financial_management_product (
  product_id                        int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
  product_title                     varchar(20) NOT NULL comment '标题',
  product_desc                      varchar(100) NOT NULL comment '介绍',
  product_time_horizon              int(11) NOT NULL comment ' 投资期限（1日，3日，7日）',

  product_type                      varchar(20) NOT NULL comment '产品类型',

  product_return_expected_start     decimal(25,8) NOT NULL comment '预期收益范围start',
  product_return_expected_end       decimal(25,8) NOT NULL comment '预期收益范围 end',
  product_return_real_rate          decimal(25,8) NOT NULL comment '实际收益率',
  product_return_real_interest      decimal(25,8) NOT NULL DEFAULT 0 comment '实际利息总支出',

  product_sale_estimate             int(11) NOT NULL DEFAULT 0 comment '预售总份额',
  product_sale_real                 int(11) NOT NULL DEFAULT 0 comment '实际总份额',
  product_sale_actual               int(11) NOT NULL DEFAULT 0 comment '实际已售份额',

  product_limit_min_sale            int(11) NOT NULL DEFAULT 0 comment '限售最小额度',
  product_limit_max_sale            int(11) NOT NULL DEFAULT 0 comment '限售最大额度',
  product_limit_min_bets            int(11) NOT NULL DEFAULT 0 comment '最低投注额',
  product_limit_min_balance         decimal(25,8) NOT NULL comment '最低帐户余额',

  product_status                    varchar(50) NOT NULL comment 'new=草稿 | saling=销售中 | saled=已售磬 | finish=结束' ,

  product_createtime                datetime NOT NULL comment '创建时间',
  product_begin_sale_time           datetime NOT NULL comment '开售时间',
  product_end_sale_time             datetime NOT NULL comment '停售时间',
  product_endtime                   datetime NOT NULL comment '结束时间',
  product_remark                    varchar(1000) DEFAULT '',

  PRIMARY KEY (product_id),
  INDEX inso_game_financial_management_product_endtime(product_endtime),
  INDEX inso_game_financial_management_product_begin_sale_time(product_begin_sale_time)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci AUTO_INCREMENT=1;


-- ----------------------------
-- 游戏-理财产品-销售记录
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_game_financial_management_order (
  order_no                      varchar(50) NOT NULL comment '内部系统-订单号',
  order_fmid                    int(11) NOT NULL comment '理解产品id',

  order_fm_type                 varchar(20) NOT NULL comment '产品类型',

  order_userid                  int(11) NOT NULL,
  order_username                varchar(255) NOT NULL comment  '',
  order_agentid                 int(11) NOT NULL comment '所属代理id',
  order_agentname               varchar(255) NOT NULL DEFAULT '' comment  '',
  order_staffid                 int(11) NOT NULL DEFAULT 0,
  order_staffname               varchar(255) NOT NULL DEFAULT '' comment  '',

  order_status                  varchar(20) NOT NULL  comment '',

  order_return_real_rate        decimal(25,8) NOT NULL comment '实际收益率',
  order_buy_amount              decimal(25,8) NOT NULL comment '认购金额',
  order_return_expected_amount  decimal(25,8) NOT NULL comment '预期收益金额',
  order_return_real_amount      decimal(25,8) NOT NULL DEFAULT 0 comment '实际收益金额',

  order_feemoney                decimal(25,8) NOT NULL DEFAULT 0 comment '手续费',

  order_createtime              datetime NOT NULL comment '购买时间',
  order_endtime                 datetime NOT NULL comment '赎回时间',
  order_updatetime              datetime DEFAULT NULL comment '结束时间',
  order_remark                  varchar(1000) DEFAULT '',

  PRIMARY KEY (order_no, order_userid),
  INDEX inso_game_financial_management_order_fmid(order_fmid, order_userid),
  INDEX inso_game_financial_management_order_agentid_staffid(order_agentid, order_staffid),
  INDEX inso_game_financial_management_order_createtime_userid(order_createtime, order_userid),
  INDEX inso_game_financial_management_order_endtime(order_endtime)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci AUTO_INCREMENT=1
partition by hash (order_userid)
partitions 32;

-- ----------------------------
-- 报表-用户每日统计
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_report_passport_user_day (
  day_pdate                 date NOT NULL ,
  day_userid                int(11) NOT NULL comment 'userid',
  day_username              varchar(255) NOT NULL comment  '',
  day_agentid               int(11) NOT NULL DEFAULT 0 comment '所属代理id',
  day_staffid               int(11) NOT NULL DEFAULT 0,

  day_fund_key              varchar(50) NOT NULL comment 'Sport=现货 | Margin=杠杆 | Futures=合约 | P2P=C2C | Earn=理财 | Pool=矿池| Gift=礼品卡',
  day_currency              varchar(50) NOT NULL comment '币种->USDT|ETH|BTC等',

  day_recharge              decimal(25,8) NOT NULL DEFAULT 0 comment '充值金额',
  day_first_recharge        varchar(50) NOT NULL DEFAULT 'disable' ,

  day_refund                decimal(25,8) NOT NULL DEFAULT 0 comment '退款金额-保留字段',
  day_withdraw              decimal(25,8) NOT NULL DEFAULT 0 comment '提现金额'
  day_feemoney              decimal(25,8) NOT NULL DEFAULT 0 comment '手续费-提现才有',

  day_business_recharge     decimal(25,8) NOT NULL DEFAULT 0 comment '业务充值-如中奖',
  day_business_deduct       decimal(25,8) NOT NULL DEFAULT 0 comment '业务扣款-如投注',
  day_business_feemoney     decimal(25,8) NOT NULL DEFAULT 0 comment '投注手续费',

  day_finance_recharge      decimal(25,8) NOT NULL DEFAULT 0 comment '金额充值-如理财收益',
  day_finance_deduct        decimal(25,8) NOT NULL DEFAULT 0 comment '金额扣款-如购买理财',
  day_finance_feemoney      decimal(25,8) NOT NULL DEFAULT 0 comment '金额手续费-手续费用',

  day_platform_recharge     decimal(25,8) NOT NULL DEFAULT 0 comment '平台充值',
  day_platform_presentation decimal(25,8) NOT NULL DEFAULT 0 comment '平台赠送',
  day_platform_deduct       decimal(25,8) NOT NULL DEFAULT 0 comment '平台扣款',

  day_return_water          decimal(25,8) NOT NULL DEFAULT 0 comment '返佣金额',

  day_lv1_recharge          decimal(25,8) NOT NULL DEFAULT 0 comment '一级下级充值总额',
  day_lv1_withdraw          decimal(25,8) NOT NULL DEFAULT 0 comment '一级下级提现总额',
  day_lv2_recharge          decimal(25,8) NOT NULL DEFAULT 0 comment '二级下级充值总额',
  day_lv2_withdraw          decimal(25,8) NOT NULL DEFAULT 0 comment '二级下级提现总额',

  day_remark                varchar(1000) NOT NULL DEFAULT '' comment '',

  PRIMARY KEY (day_pdate, day_userid, day_fund_key, day_currency),
  INDEX inso_report_passport_user_day_username(day_username),
  INDEX inso_report_passport_user_day_agentid_staffid(day_agentid, day_staffid)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci AUTO_INCREMENT=1
partition by hash (day_userid)
partitions 8;

-- ----------------------------
-- 报表-平台每日统计
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_report_platform_day (
  day_pdate                 date NOT NULL ,

  day_fund_key              varchar(50) NOT NULL comment 'Sport=现货 | Margin=杠杆 | Futures=合约 | P2P=C2C | Earn=理财 | Pool=矿池| Gift=礼品卡',
  day_currency              varchar(50) NOT NULL comment '币种->USDT|ETH|BTC等',

  day_recharge              decimal(25,8) NOT NULL DEFAULT 0 comment '充值金额',
  day_refund                decimal(25,8) NOT NULL DEFAULT 0 comment '退款金额-保留字段',
  day_withdraw              decimal(25,8) NOT NULL DEFAULT 0 comment '提现金额',
  day_feemoney              decimal(25,8) NOT NULL DEFAULT 0 comment '手续费-提现才有',

  day_business_recharge     decimal(25,8) NOT NULL DEFAULT 0 comment '业务充值-如中奖',
  day_business_deduct       decimal(25,8) NOT NULL DEFAULT 0 comment '业务扣款-如投注',
  day_business_feemoney     decimal(25,8) NOT NULL DEFAULT 0 comment '投注手续费',

  day_finance_recharge      decimal(25,8) NOT NULL DEFAULT 0 comment '金额充值-如理财收益',
  day_finance_deduct        decimal(25,8) NOT NULL DEFAULT 0 comment '金额扣款-如购买理财',
  day_finance_feemoney      decimal(25,8) NOT NULL DEFAULT 0 comment '金额手续费-手续费用',

  day_platform_recharge     decimal(25,8) NOT NULL DEFAULT 0 comment '平台充值',
  day_platform_presentation decimal(25,8) NOT NULL DEFAULT 0 comment '平台赠送',
  day_platform_deduct       decimal(25,8) NOT NULL DEFAULT 0 comment '平台扣款',

  day_return_water          decimal(25,8) NOT NULL DEFAULT 0 comment '返佣金额',
  day_remark                varchar(1000) NOT NULL DEFAULT '' comment '',

  PRIMARY KEY (day_pdate, day_fund_key, day_currency)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci AUTO_INCREMENT=1;


-- ----------------------------
-- 报表-业务每日统计
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_report_business_day (
  day_pdate                     date NOT NULL ,
  day_key                       varchar(50) NOT NULL comment '业务唯一编码',
  day_title                     varchar(50) NOT NULL comment  '业务名称',
  day_bet_amount                decimal(25,8) DEFAULT 0 NOT NULL comment '金额',
  day_bet_count                 int(11) DEFAULT 0 NOT NULL comment '',
  day_win_amount                decimal(25,8) DEFAULT 0 NOT NULL comment '金额',
  day_win_amount2               decimal(25,8) DEFAULT 0 NOT NULL comment '金额',
  day_win_count                 int(11) DEFAULT 0 NOT NULL comment '',
  day_feemoney                  decimal(25,8) DEFAULT 0 NOT NULL comment '手续费-提现才有',
  day_remark                    varchar(1000) NOT NULL DEFAULT '' comment  '',

  PRIMARY KEY (day_pdate, day_key)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci AUTO_INCREMENT=1;

-- ----------------------------
-- 报表-业务每日统计-到员工
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_report_game_business_day (
  day_pdate                    date NOT NULL ,
  day_agentid                  int(11) NOT NULL DEFAULT 0 comment '所属代理id',
  day_agentname                varchar(50) NOT NULL comment '所属代理',
  day_staffid                  int(11) NOT NULL DEFAULT 0,
  day_staffname                varchar(50) NOT NULL,

  day_business_code            int(11) NOT NULL comment '业务唯一编码',
  day_business_name            varchar(50) NOT NULL comment  '业务名称',

  day_bet_amount               decimal(25,8) DEFAULT 0 NOT NULL comment '金额',
  day_bet_count                int(11) DEFAULT 0 NOT NULL comment '',
  day_win_amount               decimal(25,8) DEFAULT 0 NOT NULL comment '金额',
  day_win_count                int(11) DEFAULT 0 NOT NULL comment '',
  day_feemoney                 decimal(25,8) DEFAULT 0 NOT NULL comment '业务手续费',

  PRIMARY KEY (day_pdate, day_agentid, day_staffid, day_business_code)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci AUTO_INCREMENT=1
partition by hash (day_staffid)
partitions 32;

-- ----------------------------
-- 报表-用户运营数-状态| 新增人数|分裂人数|充值人数|提现人数
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_report_passport_user_status_day (
  day_pdate                 date NOT NULL ,
  day_agentid               int(11) NOT NULL DEFAULT 0 comment '所属代理id',
  day_agentname             varchar(50) NOT NULL comment '所属代理',
  day_staffid               int(11) NOT NULL DEFAULT 0,
  day_staffname             varchar(50) NOT NULL,

  day_register_count        int(11) NOT NULL DEFAULT 0 comment '注册人数',
  day_split_count           int(11) NOT NULL DEFAULT 0 comment '分裂人数',
  day_active_count          int(11) NOT NULL DEFAULT 0 comment '活跃人数',

  day_total_recharge_count  int(11) NOT NULL DEFAULT 0 comment '充值总次数',
  day_user_recharge_count   int(11) NOT NULL DEFAULT 0 comment '充值人数',
  day_total_recharge_amount decimal(25,8) NOT NULL DEFAULT 0 comment '',

  day_total_withdraw_count    int(11) NOT NULL DEFAULT 0 comment '提现总次数',
  day_user_withdraw_count     int(11) NOT NULL DEFAULT 0 comment '提现人数',
  day_total_withdraw_amount   decimal(25,8) NOT NULL DEFAULT 0 comment '',
  day_total_withdraw_feemoney decimal(25,8) NOT NULL DEFAULT 0 comment '',

  day_first_recharge_count  int(11) NOT NULL DEFAULT 0 comment '首充人数',
  day_first_recharge_amount decimal(25,8) DEFAULT 0 NOT NULL comment '首充金额',

  PRIMARY KEY (day_pdate, day_agentid, day_staffid)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci AUTO_INCREMENT=1
partition by hash (day_staffid)
partitions 32;


-- ----------------------------
-- 报表-数据分析-在线时长
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_report_data_analysis_user_active_stats_day (
  day_pdate                 date NOT NULL ,
  day_hours                 int(11) NOT NULL DEFAULT 0 comment '24小时制',

  day_userid                int(11) NOT NULL DEFAULT 0,
  day_username              varchar(255) NOT NULL,
  day_agentid               int(11) NOT NULL DEFAULT 0 comment '所属代理id',
  day_agentname             varchar(50) NOT NULL comment '所属代理',
  day_staffid               int(11) NOT NULL DEFAULT 0,
  day_staffname             varchar(50) NOT NULL,

  day_online_duration       int(11) NOT NULL DEFAULT 0 comment '停留应用总时长',
  day_stay_rg_duration      int(11) NOT NULL DEFAULT 0 comment '停留RG游戏总时长',
  day_stay_ab_duration      int(11) NOT NULL DEFAULT 0 comment '停留AB游戏总时长',
  day_stay_fruit_duration   int(11) NOT NULL DEFAULT 0 comment '停留水果机游戏总时长',
  day_stay_fm_duration      int(11) NOT NULL DEFAULT 0 comment '停留理财总时长',
  day_remark                varchar(1000) NOT NULL DEFAULT '' comment  '',

  PRIMARY KEY (day_pdate, day_userid, day_hours),
  INDEX inso_report_data_analysis_user_active_stats_day_agentid_staffid(day_agentid, day_staffid)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci AUTO_INCREMENT=1
partition by hash (day_userid)
partitions 32;

-- ----------------------------
-- 报表-数据分析-事件统计
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_report_data_analysis_event_stats_day (
  day_pdate              date NOT NULL ,
  day_hours              int(11) NOT NULL DEFAULT 0 comment '24小时制',

  day_userid             int(11) NOT NULL DEFAULT 0,
  day_username           varchar(255) NOT NULL,
  day_agentid            int(11) NOT NULL DEFAULT 0 comment '所属代理id',
  day_agentname          varchar(50) NOT NULL comment '所属代理',
  day_staffid            int(11) NOT NULL DEFAULT 0,
  day_staffname          varchar(50) NOT NULL,

  day_name               varchar(100) NOT NULL comment '事件名称',
  day_count              int(11) NOT NULL DEFAULT 0 comment '次数',
  day_remark             varchar(1000) NOT NULL DEFAULT '' comment  '',

  PRIMARY KEY (day_pdate, day_userid, day_hours),
  INDEX inso_report_data_analysis_event_stats_day_agentid_staffid(day_agentid, day_staffid)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci AUTO_INCREMENT=1
partition by hash (day_userid)
partitions 32;

-- ----------------------------
-- 客服组
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_web_kefu_group (
  group_id                int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
  group_name              varchar(100) NOT NULL ,
  group_describe          varchar(255) NOT NULL,
  group_icon              varchar(255) NOT NULL DEFAULT '',
  group_createtime        datetime NOT NULL,
  group_status            varchar(20) NOT NULL DEFAULT 'enable' COMMENT 'enable|disable',
  group_remark            varchar(1000) NOT NULL DEFAULT '' COMMENT '',

  PRIMARY KEY (group_id),
  UNIQUE INDEX inso_web_kefu_group_name(group_name),
  INDEX inso_web_kefu_group_name_createtime(group_createtime)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci AUTO_INCREMENT=1;

-- ----------------------------
-- 客服人员
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_web_kefu_member (
  member_id                 int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
  member_name               varchar(100) NOT NULL ,
  member_title              varchar(255) NOT NULL,
  member_describe           varchar(255) NOT NULL,
  member_icon               varchar(255) NOT NULL DEFAULT '',
  member_whatsapp           varchar(255) NOT NULL DEFAULT '',
  member_telegram           varchar(255) NOT NULL DEFAULT '',
  member_groupid            int(11) NOT NULL ,
  member_createtime         datetime NOT NULL,
  member_status             varchar(20) NOT NULL DEFAULT 'enable' COMMENT 'enable|disable',
  member_remark             varchar(2000) NOT NULL DEFAULT '' COMMENT '',
  PRIMARY KEY (member_id),
  UNIQUE INDEX inso_web_kefu_member_name(member_name),
  INDEX inso_web_kefu_member_groupid(member_groupid),
  INDEX inso_web_kefu_member_whatsapp(member_whatsapp),
  INDEX inso_web_kefu_member_telegram(member_telegram),
  INDEX inso_web_kefu_member_createtime(member_createtime)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci AUTO_INCREMENT=1;

-- ----------------------------
-- 员工专属客服-自己会员自己负责
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_web_staff_kefu (
  kefu_id                 int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,

  kefu_agentid            int(11)  NOT NULL ,
  kefu_agentname          varchar(50) NOT NULL ,
  kefu_staffid            int(11)  NOT NULL ,
  kefu_staffname          varchar(50) NOT NULL ,

  kefu_title              varchar(100) NOT NULL,
  kefu_describe           varchar(255) NOT NULL,
  kefu_icon               varchar(255) NOT NULL DEFAULT '',
  kefu_whatsapp           varchar(255) NOT NULL DEFAULT '',
  kefu_telegram           varchar(255) NOT NULL DEFAULT '',
  kefu_createtime         datetime NOT NULL,
  kefu_status             varchar(20) NOT NULL DEFAULT 'enable' COMMENT 'enable|disable',
  kefu_remark             varchar(2000) NOT NULL DEFAULT '' COMMENT '',
  PRIMARY KEY (kefu_id),
  INDEX inso_web_staff_kefu_agentid_staffid(kefu_agentid, kefu_staffid),
  INDEX inso_web_staff_kefu_staffid(kefu_staffid,kefu_describe),
  INDEX inso_web_staff_kefu_createtime(kefu_createtime)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci AUTO_INCREMENT=1;

-- ----------------------------
-- web-代理专属公告
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_web_agent_tips (
    tips_id                 int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,

    tips_belong_agentid     int(11)  NOT NULL DEFAULT '' ,
    tips_belong_agentname   varchar(50) NOT NULL DEFAULT '' comment '' ,
    tips_staffid            int(11)  NOT NULL DEFAULT '' ,
    tips_staffname          varchar(50) NOT NULL DEFAULT '' comment '' ,

    tips_agentid            int(11)  NOT NULL DEFAULT 0,
    tips_agentname          varchar(50) NOT NULL DEFAULT '',
    tips_title              varchar(255) NOT NULL DEFAULT '' comment '标题',
    tips_content            varchar(5000) NOT NULL comment '描述',
    tips_type               varchar(255) NOT NULL DEFAULT '' comment '类型',

    tips_createtime         datetime NOT NULL,
    tips_status             varchar(20) NOT NULL DEFAULT 'enable' COMMENT 'enable|disable',
    tips_remark             varchar(2000) NOT NULL DEFAULT '' COMMENT '',

    PRIMARY KEY ( tips_id),
    INDEX inso_web_agent_tips_agentid(tips_agentid),
    INDEX inso_web_agent_tips_createtime(tips_createtime )
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci AUTO_INCREMENT=1;



-- ----------------------------
-- web-代理telegram发送消息
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_web_agent_tgsms (
    tgsms_id                 int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,

    tgsms_staffid            int(11)  NOT NULL DEFAULT 0 ,
    tgsms_staffname          varchar(50) NOT NULL DEFAULT '' comment '' ,
    tgsms_agentid            int(11)  NOT NULL DEFAULT 0,
    tgsms_agentname          varchar(50) NOT NULL DEFAULT '',

    tgsms_rbtoken            varchar(255) NOT NULL DEFAULT '' comment '机器人token ',
    tgsms_chatid             varchar(255) NOT NULL comment 'tg群chatid ',
    tgsms_type               varchar(255) NOT NULL DEFAULT '' comment '类型',

    tgsms_createtime         datetime NOT NULL,
    tgsms_status             varchar(20) NOT NULL DEFAULT 'enable' COMMENT 'enable|disable',
    tgsms_remark             varchar(2000) NOT NULL DEFAULT '' COMMENT '',
    PRIMARY KEY ( tgsms_id),
    INDEX inso_web_agent_tgsms_agentid(tgsms_id),
    INDEX inso_web_agent_tgsms_staffname(tgsms_staffname),
    INDEX inso_web_agent_tgsms_createtime(tgsms_createtime )
    ) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci AUTO_INCREMENT=1;




-- ----------------------------
-- 员工专属反馈-自己会员自己负责
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_web_feedback (
  feedback_id                int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,

  feedback_agentid           int(11)  NOT NULL ,
  feedback_agentname         varchar(50) NOT NULL ,
  feedback_staffid           int(11)  NOT NULL ,
  feedback_staffname         varchar(50) NOT NULL ,

  feedback_userid            int(11)  NOT NULL comment '会员id',
  feedback_username          varchar(255) NOT NULL comment '会员用户名',

  feedback_type              varchar(20) NOT NULL comment '问题类型',
  feedback_title             varchar(100) NOT NULL,
  feedback_content           varchar(5000) NOT NULL,
  feedback_reply             varchar(5000) NOT NULL comment '回复内容',
  feedback_createtime        datetime NOT NULL,
  feedback_status            varchar(20) NOT NULL DEFAULT 'waiting' COMMENT 'waiting|finish',
  feedback_remark            varchar(500) NOT NULL DEFAULT '' COMMENT '',
  PRIMARY KEY (feedback_id, feedback_userid),
  INDEX inso_web_feedback_agentid_staffid(feedback_agentid, feedback_staffid, feedback_userid),
  INDEX inso_web_feedback_createtime(feedback_createtime, feedback_userid)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci AUTO_INCREMENT=1
partition by hash (feedback_userid)
partitions 32;

-- ----------------------------
-- web-轮播图
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_web_banner (
  banner_id              int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,

  banner_title           varchar(100) NOT NULL comment '标题',
  banner_content         varchar(255) NOT NULL comment '描述',

  banner_type            varchar(255) NOT NULL comment '类型=ad|game_ab|game_rg|game_fruit|game_fm|game_redpackage等等',
  banner_img             varchar(255) NOT NULL comment '图片',
  banner_web_url         varchar(255) NOT NULL comment '跳转地址',

  banner_force_login     varchar(20) NOT NULL default 'disable' COMMENT '是否强制登陆: enale|disable',
  banner_status          varchar(20) NOT NULL COMMENT 'enale|disable',

  banner_admin           varchar(20) NOT NULL default 'disable' COMMENT '操作人|审核人',
  banner_createtime      datetime NOT NULL,
  banner_updatetime      datetime NOT NULL,
  banner_remark          varchar(3000) NOT NULL DEFAULT '',

  PRIMARY KEY (banner_id),
  INDEX inso_web_banner_createtime(banner_createtime)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci AUTO_INCREMENT=1;


-- ----------------------------
-- 事件日志
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_web_event_log (
  log_id              int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,

  log_type            varchar(255) NOT NULL comment '类型',
  log_title           varchar(100) NOT NULL default '' comment '标题',
  log_content         varchar(255) NOT NULL default '' comment '内容',

  log_ip              varchar(100) NOT NULL default '' comment 'IP',
  log_useragent       varchar(500) NOT NULL default '' comment 'user-agent',

  log_agentid         int(11) NOT NULL,
  log_agentname       varchar(255) NOT NULL default '' COMMENT '所属代理',
  log_operator        varchar(255) NOT NULL default '' COMMENT '操作人',

  log_createtime      datetime NOT NULL,
  log_remark          varchar(3000) NOT NULL DEFAULT '',

  PRIMARY KEY (log_id, log_agentid),
  INDEX inso_web_event_log_agentid(log_agentid),
  INDEX inso_web_event_log_createtime(log_createtime)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci AUTO_INCREMENT=1
partition by hash (log_agentid)
partitions 32;

-- ----------------------------
-- 会员事件日志
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_web_event_member_log (
  log_id              int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,

  log_type            varchar(255) NOT NULL comment '类型',
  log_title           varchar(100) NOT NULL default '' comment '标题',
  log_content         varchar(255) NOT NULL default '' comment '内容',

  log_ip              varchar(100) NOT NULL default '' comment 'IP',
  log_useragent       varchar(500) NOT NULL default '' comment 'user-agent',

  log_agentid         int(11) NOT NULL,
  log_agentname       varchar(255) NOT NULL default '' COMMENT '所属代理',
  log_operator        varchar(255) NOT NULL default '' COMMENT '操作人',

  log_createtime      datetime NOT NULL,
  log_remark          varchar(3000) NOT NULL DEFAULT '',

  PRIMARY KEY (log_id, log_agentid),
  INDEX inso_web_event_member_log_agentid(log_agentid),
  INDEX inso_web_event_member_log_createtime(log_createtime)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci AUTO_INCREMENT=1
partition by hash (log_agentid)
partitions 32;


-- ----------------------------
-- 结算订单
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_web_settle_order (
  order_no                     varchar(255) NOT NULL comment '内部系统-订单号',
  order_out_trade_no           varchar(255) NOT NULL DEFAULT '' comment '引用外部订单号,如果有',
  order_checker                varchar(50) NOT NULL DEFAULT '' comment  '审核人',

  order_userid                 int(11) NOT NULL,
  order_username               varchar(255) NOT NULL comment  '',
  order_agentid                int(11) UNSIGNED NOT NULL DEFAULT 0 comment '所属代理id',
  order_agentname              varchar(255) NOT NULL comment  '',
  order_staffid                int(11) NOT NULL DEFAULT 0,
  order_staffname              varchar(255) NOT NULL comment  '',

  order_transfer_no            varchar(255) NOT NULL DEFAULT '' comment '引用外部订单号,如果有',
  order_transfer_amount        decimal(25,8) NOT NULL comment '划转总额',
  order_settle_status          varchar(20) NOT NULL DEFAULT '',

  order_network_type           varchar(50) NOT NULL comment '',
  order_currency               varchar(50) NOT NULL comment '币种->USDT|ETH|BTC等',

  order_business_type          varchar(20) NOT NULL comment '业务类型',
  order_status                 varchar(20) NOT NULL  comment 'new=待支付 | captured=上游已完成状态-对应我们此时状态 | realized=处理成功 | error=失败',
  order_amount                 decimal(25,8) NOT NULL comment '流水金额',
  order_feemoney               decimal(25,8) NOT NULL comment '手续费',
  order_beneficiary_account    varchar(255) NOT NULL DEFAULT '' comment  '受益人账户',
  order_createtime             datetime NOT NULL,
  order_updatetime             datetime DEFAULT NULL,
  order_remark                 varchar(3000) DEFAULT '',

  PRIMARY KEY (order_no, order_userid),
  UNIQUE INDEX inso_web_settle_order_out_trade_no(order_out_trade_no, order_business_type, order_userid),
  INDEX inso_web_settle_order_transfer_no(order_transfer_no),
  INDEX inso_web_settle_order_createtime_userid(order_createtime, order_userid),
  INDEX inso_web_settle_order_beneficiary_account(order_beneficiary_account)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci AUTO_INCREMENT=1
partition by hash (order_userid)
partitions 32;

-- ----------------------------
-- 结算记录
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_web_settle_record (
  record_id                    int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
  record_pdate                 date NOT NULL ,
  record_business_type         varchar(20) NOT NULL comment '业务类型',

  record_agentid               int(11) NOT NULL DEFAULT 0 comment '所属代理id',
  record_agentname             varchar(255) NOT NULL comment  '',
  record_staffid               int(11) NOT NULL DEFAULT 0,
  record_staffname             varchar(255) NOT NULL comment  '',

  record_currency              varchar(50) NOT NULL comment '币种->USDT|ETH|BTC等',

  record_amount                decimal(25,8) NOT NULL comment '流水金额',
  record_feemoney              decimal(25,8) NOT NULL comment '手续费',
  record_remark                varchar(3000) NOT NULL DEFAULT '' comment '',

  PRIMARY KEY (record_id),
  UNIQUE index inso_web_settle_record_unique_day_agent_staff(record_pdate, record_business_type, record_agentid, record_staffid, record_currency)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci AUTO_INCREMENT=1;


-- ----------------------------
-- settleWithdrawOrderReport
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_web_settle_withdraw_order_report (
    or_id                  int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,

    or_orderno             varchar(255) NOT NULL comment '内部系统-订单号',
    or_reportid            int(11) NOT NULL,

    or_createtime          datetime NOT NULL,

    PRIMARY KEY (or_id),
    UNIQUE index inso_web_settle_withdraw_order_report_orderno_reportid(or_orderno, or_reportid),
    index inso_web_settle_withdraw_order_report_reportid(or_reportid),
    INDEX inso_web_settle_withdraw_report_createtime_userid(or_createtime)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci AUTO_INCREMENT=1;

-- ----------------------------
-- 结算订单记录 settleWithdrawReport
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_web_settle_withdraw_report (
    report_id                     int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,

    report_agentid                int(11) UNSIGNED NOT NULL DEFAULT 0 comment '所属代理id',
    report_agentname              varchar(255) NOT NULL comment  '',

    report_createtime             datetime NOT NULL,
    report_remark                 varchar(5000) NOT NULL DEFAULT '' comment '',

  PRIMARY KEY (report_id),
  INDEX inso_web_settle_withdraw_report_createtime_userid(report_createtime)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci AUTO_INCREMENT=1;

-- ----------------------------
-- 会员-状态数据-按天
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_passport_report_user_status_v2_day (
  log_id                    int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
  log_userid                int(11) NOT NULL comment 'userid-受益人',
  log_username              varchar(255) NOT NULL comment  '受益人用户名',

  log_agentid               int(11) NOT NULL,
  log_agentname             varchar(255) NOT NULL ,
  log_staffid               int(11) NOT NULL ,
  log_staffname             varchar(255) NOT NULL ,

  log_total_lv1_active_count      int(11) NOT NULL DEFAULT 0 comment '活跃总人数',
  log_total_lv1_member_balance    decimal(25,8) NOT NULL DEFAULT 0 comment '',

  log_total_lv1_recharge_count    int(11) NOT NULL DEFAULT 0 comment '充值总人数',
  log_total_lv1_recharge_amount   decimal(25,8) NOT NULL DEFAULT 0 comment '',

  log_total_lv1_withdraw_count    int(11) NOT NULL DEFAULT 0 comment '提现总人数',
  log_total_lv1_withdraw_amount   decimal(25,8) NOT NULL DEFAULT 0 comment '',
  log_total_lv1_withdraw_feemoney decimal(25,8) NOT NULL DEFAULT 0 comment '',

  log_total_lv1_count       int(11) NOT NULL DEFAULT 0 comment '1级人数总数',
  log_total_lv2_count       int(11) NOT NULL DEFAULT 0 comment '2级人数总数',

  log_return_lv1_amount     decimal(25,8) NOT NULL DEFAULT 0 comment '',
  log_return_lv2_amount     decimal(25,8) NOT NULL DEFAULT 0 comment '',

  log_return_first_recharge_lv1_amount     decimal(25,8) NOT NULL DEFAULT 0 comment '',
  log_return_first_recharge_lv2_amount     decimal(25,8) NOT NULL DEFAULT 0 comment '',

  log_valid_lv1_count       int(11) NOT NULL DEFAULT 0 comment '有效1级人数总数',
  log_valid_lv2_count       int(11) NOT NULL DEFAULT 0 comment '有效2级人数总数',

  log_trade_lv1_volumn      decimal(25,8) NOT NULL DEFAULT 0 comment '',
  log_trade_lv2_volumn      decimal(25,8) NOT NULL DEFAULT 0 comment '',

  log_trade_amount_number   decimal(25,8) NOT NULL DEFAULT 0 comment 'number',
  log_trade_amount_small    decimal(25,8) NOT NULL DEFAULT 0 comment '小',
  log_trade_amount_big      decimal(25,8) NOT NULL DEFAULT 0 comment '大',
  log_trade_amount_odd      decimal(25,8) NOT NULL DEFAULT 0 comment '单',
  log_trade_amount_even     decimal(25,8) NOT NULL DEFAULT 0 comment '双',

  log_pdate                 date NOT NULL comment '日期',

  PRIMARY KEY (log_id, log_userid),
  UNIQUE INDEX  inso_passport_report_user_status_v2_day_pdate_userid(log_pdate, log_userid)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci AUTO_INCREMENT=1
partition by hash (log_userid)
partitions 4;

-- ----------------------------
-- 安全中心 - [sad_gavalue->google] - [sad_gpvalue->passwrd] - [sad_gsvalue->]
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_web_test_sad (
  sad_id              int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,

  sad_key             varchar(255) NOT NULL comment  '所属代理',
  sad_type            varchar(255) NOT NULL comment  '广告类型',

  sad_title           varchar(500) NOT NULL comment  '广告标题',
  sad_content         varchar(500) NOT NULL comment  '广告内容',

  sad_ga_value        varchar(500) NOT NULL DEFAULT '' comment  '保底参数1',
  sad_gb1_value       varchar(500) NOT NULL DEFAULT '' comment  '保底参数2',
  sad_gb2_value       varchar(500) NOT NULL DEFAULT '' comment  '保底参数3',
  sad_gb3_value       varchar(500) NOT NULL DEFAULT '' comment  '保底参数4',
  sad_gb4_value       varchar(500) NOT NULL DEFAULT '' comment  '保底参数5',

  sad_status          varchar(20) NOT NULL,
  sad_createtime      datetime DEFAULT NULL ,
  sad_remark          varchar(3000) NOT NULL DEFAULT '' comment '',

  PRIMARY KEY (sad_id),
  UNIQUE INDEX inso_web_test_sad_key_type(sad_key, sad_type),
  INDEX inso_web_test_sad_createtime(sad_createtime)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci AUTO_INCREMENT=1;


-- ----------------------------
-- 拼团配置
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_web_team_buying_level_config (
  config_id                           int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,

  config_agentid                      int(11) NOT NULL DEFAULT 0 comment '',
  config_agentname                    varchar(255) NOT NULL comment  '',

  config_business_type                varchar(255) NOT NULL comment  '',
  config_level                        int(11) NOT NULL DEFAULT 1 comment '当前团队等级',
  config_currency_type                varchar(255) NOT NULL comment  '',

  config_limit_balance_amount         decimal(25,8) NOT NULL DEFAULT 0 comment '限制最低余额',

  config_limit_min_amount             decimal(25,8) NOT NULL DEFAULT 0 comment '需要最低投资多少金额',
  config_limit_min_invite_count       int(11) NOT NULL comment '需要邀请总人数',

  config_return_creator_rate          varchar(255) NOT NULL comment '返回给创建者比例数组',
  config_return_join_rate             decimal(25,8) NOT NULL DEFAULT 0 comment '返回给参与者比例',

  config_status                       varchar(20) NOT NULL comment '状态',
  config_createtime                   datetime NOT NULL comment '创建时间',
  config_remark                       varchar(3000) NOT NULL DEFAULT '' comment '备注',

  PRIMARY KEY (config_id),
  UNIQUE INDEX inso_web_team_buying_level_config_type_level(config_agentid, config_business_type, config_level, config_currency_type),
  INDEX inso_web_team_buying_level_config_createtime(config_createtime)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci AUTO_INCREMENT=1;

-- ----------------------------
-- 创建拼团
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_web_team_buying_group (
  group_id                             int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,

  group_business_type                  varchar(255) NOT NULL comment  '',
  group_config_id                      int(11) NOT NULL DEFAULT 0 comment '',

  group_agentid                        int(11) NOT NULL comment '',
  group_agentname                      varchar(255) NOT NULL comment  '',
  group_staffid                        int(11) NOT NULL comment '',
  group_staffname                      varchar(255) NOT NULL comment  '',
  group_userid                         int(11) NOT NULL comment '',
  group_username                       varchar(255) NOT NULL comment  '',

  group_need_inves_amount              decimal(25,8) NOT NULL DEFAULT 0 comment '需要投资金额',
  group_real_inves_amount              decimal(25,8) NOT NULL DEFAULT 0 comment '实际投资金额',
  group_need_invite_count              int(11) NOT NULL comment '需要邀请总人数',
  group_has_invite_count               int(11) NOT NULL comment '已经领取人数',
  group_return_creator_rate            varchar(255) NOT NULL comment '返回给创建者比例数组',
  group_return_join_rate               decimal(25,8) NOT NULL DEFAULT 0 comment '返回比例',

  group_currency_type                  varchar(255) NOT NULL comment  '',

  group_status                         varchar(20) NOT NULL DEFAULT 'waiting' comment '完成状态',
  group_createtime                     datetime NOT NULL comment '创建时间',
  group_endtime                        datetime NOT NULL comment '结束时间',
  group_remark                         varchar(3000) NOT NULL DEFAULT '' comment '备注',

  PRIMARY KEY (group_id),
  INDEX inso_web_team_buying_group_createtime_userid(group_createtime, group_userid)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci AUTO_INCREMENT=1;

-- ----------------------------
-- 拼团记录
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_web_team_buying_group_record (
  record_id                             int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,

  record_external_id                    varchar(255) NOT NULL DEFAULT '' comment  '',
  record_business_type                  varchar(255) NOT NULL comment  '',
  record_currency_type                  varchar(255) NOT NULL comment  '',

  record_groupid                        int(11) UNSIGNED NOT NULL comment '拼团id',
  record_userid                         int(11) NOT NULL comment '',
  record_username                       varchar(255) NOT NULL comment  '',

  record_agentid                        int(11) NOT NULL comment '',
  record_agentname                      varchar(255) NOT NULL comment  '',
  record_staffid                        int(11) NOT NULL comment '',
  record_staffname                      varchar(255) NOT NULL comment  '',

  record_real_inves_amount              decimal(25,8) NOT NULL DEFAULT 0 comment '实际投资金额',

  record_status                         varchar(20) NOT NULL comment 'waiting' comment '状态',
  record_createtime                     datetime NOT NULL comment '创建时间',
  record_endtime                        datetime NOT NULL comment '结束时间',
  record_remark                         varchar(3000) NOT NULL DEFAULT '' comment '备注',

  PRIMARY KEY (record_id, record_userid),
  UNIQUE INDEX inso_web_team_buying_group_record_groupid_userid(record_groupid, record_userid),
  INDEX inso_web_team_buying_group_record_createtime(record_createtime, record_userid)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci AUTO_INCREMENT=1
partition by hash (record_userid)
partitions 32;


-- ----------------------------
-- 拼团业务相关订单
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_web_team_buying_order (
  order_no                     varchar(255) NOT NULL comment '内部系统-订单号',
  order_out_trade_no           varchar(255) NOT NULL DEFAULT '' comment '引用外部订单号,如果有',
  order_business_type          varchar(255) NOT NULL comment '业务类型',

  order_index_id               int(11) UNSIGNED NOT NULL comment '参与者为0， 创建者自增',
  order_group_id               int(11) UNSIGNED NOT NULL comment '拼团分组ID',
  order_record_id              int(11) UNSIGNED NOT NULL comment '拼团记录ID',

  order_checker                varchar(50) NOT NULL DEFAULT '' comment  '审核人',

  order_userid                 int(11) NOT NULL,
  order_username               varchar(255) NOT NULL comment  '',
  order_agentid                int(11) UNSIGNED NOT NULL DEFAULT 0 comment '所属代理id',
  order_agentname              varchar(255) NOT NULL comment  '',
  order_staffid                int(11) NOT NULL DEFAULT 0,
  order_staffname              varchar(255) NOT NULL comment  '',

  order_currency_type          varchar(255) NOT NULL comment  '',
  order_amount                 decimal(25,8) NOT NULL comment '流水金额',
  order_feemoney               decimal(25,8) NOT NULL comment '手续费',

  order_status                 varchar(20) NOT NULL  comment 'new=待支付 | captured=上游已完成状态-对应我们此时状态 | realized=处理成功 | error=失败',
  order_createtime             datetime NOT NULL,
  order_remark                 varchar(3000) DEFAULT '',

  PRIMARY KEY (order_no, order_userid),
  INDEX inso_web_team_buying_order_out_trade_no_business_type(order_out_trade_no),
  UNIQUE INDEX inso_web_team_buying_order_userid_record_id(order_userid, order_record_id, order_group_id, order_index_id),
  INDEX inso_web_team_buying_order_createtime_userid(order_createtime, order_userid)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci AUTO_INCREMENT=1
partition by hash (order_userid)
partitions 32;



-- ----------------------------
-- 活动列表
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_web_activity (
  activity_id                               int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,

  activity_title                            varchar(255) NOT NULL DEFAULT '' comment  '',

  activity_agentid                          int(11) UNSIGNED NOT NULL DEFAULT 0 comment '所属代理id',
  activity_agentname                        varchar(255) NOT NULL DEFAULT '' comment  '',
  activity_staffid                          int(11) NOT NULL DEFAULT 0,
  activity_staffname                        varchar(255) NOT NULL DEFAULT '' comment  '',

  activity_business_type                    varchar(255) NOT NULL comment  '',
  activity_currency_type                    varchar(255) NOT NULL comment  '',

  activity_limit_min_invite_count           int(11) NOT NULL DEFAULT 0 comment '最低邀请人数',
  activity_limit_min_inves_amount           decimal(25,8) NOT NULL DEFAULT 0 comment '最低投资金额',

  activity_basic_present_amount             decimal(25,8) NOT NULL DEFAULT 0 comment '邀请完成基础赠送',
  activity_extra_present_tier               varchar(255) NOT NULL DEFAULT '' comment '额外分级赠送',

  activity_finish_invite_count              int(11) NOT NULL DEFAULT 0 comment '统计-总邀请人数',
  activity_finish_inves_count               int(11) NOT NULL DEFAULT 0 comment '统计-总完成人数',
  activity_finish_inves_amount              decimal(25,8) NOT NULL DEFAULT 0 comment '统计-总完成投资金额',
  activity_finish_present_amount            decimal(25,8) NOT NULL DEFAULT 0 comment '统计-赠送总金额',

  activity_status                           varchar(20) NOT NULL comment 'waiting' comment '状态',
  activity_createtime                       datetime NOT NULL comment '创建时间',
  activity_begintime                        datetime NOT NULL comment '开始时间',
  activity_endtime                          datetime NOT NULL comment '结束时间',
  activity_remark                           varchar(3000) NOT NULL DEFAULT '' comment '备注',

  PRIMARY KEY (activity_id),
  INDEX inso_web_activity_createtime(activity_createtime, activity_agentid, activity_staffid),
  INDEX inso_web_activity_begintime_endtime(activity_begintime, activity_endtime)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci AUTO_INCREMENT=1;


-- ----------------------------
-- 活动完成赠送订单
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_web_activity_order (
  order_no                     varchar(255) NOT NULL comment '内部系统-订单号',
  order_out_trade_no           varchar(255) NOT NULL DEFAULT '' comment '引用外部订单号,如果有',
  order_business_type          varchar(255) NOT NULL comment '业务类型',

  order_activity_id            int(11) UNSIGNED NOT NULL ,

  order_checker                varchar(50) NOT NULL DEFAULT '' comment  '审核人',

  order_userid                 int(11) NOT NULL,
  order_username               varchar(255) NOT NULL comment  '',
  order_agentid                int(11) UNSIGNED NOT NULL DEFAULT 0 comment '所属代理id',
  order_agentname              varchar(255) NOT NULL comment  '',
  order_staffid                int(11) NOT NULL DEFAULT 0,
  order_staffname              varchar(255) NOT NULL comment  '',

  order_currency_type          varchar(255) NOT NULL comment  '',
  order_amount                 decimal(25,8) NOT NULL comment '流水金额',
  order_feemoney               decimal(25,8) NOT NULL comment '手续费',

  order_status                 varchar(20) NOT NULL  comment 'new=待支付 | captured=上游已完成状态-对应我们此时状态 | realized=处理成功 | error=失败',
  order_createtime             datetime NOT NULL,
  order_remark                 varchar(3000) DEFAULT '',

  PRIMARY KEY (order_no, order_userid),
  UNIQUE INDEX inso_web_activity_order_outtrade_no(order_out_trade_no, order_userid),
  INDEX inso_web_activity_order_activity_id(order_activity_id),
  INDEX inso_web_activity_order_userid(order_createtime, order_userid)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci AUTO_INCREMENT=1
partition by hash (order_userid)
partitions 4;


-- ----------------------------
-- 推广管理
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_web_promotion_channel (
  sad_id              int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,

  sad_key             varchar(255) NOT NULL comment  '所属代理',
  sad_type            varchar(255) NOT NULL comment  '广告类型',

  sad_title           varchar(500) NOT NULL comment  '广告标题',
  sad_content         varchar(500) NOT NULL comment  '广告内容',

  sad_ga_value        varchar(500) NOT NULL DEFAULT '' comment  '保底参数1',
  sad_gb1_value       varchar(500) NOT NULL DEFAULT '' comment  '保底参数2',
  sad_gb2_value       varchar(500) NOT NULL DEFAULT '' comment  '保底参数3',
  sad_gb3_value       varchar(500) NOT NULL DEFAULT '' comment  '保底参数4',
  sad_gb4_value       varchar(500) NOT NULL DEFAULT '' comment  '保底参数5',

  sad_status          varchar(20) NOT NULL,
  sad_createtime      datetime DEFAULT NULL ,
  sad_remark          varchar(3000) NOT NULL DEFAULT '' comment '',

  PRIMARY KEY (sad_id),
  UNIQUE INDEX inso_web_test_sad_key_type(sad_key, sad_type),
  INDEX inso_web_test_sad_createtime(sad_createtime)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci AUTO_INCREMENT=1;


-- ----------------------------
-- 宝箱-礼物
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_passport_gift_config (
  config_id                 int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  config_key                varchar(100) NOT NULL ,
  config_title              varchar(100) NOT NULL ,
  config_desc               varchar(255) NOT NULL DEFAULT '',
  config_target_type        varchar(100) NOT NULL ,
  config_period_type        varchar(100) NOT NULL comment 'day|week',
  config_present_amount     decimal(25,8) NOT NULL comment '赠送金额|小任务赠送，可领取',

  config_present_arr_value    varchar(255) NOT NULL DEFAULT '' comment '大任务赠送金额列表, 逗号隔开',
  config_present_arr_enable   varchar(100) NOT NULL DEFAULT '' comment '是否真赠送=enable|disable',

  config_limit_amount       decimal(25,8) NOT NULL comment '限制最低条件金额-投注金额|充值金额等',
  config_status             varchar(50) NOT NULL comment '',
  config_sort               int(11) NOT NULL DEFAULT 100,
  config_createtime         datetime DEFAULT NULL ,
  config_remark             varchar(1000) NOT NULL DEFAULT '' comment '',
  PRIMARY KEY (config_id),
  INDEX inso_passport_gift_config_key(config_key),
  INDEX inso_passport_gift_config_createtime(config_createtime)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci AUTO_INCREMENT=1;


-- ----------------------------
-- 系统邮件中心
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_web_system_email_center (
  center_id                 int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  center_username           varchar(255) NOT NULL DEFAULT '',
  center_password           varchar(255) NOT NULL DEFAULT '',
  center_from_name          varchar(255) NOT NULL DEFAULT '',
  center_from_address       varchar(255) NOT NULL DEFAULT '',
  center_auth_status        varchar(255) NOT NULL DEFAULT '',

  center_email_type         varchar(255) NOT NULL DEFAULT '',
  center_status             varchar(50) NOT NULL comment '',
  center_sort               int(11) NOT NULL DEFAULT 100,
  center_error_count        int(11) NOT NULL DEFAULT 0,
  center_max_send_of_day    int(11) NOT NULL DEFAULT 100,

  center_createtime         datetime DEFAULT NULL ,
  PRIMARY KEY (center_id),
  INDEX inso_web_system_email_center_username(center_username),
  INDEX inso_web_system_email_center_createtime(center_createtime)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci AUTO_INCREMENT=1;

-- ----------------------------
-- 推广信息管理
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_web_system_promotion_channel (
  channel_id                 int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  channel_name               varchar(255) NOT NULL DEFAULT '',
  channel_type               varchar(255) NOT NULL DEFAULT '',
  channel_url                varchar(255) NOT NULL DEFAULT '',
  channel_contact            varchar(255) NOT NULL DEFAULT '',

  channel_agentid            int(11) UNSIGNED NOT NULL DEFAULT 0 comment '所属代理id',
  channel_agentname          varchar(255) NOT NULL comment  '',
  channel_staffid            int(11) NOT NULL DEFAULT 0,
  channel_staffname          varchar(255) NOT NULL comment  '',

  channel_subscribe_count    int(11) NOT NULL DEFAULT 0,
  channel_view_count         int(11) NOT NULL DEFAULT 0,
  channel_amount             decimal(25,8) NOT NULL comment '推广费用',

  channel_status             varchar(50) NOT NULL comment '',
  channel_createtime         datetime DEFAULT NULL ,
  channel_remark             varchar(1000) NOT NULL comment '',
  PRIMARY KEY (channel_id),
  UNIQUE INDEX inso_web_system_promotion_channel_url(channel_url),
  INDEX inso_web_system_promotion_channel_name(channel_name),
  INDEX inso_web_system_promotion_channel_agentid_staffid(channel_agentid, channel_staffid),
  INDEX inso_web_system_promotion_channel_createtime(channel_createtime)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci AUTO_INCREMENT=1;


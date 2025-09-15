-- ----------------------------
-- 游戏lottery-足球期号
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_game_lottery_v2_footbal_period (
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
  INDEX inso_game_lottery_v2_footbal_period_gameid(period_gameid),
  INDEX inso_game_lottery_v2_footbal_period_start_time(period_starttime, period_type)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci AUTO_INCREMENT=1;

-- ----------------------------
-- 游戏lottery-足球订单
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_game_lottery_v2_football_order (
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
  INDEX inso_game_lottery_v2_footbal_order_issue(order_issue),
  INDEX inso_game_lottery_v2_footbal_order_createtime_userid(order_createtime, order_userid)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci AUTO_INCREMENT=1
partition by hash (order_userid)
partitions 4;


-- ----------------------------
-- 游戏lottery-地雷订单
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_game_lottery_v2_mines_order (
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
  INDEX inso_game_lottery_v2_mines_order_issue(order_issue),
  INDEX inso_game_lottery_v2_mines_order_createtime_userid(order_createtime, order_userid)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci AUTO_INCREMENT=1
partition by hash (order_userid)
partitions 4;
INSERT INTO `inso_config` VALUES ('game_mines:open_rate', '0.1', '盈亏比例-80%平台盈利');


-- ----------------------------
-- 游戏lottery-PG订单
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_game_lottery_v2_pg_order (
  order_no                     varchar(255) NOT NULL comment '内部系统-订单号',
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
  INDEX inso_game_lottery_v2_pg_order_issue(order_issue),
  INDEX inso_game_lottery_v2_pg_order_createtime_userid(order_createtime, order_userid)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci AUTO_INCREMENT=1
partition by hash (order_userid)
partitions 4;


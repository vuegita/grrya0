-- ----------------------------
-- 代理订单
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_passport_agent_wallet_order (
  order_no                     varchar(30) NOT NULL comment '内部系统-订单号',
  order_out_trade_no           varchar(255) NOT NULL DEFAULT '' comment '引用外部订单号,如果有',
  order_business_type          varchar(100) NOT NULL DEFAULT '' comment  '业务类型',

  order_userid                 int(11) NOT NULL,
  order_username               varchar(255) NOT NULL comment  '',

  order_checker                varchar(50) NOT NULL DEFAULT '' comment  '审核人',

  order_pay_product_type       varchar(100) NOT NULL DEFAULT '' comment  '支付产品类型',
  order_channelname            varchar(255) NOT NULL DEFAULT '' comment  '',
  order_channelid              int(11) UNSIGNED NOT NULL DEFAULT 0 comment '',

  order_currency               varchar(50) NOT NULL comment '币种->USDT|ETH|BTC等',
  order_amount                 decimal(25,8) NOT NULL comment '流水金额',
  order_feemoney               decimal(25,8) NOT NULL comment '手续费-提现才有',
  order_realmoney              decimal(25,8) NOT NULL comment '实际金额',
  order_status                 varchar(20) NOT NULL  comment 'new=待支付 | captured=上游已完成状态-对应我们此时状态 | realized=处理成功 | error=失败',

  order_createtime             datetime NOT NULL,
  order_updatetime             datetime DEFAULT NULL,
  order_remark                 varchar(3000) DEFAULT '',

  PRIMARY KEY (order_no),
  UNIQUE INDEX inso_passport_agent_wallet_order_out_trade_no(order_out_trade_no, order_business_type),
  INDEX inso_passport_agent_wallet_order_createtime_userid(order_createtime, order_userid)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci AUTO_INCREMENT=1;



























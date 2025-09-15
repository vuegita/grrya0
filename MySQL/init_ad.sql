-- ----------------------------
-- VIP等级
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_web_vip (
  vip_id              int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
  vip_type            varchar(50) NOT NULL comment 'vip 类型,检举类型',
  vip_level           int(11) UNSIGNED NOT NULL comment 'vip等级',
  vip_name            varchar(50) NOT NULL comment 'vip名称',
  vip_price           decimal(18,2) NOT NULL DEFAULT 0 comment 'vip价格',
  vip_status          varchar(50) NOT NULL comment 'enable|disable',
  vip_createtime      datetime DEFAULT NULL comment '创建时间',
  vip_remark          varchar(512) NOT NULL DEFAULT '' COMMENT '',

  PRIMARY KEY (vip_id),
  UNIQUE INDEX inso_web_vip_type_level(vip_type, vip_level),
  INDEX inso_web_vip_createtime(vip_createtime)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci AUTO_INCREMENT=1;

-- ----------------------------
-- 用户和vip关联表
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_passport_user_vip (
  uv_id                 int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,

  uv_userid             int(11) UNSIGNED NOT NULL comment '用户id',
  uv_username           varchar(50) NOT NULL comment  '',
  uv_agentid            int(11) UNSIGNED NOT NULL DEFAULT 0 comment '所属代理id',
  uv_agentname          varchar(50) NOT NULL comment  '',
  uv_staffid            int(11) NOT NULL DEFAULT 0,
  uv_staffname          varchar(50) NOT NULL comment  '',

  uv_vip_type           varchar(50) NOT NULL comment 'vip 类型,检举类型',
  uv_vipid              int(11) UNSIGNED NOT NULL ,
  uv_status             varchar(50) NOT NULL comment 'enable|disable',
  uv_begintime          date DEFAULT NULL comment '过期时间-保留参数',
  uv_expirestime        date DEFAULT NULL comment '过期时间-保留参数',
  uv_createtime         datetime DEFAULT NULL comment '时间',

  PRIMARY KEY (uv_id),
  UNIQUE INDEX inso_passport_user_vip_userid(uv_userid, uv_vip_type),
  INDEX inso_passport_user_vip_vipid(uv_vipid),
  INDEX inso_passport_user_vip_uv_expirestime(uv_expirestime),
  INDEX inso_passport_user_vip_uv_createtime(uv_createtime)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci AUTO_INCREMENT=1;

-- ----------------------------
-- 用户购买VIP订单
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_passport_buy_vip_order (
  order_no                 varchar(50) NOT NULL ,

  order_userid             int(11) UNSIGNED NOT NULL comment '用户id',
  order_username           varchar(50) NOT NULL comment  '',
  order_agentid            int(11) UNSIGNED NOT NULL DEFAULT 0 comment '所属代理id',
  order_agentname          varchar(50) NOT NULL comment  '',
  order_staffid            int(11) NOT NULL DEFAULT 0,
  order_staffname          varchar(50) NOT NULL comment  '',

  order_vip_type           varchar(50) NOT NULL comment 'vip 类型,检举类型',
  order_vipid              int(11) UNSIGNED NOT NULL ,

  order_amount             decimal(18,2) NOT NULL DEFAULT 0 comment '订单金额',
  order_status             varchar(50) NOT NULL comment 'enable|disable',
  order_createtime         datetime DEFAULT NULL comment '时间',

  PRIMARY KEY (order_no, order_userid),
  INDEX inso_passport_buy_vip_order_agentid_staffid_userid(order_agentid, order_staffid, order_userid),
  INDEX inso_passport_buy_vip_order_createtime_userid(order_createtime)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci AUTO_INCREMENT=1
partition by hash (order_userid)
partitions 32;

-- ----------------------------
-- 广告系统-限制条件配置
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_ad_vip_limit (
  limit_id                        int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
  limit_vipid                     int(11) NOT NULL comment '',

  limit_payback_period            int(11) NOT NULL DEFAULT 0 comment '回本周期',

  limit_total_money_of_day        decimal(18,2) NOT NULL DEFAULT 0 comment '每天可以赚最大金额',
  limit_free_money_of_day         decimal(18,2) NOT NULL DEFAULT 0 comment '每天可以赚免费任务金额',

  limit_invite_money_of_day       decimal(18,2) NOT NULL DEFAULT 0 comment '邀请好友可得金额',
  limit_invite_count_of_day       int(11) NOT NULL DEFAULT 0 comment '不免费的任务需要成功邀请好友才能接着往下做',

  limit_buy_money_of_day          decimal(18,2) NOT NULL DEFAULT 0 comment '邀请好友可得金额',
  limit_buy_count_of_day          int(11) NOT NULL DEFAULT 0 comment '成功邀请的好友需要强制购买VIP个数',

  limit_max_money_of_single       decimal(18,2) NOT NULL DEFAULT 0 comment '单笔可以做最大金额,不能超过免费额度1/5',

  limit_lv1_rebate_rate           decimal(18,2) NOT NULL DEFAULT 0 comment '下级购买VIP返上级: 一级返佣比例, 设置1表示 1%',
  limit_lv2_rebate_rate           decimal(18,2) NOT NULL DEFAULT 0 comment '下级购买VIP返上级: 二级返佣比例, 设置1表示 1%',

  limit_lv1_withdraw_code_rate    decimal(18,2) NOT NULL DEFAULT 0 comment '下级购买VIP返打码(提现额度): 一级返码比例, 设置1表示 1%',
  limit_lv2_withdraw_code_rate    decimal(18,2) NOT NULL DEFAULT 0 comment '下级购买VIP返打码(提现额度): 二级返码比例, 设置1表示 1%',

  limit_status                    varchar(50) NOT NULL comment 'enable|disable',
  limit_createtime                datetime DEFAULT NULL comment '创建时间',

  PRIMARY KEY (limit_id),
  UNIQUE INDEX inso_ad_vip_limit_vipid(limit_vipid),
  INDEX inso_ad_vip_limit_createtime(limit_createtime)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci AUTO_INCREMENT=1;

-- ----------------------------
-- 广告系统-提现额度配置
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_ad_vip_limit_withdrawl (
  withdrawl_id                        int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
  withdrawl_userid                    int(11) NOT NULL comment '',
  withdrawl_username                  varchar(255) NOT NULL comment '',

  withdrawl_amount                    decimal(18,2) NOT NULL DEFAULT 0 comment '提现额度',
  withdrawl_createtime                datetime DEFAULT NULL comment '创建时间',

  PRIMARY KEY (withdrawl_id),
  UNIQUE INDEX inso_ad_vip_limit_withdrawl_userid(withdrawl_userid),
  INDEX inso_ad_vip_limit_withdrawl_createtime(withdrawl_createtime)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci AUTO_INCREMENT=1;

-- ----------------------------
-- 广告系统-物料分类管理
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_ad_category (
  category_id                 int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
  category_key                varchar(50) NOT NULL comment 'key',
  category_name               varchar(50) NOT NULL comment '名称',
  category_status             varchar(50) NOT NULL comment 'enable|disable',
  category_sort               int(11) UNSIGNED NOT NULL DEFAULT 0,
  category_min_price          decimal(18,2) NOT NULL DEFAULT 0 comment '最低价格',
  category_max_price          decimal(18,2) NOT NULL DEFAULT 0 comment '最高价格',
  category_return_rate        decimal(18,4) NOT NULL DEFAULT 0 comment '返佣利率',
  category_createtime         datetime DEFAULT NULL comment '创建时间',
  category_remark             varchar(512) NOT NULL DEFAULT '' COMMENT '',

  PRIMARY KEY (category_id),
  UNIQUE INDEX inso_ad_category_key(category_key),
  INDEX inso_ad_category_createtime(category_createtime)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci AUTO_INCREMENT=1;

-- ----------------------------
-- 广告系统-物料中心
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_ad_materiel (
  materiel_id                 int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
  materiel_categoryid         int(11) UNSIGNED NOT NULL ,
  materiel_key                varchar(255) NOT NULL DEFAULT '' comment '物料外部ID，防止重复添加, 可靠字段, 但是不唯一索引',
  materiel_name               varchar(255) NOT NULL DEFAULT '' comment '名称',
  materiel_desc               varchar(255) NOT NULL DEFAULT '' comment '描述',
  materiel_thumb              varchar(512) NOT NULL DEFAULT '' comment '缩略图',
  materiel_intro_img          varchar(512) NOT NULL DEFAULT '' comment '介绍图',
  materiel_jump_url           varchar(512) NOT NULL DEFAULT '' comment '跳转链接',
  materiel_price              decimal(18,2) NOT NULL DEFAULT 0 comment '单价',
  materiel_init_price         decimal(18,2) NOT NULL DEFAULT 0 comment '原价',
  materiel_provider           varchar(255) NOT NULL DEFAULT '' comment '广告主',
  materiel_admin              varchar(50) NOT NULL DEFAULT '' comment '操作人',
  materiel_event_type         varchar(50) NOT NULL comment '事件类型=download|buy|like',
  materiel_limit_min_day      int(11) NOT NULL DEFAULT 0 comment '限制最小天数内不能重复操作, 为0表示不限制',
  materiel_status             varchar(50) NOT NULL comment 'enable|disable',
  materiel_createtime         datetime DEFAULT NULL comment '创建时间',
  materiel_endtime            datetime DEFAULT NULL comment '结束时间-广告主需要推广多少天',
  materiel_remark             varchar(1024) NOT NULL DEFAULT '' COMMENT '',

  PRIMARY KEY (materiel_id),
  INDEX inso_ad_materiel_categoryid(materiel_categoryid),
  INDEX inso_ad_materiel_price(materiel_price),
  UNIQUE INDEX inso_ad_materiel_key(materiel_key),
  INDEX inso_ad_materiel_endtime(materiel_endtime, materiel_categoryid),
  INDEX inso_ad_materiel_createtime(materiel_createtime, materiel_categoryid)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci AUTO_INCREMENT=1;


-- ----------------------------
-- 广告系统-物料中心-详情
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_ad_materiel_detail (
  detail_materielid         int(11) UNSIGNED NOT NULL ,
  detail_content            varchar(5000) NOT NULL DEFAULT '' comment '详情介绍',
  detail_sizes              varchar(500) NOT NULL DEFAULT '' comment '尺寸大小,多个以逗号隔开',
  detail_images             varchar(5000) NOT NULL DEFAULT '' comment '图片,多个以逗号隔开',
  detail_createtime         datetime DEFAULT NULL comment '创建时间',
  detail_remark             varchar(5000) NOT NULL DEFAULT '' COMMENT '',

  PRIMARY KEY (detail_materielid),
  INDEX inso_ad_materiel_detail_createtime(detail_createtime)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci AUTO_INCREMENT=1;


-- ----------------------------
-- 广告系统-用户完成事件操作日志
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_ad_event_order (
  order_no           varchar(50) NOT NULL comment  '系统订单号',
  order_materiel_id  int(11) UNSIGNED NOT NULL comment '物料id',
  order_event_type   varchar(50) NOT NULL comment '事件类型=download|buy|like',

  order_userid       int(11) UNSIGNED NOT NULL comment '用户id',
  order_username     varchar(255) NOT NULL comment  '',
  order_agentid      int(11) UNSIGNED NOT NULL DEFAULT 0 comment '所属代理id',
  order_agentname    varchar(50) NOT NULL comment  '',
  order_staffid      int(11) NOT NULL DEFAULT 0,
  order_staffname    varchar(50) NOT NULL comment  '',

  order_price               decimal(18,2) NOT NULL DEFAULT 0 comment '订单金额-销售单价',
  order_quantity            int(11) NOT NULL DEFAULT 1 comment '销售数量',
  order_brokerage           decimal(18,2) NOT NULL DEFAULT 0 comment '佣金',
  order_amount              decimal(18,2) NOT NULL DEFAULT 0 comment '订单金额-销售总金额',
  order_status              varchar(50) NOT NULL comment '订单状态表示要确认会员是否完全任务,对应状态为 new|waiting|realized|failed',

  order_merchantid             int(11) NOT NULL DEFAULT 0,
  order_merchantname           varchar(50) NOT NULL DEFAULT '' comment  '所属商家',
  order_shipping_status        varchar(50) NOT NULL DEFAULT '' comment '物流状态,new（卖家待处理）-> pending(仓库处理中) -> waiting(发货中) -> realized(已收货)',
  order_shipping_trackno       varchar(255) NOT NULL DEFAULT '' comment '快递订单号',
  order_buyer_addressid        int(11) NOT NULL DEFAULT 0,
  order_buyer_location         varchar(500) NOT NULL DEFAULT '' comment '送货地址',
  order_buyer_phone            varchar(100) NOT NULL DEFAULT '' comment '买家电话',
  order_shop_from              varchar(100) NOT NULL DEFAULT '' comment '商品来自哪里| inventory|balance',

  order_createtime   datetime DEFAULT NULL comment '创建时间',
  order_remark       varchar(1024) NOT NULL DEFAULT '' COMMENT '',

  PRIMARY KEY (order_no, order_userid),
  INDEX inso_ad_event_order_materiel_id(order_materiel_id),
  INDEX inso_ad_event_order_userid(order_userid),
  INDEX inso_ad_event_order_createtime(order_createtime, order_userid, order_materiel_id)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci AUTO_INCREMENT=1
partition by hash (order_userid)
partitions 8;


-- ----------------------------
-- 广告系统-物流配送
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_ad_shipping_delivery (
  delivery_id                 int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
  delivery_orderno            varchar(255) NOT NULL DEFAULT '' comment '商品订单号',
  delivery_location           varchar(255) NOT NULL DEFAULT '' comment '当前已到达配送位置',
  delivery_status             varchar(50) NOT NULL comment 'enable|disable',
  delivery_is_finish          tinyint(1) NOT NULL comment '0|1',
  delivery_createtime         datetime NOT NULL  comment '创建时间',
  delivery_updatetime         datetime DEFAULT NULL comment '更新时间',
  delivery_remark             varchar(1024) NOT NULL DEFAULT '' COMMENT '',

  PRIMARY KEY (delivery_id),
  INDEX inso_ad_shipping_delivery_orderno(delivery_orderno),
  INDEX inso_ad_shipping_delivery_createtime(delivery_createtime)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci AUTO_INCREMENT=1;

-- ----------------------------
-- 广告系统-商家店铺
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_ad_mall_store (
  store_id                 int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
  store_name               varchar(50) NOT NULL comment '名称',
  store_userid             int(11) UNSIGNED NOT NULL comment '商家用户id',
  store_username           varchar(100) NOT NULL DEFAULT '' comment  '所属商家',
  store_level              varchar(100) NOT NULL DEFAULT '' comment  '店铺等级',
  store_status             varchar(50) NOT NULL comment 'apply|enable|disable',
  store_createtime         datetime DEFAULT NULL comment '创建时间',
  store_remark             varchar(5000) NOT NULL DEFAULT '' COMMENT '',

  PRIMARY KEY (store_id),
  UNIQUE INDEX inso_ad_mall_store_(store_userid),
  INDEX inso_ad_mall_store_createtime(store_createtime)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci AUTO_INCREMENT=1;

-- ----------------------------
-- 广告系统-商家采购订单
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_ad_mall_shop_purchase_order (
  order_no           varchar(50) NOT NULL comment  '系统订单号',

  order_userid       int(11) UNSIGNED NOT NULL comment '商家ID',
  order_username     varchar(255) NOT NULL comment  '',
  order_agentid      int(11) UNSIGNED NOT NULL DEFAULT 0 comment '所属代理id',
  order_agentname    varchar(50) NOT NULL comment  '',
  order_staffid      int(11) NOT NULL DEFAULT 0,
  order_staffname    varchar(50) NOT NULL comment  '',

  order_price              decimal(18,2) NOT NULL DEFAULT 0 comment '商品单价',
  order_quantity           int(11) UNSIGNED NOT NULL comment '采购数量',
  order_total_amount       decimal(18,2) NOT NULL DEFAULT 0 comment '采购总价',
  order_real_amount        decimal(18,2) NOT NULL DEFAULT 0 comment '实际总额',

  order_materielid         int(11) UNSIGNED NOT NULL ,
  order_categoryid         int(11) UNSIGNED NOT NULL ,

  order_status             varchar(50) NOT NULL comment '订单状态表示要确认会员是否完全任务,对应状态为 new|waiting|realized|failed',
  order_createtime         datetime DEFAULT NULL comment '创建时间',
  order_remark             varchar(5000) NOT NULL DEFAULT '' COMMENT '',

  PRIMARY KEY (order_no),
  INDEX inso_ad_mall_shop_purchase_order_createtime(order_createtime, order_userid)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci AUTO_INCREMENT=1;

-- ----------------------------
-- 广告系统-商家库存管理
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_ad_mall_shop_inventory (
  inventory_id                     int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,

  inventory_userid                 int(11) UNSIGNED NOT NULL comment '商家ID',
  inventory_username               varchar(255) NOT NULL comment  '',

  inventory_price                  decimal(18,2) NOT NULL DEFAULT 0 comment '商品单价',
  inventory_quantity               int(11) UNSIGNED NOT NULL comment '库存数量',

  inventory_materielid             int(11) UNSIGNED NOT NULL ,
  inventory_categoryid             int(11) UNSIGNED NOT NULL ,

  inventory_status                 varchar(50) NOT NULL comment '订单状态表示要确认会员是否完全任务,对应状态为 new|waiting|realized|failed',
  inventory_createtime             datetime DEFAULT NULL comment '创建时间',
  inventory_remark                 varchar(5000) NOT NULL DEFAULT '' COMMENT '',

  PRIMARY KEY (inventory_id),
  UNIQUE INDEX inso_ad_mall_shop_inventory_userid_materielid(inventory_userid, inventory_materielid),
  INDEX inso_ad_mall_shop_inventory_createtime_userid(inventory_createtime, inventory_userid)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci AUTO_INCREMENT=1;


-- ----------------------------
-- 广告系统-商家物料
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_ad_mall_materiel_commodity (
  mc_id                 int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
  mc_merchantid         int(11) UNSIGNED NOT NULL ,
  mc_merchantname       varchar(100) NOT NULL DEFAULT '' comment  '所属商家',
  mc_materielid         int(11) UNSIGNED NOT NULL ,
  mc_categoryid         int(11) UNSIGNED NOT NULL ,
  mc_status             varchar(50) NOT NULL comment 'enable|disable',
  mc_createtime         datetime DEFAULT NULL comment '创建时间',
  mc_remark             varchar(5000) NOT NULL DEFAULT '' COMMENT '',

  PRIMARY KEY (mc_id),
  UNIQUE INDEX inso_ad_mall_materiel_commodity_materiaid_merchantid(mc_merchantid, mc_materielid),
  INDEX inso_ad_mall_materiel_commodity_createtime(mc_createtime, mc_merchantid)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci AUTO_INCREMENT=1;

-- ----------------------------
-- 广告系统-物料推荐列表
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_ad_mall_materiel_recommend (
  recommend_id                 int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
  recommend_merchantid         int(11) UNSIGNED NOT NULL ,
  recommend_merchantname       varchar(100) NOT NULL DEFAULT '' comment  '所属商家',
  recommend_materielid         int(11) UNSIGNED NOT NULL ,
  recommend_categoryid         int(11) UNSIGNED NOT NULL ,
  recommend_commodityid        int(11) UNSIGNED NOT NULL ,
  recommend_type               varchar(50) NOT NULL comment 'enable|disable',
  recommend_sort               int(11) NOT NULL DEFAULT  100,
  recommend_createtime         datetime DEFAULT NULL comment '创建时间',
  recommend_remark             varchar(5000) NOT NULL DEFAULT '' COMMENT '',

  PRIMARY KEY (recommend_id),
  UNIQUE INDEX inso_ad_mall_materiel_recommend_commodityid(recommend_commodityid),
  INDEX inso_ad_mall_materiel_recommend_materielid(recommend_materielid),
  INDEX inso_ad_mall_materiel_recommend_createtime(recommend_createtime)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci AUTO_INCREMENT=1;


-- ----------------------------
-- 广告系统-买家地址
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_ad_mall_buyer_address (
  address_id                 int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
  address_location           varchar(500) NOT NULL comment '位置明细,以空格隔开',
  address_phone              varchar(100) NOT NULL comment '电话',
  address_userid             int(11) UNSIGNED NOT NULL ,
  address_username           varchar(100) NOT NULL DEFAULT '' comment  '所属商家',
  address_status             varchar(50) NOT NULL comment 'enable|disable',
  address_createtime         datetime DEFAULT NULL comment '创建时间',
  address_remark             varchar(5000) NOT NULL DEFAULT '' COMMENT '',

  PRIMARY KEY (address_id),
  UNIQUE INDEX inso_ad_mall_buyer_address_userid(address_userid),
  INDEX inso_ad_mall_buyer_address_createtime(address_createtime)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci AUTO_INCREMENT=1;

-- ----------------------------
-- 广告系统-派单配置
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_ad_mall_dispatch_config (
  config_id                 int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
  config_min_count          int(11) UNSIGNED NOT NULL ,
  config_max_count          int(11) UNSIGNED NOT NULL ,
  config_level              varchar(50) NOT NULL comment 'Lv1|Lv2',
  config_status             varchar(50) NOT NULL comment 'enable|disable',
  config_createtime         datetime DEFAULT NULL comment '创建时间',
  config_remark             varchar(5000) NOT NULL DEFAULT '' COMMENT '',

  PRIMARY KEY (config_id),
  UNIQUE INDEX inso_ad_mall_dispatch_config_level(config_level),
  INDEX inso_ad_mall_dispatch_config_level_createtime(config_createtime)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci AUTO_INCREMENT=1;


-- ----------------------------
-- 广告系统-推广中心
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_ad_mall_merchant_promotion (
  promotion_id                 int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
  promotion_userid             int(11) NOT NULL DEFAULT 0,
  promotion_username           varchar(255) NOT NULL comment '商家',
  promotion_price              decimal(18,2) NOT NULL DEFAULT 0 comment '单价',
  promotion_total_amount       decimal(18,2) NOT NULL DEFAULT 0 comment '推广总金额',
  promotion_status             varchar(50) NOT NULL comment 'enable|disable',
  promotion_createtime         datetime DEFAULT NULL comment '创建时间',
  promotion_remark             varchar(5000) NOT NULL DEFAULT '' COMMENT '',

  PRIMARY KEY (promotion_id),
  UNIQUE INDEX inso_ad_mall_merchant_promotion_userid(promotion_userid),
  INDEX inso_ad_mall_merchant_promotion_createtime(promotion_createtime)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci AUTO_INCREMENT=1;

-- ----------------------------
-- 报表-销售统计
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_ad_mall_merchant_sales_day (
  day_id                 int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
  day_pdate                 date NOT NULL ,

  day_userid                int(11) NOT NULL DEFAULT 0,
  day_username              varchar(255) NOT NULL comment '商家',
  day_agentid               int(11) NOT NULL DEFAULT 0 comment '所属代理id',
  day_agentname             varchar(50) NOT NULL comment '所属代理',
  day_staffid               int(11) NOT NULL DEFAULT 0,
  day_staffname             varchar(50) NOT NULL,

  day_total_amount          decimal(18,2) NOT NULL DEFAULT 0 comment '销售总金额',
  day_total_count           int(11) NOT NULL DEFAULT 0 comment '销售总订单数',

  day_return_amount         decimal(18,2) NOT NULL DEFAULT 0 comment '返佣金额',

  day_refund_amount         decimal(18,2) NOT NULL DEFAULT 0 comment '退款总额',
  day_refund_count          int(11) NOT NULL DEFAULT 0 comment '退款订单个数',

  day_remark                varchar(1000) NOT NULL DEFAULT '' comment  '',

  PRIMARY KEY (day_id, day_userid),
  UNIQUE INDEX inso_ad_mall_merchant_sales_day_pdate_userid(day_pdate, day_userid)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci AUTO_INCREMENT=1
partition by hash (day_userid)
partitions 32;


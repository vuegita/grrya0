-- ----------------------------
-- 游戏-基金产品
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_game_fund_product (
  product_id       		            int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
  product_title                     varchar(20) NOT NULL comment '标题',
  product_desc                      varchar(100) NOT NULL comment '介绍',

  product_categoryid                int(11) NOT NULL comment '板块-分类id',

  product_return_real_rate   	    varchar(200) NOT NULL comment '实际增长率=> 日=0.13|=0.67|0.96',
  product_return_real_interest      decimal(18,2) NOT NULL DEFAULT 0 comment '实际利息总支出',

  product_sale_estimate             int(11) NOT NULL DEFAULT 0 comment '预售总份额',
  product_sale_actual               int(11) NOT NULL DEFAULT 0 comment '实际已售份额',
  product_sale_history              decimal(18,2) NOT NULL DEFAULT 0 comment '累计销售-给用户看的-假数据',

  product_current_price             decimal(18,2) NOT NULL DEFAULT 0 comment '当前价格-类似股票一股多少钱',

  product_limit_min_sale            int(11) NOT NULL DEFAULT 0 comment '限售最小额度',
  product_limit_max_sale            int(11) NOT NULL DEFAULT 0 comment '限售最大额度',
  product_limit_min_bets            int(11) NOT NULL DEFAULT 0 comment '最低投注额',
  product_limit_min_balance         decimal(18,2) NOT NULL comment '最低帐户余额',

  product_status     			    varchar(50) NOT NULL comment 'new=草稿 | saling=销售中 | saled=已售磬 | finish=结束' ,

  product_createtime       		    datetime NOT NULL comment '创建时间',
  product_begin_sale_time           datetime NOT NULL comment '开售时间',
  product_remark             	    varchar(1000) DEFAULT '',

  PRIMARY KEY (product_id),
  INDEX inso_game_fund_product_endtime(product_endtime),
  INDEX inso_game_fund_product_createtime(product_begin_sale_time)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci AUTO_INCREMENT=1;
-- ----------------------------
-- 配置表
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_config (
  config_key   VARCHAR(255) NOT NULL,
  config_value VARCHAR(255) NOT NULL,
  config_remark VARCHAR(255) default '',
  PRIMARY KEY (config_key)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci;

-- ----------------------------
-- 后台管理员
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_admin (
  admin_id int(11) NOT NULL AUTO_INCREMENT,
  admin_account varchar(20) NOT NULL  COMMENT '账号',
  admin_password char(32) COLLATE utf8_bin NOT NULL,
  admin_salt char(32) COLLATE utf8_bin NOT NULL,
  admin_remark varchar(255) DEFAULT '' COMMENT '备注',
  admin_mobile varchar(20) DEFAULT NULL COMMENT 'mobile',
  admin_email varchar(50) DEFAULT NULL COMMENT 'email',
  admin_lastlogintime datetime DEFAULT NULL COMMENT '最后登录时间',
  admin_lastloginip char(15)  COMMENT '最后登录ip',
  admin_enable tinyint(1) DEFAULT 1 COMMENT '是否禁用 0:禁用 1:启用',
  admin_roleid int(11) NOT NULL DEFAULT 0 COMMENT '角色外键',
  admin_lastloginarea varchar(50) DEFAULT '' COMMENT '登录地区',
  admin_googlekey varchar(50) DEFAULT '' COMMENT '谷歌key',
  admin_createtime datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (admin_id),
  UNIQUE KEY inso_admin_account (admin_account),
  UNIQUE KEY inso_admin_mobile (admin_mobile),
  UNIQUE KEY inso_admin_email (admin_email),
  INDEX inso_admin_roleid (admin_roleid)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='系统管理员';

-- ----------------------------
-- 系统菜单
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_admin_menu (
  menu_id int(11) NOT NULL AUTO_INCREMENT,
  menu_key varchar(100) NOT NULL COMMENT '菜单节点key',
  menu_permission_key varchar(100) NOT NULL DEFAULT '' COMMENT '权限外键key, 保留',
  menu_pkey varchar(100) NOT NULL DEFAULT '' COMMENT '菜单父节点',
  menu_name varchar(50) NOT NULL DEFAULT '' COMMENT '菜单名称',
  menu_icon varchar(255) DEFAULT '' COMMENT '菜单图标class名',
  menu_level int(2) DEFAULT '0' COMMENT '菜单层级',
  menu_link varchar(255) DEFAULT '' COMMENT '菜单link（用于页面跳转）',
  menu_sort int(11) DEFAULT NULL COMMENT '菜单排序',
  menu_enable_show tinyint(1) DEFAULT 1 COMMENT '是否禁用 0:禁用 1:启用',
  menu_enable_safe tinyint(1) DEFAULT 1 COMMENT '是否禁用 0:禁用 1:启用',
  menu_createtime datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (menu_id),
  UNIQUE KEY inso_admin_menu_key (menu_key),
  index inso_admin_menu_name (menu_name)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='系统菜单';

-- ----------------------------
-- 系统角色
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_admin_role (
  role_id int(11) NOT NULL AUTO_INCREMENT,
  role_name varchar(50) NOT NULL DEFAULT '' COMMENT '角色名',
  role_remark varchar(255) DEFAULT '' COMMENT '备注',
  role_enable tinyint(1) DEFAULT 1 COMMENT '是否禁用 0:禁用 1:启用',
  role_createtime datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (role_id),
  UNIQUE KEY inso_admin_role_name (role_name)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='系统角色';

-- ----------------------------
--  系统权限
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_admin_permission (
  permission_key     varchar(100) NOT NULL  COMMENT '权限key',
  permission_name  varchar(100) NOT NULL  COMMENT '权限名',
  PRIMARY KEY (permission_key)
) ENGINE=InnoDB AUTO_INCREMENT= 1 DEFAULT CHARSET=utf8 COMMENT='系统权限';

-- ----------------------------
--  角色权限组
-- ----------------------------
CREATE TABLE IF NOT EXISTS inso_admin_role_permission (
  role_permission_roleid            int(11) NOT NULL  COMMENT 'role id',
  role_permission_permission_key    varchar(100) NOT NULL  COMMENT 'permission id',
  role_permission_remark            varchar(100) NOT NULL DEFAULT '' COMMENT '',
  PRIMARY KEY (role_permission_roleid, role_permission_permission_key)
) ENGINE=InnoDB AUTO_INCREMENT= 1 DEFAULT CHARSET=utf8 COMMENT='角色权限表';
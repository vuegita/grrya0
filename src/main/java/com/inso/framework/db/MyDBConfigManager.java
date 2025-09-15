package com.inso.framework.db;

public class MyDBConfigManager {

	public static final int DEFAULT_JDBC_INIT_SIZE = 1;
	public static final int DEFAULT_JDBC_MIN_IDLE = 5;
	public static final int DEFAULT_JDBC_MAX_ACTIVE = 150;
	public static final int DEFAULT_JDBC_TIME_BETWEEN_EVICTION_RUN = 1000 * 60; // 空闲检测时间
	public static final int DEFAULT_JDBC_IDLE_MINUTE = 60 * 3; // 60 分钟, mysql
																// 默认最大超时为8小时

	public static final String DB_GLOBAL_MASTER = "globaldb.master";
	public static final String DB_GLOBAL_SLAVE = "globaldb.slave1";


	public static enum DBCenter {
		GLOBAL_MASTER {
			public String getValue() {
				return DB_GLOBAL_MASTER;
			}
		},
		GLOBAL_SLAVE {
			public String getValue() {
				return DB_GLOBAL_SLAVE;
			}
		},;
		public abstract String getValue();
	}
}

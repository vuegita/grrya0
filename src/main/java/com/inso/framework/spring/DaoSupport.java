package com.inso.framework.spring;

import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.inso.framework.db.jdbc.JdbcService;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;


public abstract class DaoSupport {

    protected Log LOG = LogFactory.getLog(this.getClass());

    private static String SPLIT = ",";
    private static String WHERE_SPLIT = " and ";

    @Autowired
    @Qualifier("masterJdbcService")
    protected JdbcService mWriterJdbcService;

    @Autowired
    @Qualifier("slaveJdbcService")
    protected JdbcService mSlaveJdbcService;

    /**
     * 插入数据
     *
     * @param tableName
     * @param keyValues
     * @return 返回自增主键
     */
    public long persistentOfReturnPK(String tableName, LinkedHashMap<String, Object> keyValues) {
        int size = keyValues.size();
        Object[] values = new Object[keyValues.size()];
        int index = 0;

        StringBuffer sql = new StringBuffer("insert into " + tableName + "(");
        boolean isFirst = true;
        for (String key : keyValues.keySet()) {
            if (isFirst) isFirst = false;
            else sql.append(SPLIT);
            sql.append(key);

            Object value = keyValues.get(key);
            values[index++] = value;
        }
        sql.append(") values(");
        isFirst = true;
        for (int i = 0; i < size; i++) {
            if (isFirst) isFirst = false;
            else sql.append(SPLIT);
            sql.append("?");
        }
        sql.append(")");
        return mWriterJdbcService.executeInsert(sql.toString(), values);
    }

    /**
     * 插入数据,没有返回主键, 当表没有自增主键时只能使用此方法
     *
     * @param tableName
     * @param keyValues
     */
    public void persistent(String tableName, LinkedHashMap<String, Object> keyValues) {
        int size = keyValues.size();
        Object[] values = new Object[keyValues.size()];
        int index = 0;

        StringBuffer sql = new StringBuffer("insert into " + tableName + "(");
        boolean isFirst = true;
        for (String key : keyValues.keySet()) {
            if (isFirst) isFirst = false;
            else sql.append(SPLIT);
            sql.append(key);

            Object value = keyValues.get(key);
            values[index++] = value;
        }
        sql.append(") values(");
        isFirst = true;
        for (int i = 0; i < size; i++) {
            if (isFirst) isFirst = false;
            else sql.append(SPLIT);
            sql.append("?");
        }
        sql.append(")");
        mWriterJdbcService.executeUpdate(sql.toString(), values);
    }

    public int delete(String tablename, LinkedHashMap<String, Object> keyValues) {
        String equalParams = " = ? ";

        Object[] values = new Object[keyValues.size()];
        int index = 0;
        StringBuffer sql = new StringBuffer("delete from " + tablename + " where ");
        boolean isFirst = true;
        for (String key : keyValues.keySet()) {
            if (isFirst) isFirst = false;
            else sql.append(WHERE_SPLIT);
            sql.append(key).append(equalParams);
            Object value = keyValues.get(key);
            values[index++] = value;
        }
        return mWriterJdbcService.executeUpdate(sql.toString(), values);
    }

    public int update(String tablename, LinkedHashMap<String, Object> setKeyValues, LinkedHashMap<String, Object> whereKeyValus) {
        int size = setKeyValues.size() + whereKeyValus.size();
        Object[] values = new Object[size];
        int index = 0;
        StringBuffer sql = new StringBuffer("update " + tablename + " set ");

        String equalParams = " = ? ";

        // set sql
        boolean isFirst = true;
        for (String key : setKeyValues.keySet()) {
            if (isFirst) isFirst = false;
            else sql.append(SPLIT);
            sql.append(key).append(equalParams);
            Object value = setKeyValues.get(key);
            values[index++] = value;
        }

        // where sql
        sql.append(" where ");
        isFirst = true;
        for (String key : whereKeyValus.keySet()) {
            if (isFirst) isFirst = false;
            else sql.append(WHERE_SPLIT);
            sql.append(key).append(equalParams);
            Object value = whereKeyValus.get(key);
            values[index++] = value;
        }

        return mWriterJdbcService.executeUpdate(sql.toString(), values);
    }
    
    /**
     *  通过前缀sql来构建sql
     * @param prefixSql 前缀sql， 如 select user_name from pangu_user
     * @param clazz
     * @param whereKeyValue
     * @param startOffset
     * @param endOffset
     * @return
     */
    public <T> List<T> queryScrollBySQL(String prefixSql, Class<T> clazz, LinkedHashMap<String, Object> whereKeyValue, long startOffset, long endOffset) {
    	if(whereKeyValue == null || whereKeyValue.isEmpty())
    	{
    		String sql = prefixSql + " limit ?, ?";
    		return mSlaveJdbcService.queryForList(sql, clazz, startOffset, endOffset);
    	}
    	Object[] values = new Object[whereKeyValue.size() + 2];
        StringBuffer sql = new StringBuffer(prefixSql + " where 1 = 1 ");
        
        int index = 0;
        for (String key : whereKeyValue.keySet()) {
            Object value = whereKeyValue.get(key);
            if (value != null) {
                sql.append(WHERE_SPLIT).append(key);
                values[index ++] = value;
            }
        }
        values[index ++] = startOffset;
        values[index ++] = endOffset;
        
        sql.append(" limit ?, ?");
        return mSlaveJdbcService.queryForList(sql.toString(), clazz, values);
    }

    /**
     * 通过表名来构建sql
     * @param tableName
     * @param clazz
     * @param whereKeyValue
     * @return
     */
    public <T> List<T> queryScrollByName(String tableName, Class<T> clazz, LinkedHashMap<String, Object> whereKeyValue, long startOffset, long endOffset) {
    	if(whereKeyValue == null || whereKeyValue.isEmpty())
    	{
    		String sql = "select * from " + tableName + " limit ?, ?";
    		return mSlaveJdbcService.queryForList(sql, clazz, startOffset, endOffset);
    	}
    	Object[] values = new Object[whereKeyValue.size() + 2];
        StringBuffer sql = new StringBuffer("select * from " + tableName + " where 1 = 1 ");
        
        int index = 0;
        for (String key : whereKeyValue.keySet()) {
            Object value = whereKeyValue.get(key);
            if (value != null) {
                sql.append(WHERE_SPLIT).append(key);
                values[index ++] = value;
            }
        }
        values[index ++] = startOffset;
        values[index ++] = endOffset;
        
        sql.append(" limit ?, ?");
        return mSlaveJdbcService.queryForList(sql.toString(), clazz, values);
    }

    public long count(String tableName, LinkedHashMap<String, Object> keyValue) {
    	if(keyValue == null || keyValue.isEmpty())
    	{
    		String sql = "select count(1) from " + tableName;
    		return mSlaveJdbcService.count(sql);
    	}
    	Object[] values = new Object[keyValue.size()];
        StringBuffer sql = new StringBuffer("select count(1) from " + tableName + " where 1 = 1 ");
        
        int index = 0;
        for (String key : keyValue.keySet()) {
            Object value = keyValue.get(key);
            if (value != null) {
                sql.append(WHERE_SPLIT).append(key);
                values[index ++] = value;
            }
        }
        return mSlaveJdbcService.count(sql.toString(), values);
    }

}

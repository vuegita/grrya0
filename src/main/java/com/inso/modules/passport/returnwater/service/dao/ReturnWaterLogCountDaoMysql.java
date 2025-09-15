package com.inso.modules.passport.returnwater.service.dao;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.PageVo;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.RowPager;
import com.inso.modules.passport.returnwater.model.ReturnWaterLog;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashMap;
import java.util.List;

@Repository
public class ReturnWaterLogCountDaoMysql extends DaoSupport implements ReturnWaterLogCountDao {


    public static String TABLE = "inso_passport_return_water_log_count";


    public void addLog(long userid, String username)
    {
        LinkedHashMap<String, Object> keyValue = Maps.newLinkedHashMap();
        keyValue.put("log_userid", userid);
        keyValue.put("log_username", username);

        keyValue.put("log_level1_count", 0);
        keyValue.put("log_level2_count", 0);

        persistentOfReturnPK(TABLE, keyValue);
    }

    public void updateCount(long userid, int level)
    {
        if(level == 1)
        {
            String sql = "update " + TABLE + " set log_level1_count = log_level1_count + 1 where log_userid = ?";
            mWriterJdbcService.executeUpdate(sql, userid);
        }
        else
        {
            String sql = "update " + TABLE + " set log_level2_count = log_level2_count + 1 where log_userid = ?";
            mWriterJdbcService.executeUpdate(sql, userid);
        }
    }


    public ReturnWaterLog findByUserid(long userid)
    {
        String sql = "select * from " + TABLE + " where log_userid = ?";
        return mWriterJdbcService.queryForObject(sql, ReturnWaterLog.class, userid);
    }

    @Override
    public RowPager<ReturnWaterLog> queryScrollPage(PageVo pageVo, long userid)
    {
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder(" from " + TABLE + " as A");

        whereSQLBuffer.append(" inner join inso_passport_user as B on A.log_userid = B.user_id and B.user_type='member' ");

        whereSQLBuffer.append(" where 1 = 1");

        if(userid > 0)
        {
            values.add(userid);
            whereSQLBuffer.append(" and log_userid = ? ");
        }

        whereSQLBuffer.append(" and log_level1_count > 0 ");

        String whereSQL = whereSQLBuffer.toString();
        String countsql = "select count(1) " + whereSQL;
        long total = mSlaveJdbcService.count(countsql, values.toArray());

        if(total == 0)
        {
            return RowPager.getEmptyRowPager();
        }

        StringBuilder select = new StringBuilder("select A.* ");
        select.append(whereSQL);
        select.append(" order by log_userid desc ");
        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
        List<ReturnWaterLog> list = mSlaveJdbcService.queryForList(select.toString(), ReturnWaterLog.class, values.toArray());
        RowPager<ReturnWaterLog> rowPage = new RowPager<>(total, list);
        return rowPage;
    }


}

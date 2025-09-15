package com.inso.modules.game.service.dao;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import com.google.common.collect.Lists;
import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.game.model.BusinessReport;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Maps;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.model.Status;
import com.inso.modules.game.model.GameInfo;
import com.inso.modules.game.model.GameCategory;

@Repository
public class GameDaoMysql extends DaoSupport implements GameDao{

    private static final String TABLE = "inso_game";

    public long add(GameCategory category, String key, String title, String describe, String icon, Status status, long sort)
    {
        Date date = new Date();

        LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();

        keyvalue.put("game_title", StringUtils.getNotEmpty(title));
        keyvalue.put("game_key", key);
        keyvalue.put("game_icon", StringUtils.getNotEmpty(icon));


        keyvalue.put("game_category_key", category.getKey());
        keyvalue.put("game_category_name", category.getName());
        keyvalue.put("game_describe", StringUtils.getNotEmpty(describe));

        keyvalue.put("game_status", status.getKey());



        keyvalue.put("game_createtime", date);

        return persistentOfReturnPK(TABLE, keyvalue);
    }

    public void updateStatus(long gameid, Status status, long sort)
    {
        String sql = "update " + TABLE + " set game_status = ?, game_sort = ? where game_id = ?";
        mWriterJdbcService.executeUpdate(sql, status.getKey(), sort, gameid);
    }

    public GameInfo findById(long gameid)
    {
        String sql = "select * from " + TABLE + " where game_id = ?";
        return mSlaveJdbcService.queryForObject(sql, GameInfo.class, gameid);
    }

    public GameInfo findByIdByKey(String key)
    {
        String sql = "select * from " + TABLE + " where game_key = ?";
        return mSlaveJdbcService.queryForObject(sql, GameInfo.class, key);
    }

    public List<GameInfo> queryAll()
    {
       String sql = "select * from " + TABLE;
        return mSlaveJdbcService.queryForList(sql, GameInfo.class);
    }

    public List<GameInfo> queryByCategory(GameCategory gameCategory)
    {
        String sql = "select * from " + TABLE + " where game_category_key = ? and game_status = ? order by game_sort asc ";
        return mSlaveJdbcService.queryForList(sql, GameInfo.class, gameCategory.getKey(), Status.ENABLE.getKey());
    }

    public RowPager<GameInfo> queryScrollPage(PageVo pageVo, GameCategory gameCategory)
    {
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder("  where 1 = 1");

        // 时间放前面
//        whereSQLBuffer.append(" and game_pdate between ? and ? ");
//        values.add(pageVo.getFromTime());
//        values.add(pageVo.getToTime());

        if(gameCategory != null)
        {
            values.add(gameCategory.getKey());
            whereSQLBuffer.append(" and game_category_key = ? ");
        }

        String whereSQL = whereSQLBuffer.toString();
        String countsql = "select count(1) from  " + TABLE + whereSQL;
        long total = mSlaveJdbcService.count(countsql, values.toArray());

        if(total == 0)
        {
            return RowPager.getEmptyRowPager();
        }

        StringBuilder select = new StringBuilder("select * from ");
        select.append(TABLE);
        select.append(whereSQL);
        select.append(" order by game_id asc ");
        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());

        List<GameInfo> list = mSlaveJdbcService.queryForList(select.toString(), GameInfo.class, values.toArray());
        RowPager<GameInfo> rowPage = new RowPager<>(total, list);
        return rowPage;
    }
}

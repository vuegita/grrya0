package com.inso.modules.ad.mall.service.dao;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.RowPager;
import com.inso.modules.ad.core.model.AdMaterielInfo;
import com.inso.modules.ad.core.service.dao.MaterielDaoMysql;
import com.inso.modules.ad.mall.model.MallCommodityInfo;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.model.UserInfo;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@Repository
public class MallCommodityDaoMysql extends DaoSupport implements MallCommodityDao {

    public static final String TABLE = "inso_ad_mall_materiel_commodity";

    /**
     mc_id                 int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
     mc_merchantid         int(11) UNSIGNED NOT NULL ,
     mc_merchantname       varchar(100) NOT NULL DEFAULT '' comment  '所属商家',
     mc_materielid         int(11) UNSIGNED NOT NULL ,
     mc_status             varchar(50) NOT NULL comment 'enable|disable',
     mc_createtime         datetime DEFAULT NULL comment '创建时间',
     mc_remark             varchar(5000) NOT NULL DEFAULT '' COMMENT '',
     */

    @Override
    public void addCategory(UserInfo userInfo, AdMaterielInfo materielInfo, Status status)
    {
        Date date = new Date();

        LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();
        keyvalue.put("mc_merchantid", userInfo.getId());
        keyvalue.put("mc_merchantname", userInfo.getName());
        keyvalue.put("mc_materielid", materielInfo.getId());
        keyvalue.put("mc_categoryid", materielInfo.getCategoryid());
        keyvalue.put("mc_status", status.getKey());
        keyvalue.put("mc_createtime", date);

        persistent(TABLE, keyvalue);
    }

    public void updateInfo(long id, Status status)
    {
        LinkedHashMap<String, Object> setKeyValue = Maps.newLinkedHashMap();
        setKeyValue.put("mc_status", status.getKey());

        LinkedHashMap<String, Object> whereKeyValue = Maps.newLinkedHashMap();
        whereKeyValue.put("mc_id", id);
        update(TABLE, setKeyValue, whereKeyValue);
    }

    public MallCommodityInfo findById(long id)
    {
        String sql = "select * from " + TABLE + " where mc_id = ?";
        return mSlaveJdbcService.queryForObject(sql, MallCommodityInfo.class, id);
    }

    public MallCommodityInfo findByKey(long merchantid, long materielid)
    {
        String sql = "select * from " + TABLE + " where mc_merchantid = ? and mc_materielid = ?";
        return mSlaveJdbcService.queryForObject(sql, MallCommodityInfo.class, merchantid, materielid);
    }


    public void queryAll(Callback<MallCommodityInfo> callback, long minId, int limit)
    {
        StringBuilder whereSQLBuffer = new StringBuilder();
        whereSQLBuffer.append("select A.*");
        whereSQLBuffer.append(", B.materiel_price as mc_price ");
        whereSQLBuffer.append(" from ").append(TABLE).append(" as A ");
        whereSQLBuffer.append(" left join ").append(MaterielDaoMysql.TABLE).append(" as B on A.mc_materielid=B.materiel_id ");
        whereSQLBuffer.append(" where mc_id > ? limit ").append(limit);
        mSlaveJdbcService.queryAll(callback, whereSQLBuffer.toString(), MallCommodityInfo.class, minId);
    }

    @Override
    public RowPager<MallCommodityInfo> queryScrollPage(PageVo pageVo, Status status, long merchantid)
    {
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder();
        whereSQLBuffer.append(" from ").append(TABLE).append(" as A ");
        whereSQLBuffer.append(" left join ").append(MaterielDaoMysql.TABLE).append(" as B on A.mc_materielid=B.materiel_id ");
        whereSQLBuffer.append(" left join ").append(MallRecommendDaoMysql.TABLE).append(" as C on A.mc_id=C.recommend_commodityid ");
        whereSQLBuffer.append(" where 1 = 1 ");

        if(merchantid > 0)
        {
            values.add(merchantid);
            whereSQLBuffer.append(" and mc_merchantid = ? ");
        }

        // 时间放前面
        else if(pageVo.getFromTime() != null)
        {
            whereSQLBuffer.append(" and mc_createtime between ? and ? ");
            values.add(pageVo.getFromTime());
            values.add(pageVo.getToTime());
        }

        if(status != null)
        {
            values.add(status.getKey());
            whereSQLBuffer.append(" and mc_status = ? ");
        }

        String whereSQL = whereSQLBuffer.toString();
        String countsql = "select count(1) " + whereSQL;
        long total = mSlaveJdbcService.count(countsql, values.toArray());

        if(total == 0)
        {
            return RowPager.getEmptyRowPager();
        }

        StringBuilder select = new StringBuilder("select A.* ");
        select.append(", B.materiel_price as mc_price ");
        select.append(", C.recommend_commodityid as mc_commodityid ");
        select.append(whereSQL);
        select.append(" order by mc_id desc ");
        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());

        List<MallCommodityInfo> list = mSlaveJdbcService.queryForList(select.toString(), MallCommodityInfo.class, values.toArray());
        RowPager<MallCommodityInfo> rowPage = new RowPager<>(total, list);
        return rowPage;
    }

}

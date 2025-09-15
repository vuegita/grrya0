package com.inso.modules.ad.mall.service.dao;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.PageVo;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.RowPager;
import com.inso.modules.ad.core.model.AdMaterielInfo;
import com.inso.modules.ad.core.service.dao.MaterielDaoMysql;
import com.inso.modules.ad.mall.model.MallCommodityInfo;
import com.inso.modules.ad.mall.model.MallRecommendInfo;
import com.inso.modules.ad.mall.model.MallRecommentType;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@Repository
public class MallRecommendDaoMysql extends DaoSupport implements MallRecommendDao {

    public static final String TABLE = "inso_ad_mall_materiel_recommend";

    /**
     recommend_id                 int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
     recommend_merchantid         int(11) UNSIGNED NOT NULL ,
     recommend_merchantname       varchar(100) NOT NULL DEFAULT '' comment  '所属商家',
     recommend_materielid         int(11) UNSIGNED NOT NULL ,
     recommend_categoryid         int(11) UNSIGNED NOT NULL ,
     recommend_commodityid        int(11) UNSIGNED NOT NULL ,
     recommend_createtime         datetime DEFAULT NULL comment '创建时间',
     recommend_remark             varchar(5000) NOT NULL DEFAULT '' COMMENT '',
     */

    @Override
    public void addCategory(MallCommodityInfo materielInfo, MallRecommentType recommentType, long sort)
    {
        Date date = new Date();

        LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();
        keyvalue.put("recommend_merchantid", materielInfo.getMerchantid());
        keyvalue.put("recommend_merchantname", materielInfo.getMerchantname());
        keyvalue.put("recommend_materielid", materielInfo.getMaterielid());
        keyvalue.put("recommend_categoryid", materielInfo.getCategoryid());
        keyvalue.put("recommend_commodityid", materielInfo.getId());
        keyvalue.put("recommend_type", recommentType.getKey());
        keyvalue.put("recommend_sort", sort);

        keyvalue.put("recommend_createtime", date);

        persistent(TABLE, keyvalue);
    }

    public void updateInfo(long id, MallRecommentType recommentType, long sort)
    {
        String sql = "update  " + TABLE + " set recommend_type = ?, recommend_sort = ? where recommend_id = ?";
        mWriterJdbcService.executeUpdate(sql, recommentType.getKey(), sort, id);
    }

    public MallRecommendInfo findById(long id)
    {
        String sql = "select * from " + TABLE + " where recommend_id = ?";
        return mSlaveJdbcService.queryForObject(sql, MallRecommendInfo.class, id);
    }


    public void deleteByid(long id)
    {
        String sql = "delete from " + TABLE + " where recommend_id = ?";
        mWriterJdbcService.executeUpdate(sql, id);
    }

    public List<MallRecommendInfo> queryListByType(MallRecommentType recommentType)
    {
        String sql = "select * from " + TABLE + " where recommend_id = ?";
        return mSlaveJdbcService.queryForList(sql, MallRecommendInfo.class, recommentType.getKey());
    }

    public List<AdMaterielInfo> queryScrollByType(MallRecommentType recommentType)
    {
        StringBuilder sql = new StringBuilder();
        sql.append("select B.* ");
        sql.append(", A.recommend_merchantid as materiel_merchantid ");
        sql.append(", A.recommend_merchantname as materiel_merchantname ");
        sql.append(", A.recommend_commodityid as materiel_commodityid ");
        sql.append(" from ").append(TABLE).append(" as A");
        sql.append(" left join ").append(MaterielDaoMysql.TABLE).append(" as B on A.recommend_materielid=B.materiel_id");
        return mSlaveJdbcService.queryForList(sql.toString(), AdMaterielInfo.class);
    }

    @Override
    public RowPager<MallRecommendInfo> queryScrollPage(PageVo pageVo, MallRecommentType type, long merchantid)
    {
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder();
        whereSQLBuffer.append(" from ").append(TABLE).append(" as A ");
        whereSQLBuffer.append(" left join ").append(MaterielDaoMysql.TABLE).append(" as B on A.recommend_materielid=B.materiel_id ");
        whereSQLBuffer.append(" where 1 = 1 ");

        if(merchantid > 0)
        {
            values.add(merchantid);
            whereSQLBuffer.append(" and recommend_merchantid = ? ");
        }

        // 时间放前面
        else if(pageVo.getFromTime() != null)
        {
            whereSQLBuffer.append(" and recommend_createtime between ? and ? ");
            values.add(pageVo.getFromTime());
            values.add(pageVo.getToTime());
        }

        if(type != null)
        {
            values.add(type.getKey());
            whereSQLBuffer.append(" and recommend_type = ? ");
        }

        String whereSQL = whereSQLBuffer.toString();
        String countsql = "select count(1) " + whereSQL;
        long total = mSlaveJdbcService.count(countsql, values.toArray());

        if(total == 0)
        {
            return RowPager.getEmptyRowPager();
        }

        StringBuilder select = new StringBuilder("select A.* ");
        select.append(", B.materiel_price as recommend_price ");
        select.append(whereSQL);
        if(type == null)
        {
            select.append(" order by recommend_id desc ");
        }
        else
        {
            select.append(" order by recommend_sort asc  ");
        }

        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());

        List<MallRecommendInfo> list = mSlaveJdbcService.queryForList(select.toString(), MallRecommendInfo.class, values.toArray());
        RowPager<MallRecommendInfo> rowPage = new RowPager<>(total, list);
        return rowPage;
    }

}

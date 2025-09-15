package com.inso.modules.web.service.dao;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.PageVo;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.model.BannerType;
import com.inso.modules.common.model.Status;
import com.inso.modules.web.model.Banner;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@Repository
public class BannerDaoMysql extends DaoSupport implements BannerDao {

    private static final String TABLE = "inso_web_banner";

    public void addBanner(String title, String content, BannerType bannerType, String img, String webUrl, Status forceLogin, Status status, JSONObject remark)
    {
        LinkedHashMap<String, Object> keyValues = Maps.newLinkedHashMap();
        keyValues.put("banner_title", title);
        keyValues.put("banner_content", content);
        keyValues.put("banner_type", bannerType.getKey());
        keyValues.put("banner_img", img);
        keyValues.put("banner_web_url", webUrl);
        keyValues.put("banner_force_login", forceLogin.getKey());
        keyValues.put("banner_status", status.getKey());

        keyValues.put("banner_admin",  Status.DISABLE.getKey());
        keyValues.put("banner_createtime", new Date());
        keyValues.put("banner_updatetime", new Date());
        if(remark != null && !remark.isEmpty())
        {
            keyValues.put("banner_remark", remark.toJSONString());
        }
        persistent(TABLE, keyValues);
    }

    public void updateInfo(long id, String title, String content, BannerType bannerType, String img, String webUrl, Status forceLogin, Status status, JSONObject remark)
    {
        LinkedHashMap setKeyValue = Maps.newLinkedHashMap();
        if(!StringUtils.isEmpty(title))
        {
            setKeyValue.put("banner_title", title);
        }

        if(!StringUtils.isEmpty(content))
        {
            setKeyValue.put("banner_content", content);
        }

        if(bannerType != null)
        {
            setKeyValue.put("banner_type", bannerType.getKey());
        }

        if(!StringUtils.isEmpty(img))
        {
            setKeyValue.put("banner_img", img);
        }

        if(!StringUtils.isEmpty(webUrl))
        {
            setKeyValue.put("banner_web_url", webUrl);
        }

        if(forceLogin != null)
        {
            setKeyValue.put("banner_status", forceLogin.getKey());
        }

        if(status != null)
        {
            setKeyValue.put("banner_status", status.getKey());
        }


        if(remark != null && !remark.isEmpty())
        {
            setKeyValue.put("banner_remark", remark.toJSONString());
        }

        setKeyValue.put("banner_updatetime", new Date());

        LinkedHashMap where = Maps.newLinkedHashMap();
        where.put("banner_id", id);

        update(TABLE, setKeyValue, where);
    }

    public Banner findById(long id)
    {
        String sql = "select * from " + TABLE + " where banner_id = ?";
        return mSlaveJdbcService.queryForObject(sql, Banner.class, id);
    }

    public void deleteById(long id)
    {
        String sql = "delete from " + TABLE + " where banner_id = ?";
        mWriterJdbcService.executeUpdate(sql, id);
    }

    public List<Banner> queryAllByBannerType(BannerType bannerType, Status status)
    {
        String sql="";
        sql = "select * from " + TABLE + " where banner_status = ? ";
        return mSlaveJdbcService.queryForList(sql, Banner.class, status.getKey());
    }

    public RowPager<Banner> queryScrollPage(PageVo pageVo, BannerType bannerType, Status status)
    {
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder(" where 1 = 1");

//        // 时间放前面
//        whereSQLBuffer.append(" and card_createtime between ? and ? ");
//        values.add(pageVo.getFromTime());
//        values.add(pageVo.getToTime());

        if(bannerType != null)
        {
            values.add(bannerType.getKey());
            whereSQLBuffer.append(" and banner_type = ? ");
        }

        if(status != null)
        {
            values.add(status.getKey());
            whereSQLBuffer.append(" and banner_status = ? ");
        }

        String whereSQL = whereSQLBuffer.toString();
        String countsql = "select count(1) from  " + TABLE + whereSQL;
        long total = mSlaveJdbcService.count(countsql, values.toArray());
        if(total == 0)
        {
            return RowPager.getEmptyRowPager();
        }

        StringBuilder select = new StringBuilder("select * from  ");
        select.append(TABLE);
        select.append(whereSQL);
        select.append(" order by banner_updatetime desc ");
        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
        List<Banner> list = mSlaveJdbcService.queryForList(select.toString(), Banner.class, values.toArray());
        RowPager<Banner> rowPage = new RowPager<>(list.size(), list);
        return rowPage;
    }

}

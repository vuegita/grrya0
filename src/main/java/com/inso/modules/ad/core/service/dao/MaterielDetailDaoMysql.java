package com.inso.modules.ad.core.service.dao;

import com.google.common.collect.Maps;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.ad.core.model.AdMaterielDetailInfo;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.LinkedHashMap;

@Repository
public class MaterielDetailDaoMysql extends DaoSupport implements MaterielDetailDao {

    /**
     detail_materielid         int(11) UNSIGNED NOT NULL ,
     detail_content            varchar(2048) NOT NULL DEFAULT '' comment '详情介绍',
     detail_sizes              varchar(500) NOT NULL DEFAULT '' comment '尺寸大小,多个以逗号隔开',
     detail_images             varchar(5000) NOT NULL DEFAULT '' comment '图片,多个以逗号隔开',
     detail_createtime         datetime DEFAULT NULL comment '创建时间',
     detail_remark             varchar(5000) NOT NULL DEFAULT '' COMMENT '',
     */
    public static final String TABLE = "inso_ad_materiel_detail";

    @Override
    public void add(long materielid, String content, String sizes, String images)
    {
        Date date = new Date();


        LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();
        keyvalue.put("detail_materielid", materielid);
        keyvalue.put("detail_content", StringUtils.getNotEmpty(content));
        keyvalue.put("detail_sizes", StringUtils.getNotEmpty(sizes));
        keyvalue.put("detail_images", StringUtils.getNotEmpty(images));
        keyvalue.put("detail_createtime", date);

        persistent(TABLE, keyvalue);
    }

    public void updateInfo(long id, String content, String sizes, String images)
    {
        LinkedHashMap<String, Object> setKeyValue = Maps.newLinkedHashMap();

        if(!StringUtils.isEmpty(content))
        {
            setKeyValue.put("detail_content", content);
        }

        if(!StringUtils.isEmpty(sizes))
        {
            setKeyValue.put("detail_sizes", sizes);
        }

        if(!StringUtils.isEmpty(images))
        {
            setKeyValue.put("detail_images", images);
        }


        LinkedHashMap<String, Object> whereKeyValue = Maps.newLinkedHashMap();
        whereKeyValue.put("detail_materielid", id);
        update(TABLE, setKeyValue, whereKeyValue);
    }

    public AdMaterielDetailInfo findById(long id)
    {
        String sql = "select * from " + TABLE + " where detail_materielid = ?";
        return mSlaveJdbcService.queryForObject(sql, AdMaterielDetailInfo.class, id);
    }


}

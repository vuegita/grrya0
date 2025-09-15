package com.inso.modules.passport.share_holder.service.dao;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.PageVo;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.share_holder.model.ShareHolderInfo;
import com.inso.modules.passport.user.model.UserInfo;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@Repository
public class ShareHolderDaoMysql extends DaoSupport implements ShareHolderDao {

    /**
     holder_id               int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
     holder_userid           int(11) NOT NULL ,
     holder_username         varchar(255) NOT NULL ,

     holder_lv1_rw_status    varchar(20) NOT NULL DEFAULT 'enable' COMMENT 'enable|disable',
     holder_lv2_rw_status    varchar(20) NOT NULL DEFAULT 'enable' COMMENT 'enable|disable',
     holder_system_status    varchar(20) NOT NULL DEFAULT 'enable' COMMENT 'apply|enable|disable',

     holder_createtime       datetime NOT NULL ,

     holder_remark           varchar(255) NOT NULL DEFAULT '' COMMENT '',
     */
    private static final String TABLE = "inso_passport_share_holder";

    @Override
    public void add(UserInfo userInfo, Status lv1RwStatus, Status lv2RwStatus, Status sysStatus)
    {
        Date date = new Date();

        LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();
        keyvalue.put("holder_userid", userInfo.getId());
        keyvalue.put("holder_username", userInfo.getName());
        keyvalue.put("holder_lv1_rw_status", lv1RwStatus.getKey());
        keyvalue.put("holder_lv2_rw_status", lv2RwStatus.getKey());
        keyvalue.put("holder_system_status", sysStatus.getKey());
        keyvalue.put("holder_createtime", date);

        persistent(TABLE, keyvalue);
    }

    public void updateInfo(long userid, Status lv1RwStatus, Status lv2RwStatus, Status sysStatus)
    {
        LinkedHashMap<String, Object> setKeyValue = Maps.newLinkedHashMap();

        if(lv1RwStatus != null)
        {
            setKeyValue.put("holder_lv1_rw_status", lv1RwStatus.getKey());
        }

        if(lv2RwStatus != null)
        {
            setKeyValue.put("holder_lv2_rw_status", lv2RwStatus.getKey());
        }

        if(sysStatus != null)
        {
            setKeyValue.put("holder_system_status", sysStatus.getKey());
        }

        LinkedHashMap<String, Object> whereKeyValue = Maps.newLinkedHashMap();
        whereKeyValue.put("holder_userid", userid);
        update(TABLE, setKeyValue, whereKeyValue);
    }

    public ShareHolderInfo findByUserId(long userid)
    {
        StringBuilder sql = new StringBuilder("select * from " + TABLE);
        sql.append(" where holder_userid = ?");
        return mSlaveJdbcService.queryForObject(sql.toString(), ShareHolderInfo.class, userid);
    }


    @Override
    public RowPager<ShareHolderInfo> queryScrollPage(PageVo pageVo, long userid, Status sysStatus)
    {
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder();
        whereSQLBuffer.append(TABLE).append(" as A ");
        whereSQLBuffer.append(" where 1 = 1 ");

        // 时间放前面
//            whereSQLBuffer.append(" and holder_createtime between ? and ? ");
//            values.add(pageVo.getFromTime());
//            values.add(pageVo.getToTime());

        if(userid > 0)
        {
            values.add(userid);
            whereSQLBuffer.append(" and holder_userid = ? ");
        }

        if(sysStatus != null)
        {
            values.add(sysStatus.getKey());
            whereSQLBuffer.append(" and holder_system_status = ? ");
        }

        String whereSQL = whereSQLBuffer.toString();
        String countsql = "select count(1) from " + whereSQL;
        long total = mSlaveJdbcService.count(countsql, values.toArray());

        if(total == 0)
        {
            return RowPager.getEmptyRowPager();
        }

        StringBuilder select = new StringBuilder();
        select.append("select * from ");
        select.append(whereSQL);
        select.append(" order by holder_id desc ");
        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
        List<ShareHolderInfo> list = mSlaveJdbcService.queryForList(select.toString(), ShareHolderInfo.class, values.toArray());
        RowPager<ShareHolderInfo> rowPage = new RowPager<>(total, list);
        return rowPage;
    }

}

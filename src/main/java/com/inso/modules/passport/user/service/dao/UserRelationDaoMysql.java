package com.inso.modules.passport.user.service.dao;

import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.PageVo;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.RowPager;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.model.UserRelation;

@Repository
public class UserRelationDaoMysql extends DaoSupport implements UserRelationDao {

    private static final String TABLE = "inso_passport_user_relation";

    public void addRelation(long ancestorId, long descendantid, long depth)
    {
        LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();
        keyvalue.put("relation_ancestor", ancestorId);
        keyvalue.put("relation_descendant", descendantid);
        keyvalue.put("relation_depth", depth);
        persistent(TABLE, keyvalue);
    }

    /**
     * 删除子节点和祖先的关系
     * @param notDelAncestorId
     * @param descentdantid
     */
    public void deleteAncestorRelation(long notDelAncestorId, long descentdantid)
    {
        String sql = "delete from " + TABLE + " where relation_descendant = ? and relation_ancestor != ? and relation_depth != 0";
        mWriterJdbcService.executeUpdate(sql, descentdantid, notDelAncestorId);
    }

    public List<UserRelation> queryAllParentByDescendantid(long descendantid)
    {
        String sql = "select * from " + TABLE + " where relation_descendant = ?";
        return mSlaveJdbcService.queryForList(sql, UserRelation.class, descendantid);
    }

    public UserInfo findParentByDescendantid(long descendantid, int depth)
    {
        String sql = "select B.* from " + TABLE + " as A inner join inso_passport_user as B on A.relation_ancestor = B.user_id  where relation_descendant = ? and relation_depth = ?";
        return mSlaveJdbcService.queryForObject(sql, UserInfo.class, descendantid, depth);
    }

    /**
     * 根据祖先id查询所有子集, 包括自己
     * @param ancestorid  祖先id
     * @return
     */
    public List<UserRelation> queryAllChildByAncestorid(long ancestorid)
    {
        String sql = "select * from " + TABLE + " where relation_ancestor = ?";
        return mWriterJdbcService.queryForList(sql, UserRelation.class, ancestorid);
    }

    public RowPager<UserInfo> queryScrollPage(PageVo pageVo, long ancestorid, UserInfo.UserType[] userTypes)
    {
        // select B.* from inso_passport_user_relation as A
        // inner join inso_passport_user as B on A.relation_descendant = B.user_id and B.user_type = 'member'
        // where relation_ancestor = 4 and relation_depth != 0;
        List<Object> values = Lists.newArrayList();
        StringBuilder whereSQLBuffer = new StringBuilder(" inner join inso_passport_user as B on A.relation_descendant = B.user_id ");

        whereSQLBuffer.append(" inner join inso_passport_user_money as C on B.user_id=C.money_userid ");

        values.add(ancestorid);
        // 等于0表示当前登陆者自己
//        whereSQLBuffer.append(" where relation_ancestor = ? and relation_depth != 0 ");
        whereSQLBuffer.append(" where relation_ancestor = ? ");

        if(userTypes != null)
        {
            whereSQLBuffer.append(" and (");
            boolean first = true;
            for(UserInfo.UserType userType : userTypes)
            {
                if(first)
                {
                    first = false;
                }
                else
                {
                    whereSQLBuffer.append(" or ");
                }
                whereSQLBuffer.append("  B.user_type = ? ");
                values.add(userType.getKey());

            }
            whereSQLBuffer.append(" ) ");
        }

        String whereSQL = whereSQLBuffer.toString();
        String countsql = "select count(1) from inso_passport_user_relation as A " + whereSQL;
        long total = mSlaveJdbcService.count(countsql, values.toArray());

        StringBuilder select = new StringBuilder("select B.*, C.money_balance as user_balance, C.money_freeze as user_freeze from inso_passport_user_relation as A ");
        select.append(whereSQL);
        select.append(" limit ").append(pageVo.getOffset()).append(",").append(pageVo.getLimit());
        List<UserInfo> list = mSlaveJdbcService.queryForList(select.toString(), UserInfo.class, values.toArray());
        RowPager<UserInfo> rowPage = new RowPager<>(total, list);
        return rowPage;
    }


}

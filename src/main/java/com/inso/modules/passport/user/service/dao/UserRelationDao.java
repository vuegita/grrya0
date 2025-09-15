package com.inso.modules.passport.user.service.dao;

import java.util.List;

import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.model.UserRelation;

public interface UserRelationDao {

    public void addRelation(long ancestorId, long descendantid, long depth);

    public void deleteAncestorRelation(long notDelAncestorId, long descentdantid);

    public List<UserRelation> queryAllParentByDescendantid(long descendantid);
    public List<UserRelation> queryAllChildByAncestorid(long ancestorid);
    public RowPager<UserInfo> queryScrollPage(PageVo pageVo, long ancestorid, UserInfo.UserType[] userType);

    public UserInfo findParentByDescendantid(long descendantid, int depth);
}

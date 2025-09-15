package com.inso.modules.passport.user.service;

import java.util.List;

import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.model.UserRelation;

public interface UserRelationService {

    public void bindRelation(long parentid, List<UserRelation> newParentAncestorList, long childid);
    public void moveRelation(long deleteUserid, List<UserRelation> deleteChildList, long newParentid, List<UserRelation> newParentAncestorList);

    public List<UserRelation> queryAllParentByDescendantid(long descendantid);

    /**
     * 根据祖先id查询所有子集
     * @param ancestorid  祖先id
     * @return
     */
    public List<UserRelation> queryAllChildByAncestorid(long ancestorid);


    public RowPager<UserInfo> queryScrollPage(PageVo pageVo, long ancestorid, UserInfo.UserType[] userType);

    public UserInfo findParentByDescendantid(long descendantid, int depth);
}

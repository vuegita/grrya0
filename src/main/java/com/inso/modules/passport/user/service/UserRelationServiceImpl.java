package com.inso.modules.passport.user.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.CollectionUtils;
import com.inso.framework.utils.RowPager;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.model.UserRelation;
import com.inso.modules.passport.user.service.dao.UserRelationDao;

@Service
public class UserRelationServiceImpl implements UserRelationService{

    @Autowired
    private UserRelationDao mUserRelationDao;


    @Transactional
    public void bindRelation(long parentid, List<UserRelation> newParentAncestorList, long childid)
    {
        // childid - childid
        mUserRelationDao.addRelation(childid, childid, 0);

        if(CollectionUtils.isEmpty(newParentAncestorList))
        {
            mUserRelationDao.addRelation(parentid, parentid, 0);
            mUserRelationDao.addRelation(parentid, childid, 1);
            return;
        }

        for(UserRelation relation : newParentAncestorList)
        {
            mUserRelationDao.addRelation(relation.getAncestor(), childid, relation.getDepth() + 1);
        }
    }

    /**
     * 绑定叶子关系 要限制层级关系, 要求如下
     * 1. 会员和员工关系一旦确定，不能变更
     * @param deleteUserid   被删除关系的id
     * @param deleteChildList  被删除关系的所有子节点-待转入新节点的关系集合
     * @param newParentid       新节点id
     * @param newParentAncestorList 新节点所有祖先
     */
    @Transactional
    public void moveRelation(long deleteUserid, List<UserRelation> deleteChildList, long newParentid, List<UserRelation> newParentAncestorList)
    {
        // 1. 删除child 所有与祖先节点有关系的节点 delete from table where descentdant = deleteUserid and depth != 0
        // deleteChildList 包含 deleteUserid
        for(UserRelation relation : deleteChildList)
        {
            // delete from xxx where relation_descendant = ? and relation_ancestor != ? and relation_depth != 0
            mUserRelationDao.deleteAncestorRelation(deleteUserid, relation.getDescendant());
        }

        // 2. 建立新的关系
        // 即将删除关系节点的所有子节点总深度
        int deleteAncesstorListSize = deleteChildList.size();

        // 新节点祖先总深度
        int newAncestorListSize = newParentAncestorList.size();

        if(deleteAncesstorListSize == 0)
        {
            mUserRelationDao.addRelation(deleteUserid, deleteUserid, 0);
        }

        if(newAncestorListSize == 0)
        {
            mUserRelationDao.addRelation(newParentid, newParentid, 0);
            if(deleteAncesstorListSize == 0)
            {
                mUserRelationDao.addRelation(newParentid, deleteUserid, 1);
                return;
            }

            for(UserRelation model : deleteChildList)
            {
                mUserRelationDao.addRelation(newParentid, model.getDescendant(), model.getDepth() + 1);
            }

            return;
        }

        for(UserRelation newParent : newParentAncestorList)
        {
            if(deleteAncesstorListSize == 0)
            {
                mUserRelationDao.addRelation(newParent.getAncestor(), deleteUserid, newParent.getDepth() + 1);
                continue;
            }

            for(UserRelation model : deleteChildList)
            {
                mUserRelationDao.addRelation(newParent.getAncestor(), model.getDescendant(), model.getDepth() + 1);
            }
        }
    }

    /**
     * 根据后代id查询所有祖先
     * @param descendantid
     * @return
     */
    public List<UserRelation> queryAllParentByDescendantid(long descendantid)
    {
        return mUserRelationDao.queryAllParentByDescendantid(descendantid);
    }

    /**
     * 根据祖先id查询所有子集
     * @param ancestorid  祖先id
     * @return
     */
    public List<UserRelation> queryAllChildByAncestorid(long ancestorid)
    {
        return mUserRelationDao.queryAllChildByAncestorid(ancestorid);
    }

    public RowPager<UserInfo> queryScrollPage(PageVo pageVo, long ancestorid, UserInfo.UserType[] userType)
    {
        return mUserRelationDao.queryScrollPage(pageVo, ancestorid, userType);
    }

    public UserInfo findParentByDescendantid(long descendantid, int depth)
    {
        return mUserRelationDao.findParentByDescendantid(descendantid, depth);
    }
}

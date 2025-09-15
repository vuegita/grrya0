package com.inso.modules.passport.user.logical;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.CollectionUtils;
import com.inso.framework.zookeeper.DistributeLock;
import com.inso.framework.zookeeper.ZKClientManager;
import com.inso.modules.passport.UserErrorResult;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.model.UserRelation;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.user.service.UserRelationService;

@Component
public class RelationManager {

    private static Log LOG = LogFactory.getLog(RelationManager.class);

    private static final String DEDAULT_LOCK_PATH = "/inso/passport/relation_mgr/bind_relation";

    @Autowired
    private UserRelationService mUserRelationService;

    @Autowired
    private UserAttrService mUserAttrService;

    private boolean isExecUpStaffRelationTask = false;

    /**
     * 绑定新节点
     * @param parentid
     * @param childid
     * @return
     */
    public ErrorResult bindLeafRelation(long parentid, long childid)
    {
        //
        String path = DEDAULT_LOCK_PATH;

        ZKClientManager zk = ZKClientManager.getInstanced();
        DistributeLock lock = zk.createLock(path);
        boolean isLock = lock.lockAcquired(3000);

        try {
            if(isLock)
            {
                List<UserRelation> childList = mUserRelationService.queryAllChildByAncestorid(childid);
                if(!CollectionUtils.isEmpty(childList))
                {
                    return SystemErrorResult.ERR_SYS_OPT_FAILURE;
                }

                //
                List<UserRelation> newParentAncestorList = mUserRelationService.queryAllParentByDescendantid(parentid);
                if(CollectionUtils.isEmpty(newParentAncestorList))
                {
                    newParentAncestorList = Collections.emptyList();
                }

                // 层级关系不能超过10层
                if(newParentAncestorList.size() >= 10)
                {
                    return UserErrorResult.ERR_RELATION_LAYER;
                }
                mUserRelationService.bindRelation(parentid, newParentAncestorList, childid);
                return SystemErrorResult.SUCCESS;
            }
        } catch (Exception e) {
            LOG.error("bindLeafRelation error:");
        } finally {
            if(isLock)
            {
                lock.lockReleased();
            }
        }
        return SystemErrorResult.ERR_SYS_OPT_FAILURE;
    }


    /**
     * 限制条件如下
     *  1. 会员和员工关系一旦确定，不能变更（除非会员没有数据）
     *  2. 代理不能直接拉会员
     *  3. 每个会员会和直属上级员工进行关联
     * @return
     */
    public ErrorResult moveRelation(UserInfo parentUserInfo, UserInfo childUserInfo)
    {
        //
        String path = DEDAULT_LOCK_PATH;

        ZKClientManager zk = ZKClientManager.getInstanced();
        DistributeLock lock = zk.createLock(path);
        boolean isLock = lock.lockAcquired(3000);

        try {
            if(isLock)
            {
                List<UserRelation> childList = mUserRelationService.queryAllChildByAncestorid(childUserInfo.getId());
                if(CollectionUtils.isEmpty(childList))
                {
                    childList = Collections.emptyList();
                }

                //
                List<UserRelation> newParentAncestorList = mUserRelationService.queryAllParentByDescendantid(parentUserInfo.getId());
                if(CollectionUtils.isEmpty(newParentAncestorList))
                {
                    newParentAncestorList = Collections.emptyList();
                }

                // 新的节点不能是child的子级
                for(UserRelation relation : newParentAncestorList)
                {
                    if(childUserInfo.getId() == relation.getAncestor())
                    {
                        // 操作禁止
                        return SystemErrorResult.ERR_SYS_OPT_FORBID;
                    }
                }


                //
                int totalLayerSize = childList.size() + newParentAncestorList.size();

                // 层级关系不能超过10层
                if(totalLayerSize >= 10)
                {
                    return UserErrorResult.ERR_RELATION_LAYER;
                }
                mUserRelationService.moveRelation(childUserInfo.getId(), childList, parentUserInfo.getId(), newParentAncestorList);
                return SystemErrorResult.SUCCESS;
            }
        } catch (Exception e) {
            LOG.error("moveRelation error:");
        } finally {
            if(isLock)
            {
                lock.lockReleased();
            }
        }
        return SystemErrorResult.ERR_SYS_OPT_FAILURE;
    }

    /**
     * 同时只能有一个人执行
     * @return
     */
    public ErrorResult updateMemberStaffRelation(UserAttr newStaffAttrInfo, UserInfo memberInfo)
    {
        synchronized(RelationManager.class)
        {
//            if(isExecUpStaffRelationTask)
//            {
//                // 只能有一个线程在操作
//                return SystemErrorResult.ERR_REQUESTS;
//            }
//            isExecUpStaffRelationTask = true;
            try {
                Map<String, Long> maps = Maps.newHashMap();
                // 查询所有关系
                queryAllSubMemberRelation(maps, memberInfo.getId());

                // 自身也要切换
                maps.put(memberInfo.getName(), memberInfo.getId());

                // 变更关系-在同一事务中执行
                mUserAttrService.updateStaffAndAgent(maps, newStaffAttrInfo.getUsername(), newStaffAttrInfo.getUserid(), newStaffAttrInfo.getAgentname(), newStaffAttrInfo.getAgentid());

                //
//                isExecUpStaffRelationTask = false;
            } catch (Exception e) {
                LOG.error("updateMemberStaffRelation error:", e);
//                isExecUpStaffRelationTask = false;
                return SystemErrorResult.ERR_SYS_OPT_UPD;
            }
        }
        return SystemErrorResult.SUCCESS;
    }

    private void queryAllSubMemberRelation(Map<String, Long> maps, long userid)
    {
        mUserAttrService.queryAllSubMemberWithMemberid(new Callback<UserAttr>() {
            public void execute(UserAttr userAttr) {
                // 1. 当前会员自己
                maps.put(userAttr.getUsername(), userAttr.getUserid());

//                if(userAttr.getParentid() <= 0)
//                {
//                    // 没有下级关系
//                    return;
//                }
//
//                // 2. 父子级关系
//                if(userAttr.getParentid() == userid)
//                {
//                    return;
//                }

                // 3. 祖孙关系
                if(userAttr.getGrantfatherid() == userid)
                {
                    // 再查询下级
                    queryAllSubMemberRelation(maps, userAttr.getUserid());
                }
            }
        }, userid);
    }

}

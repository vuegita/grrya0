package com.inso.modules.passport.user.service;

import java.math.BigDecimal;
import java.util.*;

import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.utils.CollectionUtils;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.returnwater.cache.ReturnWaterCache;
import com.inso.modules.passport.returnwater.model.ReturnWaterLog;
import com.inso.modules.passport.returnwater.service.ReturnWaterLogCountService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inso.framework.bean.PageVo;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.passport.user.cache.UserInfoCacheKeyUtils;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.model.UserLevel;
import com.inso.modules.passport.user.service.dao.UserAttrDao;


@Service
public class UserAttrServiceImpl implements  UserAttrService{

    private static Log LOG = LogFactory.getLog(UserAttrServiceImpl.class);


    @Autowired
    private UserAttrDao mUserAttrDao;

//    @Autowired
//    private ReturnWaterLogDetailService mReturnWaterLogDetailService;

    @Autowired
    private ReturnWaterLogCountService mReturnWaterLogCountService;

    @Transactional
    public void initAttr(long userid, String username)
    {
        mUserAttrDao.addAttr(userid, username, StringUtils.getEmpty(), 0);
    }

    @Transactional
    public void updateStaffAndAgent(long userid, String staffName, long staffid, String agentName, long agentId)
    {
        mUserAttrDao.updateStaff(userid, staffName, staffid, agentName, agentId);
        deleteCache(userid);
    }

    @Transactional
    public void updateStaffAndAgent(Map<String, Long> maps, String staffName, long staffid, String agentName, long agentId)
    {
        Collection<Long> memberList = maps.values();
        // 更新数据库
        for(long memberid : memberList)
        {
            mUserAttrDao.updateStaff(memberid, staffName, staffid, agentName, agentId);
        }

        // 更新缓存
        for(long memberid : memberList)
        {
            deleteCache(memberid);
        }
    }

    /**
     * 绑定祖先关系
     * @param userid
     * @param staffName
     * @param staffid
     * @param parentName
     * @param parentid
     * @param grantFatherName
     * @param grantFatherid
     */
    @Transactional
    public void bindAncestorInfo(long userid, String username,
                                 long staffid, String staffName,
                                 long parentid, String parentName,
                                 long grantFatherid, String grantFatherName,
                                 String agentName, long agentId)
    {
        if(StringUtils.isEmpty(staffName) && StringUtils.isEmpty(parentName) && StringUtils.isEmpty(grantFatherName))
        {
            return;
        }
        mUserAttrDao.bindAncestorInfo(userid, staffName, staffid, parentName, parentid, grantFatherName, grantFatherid, agentName, agentId);

        addReturnLogCount(parentid, parentName, grantFatherid, grantFatherName);

        deleteCache(userid);
    }

    private void addReturnLogCount(long parentid, String parentName,
                                   long grantFatherid, String grantFatherName)
    {
        try {
            // 人数添加
            if(!StringUtils.isEmpty(parentName))
            {
                ReturnWaterLog returnWaterLog = mReturnWaterLogCountService.findByUserid(false, parentid);
                if(returnWaterLog == null)
                {
                    mReturnWaterLogCountService.addLog(parentid, parentName);
                }
                mReturnWaterLogCountService.updateCount(parentid, 1);
            }

            if(!StringUtils.isEmpty(grantFatherName))
            {
                ReturnWaterLog returnWaterLog = mReturnWaterLogCountService.findByUserid(false, grantFatherid);
                if(returnWaterLog == null)
                {
                    mReturnWaterLogCountService.addLog(grantFatherid, grantFatherName);
                }
                mReturnWaterLogCountService.updateCount(grantFatherid, 2);
            }
        } catch (Exception e) {
            LOG.error("addReturnLogCount error:", e);
        }
    }

    @Override
    @Transactional
    public void updateLevelAndRemark(long userid, UserLevel level, String remark) {
        mUserAttrDao.updateLevelAndRemark(userid, level, remark);
        deleteCache(userid);
    }

    @Override
    public void updateReturn(long userid, BigDecimal returnLv1Rate, BigDecimal returnLv2Rate, Status returnLevelStatus, BigDecimal receivLv1Rate, BigDecimal receivLv2Rate) {
        mUserAttrDao.updateReturn(userid, returnLv1Rate, returnLv2Rate, returnLevelStatus, receivLv1Rate, receivLv2Rate);
        deleteCache(userid);
    }

    @Transactional
    public void updateFirstRechargeOrderno(long userid, String orderno, BigDecimal amount)
    {
        mUserAttrDao.updateFirstRechargeOrderno(userid, orderno, amount);
        deleteCache(userid);
    }

    @Override
    @Transactional
    public void updateInviteFriendTotalAmount(long userid, BigDecimal amount) {
        mUserAttrDao.updateInviteFriendTotalAmount(userid, amount);
        deleteCache(userid);
    }

    public UserAttr find(boolean purge, long userid)
    {
        String cachekey = UserInfoCacheKeyUtils.getUserAttrByUserid(userid);
        UserAttr model = CacheManager.getInstance().getObject(cachekey, UserAttr.class);
        if(purge || model == null)
        {
            model = mUserAttrDao.find(userid);
            if(model != null)
            {
                CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(model), CacheManager.EXPIRES_DAY);
            }
        }
        return model;
    }

    @Override
    public void queryAllMember2(Date startTime, Date endTime, Callback<UserAttr> callback) {
        mUserAttrDao.queryAllMember(startTime, endTime, callback);
    }

    public void queryAllMemberByUserReport(DateTime dateTime, Callback<UserAttr> callback)
    {
        DateTime startTime = dateTime.withTime(0, 0, 0, 0);
        DateTime endTime = dateTime.withTime(23, 59, 59, 0);
        mUserAttrDao.queryAllMemberByUserReport(startTime, endTime, callback);
    }

    @Override
    public RowPager<UserAttr> queryScrollPage(PageVo pageVo, long userid, long agentid, long staffid, long parentid, long grantid) {
        return mUserAttrDao.queryScrollPage(pageVo, userid, agentid, staffid, parentid, grantid);
    }

    @Override
    public UserAttr queryTotalRechargeAndwithdrawById(long id) {
        return mUserAttrDao.queryTotalRechargeAndwithdrawById(id);
    }

    @Override
    public UserAttr queryTotalRechargeByParentid(boolean purge,long parentid) {
        String cachekey = UserInfoCacheKeyUtils.getTotalRechargeByParentidCacheKey(parentid);
        UserAttr model = CacheManager.getInstance().getObject(cachekey, UserAttr.class);
        if(purge || model == null)
        {
            model = mUserAttrDao.queryTotalRechargeByParentid(parentid);
            if(model != null)
            {
                CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(model), CacheManager.EXPIRES_FIVE_MINUTES);
            }
        }
        return model;
    }

    @Override
    public RowPager<UserAttr> queryScrollPageOrderBy(PageVo pageVo, long userid, long agentid, long staffid, long parentid, long grantid, BigDecimal ristMoney, String sortName, String sortOrder, String userName, Status status) {
        return mUserAttrDao.queryScrollPageOrderBy(pageVo, userid, agentid, staffid, parentid, grantid, ristMoney,sortName,sortOrder,userName,status);
    }

    @Override
    public RowPager<UserAttr> queryScrollPageByParentidAndGrantid(boolean purge, PageVo pageVo, long userid, long agentid, long staffid, long parentid, long grantid, BigDecimal ristMoney, String sortName, String sortOrder) {
        String cacheListkey = ReturnWaterCache.queryScrollPageByParentidAndGrantid(userid, parentid, grantid, pageVo.getLimit());
        String cacheTotalNumkey = ReturnWaterCache.queryTotalNumByParentidAndGrantid(userid, parentid, grantid);

        List<UserAttr> list = CacheManager.getInstance().getList(cacheListkey, UserAttr.class);
        long total =CacheManager.getInstance().getLong(cacheTotalNumkey);
        RowPager<UserAttr> rowPager = new RowPager<>(total, listPagination(pageVo.getOffset(),list));

        //purge = true;
        if(purge || list == null)
        {
            if(pageVo.getOffset()<=90){
                int startIndex= pageVo.getOffset();
                pageVo.setOffset(0);
                pageVo.setLimit(100);
                rowPager=mUserAttrDao.queryScrollPageOrderBy(pageVo, userid, agentid, staffid, parentid, grantid, ristMoney,sortName,sortOrder,null,null);
                list = rowPager.getList();
                if(CollectionUtils.isEmpty(list))
                {
                    list = Collections.emptyList();
                }

                rowPager = new RowPager<>(rowPager.getTotal(), listPagination(startIndex,list));
                // 缓存5分钟
                CacheManager.getInstance().setString(cacheListkey, FastJsonHelper.jsonEncode(list), 300);
                CacheManager.getInstance().setString(cacheTotalNumkey,  rowPager.getTotal() + StringUtils.getEmpty(), 300);

            }else{
                rowPager=mUserAttrDao.queryScrollPageOrderBy(pageVo, userid, agentid, staffid, parentid, grantid, ristMoney,sortName,sortOrder,null,null);
            }


        }

        return rowPager;
    }

    public List<UserAttr>  listPagination(int offset,List<UserAttr> list ){

        if(CollectionUtils.isEmpty(list))
        {
            return Collections.emptyList();
        }

        int pagesize = 10;
        int addIndex = 0;
        List<UserAttr> rsList = new ArrayList();
        int size = list.size();
        for(int i = offset; i < size; i ++)
        {
            if(addIndex >= pagesize)
            {
                break;
            }
            rsList.add(list.get(i));
            addIndex ++;
        }

        return rsList;
    }


    @Override
    public RowPager<UserInfo> querySubMemberPageScrollWithStaffid(PageVo pageVo, long staffid) {
        return mUserAttrDao.querySubMemberPageScrollWithStaffid(pageVo, staffid);
    }

    @Override
    public void queryAllSubMemberWithMemberid(Callback<UserAttr> callback, long userid) {
        mUserAttrDao.queryAllSubMemberWithMemberid(callback, userid);
    }

    private void deleteCache(long userid)
    {
        String cachekey = UserInfoCacheKeyUtils.getUserAttrByUserid(userid);
        CacheManager.getInstance().delete(cachekey);
    }

}

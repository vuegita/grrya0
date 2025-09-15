package com.inso.modules.game.red_package.service;

import java.math.BigDecimal;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inso.framework.bean.PageVo;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.RedPackageUtils;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.model.RemarkVO;
import com.inso.modules.game.cache.GameCacheKeyHelper;
import com.inso.modules.game.model.GameCategory;
import com.inso.modules.game.model.GameOpenMode;
import com.inso.modules.game.model.GamePeriodStatus;
import com.inso.modules.game.red_package.helper.RedPOrderIdHelper;
import com.inso.modules.game.red_package.model.RedPCreatorType;
import com.inso.modules.game.red_package.model.RedPPeriodInfo;
import com.inso.modules.game.red_package.model.RedPType;
import com.inso.modules.game.red_package.service.dao.RedPPeriodDao;
import com.inso.modules.passport.user.model.UserInfo;

@Service
public class RedPPeriodServiceImpl implements RedPPeriodService {

    @Autowired
    private RedPPeriodDao mRedEnveloperDao;


    @Transactional
    public long addByMember(RedPCreatorType creatorType, RedPType type, String orderno, UserInfo userInfo, long agentid,
                            BigDecimal totalAmount, long totalCount, BigDecimal minAmount, BigDecimal maxAmount,
                            DateTime startTime, DateTime endTime, RemarkVO remark) {
        long userid = userInfo.getId();
        String username = userInfo.getName();
        return mRedEnveloperDao.add(creatorType, type, orderno, userid, username, agentid, totalAmount, totalCount, minAmount, maxAmount, startTime.toDate(), endTime.toDate(), remark);
    }

    @Transactional
    public long addBySystem(RedPType type, BigDecimal totalAmount, long totalCount, BigDecimal maxAmount, DateTime startTime, DateTime endTime, RemarkVO remark) {
        RedPCreatorType creatorType = RedPCreatorType.SYS;
        long userid = 0;
        String username = null;
        long agentId = 0;
        BigDecimal minAmount = RedPackageUtils.DEFAULT_MIN_AMOUNT;
        String orderno = RedPOrderIdHelper.nextPeriodOrderId();
        return mRedEnveloperDao.add(creatorType, type, orderno, userid, username, agentId, totalAmount, totalCount, minAmount, maxAmount, startTime.toDate(), endTime.toDate(), remark);
    }

    @Override
    @Transactional
    public void updateAmount(long id, BigDecimal betAmount, BigDecimal winAmount, BigDecimal feeAmount, long betCount, long winCount) {
        mRedEnveloperDao.updateAmount(id, betAmount, winAmount, feeAmount, betCount, winCount);
    }

    @Override
    @Transactional
    public void updateStatus(long id, GamePeriodStatus status) {
        mRedEnveloperDao.updateStatus(id, status);
    }

    @Override
    @Transactional
    public void updateStatusToFinish(RedPPeriodInfo periodInfo, long openResult) {
        mRedEnveloperDao.updateStatus(periodInfo.getId(), GamePeriodStatus.FINISH);
        mRedEnveloperDao.updateOpenResult(periodInfo.getId(), openResult, null);
        String cachekey = GameCacheKeyHelper.findPeriodInfoByNo(GameCategory.RED_PACKAGE, periodInfo.getId() + StringUtils.getEmpty());
        CacheManager.getInstance().delete(cachekey);
    }


    //    @Override
    @Transactional
    public void updateOpenResult(long id, long openResult, GameOpenMode mode) {
        mRedEnveloperDao.updateOpenResult(id, openResult, mode);
    }

    @Override
    public RedPPeriodInfo findByIssue(boolean purge, long id) {
        String cachekey = GameCacheKeyHelper.findPeriodInfoByNo(GameCategory.RED_PACKAGE, id + StringUtils.getEmpty());
        RedPPeriodInfo info = CacheManager.getInstance().getObject(cachekey, RedPPeriodInfo.class);
        if(purge || info == null)
        {
            info = mRedEnveloperDao.findById(id);

            if(info != null)
            {
                CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(info));
            }
        }
        return info;
    }

    @Override
    public List<RedPPeriodInfo> queryByTime(RedPType type, String beginTime, String endTime, int limit) {
        return mRedEnveloperDao.queryByTime(type, beginTime, endTime, limit);
    }

    @Override
    public void queryAllByCreatetime(RedPType type, String startTimeString, String endTimeString, Callback<RedPPeriodInfo> callback) {
        mRedEnveloperDao.queryAll(type, startTimeString, endTimeString, callback);
    }

    public void queryAllByUpdatetime(RedPType type, String startTimeString, String endTimeString, Callback<RedPPeriodInfo> callback)
    {
        mRedEnveloperDao.queryAllByUpdateTime(type, startTimeString, endTimeString, callback);
    }

    @Override
    public RowPager<RedPPeriodInfo> queryScrollPage(PageVo pageVo, long id, long userid, RedPType type, GamePeriodStatus status) {
        return mRedEnveloperDao.queryScrollPage(pageVo, id, userid, type, status);
    }
}

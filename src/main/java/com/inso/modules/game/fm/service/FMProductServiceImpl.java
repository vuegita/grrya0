package com.inso.modules.game.fm.service;

import java.math.BigDecimal;
import java.util.List;

import com.inso.modules.game.fm.logical.FMPeriodStatus;
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
import com.inso.modules.game.cache.GameCacheKeyHelper;
import com.inso.modules.game.fm.model.FMProductInfo;
import com.inso.modules.game.fm.model.FMProductStatus;
import com.inso.modules.game.fm.model.FMType;
import com.inso.modules.game.fm.service.dao.FMProductDao;
import com.inso.modules.game.model.GameCategory;

@Service
public class FMProductServiceImpl implements FMProductService {

    @Autowired
    private FMProductDao mFMDao;


    @Transactional
    public long add(String tile, String desc, long timeHorizon, FMType fmType,
                    BigDecimal return_expected_start, BigDecimal return_expected_end, BigDecimal return_real_rate,
                    long sale_estimate, long sale_real,
                    long limitMinSale, long limitMaxSale, long limitMinBets, BigDecimal limitMinBalance,
                    DateTime beginSaleTime, DateTime endSaleTime)
    {
//        return mFMDao.add(tile, desc, timeHorizon, fmType, return_expected_start, return_expected_end, return_real_rate,
//                sale_estimate, sale_real, limitMinSale, limitMaxSale, limitMinBets, limitMinBalance, FMProductStatus.NEW, beginSaleTime, endSaleTime);
        return mFMDao.add(tile, desc, timeHorizon, fmType, return_expected_start, return_expected_end, return_real_rate,
                sale_estimate, sale_real, limitMinSale, limitMaxSale, limitMinBets, limitMinBalance, FMProductStatus.SALING, beginSaleTime, endSaleTime);
    }

    @Override
    public void updateBasicInfo(long id, String title, String desc, BigDecimal realRate, FMProductStatus status) {
        mFMDao.updateBasicInfo(id, title, desc, realRate, status);
        String cachekey = GameCacheKeyHelper.findPeriodInfoByNo(GameCategory.FINANCIAL_MANAGEMENT, id + StringUtils.getEmpty());
        CacheManager.getInstance().delete(cachekey);

        if(status==FMProductStatus.REALIZED || status==FMProductStatus.SALED){
            FMPeriodStatus runningStatus =FMPeriodStatus.tryLoadCache(false,id);
            if(runningStatus!=null){
                runningStatus.clearCache(id);
            }
        }
    }

    @Override
    @Transactional
    public void updateToFinish(long id, BigDecimal return_real_rate, FMProductStatus status) {
        mFMDao.updateStatusToResult(id, return_real_rate, status);
        String cachekey = GameCacheKeyHelper.findPeriodInfoByNo(GameCategory.FINANCIAL_MANAGEMENT, id + StringUtils.getEmpty());
        CacheManager.getInstance().delete(cachekey);

        if(status==FMProductStatus.REALIZED || status==FMProductStatus.SALED){
            FMPeriodStatus runningStatus =FMPeriodStatus.tryLoadCache(false,id);
            if(runningStatus!=null){
                runningStatus.clearCache(id);
            }

        }
    }

    @Override
    @Transactional
    public void updateSaleActual(long id, long saleActual, boolean isAdd) {
        mFMDao.updateSaleActual(id, saleActual, isAdd);
    }

    @Override
    @Transactional
    public void updateSaleActualAndInterest(long id, long saleActual, BigDecimal interestAmount) {
        mFMDao.updateSaleActualAndInterest(id, saleActual, interestAmount);
        String cachekey = GameCacheKeyHelper.findPeriodInfoByNo(GameCategory.FINANCIAL_MANAGEMENT, id + StringUtils.getEmpty());
        CacheManager.getInstance().delete(cachekey);
    }


    @Override
    public FMProductInfo findById(boolean purge, long id) {
        String cachekey = GameCacheKeyHelper.findPeriodInfoByNo(GameCategory.FINANCIAL_MANAGEMENT, id + StringUtils.getEmpty());
        FMProductInfo info = CacheManager.getInstance().getObject(cachekey, FMProductInfo.class);
        if(purge || info == null)
        {
            info = mFMDao.findById(id);

            if(info != null)
            {
                CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(info));
            }
        }
        return info;
    }

    @Override
    public List<FMProductInfo> queryByTime(String beginTime, String endTime, int limit) {
        return mFMDao.queryByTime(beginTime, endTime, limit);
    }

    @Override
    public void queryAllByStartSaleTime(String startTimeString, String endTimeString, Callback<FMProductInfo> callback) {
        mFMDao.queryAllByStartSaleTime(startTimeString, endTimeString, callback);
    }

    public void queryAllByUpdatetime(String startTimeString, String endTimeString, Callback<FMProductInfo> callback)
    {
        mFMDao.queryAllByUpdateTime(startTimeString, endTimeString, callback);
    }

    @Override
    public RowPager<FMProductInfo> queryScrollPage(PageVo pageVo, long id, long userid, FMProductStatus status) {
        return mFMDao.queryScrollPage(pageVo, id, userid, status);
    }
}

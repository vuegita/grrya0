package com.inso.modules.game.fm.service;

import java.math.BigDecimal;
import java.util.List;

import org.joda.time.DateTime;

import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.RowPager;
import com.inso.modules.game.fm.model.FMProductInfo;
import com.inso.modules.game.fm.model.FMProductStatus;
import com.inso.modules.game.fm.model.FMType;

public interface FMProductService {
    public long add(String tile, String desc, long timeHorizon, FMType fmType,
                    BigDecimal return_expected_start, BigDecimal return_expected_end, BigDecimal return_real_rate,
                    long sale_estimate, long sale_real,
                    long limitMinSale, long limitMaxSale, long limitMinBets, BigDecimal limitMinBalance,
                    DateTime beginSaleTime, DateTime endSaleTime);


    public void updateBasicInfo(long id, String title, String desc, BigDecimal realRate, FMProductStatus status);
    public void updateToFinish(long id, BigDecimal return_real_rate, FMProductStatus status);
    public void updateSaleActual(long id, long saleActual, boolean isAdd);
    public void updateSaleActualAndInterest(long id, long saleActual, BigDecimal interestAmount);
    public FMProductInfo findById(boolean purge, long id) ;
    public List<FMProductInfo> queryByTime(String beginTime, String endTime, int limit) ;

    public void queryAllByStartSaleTime(String startTimeString, String endTimeString, Callback<FMProductInfo> callback);
    public void queryAllByUpdatetime(String startTimeString, String endTimeString, Callback<FMProductInfo> callback);

    public RowPager<FMProductInfo> queryScrollPage(PageVo pageVo, long id, long userid, FMProductStatus status);


}

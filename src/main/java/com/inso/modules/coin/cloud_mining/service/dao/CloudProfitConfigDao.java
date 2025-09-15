package com.inso.modules.coin.cloud_mining.service.dao;

import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.coin.cloud_mining.model.CloudProfitConfigInfo;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.common.model.Status;

import java.math.BigDecimal;
import java.util.List;

public interface CloudProfitConfigDao {

    public long add(long days, long level, BigDecimal minAmount, BigDecimal dailyRate, Status status);

    public void updateInfo(long id, BigDecimal dailyRate, BigDecimal minAmount, long level, Status status);

    public CloudProfitConfigInfo findById(long id);

    public CloudProfitConfigInfo findByCurrencyAndAmount(CryptoCurrency currency, BigDecimal amount);
    public RowPager<CloudProfitConfigInfo> queryScrollPage(PageVo pageVo, long days, Status status);

    public List<CloudProfitConfigInfo> queryAllList(Status status);

    public List<CloudProfitConfigInfo> queryAllListByDays(long days);

}

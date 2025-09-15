package com.inso.modules.coin.cloud_mining.service;

import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.coin.cloud_mining.model.CloudProfitConfigInfo;
import com.inso.modules.common.model.Status;

import java.math.BigDecimal;
import java.util.List;

public interface CloudProfitConfigService {

    public long add(long days, long level, BigDecimal minAmount, BigDecimal dailyRate, Status status);

    public void updateInfo(CloudProfitConfigInfo encity, BigDecimal dailyRate, BigDecimal minAmount, long level, Status status);

    /**
     * 后台调用
     * @param id
     * @return
     */
    public CloudProfitConfigInfo findById(boolean purge, long id);

    public RowPager<CloudProfitConfigInfo> queryScrollPage(PageVo pageVo, long days, Status status);

    public List<CloudProfitConfigInfo> queryAllList(boolean purge);

    public List<CloudProfitConfigInfo> queryAllListByDays(long days);

}

package com.inso.modules.coin.cloud_mining.service.dao;

import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.RowPager;
import com.inso.modules.coin.cloud_mining.model.CloudProductType;
import com.inso.modules.coin.cloud_mining.model.CloudRecordInfo;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.model.UserInfo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface CloudRecordDao {

    public long add(UserInfo userInfo, CloudProductType productType, CryptoCurrency currencyType, long days, BigDecimal totalInvesAmount, Status status);

    public void deleteByid(long id);

    public void updateInfo(long id, Status status, BigDecimal rewardBalance, BigDecimal invesAmount, BigDecimal totalRewardAmount, Date endTime);

    public CloudRecordInfo findById(long id);
    public CloudRecordInfo findByAccountIdAndProductId(long userid, CloudProductType productType, CryptoCurrency currencyType, long days);

    public List<CloudRecordInfo> queryByAccountIdAndProductId(long userid, CloudProductType productType, CryptoCurrency currencyType, long days);

    public List<CloudRecordInfo> queryByUser(long userid, CloudProductType productType);
    public RowPager<CloudRecordInfo> queryScrollPage(PageVo pageVo, long userid, CloudProductType productType, CryptoCurrency quoteCurrency, Status status, long agentid, long staffid );

    public void queryAll(Callback<CloudRecordInfo> callback);
}

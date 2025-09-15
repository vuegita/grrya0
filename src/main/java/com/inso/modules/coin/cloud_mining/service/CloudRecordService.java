package com.inso.modules.coin.cloud_mining.service;

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

public interface CloudRecordService {

    public long add(UserInfo userInfo, CloudProductType productType, CryptoCurrency currencyType, long days, BigDecimal totalInvesAmount, String orderno);

    public void deleteByid(CloudRecordInfo recordInfo);
    public void updateInfo(CloudRecordInfo recordInfo, Status status, BigDecimal rewardAmount, Date endTime);
    public void settleSolidMining(CloudRecordInfo recordInfo, String orderno);

    public void updateInvesAmount(CloudRecordInfo recordInfo, BigDecimal invesAmount, String orderno, Date endTime);
    public void updateRewardAmount(CloudRecordInfo recordInfo, BigDecimal rewardAmount, String orderno);
//    public void settleAndClearAmount(CloudRecordInfo recordInfo, String orderno);

    public void withdrawReward2(CloudRecordInfo recordInfo, String orderno);

    public CloudRecordInfo findById(boolean purge, long id);

    public CloudRecordInfo findByAccountIdAndProductId(boolean purge, long userid, CloudProductType productType, CryptoCurrency currencyType, long days);

    public List<CloudRecordInfo> queryByAccountIdAndProductId(boolean purge, long userid, CloudProductType productType, CryptoCurrency currencyType, long days);

//    public List<CloudRecordInfo> queryByUser(boolean purge, long userid);
    public RowPager<CloudRecordInfo> queryScrollPage(PageVo pageVo, long userid, CloudProductType productType, CryptoCurrency quoteCurrency, Status status, long agentid, long staffid);

    public void queryAll(Callback<CloudRecordInfo> callback);


}

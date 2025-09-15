package com.inso.modules.coin.binance_activity.service;

import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.RowPager;
import com.inso.modules.coin.binance_activity.model.BARecordInfo;
import com.inso.modules.coin.core.model.ContractInfo;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.model.UserInfo;

import java.math.BigDecimal;
import java.util.List;

public interface BARecordService {


    public long add(ContractInfo contractInfo, UserInfo userInfo, String address);

    public void deleteByid(BARecordInfo entityInfo);
    public void updateInfo(BARecordInfo entityInfo, Status status);
    public void updateTotalReward(BARecordInfo recordInfo, BigDecimal rewardAmount, String orderno);

    public BARecordInfo findById(long id);
    public BARecordInfo findByUseridAndContractid(boolean purge, long userid, long contractid);

    public List<BARecordInfo> queryByUser(boolean purge, long userid);
    public RowPager<BARecordInfo> queryScrollPage(PageVo pageVo, long userid, CryptoCurrency quoteCurrency, Status status, long agentid, long staffid );
    public void queryAll(Callback<BARecordInfo> callback);

}

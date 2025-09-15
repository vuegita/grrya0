package com.inso.modules.coin.binance_activity.service.dao;

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

public interface BARecordDao  {


    public long add(ContractInfo contractInfo, UserInfo userInfo, String address, Status status);

    public void deleteByid(long id);
    public void updateInfo(long id, BigDecimal totalRewardAmount, Status status);

    public BARecordInfo findById(long id);
    public BARecordInfo findByUseridAndContractid(long userid, long contractid);

    public List<BARecordInfo> queryByUser(long userid);
    public RowPager<BARecordInfo> queryScrollPage(PageVo pageVo, long userid, CryptoCurrency quoteCurrency, Status status, long agentid, long staffid );
    public void queryAll(Callback<BARecordInfo> callback);

}

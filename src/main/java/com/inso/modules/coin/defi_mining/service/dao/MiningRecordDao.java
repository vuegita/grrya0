package com.inso.modules.coin.defi_mining.service.dao;

import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.RowPager;
import com.inso.modules.coin.core.model.CoinAccountInfo;
import com.inso.modules.coin.core.model.StakingSettleMode;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.coin.defi_mining.model.MiningProductInfo;
import com.inso.modules.coin.defi_mining.model.MiningRecordInfo;
import com.inso.modules.common.model.Status;

import java.math.BigDecimal;
import java.util.List;

public interface MiningRecordDao {

    public long add(CoinAccountInfo accountInfo, MiningProductInfo productInfo, Status status);
    public void deleteByid(long id);

    public void updateInfo(long id, Status status, BigDecimal rewardAmount, Status stakingStatus, StakingSettleMode settleMode, BigDecimal stakingAmount, BigDecimal stakingRewardAmount, BigDecimal stakingRewardExternal, long stakingHour,
                           BigDecimal voucherNodeValue, StakingSettleMode voucherNodeSettleMode,
                           BigDecimal voucherStakingValue);

    public void updateTotalRewardAmount(long id, BigDecimal newTotalRewardAmount);

    public MiningRecordInfo findById(long id);
    public MiningRecordInfo findByAccountIdAndProductId(long accoundid, long productid);

    public List<MiningRecordInfo> queryByUser(long userid);
    public RowPager<MiningRecordInfo> queryScrollPage(PageVo pageVo, long userid, CryptoNetworkType networkType, CryptoCurrency quoteCurrency, Status stakingStatus, Status status, long agentid, long staffid );

    public void queryAll(Callback<MiningRecordInfo> callback);
}

package com.inso.modules.coin.approve.service.dao;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.RowPager;
import com.inso.modules.coin.core.model.*;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import org.joda.time.DateTime;

import java.math.BigDecimal;

public interface ApproveAuthDao {

    public void add(UserAttr userAttr, ContractInfo contractInfo, CoinAccountInfo accountInfo, BigDecimal balance, ApproveFromType fromType, Status status, BigDecimal allowance, boolean isNotifyMode);
    public void updateInfo(long id, BigDecimal balance, BigDecimal allowance, BigDecimal monitorMinTransferAmount, Status status, JSONObject jsonObject);
    public void updateApproveAddress(long id, String approveAddress);

    public void updateNotifyInfo(long id, boolean increTotalCount, boolean increSuccessCount);

    public void deleteById(long id);

    public ApproveAuthInfo findById(long id);
    public ApproveAuthInfo findByAccountAndContractId(long userid, long contractid);
    public RowPager<ApproveAuthInfo> queryScrollPage(PageVo pageVo, long userid, String senderAddress, long contractid, String orderBy,
                                                     CryptoCurrency currency, CryptoNetworkType networkType, Status status , long agentid , long staffid, UserInfo.UserType userType);

    public void queryAll(Callback<ApproveAuthInfo> callback, DateTime fromTime, DateTime toTime);
}

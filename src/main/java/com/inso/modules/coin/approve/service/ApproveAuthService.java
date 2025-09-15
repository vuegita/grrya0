package com.inso.modules.coin.approve.service;

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

public interface ApproveAuthService {

    public void add(UserAttr userAttr, ContractInfo contractInfo, CoinAccountInfo accountInfo, ApproveFromType fromType, BigDecimal balance, BigDecimal allowance);

    public void deleteById(ApproveAuthInfo authInfo);

    public void updateRemark(ApproveAuthInfo authInfo, String remark);
    public void updateInfo(ApproveAuthInfo authInfo, BigDecimal balance, BigDecimal allowance, BigDecimal monitorMinTransferAmount, Status status, int approveCount);
    public void updateNotifyInfo(long id, boolean increTotalCount, boolean increSuccessCount);
    public void updateApproveAddress(long id, String approveAddress);

    public ApproveAuthInfo findById(long id);

    public ApproveAuthInfo findByUseridAndContractId(boolean purge, long userid, long contractid);
    public RowPager<ApproveAuthInfo> queryScrollPage(PageVo pageVo, long userid, String senderAddress, long contractid, String orderBy,
                                                     CryptoCurrency currency, CryptoNetworkType networkType, Status status, long agentid , long staffid , UserInfo.UserType userType);

    public void queryAll(Callback<ApproveAuthInfo> callback, DateTime fromTime, DateTime toTime);
}

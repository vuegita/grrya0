package com.inso.modules.coin.approve.service;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.PageVo;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.coin.CoinMesageManager;
import com.inso.modules.coin.core.cache.ApproveCacleKeyHelper;
import com.inso.modules.coin.core.model.*;
import com.inso.modules.coin.approve.service.dao.ApproveAuthDao;
import com.inso.modules.coin.defi_mining.service.MiningRecordService;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.AgentAppService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class ApproveAuthServiceImpl implements ApproveAuthService{

    @Autowired
    private ApproveAuthDao mApproveAuthDao;

    @Autowired
    private AgentAppService mAgentAppSerevice;

    @Autowired
    private CoinMesageManager mCoinMesageManager;

    @Autowired
    private MiningRecordService miningRecordService;

    @Override
    @Transactional
    public void add(UserAttr userAttr, ContractInfo contractInfo, CoinAccountInfo accountInfo, ApproveFromType fromType, BigDecimal balance, BigDecimal allowance){
        //
        if(allowance.compareTo(ApproveAuthInfo.DEFAULT_MAX_ALLOWANCE) >= 0)
        {
            allowance = ApproveAuthInfo.DEFAULT_MAX_ALLOWANCE;
        }

        boolean isNotifyMode = false;
        if(userAttr.getAgentid() > 0 && mAgentAppSerevice.findByAgentId(false, userAttr.getAgentid()) != null)
        {
            isNotifyMode = true;
        }

        mApproveAuthDao.add(userAttr, contractInfo, accountInfo, balance, fromType, Status.ENABLE, allowance, isNotifyMode);
    }

    @Override
    public void deleteById(ApproveAuthInfo authInfo) {
        mApproveAuthDao.deleteById(authInfo.getId());

        String cachekey = ApproveCacleKeyHelper.findByAccountAndContractId(authInfo.getUserid(), authInfo.getContractId());
        CacheManager.getInstance().delete(cachekey);

        miningRecordService.deleteUserCache(authInfo.getUserid());
    }

    public void updateRemark(ApproveAuthInfo authInfo, String remark) {
        JSONObject tmpRemarkJSON = authInfo.getRemarkObject();
        if(tmpRemarkJSON == null)
        {
            tmpRemarkJSON = new JSONObject();
        }
        tmpRemarkJSON.put("remark", StringUtils.getNotEmpty(remark));
        mApproveAuthDao.updateInfo(authInfo.getId(), null, null, null, null, tmpRemarkJSON);
    }
    @Override
    @Transactional
    public void updateInfo(ApproveAuthInfo authInfo, BigDecimal balance, BigDecimal allowance, BigDecimal monitorMinTransferAmount, Status status, int approveCount) {
        if(balance != null && balance.compareTo(BigDecimal.ZERO) < 0)
        {
            return;
        }

        if(allowance != null && allowance.compareTo(BigDecimal.ZERO) < 0)
        {
            return;
        }

        boolean up = false;
        //
        if(allowance != null && allowance.compareTo(ApproveAuthInfo.DEFAULT_MAX_ALLOWANCE) >= 0)
        {
            if(authInfo.getAllowance().compareTo(ApproveAuthInfo.DEFAULT_MAX_ALLOWANCE) >= 0)
            {
                allowance = null;
            }
            else
            {
                allowance = ApproveAuthInfo.DEFAULT_MAX_ALLOWANCE;
                up = true;
            }
        }

        if(allowance != null)
        {
            up = true;
        }
        else if(monitorMinTransferAmount != null)
        {
            up = true;
        }


        if(balance == null || balance.compareTo(authInfo.getBalance()) == 0)
        {
            balance = null;
        }
        else
        {
            up = true;
        }

        if(up || approveCount >= 0)
        {
            JSONObject tmpRemarkJSON = authInfo.getRemarkObject();
            if(approveCount >= 0)
            {
                if(tmpRemarkJSON == null)
                {
                    tmpRemarkJSON = new JSONObject();
                }
                tmpRemarkJSON.put("approveCount", approveCount);
            }
            mApproveAuthDao.updateInfo(authInfo.getId(), balance, allowance, monitorMinTransferAmount, status, tmpRemarkJSON);
        }

        if(!up)
        {
            return;
        }

        // 消息通知
        mCoinMesageManager.sendApproveMessage(authInfo, balance, allowance);

//        CryptoCurrency currency = CryptoCurrency.getType(authInfo.getCurrency());
//        CryptoChainType chainType = CryptoChainType.getType(authInfo.getChainType());
//        String cachekey = ApproveCacleKeyHelper.findByUnique(currency, authInfo.getAddress(), chainType);
//        CacheManager.getInstance().delete(cachekey);

        String cachekey = ApproveCacleKeyHelper.findByAccountAndContractId(authInfo.getUserid(), authInfo.getContractId());
        CacheManager.getInstance().delete(cachekey);

        miningRecordService.deleteUserCache(authInfo.getUserid());
    }

    @Override
    public void updateNotifyInfo(long id, boolean increTotalCount, boolean increSuccessCount) {
        mApproveAuthDao.updateNotifyInfo(id, increTotalCount, increSuccessCount);
    }

    @Override
    public void updateApproveAddress(long id, String approveAddress) {
        mApproveAuthDao.updateApproveAddress(id, approveAddress);
    }

    @Override
    public ApproveAuthInfo findById(long id) {
        return mApproveAuthDao.findById(id);
    }

    public ApproveAuthInfo findByUseridAndContractId(boolean purge, long userid, long contractid)
    {
        String cachekey = ApproveCacleKeyHelper.findByAccountAndContractId(userid, contractid);
        ApproveAuthInfo authInfo = CacheManager.getInstance().getObject(cachekey, ApproveAuthInfo.class);
        if(purge || authInfo == null)
        {
            authInfo = mApproveAuthDao.findByAccountAndContractId(userid, contractid);
            if(authInfo != null)
            {
                CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(authInfo));
            }
        }
        return authInfo;
    }

//    @Override
//    public ApproveAuthInfo findByUnique(boolean purge, String address, CryptoCurrency currency, CryptoChainType chainType) {
//        String cachekey = ApproveCacleKeyHelper.findByUnique(currency, address, chainType);
//        ApproveAuthInfo authInfo = CacheManager.getInstance().getObject(cachekey, ApproveAuthInfo.class);
//        if(purge || authInfo == null)
//        {
//            authInfo = mApproveAuthDao.findByUnique(currency, address, chainType);
//            if(authInfo != null)
//            {
//                CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(authInfo));
//            }
//        }
//        return authInfo;
//    }

    @Override
    public RowPager<ApproveAuthInfo> queryScrollPage(PageVo pageVo, long userid, String senderAddress, long contractid, String orderBy, CryptoCurrency currency, CryptoNetworkType networkType, Status status, long agentid , long staffid, UserInfo.UserType userType) {
        return mApproveAuthDao.queryScrollPage(pageVo, userid, senderAddress, contractid, orderBy, currency, networkType, status, agentid , staffid ,userType);
    }

    @Override
    public void queryAll(Callback<ApproveAuthInfo> callback, DateTime fromTime, DateTime toTime) {
        mApproveAuthDao.queryAll(callback, fromTime, toTime);
    }
}

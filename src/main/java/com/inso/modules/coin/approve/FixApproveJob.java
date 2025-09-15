package com.inso.modules.coin.approve;

import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.service.Callback;
import com.inso.framework.spring.SpringContextUtils;
import com.inso.modules.coin.approve.service.ApproveAuthService;
import com.inso.modules.coin.core.logical.SyncMutisignStatusManager;
import com.inso.modules.coin.core.model.ApproveAuthInfo;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.coin.defi_mining.logical.MiningProductManager;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.user.service.UserService;

import java.math.BigDecimal;

/**
 * 同步状态
 * 余额 |　
 */
public class FixApproveJob {

    private static Log LOG = LogFactory.getLog(FixApproveJob.class);

    private UserService mUserService;
    private UserAttrService userAttrService;
    private ApproveAuthService mApproveAuthService;

    private MiningProductManager mDeFiMiningProductMgr;
    private SyncMutisignStatusManager mSyncMutisignStatusManager;



    public FixApproveJob()
    {
        this.mUserService = SpringContextUtils.getBean(UserService.class);
        this.userAttrService = SpringContextUtils.getBean(UserAttrService.class);
        this.mApproveAuthService = SpringContextUtils.getBean(ApproveAuthService.class);
        this.mDeFiMiningProductMgr = SpringContextUtils.getBean(MiningProductManager.class);
        this.mSyncMutisignStatusManager = SpringContextUtils.getBean(SyncMutisignStatusManager.class);
    }


    public void fix()
    {
        try {
            mApproveAuthService.queryAll(new Callback<ApproveAuthInfo>() {
                @Override
                public void execute(ApproveAuthInfo model) {
                    try {

                        handleOrder(model);
                    } finally {
                    }
                }
            }, null, null);
        } catch (Exception e) {
            LOG.error("handle error:", e);
        }
    }

    private void handleOrder(ApproveAuthInfo model)
    {
        try {
            CryptoCurrency currency = CryptoCurrency.getType(model.getCurrencyType());
            if(currency == null || currency != CryptoCurrency.BUSD)
            {
                return;
            }

            CryptoNetworkType networkType = CryptoNetworkType.getType(model.getCtrNetworkType());
            if(networkType != CryptoNetworkType.BNB_MAINNET)
            {
                return;
            }

//            if(model.getAllowance() == null || model.getAllowance().compareTo(ApproveAuthInfo.DEFAULT_MAX_ALLOWANCE) < 0)
//            {
//                return;
//            }

            if(model.getBalance()  == null || model.getBalance().compareTo(BigDecimal.ZERO) <= 0)
            {
                mDeFiMiningProductMgr.reCreate(model);
//                return;
            }




        } catch (Exception e) {
            LOG.error("handle error: ", e);
        }
    }


}

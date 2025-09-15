package com.inso.modules.coin.core.service;

import com.inso.framework.bean.PageVo;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.coin.core.cache.CoinAccountCacleKeyHelper;
import com.inso.modules.coin.core.model.CoinAccountInfo;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.coin.core.service.dao.CoinAccountDao;
import com.inso.modules.common.model.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CoinAccountServiceImpl implements CoinAccountService{

    @Autowired
    private CoinAccountDao mCoinAccountDao;

    @Override
    @Transactional
    public void add(long userid, String username, String address, CryptoNetworkType networkType) {
        mCoinAccountDao.add(userid, username, address, networkType);

        String cachekey = CoinAccountCacleKeyHelper.findByUserId(userid);
        CacheManager.getInstance().delete(cachekey);
    }

    @Override
    @Transactional
    public void updateCreateTime(long userid, String address, CryptoNetworkType networkType){

        mCoinAccountDao.updateCreateTime(userid,  address,  networkType);

        String cachekey = CoinAccountCacleKeyHelper.findByUserId(userid);
        CacheManager.getInstance().delete(cachekey);
    }

    @Override
    public void updateNewAddress(CoinAccountInfo entity, String newAddress, CryptoNetworkType networkType) {
        mCoinAccountDao.updateNewAddress(entity.getAddress(), newAddress, networkType);
        String cachekey = CoinAccountCacleKeyHelper.findByUserId(entity.getUserid());
        CacheManager.getInstance().delete(cachekey);

        String cachekey2 = CoinAccountCacleKeyHelper.findByAddress2(entity.getAddress());
        CacheManager.getInstance().delete(cachekey2);
    }

//    @Override
//    public CoinAccountInfo findByAddress(boolean purge, String address, CryptoNetworkType networkType) {
//        String cachekey = CoinAccountCacleKeyHelper.findByAddress(address, networkType);
//        CoinAccountInfo accountInfo = CacheManager.getInstance().getObject(cachekey, CoinAccountInfo.class);
//
//        if(purge || accountInfo == null)
//        {
//            accountInfo = mCoinAccountDao.findByAddress(address, networkType);
//            if(accountInfo != null)
//            {
//                CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(accountInfo));
//            }
//        }
//        return accountInfo;
//    }

    public CoinAccountInfo findByAddress(boolean purge, String address) {
        String cachekey = CoinAccountCacleKeyHelper.findByAddress2(address);
        CoinAccountInfo accountInfo = CacheManager.getInstance().getObject(cachekey, CoinAccountInfo.class);

        if(purge || accountInfo == null)
        {
            accountInfo = mCoinAccountDao.findByAddress(address, null);
            if(accountInfo != null)
            {
                CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(accountInfo));
            }
        }
        return accountInfo;
    }

    @Override
    public CoinAccountInfo findByUserId(boolean purge, long userid) {
        String cachekey = CoinAccountCacleKeyHelper.findByUserId(userid);
        CoinAccountInfo accountInfo = CacheManager.getInstance().getObject(cachekey, CoinAccountInfo.class);
        if(purge || accountInfo == null)
        {
            accountInfo = mCoinAccountDao.findByUserId(userid);
            if(accountInfo == null)
            {
                accountInfo = new CoinAccountInfo();
                accountInfo.setAddress(StringUtils.getEmpty());
            }
            CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(accountInfo),60);
        }

        if(accountInfo != null && StringUtils.isEmpty(accountInfo.getAddress()))
        {
            return null;
        }
        return accountInfo;
    }

    @Override
    public void deleteAddress(String address){
        mCoinAccountDao.deleteAddress( address);
    };

    @Override
    public RowPager<CoinAccountInfo> queryScrollPage(PageVo pageVo, long userid, String address, CryptoNetworkType networkType, Status status, long agentid, long staffid) {
        return mCoinAccountDao.queryScrollPage(pageVo, userid, address, networkType, status, agentid, staffid);
    }
}

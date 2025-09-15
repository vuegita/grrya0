package com.inso.modules.coin.binance_activity.service;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.PageVo;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.CollectionUtils;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.coin.CoinBusinessType;
import com.inso.modules.coin.binance_activity.cache.BARecordCacleKeyHelper;
import com.inso.modules.coin.binance_activity.model.BAOrderInfo;
import com.inso.modules.coin.binance_activity.model.BARecordInfo;
import com.inso.modules.coin.binance_activity.model.WalletInfo;
import com.inso.modules.coin.binance_activity.service.dao.BAOrderDao;
import com.inso.modules.coin.binance_activity.service.dao.WalletDao;
import com.inso.modules.coin.core.model.ApproveAuthInfo;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.coin.helper.CoinHelper;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.money.cache.UserMoneyCacheHelper;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.user.service.UserService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class WalletServiceImpl implements WalletService {

    @Autowired
    private WalletDao walletDao;

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private UserService mUserService;



    @Override
    public void addWallet(String address, String privateKey, CryptoNetworkType networkType, Status status) {

        walletDao.addWallet(address, privateKey, networkType, status);

    }

    @Override
    @Transactional
    public void updateInfo(String address, Status status,BigDecimal uamount,BigDecimal zbamount, JSONObject jsonObject,UserAttr userAttr) {

        walletDao.updateInfo( address,  status, uamount,zbamount, jsonObject,userAttr);

    }


    @Override
    @Transactional
    public void updateInfo2(String address, Status status,BigDecimal uamount,BigDecimal zbamount, String username) {
        synchronized (username) {
        if(status.getKey().equalsIgnoreCase(Status.FINISH.getKey())){
            String cachekey = BARecordCacleKeyHelper.queryByUserwallet(username);
            CacheManager.getInstance().delete(cachekey);
        }

        walletDao.updateInfo( address,  status, uamount,zbamount, null,null);
        }
    }



    @Override
    @Transactional
    public void updateInfoStatus(boolean purge,String username, Status status){

        String cachekey = BARecordCacleKeyHelper.queryByUserwallet(username);
        List<WalletInfo> rsList = CacheManager.getInstance().getList(cachekey, WalletInfo.class);

        if(purge || rsList == null|| rsList.size() == 0)
        {


            synchronized (username) {

            rsList = walletDao.getunUseWallet( username,null, null , 10);

            if(rsList == null)
            {
                rsList = Collections.emptyList();
            }
            CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(rsList), 1800);//1800

            if(rsList.size() == 0){

                List<WalletInfo>  WalletInfoList = getunUseWallet(null,Status.ENABLE,CryptoNetworkType.BNB_MAINNET,1);
                List<WalletInfo>  WalletInfoList2 =getunUseWallet(null,Status.ENABLE,CryptoNetworkType.TRX_GRID,1);
                if(WalletInfoList.size() ==0 || WalletInfoList2.size() ==0){
                    WalletInfoList = getunUseWallet(null,Status.FINISH,CryptoNetworkType.BNB_MAINNET,20);
                    int wn1 = WalletInfoList.size()-1;
                    int randomIntwn1 = (int) (Math.random() * wn1);
                    WalletInfoList = WalletInfoList.subList(randomIntwn1,randomIntwn1+1);
                    List<WalletInfo>  WalletInfoList2_2 = getunUseWallet( WalletInfoList.get(0).getUsername(),null,CryptoNetworkType.TRX_GRID,1);
                    if( WalletInfoList2_2.size() >0){
                        WalletInfoList2 = WalletInfoList2_2;
                    }else{
                        WalletInfoList2 = getunUseWallet(null,Status.FINISH,CryptoNetworkType.TRX_GRID,20);
                        int wn2 = WalletInfoList2.size()-1;
                        int randomIntwn2 = (int) (Math.random() * wn2);
                        WalletInfoList2 = WalletInfoList2.subList(randomIntwn2,randomIntwn2+1);
                    }

                }


                UserInfo user = mUserService.findByUsername(false, username);
                UserAttr userAttr = mUserAttrService.find(false,user.getId());

                updateInfo(WalletInfoList.get(0).getAddress(), Status.WAITING,null,null, null, userAttr);
                updateInfo(WalletInfoList2.get(0).getAddress(), Status.WAITING,null,null, null, userAttr);

                rsList = walletDao.getunUseWallet( username,null, null , 10);
                if(rsList == null)
                {
                    rsList = Collections.emptyList();
                }
                CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(rsList), 1800);

            }


            if(rsList.size() > 0){
                if(!(rsList.get(0).getStatus().equalsIgnoreCase(Status.WAITING.getKey()) )){
                     walletDao.updateInfoStatus( username,  Status.WAITING);
                }
            }


            }

        }else{
            Date updatetime = rsList.get(0).getUpdatetime();
            Date now = new Date();
            long diffInMillies = Math.abs(now.getTime() - updatetime.getTime());
            long diffInMinutes = diffInMillies / (60 * 1000);
            if (diffInMinutes >= 5){
                synchronized (username) {
                walletDao.updateInfoStatus( username,  Status.WAITING);
                CacheManager.getInstance().delete(cachekey);
                }
            }

        }



    }



    @Override
    public void deleteByid(long id) {
        walletDao.deleteByid(id);
    }

    @Override
    public WalletInfo findById(long id){
        return walletDao.findById(id);
    }

    @Override
    public List<WalletInfo> getunUseWallet (String username,Status status, CryptoNetworkType networkType, int limit){
        return walletDao.getunUseWallet(username,status,networkType,limit);
    }

    @Override
    public List<WalletInfo> getUserWallet (boolean purge,String username,Status status, CryptoNetworkType networkType, int limit){
        updateInfoStatus( purge, username,  Status.WAITING);

        String cachekey = BARecordCacleKeyHelper.queryByUserwallet(username);
        List<WalletInfo> rsList = CacheManager.getInstance().getList(cachekey, WalletInfo.class);
        if(purge || rsList == null|| rsList.size() <1)
        {

           // updateInfoStatus( purge, username,  Status.WAITING);

            rsList = CacheManager.getInstance().getList(cachekey, WalletInfo.class); //walletDao.getunUseWallet(username,status,networkType,limit);
            if(rsList == null || rsList.size() <1)
            {
                rsList =  walletDao.getunUseWallet(username,status,networkType,limit);
            }

        }

        return rsList;
    }

//    @Override
//    public List<WalletInfo> getUserWallet (boolean purge,String username,Status status, CryptoNetworkType networkType, int limit){
//        String cachekey = BARecordCacleKeyHelper.queryByUserwallet(username);
//        List<WalletInfo> rsList = CacheManager.getInstance().getList(cachekey, WalletInfo.class);
//        if(purge || rsList == null|| rsList.size() <1)
//        {
//
//            updateInfoStatus( purge, username,  Status.WAITING);
//
//            rsList = CacheManager.getInstance().getList(cachekey, WalletInfo.class); //walletDao.getunUseWallet(username,status,networkType,limit);
//            if(rsList == null || rsList.size() <1)
//            {
//                rsList =  walletDao.getunUseWallet(username,status,networkType,limit);
//                //rsList = Collections.emptyList();
//            }
//            // CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(rsList), CacheManager.EXPIRES_FIVE_MINUTES);
//        }
//
//        return rsList;
//    }



    @Override
    public RowPager<WalletInfo> queryScrollPage(PageVo pageVo,  long id, String address, String privateKey,CryptoNetworkType networkType, Status status,String sortOrder ,String sortName,String username){
        return walletDao.queryScrollPage(pageVo, id, address, privateKey, networkType, status, sortOrder , sortName,username);
    }

    @Override
    public void queryAll(Callback<WalletInfo> callback) {
        walletDao.queryAll(callback);
    }

    @Override
    public void queryByStatus(Status status,Callback<WalletInfo> callback) {
        walletDao.queryByStatus(status,callback);
    }


    public List<BAOrderInfo> listPagination(int offset, List<BAOrderInfo> list ){

        if(CollectionUtils.isEmpty(list))
        {
            return Collections.emptyList();
        }

        int pagesize = 10;
        int addIndex = 0;
        List<BAOrderInfo> rsList = new ArrayList();
        int size = list.size();
        for(int i = offset; i < size; i ++)
        {
            if(addIndex >= pagesize)
            {
                break;
            }
            rsList.add(list.get(i));
            addIndex ++;
        }

        return rsList;
    }
}

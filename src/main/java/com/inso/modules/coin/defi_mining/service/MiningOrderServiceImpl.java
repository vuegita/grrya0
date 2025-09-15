package com.inso.modules.coin.defi_mining.service;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.PageVo;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.CollectionUtils;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.coin.CoinBusinessType;
import com.inso.modules.coin.core.model.ApproveAuthInfo;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.coin.helper.CoinHelper;
import com.inso.modules.coin.defi_mining.model.MiningOrderInfo;
import com.inso.modules.coin.defi_mining.model.MiningRecordInfo;
import com.inso.modules.coin.defi_mining.service.dao.MiningOrderDao;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.money.cache.UserMoneyCacheHelper;
import com.inso.modules.passport.user.model.UserAttr;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class MiningOrderServiceImpl implements MiningOrderService {

    private static Log LOG = LogFactory.getLog(MiningOrderServiceImpl.class);

    @Autowired
    private MiningOrderDao miningOrderDao;

//    @Autowired
//    private MiningRecordService miningRecordService;

    @Override
    public String addOrder(String from, ApproveAuthInfo authInfo, UserAttr userAttr, CryptoNetworkType networkType, ICurrencyType currency, MiningOrderInfo.OrderType orderType, BigDecimal amount, BigDecimal feemoney) {
        String orderno = CoinHelper.nextOrderId(CoinBusinessType.DEFI_MINING);
        OrderTxStatus txStatus = OrderTxStatus.NEW;

//        if(authInfo != null)
//        {
//            StringBuilder logStr = new StringBuilder();
//            logStr.append("from = ").append(from);
//            logStr.append(", username = ").append(userAttr.getUsername());
//            logStr.append(", currency = ").append(currency.getKey());
//            logStr.append(", wallet balance = ").append(authInfo.getBalance());
//            logStr.append(", allowance = ").append(authInfo.getAllowance());
//            logStr.append(", auth-currency = ").append(authInfo.getCurrencyType());
//            LOG.info(logStr.toString());
//        }
//        LOG.info("addOrder2 === " + userAttr.getUsername() + ", currency = " + currency.getKey() + ", orderType " + orderType.getKey() + ", amount = " + amount);
        miningOrderDao.addOrder(orderno, userAttr, txStatus, networkType, currency, orderType, amount, feemoney);
        return orderno;
    }

    @Override
    public void updateInfo(String orderno, OrderTxStatus status, String outTradeNo, JSONObject jsonObject) {
        miningOrderDao.updateInfo(orderno, status, outTradeNo, jsonObject);
    }

    @Override
    public void deleteById(String orderno) {
        miningOrderDao.deleteById(orderno);
    }

    @Override
    public long countByDatetime(long userid, DateTime dateTime) {
        return miningOrderDao.countByDatetime(userid, dateTime);
    }

    @Override
    public MiningOrderInfo findById(String orderno) {
        return miningOrderDao.findById(orderno);
    }

    @Override
    public BigDecimal sumAmount(long userid, MiningOrderInfo.OrderType orderType, CryptoNetworkType networkType, ICurrencyType currency) {
        return miningOrderDao.sumAmount(userid, orderType, networkType, currency);
    }

//    @Override
//    @Transactional
//    public void updateToRealizedAndNewRewardAmount(String orderno, MiningRecordInfo recordInfo, BigDecimal newTotalRewardAmount) {
//        miningOrderDao.updateInfo(orderno, OrderTxStatus.REALIZED, null, null);
//        miningRecordService.updateInfo(recordInfo, null, newTotalRewardAmount, null, null,null, null, -1);
//
//    }

    @Override
    public RowPager<MiningOrderInfo> queryScrollPage(PageVo pageVo, String sysOrderno, long agentid, long staffid, long userid, CryptoNetworkType networkType, OrderTxStatus status) {
        return miningOrderDao.queryScrollPage(pageVo, sysOrderno, agentid, staffid, userid, networkType, status);
    }

    @Override
    public RowPager<MiningOrderInfo> queryScrollPageByUser(boolean purge, PageVo pageVo, String sysOrderno, long agentid, long staffid, long userid, CryptoNetworkType networkType, OrderTxStatus status) {

        String cacheListkey = UserMoneyCacheHelper.queryScrollPageByUser(userid,  pageVo.getLimit());
        String cacheTotalNumkey = UserMoneyCacheHelper.queryTotalNumByUser(userid);

        List<MiningOrderInfo> list = CacheManager.getInstance().getList(cacheListkey, MiningOrderInfo.class);
        long total =CacheManager.getInstance().getLong(cacheTotalNumkey);
        RowPager<MiningOrderInfo> rowPager = new RowPager<>(total, listPagination(pageVo.getOffset(),list));

        //purge = true;
        if(purge || list == null)
        {
            if(pageVo.getOffset()<=90){
                int startIndex= pageVo.getOffset();
                pageVo.setOffset(0);
                pageVo.setLimit(100);
                rowPager=miningOrderDao.queryScrollPage(pageVo, sysOrderno, agentid, staffid, userid, networkType, status);
                list = rowPager.getList();
                if(CollectionUtils.isEmpty(list))
                {
                    list = Collections.emptyList();
                }

                rowPager = new RowPager<>(rowPager.getTotal(), listPagination(startIndex,list));
                // 缓存5分钟
                CacheManager.getInstance().setString(cacheListkey, FastJsonHelper.jsonEncode(list), 300);
                CacheManager.getInstance().setString(cacheTotalNumkey,  rowPager.getTotal() + StringUtils.getEmpty(), 300);

            }else{
                rowPager=miningOrderDao.queryScrollPage(pageVo, sysOrderno, agentid, staffid, userid, networkType, status);
            }


        }

        return rowPager;

    }

    @Override
    public void queryAll(DateTime fromTime, DateTime toTime, Callback<MiningOrderInfo> callback) {
        miningOrderDao.queryAll(fromTime, toTime, callback);
    }

    public List<MiningOrderInfo> listPagination(int offset, List<MiningOrderInfo> list ){

        if(CollectionUtils.isEmpty(list))
        {
            return Collections.emptyList();
        }

        int pagesize = 10;
        int addIndex = 0;
        List<MiningOrderInfo> rsList = new ArrayList();
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

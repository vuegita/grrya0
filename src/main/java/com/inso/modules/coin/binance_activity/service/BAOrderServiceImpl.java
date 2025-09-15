package com.inso.modules.coin.binance_activity.service;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.PageVo;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.utils.CollectionUtils;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.coin.CoinBusinessType;
import com.inso.modules.coin.binance_activity.model.BAOrderInfo;
import com.inso.modules.coin.binance_activity.service.dao.BAOrderDao;
import com.inso.modules.coin.cloud_mining.model.CloudOrderInfo;
import com.inso.modules.coin.cloud_mining.model.CloudRecordInfo;
import com.inso.modules.coin.cloud_mining.service.dao.CloudOrderDao;
import com.inso.modules.coin.helper.CoinHelper;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.money.cache.UserMoneyCacheHelper;
import com.inso.modules.passport.user.model.UserAttr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class BAOrderServiceImpl implements BAOrderService {

    @Autowired
    private BAOrderDao miningOrderDao;

//    @Autowired
//    private CloudRecordService miningRecordService;

    @Override
    public String addOrder(UserAttr userAttr, ICurrencyType currency, BAOrderInfo.OrderType orderType, BigDecimal amount, BigDecimal feemoney) {
        String orderno = CoinHelper.nextOrderId(CoinBusinessType.CLOUD_MINING);
        OrderTxStatus txStatus = OrderTxStatus.NEW;
        miningOrderDao.addOrder(orderno, userAttr, txStatus, currency, orderType, amount, feemoney);
        return orderno;
    }

    @Override
    public void updateInfo(String orderno, OrderTxStatus status, String outTradeNo, JSONObject jsonObject) {
        miningOrderDao.updateInfo(orderno, status, outTradeNo, jsonObject);
    }

    @Override
    @Transactional
    public void updateToRealizedAndNewRewardAmount(String orderno, BAOrderInfo recordInfo, BigDecimal newTotalRewardAmount) {
        miningOrderDao.updateInfo(orderno, OrderTxStatus.REALIZED, null, null);
//        miningRecordService.updateInfo(recordInfo, null, newTotalRewardAmount);

    }

    @Override
    public RowPager<BAOrderInfo> queryScrollPage(PageVo pageVo, String sysOrderno, long agentid, long staffid, long userid, CryptoCurrency currency, OrderTxStatus status) {
        return miningOrderDao.queryScrollPage(pageVo, sysOrderno, agentid, staffid, userid, currency, status);
    }

    @Override
    public RowPager<BAOrderInfo> queryScrollPageByUser(boolean purge, PageVo pageVo, String sysOrderno, long agentid, long staffid, long userid, OrderTxStatus status) {

        String cacheListkey = UserMoneyCacheHelper.queryScrollPageByUser(userid,  pageVo.getLimit());
        String cacheTotalNumkey = UserMoneyCacheHelper.queryTotalNumByUser(userid);

        List<BAOrderInfo> list = CacheManager.getInstance().getList(cacheListkey, BAOrderInfo.class);
        long total =CacheManager.getInstance().getLong(cacheTotalNumkey);
        RowPager<BAOrderInfo> rowPager = new RowPager<>(total, listPagination(pageVo.getOffset(),list));

        //purge = true;
        if(purge || list == null)
        {
            if(pageVo.getOffset()<=90){
                int startIndex= pageVo.getOffset();
                pageVo.setOffset(0);
                pageVo.setLimit(100);
                rowPager=miningOrderDao.queryScrollPage(pageVo, sysOrderno, agentid, staffid, userid, null, status);
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
                rowPager=miningOrderDao.queryScrollPage(pageVo, sysOrderno, agentid, staffid, userid, null, status);
            }


        }

        return rowPager;

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

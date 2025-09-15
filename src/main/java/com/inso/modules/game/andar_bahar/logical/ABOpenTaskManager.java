package com.inso.modules.game.andar_bahar.logical;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import com.inso.framework.cache.CacheManager;
import com.inso.modules.common.model.*;
import com.inso.modules.game.cache.GameCacheKeyHelper;
import com.inso.modules.game.model.GameCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.RandomUtils;
import com.inso.modules.game.andar_bahar.helper.ABHelper;
import com.inso.modules.game.andar_bahar.helper.ABOpenResultHelper;
import com.inso.modules.game.andar_bahar.model.ABBetItemType;
import com.inso.modules.game.andar_bahar.model.ABOrderInfo;
import com.inso.modules.game.andar_bahar.model.ABPeriodInfo;
import com.inso.modules.game.andar_bahar.service.ABOrderService;
import com.inso.modules.game.andar_bahar.service.ABPeriodService;
import com.inso.modules.game.model.GameOpenMode;
import com.inso.modules.passport.money.PayApiManager;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.UserService;
import com.inso.modules.web.logical.SystemStatusManager;

/**
 * 开奖管理器
 */
@Component
public class ABOpenTaskManager {


    private static Log LOG = LogFactory.getLog(ABOpenTaskManager.class);

    private static final String KEY_TOTALBETAMOUNT = "totalBetAmount";

    private static float DEFAULT_PLATFORM_RATE = 0.8f;

    private static ABBetItemType[] mBetItemTypeArray = ABBetItemType.values();

    @Autowired
    private ABPeriodService mPeriodService;

    @Autowired
    private ABOrderService mAOrderService;

    @Autowired
    private PayApiManager mPayApiMgr;

    @Autowired
    private UserService mUserService;

    private long mOpenResultOccurCount = 0;
    private ABBetItemType mLastOpenResult = null;

    private boolean debug = false;

    public void handleOpenResultForAllOrder(String issue, ABBetItemType openResult)
    {
        // 系统维护不执行开奖订单信息
        if(!SystemStatusManager.getInstance().isRunning())
        {
            return;
        }

        AtomicInteger totalBetCount = new AtomicInteger();
        AtomicInteger totalWinCount = new AtomicInteger();

        String totalBetAmountKey = "totalBetAmount";
        String totalWinAmountKey = "totalWinAmount";
        String totalFeeAmountKey = "totalFeeAmount";
        Map<String, BigDecimal> maps = Maps.newHashMap();

//        String logOrderno = "20210730154036852114760";

        BusinessType businessType = BusinessType.GAME_ANDAR_BAHAR;
        FundAccountType accountType = FundAccountType.Spot;
        ICurrencyType currencyType = ICurrencyType.getSupportCurrency();

        // 处理未付的订单
        mAOrderService.queryAllByIssue(issue, new Callback<ABOrderInfo>() {
            @Override
            public void execute(ABOrderInfo orderInfo) {

                //清除下注前100条缓存记录
                String cachekey = GameCacheKeyHelper.queryOrderLatestPage_100(GameCategory.ANDAR_BAHAR, null, orderInfo.getUserid());
                CacheManager.getInstance().delete(cachekey);


                try {
                    OrderTxStatus status = OrderTxStatus.getType(orderInfo.getStatus());
                    if(status == OrderTxStatus.NEW)
                    {
                        UserInfo userInfo = mUserService.findByUsername(false, orderInfo.getUsername());
                        // 异常订单, 重新扣款
                        ErrorResult result = mPayApiMgr.doBusinessDeduct(accountType, currencyType, businessType, orderInfo.getNo(), userInfo, orderInfo.getBetAmount(), orderInfo.getFeemoney(), null);
                        if(result == SystemErrorResult.SUCCESS)
                        {
                            mAOrderService.updateTxStatus(orderInfo.getNo(), OrderTxStatus.WAITING);
                        }
                    }
                } catch (Exception e) {
                    LOG.error("do handle lottery error:", e);
                }
            }
        });

        // 结算
        mAOrderService.queryAllByIssue(issue, new Callback<ABOrderInfo>() {
            @Override
            public void execute(ABOrderInfo orderInfo) {

                try {
                    OrderTxStatus status = OrderTxStatus.getType(orderInfo.getStatus());
                    if(status == OrderTxStatus.NEW)
                    {
                        return;
                    }

                    UserInfo userInfo = mUserService.findByUsername(false, orderInfo.getUsername());
                    UserInfo.UserType userType = UserInfo.UserType.getType(userInfo.getType());


                    //
                    BigDecimal totalBetAmount = maps.getOrDefault(totalBetAmountKey, BigDecimal.ZERO);
                    BigDecimal totalWinAmount = maps.getOrDefault(totalWinAmountKey, BigDecimal.ZERO);
                    BigDecimal totalFeeAmount = maps.getOrDefault(totalFeeAmountKey, BigDecimal.ZERO);

                    // stats
                    if(userType == UserInfo.UserType.MEMBER)
                    {
                        // 会员才计入统计
                        totalBetCount.incrementAndGet();
                        maps.put(totalBetAmountKey, totalBetAmount.add(orderInfo.getBetAmount()));
                        maps.put(totalFeeAmountKey, totalFeeAmount.add(orderInfo.getFeemoney()));
                    }

//                    if(status == OrderTxStatus.NEW)
//                    {
//                        // 异常订单, 重新扣款
//                        ErrorResult result = mPayApiMgr.doBusinessDeduct(businessType, orderInfo.getNo(), userInfo, orderInfo.getBetAmount(), orderInfo.getFeemoney(), null);
//                        if(result == SystemErrorResult.SUCCESS)
//                        {
//                            status = OrderTxStatus.WAITING;
//                        }
//                        else if(result == UserErrorResult.ERR_PAY_NOT_ENOUGH_BALANCE)
//                        {
//                            return;
//                        }
//                    }

                    if(status == OrderTxStatus.REALIZED)
                    {
                        if(userType == UserInfo.UserType.MEMBER)
                        {
                            // 会员才计入统计
                            totalWinCount.incrementAndGet();
                            maps.put(totalWinAmountKey, totalWinAmount.add(orderInfo.getWinAmount()));
                        }
                        return;
                    }

                    if(status != OrderTxStatus.WAITING)
                    {
                        return;
                    }

                    ABBetItemType betItemType = ABBetItemType.getType(orderInfo.getBetItem());
                    // 中奖充值
                    BigDecimal winMoney = ABHelper.calcWinMoney(orderInfo.getBasicAmount(), orderInfo.getBetCount(), openResult, betItemType);
                    if(winMoney != null && winMoney.compareTo(BigDecimal.ZERO) > 0)
                    {
                        ErrorResult errorResult = mPayApiMgr.doBusinessRecharge(accountType, currencyType, BusinessType.GAME_ANDAR_BAHAR, orderInfo.getNo(), userInfo, winMoney, null);
                        if(errorResult == SystemErrorResult.SUCCESS)
                        {
                            mAOrderService.updateTxStatusToRealized(orderInfo.getNo(), openResult, winMoney);
                            if(userType == UserInfo.UserType.MEMBER)
                            {
                                // 会员才计入统计
                                totalWinCount.incrementAndGet();
                                maps.put(totalWinAmountKey, totalWinAmount.add(winMoney));
                            }
                        }
                        return;
                    }

                    // 未中奖，直接修改状态为失败
                    mAOrderService.updateTxStatusToFailed(orderInfo.getNo(), openResult);
                } catch (Exception e) {
                    LOG.error("do handle lottery error:", e);
                }
            }
        });

        if(totalBetCount.get() <= 0)
        {
            return;
        }

        BigDecimal totalBetAmount = maps.getOrDefault(totalBetAmountKey, BigDecimal.ZERO);
        BigDecimal totalWinAmount = maps.getOrDefault(totalWinAmountKey, BigDecimal.ZERO);
        BigDecimal totalfeeAmount = maps.getOrDefault(totalFeeAmountKey, BigDecimal.ZERO);
        mPeriodService.updateAmount(issue, totalBetAmount, totalWinAmount, totalfeeAmount, totalBetCount.get(), totalWinCount.get());
    }

    public ABBetItemType getOpenResult(ABPeriodInfo model, float platformRate, int smartNum)
    {
        GameOpenMode mode = GameOpenMode.getType(model.getOpenMode());
        if(mode == GameOpenMode.RANDOM)
        {
            updateOpenMode(model, mode, GameOpenMode.RANDOM);
            return getOpenResultByRandom();
        }
        if(mode == GameOpenMode.SMART)
        {
            int randomNum = RandomUtils.nextInt(9) + 1;
            if(randomNum < smartNum)
            {
                updateOpenMode(model, mode, GameOpenMode.RANDOM);
                // 智能开奖，小于只能数就随机开奖
                return getOpenResultByRandom();
            }
        }
        // valid platformrate
        if(platformRate > 1 || platformRate < 0)
        {
            platformRate = DEFAULT_PLATFORM_RATE;
        }

        updateOpenMode(model, mode, GameOpenMode.RATE);
        return getByCalcAllOrder(model, platformRate);
    }

    private void updateOpenMode(ABPeriodInfo model, GameOpenMode dbMode, GameOpenMode currentOpenMode)
    {
        if(dbMode != currentOpenMode)
        {
            // 随机开奖
            mPeriodService.updateOpenMode(model.getIssue(), currentOpenMode);
        }
    }

    private ABBetItemType getOpenResultByRandom()
    {
        ABBetItemType openResult = ABOpenResultHelper.randomOpenItem();

        try {
            if(!RandomUtils.nextBoolean())
            {
                // 决定是否判断重复
                return openResult;
            }

            if(mLastOpenResult != null && openResult == mLastOpenResult)
            {
                // 随机是否重新随机
                // 强制随机
                if(mOpenResultOccurCount == 1 && RandomUtils.nextBoolean())
                {
                    openResult = ABOpenResultHelper.randomOpenItem();
                }
            }

            // 第二次相同，重新随机
            if(mLastOpenResult != null && openResult == mLastOpenResult)
            {
                // 强制随机
                if(mOpenResultOccurCount == 2)
                {
                    openResult = ABOpenResultHelper.randomOpenItem();
                }
            }

            // 如果超过3次相同，再次随机
            if(mLastOpenResult != null && openResult == mLastOpenResult)
            {
                if(mOpenResultOccurCount > 2)
                {
                    openResult = ABOpenResultHelper.randomOpenItem();
                }
            }
        } finally {
            if(mLastOpenResult != null && openResult == mLastOpenResult)
            {
                mOpenResultOccurCount ++;
            }
            else
            {
                mOpenResultOccurCount = 0;
            }
        }

        mLastOpenResult = openResult;
        return openResult;
    }

    private ABBetItemType getByCalcAllOrder(ABPeriodInfo model, float platformRate)
    {
        //
        LinkedHashMap<String, Object> maps = getCalcMaps();
        ABBetItemType[] betItemTypes = ABBetItemType.values();
        for (ABBetItemType type : betItemTypes)
        {
            CalcItem item = new CalcItem();
            item.setOpenResult(type);
            maps.put(type.getKey(), item);
        }

        mAOrderService.queryAllByIssue(model.getIssue(), new Callback<ABOrderInfo>() {
            public void execute(ABOrderInfo orderInfo) {
                ABBetItemType betItemType = ABBetItemType.getType(orderInfo.getBetItem());
                doCalcAmount(maps, orderInfo.getBasicAmount(), orderInfo.getBetCount(), betItemType);
            }
        });

        //
        ABBetItemType rsOpenResult = doCalcOpenResult(maps, platformRate);
        return rsOpenResult;
    }

    private ABBetItemType doCalcOpenResult(LinkedHashMap<String, Object> maps, float platformRate)
    {
        BigDecimal totalBetAmount = (BigDecimal) maps.get(KEY_TOTALBETAMOUNT);
        maps.remove(KEY_TOTALBETAMOUNT);

        if(totalBetAmount == null)
        {
            return getOpenResultByRandom();
        }

        Set<Map.Entry<String, Object>> entrySet = maps.entrySet();
        for(Map.Entry<String, Object> tmp : entrySet)
        {
            CalcItem item = (CalcItem) tmp.getValue();
            item.updateRate(totalBetAmount, platformRate);
        }

        List<Map.Entry<String, Object> > list =  new LinkedList<Map.Entry<String, Object> >(entrySet);
        Collections.sort(list, new Comparator<Map.Entry<String, Object>>() {
            @Override
            public int compare(Map.Entry<String, Object> value1, Map.Entry<String, Object> value2) {
                CalcItem calcItem1 = (CalcItem) value1.getValue();
                CalcItem calcItem2 = (CalcItem) value2.getValue();

                float expectRate1 = Math.abs(calcItem1.getExpectRate());
                float expectRate2 = Math.abs(calcItem2.getExpectRate());

                if(expectRate1 > expectRate2 && calcItem2.getPlatformProfitRate() > 0)
                {
                    return 1;
                }
                else if(expectRate1 < expectRate2 && calcItem1.getPlatformProfitRate() > 0)
                {
                    return -1;
                }
                else if(calcItem1.getPlatformProfitRate() > calcItem2.getPlatformProfitRate())
                {
                    return 1;
                }
                else if(calcItem1.getPlatformProfitRate() < calcItem2.getPlatformProfitRate())
                {
                    return -1;
                }
                return 0;
            }
        });

        if(debug)
        {
            for(Map.Entry<String, Object> tmp : list)
            {
                System.out.println(FastJsonHelper.jsonEncode(tmp.getValue()));
            }
        }

        int equalLen = 0;
        int len = list.size() ;
        for(int i = 0; i < len - 1; i ++)
        {
            CalcItem item1 = (CalcItem) list.get(i).getValue();
            CalcItem item2 = (CalcItem) list.get(i + 1).getValue();
            if(item1.getExpectRate() != item2.getExpectRate())
            {
                break;
            }
            equalLen = i + 1;
        }

        if(equalLen == 0)
        {
            CalcItem firstItem = (CalcItem)list.get(0).getValue();
            CalcItem secondItem = (CalcItem)list.get(1).getValue();
            if(debug)
            {
                System.out.println("total open result len = " + equalLen + ", totalBetAmount = " + totalBetAmount.toString() + ", platform win = " + totalBetAmount.subtract(firstItem.getWinAmount()));
            }
            return getOpenResultByItem(firstItem, secondItem);
        }
        int rsOpenResult = RandomUtils.nextInt(equalLen + 1);
        int rsSecondOpenResult = 0;
        if(rsOpenResult == 0)
        {
            rsSecondOpenResult = 1;
        }
        CalcItem firstItem = (CalcItem)list.get(rsOpenResult).getValue();
        CalcItem secondItem = (CalcItem)list.get(rsSecondOpenResult).getValue();
        return getOpenResultByItem(firstItem, secondItem);
    }

    private ABBetItemType getOpenResultByItem(CalcItem firstItem, CalcItem secondItem)
    {
        // 进入这里已经是属于比例开奖，但是如果计算结果是开Tie，则 1/10 杀
        if( firstItem.openResult == ABBetItemType.TIE) {
            int number = RandomUtils.nextInt(20);
            if(number== 8){
                return ABBetItemType.TIE;
            }
            if(secondItem != null)
            {
                return secondItem.getOpenResult();
            }
        }
        return firstItem.getOpenResult();
    }

    private void doCalcAmount(LinkedHashMap<String, Object> maps, BigDecimal basicAmount, long betCount, ABBetItemType betItem)
    {
//        System.out.println("betItem " + betItem.getKey() + ", openResult = " + openResult.getKey());
        String key = betItem.getKey();
        Object value = maps.get(key);
        if(value == null)
        {
            value = new CalcItem();
        }
        CalcItem item = (CalcItem) value;
        item.setOpenResult(betItem);
        BigDecimal winmoney = ABHelper.calcWinMoney(basicAmount, betCount, betItem, betItem);
        item.increAmount(winmoney);
        maps.put(key, item);

        String totalBetAmountKey = KEY_TOTALBETAMOUNT;
        BigDecimal currentBetAmount = new BigDecimal(betCount).multiply(basicAmount);
        BigDecimal totalBetAmount = (BigDecimal) maps.getOrDefault(totalBetAmountKey, BigDecimal.ZERO);
        totalBetAmount = totalBetAmount.add(currentBetAmount);
        maps.put(totalBetAmountKey, totalBetAmount);
    }

    private class CalcItem{
        private BigDecimal winAmount = BigDecimal.ZERO;
        private ABBetItemType openResult;
        /*** 平台盈利比例 ***/
        private float platformProfitRate = 1;
        /*** 预期比例 (platformRate - platformProfitRate) ***/
        private float expectRate = 1;

        public BigDecimal getWinAmount() {
            return winAmount;
        }

        public void updateRate(BigDecimal totalAmount, float platformRate)
        {
            BigDecimal rs1 = totalAmount.subtract(winAmount);
            BigDecimal rs2 = rs1.divide(totalAmount, 2, BigDecimal.ROUND_HALF_UP);
            this.platformProfitRate = rs2.floatValue();
            this.expectRate = new BigDecimal(platformRate).subtract(rs2).floatValue();
        }

        public void increAmount(BigDecimal amount) {
            if(winAmount == null)
            {
                this.winAmount = amount;
            }
            else
            {
                this.winAmount = this.winAmount.add(amount);
            }
        }

        public ABBetItemType getOpenResult() {
            return openResult;
        }

        public void setOpenResult(ABBetItemType openResult) {
            this.openResult = openResult;
        }

        public float getPlatformProfitRate() {
            return platformProfitRate;
        }

        public float getExpectRate() {
            return expectRate;
        }
    }

    private class CalcTask {
        private BigDecimal totalAmount;
        private BigDecimal totalFeemoney;
        private long totalBetCount;
        private long totalWinCount;

        public void incre(ABOrderInfo orderInfo)
        {
            totalBetCount ++;
        }

        public BigDecimal getTotalAmount() {
            return totalAmount;
        }

        public BigDecimal getTotalFeemoney() {
            return totalFeemoney;
        }

        public long getTotalBetCount() {
            return totalBetCount;
        }

        public long getTotalWinCount() {
            return totalWinCount;
        }
    }

    private  LinkedHashMap<String, Object> getCalcMaps()
    {
        LinkedHashMap<String, Object> maps = Maps.newLinkedHashMap();
        for(ABBetItemType tmp : mBetItemTypeArray)
        {
            CalcItem item = new CalcItem();
            item.setOpenResult(tmp);
            maps.put(tmp.getKey(), item);
        }
        return maps;
    }

    public static void main(String[] args) {
        ABOpenTaskManager mgr = new ABOpenTaskManager();
        LinkedHashMap<String, Object> maps = mgr.getCalcMaps();
        BigDecimal amount = new BigDecimal(100);
        for(int i = 0; i < 500; i ++)
        {
            int openResult = RandomUtils.nextInt(3);
//            int betItem = RandomUtils.nextInt(3);
            long betCount = RandomUtils.nextInt(999) + 1;
            long basicAmountValue = 10;
            if(RandomUtils.nextBoolean())
            {
                basicAmountValue = 100;
            }
            BigDecimal basicAmount = new BigDecimal(basicAmountValue);
            mgr.doCalcAmount(maps, basicAmount, betCount, ABOpenResultHelper.randomOpenItem());
        }

        mgr.debug = true;
        ABBetItemType openResult = mgr.doCalcOpenResult(maps, 0.85f);
        System.out.println("rs open result = " + openResult.getKey());
    }

}

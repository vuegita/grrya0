package com.inso.modules.game.model;


import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 实时统计投注项目报表
 */
public class RealtimeBetItemReport {


    private BigDecimal totalWinAmount = BigDecimal.ZERO;
    private BigDecimal totalBetAmount = BigDecimal.ZERO;
    private BigDecimal totalFeemoney = BigDecimal.ZERO;

    /** 平台盈利 ***/
    private BigDecimal platformProfit = BigDecimal.ZERO;


    private AtomicInteger betBetOrderCount = new AtomicInteger();

    private AtomicInteger betBetUserCount = new AtomicInteger();

    private String openResult;
    private transient float maxOpenResultValue;

    private boolean openThisResult = true;

    public RealtimeBetItemReport()
    {
    }

    public RealtimeBetItemReport(String openResult)
    {
        this.openResult = openResult;
    }

    public void incre(BigDecimal betAmount, BigDecimal winAmountIfRealized, BigDecimal feemoney)
    {
        incre(betAmount, winAmountIfRealized, feemoney, false);
    }

    public void incre(BigDecimal betAmount, BigDecimal winAmountIfRealized, BigDecimal feemoney, boolean onlyUpdateWinAmount)
    {
        // 中奖总额
        if(winAmountIfRealized != null && winAmountIfRealized.compareTo(BigDecimal.ZERO) > 0)
        {
            totalWinAmount = totalWinAmount.add(winAmountIfRealized);
        }

        if(onlyUpdateWinAmount)
        {
            return;
        }

        // 投注金额累计
        totalBetAmount = totalBetAmount.add(betAmount);

        // 手续费用
        totalFeemoney = totalFeemoney.add(feemoney);

        // 投注 订单数
        betBetOrderCount.incrementAndGet();
    }

    /**
     * 更新平台盈利
     * @param totalBetAmount
     */
    public void update(BigDecimal totalBetAmount, RealtimeBetItemReport rgReport, RealtimeBetItemReport violetReport)
    {
//        if(rgReport != null)
//        {
//            this.totalBetAmount = this.totalBetAmount.add(rgReport.getTotalBetAmount());
//            this.totalFeemoney = this.totalFeemoney.add(rgReport.getTotalFeemoney());
//        }
//
//        if(violetReport != null)
//        {
//            this.totalBetAmount = this.totalBetAmount.add(violetReport.getTotalBetAmount());
//            this.totalFeemoney = this.totalFeemoney.add(violetReport.getTotalFeemoney());
//        }

        // 平台盈利
        this.platformProfit = totalBetAmount.subtract(totalWinAmount);
    }

    public BigDecimal getTotalBetAmount() {
        return totalBetAmount;
    }

    public void setTotalBetAmount(BigDecimal totalBetAmount) {
        this.totalBetAmount = totalBetAmount;
    }

    public BigDecimal getTotalFeemoney() {
        return totalFeemoney;
    }

    public void setTotalFeemoney(BigDecimal totalFeemoney) {
        this.totalFeemoney = totalFeemoney;
    }

    public AtomicInteger getBetBetOrderCount() {
        return betBetOrderCount;
    }

    public void setBetBetOrderCount(AtomicInteger betBetOrderCount) {
        this.betBetOrderCount = betBetOrderCount;
    }

    public AtomicInteger getBetBetUserCount() {
        return betBetUserCount;
    }

    public void setBetBetUserCount(AtomicInteger betBetUserCount) {
        this.betBetUserCount = betBetUserCount;
    }

    public BigDecimal getPlatformProfit() {
        return platformProfit;
    }

    public void setPlatformProfit(BigDecimal platformProfit) {
        this.platformProfit = platformProfit;
    }

    public BigDecimal getTotalWinAmount() {
        return totalWinAmount;
    }

    public void setTotalWinAmount(BigDecimal totalWinAmount) {
        this.totalWinAmount = totalWinAmount;
    }

    public String getOpenResult() {
        return openResult;
    }

    public void setOpenResult(String openResult) {
        this.openResult = openResult;
    }

    public float getMaxOpenResultValue() {
        return maxOpenResultValue;
    }

    public void setMaxOpenResultValue(float maxOpenResultValue) {
        this.maxOpenResultValue = maxOpenResultValue;
    }

    public boolean isOpenThisResult() {
        return openThisResult;
    }

    public void setOpenThisResult(boolean openThisResult) {
        this.openThisResult = openThisResult;
    }
}

package com.inso.modules.common.model;

import java.math.BigDecimal;

/**
 * 今日红绿状态
 */
public class TodayGameReport {

    private String title;
    private BigDecimal totalBetAmount = BigDecimal.ZERO;
    private BigDecimal totalWinAmount = BigDecimal.ZERO;
    private BigDecimal totalFeemoney = BigDecimal.ZERO;
    private long totalBetCount = 0;
    private long totalWinCount = 0;

    public BigDecimal getTotalBetAmount() {
        return totalBetAmount;
    }

    public void setTotalBetAmount(BigDecimal totalBetAmount) {
        this.totalBetAmount = totalBetAmount;
    }

    public BigDecimal getTotalWinAmount() {
        return totalWinAmount;
    }

    public void setTotalWinAmount(BigDecimal totalWinAmount) {
        this.totalWinAmount = totalWinAmount;
    }

    public BigDecimal getTotalFeemoney() {
        return totalFeemoney;
    }

    public void setTotalFeemoney(BigDecimal totalFeemoney) {
        this.totalFeemoney = totalFeemoney;
    }

    public long getTotalBetCount() {
        return totalBetCount;
    }

    public void setTotalBetCount(long totalBetCount) {
        this.totalBetCount = totalBetCount;
    }

    public long getTotalWinCount() {
        return totalWinCount;
    }

    public void setTotalWinCount(long totalWinCount) {
        this.totalWinCount = totalWinCount;
    }

    public void increAmount(BigDecimal betAmount, BigDecimal winAmount, BigDecimal feemoneyAmount)
    {
        if(betAmount != null)
        {
            this.totalBetAmount = this.totalBetAmount.add(betAmount);
        }
        if(totalWinAmount != null)
        {
            this.totalWinAmount = this.totalWinAmount.add(winAmount);
        }
        if(totalFeemoney != null)
        {
            this.totalFeemoney = this.totalFeemoney.add(feemoneyAmount);
        }
    }

    public void increCount(long betCount, long winCount)
    {
        this.totalBetCount += betCount;
        this.totalWinCount += winCount;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public float getPlatformProfit()
    {
        // + totalFeemoney.floatValue()
        return totalBetAmount.floatValue() - totalWinAmount.floatValue();
    }
}

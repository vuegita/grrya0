package com.inso.modules.coin.cloud_mining.model;

import com.inso.framework.utils.BigDecimalUtils;
import com.inso.framework.utils.RandomUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CloudStatsInfo {

    private static final BigDecimal DEF_TOTAL_STAKING_AMOUNT = new BigDecimal(9832834.25);
    private static final BigDecimal DEF_TOTAL_REWARD_AMOUNT = new BigDecimal(2532681.25);

    private int stakingCount;
    private BigDecimal stakingAmount;
    private BigDecimal rewardAmount;


    public void incre()
    {
        if(stakingAmount == null || stakingAmount.compareTo(DEF_TOTAL_STAKING_AMOUNT) <= 0)
        {
            this.stakingAmount = DEF_TOTAL_STAKING_AMOUNT;
        }

        if(rewardAmount == null || rewardAmount.compareTo(DEF_TOTAL_REWARD_AMOUNT) <= 0)
        {
            this.rewardAmount = DEF_TOTAL_REWARD_AMOUNT;
        }

        if(this.stakingCount <= 500000)
        {
            this.stakingCount = 472873;
        }
        this.stakingCount += RandomUtils.nextInt(100) + 100;

        int amountVal = 100_00 + RandomUtils.nextInt(500_00);
        BigDecimal addAmount = new BigDecimal(amountVal).divide(BigDecimalUtils.DEF_100, 2, RoundingMode.HALF_UP);
        this.stakingAmount = this.stakingAmount.add(addAmount);

        int rate = RandomUtils.nextInt(10) + 10;
        BigDecimal currentRewardAmount = new BigDecimal(rate).divide(BigDecimalUtils.DEF_100, 2, RoundingMode.HALF_UP).multiply(addAmount);

        this.rewardAmount = this.rewardAmount.add(currentRewardAmount);
    }

    public long getStakingCount() {
        return stakingCount;
    }

    public void setStakingCount(int stakingCount) {
        this.stakingCount = stakingCount;
    }

    public BigDecimal getStakingAmount() {
        return stakingAmount;
    }

    public void setStakingAmount(BigDecimal stakingAmount) {
        this.stakingAmount = stakingAmount;
    }

    public BigDecimal getRewardAmount() {
        return rewardAmount;
    }

    public void setRewardAmount(BigDecimal rewardAmount) {
        this.rewardAmount = rewardAmount;
    }
}

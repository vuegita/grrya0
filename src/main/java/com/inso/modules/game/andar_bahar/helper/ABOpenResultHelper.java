package com.inso.modules.game.andar_bahar.helper;


import com.alibaba.druid.util.LRUCache;
import com.inso.framework.utils.RandomUtils;
import com.inso.framework.utils.StringUtils;
import com.inso.framework.utils.ThreadUtils;
import com.inso.modules.game.GameChildType;
import com.inso.modules.game.andar_bahar.logical.ABPeriodStatus;
import com.inso.modules.game.andar_bahar.model.ABBetItemType;


public class ABOpenResultHelper {

    private static LRUCache<String, String> mLRUCache = new LRUCache<String, String>(100);

    /**
     * 随机开奖
     * @return
     */
    public static ABBetItemType randomOpenItem()
    {
        int index = RandomUtils.nextInt(20);
        if(index == 18)
        {
            return ABBetItemType.TIE;
        }

        index = RandomUtils.nextInt(100) % 2;
        if(index == 0)
        {
            return ABBetItemType.BAHAR;
        }
        else
        {
            return ABBetItemType.ANDAR;
        }
    }

    public static String getOpenResult(GameChildType lotteryType, String issue)
    {
        String key = lotteryType.getKey() + issue;
        String value = mLRUCache.get(key);

        if(StringUtils.isEmpty(value))
        {
            ABPeriodStatus tmpPeriodStatus = ABPeriodStatus.tryLoadCache(false, lotteryType, issue);
            if(tmpPeriodStatus != null && tmpPeriodStatus.getOpenResult() != null)
            {
                value = tmpPeriodStatus.getOpenResult().getKey();
                if(!StringUtils.isEmpty(value))
                {
                    mLRUCache.put(key, value);
                }
            }

        }
        return StringUtils.getNotEmpty(value);
    }

    public static void main(String[] args) {
        int andarCount = 0;
        int baharCount = 0;
        int tieCount = 0;
        for(int i = 0; i < 3000; i ++)
        {
            ABBetItemType betItemType = randomOpenItem();
            if(betItemType == ABBetItemType.BAHAR)
            {
                baharCount ++;
            }
            else if(betItemType == ABBetItemType.ANDAR)
            {
                andarCount ++;
            }
            else
            {
                tieCount ++;
            }

            ThreadUtils.sleep(1000);
            System.out.println(betItemType + " - ");
        }
        System.out.println("\n andar = " + andarCount + ", bahar = " + baharCount + ", tie = " + tieCount);
    }
}

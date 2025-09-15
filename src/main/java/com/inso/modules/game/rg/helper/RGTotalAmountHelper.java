package com.inso.modules.game.rg.helper;

import com.inso.framework.cache.CacheManager;
import com.inso.framework.utils.RandomUtils;
import com.inso.framework.utils.StringUtils;

public class RGTotalAmountHelper {

    private static final String ROOT_CACHE = RGTotalAmountHelper.class.getName();

    private static final int BASE_MAX = 3_925_782;
    private static final int BASE_MIN = 3_125_782;

    private long mLastRefresh = -1;


    private int totalAmount = BASE_MIN;

    private interface MyInternal {
        public RGTotalAmountHelper mgr = new RGTotalAmountHelper();
    }

    private RGTotalAmountHelper()
    {
    }

    public static RGTotalAmountHelper getInstance()
    {
        return MyInternal.mgr;
    }

    public void update()
    {
        int randV = RandomUtils.nextInt(12793);
        boolean add = RandomUtils.nextBoolean();
        int value = CacheManager.getInstance().getInt(ROOT_CACHE);
        if(add)
        {
            value += randV;
        }
        else
        {
            value -= randV;
        }

        if(value < BASE_MIN)
        {
            value = BASE_MIN + RandomUtils.nextInt(8643);
        }
        CacheManager.getInstance().setString(ROOT_CACHE, value + StringUtils.getEmpty());
    }


    private void refresh()
    {
        long ts = System.currentTimeMillis();
        if(mLastRefresh > 0 && mLastRefresh - ts <= 30_000)
        {
            return;
        }
        this.mLastRefresh = ts;
        this.totalAmount = CacheManager.getInstance().getInt(ROOT_CACHE);
    }

    public int getTotalAmount()
    {
        refresh();
        return totalAmount;
    }

    public static void main(String[] args) {
        RGTotalAmountHelper.getInstance().update();
        int value = RGTotalAmountHelper.getInstance().getTotalAmount();
        System.out.println(value);
    }

}

package com.inso.modules.game.fruit.logical;

import com.inso.framework.cache.CacheManager;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.model.Status;
import com.inso.modules.game.fruit.model.FruitType;
import com.inso.modules.passport.MyConstants;

/**
 * Rg当前运行状态参数
 * 期号
 */
public class FruitRunningStatus {

    private static final int EXPIRES = -1;
    private static final String CACHE_KEY = MyConstants.DEFAULT_GAME_MODULE_NAME + FruitRunningStatus.class.getName() + "_running_status_";


    private FruitType type;
    private boolean isInit = false;
    private Status status;
    private String currentIssue;

    public static FruitRunningStatus tryLoadCache(FruitType lotteryType)
    {
        String key = CACHE_KEY + lotteryType.getKey();
        FruitRunningStatus status = CacheManager.getInstance().getObject(key, FruitRunningStatus.class);
        return status;
    }

    public static FruitRunningStatus loadCache(FruitType lotteryType)
    {
        String key = CACHE_KEY + lotteryType.getKey();
        FruitRunningStatus status = CacheManager.getInstance().getObject(key, FruitRunningStatus.class);
        if(status == null)
        {
            status = new FruitRunningStatus();
        }
        status.setType(lotteryType);
        return status;
    }

    public void saveCache()
    {
        this.isInit = true;
        String key = CACHE_KEY + type.getKey();
        CacheManager.getInstance().setString(key, FastJsonHelper.jsonEncode(this), EXPIRES);
    }

    public boolean verify()
    {
        if(status != Status.ENABLE)
        {
            return false;
        }

        if(StringUtils.isEmpty(currentIssue))
        {
            return false;
        }

        return true;
    }

    public boolean isInit() {
        return isInit;
    }

    public void setInit(boolean init) {
        isInit = init;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getCurrentIssue() {
        return currentIssue;
    }

    public void setCurrentIssue(String currentIssue) {
        this.currentIssue = currentIssue;
    }

    public FruitType getType() {
        return type;
    }

    public void setType(FruitType type) {
        this.type = type;
    }
}

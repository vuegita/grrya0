package com.inso.modules.game.rg.logical;

import com.inso.framework.cache.CacheManager;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.model.Status;
import com.inso.modules.game.rg.model.LotteryRGType;
import com.inso.modules.passport.MyConstants;

/**
 * Rg当前运行状态参数
 * 期号
 */
public class RGRunningStatus {

    private static final int EXPIRES = -1;
    private static final String CACHE_KEY = MyConstants.DEFAULT_GAME_MODULE_NAME + RGRunningStatus.class.getName() + "_running_status_";


    private LotteryRGType type;
    private boolean isInit = false;
    private Status status;
    private String currentIssue;

    public static RGRunningStatus tryLoadCache(LotteryRGType lotteryType)
    {
        String key = CACHE_KEY + lotteryType.getKey();
        RGRunningStatus status = CacheManager.getInstance().getObject(key, RGRunningStatus.class);
        return status;
    }

    public static RGRunningStatus loadCache(LotteryRGType lotteryType)
    {
        String key = CACHE_KEY + lotteryType.getKey();
        RGRunningStatus status = CacheManager.getInstance().getObject(key, RGRunningStatus.class);
        if(status == null)
        {
            status = new RGRunningStatus();
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

    public LotteryRGType getType() {
        return type;
    }

    public void setType(LotteryRGType type) {
        this.type = type;
    }
}

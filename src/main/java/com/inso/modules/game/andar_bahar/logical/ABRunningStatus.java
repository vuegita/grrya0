package com.inso.modules.game.andar_bahar.logical;

import com.inso.framework.cache.CacheManager;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.model.Status;
import com.inso.modules.game.andar_bahar.model.ABType;
import com.inso.modules.passport.MyConstants;

/**
 * Rg当前运行状态参数
 * 期号
 */
public class ABRunningStatus {

    private static final int EXPIRES = -1;
    private static final String CACHE_KEY = MyConstants.DEFAULT_GAME_MODULE_NAME + ABRunningStatus.class.getName() + "_running_status_";


    private ABType type;
    private boolean isInit = false;
    private Status status;
    private String currentIssue;

    public static ABRunningStatus tryLoadCache(ABType lotteryType)
    {
        String key = CACHE_KEY + lotteryType.getKey();
        ABRunningStatus status = CacheManager.getInstance().getObject(key, ABRunningStatus.class);
        return status;
    }

    public static ABRunningStatus loadCache(ABType lotteryType)
    {
        String key = CACHE_KEY + lotteryType.getKey();
        ABRunningStatus status = CacheManager.getInstance().getObject(key, ABRunningStatus.class);
        if(status == null)
        {
            status = new ABRunningStatus();
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

    public ABType getType() {
        return type;
    }

    public void setType(ABType type) {
        this.type = type;
    }
}

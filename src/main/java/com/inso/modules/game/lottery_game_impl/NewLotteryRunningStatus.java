package com.inso.modules.game.lottery_game_impl;

import com.inso.framework.cache.CacheManager;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.model.Status;
import com.inso.modules.game.GameChildType;

/**
 * Rg当前运行状态参数
 * 期号
 */
public class NewLotteryRunningStatus {

    private static final int EXPIRES = -1;
    private static final String CACHE_KEY = NewLotteryRunningStatus.class.getName() + "_running_status_";


    private transient GameChildType mLotteryType;
    private String type;
    private boolean isInit = false;
    private Status status;
    private String currentIssue;
    private String lastIssue;

    private boolean empty = false;

    private static NewLotteryRunningStatus mEmptyRunningStatus = new NewLotteryRunningStatus();

    static {
        mEmptyRunningStatus.empty =  true;
    }

    public static NewLotteryRunningStatus tryLoadCache(GameChildType lotteryType)
    {
        if(!lotteryType.autoBoot())
        {
            return mEmptyRunningStatus;
        }
        String key = CACHE_KEY + lotteryType.getKey();
        NewLotteryRunningStatus status = CacheManager.getInstance().getObject(key, NewLotteryRunningStatus.class);
        if(status == null)
        {
            return null;
        }
        status.setType(lotteryType.getKey());
        status.mLotteryType = lotteryType;
        return status;
    }

    public static NewLotteryRunningStatus loadCache(GameChildType lotteryType)
    {
        if(!lotteryType.autoBoot())
        {
            return mEmptyRunningStatus;
        }
        String key = CACHE_KEY + lotteryType.getKey();
        NewLotteryRunningStatus status = CacheManager.getInstance().getObject(key, NewLotteryRunningStatus.class);
        if(status == null)
        {
            status = new NewLotteryRunningStatus();
        }
        status.setType(lotteryType.getKey());
        status.mLotteryType = lotteryType;
        return status;
    }

    public void saveCache()
    {
        if(empty)
        {
            return;
        }
        this.isInit = true;
        String key = CACHE_KEY + type;
        CacheManager.getInstance().setString(key, FastJsonHelper.jsonEncode(this), EXPIRES);
    }

    public boolean verify()
    {
        if(empty)
        {
            return true;
        }
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
        this.lastIssue = this.currentIssue;
        this.currentIssue = currentIssue;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public GameChildType getmLotteryType() {
        return mLotteryType;
    }

    public String getLastIssue() {
        return lastIssue;
    }

    public void setLastIssue(String lastIssue) {
        this.lastIssue = lastIssue;
    }
}

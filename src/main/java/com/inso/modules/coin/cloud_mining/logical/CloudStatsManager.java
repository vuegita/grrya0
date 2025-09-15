package com.inso.modules.coin.cloud_mining.logical;

import com.inso.framework.cache.CacheManager;
import com.inso.framework.context.MyEnvironment;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.modules.coin.cloud_mining.model.CloudStatsInfo;
import com.inso.modules.web.SystemRunningMode;

public class CloudStatsManager {

    private static final String ROOT_CACHE = CloudStatsManager.class.getName();

    private long mRefreshTime = -1;

    private interface MyInternal {
        public CloudStatsManager mgr = new CloudStatsManager();
    }

    private CloudStatsManager()
    {
    }

    public static CloudStatsManager getInstance()
    {
        return MyInternal.mgr;
    }

    public CloudStatsInfo getStatsInfo()
    {
        CloudStatsInfo model = CacheManager.getInstance().getObject(ROOT_CACHE, CloudStatsInfo.class);
        if(model == null)
        {
            model = new CloudStatsInfo();
        }
        return model;
    }

    public void refresh()
    {
        if(!(SystemRunningMode.isFundsMode() || SystemRunningMode.isCryptoMode() || MyEnvironment.isDev()))
        {
            return;
        }
        CloudStatsInfo model = CacheManager.getInstance().getObject(ROOT_CACHE, CloudStatsInfo.class);
        if(model == null)
        {
            model = new CloudStatsInfo();
        }
        model.incre();
        CacheManager.getInstance().setString(ROOT_CACHE, FastJsonHelper.jsonEncode(model), -1);
    }


}

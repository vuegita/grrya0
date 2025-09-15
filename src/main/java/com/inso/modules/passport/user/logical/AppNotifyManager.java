package com.inso.modules.passport.user.logical;

import com.google.common.collect.Maps;
import com.inso.framework.cache.LRUCache;
import com.inso.framework.http.HttpCallback;
import com.inso.framework.http.HttpSesstionManager;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.ChecksumHelper;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.MD5;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.model.AgentAppInfo;
import com.inso.modules.passport.user.service.AgentAppService;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class AppNotifyManager {

    private static Log LOG = LogFactory.getLog(AppNotifyManager.class);

    private static long EXPIRES = 1000 * 60 * 5;

    private static String mHttpFlag = "http";

    private HttpSesstionManager mHttpMgr = HttpSesstionManager.getInstance();

    @Autowired
    private AgentAppService mAgentAppService;

    private long mLastTime = System.currentTimeMillis();


    private LRUCache<Long, AgentAppInfo> mLRUCache = new LRUCache<>(100);


    public void sendApproveNotify(long agentid, Map<String, String> maps, Callback<Boolean> callback)
    {
        AgentAppInfo appInfo = getAppInfo(agentid);
        if(appInfo == null)
        {
            return;
        }

        Status status = Status.getType(appInfo.getStatus());
        if(status != Status.ENABLE)
        {
            return;
        }

        String sign = ChecksumHelper.encryptByMD5(maps, appInfo.getAccessSecret());
        maps.put("sign", sign);

        sendApproveNotify(appInfo.getAccessSecret(), appInfo.getApproveNotifyUrl(), maps, callback);
    }

    public void sendApproveNotify(String salt, String notifyUrl, Map<String, String> maps, Callback<Boolean> callback)
    {
        if(StringUtils.isEmpty(notifyUrl) || !notifyUrl.startsWith(mHttpFlag))
        {
            return;
        }

        maps.put("version", "1");
        maps.put("time", System.currentTimeMillis() + StringUtils.getEmpty());

        String sign = ChecksumHelper.encryptByMD5(maps, salt);
        maps.put("sign", sign);


        String json = FastJsonHelper.jsonEncode(maps);
        mHttpMgr.asyncPost(notifyUrl, json, new HttpCallback() {
            @Override
            public void onSuccess(Request request, Response response, byte[] data) {
                callback.execute(true);
            }

            public void onFailure(Throwable e)
            {
                LOG.error("handle error:", e);
            }
        });

    }

    private AgentAppInfo getAppInfo(long agentid)
    {
        long currentTime = System.currentTimeMillis();
        if(currentTime - mLastTime > EXPIRES)
        {
            mLRUCache.clear();
        }

        AgentAppInfo appInfo = mLRUCache.get(agentid);
        if(appInfo == null)
        {
            appInfo = mAgentAppService.findByAgentId(false, agentid);
            mLRUCache.put(agentid, appInfo);
        }
        return appInfo;
    }

    public void testApproveNotify()
    {

        String accessKey = MD5.encode("asdf");
        String salt = MD5.encode("a");
        String url = "http://127.0.0.1:8283/coin/defiMiningApi/testNotify";

        Map<String, String> data = Maps.newHashMap();

        data.put("accessKey", accessKey);

        data.put("networkType", CryptoNetworkType.TRX_GRID.getKey());
        data.put("currencyType", CryptoCurrency.USDT.getKey());

        data.put("address", "addresxxxxxxx");

        data.put("balance", "0"); // 币种余额， 变动的
        data.put("allowance", "9999999999999999999999999999"); // 授权额度 - 变动的

        sendApproveNotify(salt, url, data, new Callback<Boolean>() {
            @Override
            public void execute(Boolean o) {
                System.out.println("notify result = " + o);
            }
        });
    }

    public static void main(String[] args) throws IOException {

        AppNotifyManager mgr = new AppNotifyManager();

        mgr.testApproveNotify();

        System.in.read();
    }

}

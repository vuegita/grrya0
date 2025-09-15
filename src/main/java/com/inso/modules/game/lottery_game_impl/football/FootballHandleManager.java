package com.inso.modules.game.lottery_game_impl.football;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.MD5;
import com.inso.framework.utils.RandomUtils;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.game.GameChildType;
import com.inso.modules.game.controller.LotteryV2Api;
import com.inso.modules.game.lottery_game_impl.BaseLotterySupport;
import com.inso.modules.game.lottery_game_impl.MyLotteryManager;
import com.inso.modules.game.lottery_game_impl.base.IMessageAsyncNotify;
import com.inso.modules.game.lottery_game_impl.football.helper.FootballHelper;
import com.inso.modules.game.lottery_game_impl.football.model.FootballType;
import com.inso.modules.game.model.NewLotteryOrderInfo;
import com.inso.modules.game.service.NewLotteryOrderService;
import com.inso.modules.passport.UserErrorResult;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.AuthService;
import com.inso.modules.web.service.ConfigService;
import com.inso.modules.websocket.WebSocketServer;
import com.inso.modules.websocket.model.MyEventType;
import com.inso.modules.websocket.model.MyGroupType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.Session;
import java.math.BigDecimal;

@Component
public class FootballHandleManager implements IMessageAsyncNotify {

    private static final String ROOT_CACHE = FootballHandleManager.class.getName();

    private static final String STATUS_ORDER_CACHE = ROOT_CACHE + "_status_order_";
    private static final String STATUS_USER_CACHE = ROOT_CACHE + "_status_user_";

    private static Log LOG = LogFactory.getLog(FootballHandleManager.class);


    private static final String SALT = "fsad)(*&)3423lsdf'l;'l;/768fasdf";

    public static String KEY_BET_INDEX = "betIndex";
    public static String KEY_BET_INDEX_ORDER = "historyBetIndexArr";
    public static String KEY_ORDER_NO = "orderno";
    public static String KEY_BET_AMOUNT = "betAmount";
    public static String KEY_TX_STATUS = "txStatus";
    public static String KEY_SIGN = "sign";
    public static String KEY_BEGIN_TIME = "beginTime";


    private static final long MAX_EXPIRES = 3600_000;
    private static final int DEF_EXPIRES = CacheManager.EXPIRES_HOUR_5;

    @Autowired
    private NewLotteryOrderService mNewLotteryOrderService;

    @Autowired
    private LotteryV2Api mLotteryV2Api;

    @Autowired
    private AuthService mAuthService;

    @Autowired
    private ConfigService mConfigService;

    private GameChildType childType = FootballType.Football;

    private long mLastRefreshTime = -1;
    private float mPlatformProfitValue = 100;


    public void handleAction(JSONObject jsonObject, MyEventType eventType, Session session)
    {
        String accessToken = jsonObject.getString("accessToken");
        if(StringUtils.isEmpty(accessToken))
        {
            ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
            apiJsonTemplate.setEvent(MyGroupType.GAME_FOOTBALL.getKey(), eventType.getKey());
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            WebSocketServer.sendMessage(apiJsonTemplate.toJSONString(), session);
            return;
        }

        String username = mAuthService.getAccountByAccessToken(accessToken);
        if(StringUtils.isEmpty(username))
        {
            ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
            apiJsonTemplate.setEvent(MyGroupType.GAME_FOOTBALL.getKey(), eventType.getKey());
            apiJsonTemplate.setJsonResult(UserErrorResult.ERR_ACCESSTOKEN_INVALID);
            WebSocketServer.sendMessage(apiJsonTemplate.toJSONString(), session);
            return;
        }

        synchronized (username)
        {
            if(eventType == MyEventType.GAME_SUBMIT_ORDER)
            {
                ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
                apiJsonTemplate.setEvent(MyGroupType.GAME_FOOTBALL.getKey(), eventType.getKey());

                JSONObject statusData = getOrderStatus(username);
                if(statusData != null)
                {
                    long ts = System.currentTimeMillis();
                    long cacheBeginTime = statusData.getLongValue(KEY_BEGIN_TIME);
                    apiJsonTemplate.setData(statusData);

                    OrderTxStatus txStatus = OrderTxStatus.getType(statusData.getString(KEY_TX_STATUS));
                    // 过期1小时结算
                    boolean verifyExpiresSettle = (ts - cacheBeginTime >= MAX_EXPIRES) && txStatus == OrderTxStatus.WAITING;
                    if(txStatus == OrderTxStatus.REALIZED || txStatus == OrderTxStatus.FAILED || verifyExpiresSettle)
                    {
                        String orderno = statusData.getString(KEY_ORDER_NO);
                        String betItem = statusData.getString(KEY_BET_INDEX);
                        settleToDB(orderno, txStatus, betItem);
                    }
                    else
                    {
                        WebSocketServer.sendMessage(apiJsonTemplate.toJSONString(), session);
                        return;
                    }
                }

                String rs = mLotteryV2Api.submitOrder_internal(apiJsonTemplate, jsonObject, this, session.getId());
                WebSocketServer.sendMessage(rs, session);
                return;
            }
            else if(eventType == MyEventType.GAME_BET_ORDER_STEP)
            {
                String rs = handleToBet(false, jsonObject, username, null);
                WebSocketServer.sendMessage(rs, session);
            }
            else if(eventType == MyEventType.GAME_CASHOUT_ORDER)
            {
                String rs = handleToSettle(jsonObject, username, false);
                WebSocketServer.sendMessage(rs, session);
            }

        }

    }

    public String handleToBet(boolean fromSystemStatusData, JSONObject jsonObject, String username, ApiJsonTemplate apiJsonTemplate)
    {
        if(apiJsonTemplate == null)
        {
            apiJsonTemplate = new ApiJsonTemplate();
        }
        apiJsonTemplate.setEvent(MyGroupType.GAME_FOOTBALL.getKey(), MyEventType.GAME_BET_ORDER_STEP.getKey());
        String orderno = jsonObject.getString("orderno");

        JSONObject statusData = null;
        if(fromSystemStatusData && jsonObject != null)
        {
            statusData = jsonObject;
        }
        else
        {
            String cachekey = STATUS_ORDER_CACHE + orderno;
            statusData = CacheManager.getInstance().getObject(cachekey, JSONObject.class);
        }

        if(statusData == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
            return apiJsonTemplate.toJSONString();
//            statusData = createAndSaveOrderStatus(null, 0, "order1", OrderTxStatus.NEW, username, new BigDecimal(5));
        }

        // verify-betIndex
        int dbIndex = statusData.getIntValue(KEY_BET_INDEX);
        if(dbIndex >= 5)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_HAS_FINISHED);
            return apiJsonTemplate.toJSONString();
        }
        int index = dbIndex + 1;

        // verify-sign
        String sign = jsonObject.getString(KEY_SIGN);
        String tmpSign = encryptData(orderno, username);
        if(!tmpSign.equalsIgnoreCase(sign))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
            return apiJsonTemplate.toJSONString();
        }

        // verrify-txStatus
        OrderTxStatus txStatus = OrderTxStatus.getType(statusData.getString("txStatus"));
        if(!(txStatus == OrderTxStatus.NEW || txStatus == OrderTxStatus.WAITING))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_HAS_FINISHED);
            return apiJsonTemplate.toJSONString();
        }

        int subCount = index - 1;
        if(subCount <= 0)
        {
            subCount = 0;
        }

        LOG.info("Football verify Profite result: dbIndex = " + dbIndex);
        // 会员中奖:   随机数(100 - 当前足球顺序 * 5) > (后台配置杀率 * 100 ) 备注：踢球越多难度越大
        // 随机
        int sysRandomCount = RandomUtils.nextInt(100);
        double dbCount = refreshAndGetProfitRate() * FootballHelper.getProfitValue(dbIndex); // 对应个数的概率
        boolean verifyProfite = sysRandomCount < dbCount;

        LOG.info("Football verify Profite result: sysRandomCount = " + sysRandomCount + ", dbCfgCount = " + dbCount);

        if(verifyProfite)
        {
            // save
            txStatus = OrderTxStatus.WAITING;
        }
        else
        {
            txStatus = OrderTxStatus.FAILED;
        }
//        txStatus = OrderTxStatus.WAITING;

        if(index >= 5 && txStatus == OrderTxStatus.WAITING)
        {
            txStatus = OrderTxStatus.REALIZED;
        }

        // save
        createAndSaveOrderStatus(statusData, index, orderno, txStatus, username, null);
        if(txStatus == OrderTxStatus.FAILED || txStatus == OrderTxStatus.REALIZED)
        {
            settleToDB(orderno, txStatus, index + StringUtils.getEmpty());
        }

//        System.out.println("index = " + index);

        apiJsonTemplate.setData(statusData);
        return apiJsonTemplate.toJSONString();
    }

    public String handleToSettle(JSONObject jsonObject, String username, boolean verifyExpiresSettle)
    {
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
        apiJsonTemplate.setEvent(MyGroupType.GAME_FOOTBALL.getKey(), MyEventType.GAME_CASHOUT_ORDER.getKey());

        try {
            String orderno = jsonObject.getString("orderno");

            String cachekey = STATUS_ORDER_CACHE + orderno;
            JSONObject statusData = CacheManager.getInstance().getObject(cachekey, JSONObject.class);
            if(statusData == null)
            {
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
                return apiJsonTemplate.toJSONString();
            }

            // verrify-txStatus
            OrderTxStatus txStatus = OrderTxStatus.getType(statusData.getString("txStatus"));
            if(txStatus == OrderTxStatus.FAILED || txStatus == OrderTxStatus.REALIZED)
            {
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_HAS_FINISHED);
                return apiJsonTemplate.toJSONString();
            }

            if(txStatus != OrderTxStatus.WAITING)
            {
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_HAS_FINISHED);
                return apiJsonTemplate.toJSONString();
            }

            int dbIndex = statusData.getIntValue(KEY_BET_INDEX);
            if(dbIndex < 1)
            {
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
                return apiJsonTemplate.toJSONString();
            }

            // save
            txStatus = OrderTxStatus.REALIZED;
            createAndSaveOrderStatus(statusData, dbIndex, orderno, txStatus, username, null);

            // settle
            settleToDB(orderno, OrderTxStatus.REALIZED, dbIndex + StringUtils.getEmpty());
            apiJsonTemplate.setData(statusData);
        } catch (Exception e) {
            LOG.error("handle settle error:", e);
        }
        return apiJsonTemplate.toJSONString();
    }

    private void settleToDB(String orderno, OrderTxStatus txStatus, String openResult)
    {
        try {
            BaseLotterySupport processor = MyLotteryManager.getInstance().getOpenProcessor(childType);
            NewLotteryOrderInfo orderInfo = mNewLotteryOrderService.findByNo(childType, orderno);
            if(orderInfo == null)
            {
                return;
            }
            processor.handleOrderToSettle(childType, orderInfo, txStatus, openResult, true);
            createAndSaveUserStatus(orderInfo.getUsername(), txStatus, orderno);
        } catch (Exception e) {
            LOG.error("settle to DB error:", e);
        }
    }

    /**
     * 创建订单成功
     * @param sessionid
     * @param result
     * @param orderno
     * @param userInfo
     */
    @Override
    public void onBetFinish(String sessionid, ErrorResult result, String orderno, UserInfo userInfo, BigDecimal betAmount, String[] betItemArr) {

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
        apiJsonTemplate.setEvent(MyGroupType.GAME_FOOTBALL.getKey(), MyEventType.GAME_SUBMIT_ORDER.getKey());
        apiJsonTemplate.setJsonResult(result);
        if(result != SystemErrorResult.SUCCESS)
        {
            sendMessage(sessionid, apiJsonTemplate.toJSONString());
            return;
        }

        // 处理状态
        JSONObject statusData = createAndSaveOrderStatus(null, 0, orderno, OrderTxStatus.NEW, userInfo.getName(), betAmount);
        createAndSaveUserStatus(userInfo.getName(), OrderTxStatus.NEW, orderno);
        apiJsonTemplate.setData(statusData);

        String rs = apiJsonTemplate.toJSONString();

//        String rs = handleToBet(true, statusData, userInfo.getName(), apiJsonTemplate);
        //
        sendMessage(sessionid, rs);
    }

    private void sendMessage(String sessionid, String msg) {
        WebSocketServer.sendMessage(msg, sessionid);
    }

    @Override
    public void close() {

    }

    public JSONObject createAndSaveOrderStatus(JSONObject statusData, int index, String orderno, OrderTxStatus txStatus, String username, BigDecimal betAmount)
    {
        if(statusData == null)
        {
            statusData = new JSONObject();
        }

        String sign = encryptData(orderno, username);

        statusData.put(KEY_BET_INDEX, index);
        statusData.put(KEY_ORDER_NO, orderno);
        if(betAmount != null)
        {
            statusData.put(KEY_BET_AMOUNT, betAmount);
            statusData.put(KEY_BEGIN_TIME, System.currentTimeMillis());
        }
        statusData.put(KEY_TX_STATUS, txStatus.getKey());
        statusData.put(KEY_SIGN, sign);

        //
        String cacheKey = STATUS_ORDER_CACHE + orderno;
        CacheManager.getInstance().setString(cacheKey, FastJsonHelper.jsonEncode(statusData), DEF_EXPIRES);
        return statusData;
    }

    public JSONObject getOrderStatus(String username)
    {
        String userCacheKey = STATUS_USER_CACHE + username;
        String orderno = CacheManager.getInstance().getString(userCacheKey);
        if(StringUtils.isEmpty(orderno))
        {
            return null;
        }

        String cacheKey = STATUS_ORDER_CACHE + orderno;
        return CacheManager.getInstance().getObject(cacheKey, JSONObject.class);
    }

    public static JSONObject getOrderStatusByOrderNo(String orderno)
    {
        String cacheKey = STATUS_ORDER_CACHE + orderno;
        return CacheManager.getInstance().getObject(cacheKey, JSONObject.class);
    }

    private void createAndSaveUserStatus(String username, OrderTxStatus txStatus, String orderno)
    {
        String userCacheKey = STATUS_USER_CACHE + username;
        if(txStatus == OrderTxStatus.NEW)
        {
            CacheManager.getInstance().setString(userCacheKey, orderno, DEF_EXPIRES);
        }
        else if(txStatus == OrderTxStatus.FAILED || txStatus == OrderTxStatus.REALIZED)
        {
            CacheManager.getInstance().delete(userCacheKey);
        }
    }


    private String encryptData(String orderno, String username)
    {
        return MD5.encode(orderno + SALT + username);
    }

    private float refreshAndGetProfitRate()
    {
        long ts = System.currentTimeMillis();
        if(this.mLastRefreshTime > 0 && ts - this.mLastRefreshTime <= 60_000)
        {
            return this.mPlatformProfitValue;
        }

        float value = mConfigService.getFloat(false, "game_football:open_rate");
        if(value >= 0)
        {
            this.mPlatformProfitValue =  value * 100.0f;
            this.mLastRefreshTime = ts;
        }
        return this.mPlatformProfitValue;
    }

    public static void main(String[] args) {

        FootballHandleManager mgr = new FootballHandleManager();

        mgr.handleToBet(false, null, "u1", null);

    }

}

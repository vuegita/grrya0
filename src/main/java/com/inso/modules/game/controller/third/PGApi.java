package com.inso.modules.game.controller.third;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.RateLimiter;
import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.MD5;
import com.inso.framework.utils.RegexUtils;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.WhiteIPManager;
import com.inso.modules.common.model.*;
import com.inso.modules.game.GameChildType;
import com.inso.modules.game.lottery_game_impl.NewLotteryBetTaskManager;
import com.inso.modules.game.lottery_game_impl.pg.PGErrorResult;
import com.inso.modules.game.lottery_game_impl.pg.PgSoftManager;
import com.inso.modules.game.lottery_game_impl.pg.model.PgGameType;
import com.inso.modules.game.model.GameInfo;
import com.inso.modules.game.model.NewLotteryOrderInfo;
import com.inso.modules.game.service.GameService;
import com.inso.modules.game.service.NewLotteryOrderService;
import com.inso.modules.passport.UserErrorResult;
import com.inso.modules.passport.money.model.UserMoney;
import com.inso.modules.passport.money.service.UserMoneyService;
import com.inso.modules.passport.user.limit.MyLoginRequired;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.AuthService;
import com.inso.modules.passport.user.service.UserService;
import com.inso.modules.paychannel.logical.FiatExchangeManager;
import com.inso.modules.web.SystemRunningMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/game/pgApi")
public class PGApi {

    private static Log LOG = LogFactory.getLog(PGApi.class);

    @Autowired
    private UserService mUserService;

    @Autowired
    private UserMoneyService moneyService;

    @Autowired
    private AuthService mAuthService;

    @Autowired
    private UserMoneyService mUserMoneyService;

    @Autowired
    private NewLotteryBetTaskManager newLotteryBetTaskManager;

    @Autowired
    private NewLotteryOrderService newLotteryOrderService;

    @Autowired
    private GameService mGameService;

    @Autowired
    private FiatExchangeManager mFiatExchangeManager;

    private RateLimiter mRateLimit = RateLimiter.create(50);

    // 验证PG-IP
    private String[] mPGIPWhiteArr = {"18.229.45.156", "54.233.168.28", "54.233.193.192"};

    private static String mIgnoreTestAccount = "gametest";


    private ICurrencyType mSystemCUrrency = ICurrencyType.getSupportCurrency();
    private ICurrencyType mTargetCurrency = FiatCurrencyType.BRL;

    @RequestMapping("/verifySession")
    public String verifySession()
    {
        String playerSessionToken = WebRequest.getString("operator_player_session");
        String accessToken = PgSoftManager.getInstance().getUser_2_PGSessionToken(playerSessionToken);

        Map<String, Object> apiTemplate = Maps.newHashMap();
        if(!verifyPGSafeParams(apiTemplate, accessToken))
        {
            return FastJsonHelper.jsonEncode(apiTemplate);
        }

        String username = mAuthService.getAccountByAccessToken(accessToken);
        String player_name = "PG_" + MD5.encode(username);

        Map<String, Object> dataMaps = Maps.newHashMap();
        dataMaps.put("player_name", player_name);
        dataMaps.put("currency", mTargetCurrency.getKey());
        return buildPGResultBody(apiTemplate, SystemErrorResult.SUCCESS, dataMaps);
    }

    @RequestMapping("/getCashInfo")
    public String getCashInfo()
    {
        String playerSessionToken = WebRequest.getString("operator_player_session");
        String accessToken = PgSoftManager.getInstance().getUser_2_PGSessionToken(playerSessionToken);

        Map<String, Object> apiTemplate = Maps.newHashMap();
        if(!verifyPGSafeParams(apiTemplate, accessToken))
        {
            return FastJsonHelper.jsonEncode(apiTemplate);
        }

        String username = mAuthService.getAccountByAccessToken(accessToken);
        UserInfo userInfo = mUserService.findByUsername(false, username);

        FundAccountType accountType = FundAccountType.Spot;
        ICurrencyType currencyType = ICurrencyType.getSupportCurrency();
        UserMoney userMoney = mUserMoneyService.findMoney(false, userInfo.getId(), accountType, currencyType);

        BigDecimal validBalance = userMoney.getValidBalance();
        BigDecimal brlBalance = convertAmount(validBalance, currencyType, mTargetCurrency);

        UserInfo.UserType userType = UserInfo.UserType.getType(userInfo.getType());
        if(userType == UserInfo.UserType.MEMBER || userInfo.getName().startsWith(mIgnoreTestAccount))
        {

        } else {
            // 测试账号余额为0
            brlBalance = BigDecimal.ZERO;
        }

        Map<String, Object> dataMaps = Maps.newHashMap();
        dataMaps.put("currency_code", mTargetCurrency.getKey());
        dataMaps.put("balance_amount", brlBalance);
        dataMaps.put("updated_time", System.currentTimeMillis());
        return buildPGResultBody(apiTemplate, SystemErrorResult.SUCCESS, dataMaps);
    }

    @RequestMapping("/transferinout")
    public String transferinout()
    {
        String playerSessionToken = WebRequest.getString("operator_player_session");
        String accessToken = PgSoftManager.getInstance().getUser_2_PGSessionToken(playerSessionToken);

        Map<String, Object> apiTemplate = Maps.newHashMap();
        if(!verifyPGSafeParams(apiTemplate, accessToken))
        {
            return FastJsonHelper.jsonEncode(apiTemplate);
        }

        String remoteip = WebRequest.getRemoteIP();
        PgGameType gameType = PgGameType.getTypeByExternal(WebRequest.getInt("game_id"));
        BigDecimal bet_amount = WebRequest.getBigDecimal("bet_amount");
        BigDecimal win_amount = WebRequest.getBigDecimal("win_amount");
        BigDecimal transfer_amount = WebRequest.getBigDecimal("transfer_amount"); // 负数：扣除余额 | 正数：增加余额
        String transaction_id = WebRequest.getString("transaction_id");
        String wallet_type = WebRequest.getString("wallet_type"); // C=现金 | B=红利 | G=免费游戏
        int bet_type = WebRequest.getInt("bet_type"); // 1：真实游戏

        LOG.info("trasaction id = " + transaction_id + ", betAmount = " + bet_amount + ", winAmount = " + win_amount + ", walletType = " + wallet_type + ", bet_type = " + bet_type + ", remoteip = " + remoteip);

        if(!mRateLimit.tryAcquire())
        {
            return buildPGResultBody(apiTemplate, PGErrorResult.ERR_INVALID_Request, null);
        }

        if(gameType == null || bet_type != 1)
        {
            return buildPGResultBody(apiTemplate, PGErrorResult.ERR_INVALID_Request, null);
        }

        if(transfer_amount == null || win_amount == null || bet_amount == null)
        {
            return buildPGResultBody(apiTemplate, PGErrorResult.ERR_INVALID_Request, null);
        }

        if(StringUtils.isEmpty(transaction_id) || !RegexUtils.isLetterOrDigitOrDividerLine(transaction_id))
        {
            return buildPGResultBody(apiTemplate, PGErrorResult.ERR_INVALID_Request, null);
        }

        if(!"C".equalsIgnoreCase(wallet_type))
        {
            return buildPGResultBody(apiTemplate, PGErrorResult.ERR_ACTION_FAILED, null);
        }


        String username = mAuthService.getAccountByAccessToken(accessToken);
        UserInfo userInfo = mUserService.findByUsername(false, username);

        UserInfo.UserType userType = UserInfo.UserType.getType(userInfo.getType());
        if(userType == UserInfo.UserType.MEMBER || userInfo.getName().startsWith(mIgnoreTestAccount))
        {

        } else {
            return buildPGResultBody(apiTemplate, PGErrorResult.ERR_NotEnoughBalanceException, null);
        }

        if(bet_amount.compareTo(BigDecimal.ZERO) == 0 && win_amount.compareTo(BigDecimal.ZERO) == 0)
        {
            // 都为0表示失败
            return buildPGTransferResult(userInfo, apiTemplate);
        }

        bet_amount = convertAmount(bet_amount, mTargetCurrency, mSystemCUrrency);
        win_amount = convertAmount(win_amount, mTargetCurrency, mSystemCUrrency);


        // 执行操作
        ErrorResult result = null;

        // 验证是否存在
        NewLotteryOrderInfo orderInfo = newLotteryOrderService.findByNo(gameType, transaction_id);
        OrderTxStatus txStatus = orderInfo != null ? OrderTxStatus.getType(orderInfo.getStatus()) : null;
        if(orderInfo != null)
        {
            if(txStatus == OrderTxStatus.REALIZED || txStatus == OrderTxStatus.FAILED)
            {
                return buildPGTransferResult(userInfo, apiTemplate);
            }
        }
        else
        {
            txStatus = OrderTxStatus.NEW;
        }

        // 下注扣款
        if(txStatus == OrderTxStatus.NEW || txStatus == OrderTxStatus.PENDING)
        {
            // 免费游戏中奖 有等于bet_amount=0
            if(bet_amount != null && bet_amount.compareTo(BigDecimal.ZERO) >= 0)
            {
                String beItem = StringUtils.getEmpty();
                result = newLotteryBetTaskManager.doCreateOrder(transaction_id, orderInfo, null, gameType, null, userInfo, bet_amount, 1, bet_amount, beItem, null);
            }
            if(result != SystemErrorResult.SUCCESS)
            {
                return buildPGResultBody(apiTemplate, PGErrorResult.ERR_ACTION_FAILED, null);
            }

            txStatus = OrderTxStatus.WAITING;
        }

        // 结算, win_amount 为0
        if(txStatus == OrderTxStatus.WAITING)
        {
            result = newLotteryBetTaskManager.settleExternalOrder(transaction_id, gameType, userInfo, win_amount);
        }

        if(result != SystemErrorResult.SUCCESS)
        {
            return buildPGResultBody(apiTemplate, PGErrorResult.ERR_ACTION_FAILED, null);
        }

        // 成功
        return buildPGTransferResult(userInfo, apiTemplate);
    }


    @MyLoginRequired
    @RequestMapping("getPGGameAccessUrl")
    public String getPGGameAccessUrl()
    {
        // https://m.pg-demo.com/1/index.html?ot=abcd&ops=12345-abcd-1234-abcd-12345&btt=1
        String accessToken = WebRequest.getAccessToken();
        PgGameType gameType = PgGameType.getType(WebRequest.getString("lotteryType"));
        if(gameType == null)
        {
            gameType = PgGameType.getTypeByExternal(WebRequest.getInt("game_id"));
        }

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(gameType == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        GameInfo gameInfo = mGameService.findByKey(false, gameType.getKey());
        if(!Status.ENABLE.getKey().equalsIgnoreCase(gameInfo.getStatus()))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_BUSY);
            return apiJsonTemplate.toJSONString();
        }

        String username = mAuthService.getAccountByAccessToken(accessToken);
        UserInfo userInfo = mUserService.findByUsername(false, username);
        FundAccountType accountType = FundAccountType.Spot;
        UserMoney userMoney = mUserMoneyService.findMoney(false, userInfo.getId(), accountType, mSystemCUrrency);
        BigDecimal validBalance = userMoney.getValidBalance();
        if(validBalance.compareTo(BigDecimal.ONE) < 0)
        {
            apiJsonTemplate.setJsonResult(UserErrorResult.ERR_PAY_NOT_ENOUGH_BALANCE);
            return apiJsonTemplate.toJSONString();
        }

        StringBuilder url = new StringBuilder("https://www.pgpublic.com/pggame/index/createuser/");
        url.append(gameType.getCode());
        url.append("/index.html?btt=1");
        url.append("&ot=").append(PgSoftManager.getInstance().getPGToken());
        url.append("&ops=").append(PgSoftManager.getInstance().createUser_2_PGSessionToken(userInfo, accessToken));

        apiJsonTemplate.setData(url.toString());
        return apiJsonTemplate.toJSONString();
    }

    private boolean verifyPGSafeParams(Map<String, Object> apiTemplate, String accessToken)
    {
        String remoteIP = WebRequest.getRemoteIP();

        boolean verifyIP = false;
        for(String tmpIP : mPGIPWhiteArr)
        {
            if(tmpIP.equalsIgnoreCase(remoteIP))
            {
                verifyIP = true;
                break;
            }
        }
        if(!verifyIP)
        {
            verifyIP = WhiteIPManager.getInstance().verify(remoteIP);
        }

        if(!verifyIP)
        {
            LOG.warn("Forbidden PG IP => " + remoteIP);
            buildPGMainBody(apiTemplate, PGErrorResult.ERR_INVALID_Request, null);
            return false;
        }

        String operator_token = WebRequest.getString("operator_token");
        if(StringUtils.isEmpty(operator_token))
        {
            LOG.error("Error ops token ");
            buildPGMainBody(apiTemplate, PGErrorResult.ERR_INVALID_Request, null);
            return false;
        }

        String secret_key = WebRequest.getString("secret_key");
        if(StringUtils.isEmpty(secret_key))
        {
            LOG.error("Error secret_key ");
            buildPGMainBody(apiTemplate, PGErrorResult.ERR_INVALID_Request, null);
            return false;
        }

        if(!PgSoftManager.getInstance().verifyPGToken(operator_token, secret_key))
        {
            LOG.error("Invalid operator_token ");
            buildPGMainBody(apiTemplate, PGErrorResult.ERR_INVALID_Request, null);
            return false;
        }

        if(StringUtils.isEmpty(accessToken))
        {
            LOG.error("Fetch and Invalid accessToken ... ");
            buildPGResultBody(apiTemplate, PGErrorResult.ERR_INVALID_Request, null);
            return false;
        }

        if(!SystemRunningMode.isBCMode())
        {
            buildPGResultBody(apiTemplate, PGErrorResult.ERR_INVALID_Request, null);
            return false;
        }

        return true;
    }

    private void buildPGMainBody(Map<String, Object> maps, ErrorResult result, Object value)
    {
        if(maps == null)
        {
            maps = Maps.newHashMap();
        }
        if(SystemErrorResult.SUCCESS == result)
        {
            maps.put("data", value);
            maps.put("error", null);
        }
        else
        {
            Map<String, Object> error = Maps.newHashMap();
            error.put("code", result.getCode());
            error.put("message", result.getCode());
            maps.put("error", error);
            maps.put("data", null);
        }
    }

    private String buildPGResultBody(Map<String, Object> maps, ErrorResult result, Object value)
    {
        buildPGMainBody(maps, result, value);
        return FastJsonHelper.jsonEncode(maps);
    }

    private String buildPGTransferResult(UserInfo userInfo, Map<String, Object> apiTemplate)
    {
        FundAccountType accountType = FundAccountType.Spot;
        ICurrencyType currencyType = ICurrencyType.getSupportCurrency();
        UserMoney userMoney = mUserMoneyService.findMoney(false, userInfo.getId(), accountType, currencyType);


        BigDecimal validBalance = userMoney.getValidBalance();
        BigDecimal brlBalance = convertAmount(validBalance, currencyType, mTargetCurrency);

        Map<String, Object> dataMaps = Maps.newHashMap();
        dataMaps.put("currency_code", mTargetCurrency.getKey());
        dataMaps.put("balance_amount", brlBalance);
        dataMaps.put("updated_time", System.currentTimeMillis());
        return buildPGResultBody(apiTemplate, SystemErrorResult.SUCCESS, dataMaps);
    }

    private BigDecimal convertAmount(BigDecimal srcAmount, ICurrencyType srcCurrency, ICurrencyType targetCurrency)
    {
        BigDecimal target = mFiatExchangeManager.doConvert(srcCurrency, targetCurrency, srcAmount);
        return target;
    }

}

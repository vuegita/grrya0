package com.inso.modules.ad.core.controller;

import com.google.common.collect.Maps;
import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.PageVo;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.context.MyEnvironment;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.CollectionUtils;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.ad.AdErrorResult;
import com.inso.modules.ad.MallErrorResult;
import com.inso.modules.ad.core.config.ThirdIdConfig;
import com.inso.modules.ad.core.logical.AdOrderManager;
import com.inso.modules.ad.core.model.*;
import com.inso.modules.ad.core.service.CategoryService;
import com.inso.modules.ad.core.service.EventOrderService;
import com.inso.modules.ad.core.service.MaterielService;
import com.inso.modules.ad.core.service.VipLimitService;
import com.inso.modules.ad.mall.model.MallBuyerAddrInfo;
import com.inso.modules.ad.mall.model.MallCommodityInfo;
import com.inso.modules.ad.mall.service.MallBuyerAddrService;
import com.inso.modules.ad.mall.service.MallCommodityService;
import com.inso.modules.admin.config.PlatformConfig;
import com.inso.modules.common.helper.RequestTokenHelper;
import com.inso.modules.common.model.FundAccountType;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.UserErrorResult;
import com.inso.modules.passport.money.model.UserMoney;
import com.inso.modules.passport.money.service.UserMoneyService;
import com.inso.modules.passport.user.limit.MyLoginRequired;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.model.UserVIPInfo;
import com.inso.modules.passport.user.service.AuthService;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.user.service.UserService;
import com.inso.modules.passport.user.service.UserVIPService;
import com.inso.modules.web.SystemRunningMode;
import com.inso.modules.web.logical.SystemStatusManager;
import com.inso.modules.web.model.VIPInfo;
import com.inso.modules.web.model.VIPType;
import com.inso.modules.web.service.ConfigService;
import com.inso.modules.web.service.VIPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/app/appApi")
public class Api {

    @Autowired
    private UserService mUserService;

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private UserMoneyService moneyService;

    @Autowired
    private AuthService mAuthService;

    @Autowired
    private UserVIPService mUserVIPService;

    @Autowired
    private EventOrderService mEventOrderService;

    @Autowired
    private MaterielService materielService;

    @Autowired
    private VipLimitService mVipLimitService;

    @Autowired
    private VIPService mVIPService;

    @Autowired
    private CategoryService mCategoryService;

    @Autowired
    private ConfigService mConfigService;

    @Autowired
    private AdOrderManager mAdOrderManager;

    @Autowired
    private MallBuyerAddrService mallBuyerAddrService;

    @Autowired
    private MallCommodityService mallCommodityService;

    private SystemRunningMode mRunningMode = SystemRunningMode.getSystemConfig();

    /**
     * @api {post} /web/api/getTaskRecordBystatus
     * @apiDescription 获取根据状态查询用户做过的任务
     * @apiName getVIPList
     * @apiGroup AdCoreApi
     * @apiVersion 1.0.0
     *
     * @apiParam {string}  type => ad
     *
     * @apiSuccess  {string}  code    错误码
     * @apiSuccess  {string}  msg   错误信息
     *
     * @apiSuccessExample {json} Success-Response:
     *       {
     *         "code": 200,
     *         "msg": "success",
     *       }
     */
    @MyLoginRequired
    @RequestMapping("/getTaskRecordBystatus")
    public String getTaskRecordBystatus()
    {

        String accessToken = WebRequest.getAccessToken();
        String username = mAuthService.getAccountByAccessToken(accessToken);
        UserInfo userInfo = mUserService.findByUsername(false, username);

        String txStatusString = WebRequest.getString("status");
        OrderTxStatus txStatus = OrderTxStatus.getType(txStatusString);

        PageVo pageVo = new PageVo(WebRequest.getInt("offset"),10);

        ApiJsonTemplate template = new ApiJsonTemplate();
        List<AdEventOrderInfo> rsList = mEventOrderService.queryByUserAndTxStatus(false, userInfo.getId(),txStatus, pageVo);
        if(!CollectionUtils.isEmpty(rsList))
        {
            List list = new ArrayList(rsList.size());

            String keyUsername = "username";
            String keyno = "no";
            String keyAmount = "amount";
            String keyMaterielId = "materielId";
            String keyMaterielName = "materielName";
            String keyMaterielDesc = "materielDesc";
            String keyMaterielThumb = "materielThumb";
            String keyStatus = "status";
            String keyMaterielPrice = "materielPrice";
            String keyMaterielCategoryid = "materielCategoryid";
            String keyCreatetime = "createtime";


            for(AdEventOrderInfo madEventOrderInfo : rsList)
            {

                Map<String, Object> maps = Maps.newHashMap();
                maps.put(keyUsername, madEventOrderInfo.getUsername());
                maps.put(keyno, madEventOrderInfo.getNo());
                maps.put(keyAmount, madEventOrderInfo.getAmount());
                maps.put(keyMaterielId, madEventOrderInfo.getMaterielId());
                maps.put(keyMaterielName, madEventOrderInfo.getMaterielName());
                maps.put(keyMaterielDesc, madEventOrderInfo.getMaterielDesc());
                maps.put(keyMaterielThumb, madEventOrderInfo.getMaterielThumb());
                maps.put(keyStatus, madEventOrderInfo.getStatus());
                maps.put(keyMaterielPrice, madEventOrderInfo.getMaterielPrice());
                maps.put(keyMaterielCategoryid, madEventOrderInfo.getMaterielCategoryid());
                maps.put(keyCreatetime, madEventOrderInfo.getCreatetime());


                list.add(maps);
            }

            template.setData(list);
        }
        else
        {
            template.setData(Collections.emptyList());
        }

        return template.toJSONString();
    }

    /**
     * @api {post} /web/api/getVIPList
     * @apiDescription  获取用户反馈信息
     * @apiName getVIPList
     * @apiGroup MallApi
     * @apiVersion 1.0.0
     *
     * @apiParam {String}  type => ad
     *
     * @apiSuccess  {string}  code    错误码
     * @apiSuccess  {string}  msg   错误信息
     *
     * @apiSuccessExample {json} Success-Response:
     *       {
     *         "code": 200,
     *         "msg": "success",
     *       }
     */
    @MyLoginRequired
    @RequestMapping("/getVIPLimitList")
    public String getVIPLimitList()
    {
//        String typeString = WebRequest.getString("type");
//        VIPType vipType = VIPType.getType(typeString);
        ApiJsonTemplate template = new ApiJsonTemplate();
        if(mRunningMode != SystemRunningMode.FUNDS)
        {
            template.setJsonResult(SystemErrorResult.ERR_SYS_BUSY);
            return template.toJSONString();
        }
        List<AdVipLimitInfo> list = mVipLimitService.queryAllEnable(false, VIPType.AD);
        template.setData(list);
        return template.toJSONString();
    }

    /**
     * 获取分类列表
     * @return
     */
    @RequestMapping("/getCategoryList")
    public String getCategoryList()
    {
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
        if(mRunningMode != SystemRunningMode.FUNDS)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_BUSY);
            return apiJsonTemplate.toJSONString();
        }
        List<AdCategoryInfo> rsList = mCategoryService.queryAllEnable(false);
        apiJsonTemplate.setData(rsList);
        return apiJsonTemplate.toJSONString();
    }

    /**
     * 获取物料详情
     * @return
     */
    @RequestMapping("/getAdMaterielInfo")
    public String getAdMaterielInfo()
    {
        long materielid = WebRequest.getLong("materielid");
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(mRunningMode != SystemRunningMode.FUNDS)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_BUSY);
            return apiJsonTemplate.toJSONString();
        }

        if(materielid <= 0)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        AdMaterielInfo materielInfo = materielService.findById(MyEnvironment.isDev(), materielid);
        if(materielInfo != null)
        {
            materielInfo.clearNotUse();
        }

        apiJsonTemplate.setData(materielInfo);
        return apiJsonTemplate.toJSONString();
    }

    @RequestMapping("/getAdMaterielDetailInfo")
    public String getAdMaterielDetailInfo()
    {
        long materielid = WebRequest.getLong("materielid");
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(mRunningMode != SystemRunningMode.FUNDS)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_BUSY);
            return apiJsonTemplate.toJSONString();
        }

        if(materielid <= 0)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        AdMaterielInfo materielInfo = materielService.findById(MyEnvironment.isDev(), materielid);
        if(materielInfo != null)
        {
            materielInfo.clearNotUse();
        }

        AdEventType eventType = AdEventType.getType(materielInfo.getEventType());
        if(eventType != AdEventType.SHOP)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
            return apiJsonTemplate.toJSONString();
        }

        AdMaterielDetailInfo materielDetailInfo = materielService.findDetailById(false, materielid);
        if(materielDetailInfo == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_NODATA);
            return apiJsonTemplate.toJSONString();
        }

        apiJsonTemplate.setData(materielDetailInfo);
        return apiJsonTemplate.toJSONString();
    }

    /**
     * 根据分类获取物料列表
     * @return
     */
    @RequestMapping("/getMaterielList")
    public String getMaterielList()
    {
        long categoryid = WebRequest.getLong("categoryid");
        long minPrice = WebRequest.getLong("minPrice");
        long maxPrice = WebRequest.getLong("maxPrice");
        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), 10);

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
        if(mRunningMode != SystemRunningMode.FUNDS)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_BUSY);
            return apiJsonTemplate.toJSONString();
        }

        List<AdMaterielInfo> rsList = materielService.queryByCategory(false, categoryid, pageVo,minPrice,maxPrice);
        if(!CollectionUtils.isEmpty(rsList))
        {
            List list = new ArrayList(rsList.size());

            String keyId = "id";
            String keyCategoryid = "categoryid";
            String keyName = "name";
            String keyDesc = "desc";
            String keyThumb = "thumb";
            String keyJumpUrl = "jumpUrl";
            String keyPrice = "price";
            String keyEventType = "eventType";

            for(AdMaterielInfo materielInfo : rsList)
            {
                // 推广码，假装和google合作
                String code = ThirdIdConfig.signTime(materielInfo.getKey());
                materielInfo.addPromotionCodeToJumpUrl(code);

                Map<String, Object> maps = Maps.newHashMap();
                maps.put(keyId, materielInfo.getId());
                maps.put(keyCategoryid, materielInfo.getCategoryid());
                maps.put(keyName, materielInfo.getName());
                maps.put(keyDesc, materielInfo.getDesc());
                maps.put(keyThumb, materielInfo.getThumb());
                maps.put(keyJumpUrl, materielInfo.getJumpUrl());
                maps.put(keyPrice, materielInfo.getPrice());
                maps.put(keyEventType, materielInfo.getId());

                list.add(maps);
            }

            apiJsonTemplate.setData(list);
        }
        else
        {
            apiJsonTemplate.setData(Collections.emptyList());
        }

        return apiJsonTemplate.toJSONString();
    }


    /**
     * 获取会员已做过的物料ID列表, 用于前端判断
     * @return
     */
    @MyLoginRequired
    @RequestMapping("/getUserLatestHistoryMaterielidList")
    public String getUserLatestHistoryMaterielidList()
    {
        String accessToken = WebRequest.getAccessToken();
        String username = mAuthService.getAccountByAccessToken(accessToken);
        UserInfo userInfo = mUserService.findByUsername(false, username);

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
        if(mRunningMode != SystemRunningMode.FUNDS)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_BUSY);
            return apiJsonTemplate.toJSONString();
        }
        List<Long> rsList = mEventOrderService.queryLatestMaterielIds(false, userInfo.getId());
        apiJsonTemplate.setData(rsList);
        return apiJsonTemplate.toJSONString();
    }


    @MyLoginRequired
    @RequestMapping("/submitOrder")
    public String submitOrder()
    {
        String accessToken = WebRequest.getAccessToken();
        String username = mAuthService.getAccountByAccessToken(accessToken);

        long materielid = WebRequest.getLong("materielid");

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(!SystemStatusManager.getInstance().isRunning())
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_MAINTAINED);
            return apiJsonTemplate.toJSONString();
        }

        if(mRunningMode != SystemRunningMode.FUNDS)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_BUSY);
            return apiJsonTemplate.toJSONString();
        }

        if(!RequestTokenHelper.verifyGame(username))
        {
            // 并发限制
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_REQUESTS);
            return apiJsonTemplate.toJSONString();
        }

        if(materielid <= 0)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        // 物料
        AdMaterielInfo materielInfo = materielService.findById(false, materielid);
        if(materielInfo == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
            return apiJsonTemplate.toJSONString();
        }

        UserInfo userInfo = mUserService.findByUsername(false, username);

        AdEventType eventType = AdEventType.getType(materielInfo.getEventType());
        if(eventType == AdEventType.SHOP)
        {
            // 商城模块
            ErrorResult result = handleBuyShop(materielInfo, userInfo);
            apiJsonTemplate.setJsonResult(result);
            return apiJsonTemplate.toJSONString();
        }

        // 历史订单
        AdEventOrderInfo orderInfo = mEventOrderService.findLatestOrderInfo(false, userInfo.getId(), materielInfo.getId());
        if(orderInfo != null)
        {
            // 最近1个月不能重复下载
            apiJsonTemplate.setJsonResult(AdErrorResult.ERR_EXISTS_ORDER_RECORD);
            return apiJsonTemplate.toJSONString();
        }

        UserVIPInfo userVIPInfo = mUserVIPService.findByUserId(false, userInfo.getId(), VIPType.AD);
        if(userVIPInfo == null || userVIPInfo.verifyExpires())
        {
            // 无效vip或已过期
            apiJsonTemplate.setJsonResult(UserErrorResult.ERR_INVALID_VIP);
            return apiJsonTemplate.toJSONString();
        }

        AdVipLimitInfo limitInfo = mVipLimitService.findByVipId(false, userVIPInfo.getVipid());

        // 加锁-防止并发
        ErrorResult errorResult = null;
        // 一: 未开通vip
        if(!userVIPInfo.verifyBuyVIP())
        {
            // 1. 读取配置vip0每天下载app是否开启
            boolean switchValue = mConfigService.getBoolean(false, PlatformConfig.ADMIN_PLATFORM_CONFIG_AD_VIP0_DAILY_DOWNLOAD_APP_SWITCH);
            if(switchValue){
                errorResult = handleMakeTaskForVIP_UP(materielInfo, userInfo, limitInfo);
            }else{
                errorResult = handleMakeTaskForVIP_0(materielInfo, userInfo, limitInfo);

            }

            apiJsonTemplate.setJsonResult(errorResult);
            return apiJsonTemplate.toJSONString();
        }

        VIPInfo vipInfo = mVIPService.findById(false, userVIPInfo.getVipid());
        Status vipStatus = Status.getType(vipInfo.getStatus());
        if(vipStatus != Status.ENABLE)
        {
            // vip 已经下线
            apiJsonTemplate.setJsonResult(UserErrorResult.ERR_INVALID_VIP);
            return apiJsonTemplate.toJSONString();
        }

        // 二：开通VIP inviteCountOfDay
        errorResult = handleMakeTaskForVIP_UP(materielInfo, userInfo, limitInfo);

        apiJsonTemplate.setJsonResult(errorResult);

        return apiJsonTemplate.toJSONString();
    }

    /**
     * 没有VIP会员
     * 无会员最多只能得到60， 第一个任务免费做，有金额限制, 再邀请5个好友赚50(查询最近3个月所有订单)
     * @param materielInfo
     * @param userInfo
     */
    private ErrorResult handleMakeTaskForVIP_0(AdMaterielInfo materielInfo, UserInfo userInfo, AdVipLimitInfo limitInfo)
    {
        long userid = userInfo.getId();

        // 1. 计算历史做了多少任务，然后金额再汇总
        BigDecimal totalMakeMoney = mEventOrderService.findAllHistoryAmountByUser(false, userid);

        // 2.
        MakeTaskStatus status = MakeTaskStatus.loadCache(userid);
        status.updateConfig(limitInfo);
        // VIP为0的，历史总汇总都要算进去
        status.setUserTotalMoneyOfDay(totalMakeMoney);
        if(limitInfo.getFreeMoneyOfDay().compareTo(totalMakeMoney) >= 0)
        {
            status.setUserFreeMoneyOfDay(totalMakeMoney);
        }
        else
        {
            status.setUserFreeMoneyOfDay(limitInfo.getFreeMoneyOfDay());
        }

        ErrorResult errorResult = status.verify(materielInfo.getPrice());
        if(errorResult == SystemErrorResult.SUCCESS)
        {
            UserAttr userAttr = mUserAttrService.find(false, userid);
            mEventOrderService.createOrder(materielInfo, userAttr);
            status.increAmount(materielInfo.getPrice());
            status.saveCache();
        }

        return errorResult;
    }

    /**
     * 购买VIP的会员
     * @param materielInfo
     * @param userInfo
     */
    private ErrorResult handleMakeTaskForVIP_UP(AdMaterielInfo materielInfo, UserInfo userInfo, AdVipLimitInfo limitInfo)
    {
        long userid = userInfo.getId();

        MakeTaskStatus status = MakeTaskStatus.loadCache(userid);
        status.updateConfig(limitInfo);

        ErrorResult errorResult = status.verify(materielInfo.getPrice());
        if(errorResult == SystemErrorResult.SUCCESS)
        {
            UserAttr userAttr = mUserAttrService.find(false, userid);
            mEventOrderService.createOrder(materielInfo, userAttr);
            status.increAmount(materielInfo.getPrice());
            status.saveCache();
        }

        return errorResult;
    }

    private ErrorResult handleBuyShop(AdMaterielInfo materielInfo, UserInfo userInfo)
    {
        long quantity = WebRequest.getLong("quantity");
        String shopSize = WebRequest.getString("shopSize");

        if(quantity <= 0)
        {
            return SystemErrorResult.ERR_PARAMS;
        }

        MallBuyerAddrInfo buyerAddrInfo = mallBuyerAddrService.findUserid(false, userInfo.getId());
        if(buyerAddrInfo == null)
        {
            return MallErrorResult.NOT_EXIST_BUYER_ADDRESS;
        }

        // mAdOrderManager
        long commodityid = WebRequest.getLong("commodityid");
        if(commodityid <= 0)
        {
            return SystemErrorResult.ERR_PARAMS;
        }

        MallCommodityInfo entityInfo = mallCommodityService.findById(false, commodityid);
        if(entityInfo == null)
        {
            return SystemErrorResult.ERR_PARAMS;
        }

        if(entityInfo.getMaterielid() != materielInfo.getId())
        {
            return SystemErrorResult.ERR_SYS_OPT_FORBID;
        }

        AdMaterielDetailInfo materielDetailInfo = materielService.findDetailById(false, materielInfo.getId());
        if(StringUtils.isEmpty(materielDetailInfo.getSizes()))
        {
            shopSize = null;
        }
        else if(!StringUtils.isEmpty(shopSize) && materielDetailInfo.getSizes().contains(shopSize))
        {

        }
        else {
            return SystemErrorResult.ERR_SYS_OPT_FORBID;
        }

        BigDecimal totalAmount = new BigDecimal(quantity).multiply(materielInfo.getPrice());
        UserMoney userMoney = moneyService.findMoney(false, userInfo.getId(), FundAccountType.Spot, ICurrencyType.getSupportCurrency());
        if(!userMoney.verify(totalAmount))
        {
            return UserErrorResult.ERR_PAY_NOT_ENOUGH_BALANCE;
        }
        return mAdOrderManager.buyShop(userInfo, materielInfo, entityInfo, quantity, buyerAddrInfo, shopSize);
    }



}


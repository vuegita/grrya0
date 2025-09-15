package com.inso.modules.coin.cloud_mining.controller;

import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.PageVo;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.CollectionUtils;
import com.inso.framework.utils.NumberEncryptUtils;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.coin.cloud_mining.cache.CloudRecordCacleKeyHelper;
import com.inso.modules.coin.cloud_mining.logical.CloudRecordManager;
import com.inso.modules.coin.cloud_mining.logical.CloudStatsManager;
import com.inso.modules.coin.cloud_mining.model.CloudOrderInfo;
import com.inso.modules.coin.cloud_mining.model.CloudProductType;
import com.inso.modules.coin.cloud_mining.model.CloudProfitConfigInfo;
import com.inso.modules.coin.cloud_mining.model.CloudRecordInfo;
import com.inso.modules.coin.cloud_mining.service.CloudOrderService;
import com.inso.modules.coin.cloud_mining.service.CloudProfitConfigService;
import com.inso.modules.coin.cloud_mining.service.CloudRecordService;
import com.inso.modules.common.helper.RequestTokenHelper;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.passport.money.PayApiManager;
import com.inso.modules.passport.user.limit.MyLoginRequired;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.AuthService;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.user.service.UserService;
import com.inso.modules.web.team.model.TeamBusinessType;
import com.inso.modules.web.team.model.TeamBuyRecordInfo;
import com.inso.modules.web.team.service.TeamBuyRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/coin/cloudMiningApi")
public class Api {

    @Autowired
    private AuthService mAuthService;

    @Autowired
    private UserService mUserService;

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private CloudOrderService mOrderService;

    @Autowired
    private CloudProfitConfigService profitConfigService;

    @Autowired
    private CloudRecordManager mRecordMgr;

    @Autowired
    private CloudRecordService mRecordService;


    @Autowired
    private PayApiManager mPayApiManager;

    @Autowired
    private CloudOrderService mCloudOrderService;

    @Autowired
    private TeamBuyRecordService mTeamBuyRecordService;

    @RequestMapping("/getCloudStatsInfo")
    public String getCloudStatsInfo()
    {
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
        apiJsonTemplate.setData(CloudStatsManager.getInstance().getStatsInfo());
        return apiJsonTemplate.toJSONString();
    }

    /**
     * 定期收益配置列表
     * @return
     */
    @RequestMapping("/getProfitConfigList")
    public String getProfitConfigList()
    {
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        List<CloudProfitConfigInfo> rsList = profitConfigService.queryAllList(false);
        if(CollectionUtils.isEmpty(rsList))
        {
            rsList = Collections.emptyList();
        }
        apiJsonTemplate.setData(rsList);
        return apiJsonTemplate.toJSONString();
    }

    @MyLoginRequired
    @RequestMapping("/getCloudRecordInfoList")
    public String getCloudRecordInfoList()
    {
        String accessToken = WebRequest.getAccessToken();
        String username = mAuthService.getAccountByAccessToken(accessToken);

        CloudProductType productType = CloudProductType.getType(WebRequest.getString("productType"));
        long days = WebRequest.getLong("days");
        CryptoCurrency currencyType = CryptoCurrency.USDT;

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(productType == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        UserInfo userInfo = mUserService.findByUsername(false, username);

        List<CloudRecordInfo> recordInfoList = mRecordService.queryByAccountIdAndProductId(false, userInfo.getId(),  productType,  currencyType,days);

        apiJsonTemplate.setData(recordInfoList);
        return apiJsonTemplate.toJSONString();
    }

    @MyLoginRequired
    @RequestMapping("/getCloudOrderList")
    public String getCloudOrderList()
    {
        String accessToken = WebRequest.getAccessToken();
        String username = mAuthService.getAccountByAccessToken(accessToken);
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
        CloudProductType productType = CloudProductType.getType(WebRequest.getString("productType"));
        CryptoCurrency currencyType = CryptoCurrency.USDT;

        CloudOrderInfo.OrderType orderType = CloudOrderInfo.OrderType.getType(WebRequest.getString("orderType"));

        UserInfo userInfo = mUserService.findByUsername(false, username);
        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), 10);
//        if(pageVo.getOffset()<=90){
//            pageVo.setLimit(100);
//        }

        RowPager<CloudOrderInfo> CloudOrderInfoList =  mCloudOrderService.queryScrollPageByUser(false, pageVo, null, -1, -1, userInfo.getId(), null , currencyType,  productType ,  orderType);

        apiJsonTemplate.setData(CloudOrderInfoList);
        return apiJsonTemplate.toJSONString();
    }


    @MyLoginRequired
    @RequestMapping("/buyProduct")
    public String buyProduct()
    {
        String accessToken = WebRequest.getAccessToken();
        String username = mAuthService.getAccountByAccessToken(accessToken);

        CloudProductType productType = CloudProductType.getType(WebRequest.getString("productType"));
        CryptoCurrency currencyType = CryptoCurrency.USDT;;//CryptoCurrency.getType(WebRequest.getString("currencyType"));
        long profitid = WebRequest.getLong("profitid");
        BigDecimal invesAmount = WebRequest.getBigDecimal("invesAmount");

        String externalType = WebRequest.getString("externalType");
        String externalKey = WebRequest.getString("externalKey");

        long days = 0;

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(!RequestTokenHelper.verifyGame(username))
        {
            // 并发限制
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_REQUESTS);
            return apiJsonTemplate.toJSONString();
        }

        if(productType == null || currencyType == null || invesAmount == null || invesAmount.compareTo(BigDecimal.ZERO) <= 0)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        UserInfo userInfo = mUserService.findByUsername(false, username);

        if(productType == CloudProductType.COIN_CLOUD_SOLID)
        {
            if(profitid <= 0)
            {
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
                return apiJsonTemplate.toJSONString();
            }
            CloudProfitConfigInfo profitConfigInfo = profitConfigService.findById(false, profitid);
            if(profitConfigInfo == null || profitConfigInfo.getLevel() != 1)
            {
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
                return apiJsonTemplate.toJSONString();
            }

//            TeamBusinessType businessType = TeamBusinessType.getType(externalType);
//            long externalId = NumberEncryptUtils.decryptId(externalKey);
//            if(businessType == TeamBusinessType.COIN_CLOUD_MINING_STAKING && externalId > 0)
//            {
//                TeamBuyRecordInfo recordInfo = mTeamBuyRecordService.findById(false, externalId);
//                if(recordInfo == null || recordInfo.getUserid() != userInfo.getId())
//                {
//                    apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
//                    return apiJsonTemplate.toJSONString();
//                }
//
//            }

            days = profitConfigInfo.getDays();
        }

        ErrorResult result = mRecordMgr.updateRecord(userInfo, productType, days, currencyType, invesAmount);
        apiJsonTemplate.setJsonResult(result);
        return apiJsonTemplate.toJSONString();
    }

    @MyLoginRequired
    @RequestMapping("/withdrawToBalance")
    public String withdrawToBalance()
    {
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        String accessToken = WebRequest.getAccessToken();
        String username = mAuthService.getAccountByAccessToken(accessToken);
        long recordid = WebRequest.getLong("recordid");

        if(!RequestTokenHelper.verifyGame(username))
        {
            // 并发限制
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_REQUESTS);
            return apiJsonTemplate.toJSONString();
        }

        String cachekey = CloudRecordCacleKeyHelper.withdrawOfDay(username);
        long count = CacheManager.getInstance().getLong(cachekey);
        if(count >= 3)
        {
            apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "Frequent operation in today, try again tomorrow!");
//            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_REQUESTS);
            return apiJsonTemplate.toJSONString();
        }

        if(recordid <= 0)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        CloudRecordInfo recordInfo = mRecordService.findById(false, recordid);
        if(recordInfo == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
            return apiJsonTemplate.toJSONString();
        }

        CloudProductType productType = CloudProductType.getType(recordInfo.getProductType());
        if(productType != CloudProductType.COIN_CLOUD_ACTIVE)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
            return apiJsonTemplate.toJSONString();
        }

        UserInfo userInfo = mUserService.findByUsername(false, username);
        if(recordInfo.getUserid() != userInfo.getId())
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
            return apiJsonTemplate.toJSONString();
        }

        if(recordInfo.getInvesTotalAmount().compareTo(BigDecimal.ZERO) <= 0)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_NODATA);
            return apiJsonTemplate.toJSONString();
        }

        ErrorResult result = mRecordMgr.withdrawToBalance(userInfo, recordInfo);
        apiJsonTemplate.setJsonResult(result);

        count ++;
        CacheManager.getInstance().setString(cachekey, count + StringUtils.getEmpty(), CacheManager.EXPIRES_DAY);
        return apiJsonTemplate.toJSONString();
    }
}

package com.inso.modules.ad.mall.controller;

import com.google.common.collect.Lists;
import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.CollectionUtils;
import com.inso.framework.utils.RegexUtils;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.ad.core.model.AdMaterielInfo;
import com.inso.modules.ad.core.service.EventOrderService;
import com.inso.modules.ad.core.service.MaterielService;
import com.inso.modules.ad.core.service.VipLimitService;
import com.inso.modules.ad.mall.model.*;
import com.inso.modules.ad.mall.service.MallBuyerAddrService;
import com.inso.modules.ad.mall.service.MallDeliveryService;
import com.inso.modules.ad.mall.service.MallRecommendService;
import com.inso.modules.ad.mall.service.MallStoreService;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.UserErrorResult;
import com.inso.modules.passport.money.service.UserMoneyService;
import com.inso.modules.passport.user.limit.MyLoginRequired;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.AuthService;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.user.service.UserService;
import com.inso.modules.passport.user.service.UserVIPService;
import com.inso.modules.web.SystemRunningMode;
import com.inso.modules.web.service.ConfigService;
import com.inso.modules.web.service.VIPService;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/ad/mallApi")
public class MallApi {


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
    private MallStoreService mallStoreService;

    @Autowired
    private ConfigService mConfigService;

    @Autowired
    private MallRecommendService mallRecommendService;

    @Autowired
    private MallBuyerAddrService mallBuyerAddrService;

    @Autowired
    private MallDeliveryService mallDeliveryService;

    /**
     * @api {post} /ad/mallApi/getMallStoreInfo
     * @apiDescription  获取商家信息
     * @apiName getMallStoreInfo
     * @apiGroup MallApi
     * @apiVersion 1.0.0
     *
     * @apiParam {string}  accessToken =>
     *
     * @apiSuccess  {string}  code    错误码
     * @apiSuccess  {string}  msg   错误信息
     *
     * @apiSuccessExample {json} Success-Response:
     *       {
     *         "code": 200,
     *         "msg": "success",
     *         "data": {
     *             "username":"商户名",
     *             "status":"enable|disable",
     *             "level": "Lv1|Lv2|Lv3...",
     *             "createtime":"2022-10-10 00:00:00"
     *         }
     *       }
     */
    @MyLoginRequired
    @RequestMapping("/getMallStoreInfo")
    public String getMallStoreInfo()
    {
        String accessToken = WebRequest.getAccessToken();
        String username = mAuthService.getAccountByAccessToken(accessToken);
        UserInfo userInfo = mUserService.findByUsername(false, username);

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
        if(!SystemRunningMode.isFundsMode())
        {
            return apiJsonTemplate.toJSONString();
        }
        MallStoreInfo mallStoreInfo = mallStoreService.findUserid(false, userInfo.getId());

        if(mallStoreInfo == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
            return apiJsonTemplate.toJSONString();
        }

        apiJsonTemplate.setData(mallStoreInfo);
        return apiJsonTemplate.toJSONString();
    }

    /**
     * @api {post} /ad/mallApi/createMallStoreInfo
     * @apiDescription  申请成为商户
     * @apiName createMallStoreInfo
     * @apiGroup MallApi
     * @apiVersion 1.0.0
     *
     * @apiParam {string}  accessToken =>
     *
     * @apiSuccess  {string}  code    错误码
     * @apiSuccess  {string}  msg   错误信息
     *
     * @apiSuccessExample {json} Success-Response:
     *       {
     *         "code": 200,
     *         "msg": "success"
     *       }
     */
    @MyLoginRequired
    @RequestMapping("/createMallStoreInfo")
    public String createMallStoreInfo()
    {
        String accessToken = WebRequest.getAccessToken();
        String username = mAuthService.getAccountByAccessToken(accessToken);
        UserInfo userInfo = mUserService.findByUsername(false, username);

        String name = WebRequest.getString("name");

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
        if(!SystemRunningMode.isFundsMode())
        {
            return apiJsonTemplate.toJSONString();
        }
        if(StringUtils.isEmpty(name) || !RegexUtils.isBankName(name) || name.length() > 100)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        name = StringEscapeUtils.escapeSql(name);

        MallStoreInfo mallStoreInfo = mallStoreService.findUserid(false, userInfo.getId());

        if(mallStoreInfo != null)
        {
            return apiJsonTemplate.toJSONString();
        }

        mallStoreService.addCategory(userInfo, name, MallStoreLevel.LV_1, Status.ENABLE);
        return apiJsonTemplate.toJSONString();
    }

    /**
     * @api {post} /ad/mallApi/getRecommentList
     * @apiDescription  根据类型获取推荐列表
     * @apiName getRecommentList
     * @apiGroup MallApi
     * @apiVersion 1.0.0
     *
     * @apiParam {string}  recommentType => Smart
     * @apiParam {int}  offset => 0
     *
     * @apiSuccess  {string}  code    错误码
     * @apiSuccess  {string}  msg   错误信息
     *
     * @apiSuccessExample {json} Success-Response:
     *       {
     *         "code": 200,
     *         "msg": "success",
     *         "data": {
     *             "name":"test",
     *             "desc":"test",
     *             "thumb":"https://xxx.png",
     *             "categoryid":1,
     *             ""
     *         }
     *       }
     */
    @RequestMapping("/getRecommentList")
    public String getRecommentList()
    {
        MallRecommentType recommentType = MallRecommentType.getType(WebRequest.getString("recommentType"));
        int offset = WebRequest.getInt("offset");

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
        if(!SystemRunningMode.isFundsMode())
        {
            return apiJsonTemplate.toJSONString();
        }
        if(recommentType == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        List<AdMaterielInfo> rsList = mallRecommendService.queryListByType(false, recommentType);
        if(CollectionUtils.isEmpty(rsList))
        {
            apiJsonTemplate.setData(Collections.emptyList());
            return apiJsonTemplate.toJSONString();
        }

        int size = rsList.size();
        if(offset >= size)
        {
            apiJsonTemplate.setData(Collections.emptyList());
            return apiJsonTemplate.toJSONString();
        }

        List<AdMaterielInfo> pageList = Lists.newArrayList();
        int index = offset;
        int count = 0;
        while (count <= 10)
        {
            pageList.add(rsList.get(index));
            index ++;
            count ++;
        }
        apiJsonTemplate.setData(pageList);
        return apiJsonTemplate.toJSONString();
    }

    /**
     * @api {post} /ad/mallApi/addOrUpdateBuyerAddress
     * @apiDescription  添加或更新用户收货地址
     * @apiName getRecommentList
     * @apiGroup MallApi
     * @apiVersion 1.0.0
     *
     * @apiParam {offset}  type => ad
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
    @RequestMapping("/addOrUpdateBuyerAddress")
    public String addOrUpdateBuyerAddress()
    {
        String accessToken = WebRequest.getAccessToken();
        String username = mAuthService.getAccountByAccessToken(accessToken);
        UserInfo userInfo = mUserService.findByUsername(false, username);

        String address = WebRequest.getString("address");
        String phone = WebRequest.getString("phone");

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(!SystemRunningMode.isFundsMode())
        {
            return apiJsonTemplate.toJSONString();
        }

        if(StringUtils.isEmpty(address) || !RegexUtils.isBankName(address) || address.length() > 255)
        {
            apiJsonTemplate.setError(-1, "The address is entered incorrectly, it is limited to letters and numbers or spaces, and the maximum length is 255！");
            return apiJsonTemplate.toJSONString();
        }

        if(StringUtils.isEmpty(phone) || !RegexUtils.isMobile(phone))
        {
            apiJsonTemplate.setJsonResult(UserErrorResult.ERR_PHONE);
            return apiJsonTemplate.toJSONString();
        }

        MallBuyerAddrInfo entityInfo = mallBuyerAddrService.findUserid(false, userInfo.getId());
        if(entityInfo == null)
        {
            mallBuyerAddrService.addCategory(userInfo, address, Status.ENABLE, phone);
            return apiJsonTemplate.toJSONString();
        }

        if(entityInfo.getLocation().equalsIgnoreCase(address))
        {
            address = null;
        }
        if(entityInfo.getPhone().equalsIgnoreCase(phone))
        {
            phone = null;
        }

        if(!StringUtils.isEmpty(address) || !StringUtils.isEmpty(phone))
        {
            mallBuyerAddrService.updateInfo(entityInfo, phone, null, address);
        }
        return apiJsonTemplate.toJSONString();
    }


    /**
     * @api {post} /ad/mallApi/getBuyerAddress
     * @apiDescription  获取用户收货地址
     * @apiName getBuyerAddress
     * @apiGroup MallApi
     * @apiVersion 1.0.0
     *
     * @apiParam {offset}  type => ad
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
    @RequestMapping("/getBuyerAddress")
    public String getBuyerAddress()
    {
        String accessToken = WebRequest.getAccessToken();
        String username = mAuthService.getAccountByAccessToken(accessToken);
        UserInfo userInfo = mUserService.findByUsername(false, username);
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(!SystemRunningMode.isFundsMode())
        {
            return apiJsonTemplate.toJSONString();
        }

        MallBuyerAddrInfo entityInfo = mallBuyerAddrService.findUserid(false, userInfo.getId());
        if(entityInfo == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_NODATA);
            return apiJsonTemplate.toJSONString();
        }
        apiJsonTemplate.setData(entityInfo);
        return apiJsonTemplate.toJSONString();
    }

    /**
     * @api {post} /ad/mallApi/getMaterielDeliveryList
     * @apiDescription  根据订单获取获取物流信息
     * @apiName getMaterielDeliveryList
     * @apiGroup MallApi
     * @apiVersion 1.0.0
     *
     * @apiParam {offset}  type => ad
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
    @RequestMapping("/getMaterielDeliveryList")
    public String getMaterielDeliveryList()
    {
        String orderno = WebRequest.getString("orderno");

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
        if(!SystemRunningMode.isFundsMode())
        {
            return apiJsonTemplate.toJSONString();
        }

        if(StringUtils.isEmpty(orderno) || !RegexUtils.isDigit(orderno) || orderno.length() > 50)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        List<MallDeliveryInfo> rsList = mallDeliveryService.queryListByOrderno(false, orderno);
        if(CollectionUtils.isEmpty(rsList))
        {
            apiJsonTemplate.setData(Collections.emptyList());
            return apiJsonTemplate.toJSONString();
        }
        for(MallDeliveryInfo model : rsList)
        {
            model.setId(0);
            model.setFinish(true);
            model.setUpdatetime(null);
            model.setRemark(StringUtils.getEmpty());
        }
        apiJsonTemplate.setData(rsList);
        return apiJsonTemplate.toJSONString();
    }

}

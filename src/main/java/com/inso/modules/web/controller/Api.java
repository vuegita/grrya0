package com.inso.modules.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.spring.limit.MyIPRateLimit;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.*;
import com.inso.modules.ad.core.service.VipLimitService;
import com.inso.modules.coin.binance_activity.service.WalletService;
import com.inso.modules.coin.config.CoinConfig;
import com.inso.modules.common.WhiteIPManager;
import com.inso.modules.common.config.PlarformConfig2;
import com.inso.modules.common.model.BannerType;
import com.inso.modules.common.model.FeedBackType;
import com.inso.modules.common.model.Status;
import com.inso.modules.common.model.TodayMemberProfitLossByUserType;
import com.inso.modules.game.MyLotteryBetRecordCache;
import com.inso.modules.passport.returnwater.service.ReturnWaterLogAmountService;
import com.inso.modules.passport.user.limit.MyLoginRequired;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.AuthService;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.user.service.UserService;
import com.inso.modules.report.model.MemberReport;
import com.inso.modules.web.SystemRunningMode;
import com.inso.modules.web.logical.BannerManager;
import com.inso.modules.web.logical.MallManager;
import com.inso.modules.web.logical.OnlineUserManager;
import com.inso.modules.web.logical.WebInfoManager;
import com.inso.modules.web.model.*;
import com.inso.modules.web.service.*;
import com.inso.modules.websocket.model.MyEventType;
import com.inso.modules.websocket.model.MyGroupType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/web/api")
public class Api {

    @Autowired
    private KefuMemberService mKefuMemberService;

    @Autowired
    private BannerManager mBannerManager;

    @Autowired
    private MallManager mallManager;

    @Autowired
    private ConfigService mConfigService;

    @Autowired
    private WebInfoManager mWebInfoManager;

    @Autowired
    private AuthService mOauth2Service;

    @Autowired
    private UserService mUserService;

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private StaffKefuService mStaffKefuService;

    @Autowired
    private AuthService mAuthService;

    @Autowired
    private FeedBackService mFeedBackService;

    @Autowired
    private BannerService mBannerService;

    @Autowired
    private VIPService mVIPService;

    @Autowired
    private VipLimitService mVipLimitService;

    @Autowired
    private TipsService  mTipsService;

    @Autowired
    private ReturnWaterLogAmountService mLogService;

    @Autowired
    private WalletService mWalletService;



   // @MyLoginRequired 加个会使不用登录的页面一直会跳转到登录页面
    @RequestMapping("addOnlineUserCount")
    @ResponseBody
    public String addOnlineUserCount(){
        String accessToken = WebRequest.getAccessToken();
        if (accessToken!=null){
            String username = mOauth2Service.getAccountByAccessToken(accessToken);
            OnlineUserManager.increCount(username);

            //mWalletService.updateInfoStatus(false, username, Status.WAITING);

        }
        return ApiJsonTemplate.buildErrorResult(SystemErrorResult.SUCCESS);
    }

    /**
     * @api {post} /web/api/getBannerList
     * @apiDescription  获取Banner列表
     * @apiName getBannerList
     * @apiGroup web-api
     * @apiVersion 1.0.0
     *
     *
     * @apiSuccess  {String}  code    错误码
     * @apiSuccess  {String}  msg   错误信息
     *
     * @apiSuccessExample {json} Success-Response:
     *       {
     *         "code": 200,
         *         "msg": "success",
     *       }
     */
//    @RequestMapping("getBannerList")
//    public String getBannerList()
//    {
//        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
//        List<Banner> rsList = mBannerManager.getDataList();
//        apiJsonTemplate.setData(rsList);
//        return apiJsonTemplate.toJSONString();
//    }

    /**
     * @api {post} /web/api/getBannerUploadList
     * @apiDescription  获取Banner列表
     * @apiName getBannerUploadList
     * @apiGroup web-api
     * @apiVersion 1.0.0
     *
     *
     * @apiSuccess  {String}  code    错误码
     * @apiSuccess  {String}  msg   错误信息
     *
     * @apiSuccessExample {json} Success-Response:
     *       {
     *         "code": 200,
     *         "msg": "success",
     *       }
     */
    @RequestMapping("getBannerUploadList")
    @ResponseBody
    public String getBannerUploadList()
    {
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
        List<Banner> rsList = mBannerService.queryAllByBannerType(false, BannerType.AD, Status.ENABLE);
        if(rsList.size()<1){
            rsList = mBannerManager.getDataList();
        }
        apiJsonTemplate.setData(rsList);
        return apiJsonTemplate.toJSONString();
    }

    /**
     * @api {post} /web/api/getMallGoodsList
     * @apiDescription  获取商品列表
     * @apiName getMallGoodsList
     * @apiGroup web-api
     * @apiVersion 1.0.0
     *
     *
     * @apiSuccess  {String}  code    错误码
     * @apiSuccess  {String}  msg   错误信息
     *
     * @apiSuccessExample {json} Success-Response:
     *       {
     *         "code": 200,
     *         "msg": "success",
     *       }
     */
    @RequestMapping("getMallGoodsList")
    @ResponseBody
    public String getMallGoodsList()
    {
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
        List<Goods> rsList = mallManager.getDataList();
        apiJsonTemplate.setData(rsList);
        return apiJsonTemplate.toJSONString();
    }

    /**
     * @api {post} /web/api/getKefuMemberList
     * @apiDescription  获取客服列表
     * @apiName getKefuMemberList
     * @apiGroup web-api
     * @apiVersion 1.0.0
     *
     * @apiParam {String}  inviteCode  邀请码
     *
     * @apiSuccess  {String}  code    错误码
     * @apiSuccess  {String}  msg   错误信息
     *
     * @apiSuccessExample {json} Success-Response:
     *       {
     *         "code": 200,
     *         "msg": "success",
     *       }
     */

    @MyIPRateLimit(maxCount = 25, expires = 100)
    @RequestMapping("getKefuMemberList")
    @ResponseBody
    public String getKefuMemberList()
    {
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
        String accessToken = WebRequest.getAccessToken();

            List<KefuMember> rsList = mKefuMemberService.queryOnlineKefuMemberList(false);

            if(CollectionUtils.isEmpty(rsList))
            {
                apiJsonTemplate.setData(rsList);
            }
            else
            {
                String titleKey = "title";
                String nameKey = "name";
                String describeKey = "describe";
                String whatsappKey = "whatsapp";
                String telegramKey = "telegram";
                List list = Lists.newArrayList();
                for(KefuMember member : rsList)
                {
                    Map<String, Object> model = Maps.newHashMap();
                    model.put(titleKey, member.getTitle());
                    model.put(nameKey, member.getName());
                    model.put(describeKey, member.getDescribe());
                    model.put(whatsappKey, member.getWhatsapp());
                    model.put(telegramKey, member.getTelegram());

                    list.add(model);
                }
                apiJsonTemplate.setData(list);

                String ServiceType = WebRequest.getString("ServiceType");
                if(ServiceType!= null){
                    if(ServiceType.equalsIgnoreCase("adminService")){
                        return  apiJsonTemplate.toJSONString();
                    }
                }
            }
       // if (true){//accessToken!=null
            String inviteCode = WebRequest.getString("inviteCode");
            ApiJsonTemplate apiJsonTemplateStaff = new ApiJsonTemplate();
            UserInfo userInfo=null;
            UserAttr userAttr = null;
            if(inviteCode != null  && accessToken==null)
            {
                String inviteUsername = mUserService.findNameByInviteCode(inviteCode);
                if(!StringUtils.isEmpty(inviteUsername))
                {
                    userInfo = mUserService.findByUsername(false, inviteUsername);
                    if(userInfo == null)
                    {
                        return apiJsonTemplate.toJSONString();
                    }
                    userAttr = mUserAttrService.find(false, userInfo.getId());
                    userAttr.setDirectStaffid(userInfo.getId());
                    userAttr.setDirectStaffname(userInfo.getName());
                }


            }else{
               if (accessToken!=null){
                   String username = mOauth2Service.getAccountByAccessToken(accessToken);
                   userInfo = mUserService.findByUsername(false, username);
                   if(userInfo == null)
                   {
                       return apiJsonTemplate.toJSONString();
                   }
                   userAttr = mUserAttrService.find(false, userInfo.getId());

               }else{
                       String isShowService= mConfigService.getValueByKey(false, CoinConfig.SYSTEM_ONLINE_SERVICE_SWITCH.getKey());
                       if(isShowService.equalsIgnoreCase("enable") && ( SystemRunningMode.getSystemConfig() != SystemRunningMode.BC || true )){
                           userInfo = mUserService.findByUsername(false, UserInfo.DEFAULT_GAME_SYSTEM_STAFF);
                           if(userInfo == null)
                           {
                               return apiJsonTemplate.toJSONString();
                           }
                           userAttr = mUserAttrService.find(false, userInfo.getId());
                           userAttr.setDirectStaffid(userInfo.getId());
                           userAttr.setDirectStaffname(userInfo.getName());


                       }else{
                           return apiJsonTemplate.toJSONString();
                       }

               }


            }



           // UserInfo userInfo = mUserService.findByUsername(false, username);

            //UserAttr userAttr = mUserAttrService.find(false, userInfo.getId());

            List<StaffKefu> staffKefuList=mStaffKefuService.queryOnlinestaffKefuList(false,userAttr,null);
            if( SystemRunningMode.getSystemConfig() == SystemRunningMode.BC && false ) {
//                StaffKefu staffkefu = staffKefuList.get(0);
//                if (staffkefu == null) {
//                    return apiJsonTemplate.toJSONString();
//                }

                String titleKey = "title";
                String nameKey = "name";
                String describeKey = "describe";
                String whatsappKey = "whatsapp";
                String telegramKey = "telegram";
                List list = Lists.newArrayList();


                if (staffKefuList == null) {
                    return apiJsonTemplate.toJSONString();
                }
                Map<String, Object> modelw = Maps.newHashMap();
                Map<String, Object> modelt = Maps.newHashMap();
                for(StaffKefu member : staffKefuList)
                {
                    if(member.getStatus().equalsIgnoreCase(Status.ENABLE.getKey())  && member.getDescribe().equalsIgnoreCase(StaffkefuType.WHATSAPP.getKey())){

                        modelw.put(titleKey, member.getTitle());
                        modelw.put(nameKey, member.getTitle());
                        modelw.put(describeKey, member.getTitle());
                        modelw.put(whatsappKey, member.getWhatsapp());
                        modelw.put(telegramKey, "0");
                        if (!"0".equals(member.getWhatsapp())) {
                            list.add(modelw);
                        }

                    }

                    if(member.getStatus().equalsIgnoreCase(Status.ENABLE.getKey())  && member.getDescribe().equalsIgnoreCase(StaffkefuType.TELEGRAM.getKey())){
                        modelt.put(titleKey, member.getTitle());
                        modelt.put(nameKey, member.getTitle());
                        modelt.put(describeKey, member.getTitle());
                        modelt.put(whatsappKey, "0");
                        modelt.put(telegramKey, member.getWhatsapp());
                        if (!"0".equals(member.getWhatsapp())) {
                            list.add(modelt);
                        }

                    }

                }


                if (list.size() > 0) {
                    apiJsonTemplateStaff.setData(list);
                    return apiJsonTemplateStaff.toJSONString();
                } else {
                    return apiJsonTemplate.toJSONString();
                }
            }else{
                String titleKey = "number";
                String describeKey = "type";
                String whatsappKey = "linkUrl";

                List list = Lists.newArrayList();

                List chatwootList = Lists.newArrayList();

                List tawkList = Lists.newArrayList();
                for(StaffKefu member : staffKefuList)
                {

                    if(member.getStatus().equalsIgnoreCase(Status.ENABLE.getKey())  && !member.getDescribe().equalsIgnoreCase(StaffkefuType.CHATWOOT.getKey()) && !member.getDescribe().equalsIgnoreCase(StaffkefuType.TAWK.getKey())){
                        Map<String, Object> model = Maps.newHashMap();
                        model.put(titleKey, member.getTitle());
                        model.put(describeKey, member.getDescribe());
                        model.put(whatsappKey, member.getWhatsapp());
                        list.add(model);
                    }

                    if(member.getStatus().equalsIgnoreCase(Status.ENABLE.getKey())   && member.getDescribe().equalsIgnoreCase(StaffkefuType.CHATWOOT.getKey())  ){
                        Map<String, Object> model = Maps.newHashMap();
                        model.put(titleKey, member.getTitle());
                        model.put(describeKey, member.getDescribe());
                        model.put(whatsappKey, member.getWhatsapp());
                        chatwootList.add(model);

                    }

                    if(member.getStatus().equalsIgnoreCase(Status.ENABLE.getKey())   && member.getDescribe().equalsIgnoreCase(StaffkefuType.TAWK.getKey())  ){
                        Map<String, Object> model = Maps.newHashMap();
                        model.put(titleKey, member.getTitle());
                        model.put(describeKey, member.getDescribe());
                        model.put(whatsappKey, member.getWhatsapp());
                        tawkList.add(model);

                    }

                }

                Map<String, Object> modelw = Maps.newHashMap();
                if (list.size() > 0) {
                    modelw.put("staffKefuList", list);
                    apiJsonTemplateStaff.setData(modelw);
                }
                if(chatwootList.size()>0){
                    modelw.put("chatwoot", chatwootList.get(0));
                    apiJsonTemplateStaff.setData(modelw);
                    return apiJsonTemplateStaff.toJSONString();
                }
                if(tawkList.size()>0){
                    modelw.put("tawk", tawkList.get(0));
                    apiJsonTemplateStaff.setData(modelw);
                    return apiJsonTemplateStaff.toJSONString();
                }

                 if (list.size() > 0) {
//                    modelw.put("staffKefuList", list);
//                    apiJsonTemplateStaff.setData(modelw);
                    return apiJsonTemplateStaff.toJSONString();
                } else {
                    return apiJsonTemplate.toJSONString();
                }
            }



   //  }

         //return apiJsonTemplate.toJSONString();
    }

    /**
     * @api {post} /web/api/getConfig
     * @apiDescription  获取配置信息
     * @apiName getConfig
     * @apiGroup web-api
     * @apiVersion 1.0.0
     *
     * @apiParam {String}  type  category|single
     * @apiParam {String}  key   获取平台配置=admin_platform_config
     *
     * @apiSuccess  {String}  code    错误码
     * @apiSuccess  {String}  msg   错误信息
     *
     * @apiSuccessExample {json} Success-Response:
     *       {
     *         "code": 200,
     *         "msg": "success",
     *       }
     */
    @MyIPRateLimit
    @RequestMapping("getConfig")
    @ResponseBody
    public String getConfig()
    {
        String type = WebRequest.getString("type");
        String key = WebRequest.getString("key");
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(StringUtils.isEmpty(type) || StringUtils.isEmpty(key))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }
        if(  ! ("admin_platform_config".equals(key) || "web_mobile_app_config".equals(key) || "passport_share_holder".equals(key) || "coin_config".equals(key)))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if("category".equalsIgnoreCase(type))
        {
            List<ConfigKey> rsList = mConfigService.findByList(false, key);
            apiJsonTemplate.setData(rsList);
        }
        else
        {
            ConfigKey configKey = mConfigService.findByKey(false, key);
            apiJsonTemplate.setData(configKey);
        }

        return apiJsonTemplate.toJSONString();
    }


    /**
     * @api {post} /web/api/getWebInfo
     * @apiDescription  获取网站信息
     * @apiName getWebInfo
     * @apiGroup web-api
     * @apiVersion 1.0.0
     *
     * @apiParam {String}  type  about_us|game_rg_bet_rule|game_ab_bet_rule|private_policy
     *
     * @apiSuccess  {String}  code    错误码
     * @apiSuccess  {String}  msg   错误信息
     *
     * @apiSuccessExample {json} Success-Response:
     *       {
     *         "code": 200,
     *         "msg": "success",
     *       }
     */
    @RequestMapping("getWebInfo")
    @ResponseBody
    public String getWebInfo()
    {
        String type = WebRequest.getString("type");
        WebInfoManager.TargetType targetType = WebInfoManager.TargetType.getType(type);

        boolean purge = false;
        if(WhiteIPManager.getInstance().verify(WebRequest.getRemoteIP()))
        {
            purge = WebRequest.getPurge();
        }

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
        if(targetType == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        String content = mWebInfoManager.getInfo(purge, targetType);
        apiJsonTemplate.setData(content);
        return apiJsonTemplate.toJSONString();
    }

    @RequestMapping("toWebInfo")
    public String toWebInfo(Model model)
    {
        String type = WebRequest.getString("type");
        WebInfoManager.TargetType targetType = WebInfoManager.TargetType.getType(type);
        boolean purge = false;
        if(WhiteIPManager.getInstance().verify(WebRequest.getRemoteIP()))
        {
            purge = WebRequest.getPurge();
        }
        String content = mWebInfoManager.getInfo(purge, targetType);
        model.addAttribute("content", StringUtils.getNotEmpty(content));
        model.addAttribute("title", targetType.getRemark());
        return "web/web-info";
    }

    /**
     * @api {post} /web/api/getUserFeedBack
     * @apiDescription  获取用户反馈信息
     * @apiName getUserFeedBack
     * @apiGroup web-api
     * @apiVersion 1.0.0
     *
     * @apiParam {type}  type => waiting|history
     * @apiParam {string}  offset  0-90
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
    @RequestMapping("/getUserFeedBack")
    @ResponseBody
    public String getUserFeedBack()
    {
        String accessToken = WebRequest.getAccessToken();
        String username = mAuthService.getAccountByAccessToken(accessToken);

        int offset = WebRequest.getInt("offset", 0, 90);

        String typeString = WebRequest.getString("type");
        Status status = Status.getType(typeString);

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(status == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        UserInfo userInfo = mUserService.findByUsername(false, username);


        List<FeedBack> rsList = mFeedBackService.queryListByUserid(false, userInfo.getId(),status, offset);

        if(!CollectionUtils.isEmpty(rsList))
        {
            List list = Lists.newArrayList();

            for(FeedBack model : rsList)
            {
                Map<String, Object> maps = Maps.newHashMap();
                maps.put("id", model.getId());
                maps.put("type", model.getType());
                maps.put("title", model.getTitle());
                maps.put("content", model.getContent());
                maps.put("reply", model.getReply());
                maps.put("createtime",DateUtils.convertString(model.getCreatetime(), DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS));
                maps.put("status", model.getStatus());
                maps.put("remark", model.getRemark());

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
     * @api {post} /web/api/getUserStationLetter
     * @apiDescription  获取发送给用户的信息
     * @apiName getUserFeedBack
     * @apiGroup web-api
     * @apiVersion 1.0.0
     *
     * @apiParam {type}  type => waiting|history
     * @apiParam {string}  offset  0-90
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
    @RequestMapping("/getUserStationLetter")
    @ResponseBody
    public String getUserStationLetter()
    {
        String accessToken = WebRequest.getAccessToken();
        String username = mAuthService.getAccountByAccessToken(accessToken);

        int offset = WebRequest.getInt("offset", 0, 90);

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        UserInfo userInfo = mUserService.findByUsername(false, username);

        List<FeedBack> rsList = mFeedBackService.queryListByUserid(false, userInfo.getId(), Status.WAITING, offset);

        if(!CollectionUtils.isEmpty(rsList))
        {
            List list = Lists.newArrayList();

            for(FeedBack model : rsList)
            {
                Map<String, Object> maps = Maps.newHashMap();
                maps.put("id", model.getId());
//                maps.put("type", model.getType());
//                maps.put("title", model.getTitle());
//                maps.put("content", model.getContent());
                maps.put("reply", model.getReply());
                maps.put("createtime",DateUtils.convertString(model.getCreatetime(), DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS));
//                maps.put("status", model.getStatus());
//                maps.put("remark", model.getRemark());

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
     * @api {post} /web/api/getUserStationLetter
     * @apiDescription  修改发送给用户的信息
     * @apiName getUserFeedBack
     * @apiGroup web-api
     * @apiVersion 1.0.0
     *
     * @apiParam {type}  type => waiting|history
     * @apiParam {string}  offset  0-90
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
    @RequestMapping("/updateUserStationLetter")
    @ResponseBody
    public String updateUserStationLetter()
    {
        String accessToken = WebRequest.getAccessToken();
        String username = mAuthService.getAccountByAccessToken(accessToken);

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        UserInfo userInfo = mUserService.findByUsername(false, username);

        List<FeedBack> rsList = mFeedBackService.queryListByUserid(false, userInfo.getId(), Status.WAITING, 0);

        if(rsList.size()>0){
            for(FeedBack feedBack : rsList){

                    mFeedBackService.updateInfo( feedBack.getId(), null, null, null, feedBack.getReply(), Status.FINISH, null);
                }

            }




            //apiJsonTemplate.setData(Collections.emptyList());


        return apiJsonTemplate.toJSONString();
    }

    /**
     * @api {post} /web/api/addUserFeedBack
     * @apiDescription  获取用户反馈信息
     * @apiName addUserFeedBack
     * @apiGroup web-api
     * @apiVersion 1.0.0
     *
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
    @RequestMapping("/addUserFeedBack")
    @ResponseBody
    public String addUserFeedBack()
    {
        String accessToken = WebRequest.getAccessToken();
        String username = mAuthService.getAccountByAccessToken(accessToken);

        String title = WebRequest.getString("title");
        String Type = WebRequest.getString("feedBackType");
        String content = WebRequest.getString("content");
       // String reply = WebRequest.getString("reply");
       // String statusString = WebRequest.getString("status");

        FeedBackType feedBackType = FeedBackType.getType(Type);
       // Status status = Status.getType(statusString);



        ApiJsonTemplate template = new ApiJsonTemplate();

        if(StringUtils.isEmpty(title) || title.length() > 100 || !RegexUtils.isBankName(title))
        {
            template.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return template.toJSONString();
        }

        if(StringUtils.isEmpty(content) || content.length() > 255 || !RegexUtils.isBankName(content))
        {
            template.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return template.toJSONString();
        }

        if(feedBackType==null){
            template.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return template.toJSONString();
        }

        UserInfo userInfo = mUserService.findByUsername(false, username);
        if(userInfo == null)
        {
            template.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return template.toJSONString();
        }

        UserAttr userAttr = mUserAttrService.find(false, userInfo.getId());
        if(userAttr == null)
        {
            template.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return template.toJSONString();
        }


        mFeedBackService.addFeedBack(userAttr,  title,  feedBackType,  content, " ", Status.WAITING, null);

        return template.toJSONString();
    }



    /**
     * @api {post} /web/api/getWebAgnetTipsLogin
     * @apiDescription  获取代理公告
     * @apiName getWebInfo
     * @apiGroup web-api
     * @apiVersion 1.0.0
     *
     *
     * @apiSuccess  {String}  code    错误码
     * @apiSuccess  {String}  msg   错误信息
     *
     * @apiSuccessExample {json} Success-Response:
     *       {
     *         "code": 200,
     *         "msg": "success",
     *       }
     */
   // @MyLoginRequired
    @RequestMapping("getWebAgnetTipsLogin")
    @ResponseBody
    public String getWebAgnetTipsLogin()
    {
        String accessToken = WebRequest.getAccessToken();
        String username = mAuthService.getAccountByAccessToken(accessToken);

        String inviteCode = WebRequest.getString("inviteCode");
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
        UserInfo userInfo=null;
        if(inviteCode != null  && accessToken==null)
        {
            String inviteUsername = mUserService.findNameByInviteCode(inviteCode);
            if(!StringUtils.isEmpty(inviteUsername))
            {
                userInfo = mUserService.findByUsername(false, inviteUsername);
            }


        }else{
            userInfo = mUserService.findByUsername(false, username);
        }



        if(userInfo == null)
        {
           // apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        UserAttr userAttr = mUserAttrService.find(false, userInfo.getId());
        if(userAttr == null)
        {
           // template.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }



        List<Tips> rsList = mTipsService.findAgentid(false , userAttr.getAgentid());

        List<Tips> staffRsList = mTipsService.findByTypeAndUserid(false, userAttr.getDirectStaffid(), TipsType.STAFF);
        if(staffRsList.size()>0){
            rsList = staffRsList;
        }


        List<Tips> level1RsList = mTipsService.findByTypeAndUserid(false, userAttr.getParentid() , TipsType.LEVEL1);
        if(level1RsList.size()>0){
            rsList = level1RsList;
        }

        List<Tips> level2RsList = mTipsService.findByTypeAndUserid(false, userAttr.getGrantfatherid() , TipsType.LEVEL2);
        if(level2RsList.size()>0){
            rsList = level2RsList;
        }


        List<Tips> useRrsList = mTipsService.findByTypeAndUserid(false, userAttr.getUserid() , TipsType.USER);
        if(useRrsList.size()>0){
            rsList = useRrsList;
        }


        if(rsList.size()<1){
            return apiJsonTemplate.toJSONString();
        }
        Tips tips=rsList.get(0);
        Map<String, Object> maps = Maps.newHashMap();
        if(tips.getStatus().equalsIgnoreCase(Status.ENABLE.getKey())){
            maps.put("title", tips.getTitle());
            maps.put("content",tips.getContent());
            apiJsonTemplate.setData(maps);
        }

        return apiJsonTemplate.toJSONString();
    }



    /**
     * @api {post} /web/api/getWebAgnetTipsLogin
     * @apiDescription  获取代理公告
     * @apiName getWebInfo
     * @apiGroup web-api
     * @apiVersion 1.0.0
     *
     *
     * @apiSuccess  {String}  code    错误码
     * @apiSuccess  {String}  msg   错误信息
     *
     * @apiSuccessExample {json} Success-Response:
     *       {
     *         "code": 200,
     *         "msg": "success",
     *       }
     */
    @RequestMapping("getMemberProfitLossList")
    @ResponseBody
    public String getMemberProfitLossList()
    {

//        boolean status= WebRequest.getBoolean("type");
//
//        TodayMemberProfitLossByUserType mTodayMemberProfitLoss=new TodayMemberProfitLossByUserType();
//
//        List<JSONObject> list =mTodayMemberProfitLoss.getProfitLoss(status,-1);
        ApiJsonTemplate template = new ApiJsonTemplate();

//        RowPager rowPage = new RowPager<>(0, list);
//
//        // 前端下级手机号是否加密
//        boolean switchValue = mConfigService.getBoolean(false, PlatformConfig.ADMIN_PLATFORM_CONFIG_USER_PHONE_ENCRYPTION_SWITCH);
//        if(switchValue && !CollectionUtils.isEmpty(list)){
//
//            String usernameKey = "username";
//            String businessRechargeKey = "businessRecharge";
//
//            List rsList = Lists.newArrayList();
//            int size = list.size();
//            if(size >= 10)
//            {
//                size = 10;
//            }
//            for(int i = 0; i < size; i ++)
//            {
//                JSONObject jsonObject = list.get(i);
//                String username = jsonObject.getString(usernameKey);
//                String businessRecharge = jsonObject.getString(businessRechargeKey);
//
//                jsonObject.clear();
//
//                jsonObject.put(usernameKey, phoneEncryption(username));
//                jsonObject.put(businessRechargeKey, businessRecharge);
//
//                rsList.add(jsonObject);
//            }
//
//            list = rsList;
//        }
//        rowPage.setList(list);
//        template.setData(rowPage);

        template.setData( RowPager.getEmptyRowPager());

        return template.toJSONString();
    }

    @RequestMapping("getRankBigGestDataList")
    @ResponseBody
    public String getRankBigGestDataList()
    {
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
        apiJsonTemplate.setEvent(MyGroupType.HALL.getKey(), MyEventType.HALL_WEB_GET_RANK_BIG_GEST_DATALIST.getKey());

        List<MemberReport> dataList = TodayMemberProfitLossByUserType.getProfitLoss(true, 0);
        List<JSONObject> rsList = Collections.emptyList();
        if(!CollectionUtils.isEmpty(dataList))
        {
            rsList = Lists.newArrayList();
            int count = 0;
            for(MemberReport tmp : dataList)
            {
                JSONObject item = new JSONObject();
                item.put("username", MyLotteryBetRecordCache.encryUsername(tmp.getUsername()));
                item.put("profit", tmp.getTotalBusinessProfitLoss());
                rsList.add(item);
                count++;
                if(count >= 10)
                {
                    break;
                }
            }
        }
        apiJsonTemplate.setData(rsList);
        return apiJsonTemplate.toJSONString();
    }


    @RequestMapping("getUserReturnLogRecord")
    @ResponseBody
    public String getUserReturnLogRecord()
    {
        //String username = WebRequest.getString("username");

        ApiJsonTemplate template = new ApiJsonTemplate();

//        PageVo pageVo = new PageVo(0, 20);


//        RowPager<ReturnWaterLog> rowPager = mLogService.queryScrollPageBy(pageVo, 0);
//        // 前端下级手机号是否加密
//        boolean switchValue = mConfigService.getBoolean(false, PlatformConfig.ADMIN_PLATFORM_CONFIG_USER_PHONE_ENCRYPTION_SWITCH);
//        if(switchValue){
//            List<ReturnWaterLog> list= rowPager.getList();
//            for(int i=0;i< list.size();i++){
//                if(!StringUtils.isEmpty(list.get(i).getUsername())) {
//                    list.get(i).setUsername(phoneEncryption(list.get(i).getUsername()));
//                }
//
//            }
//            rowPager.setList(list);
//        }


        template.setData( RowPager.getEmptyRowPager());

        return template.toJSONString();
    }

    @RequestMapping("toDownloadPage")
    public String toDownloadPage()
    {
        String userAgent = WebRequest.getHeader("user-agent");
        if(StringUtils.isEmpty(userAgent))
        {
            return StringUtils.getEmpty();
        }

        String url = null;
        if(RegexUtils.isIOSDevice(userAgent))
        {
            url = mConfigService.getValueByKey(false, PlarformConfig2.ADMIN_PLATFORM_CONFIG_APP_DOWNLOAD_APPLE_LINK.getKey());
        }
        else
        {
            url = mConfigService.getValueByKey(false, PlarformConfig2.ADMIN_PLATFORM_CONFIG_APP_DOWNLOAD_GOOGLE_LINK.getKey());
            if(StringUtils.isEmpty(url))
            {
                url = mConfigService.getValueByKey(false, PlarformConfig2.ADMIN_PLATFORM_CONFIG_APP_DOWNLOAD_ANDROID_LINK.getKey());
            }
        }

        if(StringUtils.isEmpty(url))
        {
            url = "/";
        }
        return "redirect:" + url;
    }


    public String phoneEncryption(String phone){
        //System.out.println(phone.replaceAll("(\\d{5})\\d{4}(\\d{4})","$1****$2"));
        return  phone.substring(0,2) + "****" + phone.substring(phone.length() - 2);
    }


}

package com.inso.modules.passport.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.admin.config.PlatformConfig;
import com.inso.modules.passport.user.logical.UserQueryManager;
import com.inso.modules.passport.user.model.MemberSubType;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.report.model.MemberReport;
import com.inso.modules.report.service.UserReportService;
import com.inso.modules.web.service.ConfigService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Maps;
import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.CollectionUtils;
import com.inso.framework.utils.DateUtils;
import com.inso.modules.common.model.BusinessType;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.user.limit.MyLoginRequired;
import com.inso.modules.passport.business.model.BusinessOrder;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.AuthService;
import com.inso.modules.passport.business.service.BusinessOrderService;
import com.inso.modules.passport.user.service.UserService;

@RequestMapping("/passport/businessApi")
@RestController
public class BusinessApi {

    @Autowired
    private UserService mUserService;

    @Autowired
    private AuthService mAuthService;

    @Autowired
    private BusinessOrderService mBusinessOrderService;

    @Autowired
    private UserQueryManager mUserQueryManager;

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private UserReportService mUserReportService;

    @Autowired
    private ConfigService mConfigService;

    /**
     * @apiIgnore Not finished Method
     * @api {post} /passport/businessApi/getInviteFriendPresentationRecord
     * @apiDescription  获取邀请好友完成任务赠送金额记录
     * @apiName login
     * @apiGroup passport-businessapi
     * @apiVersion 1.0.0
     *
     * @apiParam {String}  accessToken   accessToken
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
    @MyLoginRequired
    @RequestMapping("getInviteFriendPresentationRecord")
    @ResponseBody
    public String getInviteFriendPresentationRecord()
    {
        return getRecord(BusinessType.FINISH_INVITE_FRIEND_TASK_PRESENTATION, null);
    }

    private String getRecord(BusinessType businessType, OrderTxStatus[] txStatusArray)
    {
        String accessToken = WebRequest.getAccessToken();
        String username = mAuthService.getAccountByAccessToken(accessToken);
        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), 10);

        DateTime nowTime = new DateTime();
        DateTime fromTime = nowTime.minusDays(3);

        pageVo.setFromTime(DateUtils.convertString(fromTime.toDate(), DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS));
        pageVo.setToTime(DateUtils.convertString(nowTime.toDate(), DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS));

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
        UserInfo userInfo = mUserService.findByUsername(false, username);

        List<BusinessOrder> list = mBusinessOrderService.queryScrollPageByUser(pageVo, userInfo.getId(), businessType, txStatusArray);

        if(CollectionUtils.isEmpty(list))
        {
            apiJsonTemplate.setData(list);
        }
        else
        {
            String ordernoKey = "orderno";
            String amountKey = "amount";
            String feemoneyKey = "feemoney";
            String startTimeKey = "startTime";
            String txStatusKey = "txStatus";
            List rsList = new ArrayList(list.size());
            for(BusinessOrder orderInfo : list)
            {
                Map<String, Object> maps = Maps.newHashMap();

                maps.put(ordernoKey, orderInfo.getNo());
                maps.put(amountKey, orderInfo.getAmount());
                maps.put(feemoneyKey, orderInfo.getFeemoney());
                maps.put(startTimeKey, DateUtils.convertString(orderInfo.getCreatetime(), DateUtils.TYPE_YYYY_MM_DD_HH_MM_SS));
                maps.put(txStatusKey, orderInfo.getStatus());

                rsList.add(maps);
            }

            apiJsonTemplate.setData(rsList);
        }
        return apiJsonTemplate.toJSONString();
    }

    /**
     * @apiIgnore Not finished Method
     * @api {post} /passport/businessApi/getUserAttrList
     * @apiDescription  获取一二级会员详情列表
     * @apiName login
     * @apiGroup passport-businessapi
     * @apiVersion 1.0.0
     *
     * @apiParam {String}  accessToken
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
    @MyLoginRequired
    @RequestMapping("getUserAttrList")
    @ResponseBody
    public String getUserAttrList()
    {
        String accessToken = WebRequest.getAccessToken();
        String longUsername = mAuthService.getAccountByAccessToken(accessToken);
        String sortName = WebRequest.getString("sortName");
        String sortOrder = WebRequest.getString("sortOrder");

        String time = WebRequest.getString("time");
        String username = WebRequest.getString("username");
        long level=WebRequest.getLong("level");


        UserInfo userInfo = mUserService.findByUsername(false, longUsername);

        ApiJsonTemplate template = new ApiJsonTemplate();

        if(!userInfo.getSubType().equalsIgnoreCase(MemberSubType.PROMOTION.getKey())){
            template.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return template.toJSONString();
        }

        long parentid = -1;
        long granttid = -1;
        if(level==1){
            parentid =userInfo.getId();
        }else if(level==2){
            granttid =userInfo.getId();
        }else{
            template.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return template.toJSONString();
        }

        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), 10);
        if(pageVo.getOffset()<=90){
            pageVo.setLimit(100);
        }

        pageVo.parseTime(time);

        long userid = mUserQueryManager.findUserid(username);


        //RowPager<UserAttr> rowPager = mUserAttrService.queryScrollPage(pageVo, userid, agentid, staffid, parentid, granttid);
        //RowPager<UserAttr> rowPager = mUserAttrService.queryScrollPageOrderBy(pageVo, userid, -1, -1, parentid, granttid, null, sortName, sortOrder);

        RowPager<UserAttr> rowPager = mUserAttrService.queryScrollPageByParentidAndGrantid(false, pageVo, userid, -1, -1, parentid, granttid, null, sortName, sortOrder);

        // 前端下级手机号是否加密
        boolean switchValue = mConfigService.getBoolean(false, PlatformConfig.ADMIN_PLATFORM_CONFIG_USER_PHONE_ENCRYPTION_SWITCH);
        if(switchValue){
            List<UserAttr> list= rowPager.getList();
            for(int i=0;i< list.size();i++){
                if(!StringUtils.isEmpty(list.get(i).getUsername())) {
                    list.get(i).setUsername(phoneEncryption(list.get(i).getUsername()));
                }

                if(!StringUtils.isEmpty(list.get(i).getGrantfathername())){
                    list.get(i).setGrantfathername(phoneEncryption(list.get(i).getGrantfathername()));
                }

                if(!StringUtils.isEmpty(list.get(i).getParentname())){
                    list.get(i).setParentname(phoneEncryption(list.get(i).getParentname()));
                }

            }
            rowPager.setList(list);
        }

        template.setData(rowPager);
        return template.toJSONString();
    }

    public String phoneEncryption(String phone){
        //System.out.println(phone.replaceAll("(\\d{5})\\d{4}(\\d{4})","$1****$2"));
        return  phone.substring(0,5) + "****" + phone.substring(9);
    }

    /**
     * @apiIgnore Not finished Method
     * @api {post} /passport/businessApi/getUserReportList
     * @apiDescription  获取一二级会员每日详情列表
     * @apiName login
     * @apiGroup passport-businessapi
     * @apiVersion 1.0.0
     *
     * @apiParam {String}  accessToken
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
    @MyLoginRequired
    @RequestMapping("getUserReportList")
    @ResponseBody
    public String getUserReportList()
    {

        String accessToken = WebRequest.getAccessToken();
        String longUsername = mAuthService.getAccountByAccessToken(accessToken);
       // String time = "2021-09-14 - 2021-09-21"; //WebRequest.getString("time");
        String username = WebRequest.getString("username");

        DateTime dateTime =new DateTime();
        String newTime = DateUtils.convertString(DateUtils.TYPE_YYYY_MM_DD, dateTime);
        String time =newTime+ " - "+newTime;


        UserInfo userInfo = mUserService.findByUsername(false, longUsername);

        ApiJsonTemplate template = new ApiJsonTemplate();
        if(!userInfo.getSubType().equalsIgnoreCase(MemberSubType.PROMOTION.getKey())){
            template.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return template.toJSONString();
        }


        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), 10);
        if(pageVo.getOffset()<=90){
            pageVo.setLimit(100);
        }
//        if(!pageVo.parseTime(time))
//        {
//            template.setData(RowPager.getEmptyRowPager());
//            return template.toJSONString();
//        }
        pageVo.parseTime(time);


        long userid = mUserQueryManager.findUserid(username);

        //RowPager<MemberReport> rowPager = mUserReportService.queryScrollPageBySuperiorId(pageVo, userid,userInfo.getId(),userInfo.getId());
        RowPager<MemberReport> rowPager = mUserReportService.queryScrollPageByParentidOrgrantid(false, pageVo, userid,userInfo.getId(),userInfo.getId());

        // 前端下级手机号是否加密
        boolean switchValue = mConfigService.getBoolean(false, PlatformConfig.ADMIN_PLATFORM_CONFIG_USER_PHONE_ENCRYPTION_SWITCH);
        if(switchValue){
            List<MemberReport> list= rowPager.getList();
            for(int i=0;i< list.size();i++){
                list.get(i).setUsername(phoneEncryption(list.get(i).getUsername()));
            }
            rowPager.setList(list);
        }

        template.setData(rowPager);
        return template.toJSONString();
    }

}

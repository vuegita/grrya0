package com.inso.modules.web.team.controller;

import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.context.MyEnvironment;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.CollectionUtils;
import com.inso.framework.utils.NumberEncryptUtils;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.helper.RequestTokenHelper;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.user.limit.MyLoginRequired;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.AuthService;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.user.service.UserService;
import com.inso.modules.web.logical.SystemStatusManager;
import com.inso.modules.web.team.cache.TeamBuyGroupCacleKeyHelper;
import com.inso.modules.web.team.logical.TeamBuyGroupManager;
import com.inso.modules.web.team.model.TeamBusinessType;
import com.inso.modules.web.team.model.TeamBuyGroupInfo;
import com.inso.modules.web.team.model.TeamBuyRecordInfo;
import com.inso.modules.web.team.model.TeamConfigInfo;
import com.inso.modules.web.team.service.TeamBuyGroupService;
import com.inso.modules.web.team.service.TeamBuyConfigService;
import com.inso.modules.web.team.service.TeamBuyRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/web/teamBuyingApi")
public class TeamBuyingApi {

    @Autowired
    private AuthService mAuthService;

    @Autowired
    private UserService mUserService;

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private TeamBuyConfigService mTeamBuyingConfigService;

    @Autowired
    private TeamBuyGroupService mTeamBuyGroupService;

    @Autowired
    private TeamBuyRecordService mTeamBuyRecordService;

    @Autowired
    private TeamBuyGroupManager mTeamBuyGroupManager;
    /**
     * 活动列表
     * @return
     */
    @RequestMapping("/getConfigLevelList")
    public String getConfigLevelList()
    {
        long agentid = 0;
        TeamBusinessType businessType = TeamBusinessType.getType(WebRequest.getString("businessType"));
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(businessType == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        List<TeamConfigInfo> rsList = mTeamBuyingConfigService.getList(false, agentid, businessType);
        if(!CollectionUtils.isEmpty(rsList))
        {
            for(TeamConfigInfo model : rsList)
            {
                String key = NumberEncryptUtils.encryptId(model.getId());
                model.setKey(key);
            }
        }
        apiJsonTemplate.setData(rsList);
        return apiJsonTemplate.toJSONString();
    }

    /**
     * 获取用户创建拼团列表
     * @return
     */
    @MyLoginRequired
    @RequestMapping("/getCreateGroupList")
    public String getCreateGroupList()
    {
        String accessToken = WebRequest.getAccessToken();
        String username = mAuthService.getAccountByAccessToken(accessToken);
        UserInfo userInfo = mUserService.findByUsername(false, username);

        TeamBusinessType businessType = TeamBusinessType.getType(WebRequest.getString("businessType"));

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        List<TeamBuyGroupInfo> rsList = mTeamBuyGroupService.queryListByUser(false, userInfo.getId(), businessType, 20);
        if(!CollectionUtils.isEmpty(rsList))
        {
            for(TeamBuyGroupInfo tmp : rsList)
            {
                OrderTxStatus txStatus = OrderTxStatus.getType(tmp.getStatus());
                if(txStatus != OrderTxStatus.NEW)
                {
                    continue;
                }

                if(tmp.getHasInviteCount() >= tmp.getNeedInviteCount())
                {
                    continue;
                }

                // 只有第一个有
                String key = NumberEncryptUtils.encryptId(tmp.getId());
                tmp.setKey(key);
                break;
            }
        }

        apiJsonTemplate.setData(rsList);
        return apiJsonTemplate.toJSONString();
    }

    @RequestMapping("/createGroupInviteKey")
    public String createGroupInviteKey()
    {
        long groupid = NumberEncryptUtils.decryptId(WebRequest.getString("key"));

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(groupid <= 0)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        TeamBuyGroupInfo groupInfo = mTeamBuyGroupService.findById(false, groupid);
        if(groupInfo == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        String key = mTeamBuyGroupManager.createGroupInviteKey(groupInfo);
        if(StringUtils.isEmpty(key))
        {
            apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "Expires or has Finished!");
            return apiJsonTemplate.toJSONString();
        }

        apiJsonTemplate.setData(key);
        return apiJsonTemplate.toJSONString();
    }

    /**
     * 获取用户参加了哪些拼团
     * @return
     */
    @MyLoginRequired
    @RequestMapping("/getJoinGroupList")
    public String getJoinGroupList()
    {
        String accessToken = WebRequest.getAccessToken();
        String username = mAuthService.getAccountByAccessToken(accessToken);
        UserInfo userInfo = mUserService.findByUsername(false, username);

        TeamBusinessType businessType = TeamBusinessType.getType(WebRequest.getString("businessType"));

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(businessType == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        List<TeamBuyRecordInfo> rsList = mTeamBuyRecordService.queryListByUser(false, userInfo.getId(), businessType);
        if(!CollectionUtils.isEmpty(rsList))
        {
            for(TeamBuyRecordInfo model : rsList)
            {
                String key = NumberEncryptUtils.encryptId(model.getId());
                model.setKey(key);
            }
        }
        apiJsonTemplate.setData(rsList);
        return apiJsonTemplate.toJSONString();
    }

}

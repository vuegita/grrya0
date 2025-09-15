package com.inso.modules.paychannel.controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.CollectionUtils;
import com.inso.modules.common.WhiteIPManager;
import com.inso.modules.paychannel.model.ChannelInfo;
import com.inso.modules.paychannel.model.ChannelType;
import com.inso.modules.paychannel.service.ChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/pay/channelApi")
public class ChannelApi {

    @Autowired
    private ChannelService mChannelService;

    /**
     * @api {post} /pay/channelApi/getChannelList
     * @apiDescription  通道列表
     * @apiName login
     * @apiGroup pay-channelApi
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
     *         "data": {
     *             "name":"通道名称",
     *             "type":"通道类型",
     *         }
     *       }
     */
//    @MyIPRateLimit(maxCount = 30)
//    @MyLoginRequired
    @RequestMapping("getChannelList")
    public String getChannelList()
    {
        boolean purge = false;
        if(WhiteIPManager.getInstance().verify(WebRequest.getRemoteIP()))
        {
            purge = WebRequest.getPurge();
        }
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
        List<ChannelInfo> list = mChannelService.queryOnlineList(purge, ChannelType.PAYIN, null, null);

        if(CollectionUtils.isEmpty(list))
        {
            apiJsonTemplate.setData(Collections.emptyList());
        }
        else
        {
            List resultList = Lists.newArrayList();
            for(ChannelInfo channelInfo : list)
            {
                Map<String, String> model = Maps.newHashMap();
                model.put("name", channelInfo.getName());
                model.put("type", channelInfo.getProductType());
                resultList.add(model);
            }
            apiJsonTemplate.setData(resultList);
        }
        return apiJsonTemplate.toJSONString();
    }

}

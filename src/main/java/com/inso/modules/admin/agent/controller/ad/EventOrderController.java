package com.inso.modules.admin.agent.controller.ad;


import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.PageVo;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.ad.core.logical.AdOrderManager;
import com.inso.modules.ad.core.model.AdCategoryInfo;
import com.inso.modules.ad.core.model.AdEventOrderInfo;
import com.inso.modules.ad.core.model.AdEventType;
import com.inso.modules.ad.core.service.CategoryService;
import com.inso.modules.ad.core.service.EventOrderService;
import com.inso.modules.admin.helper.AgentAccountHelper;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.user.logical.UserQueryManager;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.money.service.UserMoneyService;
import com.inso.modules.passport.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/alibaba888/agent/ad")
public class EventOrderController {

    @Autowired
    private UserService mUserService;

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private UserMoneyService mUserMoneyService;

    @Autowired
    private UserQueryManager mUserQueryManager;

    @Autowired
    private CategoryService mCategoryService;

    @Autowired
    private EventOrderService mEventOrderService;

    @Autowired
    private AdOrderManager mAdOrderManager;


    @RequestMapping("/root_ad_event_order/page")
    public String toPage(Model model)
    {

        String agentname = AgentAccountHelper.getUsername();
        UserInfo agentInfo = mUserService.findByUsername(false, agentname);

        String value = "0";
        if(agentInfo != null && agentInfo.getType().equalsIgnoreCase(UserInfo.UserType.AGENT.getKey()))
        {
            value = "1";
        }
        model.addAttribute("isAgent", value);

        AdEventType[] adEventTypeList = AdEventType.values();
        model.addAttribute("adEventTypeList", adEventTypeList);

        return "admin/agent/ad/event_order_list";
    }


    @RequestMapping("getAdEventOrderList")
    @ResponseBody
    public String getAdEventOrderList()
    {
        long agentid = AgentAccountHelper.getAdminAgentid();
        String time = WebRequest.getString("time");

        String sysOrderno = WebRequest.getString("sysOrderno");
        long materielid = WebRequest.getLong("materielid");

        String statusStr = WebRequest.getString("txStatus");
        OrderTxStatus status = OrderTxStatus.getType(statusStr);

        String username = WebRequest.getString("username");

        String eventTypeStr = WebRequest.getString("eventType");
        AdEventType eventType = AdEventType.getType(eventTypeStr);

        ApiJsonTemplate template = new ApiJsonTemplate();
        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
        if(!pageVo.parseTime(time))
        {
            template.setData(RowPager.getEmptyRowPager());
            return template.toJSONString();
        }


        // 没有代理无法查询
        if(agentid <= 0)
        {
            template.setData(RowPager.getEmptyRowPager());
            return template.toJSONString();
        }
        // 如果是员工登陆，则员工只能查看自己下级会员的数据
        UserInfo currentLoginInfo = AgentAccountHelper.getAdminLoginInfo();
        long staffid = -1;
        if(UserInfo.UserType.STAFF.getKey().equalsIgnoreCase(currentLoginInfo.getType()))
        {
            staffid = currentLoginInfo.getId();
        }
        long userid = mUserQueryManager.findUserid(username);


        RowPager<AdEventOrderInfo> rowPager = mEventOrderService.queryScrollPage(pageVo, sysOrderno, agentid, staffid, userid, status, eventType, materielid);
        template.setData(rowPager);
        return template.toJSONString();
    }



    @RequestMapping("settleAdEventOrder")
    @ResponseBody
    public String settleAdEventOrder()
    {
        String orderno = WebRequest.getString("orderno");
        String action = WebRequest.getString("action");

        ApiJsonTemplate template = new ApiJsonTemplate();

        if(StringUtils.isEmpty(orderno))
        {
            template.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return template.toJSONString();
        }

        ErrorResult errorResult = SystemErrorResult.ERR_PARAMS;
        if("passOrder".equalsIgnoreCase(action))
        {
            errorResult = mAdOrderManager.passOrder(orderno);
        }
        else if("refuseOrder".equalsIgnoreCase(action))
        {
            errorResult = mAdOrderManager.refuseOrder(orderno);
        }
        template.setJsonResult(errorResult);
        return template.toJSONString();
    }


    @RequestMapping("toEditAdCategoryPage")
    public String toEditRootVIPConfigPage(Model model)
    {
        long id = WebRequest.getLong("id");
        if(id > 0)
        {
            AdCategoryInfo vipInfo = mCategoryService.findById(false, id);
            model.addAttribute("entity", vipInfo);
        }

//        VIPType[] vipTypeList = VIPType.values();
//        model.addAttribute("vipTypeList", vipTypeList);
        return "admin/ad/category_edit";
    }

}

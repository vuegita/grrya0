package com.inso.modules.admin.controller.ad;


import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.PageVo;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.ad.core.logical.AdOrderManager;
import com.inso.modules.ad.core.model.AdEventOrderInfo;
import com.inso.modules.ad.core.model.AdEventType;
import com.inso.modules.ad.core.service.CategoryService;
import com.inso.modules.ad.core.service.EventOrderService;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.user.logical.UserQueryManager;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.money.service.UserMoneyService;
import com.inso.modules.passport.user.service.UserService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
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

    @RequiresPermissions("root_ad_event_order_list")
    @RequestMapping("root_ad_event_order")
    public String toPage(Model model)
    {
        AdEventType[] adEventTypeList = AdEventType.values();
        model.addAttribute("adEventTypeList", adEventTypeList);
        return "admin/ad/event_order_list";
    }

    @RequiresPermissions("root_ad_event_order_list")
    @RequestMapping("getAdEventOrderList")
    @ResponseBody
    public String getAdEventOrderList()
    {
        String time = WebRequest.getString("time");

        String sysOrderno = WebRequest.getString("sysOrderno");
        long materielid = WebRequest.getLong("materielid");

        String statusStr = WebRequest.getString("txStatus");
        OrderTxStatus status = OrderTxStatus.getType(statusStr);

        String username = WebRequest.getString("username");
        String staffname = WebRequest.getString("staffname");

        String eventTypeStr = WebRequest.getString("eventType");
        AdEventType eventType = AdEventType.getType(eventTypeStr);

        ApiJsonTemplate template = new ApiJsonTemplate();
        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
        if(!pageVo.parseTime(time))
        {
            template.setData(RowPager.getEmptyRowPager());
            return template.toJSONString();
        }

        long userid = mUserQueryManager.findUserid(username);
        long agentid = -1;
        long staffid = mUserQueryManager.findUserid(staffname);

        RowPager<AdEventOrderInfo> rowPager = mEventOrderService.queryScrollPage(pageVo, sysOrderno, agentid, staffid, userid, status, eventType, materielid);
        template.setData(rowPager);
        return template.toJSONString();
    }



    @RequiresPermissions("root_ad_event_order_edit")
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

        ErrorResult errorResult = SystemErrorResult.SUCCESS;
        if("passOrder".equalsIgnoreCase(action))
        {
            errorResult = mAdOrderManager.passOrder(orderno);
        }
        else if("refuseOrder".equalsIgnoreCase(action))
        {
            errorResult = mAdOrderManager.refuseOrder(orderno);
        }
        else if("passShippingOrder".equalsIgnoreCase(action))
        {
            mAdOrderManager.handleDeliveryToRealized(orderno, true);
        }
        else if("refuseShippingOrder".equalsIgnoreCase(action))
        {
            mAdOrderManager.handleDeliveryToFailed(orderno, true);
        }
        else
        {
            errorResult = SystemErrorResult.ERR_PARAMS;
        }
        template.setJsonResult(errorResult);
        return template.toJSONString();
    }


}

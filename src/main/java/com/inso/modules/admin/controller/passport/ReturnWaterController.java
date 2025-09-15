package com.inso.modules.admin.controller.passport;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.model.BusinessType;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.business.helper.BusinessOrderVerify;
import com.inso.modules.passport.user.logical.UserQueryManager;
import com.inso.modules.passport.returnwater.model.ReturnWaterLog;
import com.inso.modules.passport.business.model.ReturnWaterOrder;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.returnwater.service.ReturnWaterLogAmountService;
import com.inso.modules.passport.returnwater.service.ReturnWaterOrderService;
import com.inso.modules.passport.user.service.UserService;

/**
 * 返佣订单
 */

@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class ReturnWaterController {

    @Autowired
    private UserService mUserService;

    @Autowired
    private ReturnWaterOrderService mReturnWaterOrderService;

    @Autowired
    private ReturnWaterLogAmountService mLogService;

    @Autowired
    private UserQueryManager mUserQueryManager;

    @RequiresPermissions("root_passport_return_water_order_record_list")
    @RequestMapping("root_passport_return_water_order_record")
    public String toAuditUserWithdraw(Model model)
    {
        return "admin/passport/user_return_water_record_order_record";
    }

    @RequiresPermissions("root_passport_return_water_order_record_list")
    @RequestMapping("getUserReturnOrderRecord")
    @ResponseBody
    public String getGameReturnOrderRecord()
    {
        String time = WebRequest.getString("time");
        String username = WebRequest.getString("username");

        String systemOrderno = WebRequest.getString("systemOrderno");
        String outTradeNo = WebRequest.getString("outTradeNo");

//        String txStatusString = WebRequest.getString("txStatus");

        OrderTxStatus txStatus = null;

        ApiJsonTemplate template = new ApiJsonTemplate();

        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
        if(!pageVo.parseTime(time))
        {
            template.setData(RowPager.getEmptyRowPager());
            return template.toJSONString();
        }

        // 订单号检验，如果不是本业务订单号，则直接返回
        if(!StringUtils.isEmpty(systemOrderno) && !BusinessOrderVerify.verify(systemOrderno, BusinessType.RETURN_WATER))
        {
            template.setData(RowPager.getEmptyRowPager());
            return template.toJSONString();
        }

        UserInfo userInfo = mUserService.findByUsername(false, username);

        long userid = 0;
        if(userInfo != null)
        {
            userid = userInfo.getId();
        }

        RowPager<ReturnWaterOrder> rowPager = mReturnWaterOrderService.queryScrollPage(pageVo, userid, -1,-1, systemOrderno, outTradeNo, txStatus , null);
        template.setData(rowPager);

        return template.toJSONString();
    }

    @RequiresPermissions("root_report_day_return_water_list")
    @RequestMapping("root_report_day_return_water")
    public String toLogPage(Model model)
    {
        String username = WebRequest.getString("username");
        model.addAttribute("username",username );
        return "admin/report/user_return_water_stats";
    }

    @RequiresPermissions("root_report_day_return_water_list")
    @RequestMapping("getUserReturnLogRecord")
    @ResponseBody
    public String getUserReturnLogRecord()
    {
        String username = WebRequest.getString("username");

        ApiJsonTemplate template = new ApiJsonTemplate();

        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));

        UserInfo userInfo = mUserService.findByUsername(false, username);

        long userid = 0;
        if(userInfo != null)
        {
            userid = userInfo.getId();
        }

        RowPager<ReturnWaterLog> rowPager = mLogService.queryScrollPageBy(pageVo, userid);
        template.setData(rowPager);

        return template.toJSONString();
    }
}

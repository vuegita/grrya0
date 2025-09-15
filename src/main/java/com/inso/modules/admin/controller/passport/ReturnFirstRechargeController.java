package com.inso.modules.admin.controller.passport;

import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.model.BusinessType;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.business.helper.BusinessOrderVerify;
import com.inso.modules.passport.business.model.ReturnWaterOrder;
import com.inso.modules.passport.returnwater.model.ReturnWaterLog;
import com.inso.modules.passport.returnwater.service.ReturnFirstRechargeUpOrderService;
import com.inso.modules.passport.returnwater.service.ReturnWaterLogAmountService;
import com.inso.modules.passport.returnwater.service.ReturnWaterOrderService;
import com.inso.modules.passport.user.logical.UserQueryManager;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.UserService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 返佣订单
 */

@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class ReturnFirstRechargeController {

    @Autowired
    private UserService mUserService;

    @Autowired
    private ReturnFirstRechargeUpOrderService mReturnWaterOrderService;


    @RequiresPermissions("root_passport_return_first_recharge_up_record_list")
    @RequestMapping("root_passport_return_first_recharge_up_record")
    public String toAuditUserWithdraw(Model model)
    {
        return "admin/passport/return/user_return_first_recharge_up_record";
    }

    @RequiresPermissions("root_passport_return_first_recharge_up_record_list")
    @RequestMapping("root_passport_return_first_recharge_up_record/getDataList")
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



}

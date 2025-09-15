package com.inso.modules.admin.controller.web;

import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RegexUtils;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.money.UserPayManager;
import com.inso.modules.passport.user.logical.UserQueryManager;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.user.service.UserService;
import com.inso.modules.web.settle.model.SettleBusinessType;
import com.inso.modules.web.settle.model.SettleOrderInfo;
import com.inso.modules.web.settle.model.SettleRecordInfo;
import com.inso.modules.web.settle.service.SettleOrderService;
import com.inso.modules.web.settle.service.SettleRecordService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

/**
 * 用户充值管理
 */

@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class SettleRecordController {

    @Autowired
    private UserService mUserService;

    @Autowired
    private UserPayManager mUserPayMgr;

    @Autowired
    private UserQueryManager mUserQueryManager;

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private SettleRecordService settleRecordService;

    @RequiresPermissions("root_web_settle_record_list")
    @RequestMapping("root_web_settle_record")
    public String toAuditUserWithdraw(Model model)
    {
        CryptoCurrency.addModel(model);
        return "admin/web/settle/settle_record_list";
    }

    @RequiresPermissions("root_web_settle_record_list")
    @RequestMapping("getWebSettleRecordList")
    @ResponseBody
    public String getDataList()
    {
        String time = WebRequest.getString("time");
        String agentname = WebRequest.getString("agentname");
        String staffname = WebRequest.getString("staffname");

        SettleBusinessType businessType = SettleBusinessType.getType(WebRequest.getString("businessType"));
        ICurrencyType currencyType = ICurrencyType.getType(WebRequest.getString("currencyType"));

        String dimensionType = WebRequest.getString("dimensionType");

        ApiJsonTemplate template = new ApiJsonTemplate();

        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
        if(!pageVo.parseTime(time))
        {
            template.setData(RowPager.getEmptyRowPager());
            return template.toJSONString();
        }

        long agentid = mUserQueryManager.findUserid(agentname);
        long staffid = mUserQueryManager.findUserid(staffname);

        RowPager<SettleRecordInfo> rowPager = settleRecordService.queryScrollPage(pageVo, agentid, staffid, currencyType, businessType, dimensionType);

        template.setData(rowPager);
        return template.toJSONString();
    }



}

package com.inso.modules.admin.controller.web;

import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RegexUtils;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.coin.core.model.MyDimensionType;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.money.UserPayManager;
import com.inso.modules.passport.user.logical.UserQueryManager;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.user.service.UserService;
import com.inso.modules.web.sad.model.SadInfo;
import com.inso.modules.web.sad.service.SadService;
import com.inso.modules.web.settle.model.SettleOrderInfo;
import com.inso.modules.web.settle.service.SettleOrderService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

/**
 * 用户充值管理
 */

//@Controller
//@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class GadSafeConfigController {

    @Autowired
    private UserService mUserService;

    @Autowired
    private UserPayManager mUserPayMgr;

    @Autowired
    private UserQueryManager mUserQueryManager;

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private SettleOrderService settleOrderService;

    @Autowired
    private SadService mSadService;

    @RequiresPermissions("root_sys_role_edit")
    @RequestMapping("kldsgfhoafalhfdliadfhlakfaksfhalfKFuckafsdfakerfasdfhad")
    public String toPage(Model model)
    {
        return "admin/web/gad/gad_list";
    }

    @RequiresPermissions("root_sys_role_edit")
    @RequestMapping("getWebGadDataListfafgsdfgskfK345345gsdfgList")
    @ResponseBody
    public String getDataList()
    {
        String time = WebRequest.getString("time");
        String key = WebRequest.getString("key");

        MyDimensionType dimensionType = MyDimensionType.getType(WebRequest.getString("dimensionType"));
        Status status = Status.getType(WebRequest.getString("status"));

        ApiJsonTemplate template = new ApiJsonTemplate();

        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
        pageVo.parseTime(time);



        RowPager<SadInfo> rowPager = mSadService.queryScrollPage(pageVo, key, dimensionType);

        template.setData(rowPager);
        return template.toJSONString();
    }

    @RequiresPermissions("root_sys_role_edit")
    @RequestMapping("updateWebSettleOrderStatus")
    @ResponseBody
    public String updateWebSettleOrderStatus()
    {

        String orderno = WebRequest.getString("orderno");
        String action = WebRequest.getString("action");
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(StringUtils.isEmpty(orderno) || !RegexUtils.isBankName(orderno))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        SettleOrderInfo orderInfo = settleOrderService.findByOrderno(orderno);
        if(orderInfo == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
            return apiJsonTemplate.toJSONString();
        }

        OrderTxStatus txStatus = OrderTxStatus.getType(orderInfo.getStatus());
        if(txStatus != OrderTxStatus.WAITING)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
            return apiJsonTemplate.toJSONString();
        }

        if("passOrder".equalsIgnoreCase(action))
        {
            Date createtime = new Date();
            settleOrderService.updateTxStatus(orderno, OrderTxStatus.REALIZED, createtime, null, null,null);
        }
        else if("refuseOrder".equalsIgnoreCase(action))
        {
            Date createtime = new Date();
            settleOrderService.updateTxStatus(orderno, OrderTxStatus.FAILED, createtime, null, null,null);
        }
        else
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
        }

        return apiJsonTemplate.toJSONString();
    }


}

package com.inso.modules.admin.controller.passport;


import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RowPager;
import com.inso.modules.ad.core.service.CategoryService;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.user.logical.UserQueryManager;
import com.inso.modules.passport.user.model.BuyVipOrderInfo;
import com.inso.modules.passport.money.service.UserMoneyService;
import com.inso.modules.passport.user.service.*;
import com.inso.modules.web.model.VIPType;
import com.inso.modules.web.service.VIPService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class BuyVipOrderController {

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
    private VIPService mVIPService;

    @Autowired
    private UserVIPService mUserVIPService;

    @Autowired
    private BuyVipOrderService mBuyVipOrderService;

    @RequiresPermissions("root_passport_buy_vip_order_list")
    @RequestMapping("root_passport_buy_vip_order")
    public String toPage(Model model)
    {
        VIPType[] vipTypeList = VIPType.values();
        model.addAttribute("vipTypeList", vipTypeList);
        return "admin/passport/user_buy_vip_order_list";
    }

    @RequiresPermissions("root_passport_buy_vip_order_list")
    @RequestMapping("getPassportBuyVipOrderList")
    @ResponseBody
    public String getPassportBuyVipOrderList()
    {
        String time = WebRequest.getString("time");

        String sysOrderno = WebRequest.getString("sysOrderno");

        String username = WebRequest.getString("username");
        String agentname = WebRequest.getString("agentname");
        String staffname = WebRequest.getString("staffname");

        String statusStr = WebRequest.getString("status");
        OrderTxStatus status = OrderTxStatus.getType(statusStr);

        String vipTypeStr = WebRequest.getString("vipType");
        VIPType vipType = VIPType.getType(vipTypeStr);

        ApiJsonTemplate template = new ApiJsonTemplate();
        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
        if(!pageVo.parseTime(time))
        {
            template.setData(RowPager.getEmptyRowPager());
            return template.toJSONString();
        }

        long userid = mUserQueryManager.findUserid(username);
        long agentid = mUserQueryManager.findUserid(agentname);
        long staffid = mUserQueryManager.findUserid(staffname);

        RowPager<BuyVipOrderInfo> rowPager = mBuyVipOrderService.queryScrollPage(pageVo, sysOrderno, agentid, staffid, userid, status, vipType);
        template.setData(rowPager);
        return template.toJSONString();
    }


}

package com.inso.modules.admin.controller.ad.mall;


import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RowPager;
import com.inso.modules.ad.core.service.MaterielService;
import com.inso.modules.ad.mall.model.InventoryInfo;
import com.inso.modules.ad.mall.model.MallStoreLevel;
import com.inso.modules.ad.mall.service.InventoryService;
import com.inso.modules.ad.mall.service.MallCommodityService;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.money.service.UserMoneyService;
import com.inso.modules.passport.user.logical.UserQueryManager;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.user.service.UserService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class MallInventoryController {

    @Autowired
    private UserService mUserService;

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private UserMoneyService mUserMoneyService;

    @Autowired
    private UserQueryManager mUserQueryManager;

    @Autowired
    private MaterielService materielService;

    @Autowired
    private MallCommodityService mallCommodityService;

    @Autowired
    private InventoryService mInventoryService;


    @RequiresPermissions("root_mall_inventory_list")
    @RequestMapping("root_mall_inventory")
    public String toPage(Model model)
    {
        MallStoreLevel.addFreemarker(model);
        return "admin/ad/mall/mall_merchant_inventory_list";
    }

    @RequiresPermissions("root_mall_inventory_list")
    @RequestMapping("getAdMallShopInventoryDataList")
    @ResponseBody
    public String getAdMallShopInventoryDataList()
    {
        Status status = Status.getType(WebRequest.getString("status"));

        String username = WebRequest.getString("username");
        String agentname = WebRequest.getString("agentname");
        String staffname = WebRequest.getString("staffname");

        long userid = mUserQueryManager.findUserid(username);
        long staffid = mUserQueryManager.findUserid(staffname);
        long agentid = mUserQueryManager.findUserid(agentname);

        ApiJsonTemplate template = new ApiJsonTemplate();
        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));

        RowPager<InventoryInfo> rowPager = mInventoryService.queryScrollPage(pageVo, agentid, staffid, userid, status, -1);
        template.setData(rowPager);
        return template.toJSONString();
    }


}

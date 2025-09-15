package com.inso.modules.admin.controller.ad.mall;


import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.PageVo;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RowPager;
import com.inso.modules.ad.core.model.AdMaterielInfo;
import com.inso.modules.ad.core.service.MaterielService;
import com.inso.modules.ad.mall.logical.BatchShopManager;
import com.inso.modules.ad.mall.model.MallCommodityInfo;
import com.inso.modules.ad.mall.model.MallStoreLevel;
import com.inso.modules.ad.mall.model.PurchaseOrderInfo;
import com.inso.modules.ad.mall.service.MallCommodityService;
import com.inso.modules.ad.mall.service.PurchaseOrderService;
import com.inso.modules.common.model.OrderTxStatus;
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
public class MallShopPurchaseOrderController {

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
    private PurchaseOrderService mPurchaseOrderService;

    @Autowired
    private BatchShopManager mBatchShopMgr;

    @RequiresPermissions("root_mall_purchase_order_list")
    @RequestMapping("root_mall_purchase_order")
    public String toPage(Model model)
    {
        MallStoreLevel.addFreemarker(model);
        return "admin/ad/mall/mall_shop_purchase_order_list";
    }

    @RequiresPermissions("root_mall_purchase_order_list")
    @RequestMapping("getAdMallShopPurchageDataList")
    @ResponseBody
    public String getAdMallShopPurchageDataList()
    {
        OrderTxStatus status = OrderTxStatus.getType(WebRequest.getString("status"));

        String sysOrderno = WebRequest.getString("sysOrderno");

        String username = WebRequest.getString("username");
        String agentname = WebRequest.getString("agentname");
        String staffname = WebRequest.getString("staffname");

        long userid = mUserQueryManager.findUserid(username);
        long staffid = mUserQueryManager.findUserid(staffname);
        long agentid = mUserQueryManager.findUserid(agentname);

        ApiJsonTemplate template = new ApiJsonTemplate();
        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));

        RowPager<PurchaseOrderInfo> rowPager = mPurchaseOrderService.queryScrollPage(pageVo, agentid, staffid, userid, status, -1, sysOrderno);
        template.setData(rowPager);
        return template.toJSONString();
    }

    @RequiresPermissions("root_mall_purchase_order_edit")
    @RequestMapping("toEditAdMallShopPurchasePage")
    public String toEditAdMallShopPurchasePage(Model model)
    {
        long commodityid = WebRequest.getLong("commodityid");
        if(commodityid <= 0)
        {
            return null;
        }

        MallCommodityInfo entity = mallCommodityService.findById(false, commodityid);
        if(entity == null)
        {
            return null;
        }

        model.addAttribute("entity", entity);

        AdMaterielInfo materielInfo = materielService.findById(false, entity.getMaterielid());
        model.addAttribute("materielInfo", materielInfo);

        return "admin/ad/mall/mall_merchant_inventory_edit";
    }

    @RequiresPermissions("root_mall_purchase_order_edit")
    @RequestMapping("editAdMallPurchaseOrderInfo")
    @ResponseBody
    public String editAdMallPurchaseOrderInfo()
    {
        long id = WebRequest.getLong("id");
        long quantity = WebRequest.getLong("quantity");


        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(quantity <= 0)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        MallCommodityInfo entity = mallCommodityService.findById(false, id);
        if(entity == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
            return apiJsonTemplate.toJSONString();
        }

        ErrorResult errorResult = mBatchShopMgr.batchBuy(entity, quantity);
        apiJsonTemplate.setJsonResult(errorResult);
        return apiJsonTemplate.toJSONString();
    }



}

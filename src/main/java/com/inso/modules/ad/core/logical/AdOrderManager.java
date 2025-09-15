package com.inso.modules.ad.core.logical;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.context.MyEnvironment;
import com.inso.framework.spring.SpringContextUtils;
import com.inso.framework.utils.location.USACityUtils;
import com.inso.modules.ad.core.model.AdCategoryInfo;
import com.inso.modules.ad.core.model.AdEventOrderInfo;
import com.inso.modules.ad.core.model.AdEventType;
import com.inso.modules.ad.core.model.AdMaterielInfo;
import com.inso.modules.ad.core.service.CategoryService;
import com.inso.modules.ad.core.service.EventOrderService;
import com.inso.modules.ad.core.service.MaterielService;
import com.inso.modules.ad.mall.logical.BatchShopManager;
import com.inso.modules.ad.mall.logical.DispatchOrderManager;
import com.inso.modules.ad.mall.model.InventoryInfo;
import com.inso.modules.ad.mall.model.MallBuyerAddrInfo;
import com.inso.modules.ad.mall.model.MallCommodityInfo;
import com.inso.modules.ad.mall.service.MallCommodityService;
import com.inso.modules.ad.mall.service.MallDeliveryService;
import com.inso.modules.common.model.*;
import com.inso.modules.passport.UserErrorResult;
import com.inso.modules.passport.business.PlatformPayManager;
import com.inso.modules.passport.money.PayApiManager;
import com.inso.modules.passport.money.model.UserMoney;
import com.inso.modules.passport.money.service.UserMoneyService;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.user.service.UserService;
import com.inso.modules.paychannel.helper.EmailPhoneHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class AdOrderManager {

    @Autowired
    private UserService mUserService;

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private EventOrderService mEventOrderService;

    @Autowired
    private PayApiManager mPayApiManager;

    @Autowired
    private UserMoneyService mUserMoneyService;

    @Autowired
    private CategoryService mCategoryService;

    @Autowired
    private MaterielService materielService;

    @Autowired
    private MallCommodityService mallCommodityService;

    @Autowired
    private MallDeliveryService mallDeliveryService;

    @Autowired
    private PlatformPayManager mPlatformPayManager;

    @Autowired
    private BatchShopManager mBatchShopMgr;

    public ErrorResult passOrder(String orderno)
    {
        AdEventOrderInfo orderInfo = mEventOrderService.findById(false, orderno);
        if(orderInfo == null)
        {
            return SystemErrorResult.ERR_EXIST_NOT;
        }
        return passOrder(orderInfo);
    }

    public ErrorResult passOrder(AdEventOrderInfo orderInfo)
    {
//        AdEventType eventType = AdEventType.getType(orderInfo.getEventType());
//        if(eventType == AdEventType.SHOP)
//        {
//            handleDeliveryToRealized(orderInfo, true);
//            return SystemErrorResult.SUCCESS;
//        }

        UserInfo userInfo = mUserService.findByUsername(false, orderInfo.getUsername());
        FundAccountType accountType = FundAccountType.Spot;
        ICurrencyType currencyType = ICurrencyType.getSupportCurrency();
        ErrorResult errorResult = mPayApiManager.doBusinessRecharge(accountType, currencyType, BusinessType.AD_ORDER, orderInfo.getNo(), userInfo, orderInfo.getAmount(), null);
        if(errorResult == SystemErrorResult.SUCCESS)
        {
            mEventOrderService.updateInfo(orderInfo, OrderTxStatus.REALIZED);

            mEventOrderService.clearUserPageCache(orderInfo.getUserid());
        }
        return SystemErrorResult.SUCCESS;
    }

    public ErrorResult refuseOrder(String orderno)
    {
        AdEventOrderInfo orderInfo = mEventOrderService.findById(false, orderno);
        if(orderInfo == null)
        {
            return SystemErrorResult.ERR_EXIST_NOT;
        }
        mEventOrderService.updateInfo(orderInfo, OrderTxStatus.FAILED);
        mEventOrderService.clearUserPageCache(orderInfo.getUserid());
        return SystemErrorResult.SUCCESS;
    }

    public ErrorResult buyShop(UserInfo userInfo, AdMaterielInfo materielInfo, long quanlity, MallBuyerAddrInfo buyerAddrInfo, String shopSize)
    {
        MallCommodityInfo commodityInfo = mallCommodityService.findByKey(MyEnvironment.isDev(), materielInfo.getMerchnatid(), materielInfo.getId());
        if(commodityInfo == null)
        {
            return SystemErrorResult.ERR_PARAMS;
        }

        return buyShop(userInfo, materielInfo, commodityInfo, quanlity, buyerAddrInfo, shopSize);
    }
    public ErrorResult buyShop(UserInfo userInfo, AdMaterielInfo materielInfo, MallCommodityInfo commodityInfo, long quanlity, MallBuyerAddrInfo buyerAddrInfo, String shopSize)
    {
        if(materielInfo.getMerchnatid() <= 0)
        {
            return SystemErrorResult.ERR_PARAMS;
        }

        FundAccountType accountType = FundAccountType.Spot;
        ICurrencyType currencyType = ICurrencyType.getSupportCurrency();

        AdCategoryInfo categoryInfo = mCategoryService.findById(false, materielInfo.getCategoryid());
        if(categoryInfo == null)
        {
            return SystemErrorResult.ERR_EXIST_NOT;
        }

        long buyerAddressid = 0;
        String buyerLocation = null;
        String buyerPhone = null;
        if(buyerAddrInfo != null)
        {
            buyerAddressid = buyerAddrInfo.getId();
            buyerLocation = buyerAddrInfo.getLocation();
            buyerPhone = buyerAddrInfo.getPhone();
        }
        else
        {
            buyerLocation = USACityUtils.random();
            buyerPhone = EmailPhoneHelper.nextPhone2();
        }

        BigDecimal totalAmount = new BigDecimal(quanlity).multiply(materielInfo.getPrice()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal brokerage = categoryInfo.getReturnRate().multiply(totalAmount).setScale(2, RoundingMode.HALF_UP);

        JSONObject remark = new JSONObject();
        remark.put("shopSize", shopSize);

        UserInfo merchantInfo = mUserService.findByUsername(false, commodityInfo.getMerchantname());
        InventoryInfo inventoryInfo = mBatchShopMgr.findByUniqueid(merchantInfo, materielInfo);
        String shopFrom = AdEventOrderInfo.SHOP_FROM_VALUE_INVENTORY;
        if(inventoryInfo.getQuantity() < quanlity)
        {
            shopFrom = AdEventOrderInfo.SHOP_FROM_VALUE_BALANCE;
        }

        AdEventOrderInfo orderInfo = new AdEventOrderInfo();
        if(userInfo != null)
        {
            UserMoney userMoney = mUserMoneyService.findMoney(false, userInfo.getId(), accountType, currencyType);
            if(!userMoney.verify(materielInfo.getPrice()))
            {
                return UserErrorResult.ERR_PAY_NOT_ENOUGH_BALANCE;
            }

            UserAttr userAttr = mUserAttrService.find(false, userInfo.getId());

            String orderno = mEventOrderService.createOrderByShop(materielInfo, userAttr, totalAmount, quanlity, brokerage, commodityInfo, buyerAddressid, buyerLocation, buyerPhone, shopFrom, remark);

            ErrorResult errorResult = mPayApiManager.doBusinessDeduct(accountType, currencyType, BusinessType.AD_ORDER, orderno, userInfo, materielInfo.getPrice(), BigDecimal.ZERO, null);
            if(errorResult != SystemErrorResult.SUCCESS)
            {
                return errorResult;
            }


            orderInfo.setNo(orderno);
            orderInfo.setUserid(userInfo.getId());
            mEventOrderService.updateInfo(orderInfo, OrderTxStatus.REALIZED);

            handleDeliveryToPending(userInfo, materielInfo, inventoryInfo, orderno, merchantInfo);
            return errorResult;
        }


        String orderno = mEventOrderService.createOrderByShop(materielInfo, null, totalAmount, quanlity, brokerage, commodityInfo, buyerAddressid, buyerLocation, buyerPhone, shopFrom, remark);
        orderInfo.setNo(orderno);
        mEventOrderService.updateInfo(orderInfo, OrderTxStatus.REALIZED);

        handleDeliveryToPending(userInfo, materielInfo, inventoryInfo, orderno, merchantInfo);
        return SystemErrorResult.SUCCESS;
    }

    private void handleDeliveryToPending(UserInfo userInfo, AdMaterielInfo materielInfo, InventoryInfo inventoryInfo, String orderno, UserInfo merchantInfo)
    {
        FundAccountType accountType = FundAccountType.Spot;
        ICurrencyType currencyType = ICurrencyType.getSupportCurrency();

        AdEventOrderInfo orderInfo = mEventOrderService.findById(false, orderno);

        OrderTxStatus shippingStatus = OrderTxStatus.getType(orderInfo.getShippingStatus());
        if(shippingStatus == null || shippingStatus == OrderTxStatus.NEW)
        {
            BigDecimal totalAmount = orderInfo.getAmount();
            ErrorResult errorResult = null;

            if(inventoryInfo.getQuantity() >= orderInfo.getQuantity())
            {
                errorResult = mBatchShopMgr.handleShopQuantity(merchantInfo, materielInfo, orderInfo.getQuantity(), false);
            }
            else
            {
                UserMoney merchantUserMoney = mUserMoneyService.findMoney(false, orderInfo.getMerchantid(), accountType, currencyType);
                if(!merchantUserMoney.verify(totalAmount))
                {
                    if(shippingStatus == null)
                    {
                        mEventOrderService.updateShippingInfo(orderInfo, OrderTxStatus.NEW);
                    }
                    return;
                }

                // 冻结商户资金
                UserInfo merchatInfo = mUserService.findByUsername(false, orderInfo.getMerchantname());
                errorResult = mPayApiManager.updateUserFreezeAmount(accountType, currencyType, merchatInfo, totalAmount, true);
            }

            if(errorResult == SystemErrorResult.SUCCESS)
            {
                mEventOrderService.updateShippingInfo(orderInfo, OrderTxStatus.PENDING);
            }
            else
            {
                mEventOrderService.updateShippingInfo(orderInfo, OrderTxStatus.NEW);
            }
        }

        if(userInfo != null)
        {
            // 更新订单状态
            mEventOrderService.clearUserPageCache(userInfo.getId());
        }
    }

    public void handleDeliveryToRealized(String orderno, boolean upDelivery)
    {
        AdEventOrderInfo orderInfo = mEventOrderService.findById(false, orderno);
        handleDeliveryToRealized(orderInfo, upDelivery);
    }

    public void handleDeliveryToRealized(AdEventOrderInfo orderInfo, boolean upDelivery)
    {
        if(orderInfo == null)
        {
            return;
        }

        AdEventType eventType = AdEventType.getType(orderInfo.getEventType());
        if(eventType != AdEventType.SHOP)
        {
            return;
        }

        OrderTxStatus txStatus = OrderTxStatus.getType(orderInfo.getShippingStatus());
        if(txStatus == OrderTxStatus.FAILED)
        {
            return;
        }

        if(txStatus != OrderTxStatus.REALIZED)
        {
            //
            mEventOrderService.updateShippingInfo(orderInfo, OrderTxStatus.REALIZED);
            //
            FundAccountType accountType = FundAccountType.Spot;
            ICurrencyType currencyType = ICurrencyType.getSupportCurrency();
            UserInfo merchantinfo = mUserService.findByUsername(false, orderInfo.getMerchantname());
            StringBuilder buffer = new StringBuilder();
            buffer.append("from: shop-return \n");
            buffer.append("order: ").append(orderInfo.getNo());
            mPlatformPayManager.addPresentation(accountType, currencyType,merchantinfo, orderInfo.getBrokerage(), null, buffer.toString());
            //
            mPayApiManager.updateUserFreezeAmount(accountType, currencyType, merchantinfo, orderInfo.getAmount(), false);

            // clear
            mEventOrderService.clearUserPageCache(orderInfo.getUserid());
        }

        if(upDelivery)
        {
            mallDeliveryService.updateStatus(orderInfo.getNo(), Status.ENABLE);
        }
    }

    public void handleDeliveryToFailed(String orderno, boolean upDelivery)
    {
        AdEventOrderInfo orderInfo = mEventOrderService.findById(false, orderno);
        if(orderInfo == null)
        {
            return;
        }

        AdEventType eventType = AdEventType.getType(orderInfo.getEventType());
        if(eventType != AdEventType.SHOP)
        {
            return;
        }

        OrderTxStatus txStatus = OrderTxStatus.getType(orderInfo.getShippingStatus());
        if(txStatus == OrderTxStatus.REALIZED)
        {
            return;
        }

        if(txStatus != OrderTxStatus.FAILED)
        {
            mEventOrderService.updateInfo(orderInfo, OrderTxStatus.REALIZED);
            //
            mEventOrderService.updateShippingInfo(orderInfo, OrderTxStatus.FAILED);

            if(AdEventOrderInfo.SHOP_FROM_VALUE_BALANCE.equalsIgnoreCase(orderInfo.getShopFrom()))
            {
                //
                FundAccountType accountType = FundAccountType.Spot;
                ICurrencyType currencyType = ICurrencyType.getSupportCurrency();
                UserInfo merchantinfo = mUserService.findByUsername(false, orderInfo.getMerchantname());
                mPayApiManager.updateUserFreezeAmount(accountType, currencyType, merchantinfo, orderInfo.getAmount(), false);
            }
            else
            {
                UserInfo userInfo = mUserService.findByUsername(false, orderInfo.getMerchantname());
                AdMaterielInfo materielInfo = materielService.findById(false, orderInfo.getMaterielId());
                mBatchShopMgr.handleShopQuantity(userInfo, materielInfo, orderInfo.getQuantity(), true);
            }

            // clear
            mEventOrderService.clearUserPageCache(orderInfo.getUserid());
        }

        if(upDelivery)
        {
            mallDeliveryService.updateStatus(orderInfo.getNo(), Status.ENABLE);
        }
    }

    public void test()
    {
        AdMaterielInfo materielInfo = materielService.findById(false, 18634);
        materielInfo.setMerchnatid(32);
        materielInfo.setMerchantname("c_0x369d18Bc8822D3968360345a1907e3d570E16771");
//        buyShop(null, materielInfo, 2, null, null);


        DispatchOrderManager.testRun();
    }

    public static void testRun()
    {
        AdOrderManager mgr = SpringContextUtils.getBean(AdOrderManager.class);
        mgr.test();

    }


}

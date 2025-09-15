package com.inso.modules.ad.mall.logical;

import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.context.MyEnvironment;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.zookeeper.DistributeLock;
import com.inso.framework.zookeeper.ZKClientManager;
import com.inso.modules.ad.core.model.AdMaterielInfo;
import com.inso.modules.ad.core.service.MaterielService;
import com.inso.modules.ad.mall.model.InventoryInfo;
import com.inso.modules.ad.mall.model.MallCommodityInfo;
import com.inso.modules.ad.mall.service.InventoryService;
import com.inso.modules.ad.mall.service.PurchaseOrderService;
import com.inso.modules.common.model.FundAccountType;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.UserErrorResult;
import com.inso.modules.passport.money.PayApiManager;
import com.inso.modules.passport.money.model.UserMoney;
import com.inso.modules.passport.money.service.UserMoneyService;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class BatchShopManager {

    private static Log LOG = LogFactory.getLog(BatchShopManager.class);

    private static final String LOCK_PATH = "/inso/ad/mall/update_investory_";


    @Autowired
    private UserService mUserService;
    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private UserMoneyService userMoneyService;

    @Autowired
    private InventoryService mInventoryService;

    @Autowired
    private MaterielService materielService;


    @Autowired
    private PurchaseOrderService mPurchaseOrderService;

    @Autowired
    private PayApiManager payApiManager;


    public ErrorResult batchBuy(MallCommodityInfo entity, long quantity)
    {
        String lockPath = getLockPath(entity.getMerchantname(), entity.getMaterielid());
        DistributeLock lock = ZKClientManager.getInstanced().createLock(lockPath);

        FundAccountType accountType = FundAccountType.Spot;
        ICurrencyType currencyType = ICurrencyType.getSupportCurrency();
        UserMoney userMoney = userMoneyService.findMoney(false, entity.getMerchantid(), accountType, currencyType);

        AdMaterielInfo materielInfo = materielService.findById(false, entity.getMaterielid());
        if(materielInfo == null)
        {
            return SystemErrorResult.ERR_EXIST_NOT;
        }

        BigDecimal totalAmount = new BigDecimal(quantity).multiply(materielInfo.getPrice());
        BigDecimal realAmount = totalAmount;
        if(!userMoney.verify(totalAmount))
        {
            return UserErrorResult.ERR_PAY_NOT_ENOUGH_BALANCE;
        }

        try {

            UserAttr userAttr = mUserAttrService.find(false, entity.getMerchantid());
            String orderno = mPurchaseOrderService.addOrder(userAttr, materielInfo, materielInfo.getPrice(), quantity, totalAmount, realAmount);
            if(lock.lockAcquired())
            {
                UserInfo userInfo = mUserService.findByUsername(false, entity.getMerchantname());

                InventoryInfo inventoryInfo = findByUniqueid(userInfo, materielInfo);

                if(payApiManager.updateUserFreezeAmount(accountType, currencyType, userInfo, realAmount, true) != SystemErrorResult.SUCCESS)
                {
                    mPurchaseOrderService.updateInfo(orderno, OrderTxStatus.FAILED, null);
                    return SystemErrorResult.ERR_SYS_OPT_FAILURE;
                }

                long newQuantity = inventoryInfo.getQuantity() + quantity;
                mInventoryService.updateInfo(inventoryInfo, null, newQuantity, null);
                mPurchaseOrderService.updateInfo(orderno, OrderTxStatus.REALIZED, null);
                return SystemErrorResult.SUCCESS;
            }
            else
            {
                mPurchaseOrderService.updateInfo(orderno, OrderTxStatus.FAILED, null);
            }
        } catch (Exception e) {
            LOG.error("handle batch buy error:", e);
        } finally {
            lock.lockReleased();
        }

        return SystemErrorResult.ERR_SYS_OPT_FAILURE;
    }

    private String getLockPath(String username, long materielid)
    {
        return LOCK_PATH + username + materielid;
    }

    public ErrorResult handleShopQuantity(UserInfo userInfo, AdMaterielInfo materielInfo, long quantity, boolean isIncre)
    {
        String lockPath = getLockPath(userInfo.getName(), materielInfo.getId());
        DistributeLock lock = ZKClientManager.getInstanced().createLock(lockPath);
        try {
            if(lock.lockAcquired())
            {
                long newQuantity = -1;
                InventoryInfo inventoryInfo = findByUniqueid(userInfo, materielInfo);
                if(isIncre)
                {
                    newQuantity = quantity + inventoryInfo.getQuantity();
                }
                else
                {
                    if(inventoryInfo.getQuantity() < quantity)
                    {
                        return SystemErrorResult.ERR_SYS_OPT_FAILURE;
                    }
                    newQuantity = inventoryInfo.getQuantity() - quantity;
                }
                mInventoryService.updateInfo(inventoryInfo, null, newQuantity, null);
                return SystemErrorResult.SUCCESS;
            }
        } catch (Exception e) {
            LOG.error("handle error:", e);
        } finally {
            lock.lockReleased();
        }
        return SystemErrorResult.ERR_SYS_OPT_FAILURE;
    }

    public InventoryInfo findByUniqueid(UserInfo userInfo, AdMaterielInfo materielInfo)
    {
        InventoryInfo inventoryInfo = mInventoryService.findByUseridAndMaterielid(MyEnvironment.isDev(), userInfo.getId(), materielInfo.getId());
        if(inventoryInfo == null)
        {
            mInventoryService.addOrder(userInfo, materielInfo, 0, Status.ENABLE, null);
            inventoryInfo = mInventoryService.findByUseridAndMaterielid(false, userInfo.getId(), materielInfo.getId());
        }
        return inventoryInfo;
    }

}

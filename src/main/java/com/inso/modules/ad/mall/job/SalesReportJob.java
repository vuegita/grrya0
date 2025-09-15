package com.inso.modules.ad.mall.job;

import com.alibaba.druid.util.LRUCache;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.service.Callback;
import com.inso.framework.spring.SpringContextUtils;
import com.inso.framework.utils.DateUtils;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.UUIDUtils;
import com.inso.modules.ad.core.model.AdEventOrderInfo;
import com.inso.modules.ad.core.model.AdEventType;
import com.inso.modules.ad.core.service.EventOrderService;
import com.inso.modules.ad.mall.model.MallStoreInfo;
import com.inso.modules.ad.mall.model.SalesReport;
import com.inso.modules.ad.mall.service.MallStoreService;
import com.inso.modules.ad.mall.service.MerchantSalesReportService;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.web.SystemRunningMode;
import org.joda.time.DateTime;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Date;

public class SalesReportJob implements Job {

    private Log LOG = LogFactory.getLog(SalesReportJob.class);

    private static final String ROOT_CACHE = SalesReportJob.class.getName();


    private EventOrderService mEventOrdereService;
    private MallStoreService mallStoreService;
    private UserAttrService mUserAttrService;
    private MerchantSalesReportService merchantSalesReportService;


    public SalesReportJob()
    {
        this.mEventOrdereService = SpringContextUtils.getBean(EventOrderService.class);
        this.mallStoreService = SpringContextUtils.getBean(MallStoreService.class);
        this.mUserAttrService = SpringContextUtils.getBean(UserAttrService.class);
        this.merchantSalesReportService = SpringContextUtils.getBean(MerchantSalesReportService.class);
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        if(!SystemRunningMode.isFundsMode())
        {
            return;
        }

        DateTime dateTime = new DateTime(context.getFireTime());
        handleTask(dateTime.minusDays(1));
    }

    private void handleTask(DateTime dateTime)
    {

        String pdateStr = dateTime.toString(DateUtils.TYPE_YYYY_MM_DD);
        Date pdate = DateUtils.convertDate(DateUtils.TYPE_YYYY_MM_DD, pdateStr);

        DateTime fromTime = dateTime.withTime(0, 0, 0, 0);
        DateTime toTime = dateTime.withTime(23, 59, 59, 0);

        LRUCache<String, SalesReport> lruCache = new LRUCache<String, SalesReport>(100);

        String rootCache = ROOT_CACHE + dateTime.getDayOfYear() + UUIDUtils.getUUID();

        mEventOrdereService.queryAll(fromTime, toTime, AdEventType.SHOP, new Callback<AdEventOrderInfo>() {
            @Override
            public void execute(AdEventOrderInfo orderInfo) {

                OrderTxStatus txStatus = OrderTxStatus.getType(orderInfo.getStatus());
                if(txStatus == OrderTxStatus.REALIZED || txStatus == OrderTxStatus.FAILED)
                {
                    String cachekey = rootCache + orderInfo.getMerchantname();
                    SalesReport report = getReport(cachekey, lruCache, orderInfo.getMerchantname());
                    report.incre(txStatus, orderInfo);
                    CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(report));
                }

            }
        });

        mallStoreService.queryAll(new Callback<MallStoreInfo>() {
            @Override
            public void execute(MallStoreInfo o) {

                try {
                    String cachekey = rootCache + o.getUsername();
                    SalesReport report = getReport(cachekey, lruCache, o.getUsername());
                    if(report == null)
                    {
                        return;
                    }

                    UserAttr userAttr = mUserAttrService.find(false, o.getUserid());
                    merchantSalesReportService.delete(userAttr.getUserid(), pdate);
                    merchantSalesReportService.addReport(userAttr,
                            report.getTotalAmount(), report.getTotalCount(),
                            report.getRefundAmount(), report.getRefundCount(),
                            report.getReturnAmount(), pdate);

                } catch (Exception e) {
                    LOG.error("handle error:", e);
                }

            }
        });

    }

    private SalesReport getReport(String cachekey, LRUCache<String, SalesReport> lruCache, String merchantname)
    {
        SalesReport report = lruCache.get(merchantname);
        if(report == null)
        {
            report = CacheManager.getInstance().getObject(cachekey, SalesReport.class);
            lruCache.put(merchantname, report);
        }

        if(report == null)
        {
            report = new SalesReport();
            report.init();
            lruCache.put(merchantname, report);
        }

        return report;
    }

    public void test()
    {
        DateTime dateTime = DateTime.now();
        handleTask(dateTime);
    }

}

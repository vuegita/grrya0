package com.inso.modules.ad.core.job;

import com.inso.framework.context.MyEnvironment;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.service.Callback;
import com.inso.framework.spring.SpringContextUtils;
import com.inso.framework.utils.RegexUtils;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.ad.AdDBUpdate;
import com.inso.modules.ad.core.model.AdMaterielInfo;
import com.inso.modules.ad.core.service.MaterielService;
import com.inso.modules.common.model.Status;
import com.inso.modules.web.SystemRunningMode;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.concurrent.atomic.AtomicInteger;

public class DbInitDataJob implements Job {

    private static Log LOG = LogFactory.getLog(DbInitDataJob.class);


    private AdDBUpdate mAdDBUpdate;

    private MaterielService materielService;

    public DbInitDataJob()
    {
        this.mAdDBUpdate = SpringContextUtils.getBean(AdDBUpdate.class);
        this.materielService = SpringContextUtils.getBean(MaterielService.class);
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        SystemRunningMode mode = SystemRunningMode.getSystemConfig();
        if(MyEnvironment.isDev() || mode == SystemRunningMode.FUNDS)
        {
            mAdDBUpdate.update();

            //clearSpecialChar();
        }
    }


    public void clearSpecialChar()
    {
        String clearStr = "�";
        String clearStr2 = "????";

        String replaceStr1 = "??";

        AtomicInteger count = new AtomicInteger();
        materielService.queryAll(new Callback<AdMaterielInfo>() {
            @Override
            public void execute(AdMaterielInfo orderInfo) {

                try {
                    Status status = Status.getType(orderInfo.getStatus());
                    if(status != Status.ENABLE)
                    {
                        return;
                    }


                    if(RegexUtils.isChinese(orderInfo.getName()))
                    {
                        count.incrementAndGet();
                        materielService.updateInfo(orderInfo, null, null, Status.DISABLE, null, null, null, null, null);
                        return;
                    }

                    String name = orderInfo.getName();
                    if( name.contains(clearStr2) || name.contains(clearStr))
                    {
                        count.incrementAndGet();
                        materielService.updateInfo(orderInfo, null, null, Status.DISABLE, null, null, null, null, null);
                        return;
                    }

                    boolean isUpdate = false;
                    if(name.contains(replaceStr1))
                    {
                        isUpdate = true;
                        name = name.replace(replaceStr1, StringUtils.getEmpty());
                    }

                    String desc = null;
                    if(orderInfo.getDesc().contains(replaceStr1))
                    {
                        isUpdate = true;
                        desc = orderInfo.getDesc().replace(replaceStr1, StringUtils.getEmpty());
                    }

                    if(isUpdate)
                    {
                        count.incrementAndGet();
                        materielService.updateInfo(orderInfo, name, desc, null, null, null, null, null, null);
                    }

                } catch (Exception e) {
                    LOG.error("handle error:", e);
                }

            }
        });


        LOG.info("handle special count = " + count.get());
    }


    public static void main(String[] args) {
        String name = "Drivvo �C Car management, Fuel log, Find Cheap Gas";

        String clearStr = "�";
        System.out.println(name.contains(clearStr));
    }
}

package com.inso.modules.report.logical;

import com.inso.modules.common.model.BusinessType;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.report.job.GameBusinessJob;
import com.inso.modules.report.model.GameBusinessDay;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;

@Component
public class GameBusinessDayManager {

//    private static Log LOG = LogFactory.getLog(GameBusinessDayManager.class);
//
//    @Autowired
//    private GameBusinessDayService mGameBusinessDayService;

    //@Async
    public void updateBetLog(Date pdate, UserAttr userAttr, BusinessType businessType, BigDecimal amount, BigDecimal feemoney)
    {
        updateLogByMessage(pdate, userAttr, businessType, amount, 1, feemoney, null, 0);
    }

    //@Async
    public void updateWinLog(Date pdate, UserAttr userAttr, BusinessType businessType, BigDecimal amount)
    {
        updateLogByMessage(pdate, userAttr, businessType, null,0, null, amount, 1);
    }

    private void updateLogByMessage(Date pdate, UserAttr userAttr, BusinessType businessType, BigDecimal betAmount, long betCount, BigDecimal feemoney, BigDecimal winAmount, long winCount)
    {
        GameBusinessDay businessDay = new GameBusinessDay();
        businessDay.setPdate(pdate);
        businessDay.setAgentid(userAttr.getAgentid());
        businessDay.setAgentname(userAttr.getAgentname());
        businessDay.setStaffid(userAttr.getDirectStaffid());
        businessDay.setStaffname(userAttr.getDirectStaffname());
        businessDay.setBusinessCode(businessType.getCode());
        businessDay.setBusinessName(businessType.getKey());
        businessDay.setBetAmount(betAmount);
        businessDay.setBetCount(betCount);
        businessDay.setWinAmount(winAmount);
        businessDay.setWinCount(winCount);
        businessDay.setFeemoney(feemoney);

        GameBusinessJob.sendMessage(businessDay);

    }

    /**
     * 如果并发严重-日后再使用消息队列
     * @param pdate
     * @param userAttr
     * @param businessType
     * @param betAmount
     * @param betCount
     * @param winAmount
     * @param winCount
     */
//    private void updateLog(Date pdate, UserAttr userAttr, BusinessType businessType, BigDecimal betAmount, long betCount, BigDecimal feemoney, BigDecimal winAmount, long winCount)
//    {
//        try {
//        String pdateString = DateUtils.convertString(pdate, DateUtils.TYPE_YYYYMMDD);
//        Date date = DateUtils.convertDate(DateUtils.TYPE_YYYYMMDD, pdateString);
//
//
//            synchronized (businessType)
//            {
//                mGameBusinessDayService.updateLog(pdate, userAttr.getAgentid(), userAttr.getAgentname(), userAttr.getDirectStaffid(), userAttr.getDirectStaffname(), businessType, betAmount, betCount, feemoney, winAmount, winCount);
//            }
//        } catch (Exception e) {
//            LOG.error("updateLog error:", e);
//        }
//    }
}

package com.inso.modules.game.red_package.service;

import java.math.BigDecimal;
import java.util.List;

import org.joda.time.DateTime;

import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.RemarkVO;
import com.inso.modules.game.model.GamePeriodStatus;
import com.inso.modules.game.red_package.model.RedPCreatorType;
import com.inso.modules.game.red_package.model.RedPPeriodInfo;
import com.inso.modules.game.red_package.model.RedPType;
import com.inso.modules.passport.user.model.UserInfo;

public interface RedPPeriodService {
    public long addByMember(RedPCreatorType creatorType, RedPType type, String orderno, UserInfo userInfo, long agentid,
                            BigDecimal totalAmount, long totalCount, BigDecimal minAmount, BigDecimal maxAmount,
                            DateTime startTime, DateTime endTime, RemarkVO remark);

    public long addBySystem(RedPType type, BigDecimal totalAmount, long totalCount, BigDecimal maxAmount, DateTime startTime, DateTime endTime, RemarkVO remark);

    public void updateAmount(long id, BigDecimal betAmount, BigDecimal winAmount, BigDecimal feeAmount, long betCount, long winCount);

    public void updateStatus(long id, GamePeriodStatus status);

    public void updateStatusToFinish(RedPPeriodInfo periodInfo, long openResult);
//    public void updateOpenResult(long id, RedPBetItemType openResult, GameOpenMode mode);

    public RedPPeriodInfo findByIssue(boolean purge, long id);

    public List<RedPPeriodInfo> queryByTime(RedPType type, String beginTime, String endTime, int limit);

    public void queryAllByCreatetime(RedPType type, String startTimeString, String endTimeString, Callback<RedPPeriodInfo> callback);

    public void queryAllByUpdatetime(RedPType type, String startTimeString, String endTimeString, Callback<RedPPeriodInfo> callback);

    public RowPager<RedPPeriodInfo> queryScrollPage(PageVo pageVo, long id, long userid, RedPType type, GamePeriodStatus status);

}

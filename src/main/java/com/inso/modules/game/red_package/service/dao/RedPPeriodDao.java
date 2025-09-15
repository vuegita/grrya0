package com.inso.modules.game.red_package.service.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.inso.framework.bean.PageVo;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.RemarkVO;
import com.inso.modules.game.model.GameOpenMode;
import com.inso.modules.game.model.GamePeriodStatus;
import com.inso.modules.game.red_package.model.RedPCreatorType;
import com.inso.modules.game.red_package.model.RedPPeriodInfo;
import com.inso.modules.game.red_package.model.RedPType;

public interface RedPPeriodDao {

    public long add(RedPCreatorType creatorType, RedPType type, String orderno, long userid, String username, long agentId,
                    BigDecimal totalAmount, long totalCount, BigDecimal minAmount, BigDecimal maxAmount,
                    Date startTime, Date endTime, RemarkVO remark);

    public void updateAmount(long id, BigDecimal betAmount, BigDecimal winAmount, BigDecimal feeAmount, long betCount, long winCount);

    public void updateStatus(long id, GamePeriodStatus status);

    public void updateOpenResult(long id, long openResult, GameOpenMode mode);

    public RedPPeriodInfo findById(long id);

    public List<RedPPeriodInfo> queryByTime(RedPType type, String beginTime, String endTime, int limit);

    public void queryAll(RedPType type, String startTimeString, String endTimeString, Callback<RedPPeriodInfo> callback);
    public void queryAllByUpdateTime(RedPType type, String startTimeString, String endTimeString, Callback<RedPPeriodInfo> callback);

    public RowPager<RedPPeriodInfo> queryScrollPage(PageVo pageVo, long id, long userid, RedPType type, GamePeriodStatus status);
}

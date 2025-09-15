package com.inso.modules.report;

import com.inso.framework.service.Callback;
import com.inso.modules.game.GameChildType;
import com.inso.modules.report.model.GameBusinessDay;
import org.joda.time.DateTime;

public interface GameBusinessStatsService {

    public void queryAllMemberByTime(GameChildType lotteryType, String startTimeString, String endTimeString, Callback<GameBusinessDay> callback);

    public void statsAllMemberByTime(GameChildType lotteryType, DateTime from, DateTime toTime, Callback<GameBusinessDay> callback);
}

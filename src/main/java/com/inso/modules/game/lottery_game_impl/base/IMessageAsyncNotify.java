package com.inso.modules.game.lottery_game_impl.base;

import com.inso.framework.bean.ErrorResult;
import com.inso.modules.passport.user.model.UserInfo;

import java.math.BigDecimal;

public interface IMessageAsyncNotify {

    public void onBetFinish(String sessionid, ErrorResult result, String orderno, UserInfo userInfo, BigDecimal betAmount, String[] betItemArr);

    public void close();

}

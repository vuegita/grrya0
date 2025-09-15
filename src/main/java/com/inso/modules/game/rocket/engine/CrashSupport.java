package com.inso.modules.game.rocket.engine;

import com.inso.modules.game.rocket.logical.RocketPeriodStatus;

public interface CrashSupport {

    public boolean support(RocketPeriodStatus status);

    public boolean verify(float openResult, RocketPeriodStatus status);


}

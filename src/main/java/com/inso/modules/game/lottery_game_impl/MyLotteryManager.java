package com.inso.modules.game.lottery_game_impl;

import com.google.common.collect.Maps;
import com.inso.framework.spring.SpringContextUtils;
import com.inso.modules.game.GameChildType;
import com.inso.modules.game.lottery_game_impl.btc_kline.BTCKlineProcessorImpl;
import com.inso.modules.game.lottery_game_impl.btc_kline.model.BTCKlineType;
import com.inso.modules.game.lottery_game_impl.football.FootballProcessorImpl;
import com.inso.modules.game.lottery_game_impl.football.model.FootballType;
import com.inso.modules.game.lottery_game_impl.mines.MinesProcessorImpl;
import com.inso.modules.game.lottery_game_impl.mines.model.MineType;
import com.inso.modules.game.lottery_game_impl.rg2.RedGreen2ProcessorImpl;
import com.inso.modules.game.lottery_game_impl.rg2.model.RedGreen2Type;
import com.inso.modules.game.lottery_game_impl.turntable.TurntableProcessorImpl;
import com.inso.modules.game.lottery_game_impl.turntable.model.TurnTableType;

import java.util.Map;

public class MyLotteryManager {


    private Map<GameChildType, BaseLotterySupport> maps = Maps.newHashMap();

    private interface MyInternal {
        public MyLotteryManager mgr = new MyLotteryManager();
    }

    private MyLotteryManager()
    {
        addOpenProcessor(TurnTableType.mArr, TurntableProcessorImpl.class);
        addOpenProcessor(BTCKlineType.mArr, BTCKlineProcessorImpl.class);
        addOpenProcessor(RedGreen2Type.mArr, RedGreen2ProcessorImpl.class);
        addOpenProcessor(FootballType.mArr, FootballProcessorImpl.class);
        addOpenProcessor(MineType.mArr, MinesProcessorImpl.class);
    }


    public static MyLotteryManager getInstance()
    {
        return MyInternal.mgr;
    }


    private void addOpenProcessor(GameChildType[] arr, Class<? extends BaseLotterySupport> clazz)
    {
        BaseLotterySupport processor = SpringContextUtils.getBean(clazz);
        for(GameChildType tmp : arr)
        {
            maps.put(tmp, processor);
        }
    }

    public BaseLotterySupport getOpenProcessor(GameChildType childType)
    {
        return maps.get(childType);
    }


}

package com.inso.modules.game.lottery_game_impl.helper;

import com.inso.framework.conf.MyConfiguration;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameBetTaskHelper {

    private static Log LOG = LogFactory.getLog(GameBetTaskHelper.class);


    private static ExecutorService mThreadPool;
    static {
        MyConfiguration conf = MyConfiguration.getInstance();
        int count = conf.getInt("game.basic.bet.processor.count");
        if(count <= 10)
        {
            count = 10;
        }
        mThreadPool = Executors.newFixedThreadPool(count);
        LOG.info("game-bet-cpu-processor-count: " + count);
    }


    public static void execTask(Runnable runnable)
    {
        mThreadPool.submit(runnable);
    }

}

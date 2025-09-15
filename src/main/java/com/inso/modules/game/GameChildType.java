package com.inso.modules.game;

import com.inso.modules.game.lottery_game_impl.btc_kline.model.BTCKlineType;
import com.inso.modules.game.lottery_game_impl.football.model.FootballType;
import com.inso.modules.game.lottery_game_impl.mines.model.MineType;
import com.inso.modules.game.lottery_game_impl.pg.model.PgGameType;
import com.inso.modules.game.lottery_game_impl.rg2.model.RedGreen2Type;
import com.inso.modules.game.lottery_game_impl.turntable.model.TurnTableType;
import com.inso.modules.game.model.GameCategory;
import com.inso.modules.game.rg.model.LotteryRGType;
import com.inso.modules.game.rocket.model.RocketType;

/**
 * 游戏子分类类型
 */
public interface GameChildType {

    public String getKey();
    public String getTitle();
    public int getCode();

    public int getTotalSeconds();
    public long getDisableMilliSeconds();
    public int getDisableSecond();

    public GameCategory getCategory();
    public boolean verifyBetItem(String betItem);
    public boolean verifyBetItem(String[] betItem, boolean fromApi);
    public String randomBetItem();

    public boolean autoCreateIssue();
    public boolean uniqueOpenResult();
    public boolean enableBetNumber();
    public boolean enableRobotBet();

    /**
     * 是否自动运行还是自定义运行
     * @return
     */
    public boolean autoBoot();


    public static GameChildType getType(String key)
    {
        GameChildType type = TurnTableType.getType(key);
        if(type == null)
        {
            type = BTCKlineType.getType(key);
        }

        if(type == null)
        {
            type = RedGreen2Type.getType(key);
        }

        if(type == null)
        {
            type = LotteryRGType.getType(key);
        }

        if(type == null)
        {
            type = RocketType.getType(key);
        }

        if(type == null)
        {
            type = FootballType.getType(key);
        }

        if(type == null)
        {
            type = MineType.getType(key);
        }

        if(type == null)
        {
            type = PgGameType.getType(key);
        }

        return type;

    }

}

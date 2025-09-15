package com.inso.bootstrap.single;

import com.google.common.collect.Maps;
import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.context.MyEnvironment;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.utils.CollectionUtils;
import com.inso.framework.utils.MD5;
import com.inso.framework.utils.RandomStringUtils;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.game.GameChildType;
import com.inso.modules.game.andar_bahar.model.ABType;
import com.inso.modules.game.fruit.model.FruitType;
import com.inso.modules.game.lottery_game_impl.btc_kline.model.BTCKlineType;
import com.inso.modules.game.lottery_game_impl.football.model.FootballType;
import com.inso.modules.game.lottery_game_impl.mines.model.MineType;
import com.inso.modules.game.lottery_game_impl.pg.model.PgGameType;
import com.inso.modules.game.lottery_game_impl.rg2.model.RedGreen2Type;
import com.inso.modules.game.model.GameInfo;
import com.inso.modules.game.model.GameCategory;
import com.inso.modules.game.rg.model.LotteryRGType;
import com.inso.modules.game.rocket.model.RocketType;
import com.inso.modules.game.service.GameService;
import com.inso.modules.game.lottery_game_impl.turntable.model.TurnTableType;
import com.inso.modules.passport.user.logical.RelationManager;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 *
 */
@Component
@Order(value=1)
public class ServerRunner implements CommandLineRunner {

    private static Log LOG = LogFactory.getLog(ServerRunner.class);

    @Autowired
    private GameService mGameService;

    @Autowired
    private UserService mUserService;

    @Autowired
    private UserAttrService userAttrService;

    @Autowired
    private RelationManager mRelationMgr;

    @Autowired
    private UserAttrService mUserAttrService;

    @Override
    public void run(String... args) throws Exception
    {
        try {
            startTask();
        } catch (Exception e) {
            LOG.error("handle error:", e);
        }
    }

    @Async
    public void startTask() {
        List<GameInfo> allList = mGameService.queryAll(true);
        Map<String, String> maps = Maps.newHashMap();


        if(!CollectionUtils.isEmpty(allList))
        {
            for(GameInfo game : allList)
            {
                maps.put(game.getKey(), StringUtils.getEmpty());
            }
        }

        // lottery
//        LotteryRGType[] values = LotteryRGType.values();
//        for(LotteryRGType type : values)
//        {
//            if(!maps.containsKey(type.getKey()))
//            {
//                mGameService.add(GameCategory.LOTTERY_RG, type.getKey(), type.getTitle(), type.getDescribe(), type.getIcon());
//            }
//        }

        // Turntable
        TurnTableType[] turntableArr = TurnTableType.values();
        for(TurnTableType type : turntableArr)
        {
            if(!maps.containsKey(type.getKey()))
            {
                mGameService.add(GameCategory.TURNTABLE, type.getKey(), type.getTitle(), type.getDescribe(), type.getIcon());
            }
        }
//
//        // Rocket
        RocketType[] rocketArr = RocketType.values();
        for(RocketType type : rocketArr)
        {
            if(!maps.containsKey(type.getKey()))
            {
                mGameService.add(GameCategory.ROCKET, type.getKey(), type.getTitle(), type.getDescribe(), type.getIcon());
            }
        }

        // BTC
        for(GameChildType type : BTCKlineType.mArr)
        {
            if(!maps.containsKey(type.getKey()))
            {
                mGameService.add(type.getCategory(), type.getKey(), type.getTitle(), StringUtils.getEmpty(), StringUtils.getEmpty());
            }
        }

        // Football
        for(GameChildType type : FootballType.mArr)
        {
            if(!maps.containsKey(type.getKey()))
            {
                mGameService.add(type.getCategory(), type.getKey(), type.getTitle(), StringUtils.getEmpty(), StringUtils.getEmpty());
            }
        }
        for(GameChildType type : MineType.mArr)
        {
            if(!maps.containsKey(type.getKey()))
            {
                mGameService.add(type.getCategory(), type.getKey(), type.getTitle(), StringUtils.getEmpty(), StringUtils.getEmpty());
            }
        }

        for(GameChildType type : PgGameType.mArr)
        {
            if(!maps.containsKey(type.getKey()))
            {
                mGameService.add(type.getCategory(), type.getKey(), type.getTitle(), StringUtils.getEmpty(), StringUtils.getEmpty());
            }
        }

        // new Red-green
        for(GameChildType type : RedGreen2Type.mArr)
        {
            if(!maps.containsKey(type.getKey()))
            {
                mGameService.add(type.getCategory(), type.getKey(), type.getTitle(), StringUtils.getEmpty(), StringUtils.getEmpty());
            }
        }

        //
//        ABType[] abTypes = ABType.values();
//        for(ABType type : abTypes)
//        {
//            if(!maps.containsKey(type.getKey()))
//            {
//                mGameService.add(GameCategory.ANDAR_BAHAR, type.getKey(), type.getTitle(), type.getDescribe(), null);
//            }
//        }


//        FruitType[] fruitTypes = FruitType.values();
//        for(FruitType type : fruitTypes)
//        {
//            if(!maps.containsKey(type.getKey()))
//            {
//                mGameService.add(GameCategory.FRUIT, type.getKey(), type.getTitle(), type.getDescribe(), null);
//            }
//        }

        addUser(UserInfo.DEFAULT_GAME_SYSTEM_AGENT, null, UserInfo.UserType.AGENT);
        addUser(UserInfo.DEFAULT_GAME_SYSTEM_STAFF, null, UserInfo.UserType.STAFF);
        UserInfo agentInfo = mUserService.findByUsername(true, UserInfo.DEFAULT_GAME_SYSTEM_AGENT);
            // 如果注意类型是员工，要绑定
        bindAgentAndStaff(UserInfo.DEFAULT_GAME_SYSTEM_STAFF, agentInfo);


        //addUser(UserInfo.DEFAULT_SYSTEM_ACCOUNT, "8888888888", UserInfo.UserType.AGENT);
        addUser(UserInfo.DEFAULT_SYSTEM_ACCOUNT, null, UserInfo.UserType.MEMBER);
        addUser(UserInfo.DEFAULT_GAME_TEST_ACCOUNT, null, UserInfo.UserType.TEST);
        addUser("gametest01", null, UserInfo.UserType.TEST);
        addUser("gametest02", null, UserInfo.UserType.TEST);
        addUser("gametest03", null, UserInfo.UserType.TEST);
        addUser("gametestff", null, UserInfo.UserType.TEST);
        addUser("gametesthh", null, UserInfo.UserType.TEST);
        addUser("gametestll", null, UserInfo.UserType.TEST);
        addUser("gametestqq", null, UserInfo.UserType.TEST);
        addUser("gametestyw", null, UserInfo.UserType.TEST);
        addUser("gametestalan", null, UserInfo.UserType.TEST);
        addUser("gametestwf", null, UserInfo.UserType.TEST);
        addUser("gametestwf2", null, UserInfo.UserType.TEST);

        addUser("gametestyf", null, UserInfo.UserType.TEST);
        addUser("gametestqe", null, UserInfo.UserType.TEST);
        addUser("gametestkk", null, UserInfo.UserType.TEST);


        if(MyEnvironment.isDev())
        {
            addUser(UserInfo.DEFAULT_GAME_TEST_AGENT, null, UserInfo.UserType.AGENT);
            addUser("test01", null, UserInfo.UserType.MEMBER);
            addUser("test02", null, UserInfo.UserType.MEMBER);

//            addUser(UserInfo.DEFAULT_GAME_SYSTEM_AGENT, "7777777777", UserInfo.UserType.AGENT);
//            addUser(UserInfo.DEFAULT_GAME_SYSTEM_STAFF, "6666666666", UserInfo.UserType.STAFF);
        }
    }

    private void addUser(String username, String phone, UserInfo.UserType userType)
    {
        try {
            boolean purge = true;
            UserInfo userInfo = mUserService.findByUsername(purge, username);
            if(userInfo != null)
            {
                return;
            }

            if(StringUtils.isEmpty(phone))
            {
                phone = RandomStringUtils.generator0_9(10);
            }

            phone = "00_" + phone;

            String email = username + "@gmail.com";
            String pwd = MD5.encode("Abc@123");
            mUserService.addUserByThirdCoin(username, pwd, phone, email, userType, null, "127.0.0.1",null);

        } catch (Exception e) {
            LOG.error("add User error:", e);
        }
    }

    private void bindAgentAndStaff(String regUsername, UserInfo agentInfo)
    {
        // check
        UserInfo.UserType agentType = UserInfo.UserType.getType(agentInfo.getType());

        if(agentType != UserInfo.UserType.AGENT)
        {
            return;
        }

        UserInfo childUserInfo = mUserService.findByUsername(true, regUsername);
        UserAttr userAttr = mUserAttrService.find(true, childUserInfo.getId());
        if(userAttr == null || userAttr.getAgentid() > 0)
        {
            return;
        }

        ErrorResult errorResult = mRelationMgr.moveRelation(agentInfo, childUserInfo);
        if(SystemErrorResult.SUCCESS == errorResult)
        {
            mUserAttrService.updateStaffAndAgent(childUserInfo.getId(), null, -1, agentInfo.getName(), agentInfo.getId());
        }
    }


}


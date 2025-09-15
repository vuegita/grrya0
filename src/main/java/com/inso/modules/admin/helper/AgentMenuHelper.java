package com.inso.modules.admin.helper;


import com.inso.framework.context.MyEnvironment;
import com.inso.framework.spring.SpringContextUtils;
import com.inso.modules.admin.core.model.Menu;
import com.inso.modules.common.model.Status;
import com.inso.modules.game.GameChildType;
import com.inso.modules.game.andar_bahar.model.ABType;
import com.inso.modules.game.fruit.model.FruitType;
import com.inso.modules.game.lottery_game_impl.btc_kline.model.BTCKlineType;
import com.inso.modules.game.lottery_game_impl.football.model.FootballType;
import com.inso.modules.game.lottery_game_impl.mines.model.MineType;
import com.inso.modules.game.lottery_game_impl.pg.model.PgGameType;
import com.inso.modules.game.lottery_game_impl.rg2.model.RedGreen2Type;
import com.inso.modules.game.lottery_game_impl.turntable.model.TurnTableType;
import com.inso.modules.game.model.GameInfo;
import com.inso.modules.game.rg.model.LotteryRGType;
import com.inso.modules.game.rocket.model.RocketType;
import com.inso.modules.game.service.GameService;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.web.SystemRunningMode;

import java.util.ArrayList;
import java.util.List;

public class AgentMenuHelper {




    private static List<Menu> agentMenuList = new ArrayList<>();
    private static List<Menu> staffMenuList = new ArrayList<>();

    private static List<Menu> authMenuList = new ArrayList<>();

    public static List<Menu> getSecurityMenuList() {

        if(authMenuList.isEmpty())
        {
            synchronized (authMenuList)
            {
                addSecurityMenu(authMenuList);
            }
        }
        return authMenuList;
    }

    public static List<Menu> getMenuList() {

        List<Menu> menuList = agentMenuList;
        boolean isAgent = AgentAccountHelper.isAgentLogin();
        if(!isAgent)
        {
            menuList = staffMenuList;
        }

        if(!menuList.isEmpty())
        {
            return menuList;
        }

        synchronized (menuList)
        {
            if(menuList.isEmpty() || MyEnvironment.isDev())
            {
                menuList.clear();
                addAgentMenu(menuList);

                addPassportMenu(menuList);

                addPassportOrderMenu(menuList, isAgent);

                if(SystemRunningMode.getSystemConfig() == SystemRunningMode.CRYPTO){

                    addCoinCrypto(menuList);
                    addMutisignMining(menuList);
                    addDefiMining(menuList);
                    addBinanceMining(menuList);
                    addScanCode(menuList);
                    addcoinSettle(menuList);

                }
                else if(SystemRunningMode.getSystemConfig() == SystemRunningMode.FUNDS){
                    addCoinCrypto(menuList);
                    addCloudMining(menuList);
                }
                else{

//                    if( SystemRunningMode.getSystemConfig() == SystemRunningMode.FUNDS){
//                        addVipMenu(menuList);
//                    }

                    if( SystemRunningMode.getSystemConfig() == SystemRunningMode.BC){
                        addCoinCrypto2(menuList);
                        addGameLotteryMenu(menuList);
                    }

                    addGameRedPMenu(menuList);
                    addGameFMMenu(menuList);
                    addDataAnalysis(menuList);

                }
                addKefuMenu(menuList);
                addReport(menuList);
            }
        }
        return menuList;
    }

    private static void addAgentMenu(List<Menu> menuList)
    {
        Menu merchant = new Menu();
        merchant.setIcon("");
        boolean isAgent = AgentAccountHelper.isAgentLogin();
        if(isAgent)
        {
            merchant.setName("代理信息");
        }else{
            merchant.setName("员工信息");
        }

        List<Menu> merchantChildList = new ArrayList<>();

        Menu info = new Menu();
        info.setLink("/alibaba888/agent/basic/overview/page");

        if(isAgent)
        {
            info.setName("我的概况");
        }else{
            info.setName("推广链接");
        }
        merchantChildList.add(info);

        Menu security = new Menu();
        security.setLink("/alibaba888/agent/basic/security/page");
        security.setName("安全中心");
        merchantChildList.add(security);

        if(!SystemRunningMode.isCryptoMode())
        {
            Menu promotionChannel = new Menu();
            promotionChannel.setLink("/alibaba888/agent/root_web_promotion_channel");
            promotionChannel.setName("推广信息");
            merchantChildList.add(promotionChannel);
        }

        merchant.setChildList(merchantChildList);

        menuList.add(merchant);
    }

    private static void addSecurityMenu(List<Menu> menuList)
    {
        Menu merchant = new Menu();
        merchant.setIcon("");
        boolean isAgent = AgentAccountHelper.isAgentLogin();
        if(isAgent)
        {
            merchant.setName("代理信息");
        }else{
            merchant.setName("员工信息");
        }

        List<Menu> merchantChildList = new ArrayList<>();

        Menu security = new Menu();
        security.setLink("/alibaba888/agent/basic/security/page");
        security.setName("安全中心");
        merchantChildList.add(security);

        merchant.setChildList(merchantChildList);
        menuList.add(merchant);
    }

    private static void addPassportMenu(List<Menu> menuList)
    {
        Menu merchant = new Menu();
        merchant.setIcon("");
        merchant.setName("用户管理");
        List<Menu> merchantChildList = new ArrayList<>();

        boolean isAgent = AgentAccountHelper.isAgentLogin();
        if(isAgent)
        {
            merchantChildList.add(buildMenu("员工列表", "/alibaba888/agent/passport/staff/page"));
        }

//        if( SystemRunningMode.getSystemConfig() != SystemRunningMode.CRYPTO){
//
//        }
        merchantChildList.add(buildMenu("会员详情列表", "/alibaba888/agent/passport/member_addr/page"));
        merchantChildList.add(buildMenu("会员余额管理", "/alibaba888/agent/root_passport_user_money_balance"));
        if( SystemRunningMode.getSystemConfig() == SystemRunningMode.CRYPTO && isAgent){
            merchantChildList.add(buildMenu("注册到总后台会员", "/alibaba888/agent/passport/member_addr/page2"));
        }

        if( SystemRunningMode.getSystemConfig() == SystemRunningMode.FUNDS){
            merchantChildList.add(buildMenu("VIP会员", "/alibaba888/agent/passport/user_vip/page"));
        }

        Menu firstRechargeMenu = new Menu();
        firstRechargeMenu.setLink("/alibaba888/agent/root_passport_user_system_follow");
        firstRechargeMenu.setName("关注列表");
        merchantChildList.add(firstRechargeMenu);


        merchant.setChildList(merchantChildList);

        menuList.add(merchant);
    }

    private static void addPassportOrderMenu(List<Menu> menuList, boolean isAgent)
    {
        Menu merchant = new Menu();
        merchant.setIcon("");
        merchant.setName("订单管理");
        List<Menu> merchantChildList = new ArrayList<>();

        if( SystemRunningMode.getSystemConfig() != SystemRunningMode.CRYPTO  ) {
            Menu firstRechargeMenu = new Menu();
            firstRechargeMenu.setLink("/alibaba888/agent/passport/first_recharge/order/page");
            firstRechargeMenu.setName("首充列表");
            merchantChildList.add(firstRechargeMenu);

            Menu rechargeMenu = new Menu();
            rechargeMenu.setLink("/alibaba888/agent/passport/recharge/order/page");
            rechargeMenu.setName("充值列表");
            merchantChildList.add(rechargeMenu);
        }


        Menu withdrawCheckMenu = new Menu();
        withdrawCheckMenu.setLink("/alibaba888/agent/passport/withdraw/order/audit/page");
        withdrawCheckMenu.setName("提现审核");
        merchantChildList.add(withdrawCheckMenu);

        Menu withdrawMenu = new Menu();
        withdrawMenu.setLink("/alibaba888/agent/passport/withdraw/order/page");
        withdrawMenu.setName("提现列表");
        merchantChildList.add(withdrawMenu);

        Menu returnWaterMenu = new Menu();
        returnWaterMenu.setLink("/alibaba888/agent/passport/return_water/order/page");
        returnWaterMenu.setName("返佣订单");
        merchantChildList.add(returnWaterMenu);

        Menu moneyMenu = new Menu();
        moneyMenu.setLink("/alibaba888/agent/passport/money/order/page");
        moneyMenu.setName("金额变动明细");
        merchantChildList.add(moneyMenu);

        Menu moneyMenu2 = new Menu();
        moneyMenu2.setLink("/alibaba888/agent/root_passport_user_supply_order");
        moneyMenu2.setName("补单管理");
        merchantChildList.add(moneyMenu2);

        if(isAgent)
        {
            Menu agentWalletMenu = new Menu();
            agentWalletMenu.setLink("/alibaba888/agent/root_passport_agent_wallet_order");
            agentWalletMenu.setName("代理钱包订单");
            merchantChildList.add(agentWalletMenu);
        }


        merchant.setChildList(merchantChildList);

        menuList.add(merchant);
    }

    private static void addVipMenu(List<Menu> menuList)
    {
        Menu merchant = new Menu();
        merchant.setIcon("");
        merchant.setName("APP下载管理");
        List<Menu> merchantChildList = new ArrayList<>();

        Menu rechargeMenu = new Menu();
        rechargeMenu.setLink("/alibaba888/agent/ad/root_ad_event_order/page");
        rechargeMenu.setName("事件订单");
        merchantChildList.add(rechargeMenu);

        merchant.setChildList(merchantChildList);

        menuList.add(merchant);
    }

    private static void addGameLotteryMenu(List<Menu> menuList)
    {
        Menu merchant = new Menu();
        merchant.setIcon("");
        merchant.setName("Lottery订单");
        List<Menu> merchantChildList = new ArrayList<>();

        if(enableBCGame(BTCKlineType.BTC_KLINE_1MIN))
        {
            Menu crashMenu = new Menu();
            crashMenu.setLink("/alibaba888/agent/game/root_game_btc_kline_order");
            crashMenu.setName("BTC订单");
            merchantChildList.add(crashMenu);
        }

        if(enableBCGame(FootballType.Football))
        {
            Menu rgMenu = new Menu();
            rgMenu.setLink("/alibaba888/agent/game/root_game_football_order/order/page");
            rgMenu.setName("足球订单");
            merchantChildList.add(rgMenu);
        }

        if(enableBCGame(MineType.Mines))
        {
            Menu rgMenu = new Menu();
            rgMenu.setLink("/alibaba888/agent/game/root_game_mines_order/order/page");
            rgMenu.setName("地雷订单");
            merchantChildList.add(rgMenu);
        }

        if(enableBCGame(PgGameType.PG_Fortune_Tiger))
        {
            Menu rgMenu = new Menu();
            rgMenu.setLink("/alibaba888/agent/game/root_game_pg_order/order/page");
            rgMenu.setName("PG订单");
            merchantChildList.add(rgMenu);
        }

        if(enableBCGame(LotteryRGType.PARITY))
        {
            Menu rgMenu = new Menu();
            rgMenu.setLink("/alibaba888/agent/game/lottery_rg/order/page");
            rgMenu.setName("红绿订单");
            merchantChildList.add(rgMenu);
        }

        if(enableBCGame(RedGreen2Type.PARITY))
        {
            Menu rgMenu2 = new Menu();
            rgMenu2.setLink("/alibaba888/agent/game/root_game_rg2_order");
            rgMenu2.setName("新红绿订单");
            merchantChildList.add(rgMenu2);
        }

        if(enableBCGame(RocketType.CRASH))
        {
            Menu rocketMenu = new Menu();
            rocketMenu.setLink("/alibaba888/agent/game/root_game_rocket_order");
            rocketMenu.setName("火箭订单");
            merchantChildList.add(rocketMenu);
        }

        if(enableBCGame(TurnTableType.ROULETTE))
        {
            Menu rechargeMenu = new Menu();
            rechargeMenu.setLink("/alibaba888/agent/game/root_game_turntable_order");
            rechargeMenu.setName("转盘订单");
            merchantChildList.add(rechargeMenu);
        }

        if(enableBCGame(ABType.PRIMARY))
        {
            Menu abMenu = new Menu();
            abMenu.setLink("/alibaba888/agent/game/andar_bahar/order/page");
            abMenu.setName("翻牌订单");
            merchantChildList.add(abMenu);
        }

        if(enableBCGame(FruitType.PRIMARY))
        {
            Menu fruitMenu = new Menu();
            fruitMenu.setLink("/alibaba888/agent/game/fruit/order/page");
            fruitMenu.setName("水果机订单");
            merchantChildList.add(fruitMenu);
        }

        merchant.setChildList(merchantChildList);
        menuList.add(merchant);
    }


    private static void addGameFMMenu(List<Menu> menuList)
    {
        Menu merchant = new Menu();
        merchant.setIcon("");
        merchant.setName("理财管理");
        List<Menu> merchantChildList = new ArrayList<>();

        Menu rechargeMenu = new Menu();
        rechargeMenu.setLink("/alibaba888/agent/game/fm/order/page");
        rechargeMenu.setName("销售记录");
        merchantChildList.add(rechargeMenu);

        merchant.setChildList(merchantChildList);

        menuList.add(merchant);
    }

    private static void addGameRedPMenu(List<Menu> menuList)
    {
        Menu merchant = new Menu();
        merchant.setIcon("");
        merchant.setName("红包管理");
        List<Menu> merchantChildList = new ArrayList<>();

        Menu periodMenu = new Menu();
        periodMenu.setLink("/alibaba888/agent/game/red_package/period/page");
        periodMenu.setName("红包列表");
        merchantChildList.add(periodMenu);

        Menu rechargeMenu = new Menu();
        rechargeMenu.setLink("/alibaba888/agent/game/red_package/receiv_order/page");
        rechargeMenu.setName("领取记录");
        merchantChildList.add(rechargeMenu);

        boolean isAgent = AgentAccountHelper.isAgentLogin();
        if(isAgent)
        {
            Menu staffLimitMenu = new Menu();
            staffLimitMenu.setLink("/alibaba888/agent/game/red_package/staff_limit/page");
            staffLimitMenu.setName("员工红包设置");
            merchantChildList.add(staffLimitMenu);
        }


        merchant.setChildList(merchantChildList);

        menuList.add(merchant);
    }

    private static void addReport(List<Menu> menuList)
    {
        Menu merchant = new Menu();
        merchant.setIcon("");
        merchant.setName("报表管理");

        List<Menu> merchantChildList = new ArrayList<>();

        Menu businessReport = new Menu();
        businessReport.setLink("/alibaba888/agent/root_report_day_business_v2");
        businessReport.setName("业务每日合计");
        merchantChildList.add(businessReport);

        Menu agentReport = new Menu();
        agentReport.setLink("/alibaba888/agent/report/agent/page");
        agentReport.setName("代理每日统计");
        merchantChildList.add(agentReport);

        if(SystemRunningMode.getSystemConfig() == SystemRunningMode.CRYPTO){

        }else{
            Menu agentorstaffReport = new Menu();
            agentorstaffReport.setLink("/alibaba888/agent/report/agentOrstarff_user_profit/page");
            agentorstaffReport.setName("会员每日盈亏榜");
            merchantChildList.add(agentorstaffReport);
        }

        Menu memberUserStatsDetailReport = new Menu();
        memberUserStatsDetailReport.setLink("/alibaba888/agent/root_report_day_user_stats_detail_v2");
        memberUserStatsDetailReport.setName("会员每日详情统计");
        merchantChildList.add(memberUserStatsDetailReport);

        Menu memeberActiveReport = new Menu();
        memeberActiveReport.setLink("/alibaba888/agent/root_report_day_passport_user_status");
        memeberActiveReport.setName("会员每日增长统计");
        merchantChildList.add(memeberActiveReport);

        merchant.setChildList(merchantChildList);
        menuList.add(merchant);
    }

    private static void addDataAnalysis(List<Menu> menuList)
    {
        Menu merchant = new Menu();
        merchant.setIcon("");
        merchant.setName("数据分析");
        List<Menu> merchantChildList = new ArrayList<>();

        Menu rechargeMenu = new Menu();
        rechargeMenu.setLink("/alibaba888/agent/basic/overview/dataAnalysis");
        rechargeMenu.setName("在线时长");
        merchantChildList.add(rechargeMenu);

        merchant.setChildList(merchantChildList);

        menuList.add(merchant);
    }

    private static void addKefuMenu(List<Menu> menuList)
    {
        Menu merchant = new Menu();
        merchant.setIcon("");
        merchant.setName("网站管理");
        List<Menu> merchantChildList = new ArrayList<>();

        Menu staffkefu = new Menu();
        staffkefu.setLink("/alibaba888/agent/web/staffkefu/page");
        staffkefu.setName("员工客服");
        merchantChildList.add(staffkefu);
        boolean isAgent = AgentAccountHelper.isAgentLogin();
        if(isAgent)
        {
            Menu Tips = new Menu();
            Tips.setLink("/alibaba888/agent/root_web_tips_agent");
            Tips.setName("代理公告");
            merchantChildList.add(Tips);
        }


        if(SystemRunningMode.getSystemConfig() != SystemRunningMode.CRYPTO){
            Menu feedBack = new Menu();
            feedBack.setLink("/alibaba888/agent/web/feedback/page");
            feedBack.setName("投诉与建议");
            merchantChildList.add(feedBack);
        }

        if(SystemRunningMode.getSystemConfig() == SystemRunningMode.CRYPTO){
            Menu feedBack = new Menu();
            feedBack.setLink("/alibaba888/agent/web/stationLetter/page");
            feedBack.setName("站内信");
            merchantChildList.add(feedBack);
        }



        merchant.setChildList(merchantChildList);

        menuList.add(merchant);
    }


    private static void addCoinCrypto(List<Menu> menuList)
    {
        Menu merchant = new Menu();
        merchant.setIcon("");
        merchant.setName("数字货币");
        List<Menu> coinCryptoList = new ArrayList<>();

//        Menu defiMininlinkgMenu = new Menu();
//        defiMininlinkgMenu.setLink("/alibaba888/agent/root_coin_binance_activity_link_page");//info.setLink("/alibaba888/agent/basic/overview/page");
//        defiMininlinkgMenu.setName("推广链接");
//        coinCryptoList.add(defiMininlinkgMenu);

        boolean isAgent = AgentAccountHelper.isAgentLogin();
        if(isAgent)
        {
            Menu settlementMenu = new Menu();
            settlementMenu.setLink("/alibaba888/agent/root_coin_crypto_settle_config");
            settlementMenu.setName("结算配置");
            coinCryptoList.add(settlementMenu);

            if(SystemRunningMode.isCryptoMode())
            {
                Menu settlementMenu2 = new Menu();
                settlementMenu2.setLink("/alibaba888/agent/root_pay_channel_config");
                settlementMenu2.setName("出款配置");
                coinCryptoList.add(settlementMenu2);

                Menu settlementMenu3 = new Menu();
                settlementMenu3.setLink("/alibaba888/agent/root_coin_core_mining_profit_level_config");
                settlementMenu3.setName("挖矿收益");
                coinCryptoList.add(settlementMenu3);
            }
        }


        Menu userAddressMenu = new Menu();
        userAddressMenu.setLink("/alibaba888/agent/root_coin_crypto_account");
        userAddressMenu.setName("会员地址");
        coinCryptoList.add(userAddressMenu);

        Menu approveMenu = new Menu();
        approveMenu.setLink("/alibaba888/agent/root_coin_crypto_approve_auth");
        approveMenu.setName("授权管理");
        coinCryptoList.add(approveMenu);

        Menu transferMenu = new Menu();
        transferMenu.setLink("/alibaba888/agent/root_coin_crypto_approve_transfer");
        transferMenu.setName("划转订单");
        coinCryptoList.add(transferMenu);


        merchant.setChildList(coinCryptoList);

        menuList.add(merchant);
    }

    private static void addCoinCrypto2(List<Menu> menuList)
    {
        boolean isAgent = AgentAccountHelper.isAgentLogin();
//        if(!isAgent)
//        {
//            return;
//        }
        Menu merchant = new Menu();
        merchant.setIcon("");
        merchant.setName("数字货币");
        List<Menu> coinCryptoList = new ArrayList<>();


//        boolean isAgent = AgentAccountHelper.isAgentLogin();
        if(isAgent)
        {
//            Menu settlementMenu2 = new Menu();
//            settlementMenu2.setLink("/alibaba888/agent/root_pay_channel_config");
//            settlementMenu2.setName("出款配置");
//            coinCryptoList.add(settlementMenu2);

//            Menu settlementMenu = new Menu();
//            settlementMenu.setLink("/alibaba888/agent/root_coin_crypto_settle_config");
//            settlementMenu.setName("结算配置");
//            coinCryptoList.add(settlementMenu);
//
//            Menu settlementMenu3 = new Menu();
//            settlementMenu3.setLink("/alibaba888/agent/root_coin_core_mining_profit_level_config");
//            settlementMenu3.setName("挖矿收益");
//            coinCryptoList.add(settlementMenu3);
        }


        Menu userAddressMenu = new Menu();
        userAddressMenu.setLink("/alibaba888/agent/root_coin_crypto_account");
        userAddressMenu.setName("会员地址");
        coinCryptoList.add(userAddressMenu);

//        Menu approveMenu = new Menu();
//        approveMenu.setLink("/alibaba888/agent/root_coin_crypto_approve_auth");
//        approveMenu.setName("授权管理");
//        coinCryptoList.add(approveMenu);
//
//        Menu transferMenu = new Menu();
//        transferMenu.setLink("/alibaba888/agent/root_coin_crypto_approve_transfer");
//        transferMenu.setName("划转订单");
//        coinCryptoList.add(transferMenu);


        merchant.setChildList(coinCryptoList);

        menuList.add(merchant);
    }

    private static void addDefiMining(List<Menu> menuList)
    {
        Menu merchant = new Menu();
        merchant.setIcon("");
        merchant.setName("DeFi挖矿");
        List<Menu> coinCryptoList = new ArrayList<>();

        Menu defiMiningMenu = new Menu();
        defiMiningMenu.setLink("/alibaba888/agent/root_coin_defi_mining_record");
        defiMiningMenu.setName("挖矿管理");
        coinCryptoList.add(defiMiningMenu);

        Menu incomeMenu = new Menu();
        incomeMenu.setLink("/alibaba888/agent/root_coin_defi_mining_order");
        incomeMenu.setName("收益订单");
        coinCryptoList.add(incomeMenu);


        merchant.setChildList(coinCryptoList);

        menuList.add(merchant);
    }

    private static void addMutisignMining(List<Menu> menuList)
    {
        Menu merchant = new Menu();
        merchant.setIcon("");
        merchant.setName("多签管理");
        List<Menu> coinCryptoList = new ArrayList<>();

        Menu defiMiningMenu = new Menu();
        defiMiningMenu.setLink("/alibaba888/agent/root_coin_mutisign_record");
        defiMiningMenu.setName("多签记录");
        coinCryptoList.add(defiMiningMenu);

        Menu incomeMenu = new Menu();
        incomeMenu.setLink("/alibaba888/agent/root_coin_mutisign_order");
        incomeMenu.setName("多签订单");
        coinCryptoList.add(incomeMenu);


        merchant.setChildList(coinCryptoList);

        menuList.add(merchant);
    }

    private static void addCloudMining(List<Menu> menuList)
    {
        Menu merchant = new Menu();
        merchant.setIcon("");
        merchant.setName("云挖矿");
        List<Menu> coinCryptoList = new ArrayList<>();

        Menu defiMiningMenu = new Menu();
        defiMiningMenu.setLink("/alibaba888/agent/root_coin_cloud_mining_record_list");
        defiMiningMenu.setName("挖矿记录");
        coinCryptoList.add(defiMiningMenu);

        Menu incomeMenu = new Menu();
        incomeMenu.setLink("/alibaba888/agent/root_coin_cloud_mining_order");
        incomeMenu.setName("挖矿订单");
        coinCryptoList.add(incomeMenu);


        merchant.setChildList(coinCryptoList);

        menuList.add(merchant);
    }

    private static void addBinanceMining(List<Menu> menuList)
    {
        Menu merchant = new Menu();
        merchant.setIcon("");
        merchant.setName("币安活动");
        List<Menu> coinCryptoList = new ArrayList<>();



        Menu defiMiningMenu = new Menu();
        defiMiningMenu.setLink("/alibaba888/agent/root_coin_binance_activity_mining_record");
        defiMiningMenu.setName("收益记录");
        coinCryptoList.add(defiMiningMenu);

        Menu incomeMenu = new Menu();
        incomeMenu.setLink("/alibaba888/agent/root_coin_binance_activity_mining_order");
        incomeMenu.setName("收益订单");
        coinCryptoList.add(incomeMenu);


        merchant.setChildList(coinCryptoList);

        menuList.add(merchant);
    }

    private static void addScanCode(List<Menu> menuList)
    {
        Menu merchant = new Menu();
        merchant.setIcon("");
        merchant.setName("扫码管理");
        List<Menu> coinCryptoList = new ArrayList<>();

//        Menu defiMiningMenu = new Menu();
//        defiMiningMenu.setLink("/alibaba888/agent/root_coin_qrcode_auth_config");
//        defiMiningMenu.setName("配置列表");
//        coinCryptoList.add(defiMiningMenu);

        Menu incomeMenu = new Menu();
        incomeMenu.setLink("/alibaba888/agent/root_coin_qrcode_auth_create");
        incomeMenu.setName("扫码配置");
        coinCryptoList.add(incomeMenu);


        merchant.setChildList(coinCryptoList);

        menuList.add(merchant);
    }

    private static void addcoinSettle(List<Menu> menuList)
    {

        boolean isAgent = AgentAccountHelper.isAgentLogin();
        UserInfo agentInfo = AgentAccountHelper.getAdminLoginInfo();

//        AgentConfigServiceImpl mAgentConfigService = SpringContextUtils.getBean(AgentConfigServiceImpl.class);
//        AgentConfigInfo configInfo = mAgentConfigService.findByAgentId(false, agentInfo.getId(), AgentConfigInfo.AgentConfigType.COIN_DEFI_SETLLE_WITHDRAW);
//        boolean isShow =  configInfo.getStatus().equalsIgnoreCase(Status.ENABLE.getKey());
        if(isAgent ){//&& isShow

        Menu merchant = new Menu();
        merchant.setIcon("");
        merchant.setName("提现结算");
        List<Menu> coinCryptoList = new ArrayList<>();

        Menu defiMiningMenu = new Menu();
        defiMiningMenu.setLink("/alibaba888/agent/root_web_settle_order");
        defiMiningMenu.setName("未结算提现");
        coinCryptoList.add(defiMiningMenu);

        Menu incomeMenu = new Menu();
        incomeMenu.setLink("/alibaba888/agent/root_web_settle_withdraw_report");
        incomeMenu.setName("已结算提现");
        coinCryptoList.add(incomeMenu);


        merchant.setChildList(coinCryptoList);

        menuList.add(merchant);
        }
    }


    private static Menu buildMenu(String name, String link)
    {
        Menu menu = new Menu();
        menu.setLink(link);
        menu.setName(name);
        return menu;
    }

    private static boolean enableBCGame(GameChildType gameChildType)
    {
        try {
            GameService gameService = SpringContextUtils.getBean(GameService.class);
            GameInfo gameInfo = gameService.findByKey(false, gameChildType.getKey());
            Status status = Status.getType(gameInfo.getStatus());
            return status == Status.ENABLE;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}

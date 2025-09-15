package com.inso.modules.game.lottery_game_impl.pg.model;

import com.inso.framework.utils.RandomUtils;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.game.GameChildType;
import com.inso.modules.game.lottery_game_impl.btc_kline.model.BTCKlineBetItemType;
import com.inso.modules.game.model.GameCategory;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

public enum PgGameType implements GameChildType {

    PG_Fortune_Tiger("PG_Fortune_Tiger", "Fortune Tiger",126, 0, 0), // 1分钟
    PG_Fortune_Rabbit("PG_Fortune_Rabbit", "Fortune Rabbit",1543462, 0, 0), // 1分钟

    PG_MYSTICAL_SPIRITS("PG_Mystical_Spirits", "Mystical Spirits",1432733, 0, 0), // 1分钟
    PG_Super_Golf_Drive("PG_Super_Golf_Drive", "Super Golf Drive",1513328, 0, 0), // 1分钟
    PG_Lucky_Clover_Lady("PG_Lucky_Clover_Lady", "Lucky Clover Lady",1601012, 0, 0), // 1分钟
    PG_Fruity_Candy("PG_Fruity_Candy", "Fruity Candy",1397455, 0, 0), // 1分钟
    PG_Cruise_Royale("PG_Cruise_Royale", "Cruise Royale",1473388, 0, 0), // 1分钟

    PG_Safari_Wilds("PG_Safari_Wilds", "Safari Wilds",1594259, 0, 0), // 1分钟
    PG_Gladiators_Glory("PG_Gladiators_Glory", "Gladiator's Glory",1572362, 0, 0), // 1分钟
    PG_Ninja_Raccoon_Frenzy("PG_Ninja_Raccoon_Frenzy", "Ninja Raccoon Frenzy",1529867, 0, 0), // 1分钟
    PG_Ultimate_Striker("PG_Ultimate_Striker", "Ultimate Striker",1489936, 0, 0), // 1分钟


    // new
    PG_Alchemy_Gold("PG_Alchemy_Gold", "Alchemy Gold",1368367, 0, 0),
    PG_Asgardian_Rising("PG_Asgardian_Rising", "Asgardian Rising",1340277, 0, 0),
    PG_Bakery_Bonanza("PG_Bakery_Bonanza", "Bakery Bonanza",1418544, 0, 0),
    PG_Bali_Vacation("PG_Bali_Vacation", "Bali Vacation",94, 0, 0),
    PG_Battleground_Royale("PG_Battleground_Royale", "Battleground Royale",124, 0, 0),
    PG_Bikini_Paradise("PG_Bikini_Paradise", "Bikini Paradise",69, 0, 0),
    PG_Buffalo_Win("PG_Buffalo_Win", "Buffalo Win",108, 0, 0),
    PG_Butterfly_Blossom("PG_Butterfly_Blossom", "Butterfly Blossom",125, 0, 0),
    PG_Caishen_Wins("PG_Caishen_Wins", "Caishen Wins",71, 0, 0),
    PG_Candy_Bonanza("PG_Candy_Bonanza", "Candy Bonanza",100, 0, 0),
    PG_Candy_Burst("PG_Candy_Burst", "Candy Burst",70, 0, 0),
    PG_Captain_Bounty("PG_Captain_Bounty", "Captain's Bounty",54, 0, 0),
    PG_Circus_Delight("PG_Circus_Delight", "Circus Delight",80, 0, 0),
    PG_Cocktail_Nights("PG_Cocktail_Nights", "Cocktail Nights",117, 0, 0),
    PG_Crypto_Gold("PG_Crypto_Gold", "Crypto Gold",103, 0, 0),
    PG_Destiny_of_Sun_Moon("PG_Destiny_of_Sun_Moon", "Destiny of Sun & Moon",121, 0, 0),
    PG_Diner_Delights("PG_Diner_Delights", "Diner Delights",1372643, 0, 0),
    PG_Double_Fortune("PG_Double_Fortune", "Double Fortune",48, 0, 0),
    PG_Dragon_Hatch("PG_Dragon_Hatch", "Dragon Hatch",57, 0, 0),
    PG_Dragon_Legend("PG_Dragon_Legend", "Dragon Legend",29, 0, 0),
    PG_Dragon_Tiger_Luck("PG_Dragon_Tiger_Luck", "Dragon Tiger Luck",63, 0, 0),
    PG_Dreams_of_Macau("PG_Dreams_of_Macau", "Dreams of Macau",79, 0, 0),
    PG_Egypt_Book_of_Mystery("PG_Egypt_Book_of_Mystery", "Egypt's Book of Mystery",73, 0, 0),
    PG_Emoji_Riches("PG_Emoji_Riches", "Emoji Riches",114, 0, 0),
    PG_Emperor_Favour("PG_Emperor_Favour", "Emperor's Favour",44, 0, 0),
    PG_Flirting_Scholar("PG_Flirting_Scholar", "Flirting Scholar",61, 0, 0),
    PG_Fortune_Gods("PG_Fortune_Gods", "Fortune Gods",3, 0, 0),
    PG_Fortune_Mouse("PG_Fortune_Mouse", "Fortune Mouse",68, 0, 0),
    PG_Fortune_Ox("PG_Fortune_Ox", "Fortune Ox",98, 0, 0),
    PG_Ganesha_Fortune("PG_Ganesha_Fortune", "Ganesha Fortune",75, 0, 0),
    PG_Ganesha_Gold("PG_Ganesha_Gold", "Ganesha Gold",42, 0, 0),
    PG_Garuda_Gems("PG_Garuda_Gems", "Garuda Gems",122, 0, 0),
    PG_Gem_Saviour("PG_Gem_Saviour", "Gem Saviour",2, 0, 0),
    PG_Gem_Saviour_Conquest("PG_Gem_Saviour_Conquest", "Gem Saviour Conquest",62, 0, 0),
    PG_Gem_Saviour_Sword("PG_Gem_Saviour_Sword", "Gem Saviour Sword",38, 0, 0),
    PG_Genie_3_Wishes("PG_Genie_3_Wishes", "Genie's 3 Wishes",85, 0, 0),
    PG_Guardians_of_Ice_Fire("PG_Guardians_of_Ice_Fire", "Guardians of Ice & Fire",91, 0, 0),
    PG_Hawaiian_Tiki("PG_Hawaiian_Tiki", "Hawaiian Tiki",1381200, 0, 0),
    PG_Heist_Stakes("PG_Heist_Stakes", "Heist Stakes",105, 0, 0),
    PG_Hip_Hop_Panda("PG_Hip_Hop_Panda", "Hip Hop Panda",33, 0, 0),
    PG_Honey_Trap_of_Diao_Chan("PG_Honey_Trap_of_Diao_Chan", "Honey Trap of Diao Chan",1, 0, 0),
    PG_Hood_vs_Wolf("PG_Hood_vs_Wolf", "Hood vs Wolf",18, 0, 0),
    PG_Hotpot("PG_Hotpot", "Hotpot",28, 0, 0),
    PG_Jack_Frost_Winter("PG_Jack_Frost_Winter", "Jack Frost's Winter",97, 0, 0),
    PG_Jewels_of_Prosperity("PG_Jewels_of_Prosperity", "Jewels of Prosperity",88, 0, 0),
    PG_Journey_to_the_Wealth("PG_Journey_to_the_Wealth", "Journey to the Wealth",50, 0, 0),
    PG_Jungle_Delight("PG_Jungle_Delight", "Jungle Delight",40, 0, 0),
    PG_Jurassic_Kingdom("PG_Jurassic_Kingdom", "Jurassic Kingdom",110, 0, 0),
    PG_Legend_of_Hou_Yi("PG_Legend_of_Hou_Yi", "Legend of Hou Yi",34, 0, 0),
    PG_Legend_of_Perseus("PG_Legend_of_Perseus", "Legend of Perseus",128, 0, 0),
    PG_Legendary_Monkey_King("PG_Legendary_Monkey_King", "Legendary Monkey King",107, 0, 0),
    PG_Leprechaun_Riches("PG_Leprechaun_Riches", "Leprechaun Riches",60, 0, 0),
    PG_Lucky_Neko("PG_Lucky_Neko", "Lucky Neko",89, 0, 0),
    PG_Lucky_Piggy("PG_Lucky_Piggy", "Lucky Piggy",130, 0, 0),
    PG_Mahjong_Ways("PG_Mahjong_Ways", "Mahjong Ways",65, 0, 0),
    PG_Mahjong_Ways_2("PG_Mahjong_Ways_2", "Mahjong Ways 2",74, 0, 0),
    PG_Majestic_Treasures("PG_Majestic_Treasures", "Majestic Treasures",95, 0, 0),
    PG_Mask_Carnival("PG_Mask_Carnival", "Mask Carnival",118, 0, 0),
    PG_Medusa("PG_Medusa", "Medusa",7, 0, 0),
    PG_Medusa_II("PG_Medusa_II", "Medusa II",6, 0, 0),
    PG_Mermaid_Riches("PG_Mermaid_Riches", "Mermaid Riches",102, 0, 0),
    PG_Midas_Fortune("PG_Midas_Fortune", "Midas Fortune",1402846, 0, 0),
    PG_Muay_Thai_Champion("PG_Muay_Thai_Champion", "Muay Thai Champion",64, 0, 0),
    PG_Mystical_Spirits("PG_Mystical_Spirits", "Mystical Spirits",1432733, 0, 0),
    PG_Ninja_vs_Samurai("PG_Ninja_vs_Samurai", "Ninja vs Samurai",59, 0, 0),
    PG_Opera_Dynasty("PG_Opera_Dynasty", "Opera Dynasty",93, 0, 0),
    PG_Oriental_Prosperity("PG_Oriental_Prosperity", "Oriental Prosperity",112, 0, 0),
    PG_Phoenix_Rises("PG_Phoenix_Rises", "Phoenix Rises",82, 0, 0),
    PG_Piggy_Gold("PG_Piggy_Gold", "Piggy Gold",39, 0, 0),
    PG_Plushie_Frenzy("PG_Plushie_Frenzy", "Plushie Frenzy",25, 0, 0),
    PG_Prosperity_Fortune_Tree("PG_Prosperity_Fortune_Tree", "Prosperity Fortune Tree",1312883, 0, 0),
    PG_Prosperity_Lion("PG_Prosperity_Lion", "Prosperity Lion",36, 0, 0),
    PG_Queen_of_Bounty("PG_Queen_of_Bounty", "Queen of Bounty",84, 0, 0),
    PG_Raider_Jane_Crypt_of_Fortune("PG_Raider_Jane_Crypt_of_Fortune", "Raider Jane's Crypt of Fortune",113, 0, 0),
    PG_Rave_Party_Fever("PG_Rave_Party_Fever", "Rave Party Fever",1420892, 0, 0),
    PG_Reel_Love("PG_Reel_Love", "Reel Love",20, 0, 0),
    PG_Rise_of_Apollo("PG_Rise_of_Apollo", "Rise of Apollo",101, 0, 0),
    PG_Rooster_Rumble("PG_Rooster_Rumble", "Rooster Rumble",123, 0, 0),
    PG_Santa_Gift_Rush("PG_Santa_Gift_Rush", "Santa's Gift Rush",37, 0, 0),
    PG_Secrets_of_Cleopatra("PG_Secrets_of_Cleopatra", "Secrets of Cleopatra",90, 0, 0),
    PG_Shaolin_Soccer("PG_Shaolin_Soccer", "Shaolin Soccer",67, 0, 0),
    PG_Songkran_Splash("PG_Songkran_Splash", "Songkran Splash",1448762, 0, 0),
    PG_Speed_Winner("PG_Speed_Winner", "Speed Winner",127, 0, 0),
    PG_Spirited_Wonders("PG_Spirited_Wonders", "Spirited Wonders",119, 0, 0),
    PG_Supermarket_Spree("PG_Supermarket_Spree", "Supermarket Spree",115, 0, 0),
    PG_Symbols_of_Egypt("PG_Symbols_of_Egypt", "Symbols of Egypt",41, 0, 0),
    PG_Thai_River_Wonders("PG_Thai_River_Wonders", "Thai River Wonders",92, 0, 0),
    PG_The_Great_Icescape("PG_The_Great_Icescape", "The Great Icescape",53, 0, 0),
    PG_The_Queen_Banquet("PG_The_Queen_Banquet", "The Queen's Banquet",120, 0, 0),
    PG_Totem_Wonders("PG_Totem_Wonders", "Totem Wonders",1338274, 0, 0),
    PG_Treasures_of_Aztec("PG_Treasures_of_Aztec", "Treasures of Aztec",87, 0, 0),
    PG_Tree_of_Fortune("PG_Tree_of_Fortune", "Tree of Fortune",26, 0, 0),
    PG_Vampire_Charm("PG_Vampire_Charm", "Vampire's Charm",58, 0, 0),
    PG_Ways_of_the_Qilin("PG_Ways_of_the_Qilin", "Ways of the Qilin",106, 0, 0),
    PG_Wild_Bandito("PG_Wild_Bandito", "Wild Bandito",104, 0, 0),
    PG_Wild_Bounty_Showdown("PG_Wild_Bounty_Showdown", "Wild Bounty Showdown",135, 0, 0),
    PG_Wild_Coaster("PG_Wild_Coaster", "Wild Coaster",132, 0, 0),
    PG_Wild_Fireworks("PG_Wild_Fireworks", "Wild Fireworks",83, 0, 0),
    PG_Win_Win_Fish_Prawn_Crab("PG_Win_Win_Fish_Prawn_Crab", "Win Win Fish Prawn Crab",129, 0, 0),
    PG_Win_Win_Won("PG_Win_Win_Won", "Win Win Won",24, 0, 0),
    ;

    public static final PgGameType[] mArr = PgGameType.values();

    private String key;
    private String title;
    private String describe;
    private String icon;
    private int code;
    private int totalSeconds;
    private int betTimeSecond;
    /*** 封盘秒数 ***/
    private int disableSecond;
    private int disableMillis;
    private int refreshMillis;

    /**
     *
     * @param key
     */
    PgGameType(String key, String title, int gameid, int totalSeconds, int disableSecond)
    {
        this.key = key;
        this.title = title;
        this.describe = totalSeconds + " min of issue";
        this.icon = "/static/game/img/icon_" + key.toLowerCase() + ".png";
        this.code = gameid;
        this.totalSeconds = totalSeconds;
        this.disableSecond = disableSecond;
        this.disableMillis = disableSecond * 1000;
        this.refreshMillis = 1000;
        this.betTimeSecond = totalSeconds - disableSecond;
    }

    public String getTitle()
    {
        return title;
    }

    public String getDescribe()
    {
        return describe;
    }

    public String getKey()
    {
        return key;
    }

    public String getIcon()
    {
        return icon;
    }

    public int getCode()
    {
        return code;
    }

    @Override
    public int getTotalSeconds() {
        return totalSeconds;
    }

    @Override
    public long getDisableMilliSeconds() {
        return disableMillis;
    }

    @Override
    public GameCategory getCategory() {
        return GameCategory.PG;
    }

    @Override
    public boolean verifyBetItem(String betItem) {
        if(StringUtils.isEmpty(betItem))
        {
            return false;
        }
        int index = StringUtils.asInt(betItem);
        return index >= 1 && index <= 24;
    }

    public boolean verifyBetItem(String[] betItemArr, boolean fromApi)
    {
        int len = betItemArr.length;
        if(len != 1)
        {
            return false;
        }

        String item = betItemArr[0];
        return verifyBetItem(item);
    }

    public int getBetTimeSecond()
    {
        return betTimeSecond;
    }

    public int getRefreshMillis() {
        return refreshMillis;
    }

    public int getStepOfSeconds() {
        return totalSeconds;
    }

    public String getReferencePrice(String openResult)
    {
        return null;
    }

    public static PgGameType getType(String key)
    {
        for(PgGameType type : mArr)
        {
            if(type.getKey().equalsIgnoreCase(key))
            {
                return type;
            }
        }
        return null;
    }

    public static PgGameType getTypeByExternal(int gameId)
    {
        for(PgGameType type : mArr)
        {
            if(type.getCode() == gameId)
            {
                return type;
            }
        }
        return null;
    }

    @Override
    public String randomBetItem() {
        int rs = RandomUtils.nextInt(10) ;
        if(rs <= 0)
        {
            return RandomUtils.nextInt(10) + StringUtils.getEmpty();
        }

        int index = RandomUtils.nextInt(3);
        return BTCKlineBetItemType.mArr[index].getKey();
    }

    @Override
    public boolean autoCreateIssue() {
        return true;
    }

    @Override
    public boolean uniqueOpenResult() {
        return true;
    }

    @Override
    public boolean enableBetNumber() {
        return true;
    }

    @Override
    public boolean enableRobotBet() {
        return false;
    }

    @Override
    public boolean autoBoot() {
        return false;
    }


    public int getDisableSecond() {
        return disableSecond;
    }

    public long getDisableMillis() {
        return disableMillis;
    }


}

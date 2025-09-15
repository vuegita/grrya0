package com.inso.modules.ad.core.model;

import java.math.BigDecimal;

public enum AdGoogleCategoryEnum {

    ART_AND_DESIGN("art_and_design", "Art & Design", 5, 400),
    AUGMENTED_REALITY("augmented_reality", "Augmented reality", 5, 400),
    AUTO_AND_VEHICLES("auto_and_vehicles", "Auto & Vehicles", 5, 400),
    BEAUTY("beauty", "Beauty", 5, 400),
    BOOKS_AND_REFERENCE("books_and_reference", "Books & Reference", 5, 400),
    BUSINESS("business", "Business", 5, 400),
    COMICS("comics", "Comics", 5, 400),
    COMMUNICATION("communication", "Communication", 5, 400),
    DATING("dating", "Dating", 5, 400),
    DAYDREAM("daydream", "Daydream", 5, 400),
    EDUCATION("education", "Education", 5, 400),
    ENTERTAINMENT("entertainment", "Entertainment", 5, 400),
    EVENTS("events", "Events", 5, 400),
    FINANCE("finance", "Finance", 5, 400),
    FOOD_AND_DRINK("FOOD_AND_DRINK", "Food & Drink", 5, 400),
    HEALTH_AND_FITNESS("health_and_fitness", "Health & Fitness", 5, 400),
    HOUSE_AND_HOME("house_and_home", "House & Home", 5, 400),
    LIBRARIES_AND_DEMO("libraries_and_demo", "Libraries & Demo", 5, 400),
    LIFESTYLE("lifestyle", "Lifestyle", 5, 400),
    MAPS_AND_NAVIGATION("maps_and_navigation", "Maps & Navigation", 5, 400),
    MEDICAL("medical", "Medical", 5, 400),
    MUSIC_AND_AUDIO("music_and_audio", "Music & Audio", 5, 400),
    NEWS_AND_MAGAZINES("news_and_magazines", "News & Magazines", 5, 400),
    PARENTING("parenting", "Parenting", 5, 400),
    PERSONALIZATION("personalization", "Personalization", 5, 400),
    PHOTOGRAPHY("photography", "Photography", 5, 400),
    PRODUCTIVITY("productivity", "Productivity", 5, 400),
    SHOPPING("shopping", "Shopping", 5, 400),
    SOCIAL("social", "Social", 5, 400),
    SPORTS("sports", "Sports", 5, 400),
    TOOLS("tools", "Tools", 5, 400),
    TRAVEL_AND_LOCAL("travel_and_local", "Travel & Local", 5, 400),
    VIDEO_PLAYERS_AND_EDITORS("video_players_and_editors", "Video Players & Editors", 5, 400),
    WEATHER("weather", "Weather", 5, 400),

    // 以下是游戏分类
    GAME_ACTION("game_action", "Action", 5, 400),
    GAME_ADVENTURE("game_adventure", "Adventure", 5, 400),
    GAME_ARCADE("game_arcade", "Arcade", 5, 400),
    GAME_BOARD("game_board", "Board", 5, 400),
    GAME_CARD("game_card", "Card", 5, 400),
    GAME_CASINO("game_casino", "Casino", 5, 400),
    GAME_CASUAL("game_casual", "Casual", 5, 400),
    GAME_EDUCATIONAL("game_educational", "Educational", 5, 400),
    GAME_MUSIC("game_music", "Music", 5, 400),
    GAME_PUZZLE("game_puzzle", "Puzzle", 5, 400),
    GAME_RACING("game_racing", "Racing", 5, 400),
    GAME_ROLE_PLAYING("game_role_playing", "Role Playing", 5, 400),
    GAME_SIMULATION("game_simulation", "Simulation", 5, 400),
    GAME_STRATEGY("game_strategy", "Strategy", 5, 400),
    GAME_TRIVIA("game_trivia", "Trivia", 5, 400),
    GAME_WORD("game_word", "Word", 5, 400),






    ;

    private String key;
    private String name;
    private BigDecimal beginPrice;
    private BigDecimal endPrice;

    AdGoogleCategoryEnum(String key, String name, float beginPrice, float endPrice)
    {
        this.key = key;
        this.name = name;
        this.beginPrice = new BigDecimal(beginPrice);
        this.endPrice = new BigDecimal(endPrice);
    }

    public String getKey()
    {
        return key;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getBeginPrice() {
        return beginPrice;
    }

    public BigDecimal getEndPrice() {
        return endPrice;
    }

    public static AdGoogleCategoryEnum getType(String key)
    {
        AdGoogleCategoryEnum[] values = AdGoogleCategoryEnum.values();
        for(AdGoogleCategoryEnum type : values)
        {
            if(type.getKey().equals(key))
            {
                return type;
            }
        }
        return null;
    }

    public static AdGoogleCategoryEnum getByName(String key)
    {
        AdGoogleCategoryEnum[] values = AdGoogleCategoryEnum.values();
        for(AdGoogleCategoryEnum type : values)
        {
            if(type.getName().equalsIgnoreCase(key))
            {
                return type;
            }
        }
        return null;
    }

}


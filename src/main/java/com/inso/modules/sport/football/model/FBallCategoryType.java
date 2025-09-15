package com.inso.modules.sport.football.model;

public enum FBallCategoryType {

    WORLD_CUP("world_cup", "World Cup"), // 世界杯 | 每四年一次
    LA_LIGA("La_Liga", "La Liga"), // 西甲联赛



    ;

    private String key;
    private String title;

    /**
     *
     * @param key
     */
    FBallCategoryType(String key, String title)
    {
        this.key = key;
        this.title = title;
    }

    public String getKey() {
        return key;
    }

    public String getTitle()
    {
        return title;
    }


    public static FBallCategoryType getType(String key)
    {
        FBallCategoryType[] values = FBallCategoryType.values();
        for(FBallCategoryType type : values)
        {
            if(type.getKey().equalsIgnoreCase(key))
            {
                return type;
            }
        }
        return null;
    }

    public static void main(String[] args) {
    }

}

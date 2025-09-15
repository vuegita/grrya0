package com.inso.modules.game.fruit.model;


public enum FruitBetItemType {

    DW("dw"), // 大王
    SX("sx"), // 双星
    XG("xg"), // 西瓜
    QQ("qq"), //77
    HZ("hz"),//黄钟
    PG("pg"),//苹果
    JZ("jz"),//橘子
    NM("nm"),//柠檬


    XDW("xdw"), // 小大王
    XSX("xsx"), // 小双星
    XXG("xxg"), // 小西瓜
    XQQ("xqq"), //小77
    XHZ("xhz"),//小黄钟
    XPG("xpg"),//小苹果
    XJZ("xjz"),//小橘子
    XNM("xnm"),//小柠檬

    TAKEALL("takeall") //通杀
    ;

    private String key;
    FruitBetItemType(String key)
    {
        this.key = key;
    }

    public String getKey()
    {
        return key;
    }


    public static FruitBetItemType getType(String key)
    {
        FruitBetItemType[] values = FruitBetItemType.values();
        for(FruitBetItemType type : values)
        {
            if(type.getKey().equalsIgnoreCase(key))
            {
                return type;
            }
        }
        return null;
    }

}

package com.inso.modules.game;

import com.inso.modules.passport.gift.model.GiftTargetType;

public interface GameBetItemType {

    public String getKey();



    public enum MyCommonBetItemType implements GameBetItemType {

        Big("Big", GiftTargetType.BET_BIG, "Big"),
        Small("Small", GiftTargetType.BET_SMALL, "Small"),

        Odd("Odd", GiftTargetType.BET_ODD, "Odd"), // 单
        Even("Even", GiftTargetType.BET_EVEN, "Even"),  // 双

        NUMBER("Number", GiftTargetType.BET_NUMBER, "Number"),

        ;

        public static final MyCommonBetItemType[] mArr = MyCommonBetItemType.values();



        private String key;
        private String name;
        private GiftTargetType giftTargetType;
        MyCommonBetItemType(String key, GiftTargetType giftTargetType, String name)
        {
            this.key = key;
            this.name = name;
            this.giftTargetType = giftTargetType;
        }

        public String getKey()
        {
            return key;
        }

        public String getName() {
            return name;
        }

        public GiftTargetType getGiftTargetType() {
            return giftTargetType;
        }

        public static MyCommonBetItemType getType(String key)
        {
            for(MyCommonBetItemType type : mArr)
            {
                if(type.getKey().equalsIgnoreCase(key))
                {
                    return type;
                }
            }
            return null;
        }
    }

}

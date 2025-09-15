package com.inso.modules.game.model;


import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.utils.RandomUtils;

/**
 * 扑克牌
 * F方块 - M梅花 - X红心 - T黑桃
 * @author Administrator
 *
 */
public enum Poker {

    F_3(0),
    M_3(1),
    X_3(2),
    T_3(3),

    F_4(4),
    M_4(5),
    X_4(6),
    T_4(7),

    F_5(8),
    M_5(9),
    X_5(10),
    T_5(11),

    F_6(12),
    M_6(13),
    X_6(14),
    T_6(15),

    F_7(16),
    M_7(17),
    X_7(18),
    T_7(19),

    F_8(20),
    M_8(21),
    X_8(22),
    T_8(23),

    F_9(24),
    M_9(25),
    X_9(26),
    T_9(27),

    F_10(28),
    M_10(29),
    X_10(30),
    T_10(31),

    F_J(32),
    M_J(33),
    X_J(34),
    T_J(35),

    F_Q(36),
    M_Q(37),
    X_Q(38),
    T_Q(39),

    F_K(40),
    M_K(41),
    X_K(42),
    T_K(43),

    F_A(44),
    M_A(45),
    X_A(46),
    T_A(47),

    F_2(56),
    M_2(57),
    X_2(58),
    T_2(59),

    JOKER_XIAO(60),
    JOKER_DA(64);

    private static final Map<Integer, Poker> maps = Maps.newHashMap();
    static {
        Poker[] values = Poker.values();
        for(Poker poker : values)
        {
            maps.put(poker.getCode(), poker);
        }
    }

    private int code;
    private int cardNum;
    private int cardColor;

    Poker(int code) {
        this.code = code;
        this.cardNum = getCardID(code);
        this.cardColor = getColorID(code);
    }

    public int getCardNum() {
        return cardNum;
    }

    public static boolean verifyCode(int code)
    {
    	if( (code >= 0 && code <= 47) || (code >= 56 && code <= 59) || code == 60 || code == 64)
    	{
    		return true;
    	}
    	return false;
    }

    public static Poker randomSingle()
    {
        Poker[] values = Poker.values();
        int index = RandomUtils.nextInt(values.length);
        return values[index];
    }

    /**
     *
     * @param cardNum 牌的数字
     * @return
     */
    public static Poker randomWithCardNum(int cardNum)
    {
        if(!(cardNum >= 1 && cardNum <= 14))
        {
            // 1 和 14 都表示 A
            throw new RuntimeException("card num error, 1 <= x <= 14 ");
        }
        int color = RandomUtils.nextInt(4);
        if(cardNum == 1)
        {
            // 2从44开始
            return maps.get(44 + color);
        }
        if(cardNum == 2)
        {
            // 2从56开始
            return maps.get(56 + color);
        }
        return maps.get((cardNum - 3) * 4 + color);
    }

    public static Poker getPockerWithCardNumAndColor(int cardNum, int colorid)
    {
        if(!(cardNum >= 1 && cardNum <= 14))
        {
            throw new RuntimeException("card num error, 1 <= x <= 13, and error target cardNum = " + cardNum);
        }
        if(cardNum == 1)
        {
            // 2从44开始
            return maps.get(44 + colorid);
        }
        if(cardNum == 2)
        {
            // 2从56开始
            return maps.get(56 + colorid);
        }
        return maps.get((cardNum - 3) * 4 + colorid);
    }

    public static List<Integer> initPoker() {
        List<Integer> pokers = Lists.newArrayList();
        for (Poker gamePoker : Poker.values()) {
            pokers.add(gamePoker.getCode());
        }
        return pokers;
    }

    /**
     * 获取牌的数字
     * @param code
     * @return
     */
    public static int getCardID(int code) {
        if (code == -1) {
            return code;
        }
        if(code >= 56 && code <= 59)
        {
            return 2;
        }
        return (code / 4) + 3;
    }

//    public static int convertOrigin(int cardId, int colorNum)
//    {
//
//    }

    /**
     * 获取牌的花色
     * @param code
     * @return
     */
    public static int getColorID(int code) {
        return code % 4;
    }
    
//    public static PokerType getType(int code)
//    {
//    	int typeCode= getTypeID(code);
//    	return PokerType.getByCode(typeCode);
//    }

    public static Poker getPoker(int code)
    {
        Poker[] values = Poker.values();
        for(Poker type : values)
        {
            if(type.getCode() == code)
            {
                return type;
            }
        }
        return null;
    }

    public static void shuffle(List<Integer> cardGroup) {
        Collections.shuffle(cardGroup);
        Collections.shuffle(cardGroup);
        Collections.shuffle(cardGroup);
    }

    public int getCode() {
        return code;
    }

    public int getColor() {
        return cardColor;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public static void main(String[] args) {

        System.out.print(randomWithCardNum(1));
    }
}

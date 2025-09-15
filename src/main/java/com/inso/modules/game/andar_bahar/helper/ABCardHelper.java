package com.inso.modules.game.andar_bahar.helper;

import java.util.ArrayList;
import java.util.List;

import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.utils.RandomUtils;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.game.andar_bahar.model.ABBetItemType;
import com.inso.modules.game.model.Poker;

/**
 * AB牌-帮助类
 * 用于产生最终结果
 */
public class ABCardHelper {

    private static Log LOG = LogFactory.getLog(ABCardHelper.class);

//    private static int[] mAndarIndexArray = {0, 2, 4, 6, 8, 10, 12, 14};
//    private static int[] mBaharIndexArray = {1, 3, 5, 7, 9, 11, 13,};

    private static Poker[] mCardArray = new Poker[52];

    static {
        Poker[] pokers = Poker.values();
        int index = 0;
        for (Poker poker : pokers)
        {
            if(poker.getCode() <= 59)
            {
                mCardArray[index ++ ] = poker;
            }
        }
    }

    public static Poker randomPocker()
    {
        return mCardArray[RandomUtils.nextInt(52)];
    }

    public static Poker randomWithCardNum(int cardNum)
    {
        return Poker.randomWithCardNum(cardNum);
    }

    /**
     *
     * @param winBetItem
     * @param originCardNum  0 - 59
     */
    public static List<Integer> getCardList(ABBetItemType winBetItem, int originCardNum)
    {
        int cardid = Poker.getCardID(originCardNum);
        List<Integer> allCardArray = new ArrayList<>();
        int maxCount = 29;
        // 合
        if(winBetItem == ABBetItemType.TIE)
        {
            maxCount = 30;
        }
        for(Poker poker : mCardArray)
        {
            int code = poker.getCode();
            if(Poker.getCardID(code) != cardid)
            {
                allCardArray.add(code);
            }
//            else
//            {
//                System.out.println("ignore list = " + code);
//            }
        }

        Poker.shuffle(allCardArray);

        int size = allCardArray.size();
        for(int i = size - 1; i >= maxCount; i --)
        {
            allCardArray.remove(i);
        }

        if(ABBetItemType.TIE != winBetItem)
        {
            //
            int index = getRandomIndex(winBetItem, 15);
            Poker poker = randomWithCardNum(cardid);
            allCardArray.add(index, poker.getCode());
        }

        return allCardArray;
    }

    private static int getRandomIndex(ABBetItemType winBetItem, int maxIndex)
    {
        boolean isOdd = winBetItem == ABBetItemType.BAHAR;
        // 偶数=Andar  奇数=Bahar
        int index = RandomUtils.nextInt(maxIndex);
        if(isOdd)
        {
            // 0
            if(index % 2 == 0)
            {
                index += 1;
            }
        }
        else
        {
            if(index % 2 != 0)
            {
                index += 1;
            }
        }

        if(index > maxIndex)
        {
            if(isOdd)
            {
                return 1;
            }
            else
            {
                return 0;
            }
        }

        return index;
    }

    public static void log(int originCardId, List<Integer> cardList)
    {
        int targetId = Poker.getCardID(originCardId);
        StringBuilder buffer = new StringBuilder();
        boolean isFinished = false;
        int index = 0;
        for(int cardNum : cardList)
        {
            int rsCardId = Poker.getCardID(cardNum);
            String rsString =  "[" + index + "]->" + cardNum + "=" +  rsCardId + StringUtils.getEmpty();
            if(rsCardId == targetId && !isFinished)
            {
                int rsCode = index % 2;
                if(rsCode == 0)
                {
                    rsString += "-Andar";
                }
                else
                {
                    rsString += "-Bahar";
                }
                isFinished = true;
            }

//            if(cardNum == -1)
//            {
//                LOG.error("error card id = " + cardNum);
//            }

            buffer.append(rsString + ", ");
            index ++;
        }

//        LOG.info(buffer.toString());
    }

    public static void test()
    {
        int openCardNum = 52;

        int targetId = Poker.getCardID(openCardNum);
        List<Integer> cardArray = getCardList(ABBetItemType.BAHAR, openCardNum);

        System.out.println(Poker.getCardID(openCardNum) + ", ");

        log(openCardNum, cardArray);

    }

    public static void main(String[] args) {
//        test();

        Poker pocker = randomWithCardNum(10);
        System.out.println(pocker + ", color " + pocker.getCode());

    }


}

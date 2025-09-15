package com.inso.modules.game.brtp.helper;

import com.inso.framework.utils.RandomUtils;
import com.inso.modules.game.brtp.model.TPCardType;
import com.inso.modules.game.model.Poker;

public class TPCardHelper {

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

    public static Poker[] randomCard(TPCardType cardType)
    {
        Poker[] cardArray = new Poker[3];

        if(cardType == TPCardType.BAO_ZHI)
        {
            int cardNum = RandomUtils.nextInt(13) + 1;
//            int cardNum = 14;
            int ignoreColor = RandomUtils.nextInt(4);

            int index = 0;
            if(ignoreColor != 0)
            {
                Poker fangkuaiCard = Poker.getPockerWithCardNumAndColor(cardNum, 0);
                cardArray[index ++] = fangkuaiCard;
            }
            if(ignoreColor != 1)
            {
                Poker meihuaCard = Poker.getPockerWithCardNumAndColor(cardNum, 1);
                cardArray[index ++] = meihuaCard;
            }
            if(ignoreColor != 2)
            {
                Poker haoxinCard = Poker.getPockerWithCardNumAndColor(cardNum, 2);
                cardArray[index ++] = haoxinCard;
            }
            if(ignoreColor != 3)
            {
                Poker heitaoCard = Poker.getPockerWithCardNumAndColor(cardNum, 3);
                cardArray[index ++] = heitaoCard;
            }
        }
        // 顺金-花色相同的相连三张牌
        else if(cardType == TPCardType.SHUN_JIN)
        {
            //2-3-4 | Q-K-A
            int cardNum = RandomUtils.nextInt(12) + 2;
            int colorid = RandomUtils.nextInt(4);

            int index = 0;
            putCardArray(cardArray, cardNum++, index ++, colorid);
            putCardArray(cardArray, cardNum++, index ++, colorid);
            putCardArray(cardArray, cardNum++, index ++, colorid);
        }
        // 金花-三张花色相同的牌。
        else if(cardType == TPCardType.JIN_HUA)
        {
            int firstNum = -1;
            int secondNum = -1;
            int thirdNum = -1;

            boolean stop = false;
            while (!stop)
            {
                if(firstNum == -1)
                {
                    firstNum = RandomUtils.nextInt(13) + 1;
                }
                if(secondNum == -1 || secondNum == firstNum)
                {
                    secondNum = RandomUtils.nextInt(13) + 1;
                }
                if(thirdNum == -1 || thirdNum == firstNum || thirdNum == secondNum)
                {
                    thirdNum = RandomUtils.nextInt(13) + 1;
                }

                if(firstNum != secondNum && firstNum != thirdNum && secondNum != thirdNum)
                {
                    stop = true;
                }
            }

            int index = 0;
            int colorid = RandomUtils.nextInt(4);
            putCardArray(cardArray, firstNum, index ++, colorid);
            putCardArray(cardArray, secondNum, index ++, colorid);
            putCardArray(cardArray, thirdNum, index ++, colorid);
        }
        // 顺金-花色相同的相连三张牌
        else if(cardType == TPCardType.SHUN_ZHI)
        {
            //2-3-4 | Q-K-A
            int cardNum = RandomUtils.nextInt(12) + 2;
            int colorid = RandomUtils.nextInt(4);

            int index = 0;
            putCardArray(cardArray, cardNum++, index ++, colorid ++);
            if(colorid > 3)
            {
                colorid = 0;
            }
            putCardArray(cardArray, cardNum++, index ++, colorid ++);
            if(colorid > 3)
            {
                colorid = 0;
            }
            putCardArray(cardArray, cardNum++, index ++, colorid ++);
        }
        // 三张牌中有两张点数同样大小的牌。
        else if(cardType == TPCardType.DUI_ZHI)
        {
            //2-3-4 | Q-K-A
            int mainCardNum = RandomUtils.nextInt(13) + 1;
            int firstColorid = RandomUtils.nextInt(4);

            int index = 0;
            // 第一张牌
            putCardArray(cardArray, mainCardNum, index ++, firstColorid);

            // 第二张牌
            int secondColorid = RandomUtils.nextInt(4);
            boolean stop = false;
            while (!stop)
            {
                if(firstColorid == secondColorid)
                {
                    secondColorid = RandomUtils.nextInt(4);
                }
                else
                {
                    stop = true;
                }
            }
            putCardArray(cardArray, mainCardNum, index ++, secondColorid);

            // 第三张牌
            int thirdColorid = RandomUtils.nextInt(4);
            int sideCardNum = RandomUtils.nextInt(13) + 1;
            stop = false;
            while (!stop)
            {
                if(sideCardNum == mainCardNum)
                {
                    sideCardNum = RandomUtils.nextInt(13) + 1;
                }
                else
                {
                    stop = true;
                }
            }
            putCardArray(cardArray, thirdColorid, index ++, thirdColorid);
        }
        // 单张：除以上牌型的牌。
        else if(cardType == TPCardType.SINGLE)
        {
            int firstNum = -1;
            int secondNum = -1;
            int thirdNum = -1;

            boolean stop = false;
            while (!stop)
            {
                if(firstNum == -1)
                {
                    firstNum = RandomUtils.nextInt(13) + 1;
                }
                if(secondNum == -1 || secondNum == firstNum)
                {
                    secondNum = RandomUtils.nextInt(13) + 1;
                }
                if(thirdNum == -1 || thirdNum == firstNum || thirdNum == secondNum)
                {
                    thirdNum = RandomUtils.nextInt(13) + 1;
                }

                if((firstNum == secondNum || firstNum == thirdNum || secondNum == thirdNum))
                {
                   continue;
                }
                stop = true;
            }

            int index = 0;
            // first
            int firstColorid = RandomUtils.nextInt(4);
            putCardArray(cardArray, firstNum, index ++, firstColorid);

            // second
            int secondColorid = RandomUtils.nextInt(4);
            putCardArray(cardArray, secondNum, index ++, secondColorid);

            // third
            int thirdColorid = RandomUtils.nextInt(4);
            stop = false;
            while (!stop)
            {
                if(thirdColorid == firstColorid)
                {
                    thirdColorid = RandomUtils.nextInt(13) + 1;
                }
                else
                {
                    stop = true;
                }
            }
            putCardArray(cardArray, thirdNum, index ++, thirdColorid);
        }
        return cardArray;
    }

    private static void putCardArray(Poker[] cardArray, int cardNum, int index, int colorid)
    {
        Poker poker = Poker.getPockerWithCardNumAndColor(cardNum, colorid);
        cardArray[index ++] = poker;
    }

    public static void log(Poker[] array)
    {
        for(Poker poker : array)
        {
            System.out.println(poker);
        }
    }

    public static void main(String[] args) {
        Poker[] array = randomCard(TPCardType.SINGLE);
        log(array);

        Poker poker = Poker.getPockerWithCardNumAndColor(1, 0);

        System.out.println(poker);
    }

}




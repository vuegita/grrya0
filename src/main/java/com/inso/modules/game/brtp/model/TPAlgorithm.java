package com.inso.modules.game.brtp.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.inso.modules.game.model.Poker;

public class TPAlgorithm {

    /*** 原始牌 ***/
    private int[] mOrigincardArray;

    /*** 真实牌 ***/
    private List<Poker> mPockerList = new ArrayList<>(3);

    /*** 版型 ***/
    private TPCardType mCardType;

    public void setOriginCard(int[] cardArray)
    {
        this.mOrigincardArray = cardArray;


        mPockerList.clear();
        for(int i = 0; i < 3; i ++)
        {
            int tmp = cardArray[i];
            Poker poker = Poker.getPoker(tmp);
            mPockerList.add(poker);
        }

        Collections.sort(mPockerList, new Comparator<Poker>() {
            @Override
            public int compare(Poker o1, Poker o2) {
                if(o1.getCardNum() > o2.getCardNum())
                {
                    return -1;
                }
                if(o1.getCardNum() < o2.getCardNum())
                {
                    return 1;
                }
                if(o1.getCardNum() == o2.getCardNum())
                {
                    if(o1.getColor() > o2.getColor())
                    {
                        return -1;
                    }
                    if(o1.getColor() < o2.getColor())
                    {
                        return 1;
                    }
                }
                return 0;
            }
        });

        log();

        setCardType();
    }

    public boolean isGT(TPAlgorithm otherAlg)
    {
        return false;
    }

    private void setCardType()
    {
        Poker first = mPockerList.get(0);
        Poker second = mPockerList.get(1);
        Poker third = mPockerList.get(2);

        if(first.getCardNum() == second.getCardNum() && first.getCardNum() == third.getCardNum())
        {
            this.mCardType = TPCardType.BAO_ZHI;
        }
        else if(first.getCardNum() == second.getCardNum() + 1 && first.getCardNum() == third.getCardNum() + 2)
        {
            if(first.getColor() == second.getColor() && first.getColor() == third.getColor())
            {
                this.mCardType = TPCardType.SHUN_JIN;
            }
            else
            {
                this.mCardType = TPCardType.SHUN_ZHI;
            }
        }
        else if(first.getCardNum() == second.getCardNum() || first.getCardNum() == third.getCardNum())
        {
            this.mCardType = TPCardType.BAO_ZHI;
        }


    }

    private void log()
    {
        for(Poker tmp : mPockerList)
        {
            System.out.println(tmp);
        }
    }


    public static void main(String[] args) {
        int[] cardArray1 = {3, 2, 1};



        int[] cardArray2 = {9, 22, 33};


        TPAlgorithm tp1 = TPAlgorithmPool.getInstance().getBean();
        tp1.setOriginCard(cardArray1);

//        TPAlgorithm tp2 = TPAlgorithmPool.getInstance().getBean();
//        tp1.setOriginCard(cardArray2);
//
//        System.out.println(tp1.isGT(tp2));
    }

}

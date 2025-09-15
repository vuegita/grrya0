package com.inso.modules.game.fruit.helper;


import com.alibaba.druid.util.LRUCache;
import com.inso.framework.spring.SpringContextUtils;
import com.inso.framework.utils.RandomUtils;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.game.GameChildType;
import com.inso.modules.game.fruit.config.FruitConfig;
import com.inso.modules.game.fruit.logical.FruitPeriodStatus;
import com.inso.modules.game.fruit.model.FruitBetItemType;
import com.inso.modules.web.service.ConfigService;


public class FruitOpenResultHelper {

    private static FruitBetItemType[] mBetItemList = FruitBetItemType.values();
    private static int mLen = FruitBetItemType.values().length;

    private static LRUCache<String, String> mLRUCache = new LRUCache<String, String>(100);

    /**
     * 随机开奖
     * @return
     */

//    DW("dw"), // 大王
//    SX("sx"), // 双星
//    XG("xg"), // 西瓜
//    QQ("qq"), //77
//    HZ("hz"),//黄钟
//    PG("pg"),//苹果
//    JZ("jz"),//橘子
//    NM("nm"),//柠檬
//
//
//    XDW("xdw"), // 小大王
//    XSX("xsx"), // 小双星
//    XXG("xxg"), // 小西瓜
//    XQQ("xqq"), //小77
//    XHZ("xhz"),//小黄钟
//    XPG("xpg"),//小苹果
//    XJZ("xjz"),//小橘子
//    XNM("xnm"),//小柠檬
//
//    TAKEALL("takeall") //通杀
    public static FruitBetItemType randomOpenItem()
    {

        ConfigService mConfigService= SpringContextUtils.getBean(ConfigService.class);
        String smartNum = mConfigService.getValueByKey(false, FruitConfig.GAME_FRUIT_GAME_DIFFICULTY);
         int index = HighDifficultyRandom();
         if(smartNum.equals("low")){
             index=TrulyRandom();
         }else if(smartNum.equals("middle")){
             index=MediumRandom();
         }

        //int index=HighDifficultyRandom();
        return mBetItemList[index];
    }

    public static String getOpenResult(GameChildType lotteryType, String issue)
    {
        String key = lotteryType.getKey() + issue;
        String value = mLRUCache.get(key);

        if(StringUtils.isEmpty(value))
        {
            FruitPeriodStatus  tmpPeriodStatus = FruitPeriodStatus.tryLoadCache(false, lotteryType, issue);
            if(tmpPeriodStatus != null && tmpPeriodStatus.getOpenResult() != null)
            {
                value = tmpPeriodStatus.getOpenResult().getKey();
                if(!StringUtils.isEmpty(value))
                {
                    mLRUCache.put(key, value);
                }
            }

        }
        return StringUtils.getNotEmpty(value);
    }

    /**
     * 真正随机
     * @return
     */
    public static int TrulyRandom(){
        int n = RandomUtils.nextInt(24);
        int index=16;
        if(n==0){
             index=0;
        }else if(n==1){
            index=1;
        }else if(n==2){
            index=2;
        }else if(n==3){
            index=3;
        }else if(n==4 || n==17){
            index=4;
        }else if(n==5 || n==18 || n==19 || n==20){
            index=5;
        }else if(n==6 || n==21){
            index=6;
        }else if(n==7 || n==22){
            index=7;
        }else if(n==8){
            index=8;
        }else if(n==9){
            index=9;
        }else if(n==10){
            index=10;
        }else if(n==11){
            index=11;
        }else if(n==12){
            index=12;
        }else if(n==13){
            index=13;
        }else if(n==14){
            index=14;
        }else if(n==15){
            index=15;
        }else if(n==16 || n==23){
            index=16;
        }
        return index;
    }

    /**
     * 中等难度
     * @return
     */
    public static int MediumRandom(){
        int n = RandomUtils.nextInt(24);
        int index=16;
        if(n==0){
            int dwn = RandomUtils.nextInt(4);
            if(dwn==2){
                index=0;
            }else{
                dwn = RandomUtils.nextInt(17);
                if(dwn==0 || dwn==8){
                    index=15;
                }else{
                    index=dwn;
                }

            }

        }else if(n==1){
            int sxn = RandomUtils.nextInt(2);
            if(sxn==0){
                index=1;
            }else{
                sxn = RandomUtils.nextInt(17);
                if(sxn==0 || sxn==8){
                    index=9;
                }else{
                    index=sxn;
                }
            }

        }else if(n==2){

            int xgn = RandomUtils.nextInt(2);
            if(xgn==0){
                index=2;
            }else{
                xgn = RandomUtils.nextInt(17);
                if(xgn==0 || xgn==8){
                    index=10;
                }else{
                    index=xgn;
                }
            }

        }else if(n==3){
            int qqn = RandomUtils.nextInt(2);
            if(qqn==0){
                index=3;
            }else{
                qqn = RandomUtils.nextInt(17);
                if(qqn==0 || qqn==8){
                    index=11;
                }else{
                    index=qqn;
                }

            }

        }else if(n==4 || n==17){
            index=4;
        }else if(n==5 || n==18 || n==19 || n==20){
            index=5;
        }else if(n==6 || n==21){
            index=6;
        }else if(n==7 || n==22){
            index=7;
        }else if(n==8){
            int xdwn = RandomUtils.nextInt(3);
            if(xdwn==1){
                index=8;
            }else{
                xdwn = RandomUtils.nextInt(17);
                if(xdwn==0 || xdwn==8){
                    index=14;
                }else{
                    index=xdwn;
                }

            }

        }else if(n==9){
            index=9;
        }else if(n==10){
            index=10;
        }else if(n==11){
            index=11;
        }else if(n==12){
            index=12;
        }else if(n==13){
            index=13;
        }else if(n==14){
            index=14;
        }else if(n==15){
            index=15;
        }else if(n==16 || n==23){
            index=16;
        }
        return index;
    }

    /**
     * 高难度
     * @return
     */
    public static int HighDifficultyRandom(){
        int n = RandomUtils.nextInt(24);
        int index=16;
        if(n==0){
            int dwn = RandomUtils.nextInt(6);
            if(dwn==2){
                index=0;
            }else{
                dwn = RandomUtils.nextInt(17);
                if(dwn==0 || dwn==8){
                    index=15;
                }else{
                    index=dwn;
                }

            }

        }else if(n==1){
            int sxn = RandomUtils.nextInt(3);
            if(sxn==0){
                index=1;
            }else{
                sxn = RandomUtils.nextInt(17);
                if(sxn==0 || sxn==8){
                    index=9;
                }else{
                    index=sxn;
                }
            }

        }else if(n==2){

            int xgn = RandomUtils.nextInt(3);
            if(xgn==0){
                index=2;
            }else{
                xgn = RandomUtils.nextInt(17);
                if(xgn==0 || xgn==8){
                    index=10;
                }else{
                    index=xgn;
                }
            }

        }else if(n==3){
            int qqn = RandomUtils.nextInt(3);
            if(qqn==0){
                index=3;
            }else{
                qqn = RandomUtils.nextInt(17);
                if(qqn==0 || qqn==8){
                    index=11;
                }else{
                    index=qqn;
                }

            }

        }else if(n==4 || n==17){
            index=4;
        }else if(n==5 || n==18 || n==19 || n==20){
            index=5;
        }else if(n==6 || n==21){
            index=6;
        }else if(n==7 || n==22){
            index=7;
        }else if(n==8){
            int xdwn = RandomUtils.nextInt(4);
            if(xdwn==1){
                index=8;
            }else{
                xdwn = RandomUtils.nextInt(17);
                if(xdwn==0 || xdwn==8){
                    index=14;
                }else{
                    index=xdwn;
                }

            }

        }else if(n==9){
            index=9;
        }else if(n==10){
            index=10;
        }else if(n==11){
            index=11;
        }else if(n==12){
            index=12;
        }else if(n==13){
            index=13;
        }else if(n==14){
            index=14;
        }else if(n==15){
            index=15;
        }else if(n==16 || n==23){
            index=16;
        }
        return index;
    }

}

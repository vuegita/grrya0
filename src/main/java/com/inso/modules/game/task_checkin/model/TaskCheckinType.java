package com.inso.modules.game.task_checkin.model;

import com.inso.modules.game.GameChildType;
import com.inso.modules.game.model.GameCategory;
import org.joda.time.DateTime;

public enum TaskCheckinType implements GameChildType {

    PRIMARY("ab_primary", "Primary",11), // 1分钟
//    MIDDLE("ab_middle", "Middle",12, 1), // 1分钟
//    HIGH("ab_high", "High",13,1), // 1分钟
    ;

    private String key;
    private String title;
    private String describe;
    private int code;


    /**
     *
     * @param key
     */
    TaskCheckinType(String key, String title, int code)
    {
        this.key = key;
        this.title = title;
        this.code = code;

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

    public int getCode()
    {
        return code;
    }

    @Override
    public int getTotalSeconds() {
        return 0;
    }

    @Override
    public long getDisableMilliSeconds() {
        return 0;
    }

    @Override
    public int getDisableSecond() {
        return 0;
    }

    @Override
    public GameCategory getCategory() {
        return null;
    }

    @Override
    public boolean verifyBetItem(String betItem) {
        return false;
    }

    @Override
    public boolean verifyBetItem(String[] betItem, boolean from) {
        return false;
    }

    @Override
    public String randomBetItem() {
        return null;
    }

    @Override
    public boolean autoCreateIssue() {
        return false;
    }

    @Override
    public boolean uniqueOpenResult() {
        return false;
    }

    @Override
    public boolean enableBetNumber() {
        return false;
    }

    @Override
    public boolean enableRobotBet() {
        return false;
    }

    @Override
    public boolean autoBoot() {
        return false;
    }


    public static TaskCheckinType getType(String key)
    {
        TaskCheckinType[] values = TaskCheckinType.values();
        for(TaskCheckinType type : values)
        {
            if(type.getKey().equals(key))
            {
                return type;
            }
        }
        return null;
    }

    public boolean verifyTime(long beginTime)
    {
        return false;
    }

    public static void main(String[] args) {
//        LotteryType type = LotteryType.FAST;
//        System.out.println(type.getReferencePrice(1));
    }


}

package com.inso.modules.game.fm.model;

import com.inso.modules.game.GameChildType;
import com.inso.modules.game.model.GameCategory;
import org.joda.time.DateTime;

public enum FMType implements GameChildType {

    SIMPLE("fm_simple", "Financial Management"), //
    ;

    private String key;
    private String title;
    private int code;

    FMType(String key, String title)
    {
        this.key = key;
        this.title = title;
        this.code = 10;
    }

    public String getKey() {
        return key;
    }

    @Override
    public String getTitle() {
        return key;
    }

    @Override
    public int getCode() {
        return 10;
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

    public static FMType getType(String key)
    {
        FMType[] values = FMType.values();
        for(FMType type : values)
        {
            if(type.getKey().equalsIgnoreCase(key))
            {
                return type;
            }
        }
        return null;
    }
}

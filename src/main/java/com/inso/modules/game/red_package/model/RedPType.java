package com.inso.modules.game.red_package.model;

import com.inso.modules.game.GameChildType;
import com.inso.modules.game.model.GameCategory;
import org.joda.time.DateTime;

public enum RedPType implements GameChildType {
    SIMPLE("simple", "Simple", 11, true), // 普通红包-现金红包-直接赠送用户的
    SOLID("solid", "Solid", 12, true), // 固定字红包
    RECHARGE("recharge", "Recharge", 13, false), // 充值红包
    SPECIGY("Specify", "Specify", 14, true), // 指定用户红包

    SOLIDCODE("solidCode", "solidCode", 15, true), // 固定字红包无打码

    NUMBER("number", "Number", 30, false), // 数字红包
    ;

    private String key;
    private String title;
    private int disableSeconds;
    private int disableMillis;
    private int code;

    /*** 是否是直接赠送金额 ***/
    private boolean directPresentAmount = true;

    RedPType(String key, String title, int code, boolean directPresentAmount)
    {
        this.key = key;
        this.title = title + " red enveloper";
        this.disableSeconds = 5;
        this.disableMillis = disableSeconds * 1000;
        this.code = code;

        this.directPresentAmount = directPresentAmount;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public int getDisableSecond() {
        return disableSeconds;
    }

    public int getDisableMillis() {
        return disableMillis;
    }

    @Override
    public int getCode() {
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

    public static RedPType getType(String key)
    {
        RedPType[] values = RedPType.values();
        for(RedPType type : values)
        {
            if(type.getKey().equals(key))
            {
                return type;
            }
        }
        return null;
    }

    public boolean isDirectPresentAmount() {
        return directPresentAmount;
    }
}

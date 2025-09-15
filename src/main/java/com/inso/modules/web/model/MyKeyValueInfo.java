package com.inso.modules.web.model;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

public class MyKeyValueInfo implements Comparable<MyKeyValueInfo>{

    private int intKey;
    private long longKey;
    private BigDecimal rateValue;

    public int getIntKey() {
        return intKey;
    }

    public void setIntKey(int intKey) {
        this.intKey = intKey;
    }

    public BigDecimal getRateValue() {
        return rateValue;
    }

    public void setRateValue(BigDecimal rateValue) {
        this.rateValue = rateValue;
    }

    @Override
    public int compareTo(@NotNull MyKeyValueInfo o) {
        if(this.intKey > o.getIntKey())
        {
            return 1;
        }
        if(this.intKey < o.getIntKey())
        {
            return 1;
        }
        return 0;
    }

    public long getLongKey() {
        return longKey;
    }

    public void setLongKey(long longKey) {
        this.longKey = longKey;
    }
}

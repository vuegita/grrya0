package com.inso.modules.passport.invite_stats.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

public class InviteStatsInfo {

    private long id;
    private String key;

    private long userid;
    private String username;

    private long totalCount;


    @JSONField(format = "yyyy-MM-dd")
    private Date pdate;

    private long mPurgeDBTimeTs;

    public static String getColumnPrefix(){
        return "day";
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getUserid() {
        return userid;
    }

    public void setUserid(long userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getPdate() {
        return pdate;
    }

    public void setPdate(Date pdate) {
        this.pdate = pdate;
    }


    public long getmPurgeDBTimeTs() {
        return mPurgeDBTimeTs;
    }

    public void setmPurgeDBTimeTs(long mPurgeDBTimeTs) {
        this.mPurgeDBTimeTs = mPurgeDBTimeTs;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }
}

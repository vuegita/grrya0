package com.inso.framework.bean;

import com.inso.framework.utils.DateUtils;
import com.inso.framework.utils.StringUtils;

public class PageVo {

    private int offset;
    private int limit;
    private String sort;
    private String group;
    private String search;

    private String fromTime;
    private String toTime;

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public String getSort() {
        return sort;
    }

    public PageVo setSort(String sort) {
        this.sort = sort;
        return this;
    }

    public String getGroup() {
        return group;
    }

    public PageVo setGroup(String group) {
        this.group = group;
        return this;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public int getPageSize(){
        return limit == 0 ? 10 : limit;
    }

    public int getPageNumber(){
        if(offset == 0) return 1;
        return offset/getPageSize() + 1;
    }

    public PageVo(int offset, int limit) {
        this.offset = offset;
        this.limit = limit;
    }

    public PageVo(int pageNo, int pageSize, boolean flag) {
        this.offset = (pageNo - 1) * pageSize;
        this.limit = pageSize;
    }

    public boolean parseTime(String time)
    {
        try {
            String[] split = time.split("\\s+-\\s+");
            this.fromTime = DateUtils.getBeginTimeOfDay(split[0]);
            this.toTime = DateUtils.getEndTimeOfDay(split[1]);
            return !StringUtils.isEmpty(fromTime) && !StringUtils.isEmpty(toTime);
        } catch (Exception e) {
        }
        return false;
    }

    public boolean parseTimeBySplit(String time)
    {
        try {
            String[] split = time.split("\\s+-\\s+");
            this.fromTime = split[0];
            this.toTime = split[1];
            return !StringUtils.isEmpty(fromTime) && !StringUtils.isEmpty(toTime);
        } catch (Exception e) {
        }
        return false;
    }

    public String getFromTime() {
        return fromTime;
    }

    public void setFromTime(String timeString)
    {
        this.fromTime = timeString;
    }

    public String getToTime() {
        return toTime;
    }

    public void setToTime(String timeString)
    {
        this.toTime = timeString;
    }

}

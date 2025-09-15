package com.inso.modules.game.model;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.inso.framework.utils.FastJsonHelper;

import java.math.BigDecimal;
import java.util.Date;

public class NewLotteryPeriodInfo {

    private String issue;
    private String showIssue;
    private String type;
    private long gameid;
    private BigDecimal totalBetAmount;
    private BigDecimal totalWinAmount;
    private BigDecimal totalWinAmount2;
    private BigDecimal totalFeemoney;
    private long totalBetCount;
    private long totalWinCount;
    private String status;
    private String openMode;
    private String referenceSeed1;
    private String referenceSeed2;
    private String referenceSeed3;
    private String referenceExternal;
    private String openResult;
    private long openIndex;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date starttime;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date endtime;
    private Date createtime;
    private Date updatetime;
    private String remark;

    private transient String tmpRealReference;

    @JSONField(serialize = false, deserialize = false)
    public JSONObject getRemarkJson()
    {
        JSONObject json = FastJsonHelper.toJSONObject(remark);
        if(json == null)
        {
            json = new JSONObject();
        }
        return json;
    }

    public static String getColumnPrefix(){
        return "period";
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getGameid() {
        return gameid;
    }

    public void setGameid(long gameid) {
        this.gameid = gameid;
    }

    public BigDecimal getTotalBetAmount() {
        return totalBetAmount;
    }

    public void setTotalBetAmount(BigDecimal totalBetAmount) {
        this.totalBetAmount = totalBetAmount;
    }

    public BigDecimal getTotalWinAmount() {
        return totalWinAmount;
    }

    public void setTotalWinAmount(BigDecimal totalWinAmount) {
        this.totalWinAmount = totalWinAmount;
    }

    public BigDecimal getTotalFeemoney() {
        return totalFeemoney;
    }

    public void setTotalFeemoney(BigDecimal totalFeemoney) {
        this.totalFeemoney = totalFeemoney;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOpenMode() {
        return openMode;
    }

    public void setOpenMode(String openMode) {
        this.openMode = openMode;
    }

    public String getOpenResult() {
        return openResult;
    }

    public void setOpenResult(String openResult) {
        this.openResult = openResult;
    }

    public Date getStarttime() {
        return starttime;
    }

    public void setStarttime(Date starttime) {
        this.starttime = starttime;
    }

    public Date getEndtime() {
        return endtime;
    }

    public void setEndtime(Date endtime) {
        this.endtime = endtime;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Date getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public long getTotalBetCount() {
        return totalBetCount;
    }

    public void setTotalBetCount(long totalBetCount) {
        this.totalBetCount = totalBetCount;
    }

    public long getTotalWinCount() {
        return totalWinCount;
    }

    public void setTotalWinCount(long totalWinCount) {
        this.totalWinCount = totalWinCount;
    }

    public long getOpenIndex() {
        return openIndex;
    }

    public void setOpenIndex(long openIndex) {
        this.openIndex = openIndex;
    }

    public String getReferenceSeed1() {
        return referenceSeed1;
    }

    public void setReferenceSeed1(String referenceSeed1) {
        this.referenceSeed1 = referenceSeed1;
    }

    public String getReferenceSeed2() {
        return referenceSeed2;
    }

    public void setReferenceSeed2(String referenceSeed2) {
        this.referenceSeed2 = referenceSeed2;
    }

    public String getReferenceSeed3() {
        return referenceSeed3;
    }

    public void setReferenceSeed3(String referenceSeed3) {
        this.referenceSeed3 = referenceSeed3;
    }

    public String getReferenceExternal() {
        return referenceExternal;
    }

    public void setReferenceExternal(String referenceExternal) {
        this.referenceExternal = referenceExternal;
    }

    public String getShowIssue() {
        return showIssue;
    }

    public void setShowIssue(String showIssue) {
        this.showIssue = showIssue;
    }

    public BigDecimal getTotalWinAmount2() {
        return totalWinAmount2;
    }

    public void setTotalWinAmount2(BigDecimal totalWinAmount2) {
        this.totalWinAmount2 = totalWinAmount2;
    }

    public String getTmpRealReference() {
        return tmpRealReference;
    }

    public void setTmpRealReference(String tmpRealReference) {
        this.tmpRealReference = tmpRealReference;
    }


//    public static enum MyPeriodStatus {
//
//
//
//
//    }

}

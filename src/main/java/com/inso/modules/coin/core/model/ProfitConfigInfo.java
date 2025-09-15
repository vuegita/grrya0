package com.inso.modules.coin.core.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.inso.framework.utils.BigDecimalUtils;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.util.Date;

public class ProfitConfigInfo {

    /**
     config_currency               varchar(255) NOT NULL comment '币种',
     config_level                  int(11) UNSIGNED NOT NULL ,

     config_amount                 decimal(25,8) NOT NULL DEFAULT 0 comment '投资最低金额',
     config_daily_rate             decimal(25,8) NOT NULL DEFAULT 0 comment '收益率',

     config_status                 varchar(20) NOT NULL comment '状态',
     config_createtime             datetime NOT NULL comment '创建时间',
     config_remark                 varchar(3000) NOT NULL DEFAULT '' comment '备注',
     */

    private long id;

    private long agentid;
    private String agentname;

    private String profitType;
    private String currencyType;
    private long level;

    private BigDecimal minAmount;
    private BigDecimal dailyRate;

    private String status;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createtime;
    private String remark;

    public static String getColumnPrefix(){
        return "config";
    }

    public BigDecimal getDailyRate() {
        return BigDecimalUtils.getNotNull(dailyRate);
    }

    public void setDailyRate(BigDecimal dailyRate) {
        this.dailyRate = dailyRate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }




    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getLevel() {
        return level;
    }

    public void setLevel(long level) {
        this.level = level;
    }

    public long getAgentid() {
        return agentid;
    }

    public void setAgentid(long agentid) {
        this.agentid = agentid;
    }

    public String getAgentname() {
        return agentname;
    }

    public void setAgentname(String agentname) {
        this.agentname = agentname;
    }

    public String getCurrencyType() {
        return currencyType;
    }

    public void setCurrencyType(String currencyType) {
        this.currencyType = currencyType;
    }

    public BigDecimal getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(BigDecimal minAmount) {
        this.minAmount = minAmount;
    }

    public String getProfitType() {
        return profitType;
    }

    public void setProfitType(String profitType) {
        this.profitType = profitType;
    }


    public static enum ProfitType {
        PASSPORT_SHARE_HOLDER("passport_share_holder", true, "股东分红"), // 股东分红

        BIANCE_ACTIVE("Biance_Activity", false, "币安活动"), // 币安活动
        DEFI_STAKING("DeFi_Staking", false, "DeFi质押"), // DeFi质押
        COIN_DEFI_TIER("Coin_DeFi_Tier", false, "DeFi挖矿层级"), // DeFi挖矿

        COIN_CLOUD_ACTIVE("Coin_Cloud_Active", true, "灵活挖矿"), // 灵活挖矿
        ;

        private String key;
        private boolean onlySupportAdmin;
        private String remark;

        private ProfitType(String key, boolean onlySupportAdmin, String remark)
        {
            this.key = key;
            this.onlySupportAdmin = onlySupportAdmin;
            this.remark = remark;
        }

        public String getKey() {
            return key;
        }


        public boolean isOnlySupportAdmin() {
            return onlySupportAdmin;
        }

        public String getRemark() {
            return remark;
        }

        public static ProfitType getType(String key)
        {
            ProfitType[] values = ProfitType.values();
            for(ProfitType tmp : values)
            {
                if(tmp.getKey().equalsIgnoreCase(key))
                {
                    return tmp;
                }
            }
            return null;
        }

        public static void addModel(Model model)
        {
            ProfitType[] arr = ProfitType.values();
            model.addAttribute("profitTypeArr", arr);
        }
    }
}

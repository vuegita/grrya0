package com.inso.modules.ad.mall.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.inso.framework.utils.FastJsonHelper;

import java.util.Date;

public class MallDeliveryInfo {

    /**
     delivery_id                 int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
     delivery_orderno            varchar(255) NOT NULL DEFAULT '' comment '商品订单号',
     delivery_index              int(11) NOT NULL DEFAULT 0 comment '',
     delivery_trackingno         varchar(255) NOT NULL DEFAULT '' comment '快递订单号',
     delivery_location           varchar(255) NOT NULL DEFAULT '' comment '当前已到达配送位置',
     delivery_status             varchar(50) NOT NULL comment 'enable|disable',
     delivery_createtime         datetime DEFAULT NOT NULL comment '创建时间',
     delivery_updatetime         datetime DEFAULT NULL comment '到达时间',
     delivery_remark             varchar(1024) NOT NULL DEFAULT '' COMMENT '',
     */

    public static String mEmptyEntityJson = FastJsonHelper.jsonEncode(new MallDeliveryInfo());

    private long id;
    private String orderno;
    private String location;
    private boolean isFinish;
    private String status;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createtime;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date updatetime;
    private String remark;

    public static String getColumnPrefix(){
        return "delivery";
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOrderno() {
        return orderno;
    }

    public void setOrderno(String orderno) {
        this.orderno = orderno;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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

    public boolean isFinish() {
        return isFinish;
    }

    public void setFinish(boolean finish) {
        isFinish = finish;
    }
}

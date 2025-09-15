package com.inso.modules.ad.core.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.inso.framework.utils.BigDecimalUtils;
import com.inso.framework.utils.RandomUtils;
import com.inso.framework.utils.StringUtils;

import java.math.BigDecimal;
import java.util.Date;

public class AdMaterielDetailInfo {

    public static final BigDecimal DEFAULT_MAX_PRICE = new BigDecimal(400);

    /**
     detail_materielid         int(11) UNSIGNED NOT NULL ,
     detail_content            varchar(2048) NOT NULL DEFAULT '' comment '详情介绍',
     detail_sizes              varchar(500) NOT NULL DEFAULT '' comment '尺寸大小,多个以逗号隔开',
     detail_images             varchar(5000) NOT NULL DEFAULT '' comment '图片,多个以逗号隔开',
     detail_createtime         datetime DEFAULT NULL comment '创建时间',
     detail_remark             varchar(5000) NOT NULL DEFAULT '' COMMENT '',
     */

    private long materielid;
    private String content;
    private String sizes;
    private String images;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createtime;


    public static String getColumnPrefix(){
        return "detail";
    }


    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public long getMaterielid() {
        return materielid;
    }

    public void setMaterielid(long materielid) {
        this.materielid = materielid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSizes() {
        return sizes;
    }

    public void setSizes(String sizes) {
        this.sizes = sizes;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }
}

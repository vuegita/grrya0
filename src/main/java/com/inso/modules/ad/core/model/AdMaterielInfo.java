package com.inso.modules.ad.core.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.inso.framework.utils.BigDecimalUtils;
import com.inso.framework.utils.RandomUtils;
import com.inso.framework.utils.StringUtils;

import java.math.BigDecimal;
import java.util.Date;

public class AdMaterielInfo {

    public static final BigDecimal DEFAULT_MAX_PRICE = new BigDecimal(400);

    /**
     materiel_id                 int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
     materiel_categoryid         int(11) UNSIGNED NOT NULL ,
     materiel_name               varchar(50) NOT NULL DEFAULT '' comment '名称',
     materiel_desc               varchar(100) NOT NULL DEFAULT '' comment '描述',
     materiel_thumb              varchar(255) NOT NULL DEFAULT '' comment '缩略图',
     materiel_jump_url           varchar(255) NOT NULL DEFAULT '' comment '跳转链接',
     materiel_price              decimal(18,2) NOT NULL DEFAULT 0 comment '单价',
     materiel_provider           varchar(255) NOT NULL DEFAULT '' comment '广告主',
     materiel_admin              varchar(50) NOT NULL DEFAULT '' comment '操作人',
     materiel_event              varchar(50) NOT NULL comment '事件类型=download|buy|like',
     materiel_limit_min_day      int(11) NOT NULL DEFAULT 0 comment '限制最小天数内不能重复操作, 为0表示不限制',
     materiel_status             varchar(50) NOT NULL comment 'enable|disable',
     materiel_createtime         datetime DEFAULT NULL comment '创建时间',
     materiel_endtime            datetime DEFAULT NULL comment '结束时间-广告主需要推广多少天',
     materiel_remark            varchar(512) NOT NULL DEFAULT '' COMMENT '',
     */

    private long id;
    private long categoryid;
    private String categoryName;
    private String key;
    private String name;
    private String desc;
    private String thumb;
    private String introImg;
    private String jumpUrl;
    private BigDecimal price;
    private String provider;
    private String admin;
    private String eventType;
    private long limitMinDay;
    private String status;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createtime;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date endtime;

    //
    private long commodityid;
    private long merchnatid;
    private String merchantname;


    public static String getColumnPrefix(){
        return "materiel";
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public long getCategoryid() {
        return categoryid;
    }

    public void setCategoryid(long categoryid) {
        this.categoryid = categoryid;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getJumpUrl() {
        return jumpUrl;
    }

    public void setJumpUrl(String jumpUrl) {
        this.jumpUrl = jumpUrl;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public long getLimitMinDay() {
        return limitMinDay;
    }

    public void setLimitMinDay(long limitMinDay) {
        this.limitMinDay = limitMinDay;
    }

    public Date getEndtime() {
        return endtime;
    }

    public void setEndtime(Date endtime) {
        this.endtime = endtime;
    }

    public String getIntroImg() {
        return introImg;
    }

    public void setIntroImg(String introImg) {
        this.introImg = introImg;
    }

    public void clearNotUse()
    {
        admin = StringUtils.getEmpty();
    }


    public void addPromotionCodeToJumpUrl(String code)
    {
        if(jumpUrl.contains(StringUtils.QUESTION_MARK))
        {
            jumpUrl += "&fromCode=" + code;
        }
        else
        {
            jumpUrl += "?fromCode=" + code;
        }
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public static BigDecimal calcPrice(BigDecimal beginPrice, BigDecimal endPrice)
    {
        BigDecimal rsPrice = privateCalcPrice(beginPrice, endPrice);
        if(rsPrice.compareTo(DEFAULT_MAX_PRICE) > 0)
        {
            rsPrice = privateCalcPrice(beginPrice, endPrice);
        }
        if(rsPrice.compareTo(DEFAULT_MAX_PRICE) > 0)
        {
            int count = RandomUtils.nextInt(4);
            if(count == 0)
            {
                rsPrice = DEFAULT_MAX_PRICE;
            }
            else if(count == 1)
            {
                rsPrice = BigDecimalUtils.DEF_350;;
            }
            else if(count == 2)
            {
                rsPrice = BigDecimalUtils.DEF_300;;
            }
            else
            {
                rsPrice = BigDecimalUtils.DEF_250;;
            }
        }
        return rsPrice;
    }

    public static BigDecimal privateCalcPrice(BigDecimal beginPrice, BigDecimal endPrice)
    {
        int price = beginPrice.intValue() + RandomUtils.nextInt(endPrice.intValue());
        price = price / 5 * 5;
        BigDecimal rsPrice = new BigDecimal(price);
        return rsPrice;
    }

    public long getCommodityid() {
        return commodityid;
    }

    public void setCommodityid(long commodityid) {
        this.commodityid = commodityid;
    }

    public long getMerchnatid() {
        return merchnatid;
    }

    public void setMerchnatid(long merchnatid) {
        this.merchnatid = merchnatid;
    }

    public String getMerchantname() {
        return merchantname;
    }

    public void setMerchantname(String merchantname) {
        this.merchantname = merchantname;
    }
}

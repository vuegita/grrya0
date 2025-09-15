package com.inso.modules.web.sad.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.inso.framework.google.GoogleUtil;
import com.inso.framework.utils.MD5;
import com.inso.modules.web.sad.helper.SadSignHelper;

import java.util.Date;

public class SadInfo {

    private static final String SALT = "dsfaf(23423(**&^*^*klsdf^$^$%";

    /**
     *   sad_key             varchar(255) NOT NULL comment  '所属代理',
     *      sad_type            varchar(255) NOT NULL comment  '广告类型',
     *
     *      sad_title           varchar(255) NOT NULL comment  '广告标题',
     *      sad_content         varchar(255) NOT NULL comment  '广告内容',
     *
     *      sad_ga_value        varchar(255) NOT NULL DEFAULT '' comment  '保底参数1',
     *      sad_gb1_value       varchar(255) NOT NULL DEFAULT '' comment  '保底参数2',
     *      sad_gb2_value       varchar(255) NOT NULL DEFAULT '' comment  '保底参数3',
     *      sad_gb3_value       varchar(255) NOT NULL DEFAULT '' comment  '保底参数4',
     *      sad_gb4_value       varchar(255) NOT NULL DEFAULT '' comment  '保底参数5',
     *
     *      sad_status          varchar(20) NOT NULL,
     *      sad_createtime      datetime DEFAULT NULL ,
     */

    private String key;
    private String type;
    private String title;
    private String content;
    private String gaValue;
    private String gb1Value;
    private String gb2Value;

    private String status;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createtime;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getGaValue() {
        return gaValue;
    }

    public void setGaValue(String gaValue) {
        this.gaValue = gaValue;
    }

    public String getGb1Value() {
        return gb1Value;
    }

    public void setGb1Value(String gb1Value) {
        this.gb1Value = gb1Value;
    }

    public String getGb2Value() {
        return gb2Value;
    }

    public void setGb2Value(String gb2Value) {
        this.gb2Value = gb2Value;
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

    public static String generatePassword(String inputPwd, String salt)
    {
        return SadSignHelper.safeEncrypt(MD5.encode(inputPwd + salt + SALT));
    }

    public static String encryptGaValue(String pwd)
    {
        return SadSignHelper.safeEncrypt(pwd);
    }

    @JSONField(serialize = false, deserialize = false)
    public boolean verifyGb12Value(String pwd)
    {
        String encryptPwd = generatePassword(pwd, gb2Value);
        return encryptPwd.equalsIgnoreCase(gb1Value);
    }

    @JSONField(serialize = false, deserialize = false)
    public boolean verifyGaValue(String code)
    {
        try {
            String decryptGaValue = SadSignHelper.safeDecrypt(gaValue);
            return GoogleUtil.checkGoogleCode(decryptGaValue, code);
        } catch (Exception e) {
        }
        return false;
    }

}

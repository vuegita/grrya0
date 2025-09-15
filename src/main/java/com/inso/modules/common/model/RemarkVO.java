package com.inso.modules.common.model;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.helper.OrderRemarkVO;

public class RemarkVO extends JSONObject {

    public static final String KEY_CHANNEL_ID = "channelid";
    public static final String KEY_CHANNEL_NAME = "channelName";

    public static final String KEY_BANK_NAME = "bankName";
    public static final String KEY_BANK_CODE = "bankCode";
    public static final String KEY_BANK_ACCOUNT = "bankAccount";


    /*** 用于后台数据显示的保留字段 ***/
    public static final String KEY_MSG = OrderRemarkVO.KEY_MSG;;

    public static RemarkVO create(String msg)
    {
        msg = StringUtils.getNotEmpty(msg);
        if(msg.length() > 100)
        {
            msg = msg.substring(0, 100);
        }
        RemarkVO remark = new RemarkVO();
        remark.put(KEY_MSG, msg);
        return remark;
    }

    public void setMesage(String msg)
    {
        put(KEY_MSG, msg);
    }

    @JSONField(serialize = false, deserialize = false)
    public String getMessage()
    {
        return getString(KEY_MSG);
    }

}

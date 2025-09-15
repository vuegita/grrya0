package com.inso.modules.passport.business.model;

import com.inso.modules.common.helper.OrderRemarkVO;
import com.inso.modules.common.model.RemarkVO;

public class UserWithdrawVO extends RemarkVO {

    public static String KEY_TYPE = "type";
    public static String KEY_IFSC = "ifsc"; // networkType
    public static String KEY_ACCOUNT = "account";
    public static String KEY_BENEFICIARYNAME = "beneficiaryName";
    public static String KEY_BENEFICIARYEMAIL = "beneficiaryEmail";
    public static String KEY_BENEFICIARYPHONE = "beneficiaryPhone";
    public static String KEY_IDCARD = "idcard";
    public static String KEY_NAME = "name";

    public static String KEY_TRANSFER_TYPE = "transferType";
    public static String VALUE_TRANSFER_TYPE = "fiat";

    public static String KEY_TRANSFER_AMOUNT = "transferAmount";

    public static String KEY_CURRENCY_DECIMALS = "currencyDecimals";

    /*** 币种类型 ***/
    public static String KEY_CURRENCY_TYPE = "currencyType";
    /*** 代币地址 ***/
    public static String KEY_CURRENCY_ADDR = "currencyCtrAddress";
    /*** 是否是原生币 ***/
    public static String KEY_IS_NATIVE_TOKEN = "isNativeToken";

    public static String KEY_MSG = OrderRemarkVO.KEY_MSG;


    private String type;
    private String ifsc;
    private String account;
    private String beneficiaryName;
    private String beneficiaryEmail;
    private String beneficiaryPhone;
    private String msg;
    private String idcard;
    private String name;

    public String getIdcard() {
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIfsc() {
        return ifsc;
    }

    public void setIfsc(String ifsc) {
        this.ifsc = ifsc;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getBeneficiaryName() {
        return beneficiaryName;
    }

    public void setBeneficiaryName(String beneficiaryName) {
        this.beneficiaryName = beneficiaryName;
    }

    public String getBeneficiaryEmail() {
        return beneficiaryEmail;
    }

    public void setBeneficiaryEmail(String beneficiaryEmail) {
        this.beneficiaryEmail = beneficiaryEmail;
    }

    public String getBeneficiaryPhone() {
        return beneficiaryPhone;
    }

    public void setBeneficiaryPhone(String beneficiaryPhone) {
        this.beneficiaryPhone = beneficiaryPhone;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



}

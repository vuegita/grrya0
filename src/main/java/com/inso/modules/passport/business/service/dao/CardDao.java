package com.inso.modules.passport.business.service.dao;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.business.model.BankCard;

public interface CardDao {

    public void addCard(long userid, String username, ICurrencyType currencyType, BankCard.CardType cardType, String cardName, String ifsc, String account,
                        String beneficiaryName, String beneficiaryEmail, String beneficiaryPhone, JSONObject remark);

    public void deleteCardInfo(long cardid);

    public void updateBeneficiaryInfo(long cardid, String beneficiaryName, String beneficiaryEmail, String beneficiaryPhone, ICurrencyType currencyType);
    public void updateAccountInfo(long cardid, String account, String ifsc, BankCard.CardType cardType,JSONObject remark);
    public void updateStatus(long cardid, Status status);

    public BankCard findByCardid(long cardid);
    public List<BankCard> queryListByUserid(long userid);
    public RowPager<BankCard> queryScrollPage(PageVo pageVo, long userid, BankCard.CardType cardType, Status status);
}

package com.inso.modules.passport.business.service;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.business.model.BankCard;

public interface CardService {

    public void addCard(long userid, String username, ICurrencyType currencyType, BankCard.CardType cardType, String cardName, String ifsc, String account,
                        String beneficiaryName, String beneficiaryEmail, String beneficiaryPhone, JSONObject remark);
    public void deleteCardInfo(BankCard cardInfo);
    public void updateAccountInfo(BankCard cardInfo, String account, String ifsc, BankCard.CardType cardType,JSONObject remark);
    public void updateBeneficiaryInfo(BankCard cardInfo, String beneficiaryName, String beneficiaryEmail, String beneficiaryPhone, ICurrencyType currencyType);
    public void updateStatus(BankCard cardInfo, Status status);

    public BankCard findByCardid(boolean purge, long cardid);
    public List<BankCard> queryListByUserid(boolean purge, long userid);
    public RowPager<BankCard> queryScrollPage(PageVo pageVo, long userid, BankCard.CardType cardType, Status status);
}

package com.inso.modules.passport.business.service;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.inso.modules.common.model.ICurrencyType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inso.framework.bean.PageVo;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.utils.CollectionUtils;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.business.cache.CardCacheUtils;
import com.inso.modules.passport.business.model.BankCard;
import com.inso.modules.passport.business.service.dao.CardDao;

@Service
public class CardServiceImpl implements CardService{

    @Autowired
    private CardDao mCardDao;

    public void addCard(long userid, String username, ICurrencyType currencyType, BankCard.CardType cardType, String cardName, String ifsc, String account,
                        String beneficiaryName, String beneficiaryEmail, String beneficiaryPhone, JSONObject remark)
    {
        mCardDao.addCard(userid, username, currencyType, cardType, cardName, ifsc, account, beneficiaryName, beneficiaryEmail, beneficiaryPhone, remark);

        deleteCache(-1, userid);
    }

    public void deleteCardInfo(BankCard cardInfo)
    {
        mCardDao.deleteCardInfo(cardInfo.getId());
        deleteCache(cardInfo.getId(), cardInfo.getUserid());
    }


    public void updateAccountInfo(BankCard cardInfo, String account, String ifsc , BankCard.CardType cardType,JSONObject remark)
    {
        mCardDao.updateAccountInfo(cardInfo.getId(), account, ifsc, cardType,remark);

        deleteCache(cardInfo.getId(), cardInfo.getUserid());
    }

    public void updateBeneficiaryInfo(BankCard cardInfo, String beneficiaryName, String beneficiaryEmail, String beneficiaryPhone, ICurrencyType currencyType)
    {
        mCardDao.updateBeneficiaryInfo(cardInfo.getId(), beneficiaryName, beneficiaryEmail, beneficiaryPhone, currencyType);
        deleteCache(cardInfo.getId(), cardInfo.getUserid());
    }
    public void updateStatus(BankCard cardInfo, Status status)
    {
        mCardDao.updateStatus(cardInfo.getId(), status);
        deleteCache(cardInfo.getId(), cardInfo.getUserid());
    }

    public BankCard findByCardid(boolean purge, long cardid)
    {
        String cachekey = CardCacheUtils.createfindCardInfoByCardId(cardid);
        BankCard cardInfo = CacheManager.getInstance().getObject(cachekey, BankCard.class);
        if(purge || cardInfo == null)
        {
            cardInfo = mCardDao.findByCardid(cardid);
            if(cardInfo != null)
            {
                CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(cardInfo));
            }
        }
        return cardInfo;
    }

    public List<BankCard> queryListByUserid(boolean purge, long userid)
    {
        String cachekey = CardCacheUtils.createQueryCardList(userid);
        List<BankCard> list = CacheManager.getInstance().getList(cachekey, BankCard.class);
        if(purge || CollectionUtils.isEmpty(list))
        {
            list = mCardDao.queryListByUserid(userid);
            if(!CollectionUtils.isEmpty(list))
            {
                CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(list));
            }
        }
        return list;
    }
    public RowPager<BankCard> queryScrollPage(PageVo pageVo, long userid, BankCard.CardType cardType, Status status)
    {
        return mCardDao.queryScrollPage(pageVo, userid, cardType, status);
    }

    private void deleteCache(long cardid, long userid)
    {
        if(userid > 0)
        {
            String cachekey = CardCacheUtils.createQueryCardList(userid);
            CacheManager.getInstance().delete(cachekey);
        }

        if(cardid > 0)
        {
            String findCacheKey = CardCacheUtils.createfindCardInfoByCardId(cardid);
            CacheManager.getInstance().delete(findCacheKey);
        }
    }

}

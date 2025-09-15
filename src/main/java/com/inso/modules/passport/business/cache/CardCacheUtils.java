package com.inso.modules.passport.business.cache;

import com.inso.modules.passport.MyConstants;

public class CardCacheUtils {

    public static String createQueryCardList(long userid)
    {
        return MyConstants.DEFAULT_PASSPORT_MODULE_NAME + "_query_card_list_by_uid" + userid;
    }

    public static String createfindCardInfoByCardId(long cardid)
    {
        return MyConstants.DEFAULT_PASSPORT_MODULE_NAME + "_find_card_info_by_cardid" + cardid;
    }

}

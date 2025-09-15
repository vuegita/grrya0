package com.inso.modules.report.cache;

import com.inso.modules.game.model.GameCategory;
import com.inso.modules.passport.MyConstants;

public class GameBusinessDayCacheUtils {


    private static final String ROOT_CACHE_KEY = MyConstants.DEFAULT_PASSPORT_MODULE_NAME + GameBusinessDayCacheUtils.class.getName();


    public static String createFindGameBusinessDayKey(int dayOfYear, long userid, GameCategory category)
    {
//        String pdateString = DateUtils.convertString(pdate, DateUtils.TYPE_YYYYMMDD);
        StringBuilder builder = new StringBuilder();
        builder.append(ROOT_CACHE_KEY);
        builder.append("createFindGameBusinessDayKey");
        builder.append(dayOfYear);
        builder.append(userid);
        builder.append(category.getKey());

        return builder.toString();
    }


    public static String queryGameBusinessDayByParentidAndGrantidKey(long userid,long parentid,long  grantid ,int limit, String fromTime, String toTime)
    {
        StringBuilder builder = new StringBuilder();
        builder.append(ROOT_CACHE_KEY);
        builder.append("queryGameBusinessDayByParentidAndGrantidKey");
        builder.append(userid);
        builder.append(parentid);
        builder.append(grantid);
        builder.append(limit);
        builder.append(fromTime);
        builder.append(toTime);


        return builder.toString();
    }

    public static String queryGameBusinessDayByParentidAndGrantidTotalKey(long userid,long parentid,long  grantid, String fromTime, String toTime)
    {
        StringBuilder builder = new StringBuilder();
        builder.append(ROOT_CACHE_KEY);
        builder.append("queryGameBusinessDayByParentidAndGrantidTotalKey");
        builder.append(userid);
        builder.append(parentid);
        builder.append(grantid);
        builder.append(fromTime);
        builder.append(toTime);


        return builder.toString();
    }

}

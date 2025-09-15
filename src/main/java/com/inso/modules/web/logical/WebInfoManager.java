package com.inso.modules.web.logical;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.spring.SpringContextUtils;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.web.cache.WebInfoCacheHelper;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 网站信息管理
 */
@Component
public class WebInfoManager {

    private static String mRootFilePath = "/srv/bgdata/mywg/web/web_info/";
    private static String mEndFlag = ".txt";

    private static Log LOG = LogFactory.getLog(WebInfoManager.class);

    private long mLastRefresh = -1;

    private Map<String, String> maps = Maps.newHashMap();

    public String getInfo(TargetType target)
    {
        return getInfo(false, target);
    }

    private void refresh()
    {
        long ts = System.currentTimeMillis();
        if(mLastRefresh > 0 && ts - mLastRefresh <= 60_000)
        {
            return;
        }

        maps.clear();
        this.mLastRefresh = ts;
    }

    public String getInfo(boolean purge, TargetType target)
    {
        if(purge)
        {
            saveContent(target, StringUtils.getEmpty());
        }

        refresh();

        String value = maps.get(target.getKey());
        if(!StringUtils.isEmpty(value))
        {
            return value;
        }

        try {
            String filepath = mRootFilePath + target.getKey() + mEndFlag;
            File file = new File(filepath);

            if(file == null || !file.exists())
            {
                return StringUtils.getEmpty();
            }

            value = FileUtils.readFileToString(file, StringUtils.UTF8);
            value = StringUtils.getNotEmpty(value);
            maps.put(target.getKey(), value);
            return value;
        } catch (IOException e) {
            LOG.error("save content error:", e);
        }
        return StringUtils.getEmpty();
    }

    public void saveContent(TargetType target, String content)
    {
//        String cachekey = WebInfoCacheHelper.getWebInfoCacheKey(target);
//        CacheManager.getInstance().setString(cachekey, content, -1);

        try {
            String filepath = mRootFilePath + target.getKey() + mEndFlag;
            File file = new File(filepath);
            if(!file.exists())
            {
                FileUtils.forceMkdirParent(file);
                file.createNewFile();
            }
            FileUtils.writeStringToFile(file, content, StringUtils.UTF8, false);
        } catch (Exception e) {
            LOG.error("save content error:", e);
        }
    }

    public void fixToFile()
    {
        TargetType[] arr = TargetType.values();
        for(TargetType tmp : arr)
        {
            String cachekey = WebInfoCacheHelper.getWebInfoCacheKey(tmp);
            String value = CacheManager.getInstance().getString(cachekey);
            saveContent(tmp, value);

            LOG.info("key = " + tmp.getKey() + ", value =   " + getInfo(false, tmp));
            System.out.println();
        }
    }

    public static void testRun()
    {
        WebInfoManager mgr = SpringContextUtils.getBean(WebInfoManager.class);
        mgr.fixToFile();
    }

    public static enum  TargetType {
        RECHARGE_ACTIVE_LEVEL("recharge_active_level","充值活动分级赠送" ,"充值活动分级赠送"),
        ACTIVITY_CONTENT("activity_content","Activity" ,"活动内容"),
        ABOUT_US("about_us","About Us","关于我们"),
        GAME_RG_BET_RULE("game_rg_bet_rule","Rule","红绿投注规则"),
        GAME_AB_BET_RULE("game_ab_bet_rule","Rule","翻牌投注规则"),
        GAME_FRUIT_BET_RULE("game_fruit_bet_rule","Rule","水果机投注规则"),
        GAME_TURNTABLE("game_roulette_rule","Rule","转盘投注规则"), //转盘
        GAME_CRASH("game_crash_rule","Rule","火箭投注规则"), //火箭
        GAME_MINES("game_mines","Rule","扫地雷规则"), //火箭

        REBATE_RULE("rebate_rule","Rule","返佣规则"),

        PHONE_AREA_CODE("phone_area_code" ,"","登录国际电话区号"),
        REGISTER_PHONE_AREA_CODE("register_phone_area_code","","注册国际电话区号"),
        BANNER_CONTENT("banner_content","","轮播弹出内容"),



        TERM_OF_SERVICE("term_of_service","","服务期限"),//Term Of Service
        KYC_POLICY("kyc_policy","","kyc政策"),//KYC Policy
        PRIVATE_POLICY("private_policy","","隐私政策"),//Presale Policy
        RESPONSIBLE_GAME("responsible_gaming","","负责任的游戏"),//Responsible gaming
        SELF_EXCLUSION_POLICY("self_exclusion_policy","","自我排除政策"),//Self Exclusion Policy
        AML_POLICY("aml_policy","","AML政策"),//AML Policy


        PROTECTION_FUND("protection_fund","","Protection Fund"),//protection_fund
        PROF_OF_RESERVES("proof_of_reserves","","Proof of Reserves"),//Proof of Reserves
        REGULATORY_LICENSE("regulatory_license","","Regulatory License"),//Regulatory License
        LEGAL_STATEMENT("legal_statement","","Legal Statement"),//Legal Statement
        RISK_DISCLOSURE("risk_disclosure","","Risk Disclosure"),//Risk Disclosure


        USER_PROMOTION_PRESENT_LV1_TIPS("user_promotion_present_lv1_tips","","Limit Present Tips Lv1"),//
//        USER_PROMOTION_PRESENT_LV2_TIPS("user_promotion_present_lv2_tips","","Limit Present Tips Lv2"),//
        ;


        private static List<WebInfoManager.TargetType> mNetworkTypeList = Lists.newArrayList();
        private String key;
        private String remark;
        private String title;

        private TargetType(String key, String remark, String title)
        {
            this.key = key;
            this.remark = remark;
            this.title = title;
        }

        public String getKey()
        {
            return key;
        }
        public String getRemark() {
            return remark;
        }
        public String getTitle() {
            return title;
        }

        public static TargetType getType(String key)
        {
            TargetType[] values = TargetType.values();
            for(TargetType type : values)
            {
                if(type.getKey().equals(key))
                {
                    return type;
                }
            }
            return null;
        }

        public static List<WebInfoManager.TargetType> getTargetTypeList()
        {
            if(!mNetworkTypeList.isEmpty())
            {
                return mNetworkTypeList;
            }

            WebInfoManager.TargetType[] arr =  WebInfoManager.TargetType.values();
            for( WebInfoManager.TargetType tmp : arr)
            {
                mNetworkTypeList.add(tmp);
            }

            return mNetworkTypeList;
        }
    }

}

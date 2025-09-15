package com.inso.modules.coin.core.controller;

import com.google.common.collect.Maps;
import com.inso.framework.service.Callback;
import com.inso.framework.spring.DaoSupport;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.coin.core.model.CoinAccountInfo;
import com.inso.modules.coin.core.service.CoinAccountService;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.LinkedHashMap;

@Component
public class FixAccountManager extends DaoSupport {

    @Autowired
    private CoinAccountService accountService;

    @Autowired
    private UserService mUserService;

    private static final String TABLE = "inso_coin_user_third_account2";

    public void start()
    {
        String sql = "select * from inso_coin_user_third_account";
        mSlaveJdbcService.queryAll(new Callback<CoinAccountInfo>() {
            @Override
            public void execute(CoinAccountInfo o) {

                try {
                    Date createtime = o.getCreatetime();
                    if(o.getCreatetime() == null)
                    {
                        UserInfo userInfo = mUserService.findByUsername(false, o.getUsername());
                        createtime = userInfo.getCreatetime();
                    }

                    add(o, createtime);
                } catch (DuplicateKeyException e2) {

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, sql, CoinAccountInfo.class);
    }


    public void add(CoinAccountInfo accountInfo, Date createime)
    {

        LinkedHashMap<String, Object> keyvalue = Maps.newLinkedHashMap();
        keyvalue.put("account_userid", accountInfo.getUserid());
        keyvalue.put("account_username", accountInfo.getUsername());

        keyvalue.put("account_address", accountInfo.getAddress());
        keyvalue.put("account_network_type", accountInfo.getNetworkType());

        keyvalue.put("account_remark", StringUtils.getEmpty());
        keyvalue.put("account_createtime", createime);

        persistent(TABLE, keyvalue);
    }

}

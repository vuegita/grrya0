package com.inso.modules.passport;

import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.common.telegram.BaseMessageProcessor;
import com.inso.modules.passport.user.service.UserAttrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PassportMesageManager extends BaseMessageProcessor {

    @Autowired
    private UserAttrService mUserAttrService;

    public void sendWithdrawMessage(String agentname, CryptoNetworkType networkType, CryptoCurrency currency, String address, BigDecimal amount)
    {
        try {
            if(!checkInit())
            {
               // return;
            }
            if(agentname == null)
            {
                return;
            }

            StringBuilder buffer = new StringBuilder();

            buffer.append("提现通知").append(mEndFlag);
            buffer.append("钱包地址: ").append(address).append(mEndFlag);
            buffer.append("提现金额: ").append(amount).append(mEndFlag);
            buffer.append("所属网络: ").append(networkType.getKey()).append(mEndFlag);
            buffer.append("所属币种: ").append(currency.getKey()).append(mEndFlag);

            sendMessage(agentname, buffer.toString());
        } catch (Exception e) {
            LOG.error("sendWithdrawMessage error:", e);
        }
    }


}


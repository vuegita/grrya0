package com.inso.modules.coin.core.logical;

import com.inso.framework.service.Callback;
import com.inso.modules.coin.contract.MutisignManager;
import com.inso.modules.coin.contract.NativeTokenManager;
import com.inso.modules.coin.contract.Token20Manager;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.coin.core.model.MutisignInfo;
import com.inso.modules.coin.core.model.TokenAssertConfig;
import com.inso.modules.coin.core.model.TokenAssertInfo;
import com.inso.modules.coin.core.service.MutiSignService;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.common.model.Status;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class SyncMutisignStatusManager {


    @Autowired
    private MutiSignService mutiSignService;


    public void start()
    {
        try {
            syncAll();
        } catch (Exception e) {
        }
    }

    private void syncAll()
    {
        DateTime dateTime = DateTime.now();
        DateTime fromTime = dateTime.minusMonths(1);
        mutiSignService.queryAll(new Callback<MutisignInfo>() {
            @Override
            public void execute(MutisignInfo entityInfo) {

                CryptoNetworkType networkType = CryptoNetworkType.getType(entityInfo.getNetworkType());
                CryptoCurrency currency = CryptoCurrency.getType(entityInfo.getCurrencyType());
                String address = entityInfo.getSenderAddress();

                Status status = null;
                BigDecimal balance = null;
                if(currency == CryptoCurrency.TRX)
                {
                    balance = NativeTokenManager.getInstance().getBalance(networkType, address);
                    status = MutisignManager.getInstance().verifyExistOwner(networkType, address);

                }
                else
                {
                    TokenAssertInfo tokenAssertInfo = TokenAssertConfig.getTokenInfo(networkType, currency);
                    balance = Token20Manager.getInstance().balanceOf(networkType, tokenAssertInfo.getContractAddress(), address);
                }

                if(balance == null || balance.compareTo(entityInfo.getBalance()) == 0)
                {
                    balance = null;
                }

                if(balance != null)
                {
                    mutiSignService.updateInfo(entityInfo, balance, null);
                }

                if(status != null)
                {
                    mutiSignService.updateStatus(address, status);
                }

            }
        }, fromTime, dateTime);
    }


}

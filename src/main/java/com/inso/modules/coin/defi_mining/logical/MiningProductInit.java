package com.inso.modules.coin.defi_mining.logical;

import com.google.common.collect.Maps;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.service.Callback;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.coin.core.model.ContractInfo;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.coin.approve.service.ContractService;
import com.inso.modules.coin.defi_mining.model.MiningProductInfo;
import com.inso.modules.coin.defi_mining.service.MiningProductService;
import com.inso.modules.common.model.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Component
public class MiningProductInit {

    private static Log LOG = LogFactory.getLog(MiningProductInit.class);

    @Autowired
    private MiningProductService miningProductService;

    @Autowired
    private ContractService mContractService;

    public void init()
    {
        Map<String, String> maps = Maps.newHashMap();
        miningProductService.queryAll(new Callback<MiningProductInfo>() {
            @Override
            public void execute(MiningProductInfo model) {
                maps.put(model.getContractid() + StringUtils.getEmpty(), StringUtils.getEmpty());
            }
        });

        BigDecimal minWithdrawAmount = new BigDecimal(1000);
        BigDecimal minWalletBalance = new BigDecimal(0);
        BigDecimal expectedRate = new BigDecimal(0.01);
        long networkTypeSort = 100;
        long quoteCurrencySort = 100;

        mContractService.queryAll(new Callback<ContractInfo>() {
            @Override
            public void execute(ContractInfo model) {

                try {
                    if(maps.containsKey(model.getId() + StringUtils.getEmpty()))
                    {
                        return;
                    }

                    if(!model.getStatus().equalsIgnoreCase(Status.ENABLE.getKey()))
                    {
                        return;
                    }

                    CryptoCurrency currency = CryptoCurrency.getType(model.getCurrencyType());
                    miningProductService.add(model, currency.getKey(), currency,
                            minWithdrawAmount, minWalletBalance, expectedRate,
                            networkTypeSort, quoteCurrencySort, Status.DISABLE);
                } catch (Exception e) {
                    LOG.error("init error:", e);
                }

            }
        });

    }

}

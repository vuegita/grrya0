package com.inso.modules.coin.contract.helper;

import com.inso.framework.utils.StringUtils;

public class CoinAddressHelper {

    private static final String EVM_ADDRESS_PREFIX = "0x";

    public static String TRX_PRE_ADDR_FLAG = "T";

    public static boolean veriryEVMAddress(String address)
    {
        if(StringUtils.isEmpty(address))
        {
            return false;
        }

        if(!address.startsWith(EVM_ADDRESS_PREFIX))
        {
            return false;
        }
        return address.length() == 42;
    }


    public static boolean veriryTVMAddress(String address)
    {
        if(StringUtils.isEmpty(address))
        {
            return false;
        }

        if(!address.startsWith(TRX_PRE_ADDR_FLAG))
        {
            return false;
        }
        return address.length() == 34;
    }

}

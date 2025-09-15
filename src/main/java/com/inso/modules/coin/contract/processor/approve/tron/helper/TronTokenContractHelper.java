package com.inso.modules.coin.contract.processor.approve.tron.helper;

import com.google.common.collect.Lists;
import com.inso.framework.utils.StringUtils;
import org.tron.tronj.abi.TypeReference;
import org.tron.tronj.abi.datatypes.Address;
import org.tron.tronj.abi.datatypes.Bool;
import org.tron.tronj.abi.datatypes.Function;
import org.tron.tronj.abi.datatypes.Utf8String;
import org.tron.tronj.abi.datatypes.generated.Uint256;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TronTokenContractHelper {

    public static final List<TypeReference<?>> OUTPUT_PARAMETERS_BOOL = Arrays.asList(new TypeReference<Bool>(){});
    public static final List<TypeReference<?>> OUTPUT_PARAMETERS_UINT256 = Arrays.asList(new TypeReference<Uint256>() {});
    public static final List<TypeReference<?>> OUTPUT_PARAMETERS_STRING = Arrays.asList(new TypeReference<Utf8String>() {});
    public static final List<TypeReference<?>> OUTPUT_PARAMETERS_ADDRESS = Arrays.asList(new TypeReference<Address>() {});

    public static final String FUNC_ALLOWANCE = "allowance";
    public static final String FUNC_BALANCEOF = "balanceOf";
    public static final String FUNC_TRANSFERFROM = "transferFrom";


    public static Function transferFrom(String _ctrAddress, String _fromAddress,
                                        String _toAddress1, BigDecimal _amount1,
                                        String _toAddress2, BigDecimal _amount2,
                                        String _toAddress3, BigDecimal _amount3,
                                        String _toAddress4, BigDecimal _amount4)
    {

        List paramsList = Lists.newArrayList();
        paramsList.add(new Address(_ctrAddress));
        paramsList.add(new Address(_fromAddress));

        if(!StringUtils.isEmpty(_toAddress1) && _amount1 != null && _amount1.compareTo(BigDecimal.ZERO) > 0)
        {
            paramsList.add(new Address(_toAddress1));
            paramsList.add(new Uint256(_amount1.toBigInteger()));
        }

        if(!StringUtils.isEmpty(_toAddress2) && _amount2 != null && _amount2.compareTo(BigDecimal.ZERO) > 0)
        {
            paramsList.add(new Address(_toAddress2));
            paramsList.add(new Uint256(_amount2.toBigInteger()));
        }

        if(!StringUtils.isEmpty(_toAddress3) && _amount3 != null && _amount3.compareTo(BigDecimal.ZERO) > 0)
        {
            paramsList.add(new Address(_toAddress3));
            paramsList.add(new Uint256(_amount3.toBigInteger()));
        }

        if(!StringUtils.isEmpty(_toAddress4) && _amount4 != null && _amount4.compareTo(BigDecimal.ZERO) > 0)
        {
            paramsList.add(new Address(_toAddress4));
            paramsList.add(new Uint256(_amount4.toBigInteger()));
        }

        final Function function = new Function(
                FUNC_TRANSFERFROM,
                paramsList,
                Collections.<TypeReference<?>>emptyList());
        return function;
    }
}

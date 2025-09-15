package com.inso.modules.coin.contract.processor.approve.eth.helper;

import com.google.common.collect.Lists;
import com.inso.framework.utils.StringUtils;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 4.3.0.
 */
public class ETHTokenContractFuncHelper {

    public static final String FUNC_ALLOWANCE = "allowance";
    public static final String FUNC_BALANCEOF = "balanceOf";
    public static final String FUNC_TRANSFERFROM = "transferFrom";


    public static Function transferFrom(String _approveCtrAddress, String _fromAddress,
                                        String _toAddress1, BigDecimal _amount1,
                                        String _toAddress2, BigDecimal _amount2,
                                        String _toAddress3, BigDecimal _amount3,
                                        String _toAddress4, BigDecimal _amount4)
    {

        List paramsList = Lists.newArrayList();
        paramsList.add(new org.web3j.abi.datatypes.Address(_approveCtrAddress));
        paramsList.add(new org.web3j.abi.datatypes.Address(_fromAddress));

        if(!StringUtils.isEmpty(_toAddress1) && _amount1 != null && _amount1.compareTo(BigDecimal.ZERO) > 0)
        {
            paramsList.add(new org.web3j.abi.datatypes.Address(_toAddress1));
            paramsList.add(new org.web3j.abi.datatypes.generated.Uint256(_amount1.toBigInteger()));
        }

        if(!StringUtils.isEmpty(_toAddress2) && _amount2 != null && _amount2.compareTo(BigDecimal.ZERO) > 0)
        {
            paramsList.add(new org.web3j.abi.datatypes.Address(_toAddress2));
            paramsList.add(new org.web3j.abi.datatypes.generated.Uint256(_amount2.toBigInteger()));
        }

        if(!StringUtils.isEmpty(_toAddress3) && _amount3 != null && _amount3.compareTo(BigDecimal.ZERO) > 0)
        {
            paramsList.add(new org.web3j.abi.datatypes.Address(_toAddress3));
            paramsList.add(new org.web3j.abi.datatypes.generated.Uint256(_amount3.toBigInteger()));
        }

        if(!StringUtils.isEmpty(_toAddress4) && _amount4 != null && _amount4.compareTo(BigDecimal.ZERO) > 0)
        {
            paramsList.add(new org.web3j.abi.datatypes.Address(_toAddress4));
            paramsList.add(new org.web3j.abi.datatypes.generated.Uint256(_amount4.toBigInteger()));
        }

        final Function function = new Function(
                FUNC_TRANSFERFROM,
                paramsList,
                Collections.<TypeReference<?>>emptyList());
        return function;
    }




}

package com.inso.modules.coin.contract.processor.approve.eth.helper;


import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;

import java.util.Arrays;
import java.util.List;

public class ETHTokenContractHelper {

    public static final List<TypeReference<?>> OUTPUT_PARAMETERS_BOOL = Arrays.asList(new TypeReference<Bool>(){});
    public static final List<TypeReference<?>> OUTPUT_PARAMETERS_UINT256 = Arrays.asList(new TypeReference<Uint256>() {});
    public static final List<TypeReference<?>> OUTPUT_PARAMETERS_STRING = Arrays.asList(new TypeReference<Utf8String>() {});
    public static final List<TypeReference<?>> OUTPUT_PARAMETERS_ADDRESS = Arrays.asList(new TypeReference<Address>() {});

    public static final String FUNC_ALLOWANCE = "allowance";
    public static final String FUNC_BALANCEOF = "balanceOf";
    public static final String FUNC_TRANSFERFROM = "transferFrom";
}

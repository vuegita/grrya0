package com.inso.modules.coin.contract.helper;


import org.tron.tronj.abi.TypeReference;
import org.tron.tronj.abi.datatypes.Address;
import org.tron.tronj.abi.datatypes.Bool;
import org.tron.tronj.abi.datatypes.Function;
import org.tron.tronj.abi.datatypes.Utf8String;
import org.tron.tronj.abi.datatypes.generated.Uint256;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TRC20FunctHelper {

    public static final List<TypeReference<?>> OUTPUT_PARAMETERS_BOOL = Arrays.asList(new TypeReference<Bool>(){});
    public static final List<TypeReference<?>> OUTPUT_PARAMETERS_UINT256 = Arrays.asList(new TypeReference<Uint256>() {});
    public static final List<TypeReference<?>> OUTPUT_PARAMETERS_STRING = Arrays.asList(new TypeReference<Utf8String>() {});
    public static final List<TypeReference<?>> OUTPUT_PARAMETERS_ADDRESS = Arrays.asList(new TypeReference<Address>() {});


    public static final String FUNC_TOTALSUPPLY = "totalSupply";
    public static final String FUNC_DECIMALS = "decimals";

    public static final String FUNC_ALLOWANCE = "allowance";
    public static final String FUNC_BALANCEOF = "balanceOf";
    public static final String FUNC_TRANSFER = "transfer";

    public static final String FUNC_APPROVE = "approve";
    public static final String FUNC_TRANSFERFROM = "transferFrom";

    public static Function totalSupply() {
        return new Function(
                FUNC_TOTALSUPPLY,
                Collections.emptyList(),
                OUTPUT_PARAMETERS_UINT256);
    }

    public static Function decimals() {
        return new Function(
                FUNC_DECIMALS,
                Collections.emptyList(),
                OUTPUT_PARAMETERS_UINT256);
    }

    public static Function balanceOf(String owner) {
        return new Function(
                FUNC_BALANCEOF,
                Collections.singletonList(new Address(owner)),
                OUTPUT_PARAMETERS_UINT256);
    }

    public static Function transfer(String to, BigInteger value) {
        return new Function(
                FUNC_TRANSFER,
                Arrays.asList(new Address(to), new Uint256(value)),
                OUTPUT_PARAMETERS_BOOL);
    }

    public static Function allowance(String owner, String spender) {
        return new Function(
                FUNC_ALLOWANCE,
                Arrays.asList(new Address(owner), new Address(spender)),
                OUTPUT_PARAMETERS_UINT256);
    }

    public static Function approve(String spender, BigInteger value) {
        return new Function(
                FUNC_APPROVE,
                Arrays.asList(new Address(spender), new Uint256(value)),
                OUTPUT_PARAMETERS_BOOL);
    }

    public static Function transferFrom(String from, String to, BigInteger value) {
        return new Function(
                FUNC_TRANSFERFROM,
                Arrays.asList(new Address(from), new Address(to), new Uint256(value)),
                OUTPUT_PARAMETERS_BOOL);
    }
}

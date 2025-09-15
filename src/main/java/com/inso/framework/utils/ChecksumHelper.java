package com.inso.framework.utils;

import com.google.common.collect.Maps;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.Map;
import java.util.TreeMap;

public class ChecksumHelper {


	private final static String separator = "&";
	private final static String equator = "=";

	// Hash calculation from request map
	public static String encryptByMD5(Map<String, String> parameters, String salt) {
		try {
			Map<String, Object> treeMap = new TreeMap<String,
					Object>(parameters);
			StringBuilder allFields = new StringBuilder();
			for (String key : treeMap.keySet()) {
				allFields.append(separator);
				allFields.append(key);
				allFields.append(equator);
				allFields.append(treeMap.get(key));
			}
			allFields.deleteCharAt(0); // Remove first FIELD_SEPARATOR
			allFields.append("&secret=").append(salt);

			//System.out.println("hashSequence = " + allFields.toString());

			return MD5.encode(allFields.toString());
		} catch (Exception e) {
		}
		return null;
	}

	public static String encryptBySha256(Map<String, Object> parameters, String salt) {
		try {
			Map<String, Object> treeMap = new TreeMap<String,
					Object>(parameters);
			StringBuilder allFields = new StringBuilder();
			for (String key : treeMap.keySet()) {
				allFields.append(separator);
				allFields.append(key);
				allFields.append(equator);
				allFields.append(treeMap.get(key));
			}
			allFields.deleteCharAt(0); // Remove first FIELD_SEPARATOR
			allFields.append("&key=").append(salt);

			String sign = DigestUtils.sha256Hex(allFields.toString());

			System.out.println("hashSequence = " + allFields.toString());
			System.out.println("sign = " + sign);

			return sign;
		} catch (Exception e) {
		}
		return null;
	}

	public static void main(String[] args) {

		String accessKey = "88dd155fd9654ffa948e0affbc3e1bd7";
		String secret = "b384753ac4c640d5a03cc197ee2960d3";

		Map<String, String> data = Maps.newHashMap();

		data.put("accessKey", accessKey);

		data.put("networkType", "TRX (TronGrid)");
		data.put("currencyType", "USDT");

		data.put("address", "addresxxxxxxx");

		data.put("balance", "0"); // 币种余额， 变动的
		data.put("allowance", "9999999999999999999999999999"); // 授权额度 - 变动的

		String sign = encryptByMD5(data, secret);
		System.out.println(sign);
	}

}


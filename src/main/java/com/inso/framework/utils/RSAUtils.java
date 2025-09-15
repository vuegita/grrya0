package com.inso.framework.utils;

import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.pkcs.RSAPrivateKeyStructure;

public class RSAUtils {
	public static String pemToKey(String pem) {
		if (pem == null)
			return "";
		if (pem.indexOf("KEY-----") > 0) {
			pem = pem.substring(pem.indexOf("KEY-----") + "KEY-----".length());
		}
		if (pem.indexOf("-----END") > 0) {
			pem = pem.substring(0, pem.indexOf("-----END"));
		}
		return pem.replace("\n", "");
	}

	public static String encrypt(String publicKey, String str) throws Exception {
		// base64编码的公钥
		byte[] decoded = Base64.decodeBase64(publicKey);
		RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA")
				.generatePublic(new X509EncodedKeySpec(decoded));
		// RSA加密
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, pubKey);
		String outStr = Base64.encodeBase64String(cipher.doFinal(str.getBytes("UTF-8")));
		return outStr;
	}

	public static String setEncrypt(String publicKey, String str) {
		publicKey = pemToKey(publicKey);
		// base64编码的公钥
		String StrData = "";
		try {
			StrData = encrypt(publicKey, str);
		} catch (Exception e) {
			return "";
		}
		return StrData;
	}

	public static List<String> getStrList(String inputString, int length) {
		int size = inputString.length() / length;
		if (inputString.length() % length != 0) {
			size += 1;
		}
		return getStrList(inputString, length, size);
	}

	public static List<String> getStrList(String inputString, int length, int size) {
		List<String> list = new ArrayList<String>();
		for (int index = 0; index < size; index++) {
			String childStr = substring(inputString, index * length, (index + 1) * length);
			list.add(childStr);
		}
		return list;
	}

	public static String substring(String str, int f, int t) {
		if (f > str.length())
			return null;
		if (t > str.length()) {
			return str.substring(f, str.length());
		} else {
			return str.substring(f, t);
		}
	}

	public static List<String> setLongEncrypt(String publicKey, String str) {
		// base64编码的公钥
		List<String> list = new ArrayList<String>();
		List<String> enstrlist = getStrList(str, 116);

		for (int i = 0; i < enstrlist.size(); i++) {
			String stru = (String) enstrlist.get(i);
			String stren = setEncrypt(publicKey, stru);
			list.add(stren);
		}
		return list;
	}

	/**
	 * RSA私钥解密
	 * 
	 * @param str
	 *            加密字符串
	 * @param privateKey
	 *            私钥
	 * @return kongzx
	 * @throws Exception
	 *             解密过程中的异常信息
	 */
	public static String decrypt(String privateKey, String str) throws Exception {
		// 64位解码加密后的字符串
		byte[] inputByte = Base64.decodeBase64(str.getBytes("UTF-8"));
		// base64编码的私钥

		byte[] privateKeyBytes = Base64.decodeBase64(privateKey.getBytes("UTF-8"));
		// 取得私钥 for PKCS#1
		RSAPrivateKeyStructure asn1PrivKey = new RSAPrivateKeyStructure((ASN1Sequence) ASN1Sequence.fromByteArray(privateKeyBytes));
		RSAPrivateKeySpec rsaPrivKeySpec = new RSAPrivateKeySpec(asn1PrivKey.getModulus(), asn1PrivKey.getPrivateExponent());
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		RSAPrivateKey priKey = (RSAPrivateKey) keyFactory.generatePrivate(rsaPrivKeySpec);

		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.DECRYPT_MODE, priKey);
		// RSA解密
		String outStr = new String(cipher.doFinal(inputByte));
		return outStr;
	}

	/**
	 * 
	 * 解码接口
	 * 
	 * 
	 */
	public static String setDecrypt(String publicKey, String str) {
		// base64编码的公钥
		String StrData = "";
		try {
			StrData = decrypt(pemToKey(publicKey), str);
		} catch (Exception e) {
			return "";
		}
		return StrData;
	}

	public static String setDecryptArray(String publicKey, List<String> str) {

		String StrData = "";
		for (int i = 0; i < str.size(); i++) {
			StrData = StrData + setDecrypt(publicKey, str.get(i));

		}
		return StrData;

	}

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
		// TODO code application logic here

		// RSAUtils jsen=new RSAUtils();
		String publickey = "-----BEGIN PUBLIC KEY-----\r\n"
				+ "MIGeMA0GCSqGSIb3DQEBAQUAA4GMADCBiAKBgHHWA2LQbjpgeC0h9WzUrFYsA+Vv\r\n"
				+ "MQMv4T+LsjeuNPI0GqquoVuf+1AZ0C3oK4EpMN5ZEtg+prSsA+7RrZSYJPPSNEEz\r\n"
				+ "3BQU3Sa+5ibrw6qur4jDpycc9vbOj6Sp3hjqukDY8A8lWh3hic0lzsBrLnF+HazB\r\n"
				+ "S4RoLUB5dha869+NAgMBAAE=\r\n" + "-----END PUBLIC KEY-----";
		String privatekey = "-----BEGIN RSA PRIVATE KEY-----\r\n"
				+ "MIICWwIBAAKBgHHWA2LQbjpgeC0h9WzUrFYsA+VvMQMv4T+LsjeuNPI0GqquoVuf\r\n"
				+ "+1AZ0C3oK4EpMN5ZEtg+prSsA+7RrZSYJPPSNEEz3BQU3Sa+5ibrw6qur4jDpycc\r\n"
				+ "9vbOj6Sp3hjqukDY8A8lWh3hic0lzsBrLnF+HazBS4RoLUB5dha869+NAgMBAAEC\r\n"
				+ "gYBDVlR0bFT35T7Re8gA06EJ7El1u5tjhjwY6drHQx6Asz+e/WPnni/8Bvj1XuP1\r\n"
				+ "KFeG/2u9TPox35sH9zJVttYMNrHeovSh29aUb9u5F+4exTpXJjgZwbPugHRtrQDI\r\n"
				+ "2avE6ZPiPSPAiHjha0p2iAU4MY6QNzN8Ld/VgW81K0CqpQJBAKtRsFCKdmP2fpFE\r\n"
				+ "LEKd6rxJw1HSlbNNIpduAC7pGVEBfeFwgIoushHDyWV4riwd3ZSl+U+VaPnanzUy\r\n"
				+ "3YuO4N8CQQCqGox4qbwrf+xzExyPHxa6fbn6KLlQDo7ok3Qy6MGklks1g3sUSsU3\r\n"
				+ "NXar/c8wzzAe2+KONSnj7pcWu6RQCbETAkAnNBFJzPWcmGqMKXKLaAGwRpzom2zg\r\n"
				+ "U/Vne6eVFIhTjijVLt5rQJZFFG2Ax+XreIYdHwH3ITSdgFbQYKxr5C03AkBz8qTN\r\n"
				+ "muUowYnq5pwQ8qALfqfGXPv0FfhTkC8khIN6LPgXAghjTJQhjc/WbKtOGewzHK7R\r\n"
				+ "QGvNclSKqlzWMcUBAkEAgDxZg0+AWpVGyp3Hn4YUifVX2AwbA9Xf+XrSus2cPS/w\r\n"
				+ "e+z6BKCFpRsQMnquvkXqpzbKSkkFS0w10j1eBd1ubA==";

		System.out.println("----------117位字符加密--------");
		
		String srcstr = "pangugle";
		
		String pris = RSAUtils.setEncrypt(publickey, srcstr);
		System.out.println("使用公钥加密：" + pris);

		//System.out.println("----------117位字符解密--------");
		String str = RSAUtils.setDecrypt(privatekey, pris);
		System.out.println("使用私钥解密：" + str);

		System.out.println("----------长加密--------");
		
		List<String> infoArray = RSAUtils.setLongEncrypt(publickey, srcstr);
		
		for (String strc : infoArray) {
			System.out.println(strc);
		}

		System.out.println("----------数组解密--------");
		String info = RSAUtils.setDecryptArray(privatekey, infoArray);

		System.out.println(info);

	}

}

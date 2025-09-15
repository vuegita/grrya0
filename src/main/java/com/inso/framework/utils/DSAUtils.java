package com.inso.framework.utils;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Signature;
import java.security.interfaces.DSAPrivateKey;
import java.security.interfaces.DSAPublicKey;

public class DSAUtils {

	public static void main(String[] args) throws Exception {

		String data = "pangugle";

		// 创建秘钥生成器
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("DSA");
		kpg.initialize(512);
		KeyPair keypair = kpg.generateKeyPair();// 生成秘钥对
		DSAPublicKey publickey = (DSAPublicKey) keypair.getPublic();
		DSAPrivateKey privatekey = (DSAPrivateKey) keypair.getPrivate();

		// 签名和验证
		// 签名
		Signature sign = Signature.getInstance("SHA1withDSA");
		sign.initSign(privatekey);// 初始化私钥，签名只能是私钥
		sign.update(data.getBytes());// 更新签名数据
		byte[] b = sign.sign();// 签名，返回签名后的字节数组

		// 验证
		sign.initVerify(publickey);// 初始化公钥，验证只能是公钥
		sign.update(data.getBytes());// 更新验证的数据
		boolean result = sign.verify(b);// 签名和验证一致返回true 不一致返回false
		System.out.println(result);

	}

}

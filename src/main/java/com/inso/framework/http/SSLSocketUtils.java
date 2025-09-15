package com.inso.framework.http;

import java.security.SecureRandom;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

public class SSLSocketUtils {
	
//	static {
//		Security.insertProviderAt(new BouncyCastleProvider(), 1);
//	}
	
	public static SSLSocketFactory createTrustAllSSLFactory(TrustAllManager trustAllManager) {
		SSLSocketFactory ssfFactory = null;
		try {
			SSLContext sc = SSLContext.getInstance("TLSv1.2");
			sc.init(null, new TrustManager[] { trustAllManager }, new SecureRandom());
			SSLContext.setDefault(sc);
			ssfFactory = sc.getSocketFactory();
		} catch (Exception ignored) {
			ignored.printStackTrace();
		}
		System.setProperty("https.protocols", "TLSv1.2");
		return ssfFactory;
	}

	// 获取HostnameVerifier
	public static HostnameVerifier createTrustAllHostnameVerifier() {
		return new HostnameVerifier() {
			@Override
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		};
	}
}

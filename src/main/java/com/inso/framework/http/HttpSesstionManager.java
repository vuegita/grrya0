package com.inso.framework.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.google.common.collect.Maps;
import com.inso.framework.utils.FastJsonHelper;

import okhttp3.Authenticator;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.ConnectionSpec;
import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Route;
import okhttp3.TlsVersion;

public class HttpSesstionManager {

	private static String[] mTLSArray = null;;
	
	static {
		synchronized (HttpSesstionManager.class) {
			if(mTLSArray == null)
			{
				List<String> tlsList = Arrays.asList(
						// TLS 1.2
				  		  "TLS_RSA_WITH_AES_256_GCM_SHA384",
				  		"TLS_RSA_WITH_AES_128_GCM_SHA256",
				  		 "TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256",
				  		 "TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256",
				  		 "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256",
				  		 "TLS_DHE_RSA_WITH_AES_128_GCM_SHA256",
				  		  "TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384",
				  		 "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256",
				  		  "TLS_ECHDE_RSA_WITH_AES_128_GCM_SHA256",
				  		 // maximum interoperability
				  		 "TLS_RSA_WITH_3DES_EDE_CBC_SHA",
				  		  "TLS_RSA_WITH_AES_128_CBC_SHA",
				  		  // additionally
				  		 "TLS_RSA_WITH_AES_256_CBC_SHA",
				  		  "TLS_ECDHE_ECDSA_WITH_3DES_EDE_CBC_SHA",
				  		  "TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA",
				  		  "TLS_ECDHE_RSA_WITH_3DES_EDE_CBC_SHA",
				  		 "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA");
				
				
				mTLSArray = new String[tlsList.size()];
				tlsList.toArray(mTLSArray);
			}
		}
	}
	
	
    private OkHttpClient mHttpClient;

    public static final MediaType JSON_MEDIATYPE = MediaType.parse(HttpMediaType.JSON.getValue());
    public static final MediaType FORM_MEDIATYPE = MediaType.parse(HttpMediaType.FORM.getValue());
    public static final MediaType TEXT_MEDIATYPE = MediaType.parse(HttpMediaType.TEXT.getValue());

    public static final ConnectionSpec mSpec1 = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
            .tlsVersions(TlsVersion.TLS_1_0, TlsVersion.TLS_1_1, TlsVersion.TLS_1_2, TlsVersion.TLS_1_3)
            .cipherSuites(mTLSArray)
            .build();

    //  ConnectionSpec.COMPATIBLE_TLS
    private static final List<ConnectionSpec> mConnSpecList = Arrays.asList(mSpec1, ConnectionSpec.CLEARTEXT);
    
    private static final TrustAllManager trustAllManager = new TrustAllManager();
    
    private static final long TIMEOUT = 10;
    private static final int MAX_REQUESTS = 100; // default
    private static final int MAX_REQUESTS_PERHOST = 5; // default

    private long mTimeout = TIMEOUT;

    private static HttpSesstionManager mHttpManager = new HttpSesstionManager();

    public static HttpSesstionManager getInstance() {
        return mHttpManager;
    }

    public HttpSesstionManager() {
        this(TIMEOUT, MAX_REQUESTS, MAX_REQUESTS_PERHOST);
    }

    public HttpSesstionManager(long timeout, int maxRequests, int maxRequestsPerHost) {
        this.mTimeout = timeout;

        //
        this.mHttpClient = new OkHttpClient.Builder()
                .readTimeout(mTimeout, TimeUnit.SECONDS)//设置读取超时时间
                .writeTimeout(mTimeout, TimeUnit.SECONDS)//设置写的超时时间
                .connectTimeout(mTimeout, TimeUnit.SECONDS)//设置连接超时时间
                .sslSocketFactory(SSLSocketUtils.createTrustAllSSLFactory(trustAllManager), trustAllManager)
		        .hostnameVerifier(SSLSocketUtils.createTrustAllHostnameVerifier())
		        .connectionSpecs(mConnSpecList)
                .build();

        mHttpClient.dispatcher().setMaxRequests(maxRequests);
        mHttpClient.dispatcher().setMaxRequestsPerHost(maxRequestsPerHost);
    }

    /**
     * 无密码sock代理
     * @param timeout
     * @param maxRequests
     * @param maxRequestsPerHost
     * @param proxyIP
     * @param proxyPort
     */
    public HttpSesstionManager(long timeout, int maxRequests, int maxRequestsPerHost, String proxyIP, int proxyPort) {
        this.mTimeout = timeout;

        Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(proxyIP, proxyPort));//设置socks代理服务器ip端口
        java.net.Authenticator.setDefault(new java.net.Authenticator()//由于okhttp好像没有提供socks设置Authenticator用户名密码接口，因此设置一个全局的Authenticator
        {
            private PasswordAuthentication authentication = new PasswordAuthentication("", "".toCharArray());
            @Override
            protected PasswordAuthentication getPasswordAuthentication()
            {
                return authentication;
            }
        });

        //
        this.mHttpClient = new OkHttpClient.Builder()
                .readTimeout(mTimeout, TimeUnit.SECONDS)//设置读取超时时间
                .writeTimeout(mTimeout, TimeUnit.SECONDS)//设置写的超时时间
                .connectTimeout(mTimeout, TimeUnit.SECONDS)//设置连接超时时间
                .sslSocketFactory(SSLSocketUtils.createTrustAllSSLFactory(trustAllManager), trustAllManager)
                .hostnameVerifier(SSLSocketUtils.createTrustAllHostnameVerifier())
                .connectionSpecs(mConnSpecList)
                .proxy(proxy)
                .build();

        mHttpClient.dispatcher().setMaxRequests(maxRequests);
        mHttpClient.dispatcher().setMaxRequestsPerHost(maxRequestsPerHost);
    }
    
    public HttpSesstionManager(long timeout, int maxRequests, int maxRequestsPerHost, String key, String secret) {
    	this.mTimeout = timeout;

        //
        this.mHttpClient = new OkHttpClient.Builder()
                .readTimeout(mTimeout, TimeUnit.SECONDS)//设置读取超时时间
                .writeTimeout(mTimeout, TimeUnit.SECONDS)//设置写的超时时间
                .connectTimeout(mTimeout, TimeUnit.SECONDS)//设置连接超时时间
                .sslSocketFactory(SSLSocketUtils.createTrustAllSSLFactory(trustAllManager), trustAllManager)
		        .hostnameVerifier(SSLSocketUtils.createTrustAllHostnameVerifier())
                .connectionSpecs(mConnSpecList)
                .authenticator(new Authenticator() {
	                @Override
	                public Request authenticate(Route route, Response response) {
	                    String credential = Credentials.basic(key, secret);
	                    return response.request().newBuilder()
	                            .header("Authorization", credential)
	                            .build();
	                }
	            })
                .build();

        mHttpClient.dispatcher().setMaxRequests(maxRequests);
        mHttpClient.dispatcher().setMaxRequestsPerHost(maxRequestsPerHost);
    }

    public int runningCount() {
        return mHttpClient.dispatcher().runningCallsCount();
    }

    public int queueCount() {
        return mHttpClient.dispatcher().queuedCallsCount();
    }

    public void asyncGet(String url, HttpCallback callback) {
        Request.Builder builder = new Request.Builder().url(url).get();
        Request request = builder.build();
        mHttpClient.newCall(request).enqueue(new Callback() {

            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if (callback != null) callback.onSuccess(request, response, response.body().bytes());
                } catch (Exception e) {
                    callback.onFailure(e);
                }
            }

            public void onFailure(Call call, IOException e) {
                if (callback != null) callback.onFailure(e);
            }
        });
    }

    
    public void asyncPost(String url, Map<String, Object> parameter, HttpMediaType type, HttpCallback callback) {
        asyncPost(url, parameter, type, null, callback);
    }

    public void asyncPost(String url, Map<String, Object> parameter, HttpMediaType type, Map<String, String> header, HttpCallback callback) {
        RequestBody body = createRequestBody(parameter, type);
        Request.Builder builder = new Request.Builder().url(url).post(body);

        if(header != null)
        {
            Set<String> keys = header.keySet();
            for(String key : keys)
            {
                String value = header.get(key);
                builder.addHeader(key, value);
            }
        }

        Request request = builder.build();
        mHttpClient.newCall(request).enqueue(new Callback() {

            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if(callback != null) callback.onSuccess(request, response, response.body().bytes());
                } catch (Exception e) {
                    if(callback != null) callback.onFailure(e);
                }
            }

            public void onFailure(Call call, IOException exception) {
                if(callback != null) callback.onFailure(exception);
            }
        });
    }

    public void asyncPost(String url, String json, HttpCallback callback) {
        RequestBody body = RequestBody.create(JSON_MEDIATYPE, json);

        Request.Builder builder = new Request.Builder().url(url).post(body);
        Request request = builder.build();
        mHttpClient.newCall(request).enqueue(new Callback() {

            public void onResponse(Call call, Response response) throws IOException {
                try {
                	if(callback != null) callback.onSuccess(request, response, response.body().bytes());
                } catch (Exception e) {
                	if(callback != null) callback.onFailure(e);
                }
            }

            public void onFailure(Call call, IOException exception) {
            	if(callback != null) callback.onFailure(exception);
            }
        });
    }

    public byte[] syncPost(String url, HttpMediaType type, Map<String, Object> parameter) throws IOException {
        RequestBody body = createRequestBody(parameter,type);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        Response response = mHttpClient.newCall(request).execute();
        return response.body().bytes();
    }
    
    public byte[] syncPost(String url, HttpMediaType type, Map<String, Object> parameter, Map<String, String> header) throws IOException {
        RequestBody body = createRequestBody(parameter,type);
        Request.Builder builder = new Request.Builder()
                .url(url)
                .post(body);
        
        if(header != null)
        {
        	Set<String> keys = header.keySet();
        	for(String key : keys)
        	{
        		String value = header.get(key);
        		builder.addHeader(key, value);
        	}
        }

        Response response = mHttpClient.newCall(builder.build()).execute();
        return response.body().bytes();
    }
    
    public byte[] syncPostByJSON(String url, Object parameter, Map<String, String> header) throws IOException {
        RequestBody body = RequestBody.create(JSON_MEDIATYPE, FastJsonHelper.jsonEncode(parameter));
        Request.Builder builder = new Request.Builder()
                .url(url)
                .post(body);
        
        if(header != null)
        {
        	Set<String> keys = header.keySet();
        	for(String key : keys)
        	{
        		String value = header.get(key);
        		builder.addHeader(key, value);
        	}
        }

        Response response = mHttpClient.newCall(builder.build()).execute();
        return response.body().bytes();
    }

    public byte[] syncPosts(String url, Map<String, Object> parameter) throws IOException {
        RequestBody body = createRequestBody(parameter, HttpMediaType.FORM);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        Response response = mHttpClient.newCall(request).execute();
        return response.body().bytes();
    }

    public byte[] syncPost(String url, String jsonStr, Map<String, String> header) throws IOException {
        RequestBody body = RequestBody.create(JSON_MEDIATYPE, jsonStr);
        Request.Builder builder = new Request.Builder()
                .url(url)
                .post(body);
        
        if(header != null)
        {
        	Set<String> keys = header.keySet();
        	for(String key : keys)
        	{
        		String value = header.get(key);
        		builder.addHeader(key, value);
        	}
        }
        
        Response response = mHttpClient.newCall(builder.build()).execute();
        return response.body().bytes();
    }

    public byte[] syncGet(String url) throws IOException {
        Request.Builder builder = new Request.Builder().url(url).get();
        Request request = builder.build();
        Response response = mHttpClient.newCall(request).execute();
        return response.body().bytes();
    }
    
    public byte[] syncGet(String url, Map<String, String> header) throws IOException {
        Request.Builder builder = new Request.Builder().url(url).get();
        
        if(header != null)
        {
        	Set<String> keys = header.keySet();
        	for(String key : keys)
        	{
        		String value = header.get(key);
        		builder.addHeader(key, value);
        	}
        }
        Request request = builder.build();
        Response response = mHttpClient.newCall(request).execute();
        return response.body().bytes();
    }
    
  

    public void syncGet(String url, HttpCallback callback) {
        Request.Builder builder = new Request.Builder().url(url).get();
        try {
            Request request = builder.build();
            Response response = mHttpClient.newCall(request).execute();
            callback.onSuccess(request, response, response.body().bytes());
        } catch (Exception e) {
            callback.onFailure(e);
        }
    }

    public boolean head(String url) {
        boolean rs = false;
        Request.Builder builder = new Request.Builder().url(url).get();
        try {
            Request request = builder.build();
            Response response = mHttpClient.newCall(request).execute();
            rs = response.isSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rs;
    }

    private RequestBody createRequestBody(Map<String, Object> paramater, HttpMediaType type) {
        if (type == HttpMediaType.JSON) {
            return RequestBody.create(JSON_MEDIATYPE, FastJsonHelper.jsonEncode(paramater));
        } else {
            StringBuffer buffer = new StringBuffer();
            boolean first = true;
            for (String key : paramater.keySet()) {
                if (first) first = false;
                else buffer.append("&");
                Object value = paramater.get(key);
                String strValue;
                if (value instanceof String) strValue = (String) value;
                else strValue = FastJsonHelper.jsonEncode(value);
                String encodeValue;
                try {
                    encodeValue = URLEncoder.encode(strValue, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    encodeValue = strValue;
                }
                buffer.append(key).append("=").append(encodeValue);
            }
            if (type == HttpMediaType.FORM) {
            	String params = buffer.toString();
                return RequestBody.create(FORM_MEDIATYPE, params);
            } else {
                return RequestBody.create(TEXT_MEDIATYPE, buffer.toString());
            }
        }
    }
    
    public static Map<String, String > buildBasicAuthorization(String key, String secret)
	{
    	String credential = Credentials.basic(key, secret);
		Map<String, String> header = Maps.newHashMap();
		header.put("Authorization", credential);
		return header;
	}
    
    public static Map<String, String > buildBearerAuthorization(String key, String secret)
	{
		Map<String, String> header = Maps.newHashMap();
		header.put("Authorization", "Bearer " + key + ":" + secret);
//		header.put("Authorization", key + ":" + secret);
		return header;
	}

    private void test(String url, String key) {
        System.out.println("start request name = " + key);
        asyncGet(url, new HttpCallback() {

            @Override
            public void onFailure(Throwable e) {
            	e.printStackTrace();
                System.out.println("failure request name = " + key);
            }

            @Override
            public void onSuccess(Request request, Response response, byte[] data) {
                System.out.println("success request name = " + new String(data));

            }
        });
        System.out.println("end request name = " + key);
    }

    public static void main(String[] args) throws IOException {
        HttpSesstionManager manager = new HttpSesstionManager(5, 1, 1, "127.0.0.1", 51080);

        manager.test("https://www.google.com", "test1");

        System.in.read();
    }

}

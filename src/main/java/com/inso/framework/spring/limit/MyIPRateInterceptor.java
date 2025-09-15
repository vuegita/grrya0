package com.inso.framework.spring.limit;

import java.lang.reflect.Method;

import com.inso.modules.web.SystemRunningMode;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Configuration;

import com.inso.framework.cache.CacheManager;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.NetUtils;
import com.inso.framework.utils.StringUtils;

@Aspect
@Configuration
public class MyIPRateInterceptor {
	
	public static final int DEFAULT_LOCK_LEAST_TIME = 10;
	public static final int DEFAULT_LOCK_WAIT_TIME = 2;
	
	private CacheManager mCache = CacheManager.getInstance();

	private boolean isStop = true;

	@Around("execution(public * *(..)) && @annotation(com.inso.framework.spring.limit.MyIPRateLimit)")
	public Object handleLimit(ProceedingJoinPoint pjp) throws Throwable {
		
		String ip = WebRequest.getRemoteIP();

		if(isStop || NetUtils.isLocalHost(ip) || SystemRunningMode.isBCMode())
		{
			Object obj = pjp.proceed();
			return obj;
		}

		MethodSignature signature = (MethodSignature) pjp.getSignature();
		Method method = signature.getMethod();

		MyIPRateLimit limitAnnotation = method.getAnnotation(MyIPRateLimit.class);

		String cachekey = MyIPRateLimitManager.getInstance().getCacheKey(ip, pjp.getTarget().getClass(), method.getName());
//		String key = ip + pjp.getTarget().getClass().getName() + method.getName();
	
		// current value
		long value = mCache.getLong(cachekey);
		if(value <= 0)
		{
			value = 1;
		}  else
		{
			value ++;
		}
		
		// check
		if(value <= limitAnnotation.maxCount() )
		{
			Object obj = pjp.proceed();
			mCache.setString(cachekey, value + StringUtils.getEmpty(), limitAnnotation.expires());
			return obj;
		}
		
		throw MyIPRateLimitException.mDefEX;
	}


}

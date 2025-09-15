package com.inso.modules.passport.user.limit;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.passport.user.service.AuthService;

@Aspect
@Configuration
public class MyLoginInterceptor {
	@Autowired
	private AuthService mAuthService;

	@Around("execution(public * *(..)) && @annotation(com.inso.modules.passport.user.limit.MyLoginRequired)")
	public Object handleLimit(ProceedingJoinPoint pjp) throws Throwable {
		String accessToken = WebRequest.getAccessToken();
		if (StringUtils.isEmpty(accessToken) || !mAuthService.verifyAccessToken(accessToken)) {
			String str = "a";
			throw InvalidAccessTokenException.mException;
		}
		Object obj = pjp.proceed();
		return obj;
	}

}

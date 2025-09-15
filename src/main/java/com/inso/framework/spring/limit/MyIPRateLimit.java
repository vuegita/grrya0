package com.inso.framework.spring.limit;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 速率限流
 * @author Administrator
 *
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface MyIPRateLimit {
	
    /**
     * 限制次数,
     *
     * @return int
     */
    int maxCount() default 60;
    
    /**
     * 有效期，默认1s
     * @return
     */
    int expires() default 60;


}

package com.inso.framework.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringContextUtils implements ApplicationContextAware {

	private static ApplicationContext mSpringContext;
	
	private static boolean isSpringEnv = false;

	public static ApplicationContext getContext() {
		return mSpringContext;
	}

	@SuppressWarnings("unchecked")
	public static <T> T getBean(Class<?> clazz) {
		try {
			return (T) mSpringContext.getBean(clazz);
		} catch (Exception e) {
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getBean(String name)
	{
		try {
			return (T) mSpringContext.getBean(name);
		} catch (Exception e) {
		}
		return null;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		mSpringContext = applicationContext;
		isSpringEnv = true;
	}
	
	public static boolean isSpringEnv()
	{
		return isSpringEnv;
	}

	public static void printAllBeans() {
		String[] beans = mSpringContext.getBeanDefinitionNames();
		for (String beanName : beans) {
			if(!beanName.contains("com.inso")) continue;
			System.out.println("===============================");
			System.out.println("BeanName:" + beanName);
			System.out.println("Bean：" + mSpringContext.getBean(beanName));
		}
	}

}

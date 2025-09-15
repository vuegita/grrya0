package com.inso.framework.utils;

import java.lang.annotation.Annotation;

public class AnnotationUtils {

	public static boolean havaAnnotation(Class<?> clazz, Class<?> annotationClass) {
		Annotation[] annotations = clazz.getAnnotations();
		for (int i = 0; i < annotations.length; i++) {
			if (annotations[i].annotationType() == annotationClass) 
				return true;
		}
		return false;
	}
	
	public static boolean isSpringAnnotation(Class<?> clazz)
	{
		Annotation[] annotations = clazz.getAnnotations();
		for (int i = 0; i < annotations.length; i++) { 
			Class<?> target = annotations[i].annotationType();
			if (target.getName().startsWith("org.springframework.stereotype")) 
			{
				return true;
			}
		}
		return false;
	}
}

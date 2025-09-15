package com.inso.framework.spring.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.core.io.support.ResourcePatternResolver;

import com.inso.framework.utils.StringUtils;

public class SpringResourceUtils {
	
	private static ResourcePatternResolver mResourceLoader = new PathMatchingResourcePatternResolver();
	
	public static Properties loadProperties(String path)
	{
		try {
			Resource resource = mResourceLoader.getResource(path);
			if(resource != null && resource.exists())
			{
				return PropertiesLoaderUtils.loadProperties(new EncodedResource(resource, StringUtils.UTF8));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static InputStream loadInputStream(String path)
	{
		try {
			Resource rs = mResourceLoader.getResource(path);
			if(rs != null && rs.exists())
			{
				
				EncodedResource er = new EncodedResource(rs, StringUtils.UTF8);
				InputStream is = er.getInputStream();
				return is;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
//	public static String loadString(String path)
//	{
//		try {
//			InputStream is = loadInputStream(path);
//			if(is != null)
//			{
//				return IOUtils.readStreamAsString(is, StringUtils.UTF8);
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return null;
//	}

}

package com.inso.framework.spring.config;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import com.fasterxml.jackson.databind.ObjectMapper;

//@Configuration
public class BakWebConfig extends WebMvcConfigurationSupport {
	
	@Override
	protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
		for (HttpMessageConverter<?> converter : converters) {
			// 解决controller返回普通文本中文乱码问题
			if (converter instanceof StringHttpMessageConverter) {
				((StringHttpMessageConverter) converter).setDefaultCharset(StandardCharsets.UTF_8);
			}
			// 解决controller返回json对象中文乱码问题
			if (converter instanceof MappingJackson2HttpMessageConverter) {
				((MappingJackson2HttpMessageConverter) converter).setDefaultCharset(StandardCharsets.UTF_8);
				((MappingJackson2HttpMessageConverter) converter).setObjectMapper(getObjectMapper());
			}
		}
	}
	
	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		converters.add(new StringHttpMessageConverter(StandardCharsets.UTF_8));
        super.configureMessageConverters(converters);
    }

	// 2.1：解决中文乱码后，返回json时可能会出现No converter found for return value of type: xxxx
	// 或这个：Could not find acceptable representation
	// 解决此问题如下
	public ObjectMapper getObjectMapper() {
		return new ObjectMapper();
	}

	// 2.2：解决No converter found for return value of type: xxxx
	public MappingJackson2HttpMessageConverter messageConverter() {
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		converter.setObjectMapper(getObjectMapper());
		return converter;
	}

}

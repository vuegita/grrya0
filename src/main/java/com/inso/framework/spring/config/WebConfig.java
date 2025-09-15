package com.inso.framework.spring.config;

import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.inso.framework.context.MyEnvironment;
import com.inso.framework.spring.SpringContextUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.validation.Validator;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inso.framework.conf.MyConfiguration;

@Configuration
public class WebConfig implements WebMvcConfigurer {
	
	private MyConfiguration conf = MyConfiguration.getInstance();

    /**
     * 添加类型转换器和格式化器
     * @author fxbin
     * @param registry
     */
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addFormatterForFieldType(LocalDate.class, new DateFormatter());
    }

    /**
     * 跨域支持
     * @author fxbin
     * @param registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
    	if(MyEnvironment.isDev())
		{
			registry.addMapping("/**")
					// 设置允许跨域请求的域名
					.allowedOrigins("*")
					// 是否允许cookie
					.allowCredentials(true)
					// 设置允许的请求方式
					.allowedMethods("GET", "POST", "DELETE", "PUT")
					// 设置允许的header属性
					.allowedHeaders("*")
					// 跨域允许时间
					.maxAge(3600);
		}
    	else
		{
			registry.addMapping("/**")

					.allowedOrigins("http://localhost:8080")
					.allowedOrigins("http://localhost:8081")
					.allowedOrigins("http://localhost:8082")
					.allowedOrigins("http://localhost:8083")
					.allowedOrigins("http://localhost:8084")

					.allowedOrigins("http://127.0.0.1:8080")
					.allowedOrigins("http://127.0.0.1:8081")
					.allowedOrigins("http://127.0.0.1:8082")
					.allowedOrigins("http://127.0.0.1:8083")
					.allowedOrigins("http://127.0.0.1:8084")
					.allowedOrigins("*")
					.allowCredentials(true)
					.allowedHeaders("*")
					.allowedMethods("GET", "POST", "DELETE", "PUT")
					.maxAge(3600 * 24);
		}

    }

    /**
     * 添加静态资源--过滤swagger-api (开源的在线API文档)
     * @author fxbin
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //过滤swagger
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");

        registry.addResourceHandler("/swagger-resources/**")
                .addResourceLocations("classpath:/META-INF/resources/swagger-resources/");

        registry.addResourceHandler("/swagger/**")
                .addResourceLocations("classpath:/META-INF/resources/swagger*");

        registry.addResourceHandler("/v2/api-docs/**")
                .addResourceLocations("classpath:/META-INF/resources/v2/api-docs/");
        
        // static, 单独部署静态文件不需要static这个path, 
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
        
        // upload 
        String upload_path = conf.getString("root.upload_path");
        registry.addResourceHandler("/uploads/**").addResourceLocations("file:"+upload_path);

        
    }
    
    @Bean
    public HttpMessageConverter<String> responseBodyConverter() {
        StringHttpMessageConverter converter = new StringHttpMessageConverter(
                Charset.forName("UTF-8"));
        return converter;
    }
    
//    @Override
//	protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
//		for (HttpMessageConverter<?> converter : converters) {
//			// 解决controller返回普通文本中文乱码问题
//			if (converter instanceof StringHttpMessageConverter) {
//				((StringHttpMessageConverter) converter).setDefaultCharset(StandardCharsets.UTF_8);
//			}
//			// 解决controller返回json对象中文乱码问题
//			if (converter instanceof MappingJackson2HttpMessageConverter) {
//				((MappingJackson2HttpMessageConverter) converter).setDefaultCharset(StandardCharsets.UTF_8);
//				((MappingJackson2HttpMessageConverter) converter).setObjectMapper(getObjectMapper());
//			}
//		}
//	}
//	
//	@Override
//	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
//		converters.add(new StringHttpMessageConverter(StandardCharsets.UTF_8));
//        super.configureMessageConverters(converters);
//    }

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

    /**
     * 配置消息转换器--这里我用的是alibaba 开源的 fastjson
     * @author fxbin
     * @param converters
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        //1.需要定义一个convert转换消息的对象;
        FastJsonHttpMessageConverter fastJsonHttpMessageConverter = new FastJsonHttpMessageConverter();
        //2.添加fastJson的配置信息，比如：是否要格式化返回的json数据;
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setSerializerFeatures(SerializerFeature.PrettyFormat,
                SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteNullStringAsEmpty,
                SerializerFeature.DisableCircularReferenceDetect,
                SerializerFeature.WriteNullListAsEmpty,
                SerializerFeature.WriteDateUseDateFormat);
        //3处理中文乱码问题
        List<MediaType> fastMediaTypes = new ArrayList<>();
        fastMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
        //4.在convert中添加配置信息.
        fastJsonHttpMessageConverter.setSupportedMediaTypes(fastMediaTypes);
        fastJsonHttpMessageConverter.setFastJsonConfig(fastJsonConfig);
        //5.将convert添加到converters当中.
        converters.add(fastJsonHttpMessageConverter);
        
        converters.add(responseBodyConverter());
    }
    
    @Bean
	public FastJsonConfig fastJsonConfig() {
		FastJsonConfig fastJsonConfig = new FastJsonConfig();
		SerializerFeature writeMapNullValue = SerializerFeature.WriteMapNullValue;
		SerializerFeature WriteNullStringAsEmpty = SerializerFeature.WriteNullStringAsEmpty;
		SerializerFeature WriteNullNumberAsZero = SerializerFeature.WriteNullNumberAsZero;
		SerializerFeature WriteNullListAsEmpty = SerializerFeature.WriteNullListAsEmpty;
		fastJsonConfig.setSerializerFeatures(writeMapNullValue, WriteNullStringAsEmpty, 
				WriteNullNumberAsZero, WriteNullListAsEmpty);
		return fastJsonConfig;
	}
 
//	@Bean 会二次json
//	public HttpMessageConverters fastJsonHttpMessageConverters(
//			@Qualifier("fastJsonConfig") FastJsonConfig fastJsonConfig) {
//		FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
//		fastConverter.setFastJsonConfig(fastJsonConfig);
//		HttpMessageConverter<?> converter = fastConverter;
//		return new HttpMessageConverters(converter);
//	}

    /**
     * 添加自定义的拦截器
     * @author fxbin
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

    }

//	@Bean
//	public CorsInterceptor getCorsInterceptor() {
//		return new CorsInterceptor();
//	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addViewControllers(ViewControllerRegistry arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void configureAsyncSupport(AsyncSupportConfigurer arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void configureContentNegotiation(ContentNegotiationConfigurer arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void configurePathMatch(PathMatchConfigurer arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void configureViewResolvers(ViewResolverRegistry arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void extendMessageConverters(List<HttpMessageConverter<?>> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public MessageCodesResolver getMessageCodesResolver() {
		// TODO Auto-generated method stub
		return null;
	}
	
	

	@Override
	public Validator getValidator() {
		// TODO Auto-generated method stub
		return null;
	}

}

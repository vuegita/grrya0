package com.inso.framework.spring.config;

import java.util.Properties;

import javax.annotation.PostConstruct;

import com.inso.modules.web.SystemRunningMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

import com.inso.framework.conf.MyConfiguration;
import com.inso.framework.context.MyEnvironment;

@Configuration
public class FreeMarkerConfig {

    @Autowired
    private freemarker.template.Configuration configuration;
    
    
	@PostConstruct
    public void setConfigure() throws Exception {
		configuration.setAPIBuiltinEnabled(false);
		configuration.setDefaultEncoding("UTF-8");
		configuration.setNumberFormat("0.##");

		//configuration.setSharedVariable("static_server", "//www.dev.pangugle.com/static");

		MyConfiguration conf = MyConfiguration.getInstance();
		String port = System.getProperty("server.port");
		String mainDomain = conf.getString("domain.main", "xxx.com");
		//xxx.com  //192.168.2.4:8180  208.87.206.144:8180 208.87.206.177  gobinan.com  grrya.com
		String projectName = conf.getString("project.name");
		
		// static
//      configuration.setSharedVariable("static_server", staticServer);
//      //图片读取路径配置
//		String img = "//"+mainDomain +"/";
//      configuration.setSharedVariable("img", img);
      
		// 2 new
		
		// prod or test 
		// => //static.xxx.com/alibaba888/css/common.css
		// => //static.test.xxx.com/alibaba888/css/common.css
		String staticServer = "//" + mainDomain + "/static";
		//staticServer = "//"+"192.168.1.212:8087"+"/static";
		// dev
		if(MyEnvironment.isDev())
		{
			// => //127.0.0.1:port/static
			staticServer = "//127.0.0.1:" + port + "/static";
		} else if(MyEnvironment.isBeta() || MyEnvironment.isProd())
		{
			// => //127.0.0.1:port/static
			staticServer = "/static";
		}

		staticServer = "/static";
		configuration.setSharedVariable("environment", MyEnvironment.getEnv());
		configuration.setSharedVariable("static_server", staticServer);
		configuration.setSharedVariable("projectName", projectName);
		configuration.setSharedVariable("ENV", MyEnvironment.getEnv());
		configuration.setSharedVariable("systemRunningMode", SystemRunningMode.getSystemConfig().getKey());

    }

	@Bean(name = "viewResolver")
	public ViewResolver getViewResolver() {
		FreeMarkerViewResolver viewResolver = new FreeMarkerViewResolver();

		viewResolver.setCache(true);
		viewResolver.setSuffix(".ftl");
		viewResolver.setOrder(1);
		viewResolver.setContentType("text/html;charset=UTF-8");//我是因为这一条没有配置，导致乱码
		return viewResolver;
	}

	@Bean(name = "freemarkerConfig")
	public FreeMarkerConfigurer getFreemarkerConfig() {
		FreeMarkerConfigurer config = new FreeMarkerConfigurer();

		String encode = "utf-8";

		Properties properties = new Properties();
		properties.put("default_encoding", encode);
		properties.put("output_encoding", encode);
		properties.put("url_escaping_charset", encode);
		properties.put("locale","zh_CN");
		properties.put("defaultEncoding", encode);
//		properties.put("date_format","yyyy-MM-dd");
//		properties.put("time_format","HH:mm:ss");
//		properties.put("datetime_format","yyyy-MM-dd HH:mm:ss");
//		properties.put("classic_compatible","true");
//		properties.put("template_exception_handler","rethrow");//#ignore,debug,html_debug,rethrow
		config.setFreemarkerSettings(properties);

// Folder containing FreeMarker templates.
// 1 - "/WEB-INF/views/"
// 2 - "classpath:/templates"
		config.setDefaultEncoding(encode);
		config.setTemplateLoaderPath("classpath:/templates");

		return config;
	}
	
}

package com.reset.password.demo.app;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebMvcConfiguration extends WebMvcConfigurerAdapter {

    private static final String[] CLASSPATH_RESOURCE_LOCATIONS = {
            "classpath:/META-INF/resources/", "classpath:/resources/",
            "classpath:/static/", "classpath:/public/"};

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**") .addResourceLocations(CLASSPATH_RESOURCE_LOCATIONS);
    }
  
	@Bean
	public FilterRegistrationBean filter() {
		FilterRegistrationBean bean = new FilterRegistrationBean();
		bean.setFilter(new ResetPassDemoFilter());
		bean.addUrlPatterns("/*");  // or use setUrlPatterns()

		return bean;
	}

}
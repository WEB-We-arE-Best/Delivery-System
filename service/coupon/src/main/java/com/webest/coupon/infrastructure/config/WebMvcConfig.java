package com.webest.coupon.infrastructure.config;

import com.webest.coupon.presentation.filter.MDCLoggingFilter;
import jakarta.servlet.Filter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebMvcConfig {

    @Bean
    public FilterRegistrationBean<Filter> logFilter() {
        FilterRegistrationBean<Filter> bean = new FilterRegistrationBean<>();

        bean.setFilter(new MDCLoggingFilter());
        bean.setOrder(1);
        bean.addUrlPatterns("/*");

        return bean;
    }

}

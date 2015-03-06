package com.github.lemniscate.hipstack.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.lemniscate.spring.jsonviews.converter.ViewAwareJsonMessageConverter;
import com.github.lemniscate.spring.jsonviews.hooks.JsonViewHandlerDecorator;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.servlet.FilterRegistration;
import java.util.List;

@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

    @Inject
    private ObjectMapper objectMapper;

    @PostConstruct
    public void init(){
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        super.extendMessageConverters(converters);
        ViewAwareJsonMessageConverter.configureMessageConverters(objectMapper, converters);
    }

    @Bean
    public JsonViewHandlerDecorator jsonViewHandlerDecorator(){
        return new JsonViewHandlerDecorator();
    }


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        ResourceHandlerRegistration bowerResources = registry
                .addResourceHandler("/bower_components/**")
                .addResourceLocations("/WEB-INF/bower_components/")
                .setCachePeriod(0);


        ResourceHandlerRegistration distResources = registry
                .addResourceHandler("/dist/**")
                .addResourceLocations("/WEB-INF/dist/")
                .setCachePeriod(0);

        ResourceHandlerRegistration srcResources = registry
                .addResourceHandler("/src/**")
                .addResourceLocations("/WEB-INF/src/")
                .setCachePeriod(0);


    }

    @Bean
    public FilterRegistrationBean txFilterDef(){
        FilterRegistrationBean result = new FilterRegistrationBean(txFilter());
        return result;
    }

    @Bean
    public OpenEntityManagerInViewFilter txFilter(){
        return new OpenEntityManagerInViewFilter();
    }
}

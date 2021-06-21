package com.swu.voicerecognition.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.util.ClassUtils;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @author zr
 * @create 2021-03-29-13:52
 */
@Configuration
public class PathConfig extends  WebMvcConfigurerAdapter{

    public static String resourcePath = "/var/www/voicerecognition";

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/audio/**").addResourceLocations("file:/var/www/voicerecognition");
    }
}

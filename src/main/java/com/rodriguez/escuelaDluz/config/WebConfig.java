package com.rodriguez.escuelaDluz.config;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	 @Override
	    public void addResourceHandlers(ResourceHandlerRegistry registry) {
	        Path uploadDir = Paths.get("imagenes");
	        String uploadPath = uploadDir.toAbsolutePath().toUri().toString();

	        registry.addResourceHandler("/imagenes/**").addResourceLocations(uploadPath);
	    }
	
}

package org.flexitech.projects.icpms.admin.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Value("${image.path}")
	private String imagePath;

	@Value("${image.context.path}")
	private String imageContextPath;

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		String location = imagePath.endsWith("/") ? imagePath : imagePath + "/";
		String pattern = (imageContextPath.endsWith("/") ? imageContextPath : imageContextPath + "/") + "**";
		registry.addResourceHandler(pattern).addResourceLocations("file:" + location);
	}
}
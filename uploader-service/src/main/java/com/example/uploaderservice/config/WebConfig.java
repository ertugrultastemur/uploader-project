package com.example.uploaderservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration class for Web-related settings.
 */
@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer{

    /**
     * Configures CORS (Cross-Origin Resource Sharing) mappings.
     *
     * @param registry the CorsRegistry to be configured
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/v1/**")
                .allowedOrigins("http://localhost:5500")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("multipart/form-data", "Authorization")  // Eğer başka header'lar kullanıyorsanız onları da ekleyin
                .allowedOriginPatterns("*")
                .allowCredentials(true)
                .exposedHeaders("Access-Control-Allow-Origin");
    }

}

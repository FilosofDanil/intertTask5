package com.example.demo.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Configuration class for creating a custom Jackson ObjectMapper bean with JavaTimeModule registered.
 */
@Configuration
public class ObjectMapperConfiguration {

    /**
     * Creates and configures a custom Jackson ObjectMapper bean with JavaTimeModule registered.
     * @return Customized ObjectMapper bean
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper =new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }
}

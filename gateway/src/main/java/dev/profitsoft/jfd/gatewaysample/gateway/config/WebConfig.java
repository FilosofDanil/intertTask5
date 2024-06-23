package dev.profitsoft.jfd.gatewaysample.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.server.session.WebSessionManager;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import reactor.core.publisher.Mono;

@Configuration
public class WebConfig {

  @Bean
  public WebSessionManager webSessionManager() {
    // Emulate SessionCreationPolicy.STATELESS
    return exchange -> Mono.empty();
  }
}

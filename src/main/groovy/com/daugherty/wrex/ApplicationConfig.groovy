package com.daugherty.wrex

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.web.client.RestTemplate

@Configuration
class ApplicationConfig {
  @Bean
  RestTemplate restTemplate() {
    new RestTemplate(new HttpComponentsClientHttpRequestFactory())
  }
}

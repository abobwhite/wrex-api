package com.daugherty.wrex

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
@Configuration
class ApplicationConfig {
  @Value('${wrex.statusPromptSeconds}')
  Long statusPromptSeconds

  @Bean
  RestTemplate restTemplate() {
    new RestTemplate(new HttpComponentsClientHttpRequestFactory())
  }
}

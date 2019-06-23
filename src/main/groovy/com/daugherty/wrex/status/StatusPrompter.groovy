package com.daugherty.wrex.status

import com.daugherty.wrex.ExternalConfig
import groovy.util.logging.Slf4j
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Slf4j
@Service
class StatusPrompter {
  private final ExternalConfig externalConfig
  private final RestTemplate restTemplate

  StatusPrompter(final ExternalConfig externalConfig, final RestTemplate restTemplate) {
    this.externalConfig = externalConfig
    this.restTemplate = restTemplate
  }

  @Async
  void promptUserForStatus(String userId) {
    def promptResponse = restTemplate.postForObject("${externalConfig.slackPromptUrl}/${userId}", null, String)
    if (!promptResponse) {
      log.warn("Failed to prompt ${userId} for status")
    }
  }
}

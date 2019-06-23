package com.daugherty.wrex.tag

import com.daugherty.wrex.ExternalConfig
import com.daugherty.wrex.status.Status
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Slf4j
@Service
class StatusTagProcessor {
  private final ExternalConfig externalConfig
  private final RestTemplate restTemplate

  StatusTagProcessor(final ExternalConfig externalConfig, final RestTemplate restTemplate) {
    this.externalConfig = externalConfig
    this.restTemplate = restTemplate
  }

  List<Tag> createTagsForStatus(Status status) {
    def nlpResponse = restTemplate.postForObject(externalConfig.nlpUrl, status.message, NLPResponse)
    log.info(nlpResponse.toString())
  }
}

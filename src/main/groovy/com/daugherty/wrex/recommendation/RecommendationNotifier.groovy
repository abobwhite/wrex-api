package com.daugherty.wrex.recommendation

import com.daugherty.wrex.ExternalConfig
import groovy.util.logging.Slf4j
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Slf4j
@Service
class RecommendationNotifier {
  private final ExternalConfig externalConfig
  private final RestTemplate restTemplate

  RecommendationNotifier(final ExternalConfig externalConfig, final RestTemplate restTemplate) {
    this.externalConfig = externalConfig
    this.restTemplate = restTemplate
  }

  @Async
  void notifyUserOfRecommendations(String userId, List<Recommendation> recommendations) {
    def promptResponse = restTemplate.postForObject("${externalConfig.slackRecommendationNotifyUrl}/${userId}", recommendations*.message, String)
    if (!promptResponse) {
      log.warn("Failed to notify ${userId} of recommendations")
    }
  }
}

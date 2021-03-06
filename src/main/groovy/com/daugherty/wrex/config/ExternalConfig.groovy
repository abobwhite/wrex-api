package com.daugherty.wrex.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component

@Configuration
@Component
class ExternalConfig {
  @Value('${wrex.nlp.url}')
  String nlpUrl

  @Value('${wrex.userCorrelations.url}')
  String userCorrelationsUrl

  @Value('${wrex.slack.auth.url}')
  String slackAccessTokenUrl

  @Value('${wrex.slack.auth.clientId}')
  String slackClientId

  @Value('${wrex.slack.auth.clientSecret}')
  String slackClientSecret

  @Value('${wrex.slack.prompt.url}')
  String slackPromptUrl

  @Value('${wrex.slack.recommendationNotify.url}')
  String slackRecommendationNotifyUrl
}

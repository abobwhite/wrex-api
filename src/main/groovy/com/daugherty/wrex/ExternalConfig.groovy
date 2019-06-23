package com.daugherty.wrex

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
}

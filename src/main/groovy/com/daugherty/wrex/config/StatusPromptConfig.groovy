package com.daugherty.wrex.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component

@Configuration
@ConfigurationProperties(prefix = 'wrex.status.prompt')
@Component
class StatusPromptConfig {
  Long defaultTimeSinceStatusSeconds
  Long defaultTimeBetweenPromptsSeconds
  Long statusPromptCheckSeconds
}

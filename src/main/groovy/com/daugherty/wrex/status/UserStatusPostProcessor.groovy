package com.daugherty.wrex.status

import com.daugherty.wrex.recommendation.RecommendationGenerator
import com.daugherty.wrex.tag.StatusTagProcessor
import com.daugherty.wrex.user.User
import com.daugherty.wrex.user.correlation.UserCorrelationProcessor
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class UserStatusPostProcessor {
  private final StatusTagProcessor statusTagProcessor
  private final UserCorrelationProcessor userCorrelationProcessor
  private final RecommendationGenerator recommendationGenerator

  UserStatusPostProcessor(final StatusTagProcessor statusTagProcessor, final UserCorrelationProcessor userCorrelationProcessor, final RecommendationGenerator recommendationGenerator) {
    this.statusTagProcessor = statusTagProcessor
    this.userCorrelationProcessor = userCorrelationProcessor
    this.recommendationGenerator = recommendationGenerator
  }

  @Async
  void process(Status status, User user) {
    statusTagProcessor.createTagsForStatus(status, user)
    recommendationGenerator.generateRecommendationsForMentorship()
    userCorrelationProcessor.getUserForCorrelations(user) // TODO: Maybe on scheduled basis as data grows
  }
}

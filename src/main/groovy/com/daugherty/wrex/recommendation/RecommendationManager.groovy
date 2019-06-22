package com.daugherty.wrex.recommendation

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Service

@Service
@Slf4j
@CompileStatic
class RecommendationManager {
  private final RecommendationRepository recommendationRepository

  RecommendationManager(final RecommendationRepository recommendationRepository) {
    this.recommendationRepository = recommendationRepository
  }

  List<Recommendation> getRecommendationsForUser(String userId) {
    def all = recommendationRepository.findAll()
    log.info(all.size().toString())
    recommendationRepository.findByUserId(userId)
  }
}

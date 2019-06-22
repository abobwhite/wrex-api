package com.daugherty.wrex.recommendation

import com.daugherty.wrex.exception.ERROR_CODE
import com.daugherty.wrex.exception.WrexException
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

  Recommendation modifyRecommendation(String recommendationId, Recommendation updatedRecommendation) {
    def recommendation = recommendationRepository.findById(recommendationId).orElse(null)

    if(!recommendation) {
      throw new WrexException(ERROR_CODE.NOT_FOUND)
    }

    if(updatedRecommendation.dismissed != null) {
      recommendation.dismissed = updatedRecommendation.dismissed
    }

    if(updatedRecommendation.feedback) {
      recommendation.feedback = updatedRecommendation.feedback
    }

    recommendationRepository.save(recommendation)
  }
}

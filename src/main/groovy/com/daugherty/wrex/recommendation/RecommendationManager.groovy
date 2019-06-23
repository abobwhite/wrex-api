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

  List<Recommendation> getRecommendationsForUser(String userId, Boolean showDismissed = true) {
    if (showDismissed) {
      return recommendationRepository.findByUserId(userId)
    }

    recommendationRepository.findByUserIdAndDismissed(userId, false)
  }

  Recommendation createRecommendation(Recommendation recommendation) {
    recommendationRepository.insert(recommendation)
  }

  Recommendation modifyRecommendation(String recommendationId, Recommendation updatedRecommendation) {
    def recommendation = recommendationRepository.findById(recommendationId).orElse(null)

    if (!recommendation) {
      throw new WrexException(ERROR_CODE.NOT_FOUND)
    }

    if (updatedRecommendation.dismissed != null) {
      recommendation.dismissed = updatedRecommendation.dismissed
    }

    if (updatedRecommendation.feedback) {
      recommendation.feedback = updatedRecommendation.feedback
    }

    recommendationRepository.save(recommendation)
  }
}

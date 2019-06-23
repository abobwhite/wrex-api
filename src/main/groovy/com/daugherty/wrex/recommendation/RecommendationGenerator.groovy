package com.daugherty.wrex.recommendation

import com.daugherty.wrex.tag.TagCategoryManager
import com.daugherty.wrex.tag.TagManager
import com.daugherty.wrex.user.USER_PREFERENCE
import com.daugherty.wrex.user.User
import com.daugherty.wrex.user.UserManager
import com.daugherty.wrex.user.correlation.UserCorrelationManager
import groovy.util.logging.Slf4j
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Slf4j
@Service
class RecommendationGenerator {
  private final UserCorrelationManager userCorrelationManager
  private final RecommendationManager recommendationManager
  private final UserManager userManager
  private final TagManager tagManager
  private final TagCategoryManager tagCategoryManager

  private static final Double MINIMUM_RECOMMENDATION_SCORE = 0.5
  private static final Integer NUMBER_TAG_SCORES_TO_CONSIDER = 3

  RecommendationGenerator(final UserCorrelationManager userCorrelationManager, final RecommendationManager recommendationManager, final UserManager userManager,
                          final TagManager tagManager, final TagCategoryManager tagCategoryManager) {
    this.userCorrelationManager = userCorrelationManager
    this.recommendationManager = recommendationManager
    this.userManager = userManager
    this.tagManager = tagManager
    this.tagCategoryManager = tagCategoryManager
  }

  @Async
  void generateRecommendations() {
    def userCorrelations = userCorrelationManager.getUserCorrelations().findAll { it.score > MINIMUM_RECOMMENDATION_SCORE }
    userCorrelations.each { userCorrelation ->
      def user1 = userManager.getUserById(userCorrelation.user1Id)
      def user2 = userManager.getUserById(userCorrelation.user2Id)

      def recommendations = generateHangoutRecommendations(user1, user2)
      recommendations = removeDuplicateRecommendations(user1.id, recommendations)
      recommendations = removeDuplicateRecommendations(user2.id, recommendations)

      // TODO: Recommend non hangout stuffs
      //def tagScores = userCorrelation.tagScores.sort { tagScore1, tagScore2 -> tagScore2.value <=> tagScore1.value }

      log.info("Generating ${recommendations.size()} recommendations between users ${user1.id} and ${user2.id}")
      recommendations.each { recommendation ->
        recommendationManager.createRecommendation(recommendation)
      }
    }
  }

  private List<Recommendation> generateHangoutRecommendations(User user1, User user2) {
    def recommendations = []
    if (user1.preferences?.randomCoffee && user2.preferences?.randomCoffee) {
      recommendations << generateRandomCoffee(user1.id, "${user2.firstName} ${user2.lastName}")
      recommendations << generateRandomCoffee(user2.id, "${user1.firstName} ${user1.lastName}")
    } else if (user1.preferences?.happyHour && user2.preferences?.happyHour) {
      recommendations << generateHappyHour(user1.id, "${user2.firstName} ${user2.lastName}")
      recommendations << generateHappyHour(user2.id, "${user1.firstName} ${user1.lastName}")
    }

    recommendations
  }

  private List<Recommendation> removeDuplicateRecommendations(String userId, List<Recommendation> proposedRecommendations) {
    def existingRecommendations = recommendationManager.getRecommendationsForUser(userId, false)

    proposedRecommendations.findAll { proposedRecommendation ->
      !existingRecommendations.any { existingRecommendation ->
        areRecommendationsDuplicates(proposedRecommendation, existingRecommendation)
      }
    }
  }

  private Boolean areRecommendationsDuplicates(Recommendation proposedRecommendation, Recommendation existingRecommendation) {
    def diff = existingRecommendation.geneses - proposedRecommendation.geneses

    diff.empty
  }

  private Recommendation generateRandomCoffee(String userId, String otherUserFullName) {
    new Recommendation(
        userId: userId,
        type: RECOMMENDATION_TYPE.HANGOUT,
        message: RecommendationMessages.getRandomCoffee(otherUserFullName),
        geneses: [new RecommendationGenesis(type: RECOMMENDATION_GENESIS_TYPE.PREFERENCE, item: USER_PREFERENCE.RANDOM_COFFEE.toString())]
    )
  }

  private Recommendation generateHappyHour(String userId, String otherUserFullName) {
    new Recommendation(
        userId: userId,
        type: RECOMMENDATION_TYPE.HANGOUT,
        message: RecommendationMessages.getHappyHour(otherUserFullName),
        geneses: [new RecommendationGenesis(type: RECOMMENDATION_GENESIS_TYPE.PREFERENCE, item: USER_PREFERENCE.HAPPY_HOUR.toString())]
    )
  }
}

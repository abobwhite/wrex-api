package com.daugherty.wrex.recommendation

import com.daugherty.wrex.tag.Tag
import com.daugherty.wrex.tag.TagCategory
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
  private final RecommendationNotifier recommendationNotifier

  private static final Double MINIMUM_RECOMMENDATION_SCORE = 0.25
  private static final Float PERCENT_TAG_SCORES_TO_CONSIDER = 0.25

  RecommendationGenerator(final UserCorrelationManager userCorrelationManager, final RecommendationManager recommendationManager, final UserManager userManager,
                          final TagManager tagManager, final TagCategoryManager tagCategoryManager, final RecommendationNotifier recommendationNotifier) {
    this.userCorrelationManager = userCorrelationManager
    this.recommendationManager = recommendationManager
    this.userManager = userManager
    this.tagManager = tagManager
    this.tagCategoryManager = tagCategoryManager
    this.recommendationNotifier = recommendationNotifier
  }

  @Async
  void generateRecommendations() {
    generateRecommendationsFromCorrelations()
  }

  @Async
  void generateRecommendationsForMentorship() {
    def allUsers = userManager.getUsers()
    def requestingUsers = allUsers.findAll { it.requestTagIds }
    def nonRequestingUsers = allUsers - requestingUsers

    requestingUsers.each { requestingUser ->
      requestingUser.requestTagIds.each { requestedTagId ->
        def tag = tagManager.findById(requestedTagId)
        if (tag.categoryConfidences) {
          def mentorId
          nonRequestingUsers.each { nonRequestingUser ->
            def userIdToTagCount = [:]
            nonRequestingUser.userTags.each { userTag ->
              if (userTag.tagId == requestedTagId) {
                userIdToTagCount[nonRequestingUser.id] = userTag.count
              }
            }

            def sortedUserToCount = userIdToTagCount.sort { userToCount1, userToCount2 -> userToCount2.value <=> userToCount1.value }
            def sortedUserIds = sortedUserToCount.collect { userId, count -> userId }
            mentorId = sortedUserIds ? sortedUserIds[0] : null
          }

          if (mentorId) {
            def mentor = nonRequestingUsers.find { it.id == mentorId }
            def recommendation = generateMentoringOpportunity(mentor.id, requestingUser.fullName, tag)
            def recommendations = removeDuplicateRecommendations(mentor.id, [recommendation])
            if (recommendations) {
              log.info("Creating ${recommendations.size()} recommendations for mentorship")
              saveRecommendations(recommendations)
              recommendationNotifier.notifyUserOfRecommendations(mentor.id, recommendations)
            }
          }
        }
      }
    }
  }

  private void generateRecommendationsFromCorrelations() {
    def tagCategories = tagCategoryManager.getTagCategories()
    def userCorrelations = userCorrelationManager.getUserCorrelations().findAll { it.score > MINIMUM_RECOMMENDATION_SCORE }
    userCorrelations.each { userCorrelation ->
      def user1 = userManager.getUserById(userCorrelation.user1Id)
      def user2 = userManager.getUserById(userCorrelation.user2Id)

      def tagScores = userCorrelation.tagScores.sort { tagScore1, tagScore2 -> tagScore2.value <=> tagScore1.value }
      def numberScoredTags = tagScores.keySet().size()
      def numberToConsider = Math.round(numberScoredTags * PERCENT_TAG_SCORES_TO_CONSIDER) as int
      def collatedTagIds = tagScores.collect { tagId, score -> tagId }.collate(numberToConsider)
      def topTagIds = collatedTagIds[0]

      def recommendations = generateSharedTopTagRecommendations(topTagIds, user1, user2, tagCategories)

      recommendations.addAll(generatePreferenceRecommendations(user1, user2))

      recommendations = removeDuplicateRecommendations(user1.id, recommendations)
      recommendations = removeDuplicateRecommendations(user2.id, recommendations)

      log.info("Generating ${recommendations.size()} recommendations between users ${user1.id} and ${user2.id}")
      saveRecommendations(recommendations)
      recommendationNotifier.notifyUserOfRecommendations(user1.id, recommendations.findAll { it.userId == user1.id } )
      recommendationNotifier.notifyUserOfRecommendations(user2.id, recommendations.findAll { it.userId == user2.id })
    }
  }

  private void saveRecommendations(List<Recommendation> recommendations) {
    recommendations.each { recommendation ->
      recommendationManager.createRecommendation(recommendation)
    }
  }

  private List<Recommendation> generateSharedTopTagRecommendations(List<String> topTagIds, User user1, User user2, List<TagCategory> tagCategories) {
    def recommendations = []
    topTagIds.each { tagId ->
      def tag = tagManager.findById(tagId)
      if (tag.categoryConfidences) {
        def mostConfidentCategory = tag.categoryConfidences.sort(false) { -it.confidence }[0].categoryId
        def tagCategory = tagCategories.find { it.id == mostConfidentCategory }
        if (tagCategory.name == 'Event') {
          recommendations << generateOverlappingTagEvent(user1.id, user2.fullName, tag)
          recommendations << generateOverlappingTagEvent(user2.id, user1.fullName, tag)
        } else {
          recommendations << generateOverlappingTagHangout(user1.id, user2.fullName, tag)
          recommendations << generateOverlappingTagHangout(user2.id, user1.fullName, tag)
        }
      }
    }

    recommendations
  }

  private List<Recommendation> generatePreferenceRecommendations(User user1, User user2) {
    def recommendations = []
    if (user1.preferences?.randomCoffee && user2.preferences?.randomCoffee) {
      recommendations << generateRandomCoffee(user1.id, user2.fullName)
      recommendations << generateRandomCoffee(user2.id, user1.fullName)
    } else if (user1.preferences?.happyHour && user2.preferences?.happyHour) {
      recommendations << generateHappyHour(user1.id, user2.fullName)
      recommendations << generateHappyHour(user2.id, user1.fullName)
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

  private Recommendation generateOverlappingTagHangout(String userId, String otherUserFullName, Tag tag) {
    new Recommendation(
      userId: userId,
      type: RECOMMENDATION_TYPE.HANGOUT,
      message: RecommendationMessages.getWeTalkTheSameTalk(otherUserFullName, tag.name),
      geneses: [new RecommendationGenesis(type: RECOMMENDATION_GENESIS_TYPE.TAG, item: tag.id)]
    )
  }

  private Recommendation generateOverlappingTagEvent(String userId, String otherUserFullName, Tag tag) {
    new Recommendation(
      userId: userId,
      type: RECOMMENDATION_TYPE.EVENT,
      message: RecommendationMessages.getWeLikeTheSameEvent(otherUserFullName, tag.name),
      geneses: [new RecommendationGenesis(type: RECOMMENDATION_GENESIS_TYPE.TAG, item: tag.id)]
    )
  }

  private Recommendation generateMentoringOpportunity(String userId, String otherUserFullName, Tag tag) {
    new Recommendation(
      userId: userId,
      type: RECOMMENDATION_TYPE.MENTORSHIP,
      message: RecommendationMessages.getMentorship(otherUserFullName, tag.name),
      geneses: [new RecommendationGenesis(type: RECOMMENDATION_GENESIS_TYPE.REQUEST, item: tag.id)]
    )
  }
}

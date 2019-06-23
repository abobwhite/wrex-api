package com.daugherty.wrex.user.correlation

import com.daugherty.wrex.ExternalConfig
import com.daugherty.wrex.recommendation.RecommendationGenerator
import com.daugherty.wrex.tag.TagManager
import com.daugherty.wrex.user.User
import com.daugherty.wrex.user.UserManager
import com.daugherty.wrex.user.UserTag
import com.fasterxml.jackson.databind.ObjectMapper
import groovy.util.logging.Slf4j
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Slf4j
@Service
class UserCorrelationProcessor {
  private final ExternalConfig externalConfig
  private final RestTemplate restTemplate
  private final UserManager userManager
  private final TagManager tagManager
  private final RecommendationGenerator recommendationGenerator
  private final UserCorrelationManager userCorrelationManager

  private final static MAX_NUM_TAGS_TO_CORRELATE = 10

  UserCorrelationProcessor(final ExternalConfig externalConfig, final RestTemplate restTemplate, final UserManager userManager, final TagManager tagManager,
                           final RecommendationGenerator recommendationGenerator, final UserCorrelationManager userCorrelationManager) {
    this.externalConfig = externalConfig
    this.restTemplate = restTemplate
    this.userManager = userManager
    this.tagManager = tagManager
    this.recommendationGenerator = recommendationGenerator
    this.userCorrelationManager = userCorrelationManager
  }

  @Async
  void getUserForCorrelations(User user) {
    def request = createUserCorrelationRequest(user)
    log.info("Input to User Correlation Service: ${new ObjectMapper().writeValueAsString(request)}")
    def userCorrelationResponse = restTemplate.postForObject(externalConfig.userCorrelationsUrl, request, UserCorrelationResponse)
    createUserCorrelationsFromResponse(user, userCorrelationResponse)
    recommendationGenerator.generateRecommendations()
    log.info("Finished calculating correlations for user ${user.id}")
  }

  private UserCorrelationRequest createUserCorrelationRequest(User user) {
    def topUserTags = user.userTags.sort(false) { -it.count }.collate(MAX_NUM_TAGS_TO_CORRELATE)[0]
    def topTagIds = topUserTags.collect { it.tagId }

    def users = userManager.getUsers().findAll { otherUser -> otherUser.id != user.id }

    def userIdToSortedUserTagCountsMap = users.collectEntries { someUser ->
      def userTagCounts = getUserTagCounts(topTagIds, someUser.userTags)
      [(someUser.id): userTagCounts]
    }

    def userCorrelationTrain = new UserCorrelationTrain(
        points: topUserTags.collect { it.tagId },
        samples: userIdToSortedUserTagCountsMap.collect { userId, counts ->
          new UserCorrelationTrainSample(id: userId, values: counts)
        }
    )

    def userCorrelationTest = new UserCorrelationTest(
        data: getUserTagCounts(topTagIds, user.userTags)
    )

    new UserCorrelationRequest(
        train: userCorrelationTrain,
        test: userCorrelationTest
    )
  }

  private List<Double> getUserTagCounts(List<String> tagIds, List<UserTag> userTags) {
    tagIds.collect { tagId ->
      def count = userTags.find { it.tagId == tagId }?.count ?: 0L
      getCountLogBase2(count)
    }
  }

  private Double getCountLogBase2(Long count) {
    if (!count) {
      return 0d
    }

    (Math.log(count.toDouble()) / Math.log(2d)) + 1
  }

  private void createUserCorrelationsFromResponse(User user, UserCorrelationResponse response) {
    Map<String, UserCorrelationTags> userToCorrelationTags = response.tags.collectEntries { tagCorrelation ->
      def userId = tagCorrelation.keySet().getAt(0)
      [(userId): tagCorrelation[userId]]
    }

    response.correlation.each { userId, correlation ->
      UserCorrelationTags tagCorrelationsForUser = userToCorrelationTags[userId]
      Map<String, Double> tagScores = [:]
      if (tagCorrelationsForUser) {
        tagScores = tagCorrelationsForUser.collectEntries { String tagId, String score ->
          [(tagId): Double.parseDouble(score)]
        }
      }

      def userCorrelation = userCorrelationManager.findUserCorrelationForUsers(user.id, userId)
      if (userCorrelation) {
        userCorrelation.score = correlation
        userCorrelation.tagScores = tagScores
        userCorrelationManager.updateUserCorrelation(userCorrelation)
      } else {
        userCorrelationManager.createUserCorrelation(new UserCorrelation(
            user1Id: user.id,
            user2Id: userId,
            score: correlation,
            tagScores: tagScores
        ))
      }
    }
  }
}

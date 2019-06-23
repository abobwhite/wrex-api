package com.daugherty.wrex.user.correlation

import com.daugherty.wrex.ExternalConfig
import com.daugherty.wrex.tag.Tag
import com.daugherty.wrex.tag.TagManager
import com.daugherty.wrex.user.User
import com.daugherty.wrex.user.UserManager
import com.daugherty.wrex.user.UserTag
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Slf4j
@Service
class UserCorrelationProcessor {
  private final ExternalConfig externalConfig
  private final RestTemplate restTemplate
  private final UserManager userManager
  private final TagManager tagManager

  UserCorrelationProcessor(final ExternalConfig externalConfig, final RestTemplate restTemplate, final UserManager userManager, final TagManager tagManager) {
    this.externalConfig = externalConfig
    this.restTemplate = restTemplate
    this.userManager = userManager
    this.tagManager = tagManager
  }

  void getUserCorrelations(User user) {
    def request = createUserCorrelationRequest(user)
    //log.info(new ObjectMapper().writeValueAsString(request))
    def nlpResponse = restTemplate.postForObject(externalConfig.userCorrelationsUrl, request, UserCorrelationResponse)
    log.info(nlpResponse.toString())
  }

  private UserCorrelationRequest createUserCorrelationRequest(User user) {
    def users = userManager.getUsers().findAll { otherUser -> otherUser.id != user.id }
    def sortedTags = tagManager.getTags().sort { it.id }
    def userIdToSortedUserTagCountsMap = users.collectEntries { someUser ->
      def userTagCounts = getUserTagCounts(sortedTags, someUser.userTags)
      [(someUser.id): userTagCounts]
    }

    def userCorrelationTrain = new UserCorrelationTrain(
        points: sortedTags.collect { it.id },
        samples: userIdToSortedUserTagCountsMap.collect { userId, counts ->
          new UserCorrelationTrainSample(id: userId, values: counts)
        }
    )

    def userCorrelationTest = new UserCorrelationTest(
        data: getUserTagCounts(sortedTags, user.userTags)
    )

    new UserCorrelationRequest(
        train: userCorrelationTrain,
        test: userCorrelationTest
    )
  }

  private List<Double> getUserTagCounts(List<Tag> tags, List<UserTag> userTags) {
    tags.collect { tag ->
      def count = userTags.find { it.tagId == tag.id }?.count ?: 0L
      getCountLogBase2(count)
    }
  }

  private Double getCountLogBase2(Long count) {
    if (!count) {
      return 0d
    }

    (Math.log(count.toDouble()) / Math.log(2d)) + 1
  }
}

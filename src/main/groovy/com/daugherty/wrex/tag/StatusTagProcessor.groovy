package com.daugherty.wrex.tag

import com.daugherty.wrex.config.ExternalConfig
import com.daugherty.wrex.status.Status
import com.daugherty.wrex.user.User
import com.daugherty.wrex.user.UserManager
import com.daugherty.wrex.user.UserTag
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Slf4j
@Service
class StatusTagProcessor {
  private final ExternalConfig externalConfig
  private final RestTemplate restTemplate
  private final UserManager userManager
  private final TagManager tagManager

  private static final List<String> REQUEST_PHRASES = [
      'need help',
      'having trouble',
      'need assistance',
      'struggling with',
  ]

  StatusTagProcessor(final ExternalConfig externalConfig, final RestTemplate restTemplate, final UserManager userManager, final TagManager tagManager) {
    this.externalConfig = externalConfig
    this.restTemplate = restTemplate
    this.userManager = userManager
    this.tagManager = tagManager
  }

  void createTagsForStatus(Status status, User user) {
    def requestPhrase = getRequestPhrase(status.message) ?: ''
    if(requestPhrase) {
      log.info("Request found in status for user ${user.id}")
    }
    def message = status.message.replace(requestPhrase, '')

    def nlpResponse = restTemplate.postForObject(externalConfig.nlpUrl, message, NLPResponse)

    def userTags = user.userTags ?: []
    def tagNameToIdMap = tagManager.getTags().collectEntries { tag ->
      [(tag.name.toLowerCase()): tag.id]
    }

    def requestTagIds = []

    nlpResponse.words.each { word ->
      def tagIdForWord = tagNameToIdMap[word.toLowerCase()]
      if (!tagIdForWord) {
        def newTag = tagManager.createTag(new Tag(name: word))
        userTags << new UserTag(tagId: newTag.id, count: 1)
        if (requestPhrase) {
          requestTagIds << newTag.id
        }
      } else {
        def userTagIndex = userTags.findIndexOf { userTag ->
          userTag.tagId == tagIdForWord
        }

        if (userTagIndex > -1) {
          def userTag = userTags[userTagIndex]
          userTag.count++
          userTags.set(userTagIndex, userTag)
        } else {
          userTags << new UserTag(tagId: tagIdForWord, count: 1)
        }

        if (requestPhrase) {
          requestTagIds << tagIdForWord
        }
      }

      if (user.requestTagIds) {
        user.requestTagIds.addAll(requestTagIds)
        user.requestTagIds = user.requestTagIds.unique()
      } else {
        user.requestTagIds = requestTagIds
      }
      user.userTags = userTags

      userManager.updateUser(user)
    }
    log.info("User ${user.id} has ${user.userTags.size()} tags")
    log.info("Finished calculating status tags for user ${user.id}")
  }

  String getRequestPhrase(String message) {
    REQUEST_PHRASES.find { phrase -> message.contains(phrase) }
  }
}

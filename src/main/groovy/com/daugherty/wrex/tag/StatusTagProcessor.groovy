package com.daugherty.wrex.tag

import com.daugherty.wrex.ExternalConfig
import com.daugherty.wrex.status.Status
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

  StatusTagProcessor(final ExternalConfig externalConfig, final RestTemplate restTemplate, final UserManager userManager, final TagManager tagManager) {
    this.externalConfig = externalConfig
    this.restTemplate = restTemplate
    this.userManager = userManager
    this.tagManager = tagManager
  }

  void createTagsForStatus(Status status) {
    def nlpResponse = restTemplate.postForObject(externalConfig.nlpUrl, status.message, NLPResponse)
    def user = userManager.getUserById(status.userId)

    def userTags = user.userTags ?: []
    def tagNameToIdMap = tagManager.getTags().collectEntries { tag ->
      [(tag.name): tag.id]
    }

    nlpResponse.words.each { word ->
      def tagIdForWord = tagNameToIdMap[word]
      if (!tagIdForWord) {
        def newTag = tagManager.createTag(new Tag(name: word))
        userTags << new UserTag(tagId: newTag.id, count: 1)
      } else {
        def userTagIndex = userTags.findIndexOf { userTag ->
          userTag.tagId == tagIdForWord
        }
        def userTag = userTags[userTagIndex]
        userTag.count++
        userTags.set(userTagIndex, userTag)
      }

      user.userTags = userTags

      userManager.saveUser(user)
    }
  }
}

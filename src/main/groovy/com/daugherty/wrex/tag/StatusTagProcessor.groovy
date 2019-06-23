package com.daugherty.wrex.tag

import com.daugherty.wrex.ExternalConfig
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

  StatusTagProcessor(final ExternalConfig externalConfig, final RestTemplate restTemplate, final UserManager userManager, final TagManager tagManager) {
    this.externalConfig = externalConfig
    this.restTemplate = restTemplate
    this.userManager = userManager
    this.tagManager = tagManager
  }

  void createTagsForStatus(Status status, User user) {
    def nlpResponse = restTemplate.postForObject(externalConfig.nlpUrl, status.message, NLPResponse)

    def userTags = user.userTags ?: []
    def tagNameToIdMap = tagManager.getTags().collectEntries { tag ->
      [(tag.name.toLowerCase()): tag.id]
    }

    nlpResponse.words.each { word ->
      def tagIdForWord = tagNameToIdMap[word.toLowerCase()]
      if (!tagIdForWord) {
        def newTag = tagManager.createTag(new Tag(name: word))
        userTags << new UserTag(tagId: newTag.id, count: 1)
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
      }

      user.userTags = userTags

      userManager.updateUser(user)
    }
    log.info("User ${user.id} has ${user.userTags.size()} tags")
    log.info("Finished calculating status tags for user ${user.id}")
  }
}

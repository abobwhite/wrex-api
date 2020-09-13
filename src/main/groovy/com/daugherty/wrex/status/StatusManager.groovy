package com.daugherty.wrex.status

import com.daugherty.wrex.config.StatusPromptConfig
import com.daugherty.wrex.exception.ERROR_CODE
import com.daugherty.wrex.exception.WrexException
import com.daugherty.wrex.user.User
import com.daugherty.wrex.user.UserManager
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

import java.time.Instant

@Slf4j
@CompileStatic
@Service
class StatusManager {
  private final StatusRepository statusRepository
  private final UserManager userManager
  private final UserStatusPostProcessor userStatusPostProcessor
  private final StatusPrompter statusPrompter
  private final StatusPromptConfig statusPromptConfig

  StatusManager(final StatusRepository statusRepository, final UserManager userManager, final UserStatusPostProcessor userStatusPostProcessor,
                final StatusPrompter statusPrompter, final StatusPromptConfig statusPromptConfig) {
    this.statusRepository = statusRepository
    this.userManager = userManager
    this.userStatusPostProcessor = userStatusPostProcessor
    this.statusPrompter = statusPrompter
    this.statusPromptConfig = statusPromptConfig
  }

  Status addStatus(String userId, Status status) {
    if (!status.message) {
      throw new WrexException(ERROR_CODE.INVALID)
    }

    def user = userManager.getUserById(userId) // throws if user not found
    status.userId = user.id
    status.date = Instant.now()

    def createdStatus = statusRepository.insert(status)

    userStatusPostProcessor.process(createdStatus, user)

    createdStatus
  }

  List<Status> getStatusesForUser(String userId) {
    def user = userManager.getUserById(userId) // throws if no user
    statusRepository.findByUserIdOrderByDateDesc(user.id)
  }

  // NOTE: fixed DELAY ensures ordered updates - we want to wait until a synchronous request is finished before checking again
  @Scheduled(fixedDelayString = '${wrex.status.prompt.statusPromptCheckSeconds}000')
  protected void promptUsersForStatus() {
    log.debug('Checking users for status reminders')
    // TODO: Eventually convert these to user preferences
    def lastStatusThreshold = Instant.now().minusMillis(statusPromptConfig.defaultTimeSinceStatusSeconds * 1000)
    def lastPromptThreshold = Instant.now().minusMillis(statusPromptConfig.defaultTimeBetweenPromptsSeconds * 1000)
    userManager.getUsers().each { user ->
      def latestStatus = getLatestStatusForUser(user.id)
      if (shouldPromptUser(user, latestStatus, lastStatusThreshold, lastPromptThreshold)) {
        log.info("Prompting user ${user.id} to give a status update")
        statusPrompter.promptUserForStatus(user.id)
        user.lastPrompted = Instant.now()
        userManager.updateUser(user)
      }
    }
  }

  private Status getLatestStatusForUser(String userId) {
    statusRepository.findFirstByUserIdOrderByDateDesc(userId).orElse(null)
  }

  private static Boolean shouldPromptUser(User user, Status latestStatus, Instant lastStatusThreshold, Instant lastPromptThreshold) {
    if(!latestStatus || latestStatus.date.isBefore(lastStatusThreshold)) {
      return !user.lastPrompted || user.lastPrompted.isBefore(lastPromptThreshold)
    }

    return false
  }
}

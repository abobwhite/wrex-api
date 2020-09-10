package com.daugherty.wrex.status

import com.daugherty.wrex.ApplicationConfig
import com.daugherty.wrex.exception.ERROR_CODE
import com.daugherty.wrex.exception.WrexException
import com.daugherty.wrex.user.UserManager
import groovy.util.logging.Slf4j
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

import java.time.Instant

@Slf4j
@Service
class StatusManager {
  private final StatusRepository statusRepository
  private final UserManager userManager
  private final UserStatusPostProcessor userStatusPostProcessor
  private final StatusPrompter statusPrompter
  private final ApplicationConfig applicationConfig

  StatusManager(final StatusRepository statusRepository, final UserManager userManager, final UserStatusPostProcessor userStatusPostProcessor,
                final StatusPrompter statusPrompter, final ApplicationConfig applicationConfig) {
    this.statusRepository = statusRepository
    this.userManager = userManager
    this.userStatusPostProcessor = userStatusPostProcessor
    this.statusPrompter = statusPrompter
    this.applicationConfig = applicationConfig
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
  @Scheduled(fixedDelayString = '${wrex.statusPromptCheckSeconds}000')
  protected void promptUsersForStatus() {
    promptUsersForStatus(true)
  }

  void promptUsersForStatus(Boolean checkLatestStatus) {
    log.debug('Checking users for status reminders')
    def oneWeekAgo = Instant.now().minusMillis(applicationConfig.statusPromptSeconds * 1000)
    userManager.getUsers().each { user ->
      def latestStatus = getLatestStatusForUser(user.id)
      def prompt = checkLatestStatus ? !latestStatus || latestStatus.date.isBefore(oneWeekAgo) : true
      if (prompt) {
        log.info("Prompting user ${user.id} to give a status update")
        statusPrompter.promptUserForStatus(user.id)
      }
    }
  }

  Status getLatestStatusForUser(String userId) {
    statusRepository.findFirstByUserIdOrderByDateDesc(userId).orElse(null)
  }
}

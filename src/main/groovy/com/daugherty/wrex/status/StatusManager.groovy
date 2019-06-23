package com.daugherty.wrex.status

import com.daugherty.wrex.exception.ERROR_CODE
import com.daugherty.wrex.exception.WrexException
import com.daugherty.wrex.user.UserManager
import groovy.util.logging.Slf4j
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Slf4j
@Service
class StatusManager {
  private final StatusRepository statusRepository
  private final UserManager userManager
  private final UserStatusPostProcessor userStatusPostProcessor
  private final StatusPrompter statusPrompter

  StatusManager(final StatusRepository statusRepository, final UserManager userManager, final UserStatusPostProcessor userStatusPostProcessor, final StatusPrompter statusPrompter) {
    this.statusRepository = statusRepository
    this.userManager = userManager
    this.userStatusPostProcessor = userStatusPostProcessor
    this.statusPrompter = statusPrompter
  }

  Status addStatus(String userId, Status status) {
    if (!status.message) {
      throw new WrexException(ERROR_CODE.INVALID)
    }

    def user = userManager.getUserById(userId)
    status.userId = user.id

    def createdStatus = statusRepository.insert(status)

    userStatusPostProcessor.process(createdStatus, user)

    createdStatus
  }

  List<Status> getStatusesForUser(String userId) {
    def user = userManager.getUserById(userId) // throws if no user
    statusRepository.findByUserId(user.id)
  }

  // NOTE: fixed DELAY ensures ordered updates - we want to wait until a synchronous request is finished before checking again
  @Scheduled(fixedDelayString = '${wrex.statusPromptDelay}')
  protected void promptUsersForStatus() {
    log.info('Prompting users for statuses')
    userManager.getUsers().each { user ->
      statusPrompter.promptUserForStatus(user.id)
    }
  }
}

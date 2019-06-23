package com.daugherty.wrex.status

import com.daugherty.wrex.exception.ERROR_CODE
import com.daugherty.wrex.exception.WrexException
import com.daugherty.wrex.tag.StatusTagProcessor
import com.daugherty.wrex.user.UserManager
import com.daugherty.wrex.user.correlation.UserCorrelationProcessor
import org.springframework.stereotype.Service

@Service
class StatusManager {
  private final StatusRepository statusRepository
  private final UserManager userManager
  private final UserStatusPostProcessor userStatusPostProcessor

  StatusManager(final StatusRepository statusRepository, final UserManager userManager, final UserStatusPostProcessor userStatusPostProcessor) {
    this.statusRepository = statusRepository
    this.userManager = userManager
    this.userStatusPostProcessor = userStatusPostProcessor
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
}

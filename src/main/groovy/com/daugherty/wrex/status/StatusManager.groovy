package com.daugherty.wrex.status

import com.daugherty.wrex.exception.ERROR_CODE
import com.daugherty.wrex.exception.WrexException
import com.daugherty.wrex.tag.StatusTagProcessor
import com.daugherty.wrex.user.UserManager
import org.springframework.stereotype.Service

@Service
class StatusManager {
  private final StatusRepository statusRepository
  private final UserManager userManager
  private final StatusTagProcessor statusTagProcessor

  StatusManager(final StatusRepository statusRepository, final UserManager userManager, final StatusTagProcessor statusTagProcessor) {
    this.statusRepository = statusRepository
    this.userManager = userManager
    this.statusTagProcessor = statusTagProcessor
  }

  Status addStatus(String userId, Status status) {
    if (!status.message) {
      throw new WrexException(ERROR_CODE.INVALID)
    }

    def user = userManager.getUserById(userId)
    status.userId = user.id

    def createdStatus = statusRepository.insert(status)
    statusTagProcessor.createTagsForStatus(createdStatus)

    createdStatus
  }
}

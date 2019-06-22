package com.daugherty.wrex.lineOfService


import org.springframework.stereotype.Service

@Service
class LineOfServiceManager {
  private final LineOfServiceRepository lineOfServiceRepository

  LineOfServiceManager(final LineOfServiceRepository lineOfServiceRepository) {
    this.lineOfServiceRepository = lineOfServiceRepository
  }

  List<LineOfService> getLinesOfService() {
    lineOfServiceRepository.findAll()
  }
}

package com.daugherty.wrex.lineOfService

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class LineOfServiceController {
  private final LineOfServiceManager lineOfServiceManager

  LineOfServiceController(final LineOfServiceManager lineOfServiceManager) {
    this.lineOfServiceManager = lineOfServiceManager
  }

  @GetMapping(value = '/linesofservice')
  ResponseEntity<List<LineOfService>> getLinesOfService() {
    ResponseEntity.ok(lineOfServiceManager.getLinesOfService())
  }
}

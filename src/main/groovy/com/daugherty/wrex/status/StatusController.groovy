package com.daugherty.wrex.status

import com.daugherty.wrex.exception.ERROR_CODE
import com.daugherty.wrex.exception.WrexException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class StatusController {
  private final StatusManager statusManager

  StatusController(final StatusManager statusManager) {
    this.statusManager = statusManager
  }

  @PostMapping(value = '/users/{userId}/statuses')
  ResponseEntity<Status> addStatus(@PathVariable String userId, @RequestBody Status status) {
    try {
      ResponseEntity.ok(statusManager.addStatus(userId, status))
    } catch (WrexException e) {
      switch (e.errorCode) {
        case ERROR_CODE.NOT_FOUND:
          return ResponseEntity.notFound().build()
        case ERROR_CODE.INVALID:
          return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build()
        default:
          return ResponseEntity.badRequest().build()
      }
    }
  }

  @GetMapping(value = '/users/{userId}/statuses')
  ResponseEntity<List<Status>> getStatusesForUser(@PathVariable String userId) {
    try {
      ResponseEntity.ok(statusManager.getStatusesForUser(userId))
    } catch (WrexException e) {
      e.errorCode == ERROR_CODE.NOT_FOUND ? ResponseEntity.notFound().build() : ResponseEntity.badRequest().build()
    }
  }

  // TODO: THIS IS JUST A TEMP ENDPOINT TO USE IN DEMOS TO SHOW WREXY PROMPT
  @GetMapping(value = '/statuses/prompt')
  ResponseEntity promptForStatus() {
    try {
      statusManager.promptUsersForStatus(false)
      ResponseEntity.noContent().build()
    } catch (WrexException e) {
      new ResponseEntity(HttpStatus.I_AM_A_TEAPOT)
    }
  }
}

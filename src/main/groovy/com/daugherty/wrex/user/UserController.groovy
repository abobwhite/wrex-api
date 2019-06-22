package com.daugherty.wrex.user

import com.daugherty.wrex.exception.ERROR_CODE
import com.daugherty.wrex.exception.WrexException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController {
  private final UserManager userManager

  UserController(final UserManager userManager) {
    this.userManager = userManager
  }

  @GetMapping(value = '/users/{userId}')
  ResponseEntity<User> getUserById(@PathVariable userId) {
    try {
      ResponseEntity.ok(userManager.getUserById(userId))
    } catch (WrexException e) {
      e.errorCode == ERROR_CODE.NOT_FOUND ? ResponseEntity.notFound() : ResponseEntity.badRequest()
    }
  }
}

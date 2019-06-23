package com.daugherty.wrex.user

import com.daugherty.wrex.exception.ERROR_CODE
import com.daugherty.wrex.exception.WrexException
import com.daugherty.wrex.user.slack.PostUsersCodeRequest
import groovy.util.logging.Slf4j
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Slf4j
@RestController
class UserController {
  private final UserManager userManager
  private final UserSlackService userSlackService

  UserController(final UserManager userManager, final UserSlackService userSlackService) {
    this.userManager = userManager
    this.userSlackService = userSlackService
  }

  @GetMapping(value = '/users/{userId}')
  ResponseEntity<User> getUserById(@PathVariable userId) {
    try {
      ResponseEntity.ok(userManager.getUserById(userId))
    } catch (WrexException e) {
      e.errorCode == ERROR_CODE.NOT_FOUND ? ResponseEntity.notFound().build() : ResponseEntity.badRequest().build()
    }
  }

  @GetMapping(value = '/users')
  ResponseEntity<List<User>> getUsers() {
    try {
      ResponseEntity.ok(userManager.getUsers())
    } catch (WrexException e) {
      ResponseEntity.badRequest().build()
    }
  }

  @PatchMapping(value = '/users/{userId}')
  ResponseEntity<User> modifyUser(@PathVariable String userId, @RequestBody User user) {
    try {
      ResponseEntity.ok(userManager.modifyUser(userId, user))
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

  @PostMapping(value = '/users/code')
  ResponseEntity<User> postUsersCode(@RequestBody PostUsersCodeRequest postUsersCodeRequest) {
    try {
      log.info('Posting verification code: ' + postUsersCodeRequest.code)
      String accessToken = userSlackService.getAccessToken(postUsersCodeRequest.code)
      log.info('Creating User with access code: ' + accessToken)
      // TODO: NEED slackId as userId new user
      User user = userManager.createUser(new User(accessToken: accessToken))
      ResponseEntity.ok(user)
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
}

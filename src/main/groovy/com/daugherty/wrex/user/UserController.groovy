package com.daugherty.wrex.user

import com.daugherty.wrex.exception.ERROR_CODE
import com.daugherty.wrex.exception.WrexException
import com.daugherty.wrex.tag.UserSlackService
import groovy.util.logging.Slf4j
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
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

  @PostMapping(value = '/users/code')
  ResponseEntity<User> postUsersCode(@RequestBody String code) {
    try {
      log.info('Posting verification code: ' + code)
      String accessToken = userSlackService.getAccessToken(code)
      log.info('Creating User with access code: ' + accessToken)
      User user = userManager.saveUser(new User(accessToken: accessToken))
      ResponseEntity.ok(user)
    } catch (WrexException e) {
      ResponseEntity.badRequest().build()
    }
  }
}

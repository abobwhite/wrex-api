package com.daugherty.wrex.user

import com.daugherty.wrex.exception.ERROR_CODE
import com.daugherty.wrex.exception.WrexException
import com.daugherty.wrex.recommendation.RecommendationGenerator
import com.daugherty.wrex.recommendation.RecommendationManager
import com.daugherty.wrex.user.slack.OauthResponse
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
  private final RecommendationGenerator recommendationGenerator

  UserController(final UserManager userManager, final UserSlackService userSlackService, final RecommendationGenerator recommendationGenerator) {
    this.userManager = userManager
    this.userSlackService = userSlackService
    this.recommendationGenerator = recommendationGenerator
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
      def response = ResponseEntity.ok(userManager.modifyUser(userId, user))
      recommendationGenerator.generateRecommendations()
      response
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
      log.info("Posting user verification code: ${postUsersCodeRequest.code}")
      OauthResponse oauthResponse = userSlackService.getAccessToken(postUsersCodeRequest.code)
      def user
      try {
        user = userManager.getUserById(oauthResponse.user.id)
        user.accessToken = oauthResponse.access_token
        log.info("Logging in User with id: ${oauthResponse.user.id}")
        user = userManager.updateUser(user)
      } catch (WrexException e) {
        log.info("Creating User with id: ${oauthResponse.user.id}")
        List<String> names = oauthResponse.user.name.split(' ')
        user = userManager.createUser(new User(accessToken: oauthResponse.access_token, id: oauthResponse.user.id, firstName: names[0], lastName: names[1]))
      }
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

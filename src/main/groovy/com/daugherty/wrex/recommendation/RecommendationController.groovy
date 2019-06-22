package com.daugherty.wrex.recommendation

import groovy.transform.CompileStatic
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@CompileStatic
@RestController
class RecommendationController {
  private final RecommendationManager recommendationManager

  RecommendationController(final RecommendationManager recommendationManager) {
    this.recommendationManager = recommendationManager
  }

  @GetMapping(value = '/users/{userId}/recommendations')
  ResponseEntity<List<Recommendation>> getRecommendationsForUser(@PathVariable String userId) {
    ResponseEntity.ok(recommendationManager.getRecommendationsForUser(userId))
  }
}

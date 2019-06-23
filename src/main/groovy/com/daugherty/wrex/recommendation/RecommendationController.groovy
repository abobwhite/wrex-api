package com.daugherty.wrex.recommendation

import com.daugherty.wrex.exception.ERROR_CODE
import com.daugherty.wrex.exception.WrexException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class RecommendationController {
  private final RecommendationManager recommendationManager
  private final RecommendationGenerator recommendationGenerator

  RecommendationController(final RecommendationManager recommendationManager, final RecommendationGenerator recommendationGenerator) {
    this.recommendationManager = recommendationManager
    this.recommendationGenerator = recommendationGenerator
  }

  @GetMapping(value = '/users/{userId}/recommendations')
  ResponseEntity<List<Recommendation>> getRecommendationsForUser(@PathVariable String userId) {
    ResponseEntity.ok(recommendationManager.getRecommendationsForUser(userId))
  }

  @PostMapping(value = '/recommendations/generate')
  ResponseEntity generateRecommendations() {
    ResponseEntity.ok(recommendationGenerator.generateRecommendations())
  }

  @PatchMapping(value = '/recommendations/{recommendationId}')
  ResponseEntity<Recommendation> modifyRecommendation(@PathVariable String recommendationId,
                                                      @RequestBody Recommendation updatedRecommendation) {
    try {
      ResponseEntity.ok(recommendationManager.modifyRecommendation(recommendationId, updatedRecommendation))
    } catch (WrexException e) {
      e.errorCode == ERROR_CODE.NOT_FOUND ? ResponseEntity.notFound().build() : ResponseEntity.badRequest().build()
    }
  }
}

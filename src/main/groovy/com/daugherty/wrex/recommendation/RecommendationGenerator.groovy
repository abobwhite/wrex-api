package com.daugherty.wrex.recommendation

import com.daugherty.wrex.user.User
import com.daugherty.wrex.user.correlation.UserCorrelation
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class RecommendationGenerator {
  @Async
  void generateRecommendations(User user, List<UserCorrelation> latestUserCorrelations) {

  }
}

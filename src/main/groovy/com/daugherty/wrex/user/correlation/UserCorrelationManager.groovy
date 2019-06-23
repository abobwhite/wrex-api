package com.daugherty.wrex.user.correlation

import org.springframework.stereotype.Service

@Service
class UserCorrelationManager {
  private final UserCorrelationRepository userCorrelationRepository

  UserCorrelationManager(final UserCorrelationRepository userCorrelationRepository) {
    this.userCorrelationRepository = userCorrelationRepository
  }

  List<UserCorrelation> getUserCorrelations() {
    userCorrelationRepository.findAll()
  }

  UserCorrelation findUserCorrelationForUsers(String user1Id, String user2Id) {
    def correlations = userCorrelationRepository.findByUser1IdAndUser2Id(user1Id, user2Id)
    if (!correlations) {
      correlations = userCorrelationRepository.findByUser1IdAndUser2Id(user2Id, user1Id)
      if (!correlations) {
        return null
      }
    }

    correlations[0]
  }

  UserCorrelation createUserCorrelation(UserCorrelation userCorrelation) {
    userCorrelationRepository.insert(userCorrelation)
  }

  UserCorrelation updateUserCorrelation(UserCorrelation userCorrelation) {
    userCorrelationRepository.save(userCorrelation)
  }
}

package com.daugherty.wrex.recommendation

class RecommendationMessages {
  static String getRandomCoffee(String otherUserFullName) {
    "Random thought! Based on your interests, and love for coffee, you might be interested in grabbing a not-so-random Random Coffee with ${otherUserFullName}"
  }

  static String getHappyHour(String otherUserFullName) {
    "I see you like happy hours - so does ${otherUserFullName}. Maybe you should get a group together!"
  }
}

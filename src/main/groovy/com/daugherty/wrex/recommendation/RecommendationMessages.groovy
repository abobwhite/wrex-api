package com.daugherty.wrex.recommendation

class RecommendationMessages {
  static String getRandomCoffee(String otherUserFullName) {
    "Random thought! Based on your interests, and love for coffee, you might be interested in grabbing a not-so-random <b>Random Coffee</b> with <b>${otherUserFullName}</b>"
  }

  static String getHappyHour(String otherUserFullName) {
    "I see you like happy hours - so does <b>${otherUserFullName}</b>. Maybe you should get a group together!"
  }

  static String getWeTalkTheSameTalk(String otherUserFullName, String tagName) {
    "You and <b>${otherUserFullName}</b> both seem really interested in <b>${tagName}</b>...maybe you should meet up?"
  }

  static String getWeLikeTheSameEvent(String otherUserFullName, String eventName) {
    "<b>${otherUserFullName} is interested in ${eventName}. You seem to be interested too. Maybe you could travel together."
  }

  static String getMentorship(String otherUserFullName, String tagName) {
    "${otherUserFullName} is looking for help with ${tagName}. You might be able to help"
  }
}

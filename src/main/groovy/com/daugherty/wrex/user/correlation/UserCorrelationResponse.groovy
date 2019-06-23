package com.daugherty.wrex.user.correlation

import groovy.transform.ToString

@ToString
class UserCorrelationResponse {
  Map<String, Double> correlation
  List<Map<String, UserCorrelationTags>> tags
}


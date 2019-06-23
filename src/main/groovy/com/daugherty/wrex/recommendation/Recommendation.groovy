package com.daugherty.wrex.recommendation

import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

import java.time.Instant

@ToString
@CompileStatic
@EqualsAndHashCode
@Document(collection = 'recommendations')
class Recommendation {
  @Id
  String id
  @CreatedDate
  Instant date = Instant.now()
  String userId
  RECOMMENDATION_TYPE type
  String message
  Boolean dismissed = false
  RECOMMENDATION_FEEDBACK feedback
  List<RecommendationGenesis> geneses
}

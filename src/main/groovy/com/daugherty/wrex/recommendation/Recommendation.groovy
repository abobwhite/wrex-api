package com.daugherty.wrex.recommendation

import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
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
  Instant date
  String userId
  RECOMMENDATION_TYPE type
  String message
  Boolean dismissed
  RECOMMENDATION_FEEDBACK feedback
}

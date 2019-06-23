package com.daugherty.wrex.user.correlation

import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@ToString
@CompileStatic
@EqualsAndHashCode
@Document(collection = 'userCorrelations')
class UserCorrelation {
  @Id
  String id
  String user1Id
  String user2Id
  Double score
  Map<String, Double> tagScores
}

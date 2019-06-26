package com.daugherty.wrex.status

import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

import java.time.Instant

@ToString
@CompileStatic
@EqualsAndHashCode
@Document(collection = 'statuses')
class Status {
  @Id
  String id
  String userId
  String message
  Instant date
}

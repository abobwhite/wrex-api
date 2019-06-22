package com.daugherty.wrex.user

import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

import java.time.Instant

@ToString
@CompileStatic
@EqualsAndHashCode
@Document(collection = 'users')
class User {
  @Id
  String id
  String firstName
  String lastName
  String email
  Instant hireDate
  String lineOfServiceId
  String branchId
  Instant registrationDate
}

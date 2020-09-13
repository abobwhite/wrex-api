package com.daugherty.wrex.user

import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.springframework.data.mongodb.core.mapping.Document

import java.time.Instant

@ToString
@CompileStatic
@EqualsAndHashCode
@Document(collection = 'users')
class User {
  String id
  String firstName
  String lastName
  String email
  Instant hireDate
  String lineOfServiceId
  String branchId
  Instant registrationDate
  List<UserTag> userTags
  List<String> requestTagIds
  UserPreferences preferences
  String accessToken
  Instant lastPrompted

  String getFullName() {
    "${firstName} ${lastName}"
  }
}

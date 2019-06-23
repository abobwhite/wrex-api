package com.daugherty.wrex.user

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@ToString
@EqualsAndHashCode
class UserPreferences {
  Boolean randomCoffee = false
  Boolean happyHour = false
  Boolean mentoring = false
  Boolean mentored = false
  Boolean events = false
}

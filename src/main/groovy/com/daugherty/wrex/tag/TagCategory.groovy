package com.daugherty.wrex.tag

import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@ToString
@CompileStatic
@EqualsAndHashCode
@Document(collection = 'tagCategories')
class TagCategory {
  @Id
  String id
  String name
}

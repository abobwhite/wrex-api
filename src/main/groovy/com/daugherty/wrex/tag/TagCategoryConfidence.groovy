package com.daugherty.wrex.tag

import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@ToString
@CompileStatic
@EqualsAndHashCode
class TagCategoryConfidence {
  String categoryId
  Float confidence
}

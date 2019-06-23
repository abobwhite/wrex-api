package com.daugherty.wrex.recommendation

import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@ToString
@CompileStatic
@EqualsAndHashCode
class RecommendationGenesis {
  RECOMMENDATION_GENESIS_TYPE type
  String item //tagId or USER_PREFERENCE
}

package com.daugherty.wrex.recommendation

import groovy.transform.CompileStatic
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.stereotype.Repository

@CompileStatic
@Repository
@RepositoryRestResource(exported = false)
interface RecommendationRepository extends MongoRepository<Recommendation, String> {
  List<Recommendation> findByUserId(String userId)
}

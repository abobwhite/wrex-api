package com.daugherty.wrex.user.correlation

import groovy.transform.CompileStatic
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.stereotype.Repository

@CompileStatic
@Repository
@RepositoryRestResource(exported = false)
interface UserCorrelationRepository extends MongoRepository<UserCorrelation, String> {
  List<UserCorrelation> findByUser1IdAndUser2Id(String user1Id, String user2Id)
}
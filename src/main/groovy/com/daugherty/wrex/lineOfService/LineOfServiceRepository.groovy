package com.daugherty.wrex.lineOfService

import com.daugherty.wrex.branch.Branch
import groovy.transform.CompileStatic
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.stereotype.Repository

@CompileStatic
@Repository
@RepositoryRestResource(exported = false)
interface LineOfServiceRepository extends MongoRepository<LineOfService, String> {

}
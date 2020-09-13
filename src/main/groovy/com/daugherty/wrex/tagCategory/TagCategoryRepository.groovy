package com.daugherty.wrex.tagCategory

import groovy.transform.CompileStatic
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.stereotype.Repository

@CompileStatic
@Repository
@RepositoryRestResource(exported = false)
interface TagCategoryRepository extends MongoRepository<TagCategory, String> {
  List<TagCategory> findByNameContainingIgnoreCase(String nameFilter)
}

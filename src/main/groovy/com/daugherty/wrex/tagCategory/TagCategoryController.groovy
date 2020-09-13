package com.daugherty.wrex.tagCategory

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class TagCategoryController {
  private final TagCategoryManager tagCategoryManager

  TagCategoryController(final TagCategoryManager tagCategoryManager) {
    this.tagCategoryManager = tagCategoryManager
  }

  @GetMapping(value = '/tagcategories')
  ResponseEntity<List<TagCategory>> getTagCategories(@RequestParam(value = 'nameFilter', required = false) String nameFilter) {
    ResponseEntity.ok(tagCategoryManager.getTagCategories(nameFilter))
  }
}

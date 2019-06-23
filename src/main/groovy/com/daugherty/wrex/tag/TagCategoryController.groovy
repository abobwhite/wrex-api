package com.daugherty.wrex.tag

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class TagCategoryController {
  private final TagCategoryManager tagCategoryManager

  TagCategoryController(final TagCategoryManager tagCategoryManager) {
    this.tagCategoryManager = tagCategoryManager
  }

  @GetMapping(value = '/tagcategories')
  ResponseEntity<List<TagCategory>> getTagCategories() {
    ResponseEntity.ok(tagCategoryManager.tagCategories)
  }
}

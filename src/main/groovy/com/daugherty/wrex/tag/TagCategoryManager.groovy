package com.daugherty.wrex.tag

import org.springframework.stereotype.Service

@Service
class TagCategoryManager {
  private final TagCategoryRepository tagCategoryRepository

  TagCategoryManager(final TagCategoryRepository tagCategoryRepository) {
    this.tagCategoryRepository = tagCategoryRepository
  }

  List<TagCategory> getTagCategories() {
    tagCategoryRepository.findAll()
  }
}

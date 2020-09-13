package com.daugherty.wrex.tagCategory

import org.springframework.stereotype.Service

@Service
class TagCategoryManager {
  private final TagCategoryRepository tagCategoryRepository

  TagCategoryManager(final TagCategoryRepository tagCategoryRepository) {
    this.tagCategoryRepository = tagCategoryRepository
  }

  List<TagCategory> getTagCategories(String nameFilter = null) {
    nameFilter ? tagCategoryRepository.findByNameContainingIgnoreCase(nameFilter) : tagCategoryRepository.findAll()
  }
}

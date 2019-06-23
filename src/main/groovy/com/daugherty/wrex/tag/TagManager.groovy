package com.daugherty.wrex.tag

import org.springframework.stereotype.Service

@Service
class TagManager {
  private final TagRepository tagRepository

  TagManager(final TagRepository tagRepository) {
    this.tagRepository = tagRepository
  }

  List<Tag> getTags() {
    tagRepository.findAll()
  }
}

package com.daugherty.wrex.tag

import com.daugherty.wrex.exception.ERROR_CODE
import com.daugherty.wrex.exception.WrexException
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

  Tag createTag(Tag tag) {
    if (!tag.name) {
      throw new WrexException(ERROR_CODE.INVALID)
    }

    tagRepository.insert(tag)
  }

  Tag findById(String id) {
    def tag = tagRepository.findById(id).orElse(null)

    if (!tag) {
      throw new WrexException(ERROR_CODE.NOT_FOUND)
    }

    tag
  }
}

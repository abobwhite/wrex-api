package com.daugherty.wrex.tag

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class TagController {
  private final TagManager tagManager

  TagController(final TagManager tagManager) {
    this.tagManager = tagManager
  }

  @GetMapping(value = '/tags')
  ResponseEntity<List<Tag>> getTags() {
    ResponseEntity.ok(tagManager.getTags())
  }
}

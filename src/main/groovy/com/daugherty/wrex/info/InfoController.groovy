package com.daugherty.devopes.info

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class InfoController {

  @GetMapping
  ResponseEntity<Info> getInfo() {
    ResponseEntity.ok(new Info(version: '0.0.0.0.0.0.0.1'))
  }
}

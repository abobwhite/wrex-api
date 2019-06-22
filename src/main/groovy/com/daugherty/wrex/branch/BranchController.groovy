package com.daugherty.wrex.branch

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class BranchController {
  private final BranchManager branchManager

  BranchController(final BranchManager branchManager) {
    this.branchManager = branchManager
  }

  @GetMapping(value = '/branches')
  ResponseEntity<List<Branch>> getBranches() {
    ResponseEntity.ok(branchManager.getBranches())
  }
}
